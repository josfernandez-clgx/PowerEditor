/*
 * Created on 2005. 5. 5.
 *
 */
package com.mindbox.pe.server.config;

import java.util.HashMap;
import java.util.Map;


/**
 * Container of {@link com.mindbox.pe.server.config.AttributeConfiguration} instances.
 * @author Geneho Kim
 * @since PowerEditor 4.2.0
 */
abstract class AbstractAttributeConfigurationHolder {

	private final Map<String, AttributeConfiguration> attributeMap = new HashMap<String, AttributeConfiguration>();

	public final void addAttributeConfiguration(AttributeConfiguration attributeConfig) {
		synchronized (attributeMap) {
			attributeMap.put(attributeConfig.getType(), attributeConfig);
		}
	}

	public final AttributeConfiguration findAttributeConfiguration(String type) {
		synchronized (attributeMap) {
			return attributeMap.get(type);
		}
	}

	public final String findAttributeName(String type, String defaultName) {
		AttributeConfiguration ac = findAttributeConfiguration(type);
		return (ac == null ? defaultName : ac.getName());
	}

	public final String findAttributeValue(String type, String defaultValue) {
		AttributeConfiguration ac = findAttributeConfiguration(type);
		return (ac == null ? defaultValue : ac.getValue());
	}

	public final boolean findAttributeIsValueAsString(String type) {
		AttributeConfiguration ac = findAttributeConfiguration(type);
		return (ac == null ? false : ac.isValueAsString());
	}

	public String toString() {
		return "attributeSize=" + attributeMap.size();
	}
}