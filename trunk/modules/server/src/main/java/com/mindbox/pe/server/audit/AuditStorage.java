package com.mindbox.pe.server.audit;

import com.mindbox.pe.server.spi.ServiceException;
import com.mindbox.pe.server.spi.audit.AuditEvent;

/**
 * Encapsulates persistent storage of audit logs.
 * 
 * This may not seem necessary. But, this provides an easier way to unit-test,
 * by mocking this interface. 
 * 
 * Furthermore, this provides a mechanism to implement threaded invocation of 
 * data source (DB) calls to insert audit logs.
 */
public interface AuditStorage {

	void log(AuditEvent auditEvent) throws ServiceException;

	int getNextAuditID() throws AuditException;
	
	// TODO Kim: add methods to retrieve audit entries
}
