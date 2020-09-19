package com.g2forge.reassert.express.v2.model;

import com.g2forge.alexandria.java.adt.name.INamed;
import com.g2forge.alexandria.java.function.ISupplier;

public interface ILiteral<Name, Value> extends IConstant<Name, Value>, INamed<Name>, ISupplier<Value> {}
