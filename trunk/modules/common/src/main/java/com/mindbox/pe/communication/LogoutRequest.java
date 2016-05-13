package com.mindbox.pe.communication;


public class LogoutRequest extends SessionRequest<AbstractSimpleResponse> {
	private static final long serialVersionUID = 2003052312001002L;

	public String toString() {
		return "LogoutRequest for " + getUserID();
	}

	public LogoutRequest(String s, String s1) {
		super(s, s1);
	}
}
