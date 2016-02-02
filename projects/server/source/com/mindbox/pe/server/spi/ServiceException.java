package com.mindbox.pe.server.spi;

public class ServiceException extends Exception {

	private static final long serialVersionUID = 200702140000L;

	public ServiceException(String msg) {
		super(msg);
	}

	public ServiceException(String msg, Throwable t) {
		super(msg, t);
	}
}
