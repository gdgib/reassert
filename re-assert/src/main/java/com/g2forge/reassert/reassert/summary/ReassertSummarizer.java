package com.g2forge.reassert.reassert.summary;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.jgrapht.Graph;
import org.jgrapht.alg.shortestpath.AllDirectedPaths;
import org.slf4j.event.Level;

import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.g2forge.alexandria.java.adt.ComparableComparator;
import com.g2forge.alexandria.java.core.helpers.HCollection;
import com.g2forge.alexandria.java.io.RuntimeIOException;
import com.g2forge.alexandria.java.io.dataaccess.IDataSink;
import com.g2forge.alexandria.java.type.ref.ATypeRef;
import com.g2forge.alexandria.java.type.ref.ITypeRef;
import com.g2forge.reassert.core.algorithm.ReassertVertexDescriber;
import com.g2forge.reassert.core.api.module.IContext;
import com.g2forge.reassert.core.model.HReassertModel;
import com.g2forge.reassert.core.model.IEdge;
import com.g2forge.reassert.core.model.artifact.Artifact;
import com.g2forge.reassert.core.model.contract.Notice;
import com.g2forge.reassert.core.model.contract.license.ILicense;
import com.g2forge.reassert.core.model.contract.license.UnspecifiedLicense;
import com.g2forge.reassert.core.model.contract.usage.IUsage;
import com.g2forge.reassert.core.model.contract.usage.UnspecifiedUsage;
import com.g2forge.reassert.core.model.report.GraphContextualFinding;
import com.g2forge.reassert.core.model.report.IFinding;
import com.g2forge.reassert.core.model.report.IReport;
import com.g2forge.reassert.reassert.summary.convert.SummaryModule;
import com.g2forge.reassert.reassert.summary.model.ArtifactSummary;
import com.g2forge.reassert.reassert.summary.model.ReportSummary;
import com.g2forge.reassert.reassert.summary.model.RiskSummary;
import com.g2forge.reassert.term.analyze.model.findings.IRiskFinding;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ReassertSummarizer {
	protected final IContext context;

	protected Set<Artifact<?>> computeOrigins(IReport report, final Graph<Artifact<?>, IEdge> subgraph) {
		if (subgraph.vertexSet().containsAll(report.getOrigins().getOrigins())) return new HashSet<>(report.getOrigins().getOrigins());
		return subgraph.vertexSet().stream().filter(a -> subgraph.inDegreeOf(a) < 1).collect(Collectors.toSet());
	}

	protected SummaryModule createSummaryModule() {
		return new SummaryModule(getContext());
	}

	protected <T> void render(Class<T> type, Collection<T> value, IDataSink sink) {
		final CsvMapper mapper = new CsvMapper();
		mapper.disable(MapperFeature.SORT_PROPERTIES_ALPHABETICALLY);
		mapper.registerModule(createSummaryModule());

		final ObjectWriter writer = mapper.writerFor(type).with(mapper.schemaFor(type).withHeader().withColumnReordering(true).withArrayElementSeparator("\n"));
		try (final OutputStream stream = sink.getStream(ITypeRef.of(OutputStream.class))) {
			writer.writeValues(stream).writeAll(value);
		} catch (IOException e) {
			throw new RuntimeIOException(e);
		}
	}

	public void renderArtifacts(ReportSummary reportSummary, IDataSink sink) {
		render(ArtifactSummary.class, reportSummary.getArtifacts(), sink);
	}

	public void renderRisks(ReportSummary reportSummary, IDataSink sink) {
		render(RiskSummary.class, reportSummary.getRisks(), sink);
	}

	public ReportSummary summarize(IReport report) {
		final ReportSummary.ReportSummaryBuilder retVal = ReportSummary.builder();

		final List<Artifact<?>> artifacts = report.getGraph().vertexSet().stream().flatMap(new ATypeRef<Artifact<?>>() {}::castIfInstance).sorted(new Comparator<Artifact<?>>() {
			@Getter
			protected final ReassertVertexDescriber describer = new ReassertVertexDescriber(getContext());

			@Override
			public int compare(Artifact<?> o1, Artifact<?> o2) {
				final ReassertVertexDescriber describer = getDescriber();
				final String n1 = describer.apply(o1).getName();
				final String n2 = describer.apply(o2).getName();
				return n1.compareTo(n2);
			}
		}).collect(Collectors.toList());

		final Graph<Artifact<?>, IEdge> subgraph = HReassertModel.asArtifactGraph(report.getGraph());
		final AllDirectedPaths<Artifact<?>, IEdge> paths = new AllDirectedPaths<>(subgraph);
		final Set<Artifact<?>> origins = computeOrigins(report, subgraph);

		for (Artifact<?> artifact : artifacts) {
			final ArtifactSummary.ArtifactSummaryBuilder artifactSummary = ArtifactSummary.builder();
			artifactSummary.artifact(artifact.getCoordinates());

			// Find the license and usage
			final Collection<ILicense> licenses = HReassertModel.get(report.getGraph(), artifact, true, Notice.class::isInstance, ITypeRef.of(ILicense.class));
			final Collection<IUsage> usages = HReassertModel.get(report.getGraph(), artifact, true, Notice.class::isInstance, ITypeRef.of(IUsage.class));
			artifactSummary.licenses(licenses);
			artifactSummary.usages(usages);

			// Find all the findings, and use that to compute the level
			final Collection<GraphContextualFinding> findings = HReassertModel.get(report.getGraph(), artifact, true, Notice.class::isInstance, ITypeRef.of(GraphContextualFinding.class));
			if (findings.isEmpty()) artifactSummary.level(Level.INFO);
			else {
				artifactSummary.level(findings.stream().map(IFinding::getLevel).min(ComparableComparator.create()).get());
				for (GraphContextualFinding finding : findings) {
					artifactSummary.finding(finding.getFinding());

					final IFinding innermost = finding.getInnermostFinding();
					if (innermost instanceof IRiskFinding) {
						final RiskSummary.RiskSummaryBuilder riskSummary = RiskSummary.builder();
						riskSummary.artifact(artifact.getCoordinates());
						riskSummary.level(finding.getLevel()).risk(finding.getFinding());
						riskSummary.usage(usages.size() == 1 ? HCollection.getOne(usages) : UnspecifiedUsage.create());
						riskSummary.license(licenses.size() == 1 ? HCollection.getOne(licenses) : UnspecifiedLicense.create());
						retVal.risk(riskSummary.build());
					}
				}
			}

			// Compute paths to this artifact
			if (!origins.contains(artifact)) artifactSummary.paths(paths.getAllPaths(origins, HCollection.asSet(artifact), true, Integer.MAX_VALUE));

			retVal.artifact(artifactSummary.build());
		}

		return retVal.build();
	}
}
