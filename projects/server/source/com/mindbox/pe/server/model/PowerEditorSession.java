package com.mindbox.pe.server.model;

import javax.servlet.http.HttpSession;

public class PowerEditorSession {
	private final String userID;
	private final HttpSession httpSession;

	public PowerEditorSession(HttpSession httpsession, String userID) {
		this.userID = userID;
		this.httpSession = httpsession;
	}

	public HttpSession getHttpSession() {
		return httpSession;
	}

	public String getSessionId() {
		return httpSession.getId();
	}

	public String getUserID() {
		return userID;
	}

	public String toString() {
		return "Session[" + httpSession.getId() + "@" + userID+"]";
	}


}