package com.g2forge.reassert.standard.model.contract.usage;

import com.g2forge.reassert.core.api.ReassertLegalOpinion;
import com.g2forge.reassert.core.model.contract.usage.IUsageTerm;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@ReassertLegalOpinion
@Getter
@RequiredArgsConstructor
public enum StandardUsageTerm implements IUsageTerm {
	Commercial("Commercial usage"),
	DistributionPublic("Public distribution"),
	DistributionPrivate("Private distribution"),
	DistributionService("SaaS distribution"),
	UseLink("Linked"),
	UseCopy("Copied"),
	UseModified("Modified"),
	DistributingBinary("Binary distribution"),
	DistributingSource("Source distribution");

	protected final String description;

	@Override
	public String getName() {
		return name();
	}
}
