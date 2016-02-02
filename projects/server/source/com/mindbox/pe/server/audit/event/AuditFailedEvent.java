package com.mindbox.pe.server.audit.event;

public final class AuditFailedEvent {

	private String message;
	private Exception exception;

	public AuditFailedEvent(String message, Exception exception) {
		this.message = message;
		this.exception = exception;
	}

	public String getMessage() {
		return message;
	}

	public Exception getException() {
		return exception;
	}
}
