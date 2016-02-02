package com.mindbox.pe.server.spi.audit;

public interface AuditKBMaster {

	int getKbAuditID();

	int getKbChangedTypeID();

	int getElementID();

	int detailCount();

	/**
	 * Gets the detail at the specified index.
	 * @param index zero-based index
	 * @return the detail at the specified index
	 * @throws IndexOutOfBoundsException if index is out or range
	 */
	AuditKBDetail getDetail(int index);
}
