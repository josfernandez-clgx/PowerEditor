package com.mindbox.pe.communication;


public class EnableUserRequest extends RequestComm<StringResponse> {

	private static final long serialVersionUID = 2012042413281000L;

	private final String userID;
	private final String sessionID;

	public EnableUserRequest(String userID, String sessionID) {
		this.userID = userID;
		this.sessionID = sessionID;
	}

	public String toString() {
		return "EnableUserRequest[" + userID + "]";
	}

	public String getUserID() {
		return userID;
	}

	public String getSessionID() {
		return sessionID;
	}

}
