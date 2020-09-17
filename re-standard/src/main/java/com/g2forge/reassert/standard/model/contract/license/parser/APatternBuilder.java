package com.g2forge.reassert.standard.model.contract.license.parser;

import java.util.regex.Pattern;

import com.g2forge.alexandria.java.function.IConsumer1;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter(AccessLevel.PROTECTED)
@RequiredArgsConstructor
public abstract class APatternBuilder<T> implements IPatternBuilder<T> {
	protected final StringBuilder builder;

	protected final boolean gap;

	protected boolean current = true;

	@Override
	public IPatternBuilder<T> alt(@SuppressWarnings("unchecked") IConsumer1<? super IPatternBuilder<?>>... alternatives) {
		final StringBuilder builder = getBuilder();
		builder.append('(');
		boolean first = true;
		for (IConsumer1<? super IPatternBuilder<?>> alternative : alternatives) {
			if (first) first = false;
			else builder.append('|');
			builder.append('(');
			alternative.accept(this);
			builder.append(')');
		}
		builder.append(')');
		return this;
	}

	protected void assertCurrent() {
		if (!isCurrent()) throw new IllegalStateException();
	}

	@Override
	public IPatternBuilder<IPatternBuilder<T>> child(boolean required, boolean gap) {
		assertCurrent();

		final StringBuilder builder = getBuilder();
		builder.append('(');
		current = false;
		final APatternBuilder<T> retVal = this;
		return new APatternBuilder<IPatternBuilder<T>>(builder, gap) {
			@Override
			public IPatternBuilder<T> build() {
				retVal.current = true;
				builder.append(')');
				if (!required) builder.append('?');
				return retVal;
			}
		};
	}

	protected static final String GAP_PATTERN = "[-_\\p{Space}]*";

	protected StringBuilder gap() {
		final StringBuilder builder = getBuilder();
		if (isGap() && !isEmpty()) builder.append(GAP_PATTERN);
		return builder;
	}

	protected boolean isEmpty() {
		return builder.length() <= 0;
	}

	@Override
	public IPatternBuilder<IPatternBuilder<T>> optional() {
		return child(false, isGap());
	}

	@Override
	public IPatternBuilder<T> text(String text) {
		assertCurrent();

		gap().append(Pattern.quote(text));
		return this;
	}

	@Override
	public IPatternBuilder<T> version(int major, Integer minor, Integer patch) {
		assertCurrent();

		final StringBuilder builder = getBuilder();
		if (isGap() && !isEmpty()) builder.append("((,?").append(GAP_PATTERN).append("v ?)|(,?").append(GAP_PATTERN);
		else builder.append("((v ?)|(");
		builder.append("ve(r?)sion").append(GAP_PATTERN).append(")|(").append(GAP_PATTERN).append("))");

		builder.append(major);

		final String separator = "[-.]";

		if ((minor == null) || (minor == 0)) builder.append('(').append(separator).append("0)?");
		else builder.append(separator).append(minor);

		if ((patch != null) && (patch != 0)) builder.append(separator).append(patch);

		builder.append("\\.?");
		return this;
	}
}