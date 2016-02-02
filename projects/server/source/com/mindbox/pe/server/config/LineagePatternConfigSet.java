/*
 * Created on 2005. 9. 1.
 *
 */
package com.mindbox.pe.server.config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.mindbox.pe.server.Util;
import com.mindbox.pe.server.config.RuleGenerationConfiguration.LineagePatternConfig;


/**
 * A set of lineage pattern configs.
 * @author Geneho Kim
 * @since PowerEditor 4.3.7
 */
public final class LineagePatternConfigSet {

	/**
	 * Creates a new instance of this as a copy of the specified source.
	 * @param source
	 * @return a new instance of this as a copy of the specified source
	 */
	static LineagePatternConfigSet newInstance(LineagePatternConfigSet source) {
		LineagePatternConfigSet copy = new LineagePatternConfigSet();
		copy.configMap.putAll(source.configMap);
		return copy;
	}

	private final Map<String, List<LineagePatternConfig>> configMap = new HashMap<String, List<LineagePatternConfig>>();
	private boolean noConfigAdded = true;

	void addLineagePatternConfig(RuleGenerationConfiguration.LineagePatternConfig patternConfig) {
		if (patternConfig == null) throw new NullPointerException("Lineage pattern cannot be null");
		if (patternConfig.isPatternOn() && Util.isEmpty(patternConfig.getPrefix())) throw new IllegalArgumentException("Lineage pattern has no prefix: " + patternConfig);
		if (patternConfig.isPatternOn() && Util.isEmpty(patternConfig.getText())) throw new IllegalArgumentException("Lineage pattern has no text: " + patternConfig);
		if (patternConfig.isPatternOn() && Util.isEmpty(patternConfig.getVariable())) throw new IllegalArgumentException("Lineage pattern has no variable: " + patternConfig);

		if (!patternConfig.isPatternOn()) {
			//System.out.println("Lineage pattern ignored b/c generate is set to no: " + patternConfig);
			return;
		}

		synchronized (configMap) {
			if (noConfigAdded) {
				configMap.clear();
				noConfigAdded = false;
			}

			String[] keys = patternConfig.getPrefix().split("\\,");
			for (int i = 0; i < keys.length; i++) {
				getPatternConfigList(keys[i].toUpperCase()).add(patternConfig);
			}
		}
	}

	private List<LineagePatternConfig> getPatternConfigList(String key) {
		if (!configMap.containsKey(key)) {
			configMap.put(key, new ArrayList<LineagePatternConfig>());
		}
		return configMap.get(key);
	}

	/**
	 * Get a list of prefix as a string array.
	 * Each prefix may have a different lineage pattern config.
	 * @return a list of prefix
	 */
	public String[] getPrefix() {
		synchronized (configMap) {
			return configMap.keySet().toArray(new String[0]);
		}
	}

	/**
	 * Gets the lineage pattern config for the specified prefix.
	 * @param prefix
	 * @return the lineage pattern config for <code>prefix</code>
	 */
	public RuleGenerationConfiguration.LineagePatternConfig[] getLineagePatternConfigs(String prefix) {
		synchronized (configMap) {
			return getPatternConfigList(prefix).toArray(
					new RuleGenerationConfiguration.LineagePatternConfig[0]);
		}
	}

	public int size() {
		return configMap.size();
	}
}
