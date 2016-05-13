package com.mindbox.pe.server.migration.retired;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


/**
 * Encapsulates audit details.
 *
 */
@Deprecated
public final class MutableAuditKBDetailRetired implements AuditKBDetailRetired {

	private int kbAuditDetailID;
	private int kbAuditID;
	private int kbModTypeID;
	private String description;
	private final List<AuditKBDetailDataRetired> detailDataList = Collections.synchronizedList(new ArrayList<AuditKBDetailDataRetired>());

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public int getKbAuditDetailID() {
		return kbAuditDetailID;
	}

	public void setKbAuditDetailID(int kbAuditDetailID) {
		this.kbAuditDetailID = kbAuditDetailID;
	}

	public int getKbAuditID() {
		return kbAuditID;
	}

	public void setKbAuditID(int kbAuditID) {
		this.kbAuditID = kbAuditID;
	}

	public int getKbModTypeID() {
		return kbModTypeID;
	}

	public void setKbModTypeID(int kbModTypeID) {
		this.kbModTypeID = kbModTypeID;
	}

	public int detailDataCount() {
		return detailDataList.size();
	}

	public AuditKBDetailDataRetired getDetailData(int index) {
		return detailDataList.get(index);
	}

	public void add(AuditKBDetailDataRetired detailData) {
		detailDataList.add(detailData);
	}

	public String toString() {
		return "AuditKBDetail[kbAuditID=" + kbAuditID + ",detailID=" + kbAuditDetailID + ",modType=" + kbModTypeID + "]";
	}
}
