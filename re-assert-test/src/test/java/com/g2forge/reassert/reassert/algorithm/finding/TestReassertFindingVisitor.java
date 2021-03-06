package com.g2forge.reassert.reassert.algorithm.finding;

import java.util.Set;
import java.util.stream.Collectors;

import org.jgrapht.Graph;
import org.junit.Test;
import org.slf4j.event.Level;

import com.g2forge.alexandria.java.type.ref.ITypeRef;
import com.g2forge.reassert.contract.ContractComparisonAnalyzer;
import com.g2forge.reassert.contract.algorithm.licenseusage.ReassertLicenseUsageVisitor;
import com.g2forge.reassert.contract.algorithm.worklicense.ReassertWorkLicenseVisitor;
import com.g2forge.reassert.core.model.HReassertModel;
import com.g2forge.reassert.core.model.IEdge;
import com.g2forge.reassert.core.model.IVertex;
import com.g2forge.reassert.core.model.artifact.Artifact;
import com.g2forge.reassert.core.model.report.GraphContextFinding;
import com.g2forge.reassert.list.ListCoordinates;
import com.g2forge.reassert.reassert.ATestReassert;
import com.g2forge.reassert.reassert.TestGraph;
import com.g2forge.reassert.standard.algorithm.StandardLicenseUsageRules;
import com.g2forge.reassert.standard.algorithm.StandardWorkLicenseRules;

public class TestReassertFindingVisitor extends ATestReassert {
	@Test
	public void gplcompatible() {
		test("gplcompatible", this::interesting);
	}

	@Test
	public void gplincompatible() {
		test("gplincompatible", this::interesting);
	}

	@Test
	public void gplsaas() {
		test("gplsaas", this::interesting);
	}

	protected Graph<IVertex, IEdge> interesting(Graph<IVertex, IEdge> graph) {
		final Graph<IVertex, IEdge> retVal = HReassertModel.clone(graph);
		final Set<GraphContextFinding> uninterested = retVal.vertexSet().stream().flatMap(ITypeRef.of(GraphContextFinding.class)::castIfInstance).filter(f -> f.getLevel().compareTo(Level.WARN) > 0).collect(Collectors.toSet());
		for (IVertex vertex : uninterested) {
			retVal.removeVertex(vertex);
		}
		return retVal;
	}

	@Override
	protected TestGraph load(final Artifact<ListCoordinates> artifact) {
		return new TestGraph(artifact, new ReassertWorkLicenseVisitor(StandardWorkLicenseRules.create()), new ReassertLicenseUsageVisitor(new ContractComparisonAnalyzer(StandardLicenseUsageRules.create())));
	}
}
