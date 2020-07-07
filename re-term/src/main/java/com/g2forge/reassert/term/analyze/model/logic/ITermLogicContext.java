package com.g2forge.reassert.term.analyze.model.logic;

import com.g2forge.reassert.core.model.contract.TermRelation;
import com.g2forge.reassert.core.model.contract.license.ILicenseTerm;
import com.g2forge.reassert.core.model.contract.usage.IUsageTerm;
import com.g2forge.reassert.term.eee.express.IExpression;

public interface ITermLogicContext {
	public default Object getContext() {
		throw new UnsupportedOperationException();
	}

	public IExpression<TermRelation> term(ILicenseTerm licenseTerm);

	public IExpression<TermRelation> term(IUsageTerm usageTerm);
}