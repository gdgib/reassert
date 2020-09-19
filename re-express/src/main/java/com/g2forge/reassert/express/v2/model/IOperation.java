package com.g2forge.reassert.express.v2.model;

import java.util.List;

import com.g2forge.alexandria.java.function.builder.IBuilder;

public interface IOperation<Name, Value> extends IExpression<Name, Value> {
	public interface IOperationBuilder<Name, Value, Builder extends IOperationBuilder<Name, Value, Builder, Built>, Built extends IOperation<Name, Value>> extends IBuilder<Built> {
		public Builder argument(IExpression<Name, Value> argument);

		public default Builder argument$(Name name, Value value) {
			return argument(new Literal<>(name, value));
		}

		public default Builder argument$(Value value) {
			return argument$(null, value);
		}

		public default Built valid() {
			final Built retVal = build();
			if (!retVal.getOperator().isValid(retVal.getArguments())) throw new IllegalArgumentException();
			return retVal;
		}
	}

	public interface IOperator {
		public <Name, Value> IOperationBuilder<Name, Value, ?, ?> builder();

		public default boolean isValid(List<? extends IExpression<?, ?>> arguments) {
			return true;
		}
	}

	public List<? extends IExpression<Name, Value>> getArguments();

	public IOperator getOperator();

	@Override
	public default boolean isSame(IExpression<?, ?> that) {
		if (this == that) return true;
		if ((that == null) || !(that instanceof IOperation)) return false;

		final IOperation<?, ?> cast = (IOperation<?, ?>) that;
		if (!getOperator().equals(cast.getOperator())) return false;
		if (!HExpression.isSame(getArguments(), cast.getArguments())) return false;
		return true;
	}
}