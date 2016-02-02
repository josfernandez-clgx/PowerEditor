package com.mindbox.pe.communication;

public abstract class SessionRequest<T extends ResponseComm> extends RequestComm<T> {

	private static final long serialVersionUID = 2003052312009999L;

	/**
	 * Create entity action type. Value is 0.
	 */
	public static final int ACTION_TYPE_NEW = 0;
	
	/**
	 * Update entity action type. Value is 1.
	 */
	public static final int ACTION_TYPE_UPDATE = 1;
	
	/**
	 * Delete entity action type. Value is -1.
	 */
	public static final int ACTION_TYPE_DELETE = -1;
	
	public static final int ACTION_TYPE_LOCK = 9;
	
	public static final int ACTION_TYPE_UNLOCK = 8;
	
	public static final int ACTION_CHECK_HAS_RULE = 11;
	
	/**
	 * Constructs a new Action request instance with the specified user id and session id.
	 * @param userID the user id
	 * @param sessionID the session id
	 */
	protected SessionRequest(String userID, String sessionID) {
		mUserId = userID;
		mSessionId = sessionID;
	}


	public void setSessionID(String s) {
		mSessionId = s;
	}

	public String getSessionID() {
		return mSessionId;
	}

	public String toString() {
		return "ActionRequest[" + mUserId + "/" + mSessionId +"]";
	}

	public void setUserID(String s) {
		mUserId = s;
	}

	public String getUserID() {
		return mUserId;
	}

	private String mUserId;
	private String mSessionId;
}
