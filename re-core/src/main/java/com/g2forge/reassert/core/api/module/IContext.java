package com.g2forge.reassert.core.api.module;

import java.util.Collection;

import com.g2forge.alexandria.java.core.helpers.HStream;
import com.g2forge.reassert.cache.ICache;
import com.g2forge.reassert.core.api.described.IDescriber;
import com.g2forge.reassert.core.api.described.IDescription;
import com.g2forge.reassert.core.api.module.config.IConfig;
import com.g2forge.reassert.core.api.parser.IParser;
import com.g2forge.reassert.core.api.scanner.IScanner;
import com.g2forge.reassert.core.api.system.ISystem;
import com.g2forge.reassert.core.model.contract.license.ILicenseApplied;
import com.g2forge.reassert.core.model.contract.terms.ITermsLoader;
import com.g2forge.reassert.core.model.contract.usage.IUsageApplied;
import com.g2forge.reassert.core.model.coordinates.ICoordinates;

public interface IContext {
	public IDescription describe(Object object);

	public default <S extends ISystem<?>> S findSystem(Class<S> klass) {
		return HStream.findOne(getSystems().stream().filter(klass::isInstance).map(klass::cast));
	}

	public default <Coordinates extends ICoordinates> ISystem<Coordinates> findSystem(Coordinates coordinates) {
		try {
			@SuppressWarnings("unchecked")
			final ISystem<Coordinates> retVal = (ISystem<Coordinates>) HStream.findOne(getSystems().stream().filter(s -> s.isValid(coordinates)));
			return retVal;
		} catch (IllegalArgumentException exception) {
			throw new IllegalArgumentException(String.format("Failed to find system for coordinates: %1$s", coordinates), exception);
		}
	}
	
	public ICache getCache();

	public IConfig getConfig();

	public Collection<IDescriber<?>> getDescribers();

	public IParser<ILicenseApplied> getLicenseParser();
	
	public ITermsLoader getTermsLoader();
	
	public IScanner getScanner();
	
	public Collection<ISystem<?>> getSystems();
	
	public IParser<IUsageApplied> getUsageParser();
}
