package com.mindbox.pe.server.migration.retired;

@Deprecated
public interface AuditKBDetailRetired {

	int getKbAuditDetailID();

	int getKbAuditID();

	int getKbModTypeID();

	String getDescription();

	int detailDataCount();

	/**
	 * Gets the detail data at the specified index.
	 * @param index zero-based index
	 * @return the detail data at the specified index
	 * @throws IndexOutOfBoundsException if index is out or range
	 */
	AuditKBDetailDataRetired getDetailData(int index);
}
