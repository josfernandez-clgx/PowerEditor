package com.mindbox.pe.model.table;

import java.util.List;

import com.mindbox.pe.model.EnumValue;

/**
 * Proxy interface that retrieves a list of {@link EnumValue} in a enumeration source on server.
 * 
 *
 */
public interface EnumerationSourceProxy {

	List<EnumValue> getAllEnumValues(String dataSourceName);
	
	List<EnumValue> getApplicableEnumValues(String dataSourceName, String selectorValue);
}
