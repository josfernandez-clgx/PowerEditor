package com.mindbox.pe.communication;


public class LoggedUserProfileRequest extends RequestComm<FetchUserProfileResponse> {

	private static final long serialVersionUID = 2003101012001000L;
	
	private final String uid;
	private final String sid;

	public LoggedUserProfileRequest(String userID, String sessionID) {
		this.uid = userID;
		this.sid = sessionID;
	}

	public String toString() {
		return "LoggedUserProfileRequest[" + uid + ":" + sid + "]";
	}

	public String getUserID() {
		return uid;
	}

	public String getSessionID() {
		return sid;
	}
	
}
