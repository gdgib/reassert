package com.g2forge.reassert.core.model.contract.usage;

import com.g2forge.alexandria.java.core.marker.ISingleton;
import com.g2forge.reassert.core.model.contract.ITerms;
import com.g2forge.reassert.core.model.contract.Terms;

/**
 * Indicates that no usage was specified.
 */
public class UnspecifiedUsage implements IUsage, ISingleton {
	protected static final UnspecifiedUsage INSTANCE = new UnspecifiedUsage();

	public static UnspecifiedUsage create() {
		return INSTANCE;
	}

	private UnspecifiedUsage() {}

	@Override
	public String getName() {
		return "Unspecified Usage";
	}

	/**
	 * An unspecified usage has no specified terms, which means usage isn't possible.
	 */
	@Override
	public ITerms<IUsageTerm> getTerms() {
		return Terms.createNone();
	}
}