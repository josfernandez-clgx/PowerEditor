package com.mindbox.pe.server.audit.event;

public interface AuditFailedEventSupport {

	void fireAuditFailed(String message, Exception exception);

}
