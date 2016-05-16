package com.mindbox.pe.server;

import com.mindbox.pe.model.exceptions.SapphireException;

/**
 * 
 * @since PowerEditor 1.0
 */
public class ServerException extends SapphireException {

	private static final long serialVersionUID = -7088621056400980252L;

	public ServerException() {
		super();
	}

	public ServerException(String msg) {
		super(msg);
	}

}
