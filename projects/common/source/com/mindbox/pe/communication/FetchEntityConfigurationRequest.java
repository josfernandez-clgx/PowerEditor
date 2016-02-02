package com.mindbox.pe.communication;


public class FetchEntityConfigurationRequest extends RequestComm<FetchEntityConfigurationResponse> {

	private static final long serialVersionUID = 2006061390000L;
	
	private final String sessionID;

	public FetchEntityConfigurationRequest(String sessionID) {
		this.sessionID = sessionID;
	}

	public String toString() {
		return "FetchEntityTypeDefinitionRequest[" + sessionID + "]";
	}

	public String getSessionID() {
		return sessionID;
	}
}
