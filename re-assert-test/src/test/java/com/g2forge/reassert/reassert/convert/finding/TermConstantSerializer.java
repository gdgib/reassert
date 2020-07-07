package com.g2forge.reassert.reassert.convert.finding;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.g2forge.alexandria.java.function.IFunction1;
import com.g2forge.reassert.core.api.described.IDescription;
import com.g2forge.reassert.core.model.IVertex;
import com.g2forge.reassert.term.analyze.model.TermConstant;

import lombok.Getter;

@Getter
public class TermConstantSerializer extends StdSerializer<TermConstant> {
	private static final long serialVersionUID = 4188997795853592234L;

	protected final IFunction1<IVertex, IDescription> vertexDescriber;

	protected TermConstantSerializer(IFunction1<IVertex, IDescription> vertexDescriber) {
		super(TermConstant.class);
		this.vertexDescriber = vertexDescriber;
	}

	@Override
	public void serialize(TermConstant value, JsonGenerator generator, SerializerProvider provider) throws IOException {
		generator.getCodec().writeValue(generator, toStored(value));
	}

	protected StoredTermConstant toStored(TermConstant value) {
		final String contract = getVertexDescriber().apply(value.getContract()).getName();
		return new StoredTermConstant(value.getTerm(), contract);
	}
}