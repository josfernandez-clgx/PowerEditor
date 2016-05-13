package com.mindbox.pe.common.config;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import com.mindbox.pe.common.UtilBase;
import com.mindbox.pe.model.Constants;
import com.mindbox.pe.model.TemplateUsageType;
import com.mindbox.pe.xsd.config.CellSelectionType;
import com.mindbox.pe.xsd.config.EntityProperty;
import com.mindbox.pe.xsd.config.EntityPropertyType;
import com.mindbox.pe.xsd.config.EntityTab;
import com.mindbox.pe.xsd.config.EntityType;
import com.mindbox.pe.xsd.config.FeatureConfig.Feature;
import com.mindbox.pe.xsd.config.FeatureNameType;
import com.mindbox.pe.xsd.config.GuidelineTab;
import com.mindbox.pe.xsd.config.PowerEditorConfiguration;
import com.mindbox.pe.xsd.config.RuleGenerationLHS;
import com.mindbox.pe.xsd.config.UserInterfaceConfig;

/**
 * @author Geneho Kim
 * @author MindBox
 * @since PowerEditor 3.0.0
 */
public final class ConfigUtil {

	private static final SimpleDateFormat XML_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
	private static final SimpleDateFormat XML_DATE_FORMAT_REPORT = new SimpleDateFormat("MM/dd/yy HH:mm a");

	public static boolean allowsIdenticalDateSynonymDates(final UserInterfaceConfig userInterfaceConfig) {
		return userInterfaceConfig.getDateSynonym() != null && userInterfaceConfig.getDateSynonym().isAllowIndenticalDates() != null
				&& userInterfaceConfig.getDateSynonym().isAllowIndenticalDates().booleanValue();
	}

	public static final boolean asBoolean(String value) {
		return (value != null && value.equalsIgnoreCase(Constants.CONFIG_VALUE_YES) || Boolean.valueOf(value).booleanValue());
	}

	public static final String asGenericEntityPropertyType(String value) {
		if (value.equalsIgnoreCase(Constants.PROPERTY_TYPE_ENUM)) {
			return Constants.PROPERTY_TYPE_ENUM;
		}
		if (value.equalsIgnoreCase(Constants.PROPERTY_TYPE_BOOLEAN)) {
			return Constants.PROPERTY_TYPE_BOOLEAN;
		}
		if (value.equalsIgnoreCase(Constants.PROPERTY_TYPE_INT)) {
			return Constants.PROPERTY_TYPE_INT;
		}
		if (value.equalsIgnoreCase(Constants.PROPERTY_TYPE_INTEGERLIST)) {
			return Constants.PROPERTY_TYPE_INTEGERLIST;
		}
		if (value.equalsIgnoreCase(Constants.PROPERTY_TYPE_FLOAT)) {
			return Constants.PROPERTY_TYPE_FLOAT;
		}
		if (value.equalsIgnoreCase(Constants.PROPERTY_TYPE_LONG)) {
			return Constants.PROPERTY_TYPE_LONG;
		}
		if (value.equalsIgnoreCase(Constants.PROPERTY_TYPE_DOUBLE)) {
			return Constants.PROPERTY_TYPE_DOUBLE;
		}
		if (value.equalsIgnoreCase(Constants.PROPERTY_TYPE_DATE)) {
			return Constants.PROPERTY_TYPE_DATE;
		}
		if (value.equalsIgnoreCase(Constants.PROPERTY_TYPE_STRING)) {
			return Constants.PROPERTY_TYPE_STRING;
		}
		if (value.equalsIgnoreCase(Constants.PROPERTY_TYPE_SYMBOL)) {
			return Constants.PROPERTY_TYPE_SYMBOL;
		}
		if (value.equalsIgnoreCase(Constants.PROPERTY_TYPE_CURRENCY)) {
			return Constants.PROPERTY_TYPE_CURRENCY;
		}
		if (value.equalsIgnoreCase(Constants.PROPERTY_TYPE_PERCENT)) {
			return Constants.PROPERTY_TYPE_PERCENT;
		}
		throw new IllegalArgumentException("Invalid entity property type: " + value);
	}

	public static final boolean containsUsageType(final GuidelineTab guidelineTab, final TemplateUsageType usageType) {
		for (GuidelineTab.UsageType guidelineTabUsageType : guidelineTab.getUsageType()) {
			if (usageType.toString().equals(guidelineTabUsageType.getName())) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Gets the tab configuration that contains the specified usage type string.
	 * @param usageType the usage type, as specified rule-set in the generated rules
	 * @param userInterfaceConfig user interface config
	 * @return the tab configuration that contains <code>usageType</code> usage type, if found;
	 *         null, otherwise
	 */
	public static GuidelineTab findConfigurationForRuleSet(final TemplateUsageType usageType, final UserInterfaceConfig userInterfaceConfig) {
		for (GuidelineTab guidelineTab : userInterfaceConfig.getGuideline().getGuidelineTab()) {
			for (GuidelineTab.UsageType usageTypeName : guidelineTab.getUsageType()) {
				if (usageType.toString().equals(usageTypeName)) {
					return guidelineTab;
				}
			}
		}
		return null;
	}

	public static EntityPropertyType findPropertyType(final EntityType entityType, final String name) {
		for (final EntityProperty entityProperty : entityType.getEntityProperty()) {
			if (entityProperty.getName().equals(name)) {
				return entityProperty.getType();
			}
		}
		return null;
	}

	public static GuidelineTab findTabConfigFor(final List<GuidelineTab> guidelineTabConfigList, final TemplateUsageType usageType) {
		for (final GuidelineTab guidelineTab : guidelineTabConfigList) {
			if (containsUsageType(guidelineTab, usageType)) {
				return guidelineTab;
			}
		}
		return null;
	}

	public static CellSelectionType getCellSelectionType(boolean isExclusion, boolean isMultiSelect) {
		CellSelectionType key = null;
		if (!isExclusion && isMultiSelect) {
			key = CellSelectionType.ENUM_INCLUDE_MULTIPLE;
		}
		else if (!isExclusion && !isMultiSelect) {
			key = CellSelectionType.ENUM_INCLUDE_SINGLE;
		}
		else if (isExclusion && isMultiSelect) {
			key = CellSelectionType.ENUM_EXCLUDE_MULTIPLE;
		}
		else {
			key = CellSelectionType.ENUM_EXCLUDE_SINGLE;
		}
		return key;
	}

	public static final boolean isCanBelongToMultipleCategories(final EntityType entityType) {
		return UtilBase.asBoolean(entityType.isCanBelongToMultipleCategories(), true);
	}

	public static final boolean isCanClone(final EntityType entityType) {
		return UtilBase.asBoolean(entityType.isCanClone(), false);
	}

	public static boolean isContainedInTab(final String propertyName, final List<EntityTab.EntityPropertyTab> tabDefs) {
		if (tabDefs != null) {
			for (EntityTab.EntityPropertyTab entityPropertyTab : tabDefs) {
				if (entityPropertyTab.getEntityPropertyName().contains(propertyName)) {
					return true;
				}
			}
		}
		return false;
	}

	public static boolean isFeatureEnabled(final PowerEditorConfiguration powerEditorConfiguration, final FeatureNameType featureNameType) {
		if (powerEditorConfiguration.getFeatureConfig() == null) {
			return true;
		}
		for (final Feature feature : powerEditorConfiguration.getFeatureConfig().getFeature()) {
			if (feature.getName() == featureNameType) {
				return UtilBase.asBoolean(feature.isEnable(), true);
			}
		}
		return true;
	}

	public static boolean isPatternOn(final RuleGenerationLHS.Pattern pattern) {
		return UtilBase.asBoolean(pattern.isGenerate(), true);
	}

	public static final boolean isUniqueCategoryNames(final EntityType entityType) {
		return UtilBase.asBoolean(entityType.isUniqueCategoryNames(), true);
	}

	public static final boolean isUniqueEntityNames(final EntityType entityType) {
		return UtilBase.asBoolean(entityType.isUniqueEntityNames(), true);
	}

	public static final boolean isUseInCompatibility(final EntityType entityType) {
		return UtilBase.asBoolean(entityType.isUseInCompatibility(), false);
	}

	public static final boolean isUseInContext(final EntityType entityType) {
		return UtilBase.asBoolean(entityType.isUseInContext(), false);
	}

	public static final boolean isUseInMessageContext(final EntityType entityType) {
		return UtilBase.asBoolean(entityType.isUseInMessageContext(), false);
	}

	public static Date toDate(String dateXMLStr) {
		if (dateXMLStr == null || dateXMLStr.trim().length() < 1) {
			return null;
		}
		try {
			synchronized (XML_DATE_FORMAT) {
				return XML_DATE_FORMAT.parse(dateXMLStr);
			}
		}
		catch (Exception ex) {
			return null;
		}
	}

	public static String toDateXMLReportString(Date date) {
		if (date == null) {
			return "";
		}
		else {
			synchronized (XML_DATE_FORMAT_REPORT) {
				return XML_DATE_FORMAT_REPORT.format(date);
			}
		}
	}

	public static String toDateXMLString(Date date) {
		if (date == null) {
			return "";
		}
		else {
			synchronized (XML_DATE_FORMAT) {
				return XML_DATE_FORMAT.format(date);
			}
		}
	}

	public static Date toXMLReportDate(String dateXMLStr) {
		if (dateXMLStr == null || dateXMLStr.trim().length() < 1) {
			return null;
		}
		try {
			synchronized (XML_DATE_FORMAT_REPORT) {
				return XML_DATE_FORMAT_REPORT.parse(dateXMLStr);
			}
		}
		catch (Exception ex) {
			return null;
		}
	}

	private ConfigUtil() {
	}

}
