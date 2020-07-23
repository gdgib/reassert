package com.g2forge.reassert.maven.api;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;

import com.g2forge.alexandria.java.function.IFunction1;
import com.g2forge.alexandria.java.io.RuntimeIOException;
import com.g2forge.gearbox.command.process.IProcess;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Singular;

@Getter
@Builder(toBuilder = true)
@RequiredArgsConstructor
public class ProcessOutputHandler<K> {
	public static interface IOutputMatcher {
		public boolean isApplicable(boolean success);

		/**
		 * Test if this condition is now a match.
		 * 
		 * @param line A line of output
		 * @param output {@code true} if the line comes from stdout, {@code false} if it comes from stderr.
		 * @return {@code null} if this condition may or may not match, {@code true} if it will and we can stop scanning, {@code false} if it will not match and
		 *         we can stop scanning.
		 */
		public Boolean isMatch(String line, boolean output);
	}

	protected final Logger log;

	@Singular
	protected final Set<K> keys;

	protected final IFunction1<? super K, ? extends IOutputMatcher> matcherFactory;

	public Set<K> handle(IProcess process) {
		final Map<K, IOutputMatcher> possible = new LinkedHashMap<>();
		for (K key : getKeys()) {
			final IOutputMatcher matcher = getMatcherFactory().apply(key);
			if (!matcher.isApplicable(process.isSuccess())) continue;
			possible.put(key, matcher);
		}
		final Set<K> retVal = new LinkedHashSet<>();

		// Note that we proceed even if there are no matchers, because this is the process that logs the outputs, too

		try {
			stream(process, possible, retVal, true);
			stream(process, possible, retVal, false);
		} catch (IOException e) {
			throw new RuntimeIOException(e);
		}

		return retVal;
	}

	protected void stream(IProcess process, final Map<K, IOutputMatcher> possible, final Set<K> retVal, boolean output) throws IOException {
		try (final BufferedReader reader = new BufferedReader(new InputStreamReader(output ? process.getStandardOutput() : process.getStandardError()))) {
			while (true) {
				final String line = reader.readLine();
				if (line == null) break;
				if (log.isDebugEnabled()) log.debug(line);

				final Set<K> complete = new HashSet<>();
				for (Map.Entry<K, IOutputMatcher> entry : possible.entrySet()) {
					final Boolean match = entry.getValue().isMatch(line, output);
					if (match == null) continue;
					if (match) retVal.add(entry.getKey());
					complete.add(entry.getKey());
				}
				complete.forEach(possible::remove);
			}
		}
	}
}