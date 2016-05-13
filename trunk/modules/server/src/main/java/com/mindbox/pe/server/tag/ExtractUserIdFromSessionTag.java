package com.mindbox.pe.server.tag;

import javax.servlet.jsp.JspException;

import com.mindbox.pe.server.cache.SessionManager;

public class ExtractUserIdFromSessionTag extends AbstractVarTag {

	private static final long serialVersionUID = 2010083110440000L;

	private String sessionId;

	@Override
	public int doStartTag() throws JspException {
		if (sessionId != null && SessionManager.getInstance().hasSession(sessionId)) {
			setVarObject(SessionManager.getInstance().getSession(sessionId).getUserID());
		}
		return SKIP_BODY;
	}

	public String getSessionId() {
		return sessionId;
	}

	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}
}
