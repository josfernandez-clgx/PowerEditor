package com.mindbox.pe.communication;

public class FetchApplicableEnumerationValuesRequest extends FetchAllEnumerationValuesRequest {

	private static final long serialVersionUID = 2008052810000002L;
	
	private final String selectorValue;

	public FetchApplicableEnumerationValuesRequest(String sourceName, String selectorValue, String userID, String sessionID) {
		super(sourceName, userID, sessionID);
		this.selectorValue = selectorValue;
	}

	public String getSelectorValue() {
		return selectorValue;
	}

}
