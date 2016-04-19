package com.mindbox.pe.server.enumsrc;

/**
 * Indicates enum source configuration has non-recoverable error.
 * 
 */
public final class EnumSourceConfigException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 9150295821149182441L;

	public EnumSourceConfigException(String message, Throwable cause) {
		super(message, cause);
	}

	public EnumSourceConfigException(String message) {
		super(message);
	}

	public EnumSourceConfigException(Throwable cause) {
		super(cause);
	}

}
