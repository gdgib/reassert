package com.g2forge.reassert.contract.model.logic;

import java.util.List;

import com.g2forge.alexandria.java.core.helpers.HCollection;
import com.g2forge.reassert.core.model.contract.terms.ITerm;
import com.g2forge.reassert.expression.express.Operation;

import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.Singular;

@Data
@Builder(toBuilder = true)
@RequiredArgsConstructor
public class OperationTerm implements ITerm {
	protected final Operation.Operator operator;

	@Singular
	protected final List<ITerm> arguments;

	public OperationTerm(Operation.Operator operator, ITerm... arguments) {
		this(operator, HCollection.asList(arguments));
	}

	@Override
	public String getDescription() {
		throw new UnsupportedOperationException();
	}

	@Override
	public String getName() {
		throw new UnsupportedOperationException();
	}
}