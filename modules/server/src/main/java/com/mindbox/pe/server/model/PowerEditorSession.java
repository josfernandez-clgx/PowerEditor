package com.mindbox.pe.server.model;

import javax.servlet.http.HttpSession;

public class PowerEditorSession {

	private final String userID;
	private final HttpSession httpSession;
	private long lastAccessedTime = 0;

	public PowerEditorSession(HttpSession httpsession, String userID) {
		this.userID = userID;
		this.httpSession = httpsession;
		this.lastAccessedTime = System.currentTimeMillis();
	}

	public HttpSession getHttpSession() {
		return httpSession;
	}

	public synchronized long getLastAccessedTime() {
		return lastAccessedTime;
	}

	public String getSessionId() {
		return httpSession.getId();
	}

	public String getUserID() {
		return userID;
	}

	public synchronized void resetLastAccessedTime() {
		lastAccessedTime = System.currentTimeMillis();
	}

	@Override
	public String toString() {
		return "Session[" + httpSession.getId() + "@" + userID + "]";
	}

}