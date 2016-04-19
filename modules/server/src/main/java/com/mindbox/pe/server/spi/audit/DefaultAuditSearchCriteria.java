package com.mindbox.pe.server.spi.audit;

import java.util.Date;

import com.mindbox.pe.common.UtilBase;

public final class DefaultAuditSearchCriteria implements AuditSearchCriteria {

	private Date beginDate;
	private Date endDate;
	private int[] auditTypes;
	private int[] kbModifiedElementTypes;

	public Date getBeginDate() {
		return beginDate;
	}

	public void setBeginDate(Date beginDate) {
		this.beginDate = beginDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public int[] getKbModifiedElementTypes() {
		return kbModifiedElementTypes;
	}

	public void setKbModifiedElementTypes(int[] kbModifiedElementTypes) {
		this.kbModifiedElementTypes = kbModifiedElementTypes;
	}


	public int[] getAuditTypes() {
		return auditTypes;
	}

	public void setAuditTypes(int[] auditTypes) {
		this.auditTypes = auditTypes;
	}

	public String toString() {
		return "AuditSearchCriteria[" + UtilBase.toString(auditTypes) + "," + beginDate + "," + endDate + "," + kbModifiedElementTypes
				+ ']';
	}

}
