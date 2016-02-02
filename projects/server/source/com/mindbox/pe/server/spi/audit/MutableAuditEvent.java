package com.mindbox.pe.server.spi.audit;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;


/**

 * Encapsulates audit event.
 *
 */
public final class MutableAuditEvent implements AuditEvent {

	private int auditID;
	private AuditEventType auditType;
	private String userName;
	private Date date;
	private final List<AuditKBMaster> kbMasterList = new LinkedList<AuditKBMaster>();

	public AuditEventType getAuditType() {
		return auditType;
	}

	public void setAuditType(AuditEventType auditType) {
		this.auditType = auditType;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public int getAuditID() {
		return auditID;
	}

	public void setAuditID(int auditID) {
		this.auditID = auditID;
	}

	public boolean equals(Object obj) {
		if (obj instanceof MutableAuditEvent) {
			return auditID == ((MutableAuditEvent) obj).auditID;
		}
		else {
			return false;
		}
	}

	public int hashCode() {
		return auditID;
	}

	public String toString() {
		return "AuditEvent[" + auditType.getId() + ",id=" + auditID + ",date=" + date + ",user=" + userName + "]";
	}

	public void add(AuditKBMaster auditKBMaster) {
		synchronized (kbMasterList) {
			kbMasterList.add(auditKBMaster);
		}
	}

	public AuditKBMaster getKBMaster(int index) {
		synchronized (kbMasterList) {
			return kbMasterList.get(index);
		}
	}

	public int kbMasterCount() {
		synchronized (kbMasterList) {
			return kbMasterList.size();
		}
	}
}
