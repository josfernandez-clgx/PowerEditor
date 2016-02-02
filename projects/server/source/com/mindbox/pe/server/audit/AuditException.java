package com.mindbox.pe.server.audit;

public class AuditException extends Exception {

	private static final long serialVersionUID = 200704250000L;

	public AuditException(String message) {
		super(message);
	}

	public AuditException(String message, Throwable t) {
		super(message, t);
	}
}
