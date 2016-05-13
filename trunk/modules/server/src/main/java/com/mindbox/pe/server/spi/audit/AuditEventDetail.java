package com.mindbox.pe.server.spi.audit;

import java.util.List;

/**
 * Audit event details. 
 * This is optional; that is, some audit event do not have any details.
 * 
 * @since 5.5.5
 */
public interface AuditEventDetail {

	List<String> getDetails();
}
