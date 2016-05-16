package com.mindbox.pe.server.spi;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import javax.xml.bind.JAXBException;

import com.mindbox.pe.server.spi.audit.AuditEvent;
import com.mindbox.pe.server.spi.audit.AuditSearchCriteria;
import com.mindbox.pe.xsd.audit.ChangeDetail;

/**
 *
 * @since PowerEditor 1.0
 */
public interface AuditServiceProvider extends PowerEditorServiceProvider {

	/**
	 * Persist the specified audit event.
	 * @param auditEvent the audit event to persist
	 * @throws ServiceException on error
	 */
	void insert(AuditEvent auditEvent) throws ServiceException;

	void insertChangeDetails(Connection connection, final int kbAuditId, final List<ChangeDetail> changeDetails) throws SQLException, JAXBException;

	/**
	 * Retrieves all audit details.
	 * @param auditSearchCriteria search criteria
	 * @return list of {@link AuditEvent} objects
	 * @throws ServiceException on error
	 * @throws NullPointerException if auditSearchCriteria is <code>null</code>
	 */
	List<AuditEvent> retrieveAuditEvents(AuditSearchCriteria auditSearchCriteria) throws ServiceException;
}
