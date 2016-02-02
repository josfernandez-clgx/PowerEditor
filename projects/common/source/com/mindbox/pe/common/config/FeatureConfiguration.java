/*
 * Created on 2004. 10. 8.
 *
 */
package com.mindbox.pe.common.config;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;


/**
 * Contains feature configurations.
 * 
 * @author Geneho Kim
 * @since PowerEditor 4.0.1
 */
public class FeatureConfiguration extends AbstractDigestedObjectHolder {

	private static final long serialVersionUID = 200410081400001L;

	public static final String PARAMETER_FEATURE = "parameter";
	public static final String PHASE_FEATURE = "phase";
	public static final String CBR_FEATURE = "cbr";
	
	private Map<String, Object> featureMap = new HashMap<String, Object>();
	
	public void addFeature(Object obj) {
		if (obj instanceof FeatureDefinition) {
			featureMap.put(((FeatureDefinition)obj).getName(), obj);
		}
	}
	
	public FeatureDefinition getFeatureConfig(String featureName) {
		return (FeatureDefinition) featureMap.get(featureName);
	}
	
	public boolean isFeatureEnabled(String featureName) {
		if (featureMap.containsKey(featureName)) {
			return getFeatureConfig(featureName).isEnable();
		}
		else {
			// GKim: maybe false is a better option
			return true;
		}
	}
	
	public Map<String, Object> getFeatureMap() {
		return Collections.unmodifiableMap(featureMap);
	}
}
