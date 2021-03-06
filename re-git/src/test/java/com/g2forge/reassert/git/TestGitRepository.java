package com.g2forge.reassert.git;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.jgrapht.Graph;
import org.junit.Test;

import com.g2forge.alexandria.java.core.helpers.HCollection;
import com.g2forge.alexandria.java.core.resource.Resource;
import com.g2forge.alexandria.java.type.ref.ITypeRef;
import com.g2forge.alexandria.test.HAssert;
import com.g2forge.enigma.diagram.dot.convert.DotRenderer;
import com.g2forge.reassert.core.algorithm.ReassertGraphVisualizer;
import com.g2forge.reassert.core.api.module.Context;
import com.g2forge.reassert.core.model.HReassertModel;
import com.g2forge.reassert.core.model.IEdge;
import com.g2forge.reassert.core.model.IVertex;
import com.g2forge.reassert.core.model.artifact.Artifact;
import com.g2forge.reassert.core.model.contract.Notice;
import com.g2forge.reassert.core.model.contract.license.ILicenseApplied;
import com.g2forge.reassert.core.model.contract.license.LicenseVersion;
import com.g2forge.reassert.core.model.contract.license.UnknownLicense;
import com.g2forge.reassert.core.test.ATestRepository;
import com.g2forge.reassert.standard.model.contract.license.FamilyVersionLicense;
import com.g2forge.reassert.standard.model.contract.license.StandardLicenseFamily;

import lombok.Getter;

public class TestGitRepository extends ATestRepository<GitCoordinates> {
	@Getter(lazy = true)
	private final GitSystem system = getContext().findSystem(GitSystem.class);

	public Context.ContextBuilder computeContext(Context.ContextBuilder builder) {
		return super.computeContext(builder).module(GitModule.create());
	}
	
	@Override
	protected GitCoordinates createCoordinates() {
		return new GitCoordinates(getSystem(), "https://github.com/g2forge/alexandria.git", null);
	}

	@Test
	public void licenses() {
		final Graph<IVertex, IEdge> graph = getGraph();
		final Artifact<GitCoordinates> artifact = HReassertModel.findArtifact(graph, getCoordinates());
		final Collection<ILicenseApplied> licenses = HReassertModel.get(graph, artifact, true, Notice.class::isInstance, ITypeRef.of(ILicenseApplied.class));
		final Map<Class<?>, List<ILicenseApplied>> grouped = licenses.stream().collect(Collectors.groupingBy(Object::getClass));
		HAssert.assertTrue(grouped.get(UnknownLicense.class).size() == 1);
		HAssert.assertEquals(HCollection.asList(new FamilyVersionLicense(StandardLicenseFamily.Apache, new LicenseVersion(2, 0), false)), grouped.get(FamilyVersionLicense.class));
	}

	@Test
	public void load() {
		final Graph<IVertex, IEdge> graph = getGraph();
		final Artifact<GitCoordinates> artifact = HReassertModel.findArtifact(graph, getCoordinates());
		HAssert.assertEquals(getCoordinates(), artifact.getCoordinates());
	}

	@Test
	public void visualize() {
		final String actual = new DotRenderer().render(new ReassertGraphVisualizer(getContext()).visualize(getGraph()));
		HAssert.assertEquals(new Resource(getClass(), "visualize.dot"), actual);
	}
}
