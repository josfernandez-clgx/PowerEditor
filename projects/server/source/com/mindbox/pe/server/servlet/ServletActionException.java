/*
 * Created on May 26, 2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package com.mindbox.pe.server.servlet;

import com.mindbox.pe.server.ServerException;

/**
 * Indicates an error occurred during a servlet operation.
 * @since PowerEditor 1.0
 */
public class ServletActionException extends ServerException {

	private static final long serialVersionUID = 8998851066536991732L;

	private final String resourceKey;
	private final Object[] params;

	/**
	 * 
	 * @param resourceKey
	 * @param msg
	 */
	public ServletActionException(String resourceKey, String msg) {
		super(msg);
		this.resourceKey = resourceKey;
		this.params = new Object[] { msg};
	}

	/**
	 * 
	 * @param resourceKey message key
	 * @param params parameters to the message; all elements must implement <code>java.io.Serializable</code>; can be <code>null</code>
	 */
	public ServletActionException(String resourceKey, Object[] params) {
		super(resourceKey);
		this.resourceKey = resourceKey;
		this.params = params;
	}

	public String getResourceKey() {
		return resourceKey;
	}

	public Object[] getResourceParams() {
		return params;
	}
}