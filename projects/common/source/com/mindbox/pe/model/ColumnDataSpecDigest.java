package com.mindbox.pe.model;

import java.io.Serializable;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;

import com.mindbox.pe.common.UtilBase;
import com.mindbox.pe.common.format.FloatFormatter;
import com.mindbox.pe.model.table.DateDataHelper;


/**
 * Column data spec data holder for digester.
 * @author Geneho
 * @since PowerEditor 3.2.0
 */
public final class ColumnDataSpecDigest implements Serializable {

	public static enum EnumSourceType {
		DOMAIN_ATTRIBUTE, COLUMN, EXTERNAL;
	}

	public static final String TYPE_BOOLEAN = "Boolean";
	public static final String TYPE_CODE = "Code";
	public static final String TYPE_CURRENCY = "Currency";
	public static final String TYPE_CURRENCY_RANGE = "CurrencyRange";
	public static final String TYPE_DATE = "Date";
	public static final String TYPE_DATE_RANGE = "DateRange";
	public static final String TYPE_DATE_TIME = "DateTime";
	public static final String TYPE_DATE_TIME_RANGE = "DateTimeRange";
	public static final String TYPE_DYNAMIC_STRING = "DynamicString";
	public static final String TYPE_ENTITY = "EntityList";
	public static final String TYPE_ENUM_LIST = "EnumList";
	public static final String TYPE_FLOAT = "Float";
	public static final String TYPE_FLOAT_RANGE = "FloatRange";
	public static final String TYPE_INTEGER = "Integer";
	public static final String TYPE_INTEGER_RANGE = "IntegerRange";
	public static final String TYPE_PERCENT = "Percent";
	public static final String TYPE_RULE_ID = "RuleID";
	public static final String TYPE_STRING = "String";
	public static final String TYPE_SYMBOL = "Symbol";
	public static final String TYPE_TIME_RANGE = "TimeRange";

	private static final long serialVersionUID = 2004060810000L;

	private String entityType;
	private String allowEntity;
	private String allowCategory;
	private String type;
	private String multiSelect;
	private String allowBlank;
	private String minValue;
	private String maxValue;
	private String showLhsAttributes;
	private int precision = FloatFormatter.NO_PRECISION; // precision is only meaningful for float types (i.e. Float, floatRange, Currency, CurrencyRange, Percent)
	private String sortEnumValue;
	private final List<ColumnAttributeItemDigest> attributeItemList = new ArrayList<ColumnAttributeItemDigest>();
	private final List<String> enumValueList = new LinkedList<String>();
	private EnumSourceType enumSourceType;
	private String enumSourceName;
	private String enumSelectorColumnName;
	private String mappedAttribute = null;

	/** 
	 * Default constructor .
	 */
	public ColumnDataSpecDigest() {
	}

	/**
	 * Create a new instance of this that is an exact copy of the source.
	 * This performs deep-copy.
	 * @param source
	 * @since PowerEditor 4.3.2
	 */
	public ColumnDataSpecDigest(ColumnDataSpecDigest source) {
		this.type = source.type;
		this.entityType = source.entityType;
		this.allowCategory = source.allowCategory;
		this.allowEntity = source.allowEntity;
		this.multiSelect = source.multiSelect;
		this.allowBlank = source.allowBlank;
		this.minValue = source.minValue;
		this.maxValue = source.maxValue;
		this.showLhsAttributes = source.showLhsAttributes;
		this.precision = source.precision;
		this.sortEnumValue = source.sortEnumValue;
		for (ColumnAttributeItemDigest element : source.attributeItemList) {
			this.attributeItemList.add(new ColumnAttributeItemDigest(element));
		}
		for (String element : source.enumValueList) {
			this.enumValueList.add(new String(element));
		}
		this.mappedAttribute = source.mappedAttribute;
		this.enumSelectorColumnName = source.enumSelectorColumnName;
		this.enumSourceName = source.enumSourceName;
		this.enumSourceType = source.enumSourceType;
	}

	public boolean canBeSelector() {
		return type.equals(TYPE_BOOLEAN) || (type.equals(TYPE_ENUM_LIST) && !isMultiSelectAllowed());
	}

	public String getEnumSelectorColumnName() {
		return enumSelectorColumnName;
	}

	public boolean isEnumSelectorColumnSet() {
		return !UtilBase.isEmpty(enumSelectorColumnName);
	}

	public boolean isEnumListAndSelectorSetFor(String columnName) {
		return type.equals(ColumnDataSpecDigest.TYPE_ENUM_LIST) && enumSourceType == EnumSourceType.EXTERNAL
				&& columnName.equals(enumSelectorColumnName);
	}

	public void setEnumSelectorColumnName(String enumSelectorColumnName) {
		this.enumSelectorColumnName = enumSelectorColumnName;
	}

	public String getMappedAttribute() {
		return mappedAttribute;
	}

	public void setAttributeMap(String str) {
		this.mappedAttribute = str;
	}

	public EnumSourceType getEnumSourceType() {
		return enumSourceType;
	}

	public void setEnumSourceType(EnumSourceType enumSourceType) {
		this.enumSourceType = enumSourceType;
	}

	public void setEnumSourceTypeStr(String typeStr) {
		this.enumSourceType = EnumSourceType.valueOf(typeStr);
	}

	public String getEnumSourceName() {
		return enumSourceName;
	}

	public void setEnumSourceName(String enumSourceName) {
		this.enumSourceName = enumSourceName;
	}

	public String getEntityType() {
		return entityType;
	}

	public void setEntityType(String entityType) {
		this.entityType = entityType;
	}

	public String getAllowCategory() {
		return allowCategory;
	}

	public void setAllowCategory(String allowCategory) {
		this.allowCategory = allowCategory;
	}

	public boolean isCategoryAllowed() {
		return (allowCategory != null && allowCategory.equalsIgnoreCase(Constants.VALUE_YES));
	}

	public void setIsCategoryAllowed(boolean flag) {
		allowCategory = (flag ? Constants.VALUE_YES : Constants.VALUE_NO);
	}

	public String getAllowEntity() {
		return allowEntity;
	}

	public void setAllowEntity(String allowEntity) {
		this.allowEntity = allowEntity;
	}

	public boolean isEntityAllowed() {
		return (allowEntity != null && allowEntity.equalsIgnoreCase(Constants.VALUE_YES));
	}

	public void setIsEntityAllowed(boolean flag) {
		allowEntity = (flag ? Constants.VALUE_YES : Constants.VALUE_NO);
	}

	public void clearEnumValues() {
		enumValueList.clear();
	}

	public void addColumnEnumValue(String value) {
		enumValueList.add(value);
	}

	public List<String> getAllColumnEnumValues() {
		return Collections.unmodifiableList(enumValueList);
	}

	public List<EnumValue> getColumnEnumValuesAsEnumValueList() {
		List<EnumValue> enumValueInstanceList = new LinkedList<EnumValue>();
		for (String value : enumValueList) {
			EnumValue evInstance = new EnumValue();
			evInstance.setDeployValue(value);
			evInstance.setDisplayLabel(value);
			enumValueInstanceList.add(evInstance);
		}
		return enumValueInstanceList;
	}

	public boolean hasEnumValue() {
		return !enumValueList.isEmpty();
	}

	public void addAttributeItem(ColumnAttributeItemDigest digest) {
		attributeItemList.add(digest);
	}

	public List<ColumnAttributeItemDigest> getAllAttributeItems() {
		return Collections.unmodifiableList(attributeItemList);
	}

	public boolean hasAttributeItem() {
		return !attributeItemList.isEmpty();
	}

	public void clearAttributeItems() {
		attributeItemList.clear();
	}

	/**
	 * @return Returns the allowBlank.
	 */
	public String getAllowBlank() {
		return allowBlank;
	}

	/**
	 * @param allowBlank The allowBlank to set.
	 */
	public void setAllowBlank(String allowBlank) {
		this.allowBlank = allowBlank;
	}

	public boolean isBlankAllowed() {
		return (allowBlank != null && allowBlank.equalsIgnoreCase(Constants.VALUE_YES));
	}

	public void setIsBlankAllowed(boolean flag) {
		allowBlank = (flag ? Constants.VALUE_YES : Constants.VALUE_NO);
	}

	/**
	 * @return Returns the maxValue.
	 */
	public String getMaxValue() {
		return maxValue;
	}

	/**
	 * @param maxValue The maxValue to set.
	 */
	public void setMaxValue(String maxValue) {
		this.maxValue = maxValue;
	}

	/**
	 * @return Returns the minValue.
	 */
	public String getMinValue() {
		return minValue;
	}

	public boolean hasMinValue() {
		return !UtilBase.isEmptyAfterTrim(minValue);
	}

	public boolean hasMaxValue() {
		return !UtilBase.isEmptyAfterTrim(maxValue);
	}

	public long getMaxAsLong() {
		if (maxValue == null) {
			return Long.MAX_VALUE;
		}
		try {
			return Long.valueOf(maxValue).longValue();
		}
		catch (Exception ex) {
			return Long.MAX_VALUE;
		}
	}

	public double getMaxAsDouble() {
		if (maxValue == null) {
			return Double.POSITIVE_INFINITY;
		}
		try {
			return Double.valueOf(maxValue).doubleValue();
		}
		catch (Exception ex) {
			return Double.POSITIVE_INFINITY;
		}
	}

	public float getMaxAsFloat() {
		if (maxValue == null) {
			return Float.POSITIVE_INFINITY;
		}
		try {
			return Float.valueOf(maxValue).floatValue();
		}
		catch (Exception ex) {
			return Float.POSITIVE_INFINITY;
		}
	}

	public Date getMaxAsDate() {
		try {
			return DateDataHelper.asDateValue(maxValue);
		}
		catch (ParseException e) {
			Logger.getLogger(getClass()).warn("<MaxValue> tag must specify a date in MM/dd/yyyy format: it has " + maxValue);
			return null;
		}
	}

	public Date getMinAsDate() {
		try {
			return DateDataHelper.asDateValue(maxValue);
		}
		catch (ParseException e) {
			Logger.getLogger(getClass()).warn("<MinValue> tag must specify a date in MM/dd/yyyy format: it has " + minValue);
			return null;
		}
	}

	public long getMinAsLong() {
		if (minValue == null) {
			return 0;
		}
		try {
			return Long.valueOf(minValue).longValue();
		}
		catch (Exception ex) {
			return 0;
		}
	}

	public double getMinAsDouble() {
		if (minValue == null) {
			return 0.0;
		}
		try {
			return Double.valueOf(minValue).doubleValue();
		}
		catch (Exception ex) {
			return 0.0;
		}
	}

	public float getMinAsFloat() {
		if (minValue == null) {
			return 0.0f;
		}
		try {
			return Float.valueOf(minValue).floatValue();
		}
		catch (Exception ex) {
			return 0.0f;
		}
	}

	/**
	 * @param minValue The minValue to set.
	 */
	public void setMinValue(String minValue) {
		this.minValue = minValue;
	}

	/**
	 * @return Returns the multiSelect.
	 */
	public String getMultipleSelect() {
		return multiSelect;
	}

	/**
	 * @param multiSelect The multiSelect to set.
	 */
	public void setMultipleSelect(String multiSelect) {
		this.multiSelect = multiSelect;
	}

	public boolean isMultiSelectAllowed() {
		return (multiSelect != null && multiSelect.equalsIgnoreCase(Constants.VALUE_YES));
	}

	public void setIsMultiSelectAllowed(boolean flag) {
		multiSelect = (flag ? Constants.VALUE_YES : Constants.VALUE_NO);
	}

	/**
	 * @return Returns the showLhsAttributes.
	 */
	public String getShowLhsAttributes() {
		return showLhsAttributes;
	}

	/**
	 * @param showLhsAttributes The showLhsAttributes to set.
	 */
	public void setShowLhsAttributes(String showLhsAttributes) {
		this.showLhsAttributes = showLhsAttributes;
	}

	public boolean isLHSAttributeVisible() {
		return (showLhsAttributes != null && showLhsAttributes.equalsIgnoreCase(Constants.VALUE_YES));
	}

	public void setIsLHSAttributeVisible(boolean flag) {
		showLhsAttributes = (flag ? Constants.VALUE_YES : Constants.VALUE_NO);
	}

	/**
	 * @return Returns the type.
	 */
	public String getType() {
		return type;
	}

	public boolean isRangeType() {
		return type.endsWith("Range");
	}

	public boolean isDoubleType() {
		return type.equals(TYPE_CURRENCY) || type.equals(TYPE_FLOAT) || type.equals(TYPE_PERCENT);
	}

	/**
	 * @param type The type to set. Must be one of TYPE_* constants defined in this class.
	 */
	public void setType(String type) {
		this.type = type;
	}

	public final boolean isRuleIDType() {
		return type.equals(TYPE_RULE_ID);
	}

	public boolean isPrecisionSet() {
		return precision >= FloatFormatter.MIN_PRECISION;
	}

	public int getPrecision() {
		return precision;
	}

	public void setPrecision(int precision) {
		if (FloatFormatter.isValidPrecision(precision)) this.precision = precision;
	}

	/** backward compatibility for importing precision values either in the old "<precision>.<width>" format, or the new "<precision>" format */
	public void setPrecisionImport(String precision) {
		if (precision != null && precision.trim().length() > 0) {
			String[] parts = precision.split("\\.");
			setPrecision(Integer.parseInt(parts[0]));
		}
	}

	public String toString() {
		return "DataSpec[" + type + ",blank=" + allowBlank + ",multi=" + multiSelect + ",min=" + minValue + ",max=" + maxValue
				+ ",precision=" + precision + ",showLHS=" + showLhsAttributes + "]";
	}

	/**
	 * @return Returns the sortEnumValue.
	 */
	public String getSortEnumValue() {
		return sortEnumValue;
	}

	/**
	 * @param sortEnumValue The sortEnumValue to set.
	 */
	public void setSortEnumValue(String sortEnumValue) {
		this.sortEnumValue = sortEnumValue;
	}

	public boolean isEnumValueNeedSorted() {
		return (sortEnumValue != null && sortEnumValue.equalsIgnoreCase(Constants.VALUE_YES));
	}

	public void setIsEnumValueNeedSorted(boolean flag) {
		sortEnumValue = (flag ? Constants.VALUE_YES : Constants.VALUE_NO);
	}

	public void resetColumnEnumSourceTypeIfNecessary() {
		if (type.equals(ColumnDataSpecDigest.TYPE_ENUM_LIST) && enumSourceType == null) {
			if (!hasEnumValue() && !UtilBase.isEmpty(mappedAttribute)) {
				setEnumSourceType(EnumSourceType.DOMAIN_ATTRIBUTE);
			}
			else {
				setEnumSourceType(EnumSourceType.COLUMN);
			}
		}

	}
}