package com.mindbox.pe.communication;


/**
 * 
 * @author Gene Kim
 * @author MindBox, Inc
 * @since PowerEditor 1.0
 */
public class AuthenticaionFailedException extends ServerException {

	private static final long serialVersionUID = 7855263951203256956L;

	public AuthenticaionFailedException(ErrorResponse cause) {
		super(cause);
	}

}