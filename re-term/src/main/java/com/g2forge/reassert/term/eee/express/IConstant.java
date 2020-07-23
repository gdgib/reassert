package com.g2forge.reassert.term.eee.express;

import com.g2forge.alexandria.java.adt.name.INamed;
import com.g2forge.reassert.term.eee.explain.model.IExplained;

public interface IConstant<T> extends IExpression<T>, IExplained<T>, INamed<String> {
	public T getValue();
}