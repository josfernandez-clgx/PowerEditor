package com.mindbox.pe.communication;

public class FetchNextRuleIDRequest extends SessionRequest<LongResponse> {

	private static final long serialVersionUID = 290670544346609824L;

	public FetchNextRuleIDRequest(String userID, String sessionID) {
		super(userID, sessionID);
	}

}
