package com.mindbox.ftest.pe.util;

public class CommandTimedOutException extends RuntimeException {

	private static final long serialVersionUID = 20070525000001L;
	
	public CommandTimedOutException() {
		super();
	}

	public CommandTimedOutException(String msg) {
		super(msg);
	}

	public CommandTimedOutException(String msg, Throwable t) {
		super(msg, t);
	}
}
