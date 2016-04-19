package com.mindbox.pe.server.config;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.mindbox.pe.common.UtilBase;
import com.mindbox.pe.model.EnumValue;
import com.mindbox.pe.model.table.EnumerationSourceProxy;
import com.mindbox.pe.server.enumsrc.EnumerationSource;
import com.mindbox.pe.server.enumsrc.EnumerationSourceFactory;
import com.mindbox.pe.xsd.config.EnumerationSources;

public class EnumerationSourceConfigHelper implements EnumerationSourceProxy {

	private final Map<String, EnumerationSource> enumerationSourceMap = new HashMap<String, EnumerationSource>();

	public EnumerationSourceConfigHelper(final EnumerationSources enumerationSources) {
		if (enumerationSources != null) {
			try {
				for (final EnumerationSources.EnumerationSource enumSourceConfig : enumerationSources.getEnumerationSource()) {
					enumerationSourceMap.put(enumSourceConfig.getName().trim(), EnumerationSourceFactory.createEnumerationSource(enumSourceConfig));
				}
			}
			catch (Exception e) {
				throw new RuntimeException("Failed to digest enumeration source confiuration.", e);
			}
		}
	}

	public EnumerationSource getEnumerationSource(String name) {
		if (UtilBase.isEmptyAfterTrim(name)) throw new IllegalArgumentException("name is required");
		if (!enumerationSourceMap.containsKey(name)) throw new IllegalArgumentException(String.format("No enumeration source with name '%s' found", name));
		return enumerationSourceMap.get(name);
	}

	public Set<String> getEnumerationSourceNames() {
		return Collections.unmodifiableSet(enumerationSourceMap.keySet());
	}

	@Override
	public List<EnumValue> getAllEnumValues(String dataSourceName) {
		return getEnumerationSource(dataSourceName).getAllEnumValues();
	}

	@Override
	public List<EnumValue> getApplicableEnumValues(String dataSourceName, String selectorValue) {
		return getEnumerationSource(dataSourceName).getApplicable(selectorValue);
	}
}
