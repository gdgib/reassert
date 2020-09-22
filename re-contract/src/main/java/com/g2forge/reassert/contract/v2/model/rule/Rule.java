package com.g2forge.reassert.contract.v2.model.rule;

import com.g2forge.reassert.contract.v2.model.ICTName;
import com.g2forge.reassert.contract.v2.model.finding.IFindingFactory;
import com.g2forge.reassert.core.model.contract.terms.TermRelation;
import com.g2forge.reassert.express.v2.model.IExpression;

import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@Builder(toBuilder = true)
@RequiredArgsConstructor
public class Rule implements IRule {
	protected final IExpression<ICTName, TermRelation> expression;

	protected final IFindingFactory<?> finding;
}
