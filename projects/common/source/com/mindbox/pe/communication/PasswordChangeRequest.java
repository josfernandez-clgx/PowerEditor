package com.mindbox.pe.communication;

import com.mindbox.pe.common.PasswordOneWayHashUtil;

/**
 * @author vineet khosla
 * @since PowerEditor 5.1
 */
public class PasswordChangeRequest extends RequestComm<PasswordChangeResponse> {

	private static final long serialVersionUID = 2003052312001009L;

	private final String newPasswordAsClearText;
	private final String newPasswordAsOneWayHash;
	private final String confirmNewPasswordAsClearText;
	private final String loginUserId;
	private final String loginPasswordAsClearText;

	public PasswordChangeRequest(String userID, String oldPassword, String newPassword, String confirmNewPassword) {
		this.loginPasswordAsClearText = oldPassword;
		this.loginUserId = userID;
		this.newPasswordAsClearText = newPassword;
		this.confirmNewPasswordAsClearText = confirmNewPassword;
		this.newPasswordAsOneWayHash = PasswordOneWayHashUtil.convertToOneWayHash(newPassword, PasswordOneWayHashUtil.HASH_ALGORITHM_MD5);
	}

	public String getLoginUserId() {
		return loginUserId;
	}

	public String getLoginPassword() {
		return loginPasswordAsClearText;
	}

	public String toString() {
		return "PasswordChangeRequest[" + getLoginUserId() + "]";
	}

	public String getConfirmNewPasswordAsClearText() {
		return confirmNewPasswordAsClearText;
	}

	public String getNewPasswordAsClearText() {
		return newPasswordAsClearText;
	}

	public String getNewPasswordAsOneWayHash() {
		return newPasswordAsOneWayHash;
	}
}
