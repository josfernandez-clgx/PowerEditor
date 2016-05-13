package com.mindbox.pe.communication;


/**
 * Indicates an operation has failed.
 * @since PowerEditor 1.0
 */
public class OperationFailedException extends ServerException {

	private static final long serialVersionUID = -1613798528124785595L;

	/**
	 * @param msgKey the detailed message key
	 * @param params message parameters
	 */
	public OperationFailedException(String msgKey, Object[] params) {
		super(msgKey, params);
	}
	
	public OperationFailedException(ErrorResponse cause) {
		super(cause);
	}

}