package com.g2forge.reassert.term.eee.explain.convert;

import com.g2forge.enigma.backend.convert.textual.ITextualRenderContext;
import com.g2forge.reassert.term.eee.explain.model.IExplained;

public interface IExplanationRenderContext extends ITextualRenderContext<IExplained<?>, IExplanationRenderContext> {
	public ExplanationMode getMode();

	public IExplanationRenderContext value(Object value);
}
