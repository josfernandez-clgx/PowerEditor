package com.mindbox.pe.model.table;

import java.util.ArrayList;
import java.util.List;

import com.mindbox.pe.common.DomainClassProvider;
import com.mindbox.pe.common.UtilBase;
import com.mindbox.pe.model.EnumValue;
import com.mindbox.pe.model.domain.DomainAttribute;
import com.mindbox.pe.model.domain.DomainClass;
import com.mindbox.pe.model.template.ColumnDataSpecDigest;
import com.mindbox.pe.model.template.ColumnDataSpecDigest.EnumSourceType;

public class EnumValuesDataHelper {

	/**
	 * Converts the specified string grid cell value into a value object for enum column.
	 * @param columnDataSpecDigest columnDataSpecDigest 
	 * @param strValue the string value
	 * @param domainClassProvider domainClassProvider
	 * @param enumerationSourceProxy enumeration source proxy
	 * @return object value
	 */
	public static Object convertToEnumValue(final ColumnDataSpecDigest columnDataSpecDigest, final String strValue, final DomainClassProvider domainClassProvider,
			final EnumerationSourceProxy enumerationSourceProxy) {
		final List<EnumValue> enumValueList = getAllEnumValues(columnDataSpecDigest, domainClassProvider, enumerationSourceProxy);
		if (columnDataSpecDigest.isMultiSelectAllowed()) {
			return convertToEnumValueEnumValues(columnDataSpecDigest, strValue, enumValueList);
		}
		else {
			if (!UtilBase.isEmpty(strValue)) {
				EnumValue enumValueFound = getEnumValueForStringValue(enumValueList, strValue);
				if (enumValueFound != null) {
					return enumValueFound;
				}
			}
			return strValue;
		}
	}

	private static EnumValues<EnumValue> convertToEnumValueEnumValues(ColumnDataSpecDigest columnDataSpecDigest, String strValue, List<EnumValue> enumValueList) {
		EnumValues<String> parsed = EnumValues.parseValue(strValue, true, enumValueList);
		EnumValues<EnumValue> enumValues = new EnumValues<EnumValue>();
		enumValues.setSelectionExclusion(parsed.isSelectionExclusion());
		for (int i = 0; i < parsed.size(); i++) {
			EnumValue enumValue = getEnumValueForStringValue(enumValueList, parsed.get(i));
			if (enumValue != null) {
				enumValues.add(enumValue);
			}
		}
		return enumValues;
	}

	public static List<EnumValue> getAllEnumValues(ColumnDataSpecDigest columnDataSpecDigest, DomainClassProvider domainClassProvider,
			EnumerationSourceProxy enumerationSourceProxy) {
		if (columnDataSpecDigest.getEnumSourceType() == EnumSourceType.DOMAIN_ATTRIBUTE) {
			return getEnumValuesFromDomainAttribute(columnDataSpecDigest.getMappedAttribute(), domainClassProvider);
		}
		else if (columnDataSpecDigest.getEnumSourceType() == EnumSourceType.EXTERNAL) {
			return getAllEnumValuesFromEnumerationSource(columnDataSpecDigest.getEnumSourceName(), enumerationSourceProxy);
		}
		else {
			return columnDataSpecDigest.getColumnEnumValuesAsEnumValueList();
		}
	}

	private static final List<EnumValue> getAllEnumValuesFromEnumerationSource(String dataSourceName, EnumerationSourceProxy enumerationSourceProxy) {
		return enumerationSourceProxy.getAllEnumValues(dataSourceName);
	}

	private static EnumValue getEnumValueForStringValue(List<EnumValue> enumValues, String strValue) {
		// check deploy id first
		for (EnumValue enumValue : enumValues) {
			if (enumValue.hasDeployID() && strValue.equals(enumValue.getDeployID().toString())) {
				return enumValue;
			}
		}
		// check deploy value
		for (EnumValue enumValue : enumValues) {
			if (!enumValue.hasDeployID() && strValue.equals(enumValue.getDeployValue())) {
				return enumValue;
			}
		}
		// check display label
		for (EnumValue enumValue : enumValues) {
			if (strValue.equals(enumValue.getDisplayLabel())) {
				return enumValue;
			}
		}
		return null;
	}

	private static List<EnumValue> getEnumValuesFromDomainAttribute(String mappedAttribute, DomainClassProvider domainClassProvider) {
		List<EnumValue> enumValueList = new ArrayList<EnumValue>();
		if (mappedAttribute != null) {
			String[] strs = mappedAttribute.split("\\.");
			if (strs.length == 2) {
				DomainClass domainClass = domainClassProvider.getDomainClass(strs[0]);
				DomainAttribute attribute = domainClass == null ? null : domainClass.getDomainAttribute(strs[1]);
				if (attribute != null) {
					EnumValue[] enumValues = attribute.getEnumValues();
					for (int i = 0; i < enumValues.length; i++) {
						enumValueList.add(enumValues[i]);
					}
				}
			}
		}
		return enumValueList;
	}

}
