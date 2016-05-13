package com.mindbox.pe.communication;

/**
 * Base exception for exception caused by the server.
 * Exception of this type should be reported to the user.
 * @author Gene Kim
 * @author MindBox, Inc
 * @since PowerEditor 1.0
 */
public abstract class ServerException extends Exception {
	/**
	 * 
	 */
	private static final long serialVersionUID = 3656090025287504162L;

	private final ErrorResponse cause;
	private final Object[] params;

	protected ServerException(ErrorResponse cause) {
		super();
		this.cause = cause;
		this.params = null;
	}

	protected ServerException(String messageKey, Object[] params) {
		super(messageKey);
		this.params = params;
		this.cause = null;
	}

	public final ErrorResponse getErrorResponse() {
		return cause;
	}

	public final String getErrorMessageKey() {
		return super.getMessage();
	}

	public final Object[] getErrorParams() {
		return params;
	}
}