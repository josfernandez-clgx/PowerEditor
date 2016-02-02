package com.mindbox.pe.common.config;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.mindbox.pe.common.digest.TemplateUsageTypeDigest;
import com.mindbox.pe.model.EnumValue;
import com.mindbox.pe.model.GenericEntityType;
import com.mindbox.pe.model.TemplateUsageType;

/**
 * User Inteface Configuration.
 * Encapsulates information in the <code>PowerEditorConfiguration.xml</code> file.
 * @author Gene Kim
 * @author MindBox, Inc
 * @since PowerEditor 2.0.0
 */
public class UIConfiguration implements ValidatingConfig, Serializable {

	private static final long serialVersionUID = 8437158152850761981L;

	public static final String FORMAT_STR_DATE = "MM/dd/yyyy";

	public static final SimpleDateFormat FORMAT_DATE = new SimpleDateFormat(FORMAT_STR_DATE);

	public static final String FORMAT_STR_DATE_TIME_MIN = "MM/dd/yyyy HH:mm";

	public static final String FORMAT_STR_DATE_TIME_SEC = "MM/dd/yyyy HH:mm:ss";

	public static final String FORMAT_STR_YYYY_MM_DD_TIME_SEC = "yyyy-MM-dd HH:mm:ss";

	public static final SimpleDateFormat FORMAT_DATE_TIME_MIN = new SimpleDateFormat(FORMAT_STR_DATE_TIME_MIN);

	public static final SimpleDateFormat FORMAT_DATE_TIME_SEC = new SimpleDateFormat(FORMAT_STR_DATE_TIME_SEC);

	public static final SimpleDateFormat FORMAT_YYYY_MM_DD_TIME_SEC = new SimpleDateFormat(FORMAT_STR_YYYY_MM_DD_TIME_SEC);

	public static final SimpleDateFormat FORMAT_TIME_SEC = new SimpleDateFormat("HH:mm:ss");

	public static final long DAY_ADJUSTMENT = 24L * 60L * 60L * 1000L;

	private static final String DEFAULT_WINDOW_TITLE = "MindBox PowerEditor";


	private static Map<String, List<EnumValue>> enumValues = new HashMap<String, List<EnumValue>>();

	public static final void addEnumValue(String key, EnumValue val) {
		String keyInUpper = key.toUpperCase();
		synchronized (enumValues) {
			if (enumValues.containsKey(keyInUpper)) {
				enumValues.get(keyInUpper).add(val);
			}
			else {
				List<EnumValue> enumList = new ArrayList<EnumValue>();
				enumList.add(val);
				enumValues.put(keyInUpper, enumList);
			}
		}
	}

	public static final List<EnumValue> getEnumValues(String key) {
		synchronized (enumValues) {
			return enumValues.get(key.toUpperCase());
		}
	}

	public static final boolean areEnumValuesPopulated() {
		return (enumValues.size() > 0);
	}


	private final Map<GenericEntityType, EntityTabConfig> entityConfigMap;
	private final List<GuidelineTabConfig> guidelineConfigList;
	private final List<TemplateUsageType> usageConfigList;
	private boolean showTemplateID = false;
	private boolean sortEnumValues = false;
	private boolean fitGridToScreen = false;
	private String[] defaultTime;
	private int defaultExpDays = 0;
	private String clientWindowTitle = DEFAULT_WINDOW_TITLE;
	private UserDisplayNameAttribute userDisplayNameAttribute = UserDisplayNameAttribute.ID;
	private boolean allowDisableEnableUser = false;
	private UIPolicies uiPolicies;

	public UIConfiguration() {
		// There are 3 UI cofiguration elements: <Guideline>, <Entity>, <Task>
		this.entityConfigMap = new LinkedHashMap<GenericEntityType, EntityTabConfig>();
		this.guidelineConfigList = new LinkedList<GuidelineTabConfig>();
		this.usageConfigList = new LinkedList<TemplateUsageType>();
	}

	public void validate() {
		if (entityConfigMap.isEmpty())
			throw new IllegalStateException("UserInterface element has no <Entity> tag or <Entity> tag has no <EntityTab> tags.");
		if (usageConfigList.isEmpty())
			throw new IllegalStateException("UserInterface element has no <UsageTypeList> tag or it has no <UsageType> tags.");
		if (guidelineConfigList.isEmpty()) throw new IllegalStateException("<Guideline> tag contains no <GuidelineTab> tags");
	}

	public void addEntityTagConfig(EntityTabConfig entityTabConfig) {
		if (entityTabConfig != null) {
			GenericEntityType type = GenericEntityType.forName(entityTabConfig.getType());
			if (type != null) {
				entityConfigMap.put(type, entityTabConfig);
			}
		}
	}

	public void addGuidelineTabConfig(GuidelineTabConfig tabConfig) {
		if (tabConfig != null) {
			guidelineConfigList.add(tabConfig);
		}
	}

	public void addTemplateUsageType(TemplateUsageTypeDigest usageTypeDigest) {
		if (usageTypeDigest != null) {
			TemplateUsageType usageType = TemplateUsageType.createInstance(
					usageTypeDigest.getName(),
					(usageTypeDigest.getDisplayName() == null ? usageTypeDigest.getName() : usageTypeDigest.getDisplayName()),
					usageTypeDigest.getPrivilege());
			usageConfigList.add(usageType);
		}
	}

	public UIPolicies getUIPolicies() {
		return uiPolicies;
	}

	/**
	 * 
	 * @param uiPolicies
	 * @throws IllegalStateException if uiPolicies has been set already; i.e., it's not <code>null</code>
	 */
	public void setUIPolicies(UIPolicies uiPolicies) {
		if (this.uiPolicies != null) throw new IllegalStateException("uiPolicies is already set");
		this.uiPolicies = uiPolicies;
	}

	/**
	 * 
	 * @return client window title
	 * @since PowerEditor 4.3.6
	 */
	public String getClientWindowTitle() {
		return clientWindowTitle;
	}

	public void setClientWindowTitle(String value) {
		this.clientWindowTitle = value;
	}

	public boolean showGuidelineTemplateID() {
		return showTemplateID;
	}

	public void setShowTemplateID(String value) {
		this.showTemplateID = ConfigUtil.asBoolean(value);
	}

	public boolean fitGridToScreen() {
		return fitGridToScreen;
	}

	public void setFitGridToScreen(String value) {
		this.fitGridToScreen = ConfigUtil.asBoolean(value);
	}

	public boolean sortEnumValues() {
		return sortEnumValues;
	}

	public void setSortEnumValue(String value) {
		this.sortEnumValues = ConfigUtil.asBoolean(value);
	}

	public Map<GenericEntityType, EntityTabConfig> getEntityTabConfigurationMap() {
		return Collections.unmodifiableMap(entityConfigMap);
	}

	public String[] getDefaultTime() {
		return defaultTime;
	}

	public void setDefaultTimeString(String value) {
		if (value != null) {
			defaultTime = value.trim().split(":");
		}
	}

	public int getDefaultExpirationDays() {
		return defaultExpDays;
	}

	public void setDefaultExpirationDaysString(String value) {
		this.defaultExpDays = Integer.parseInt(value);
	}

	/**
	 * Gets the tab confugrations of this UI configuration.
	 * @return the tab configurations
	 */
	public GuidelineTabConfig[] getTabConfigurations() {
		return guidelineConfigList.toArray(new GuidelineTabConfig[0]);
	}

	/**
	 * Gets the tab configuration that contains the specified usage type string.
	 * @param usageType the usage type, as specified rule-set in the generated rules
	 * @return the tab configuration that contains <code>usageType</code> usage type, if found;
	 *         null, otherwise
	 */
	public GuidelineTabConfig findConfigurationForRuleSet(TemplateUsageType usageType) {
		for (Iterator<GuidelineTabConfig> iter = guidelineConfigList.iterator(); iter.hasNext();) {
			GuidelineTabConfig element = iter.next();
			if (element.containsUsageType(usageType)) {
				return element;
			}
		}
		return null;
	}

	public List<TemplateUsageType> getUsageConfigList() {
		return usageConfigList;
	}

	public UserDisplayNameAttribute getUserDisplayNameAttribute() {
		return userDisplayNameAttribute;
	}

	public void setUserDisplayNameAttributeValue(String userDisplayNameAttributeValue) {
		if (userDisplayNameAttributeValue != null) {
			this.userDisplayNameAttribute = UserDisplayNameAttribute.valueOf(userDisplayNameAttributeValue.toUpperCase());
		}
	}

	public boolean isAllowDisableEnableUser() {
		return allowDisableEnableUser;
	}

	public void setAllowDisableEnableUserFlag(String allowDisableEnableUserFlag) {
		this.allowDisableEnableUser = (allowDisableEnableUserFlag != null && Boolean.valueOf(allowDisableEnableUserFlag));
	}
}