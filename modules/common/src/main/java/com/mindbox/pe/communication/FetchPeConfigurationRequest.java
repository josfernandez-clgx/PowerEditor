package com.mindbox.pe.communication;


public class FetchPeConfigurationRequest extends RequestComm<FetchPeConfigurationResponse> {

	private static final long serialVersionUID = 2006061390000L;

	private final String sessionID;

	public FetchPeConfigurationRequest(String sessionID) {
		this.sessionID = sessionID;
	}

	public String toString() {
		return "FetchPeConfigurationRequest[" + sessionID + "]";
	}

	public String getSessionID() {
		return sessionID;
	}
}
