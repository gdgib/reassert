package com.g2forge.reassert.reassert.algorithm.finding;

import java.util.Set;
import java.util.stream.Collectors;

import org.jgrapht.Graph;
import org.junit.Test;
import org.slf4j.event.Level;

import com.g2forge.alexandria.java.type.ref.ITypeRef;
import com.g2forge.reassert.core.model.HReassertModel;
import com.g2forge.reassert.core.model.IEdge;
import com.g2forge.reassert.core.model.IVertex;
import com.g2forge.reassert.core.model.artifact.Artifact;
import com.g2forge.reassert.core.model.report.GraphContextualFinding;
import com.g2forge.reassert.license.StandardWorkTypeFactory;
import com.g2forge.reassert.list.ListCoordinates;
import com.g2forge.reassert.reassert.algorithm.ATestVisitor;
import com.g2forge.reassert.reassert.algorithm.ReassertFindingVisitor;
import com.g2forge.reassert.reassert.algorithm.ReassertWorkVisitor;
import com.g2forge.reassert.reassert.algorithm.example.ExampleGraph;
import com.g2forge.reassert.term.StandardLicenseUsageRules;
import com.g2forge.reassert.term.analyze.LicenseUsageAnalyzer;

public class TestReassertFindingVisitor extends ATestVisitor {
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
		final Set<GraphContextualFinding> uninterested = retVal.vertexSet().stream().flatMap(ITypeRef.of(GraphContextualFinding.class)::castIfInstance).filter(f -> f.getLevel().compareTo(Level.WARN) > 0).collect(Collectors.toSet());
		for (IVertex vertex : uninterested) {
			retVal.removeVertex(vertex);
		}
		return retVal;
	}

	@Override
	protected ExampleGraph load(final Artifact<ListCoordinates> artifact) {
		return new ExampleGraph(artifact, new ReassertWorkVisitor(StandardWorkTypeFactory.create()), new ReassertFindingVisitor(new LicenseUsageAnalyzer(StandardLicenseUsageRules.create())));
	}
}