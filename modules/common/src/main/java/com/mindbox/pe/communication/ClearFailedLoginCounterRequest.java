package com.mindbox.pe.communication;


public class ClearFailedLoginCounterRequest extends RequestComm<BooleanResponse> {

	private static final long serialVersionUID = 2014012113281000L;

	private final String userID;
	private final String sessionID;

	public ClearFailedLoginCounterRequest(String userID, String sessionID) {
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
