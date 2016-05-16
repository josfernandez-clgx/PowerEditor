package com.mindbox.pe.server.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import com.mindbox.pe.model.Auditable;
import com.mindbox.pe.model.admin.Role;
import com.mindbox.pe.model.admin.UserData;
import com.mindbox.pe.model.admin.UserPassword;
import com.mindbox.pe.server.config.ConfigurationManager;

/**
 * User.
 * A user can have access to one or more channels.
 * @since PowerEditor 1.0
 */
public final class User implements Serializable, Auditable {

	private static final long serialVersionUID = 2003052312327001L;

	public static User valueOf(UserData userdata) {
		User user = new User(
				userdata.getUserID(),
				userdata.getName(),
				userdata.getStatus(),
				userdata.getPasswordChangeRequired(),
				userdata.getFailedLoginCounter(),
				userdata.getLastFailedAttemptDate(),
				userdata.getPasswordHistory());
		user.setRoles(userdata.getRoles());
		user.setDisabled(userdata.isDisabled());
		return user;
	}

	private String userId;
	private String name;
	private String status;
	private boolean passwordChangeRequired;
	private Date lastFailedAttemptDate;
	private int failedLoginCounter;
	private final List<Role> roles;
	private List<UserPassword> passwordHistory;
	private boolean disabled = false;

	public User(String userID, String name, String status, boolean passwordChangeRequired, int failedLoginCounter, Date lastFailedAttemptDate, List<UserPassword> passwordHistory) {
		this.userId = userID;
		this.name = name;
		this.status = status;
		this.roles = new ArrayList<Role>();
		this.passwordChangeRequired = passwordChangeRequired;
		this.failedLoginCounter = failedLoginCounter;
		this.lastFailedAttemptDate = lastFailedAttemptDate;
		this.passwordHistory = new ArrayList<UserPassword>();
		if (passwordHistory != null) {
			this.passwordHistory.addAll(passwordHistory);
		}
	}

	/**
	 * This constructor is called only during import
	 * @param userID userID
	 * @param name name
	 * @param status status
	 * @param password password
	 * @param passwordChangeRequired passwordChangeRequired
	 * @param failedLoginCounter failedLoginCounter
	 * @param lastFailedAttemptDate lastFailedAttemptDate
	 */
	public User(String userID, String name, String status, String password, boolean passwordChangeRequired, int failedLoginCounter, Date lastFailedAttemptDate) {
		this(userID, name, status, passwordChangeRequired, failedLoginCounter, lastFailedAttemptDate, Collections.singletonList(new UserPassword(password, new Date())));
	}

	private User(User source) {
		this(source.userId, source.name, source.status, source.passwordChangeRequired, source.failedLoginCounter, source.lastFailedAttemptDate, source.passwordHistory);
		this.roles.addAll(source.roles);
		this.disabled = source.disabled;
	}

	public void add(Role role) {
		if (!roles.contains(role)) {
			roles.add(role);
		}
	}

	public UserData asUserData() {
		UserData userData = new UserData(userId, name, status, roles, passwordChangeRequired, failedLoginCounter, lastFailedAttemptDate, passwordHistory);
		userData.setDisabled(this.disabled);
		return userData;
	}

	@Override
	public Auditable deepCopy() {
		return new User(this);
	}

	public boolean equals(User user) {
		return user.getUserID().equals(userId);
	}

	@Override
	public String getAuditDescription() {
		return "user '" + userId + "'";
	}

	@Override
	public String getAuditName() {
		return userId;
	}

	public String getCurrentPassword() {
		return passwordHistory != null && passwordHistory.size() > 0 ? passwordHistory.get(0).getPassword() : null;
	}

	public Date getCurrentPasswordChangeDate() {
		return passwordHistory != null && passwordHistory.size() > 0 ? passwordHistory.get(0).getPasswordChangeDate() : null;
	}

	public int getFailedLoginCounter() {
		return failedLoginCounter;
	}

	@Override
	public int getID() {
		// Just so User object implements Persistent
		return userId.hashCode();
	}

	public Date getLastFailedAttemptDate() {
		return lastFailedAttemptDate;
	}

	public String getName() {
		return name;
	}

	public boolean getPasswordChangeRequired() {
		return passwordChangeRequired;
	}

	public List<UserPassword> getPasswordHistory() {
		return passwordHistory;
	}

	public String[] getRecentPasswords() {
		String[] passwords = new String[passwordHistory.size()];
		for (int i = 0; i < passwordHistory.size(); i++) {
			passwords[i] = passwordHistory.get(i).getPassword();
		}
		return passwords;
	}

	public List<Role> getRoles() {
		return Collections.unmodifiableList(roles);
	}

	public String getStatus() {
		return status;
	}

	public String getUserID() {
		return userId;
	}

	@Override
	public int hashCode() {
		return userId.hashCode();
	}

	public boolean hasPrivilege(String privilegeName) {
		for (Role role : roles) {
			if (role.hasPrivilege(privilegeName)) {
				return true;
			}
		}
		return false;
	}

	public boolean isDisabled() {
		return disabled;
	}

	public void removeRole(Role role) {
		if (roles.contains(role)) {
			roles.remove(role);
		}
	}

	public void setCurrentPassword(String newPassword, Date date) {
		int lookback = ConfigurationManager.getInstance().getUserPasswordPoliciesConfigHelper().getLookback();
		UserPassword userPassword = new UserPassword(newPassword, date);
		passwordHistory.add(0, userPassword);
		while (passwordHistory.size() > lookback + 1) {
			passwordHistory.remove(lookback + 1);
		}
	}

	public void setDisabled(boolean disabled) {
		this.disabled = disabled;
	}

	/**
	 * Should always be reset 0, when either there is successful login or when admin changes password
	 * @author vineet khosla
	 * @since PowerEditor 5.1
	 * @param failedLoginCounter failedLoginCounter
	 */
	public void setFailedLoginCounter(int failedLoginCounter) {
		this.failedLoginCounter = failedLoginCounter;
	}

	public void setLastFailedAttemptDate(Date lastFailedAttemptDate) {
		this.lastFailedAttemptDate = lastFailedAttemptDate;
	}

	public void setName(String s) {
		this.name = s;
	}

	public void setPassword(String newPassword) {
		setCurrentPassword(newPassword, new Date());
	}

	public void setPasswordChangeRequired(boolean passwordChangeRequired) {
		this.passwordChangeRequired = passwordChangeRequired;
	}

	public void setPasswordHistory(List<UserPassword> list) {
		passwordHistory.clear();
		if (list != null) {
			passwordHistory.addAll(list);
		}
	}

	public void setRoles(List<Role> list) {
		roles.clear();
		roles.addAll(list);
	}

	public void setStatus(String s) {
		this.status = s;
	}

	public void setUserID(String s) {
		userId = s;
	}

	@Override
	public String toString() {
		return "User[" + userId + "]";
	}
}