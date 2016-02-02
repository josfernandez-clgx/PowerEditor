package com.mindbox.pe.model.admin;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.mindbox.pe.model.Associable;
import com.mindbox.pe.model.Constants;

public class UserData implements Associable, Serializable, Comparable<UserData> {

	private static final long serialVersionUID = 2003061613438009L;

	private String userID;
	private String name;
	private List<Role> roles;
	private String status;
	private boolean passwordChangeRequired;
	private int failedLoginCounter;
	private List<UserPassword> passwordHistory;
	private boolean disabled = false;

	public UserData(String userID, String name, String status, List<Role> roles, boolean passwordChangeRequired, int failedLoginCounter,
			List<UserPassword> passwordHistory) {
		setName(name);
		setRoles(roles);
		setUserID(userID);
		setStatus(status);
		setPasswordChangeRequired(passwordChangeRequired);
		setFailedLoginCounter(failedLoginCounter);
		this.passwordHistory = new ArrayList<UserPassword>();
		if (passwordHistory != null) {
			this.passwordHistory.addAll(passwordHistory);
		}
	}

	public UserData(UserData source) {
		this.name = source.name;
		this.status = Constants.ACTIVE_STATUS;
		this.userID = source.userID + " -- Clone";
		this.failedLoginCounter = source.failedLoginCounter;
		this.disabled = source.disabled;
		// TT 2119
		this.roles = new ArrayList<Role>();
		List<Role> orignalUserRoleList = source.getRoles();
		if (orignalUserRoleList != null) {
			for (Role role : orignalUserRoleList) {
				this.roles.add(role);
			}
		}

		// TT 2119
		this.passwordHistory = new ArrayList<UserPassword>();
		List<UserPassword> orignalUserPwdHistoryList = source.getPasswordHistory();
		if (orignalUserPwdHistoryList != null) {
			for (UserPassword up : orignalUserPwdHistoryList) {
				this.passwordHistory.add(up);
			}
		}
	}

	public void setRoles(List<Role> list) {
		roles = list;
	}

	public List<Role> getRoles() {
		return roles;
	}

	public String toString() {
		return "UserId:" + getName();
	}

	public void setUserID(String s) {
		userID = s;
	}

	public String getUserID() {
		return userID;
	}

	public void setPasswordChangeRequired(boolean passwordChangeRequired) {
		this.passwordChangeRequired = passwordChangeRequired;
	}

	public boolean getPasswordChangeRequired() {
		return passwordChangeRequired;
	}


	public int compareTo(UserData userdata) throws ClassCastException {
		if (this == userdata) return 0;
		return getName().compareTo(userdata.getName());
	}

	public boolean equals(UserData userdata) {
		return userdata.getUserID().equals(getUserID());
	}

	public void setStatus(String s) {
		status = s;
	}

	public void setName(String s) {
		if (s == null) throw new NullPointerException("Name of user cannot be null");
		name = s;
	}

	public String getName() {
		return name;
	}

	public String getStatus() {
		return status;
	}

	public int getFailedLoginCounter() {
		return failedLoginCounter;
	}

	public void setFailedLoginCounter(int failedLoginCounter) {
		this.failedLoginCounter = failedLoginCounter;
	}

	public int getID() {
		return name.hashCode();
	}

	public List<UserPassword> getPasswordHistory() {
		return passwordHistory;
	}

	public void setPasswordHistory(List<UserPassword> recentPasswords) {
		this.passwordHistory = recentPasswords;
	}

	public String getCurrentPassword() {
		return passwordHistory != null && passwordHistory.size() > 0 ? passwordHistory.get(0).getPassword() : null;
	}

	public void setCurrentPassword(String newPassword, int lookback) {
		UserPassword userPassword = new UserPassword(newPassword, new Date());
		passwordHistory.add(0, userPassword);
		while (passwordHistory.size() > lookback + 1) {
			passwordHistory.remove(lookback + 1);
		}
	}

	public boolean isDisabled() {
		return disabled;
	}

	public void setDisabled(boolean disabled) {
		this.disabled = disabled;
	}
}
