/*
 * Created on 2005. 9. 1.
 *
 */
package com.mindbox.pe.server.config;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.mindbox.pe.common.config.ConfigUtil;
import com.mindbox.pe.server.Util;
import com.mindbox.pe.xsd.config.LHSPatternType;
import com.mindbox.pe.xsd.config.RuleGenerationLHS;


/**
 * A set of lineage pattern configs.
 * @author Geneho Kim
 * @since PowerEditor 4.3.7
 */
public final class LineagePatternConfigHelper {

	private final Map<String, List<RuleGenerationLHS.Pattern>> configMap = new HashMap<String, List<RuleGenerationLHS.Pattern>>();
	private boolean noConfigAdded = true;

	public LineagePatternConfigHelper(final RuleGenerationLHS ruleGenerationLHS) {
		for (final RuleGenerationLHS.Pattern patternConfig : ruleGenerationLHS.getPattern()) {
			if (patternConfig.getType() == LHSPatternType.LINEAGE) {
				// Validate pattern
				final boolean isPatternOn = ConfigUtil.isPatternOn(patternConfig);
				if (isPatternOn && Util.isEmpty(patternConfig.getPrefix())) {
					throw new IllegalArgumentException("Lineage pattern has no prefix: " + patternConfig);
				}
				if (isPatternOn && Util.isEmpty(patternConfig.getText())) {
					throw new IllegalArgumentException("Lineage pattern has no text: " + patternConfig);
				}
				if (isPatternOn && Util.isEmpty(patternConfig.getVariable())) {
					throw new IllegalArgumentException("Lineage pattern has no variable: " + patternConfig);
				}

				if (isPatternOn) {
					if (noConfigAdded) {
						configMap.clear();
						noConfigAdded = false;
					}

					final String[] keys = patternConfig.getPrefix().split("\\,");
					for (int i = 0; i < keys.length; i++) {
						getPatternConfigList(keys[i].toUpperCase()).add(patternConfig);
					}
				}
			}
		}
	}

	/**
	 * Gets the lineage pattern config for the specified prefix.
	 * @param prefix
	 * @return the lineage pattern config for <code>prefix</code>
	 */
	public List<RuleGenerationLHS.Pattern> getLineagePatternConfigs(final String prefix) {
		synchronized (configMap) {
			return Collections.unmodifiableList(getPatternConfigList(prefix));
		}
	}

	private List<RuleGenerationLHS.Pattern> getPatternConfigList(final String key) {
		if (!configMap.containsKey(key)) {
			configMap.put(key, new ArrayList<RuleGenerationLHS.Pattern>());
		}
		return configMap.get(key);
	}

	/**
	 * Get a list of prefix as a string array.
	 * Each prefix may have a different lineage pattern config.
	 * @return a list of prefix
	 */
	public List<String> getPrefix() {
		synchronized (configMap) {
			return Collections.unmodifiableList(new ArrayList<String>(configMap.keySet()));
		}
	}

	public int size() {
		return configMap.size();
	}
}
