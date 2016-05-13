package com.mindbox.pe.common.config;

import java.io.Serializable;

public class UserManagementConfig implements Serializable {

	private static final long serialVersionUID = 2006033110000L;

	private boolean readOnly = false;
	private boolean statusChangeable = true;
	private boolean passwordChangeable = true;
	private boolean nameChangeable = true;
	private boolean roleChangeable = true;
	private boolean allowDelete = true;
	private boolean hideCopyButton = false;

	public boolean isAllowDelete() {
		return allowDelete;
	}

	/** @since 5.9.0 */
	public boolean isHideCopyButton() {
		return hideCopyButton;
	}

	public boolean isNameChangeable() {
		return nameChangeable;
	}

	public boolean isPasswordChangeable() {
		return passwordChangeable;
	}

	public boolean isReadOnly() {
		return readOnly;
	}

	public boolean isRoleChangeable() {
		return roleChangeable;
	}

	public boolean isStatusChangeable() {
		return statusChangeable;
	}

	public void setAllowDelete(boolean allowDelete) {
		this.allowDelete = allowDelete;
	}

	/** @since 5.9.0 */
	public void setHideCopyButton(boolean hideCopyButton) {
		this.hideCopyButton = hideCopyButton;
	}

	public void setNameChangeable(boolean nameChangeable) {
		this.nameChangeable = nameChangeable;
	}

	public void setPasswordChangeable(boolean passwordChangeable) {
		this.passwordChangeable = passwordChangeable;
	}

	public void setReadOnly(boolean readOnly) {
		this.readOnly = readOnly;
	}

	public void setRoleChangeable(boolean roleChangeable) {
		this.roleChangeable = roleChangeable;
	}

	public void setStatusChangeable(boolean statusChangeable) {
		this.statusChangeable = statusChangeable;
	}
}
