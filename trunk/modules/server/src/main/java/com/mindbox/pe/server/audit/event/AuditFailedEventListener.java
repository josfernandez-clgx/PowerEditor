package com.mindbox.pe.server.audit.event;

public interface AuditFailedEventListener {

	void auditFailed(AuditFailedEvent auditErrorEvent);
}
