package com.mindbox.pe.communication;



public class LoginResponse extends ResponseComm {

	private static final long serialVersionUID = 2003052312001001L;

	public static LoginResponse createSuccessInstance(String sessionID) {
		return new LoginResponse(sessionID, null, true, false);
	}

	public static LoginResponse createSuccessWithPasswordExpiryNoticeInstance(String sessionID, int daysUntilPasswordExpiration) {
		LoginResponse result = new LoginResponse(sessionID, null, true, false);
		result.notifyPasswordExpiration = true;
		result.daysUntilPasswordExpiration = daysUntilPasswordExpiration;
		return result;
	}

	public static LoginResponse createFailureInstance(String failureMsg) {
		return new LoginResponse(null, failureMsg, false, false);
	}
	
	public static LoginResponse createPasswordNeedsResetInstance(String resetMsg) {
		return new LoginResponse(null, resetMsg, true, true);
	}

	private final String sessionID;
	private final boolean authenticated;
	private final String loginFailureMsg;
	private final boolean passwordNeedsReset;
	private boolean notifyPasswordExpiration = false;
	private int daysUntilPasswordExpiration = Integer.MAX_VALUE;

	private LoginResponse(String sessionID, String failureMsg, boolean authenticated, boolean passwordNeedsReset) {
		this.sessionID = sessionID;
		this.authenticated = authenticated;
		this.loginFailureMsg = failureMsg;
		this.passwordNeedsReset = passwordNeedsReset;
	}

	public String getSessionID() {
		return sessionID;
	}

	public boolean isAuthenticated() {
		return authenticated;
	}
	
	public boolean isPasswordNeedsReset() {
		return passwordNeedsReset;
	}

	public String toString() {
		return "LoginResponse[auth?=" + authenticated + ",session=" + sessionID + " ,pwdNeedReset?="+passwordNeedsReset+ "]";
	}

	public String getLoginFailureMsg() {
		return loginFailureMsg;
	}

	public boolean getNotifyPasswordExpiration() {
		return notifyPasswordExpiration;
	}
	
	public int getDaysUntilPasswordExpiration() {
		return daysUntilPasswordExpiration;
	}
}
