package com.mindbox.pe.server.spi.audit;

public interface AuditKBDetail {
	
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
	AuditKBDetailData getDetailData(int index);
}
