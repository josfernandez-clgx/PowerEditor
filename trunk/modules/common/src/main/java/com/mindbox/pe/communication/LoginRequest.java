package com.mindbox.pe.communication;



public class LoginRequest extends RequestComm<LoginResponse> {

	private static final long serialVersionUID = 2003052312001009L;
	
	private final String loginUserId;
	private final String loginPasswordAsClearText;

	public LoginRequest(String userID, String pwd) {
		this.loginUserId = userID;
		this.loginPasswordAsClearText = pwd;
	}

	public String toString() {
		return "LoginRequest[" + loginUserId + "]";
	}

	public String getLoginUserId() {
		return loginUserId;
	}

	public String getLoginPassword() {
		return loginPasswordAsClearText;
	}
	
}
