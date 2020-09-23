package com.g2forge.reassert.reassert.model.report;

import org.slf4j.event.Level;

import com.g2forge.reassert.contract.v2.model.finding.rule.INoticeFinding;
import com.g2forge.reassert.core.model.contract.terms.TermRelation;
import com.g2forge.reassert.express.v2.model.IExplained;

import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@Builder(toBuilder = true)
@RequiredArgsConstructor
public class TestRuleFinding implements INoticeFinding {
	protected final Level level;

	protected final String description;

	@Override
	public IExplained<TermRelation> getResult() {
		throw new UnsupportedOperationException();
	}
}
