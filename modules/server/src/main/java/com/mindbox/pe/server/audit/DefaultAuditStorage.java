package com.mindbox.pe.server.audit;

import com.mindbox.pe.server.db.DBIdGenerator;
import com.mindbox.pe.server.spi.ServiceException;
import com.mindbox.pe.server.spi.ServiceProviderFactory;
import com.mindbox.pe.server.spi.audit.AuditEvent;

/**
 * Default implementation of {@link AuditStorage} that uses {@link ServiceProviderFactory#getAuditServiceProvider()}.
 * 
 * This may not seem necessary. But, this provides an easier way to unit-test,
 * by mocking the {@link AuditStorage} interface. 
 * 
 * Furthermore, this provides a mechanism to implement threaded invocation of 
 * data source (DB) calls to insert audit logs.
 */
class DefaultAuditStorage implements AuditStorage {

	public void log(AuditEvent auditEvent) throws ServiceException {
		if (auditEvent == null) throw new NullPointerException("auditEvent cannot be null");
		ServiceProviderFactory.getAuditServiceProvider().insert(auditEvent);
	}

	public int getNextAuditID() throws AuditException {
		try {
			return DBIdGenerator.getInstance().nextAuditID();
		}
		catch (Exception e) {
			throw new AuditException("Failed to retrieve unique id from data source", e);
		}
	}
}
