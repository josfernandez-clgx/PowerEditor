package com.mindbox.pe.communication;

import com.mindbox.pe.model.UserProfile;

public class FetchUserProfileResponse extends ResponseComm {

	private static final long serialVersionUID = 200407261331000L;

	private final String sessionID;

	private final UserProfile userProfile;

	public FetchUserProfileResponse(String sessionID, UserProfile userProfile) {
		this.sessionID = sessionID;
		this.userProfile = userProfile;
	}

	public String getSessionID() {
		return sessionID;
	}

	public String toString() {
		return "FetchUserProfileResponse[session=" + sessionID + "]";
	}

	public UserProfile getUserProfile() {
		return userProfile;
	}

}