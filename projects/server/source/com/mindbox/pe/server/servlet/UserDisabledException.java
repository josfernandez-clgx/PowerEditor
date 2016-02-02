package com.mindbox.pe.server.servlet;

public class UserDisabledException extends ServletActionException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6998517616384988584L;

	public UserDisabledException(String resourceKey, Object[] params) {
		super(resourceKey, params);
	}

	public UserDisabledException(String resourceKey, String msg) {
		super(resourceKey, msg);
	}

}
