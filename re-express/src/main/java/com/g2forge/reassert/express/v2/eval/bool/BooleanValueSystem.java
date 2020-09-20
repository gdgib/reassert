package com.g2forge.reassert.express.v2.eval.bool;

import java.util.Objects;

import com.g2forge.alexandria.java.core.marker.ISingleton;
import com.g2forge.reassert.express.v2.eval.value.IValueSystem;

public class BooleanValueSystem implements IValueSystem<Boolean>, ISingleton {
	private static final BooleanValueSystem INSTANCE = new BooleanValueSystem();

	public static BooleanValueSystem create() {
		return INSTANCE;
	}

	protected BooleanValueSystem() {}

	@Override
	public boolean isEqual(Boolean left, Boolean right) {
		return Objects.equals(left, right);
	}

	@Override
	public boolean isSame(Boolean left, Boolean right) {
		return Objects.equals(left, right);
	}

	@Override
	public boolean isValid(Boolean value) {
		return value != null;
	}
}
