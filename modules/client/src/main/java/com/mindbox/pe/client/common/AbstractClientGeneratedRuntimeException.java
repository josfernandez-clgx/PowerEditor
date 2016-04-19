package com.mindbox.pe.client.common;

import com.mindbox.pe.client.ClientUtil;
import com.mindbox.pe.client.MainApplication;

/**
 * Runtime exception that was originated from the client, as opposed to server.
 *
 * Instantiaing an instance of this requires propery initialization of {@link ClientUtil}.
 *
 * @see MainApplication#handleRuntimeException(Exception)
 */
public class AbstractClientGeneratedRuntimeException extends RuntimeException {
	/**
	 * 
	 */
	private static final long serialVersionUID = -3951228734910107454L;

	/**
	 * Creates a new instance with the specified message key
	 * @param messageKey message key
	 * @throws NullPointerException if <code>messageKey</code> is <code>null</code>
	 */
	protected AbstractClientGeneratedRuntimeException(String messageKey) {
		super(ClientUtil.getInstance().getMessage(messageKey));
		if (messageKey == null) throw new NullPointerException();
	}

	/**
	 * Creates a new instance with the specified message key and message parameters.
	 * @param messageKey message key
	 * @param params message parameters
	 * @throws NullPointerException if <code>messageKey</code> or <code>params</code> is <code>null</code>
	 */
	protected AbstractClientGeneratedRuntimeException(String messageKey, Object... params) {
		super(ClientUtil.getInstance().getMessage(messageKey, params));
		if (messageKey == null) throw new NullPointerException("messageKey cannot be null");
		if (params == null) throw new NullPointerException("params cannot be null");
	}
}
