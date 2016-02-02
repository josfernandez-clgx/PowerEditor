package com.mindbox.pe.server.spi.audit;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Encapsulates audit master.
 *
 */
public final class MutableAuditKBMaster implements AuditKBMaster {

	private int KbAuditID;
	private int kbChangedTypeID;
	private int elementID;
	private final List<AuditKBDetail> detailList = Collections.synchronizedList(new ArrayList<AuditKBDetail>());

	public int getElementID() {
		return elementID;
	}

	public void setElementID(int elementID) {
		this.elementID = elementID;
	}

	public int getKbAuditID() {
		return KbAuditID;
	}

	public void setKbAuditID(int kbAuditID) {
		KbAuditID = kbAuditID;
	}

	public int getKbChangedTypeID() {
		return kbChangedTypeID;
	}

	public void setKbChangedTypeID(int kbChangedTypeID) {
		this.kbChangedTypeID = kbChangedTypeID;
	}

	public int detailCount() {
		return detailList.size();
	}

	public AuditKBDetail getDetail(int index) {
		return detailList.get(index);
	}

	public void add(AuditKBDetail detail) {
		detailList.add(detail);
	}

	public String toString() {
		return "AuditKBMaster[kbAuditID=" + KbAuditID + ",changedType=" + kbChangedTypeID + ",elementID=" + elementID + "]";
	}
}
