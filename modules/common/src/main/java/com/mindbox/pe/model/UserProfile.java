package com.mindbox.pe.model;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.mindbox.pe.common.config.MessageConfiguration;
import com.mindbox.pe.model.admin.Privilege;

/**
 * User profile container.
 * User profile holds a user specific PE configuration objects for clients.
 * @author Geneho Kim
 */
public class UserProfile implements Serializable {

	private static final long serialVersionUID = 2003093070000L;

	private final String userID;
	private final Set<Privilege> privilegeSet;
	/** @since PowerEditor 4.2.0 */
	private final Map<Object, MessageConfiguration> defaultCondMsgs;
	/** @since PowerEditor 5.1.0 */
	private final int historyLookBack;
	/** @since PowerEditor 5.1.0 */
	private final int maxFailedLoginAttempts;
	/** @since PowerEditor 5.1.0 */
	private final int passwordExpirationDays;

	private final String[] defaultTime;
	private final int defaultExpirationDays;
	private final boolean showDeployAsOfDate;

	public UserProfile(final String userID, final Set<Privilege> privileges, final Map<Object, MessageConfiguration> defaultCondMsgs, final int historyLookBack,
			final int maxAttempts, final int passwordExpirationDays, final String[] defaultTime, final int defaultExpirationDays, final boolean showDeployAsOfDate) {
		this.userID = userID;
		this.privilegeSet = (privileges == null ? new HashSet<Privilege>() : privileges);
		this.defaultCondMsgs = defaultCondMsgs;
		this.historyLookBack = historyLookBack;
		this.maxFailedLoginAttempts = maxAttempts;
		this.passwordExpirationDays = passwordExpirationDays;
		this.defaultTime = defaultTime;
		this.defaultExpirationDays = defaultExpirationDays;
		this.showDeployAsOfDate = showDeployAsOfDate;
	}

	/**
	 * @param usageType usageType
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

	/**
	 * @since PowerEditor 4.2.0.
	 * @return default condition message map
	 */
	public Map<Object, MessageConfiguration> getDefaultCondMsgs() {
		return defaultCondMsgs;
	}

	/**
	 * @since PowerEditor 4.2.0
	 * @return the default expiration days
	 */
	public int getDefaultExpirationDays() {
		return defaultExpirationDays;
	}

	/**
	 * @since PowerEditor 4.2.0
	 * @return default hour set in the xml file.
	 */
	public String getDefaultHour() {
		return defaultTime == null ? null : defaultTime[0];
	}

	/**
	 * @since PowerEditor 4.2.0
	 * @return default minute set in the xml file.
	 */
	public String getDefaultMinute() {
		return defaultTime == null ? null : defaultTime[1];
	}

	/**
	 * 
	 * @return the default time strings
	 * @since PowerEditor 4.2.0
	 */
	public String[] getDefaultTime() {
		return defaultTime;
	}

	public int getHistoryLookBack() {
		return historyLookBack;
	}

	public int getMaxFailedLoginAttempts() {
		return maxFailedLoginAttempts;
	}

	public int getPasswordExpirationDays() {
		return passwordExpirationDays;
	}

	public Set<Privilege> getPrivileges() {
		return privilegeSet;
	}

	public String getUserID() {
		return userID;
	}

	public boolean isShowDeployAsOfDate() {
		return showDeployAsOfDate;
	}

	@Override
	public String toString() {
		return "UserProfile[" + userID + "]";
	}
}
