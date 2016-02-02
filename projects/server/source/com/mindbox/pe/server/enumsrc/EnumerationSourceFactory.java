package com.mindbox.pe.server.enumsrc;

import java.util.HashMap;
import java.util.Map;

import com.mindbox.pe.common.UtilBase;
import com.mindbox.pe.server.config.ConfigParameter;
import com.mindbox.pe.server.config.EnumerationSourceConfig;

/**
 * Factory for {@link EnumerationSource}.
 * 
 */
public final class EnumerationSourceFactory {

	enum Type {
		XML
	};

	public static EnumerationSource createEnumerationSource(EnumerationSourceConfig enumerationSourceConfig) {
		if (enumerationSourceConfig == null) throw new NullPointerException("enumerationSourceConfig cannot be null");
		if (UtilBase.isEmptyAfterTrim(enumerationSourceConfig.getType())) throw new IllegalArgumentException("type is required: " + enumerationSourceConfig);
		if (UtilBase.isEmptyAfterTrim(enumerationSourceConfig.getName())) throw new IllegalArgumentException("name is required: " + enumerationSourceConfig);
		EnumerationSource enumerationSource;
		switch (Type.valueOf(enumerationSourceConfig.getType())) {
		case XML:
			enumerationSource = new XMLEnumerationSource();
			break;
		default:
			throw new IllegalArgumentException("Invalid type: " + enumerationSourceConfig.getType());
		}
		
		// extract parameter map
		Map<String, String> paramMap = new HashMap<String, String>();
		for (ConfigParameter configParameter : enumerationSourceConfig.getParameterList()) {
			paramMap.put(configParameter.getName(), configParameter.getValue());
		}
		// initialize enumeration source
		enumerationSource.initialize(enumerationSourceConfig.getName(), enumerationSourceConfig.isSupportsSelector(), paramMap);
		
		return enumerationSource;
	}

	private EnumerationSourceFactory() {

	}
}
