package com.mindbox.pe.communication;


/**
 * Indicate user does not have authority to perform the requested operation.
 * @author Gene Kim
 * @author MindBox
 * @since PowerEditor 4.2.0
 */
public class AuthorizationFailedException extends ServerException {

	private static final long serialVersionUID = 7846808982871520287L;

	public AuthorizationFailedException(ErrorResponse cause) {
		super(cause);
	}

}