package com.mindbox.pe.communication;


public class GetLastDeployErrorRequest extends SessionRequest<StringResponse> {
	
	private static final long serialVersionUID = 200402041420000L;

	private final int runID;
	public GetLastDeployErrorRequest(String s, String s1, int runID) {
		super(s, s1);
		this.runID = runID;
	}
	
	public int getRunID() {
		return runID;
	}

	public String toString() {
		return "GetLastDeployErrorRequest for " + getUserID();
	}

}
