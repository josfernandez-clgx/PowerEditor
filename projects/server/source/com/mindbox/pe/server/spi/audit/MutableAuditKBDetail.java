package com.mindbox.pe.server.spi.audit;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


/**
 * Encapsulates audit details.
 *
 */
public final class MutableAuditKBDetail implements AuditKBDetail {

	private int kbAuditDetailID;
	private int kbAuditID;
	private int kbModTypeID;
	private String description;
	private final List<AuditKBDetailData> detailDataList = Collections.synchronizedList(new ArrayList<AuditKBDetailData>());

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

	public AuditKBDetailData getDetailData(int index) {
		return detailDataList.get(index);
	}

	public void add(AuditKBDetailData detailData) {
		detailDataList.add(detailData);
	}

	public String toString() {
		return "AuditKBDetail[kbAuditID=" + kbAuditID + ",detailID=" + kbAuditDetailID + ",modType=" + kbModTypeID + "]";
	}
}
