package com.mindbox.pe.communication;


public class FetchUserProfileRequest extends RequestComm<FetchUserProfileResponse> {

	private static final long serialVersionUID = 2004072613281000L;
	
	private final String sessionID;

	public FetchUserProfileRequest(String sessionID) {
		this.sessionID = sessionID;
	}

	public String toString() {
		return "FetchUserProfileRequest[" + sessionID + "]";
	}

	public String getSessionID() {
		return sessionID;
	}
	
}
