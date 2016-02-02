package com.mindbox.pe.model;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.mindbox.pe.common.config.EntityTabConfig;
import com.mindbox.pe.common.config.FeatureConfiguration;
import com.mindbox.pe.common.config.GuidelineTabConfig;
import com.mindbox.pe.common.config.MessageConfiguration;
import com.mindbox.pe.common.config.UIConfiguration;
import com.mindbox.pe.common.config.UIPolicies;
import com.mindbox.pe.common.config.UserManagementConfig;
import com.mindbox.pe.model.admin.Privilege;

/**
 * User profile container.
 * User profile holds a user specific PE configuration objects for clients.
 * @author Geneho Kim
 * @since PowerEditor
 */
public class UserProfile implements Serializable {

	private static final long serialVersionUID = 2003093070000L;

	private final String userID;
	private final Set<Privilege> privilegeSet;
	/** @since PowerEditor 4.0.1 */
	private final FeatureConfiguration featureConfiguration;
	/** @since PowerEditor 4.2.0 */
	private final Map<Object, MessageConfiguration> defaultCondMsgs;
	private final UserManagementConfig userManagementConfig;
	/** @since PowerEditor 5.1.0 */
	private final int historyLookBack;
	/** @since PowerEditor 5.1.0 */
	private final int maxFailedLoginAttempts;
	/** @since PowerEditor 5.1.0 */
	private final int passwordExpirationDays;
	private final UIConfiguration uiConfiguration;

	public UserProfile(String userID, Set<Privilege> privileges, FeatureConfiguration featureConfiguration,
			UIConfiguration uiConfiguration, Map<Object, MessageConfiguration> defaultCondMsgs, UserManagementConfig userManagementConfig,
			int historyLookBack, int maxAttempts, int expirationDays) {
		this.userID = userID;
		this.privilegeSet = (privileges == null ? new HashSet<Privilege>() : privileges);
		this.featureConfiguration = featureConfiguration;
		this.uiConfiguration = uiConfiguration;
		this.defaultCondMsgs = defaultCondMsgs;
		this.userManagementConfig = userManagementConfig;
		this.historyLookBack = historyLookBack;
		this.maxFailedLoginAttempts = maxAttempts;
		this.passwordExpirationDays = expirationDays;
	}

	public UIConfiguration getUiConfiguration() {
		return uiConfiguration;
	}
	
	/**
	 * Gets userManagementConfig.
	 * @since PowerEditor 4.5.0
	 */
	public UserManagementConfig getUserManagementConfig() {
		return userManagementConfig;
	}

	/**
	 * 
	 * @return the default time strings
	 * @since PowerEditor 4.2.0
	 */
	public String[] getDefaultTime() {
		return uiConfiguration.getDefaultTime();
	}

	/**
	 * @since PowerEditor 4.2.0
	 * @return default hour set in the xml file.
	 */
	public String getDefaultHour() {
		if ((uiConfiguration.getDefaultTime() != null) && (uiConfiguration.getDefaultTime().length > 0)) {
			return uiConfiguration.getDefaultTime()[0];
		}

		return null;
	}

	/**
	 * @since PowerEditor 4.2.0
	 * @return default minute set in the xml file.
	 */
	public String getDefaultMinute() {
		if ((uiConfiguration.getDefaultTime() != null) && (uiConfiguration.getDefaultTime().length > 1)) {
			return uiConfiguration.getDefaultTime()[1];
		}

		return null;
	}

	/**
	 * @since PowerEditor 4.2.0
	 * @return the default expiration days
	 */
	public int getDefaultExpirationDays() {
		return uiConfiguration.getDefaultExpirationDays();
	}

	/**
	 * 
	 * @return feature configuration object
	 * @since PowerEditor 4.0.1
	 */
	public FeatureConfiguration getFeatureConfiguration() {
		return featureConfiguration;
	}

	public boolean showGuidelineTemplateID() {
		return uiConfiguration.showGuidelineTemplateID();
	}

	public Map<GenericEntityType, EntityTabConfig> getEntityTabConfigMap() {
		return uiConfiguration.getEntityTabConfigurationMap();
	}

	public GuidelineTabConfig[] getGuidelineConfigurations() {
		return uiConfiguration.getTabConfigurations();
	}

	public UIPolicies getUIPolicies() {
		return uiConfiguration.getUIPolicies();
	}

	public String getUserID() {
		return userID;
	}

	public Set<Privilege> getPrivileges() {
		return privilegeSet;
	}

	/**
	 * @since PowerEditor 4.2.0.
	 * @return default condition message map
	 */
	public Map<Object,MessageConfiguration> getDefaultCondMsgs() {
		return defaultCondMsgs;
	}

	/**
	 * @param usageType
	 * @since PowerEditor 4.2.0.
	 * @return default condition configuration for <code>usageType</code>
	 */
	public MessageConfiguration getDefaultCondMsg(TemplateUsageType usageType) {
		MessageConfiguration retVal = (MessageConfiguration) defaultCondMsgs.get(usageType);
		if (retVal == null) {
			retVal = (MessageConfiguration) defaultCondMsgs.get("defaultRuleGen");
		}
		return retVal;
	}

	public String toString() {
		return "UserProfile[" + userID + "]";
	}

	public int getHistoryLookBack() {
		return historyLookBack;
	}

	public int getPasswordExpirationDays() {
		return passwordExpirationDays;
	}

	public int getMaxFailedLoginAttempts() {
		return maxFailedLoginAttempts;
	}
}
