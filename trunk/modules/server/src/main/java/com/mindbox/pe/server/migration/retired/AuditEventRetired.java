package com.mindbox.pe.server.migration.retired;

import java.util.Date;

import com.mindbox.pe.server.spi.audit.AuditEventType;

@Deprecated
public interface AuditEventRetired {

	int getAuditID();

	AuditEventType getAuditType();

	Date getDate();

	String getDescription();

	/**
	 * Gets the KB master at the specified index.
	 * @param index zero-based index
	 * @return the KB master at the specified index
	 * @throws IndexOutOfBoundsException if index is out or range
	 */
	AuditKBMasterRetired getKBMaster(int index);

	String getUserName();

	int kbMasterCount();
}
