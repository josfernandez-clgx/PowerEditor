package com.mindbox.pe.communication;


/**
 * 
 * @author Gene Kim
 * @author MindBox, Inc
 * @since PowerEditor 1.0
 */
public class LockException extends ServerException {

	private static final long serialVersionUID = 1906421466458265656L;

	public LockException(String messageKey, Object[] params) {
		super(messageKey, params);
	}

}
