package com.mindbox.pe.server.spi.audit;

import java.util.Date;


public interface AuditEvent {

	int getAuditID();

	AuditEventType getAuditType();

	String getUserName();

	Date getDate();
	
	int kbMasterCount();
	
	/**
	 * Gets the KB master at the specified index.
	 * @param index zero-based index
	 * @return the KB master at the specified index
	 * @throws IndexOutOfBoundsException if index is out or range
	 */
	AuditKBMaster getKBMaster(int index);
}
