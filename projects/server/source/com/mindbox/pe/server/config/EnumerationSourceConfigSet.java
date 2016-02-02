package com.mindbox.pe.server.config;

import java.io.Reader;
import java.util.ArrayList;
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

public class EnumerationSourceConfigSet implements EnumerationSourceProxy {

	private final List<EnumerationSourceConfig> enumSourceConfigList = new ArrayList<EnumerationSourceConfig>();
	private final Map<String, EnumerationSource> enumerationSourceMap = new HashMap<String, EnumerationSource>();

	public EnumerationSourceConfigSet(Reader reader) {
		try {
			ConfigXMLDigester.getInstance().digestEnumerationSourceConfig(reader, this);
			setEnumSources();
		}
		catch (Exception e) {
			throw new RuntimeException("Failed to digest enumeration source confiuration.", e);
		}
	}

	public void add(EnumerationSourceConfig enumerationSourceConfig) {
		enumSourceConfigList.add(enumerationSourceConfig);
	}

	public EnumerationSource getEnumerationSource(String name) {
		if (UtilBase.isEmptyAfterTrim(name)) throw new IllegalArgumentException("name is required");
		if (!enumerationSourceMap.containsKey(name))
			throw new IllegalArgumentException(String.format("No enumeration source with name '%s' found", name));
		return enumerationSourceMap.get(name);
	}

	public Set<String> getEnumerationSourceNames() {
		return Collections.unmodifiableSet(enumerationSourceMap.keySet());
	}

	private void setEnumSources() {
		for (EnumerationSourceConfig enumSourceConfig : enumSourceConfigList) {
			enumerationSourceMap.put(enumSourceConfig.getName().trim(), EnumerationSourceFactory.createEnumerationSource(enumSourceConfig));
		}
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
