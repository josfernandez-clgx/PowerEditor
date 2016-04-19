package com.mindbox.pe.server.migration.retired;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


/**
 * Encapsulates audit master.
 *
 */
@Deprecated
public final class MutableAuditKBMasterRetired implements AuditKBMasterRetired {

	private int KbAuditID;
	private int kbChangedTypeID;
	private int elementID;
	private final List<AuditKBDetailRetired> detailList = Collections.synchronizedList(new ArrayList<AuditKBDetailRetired>());

	public void add(AuditKBDetailRetired detail) {
		detailList.add(detail);
	}

	public int detailCount() {
		return detailList.size();
	}

	public AuditKBDetailRetired getDetail(int index) {
		return detailList.get(index);
	}

	public int getElementID() {
		return elementID;
	}

	public int getKbAuditID() {
		return KbAuditID;
	}

	public int getKbChangedTypeID() {
		return kbChangedTypeID;
	}

	public void setElementID(int elementID) {
		this.elementID = elementID;
	}

	public void setKbAuditID(int kbAuditID) {
		KbAuditID = kbAuditID;
	}

	public void setKbChangedTypeID(int kbChangedTypeID) {
		this.kbChangedTypeID = kbChangedTypeID;
	}

	public String toString() {
		return "AuditKBMaster[kbAuditID=" + KbAuditID + ",changedType=" + kbChangedTypeID + ",elementID=" + elementID + "]";
	}
}
