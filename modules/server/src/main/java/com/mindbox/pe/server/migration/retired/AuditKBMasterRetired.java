package com.mindbox.pe.server.migration.retired;


@Deprecated
public interface AuditKBMasterRetired {

	void add(AuditKBDetailRetired auditKBDetail);

	int detailCount();

	/**
	 * Gets the detail at the specified index.
	 * @param index zero-based index
	 * @return the detail at the specified index
	 * @throws IndexOutOfBoundsException if index is out or range
	 */
	AuditKBDetailRetired getDetail(int index);

	int getElementID();

	int getKbAuditID();

	int getKbChangedTypeID();
}
