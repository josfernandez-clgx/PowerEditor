package com.mindbox.pe.server.spi;

import java.util.List;

import com.mindbox.pe.server.spi.audit.AuditEvent;
import com.mindbox.pe.server.spi.audit.AuditSearchCriteria;

/**
 *
 * @since PowerEditor 1.0
 */
public interface AuditServiceProvider extends PowerEditorServiceProvider {

    /**
	 * Persist the specified audit event.
	 * @param auditDetail the audit event to persist
	 * @throws ServiceException on error
	 */
	void insert(AuditEvent auditEvent) throws ServiceException;


	/**
	 * Retrieves all audit details.
	 * @param auditSearchCriteria search criteria
	 * @return list of {@link AuditEvent} objects
	 * @throws ServiceException on error
	 * @throws NullPointerException if auditSearchCriteria is <code>null</code>
	 */
	List<AuditEvent> retrieveAuditEvents(AuditSearchCriteria auditSearchCriteria) throws ServiceException;
}
