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
	private String description;
	private final List<AuditKBMaster> kbMasterList = new LinkedList<AuditKBMaster>();

	public void add(AuditKBMaster auditKBMaster) {
		synchronized (kbMasterList) {
			kbMasterList.add(auditKBMaster);
		}
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof MutableAuditEvent) {
			return auditID == ((MutableAuditEvent) obj).auditID;
		}
		else {
			return false;
		}
	}

	@Override
	public int getAuditID() {
		return auditID;
	}

	@Override
	public AuditEventType getAuditType() {
		return auditType;
	}

	@Override
	public Date getDate() {
		return date;
	}

	@Override
	public final String getDescription() {
		return description;
	}

	@Override
	public AuditKBMaster getKBMaster(int index) {
		synchronized (kbMasterList) {
			return kbMasterList.get(index);
		}
	}

	@Override
	public String getUserName() {
		return userName;
	}

	@Override
	public int hashCode() {
		return auditID;
	}

	@Override
	public int kbMasterCount() {
		synchronized (kbMasterList) {
			return kbMasterList.size();
		}
	}

	public void setAuditID(int auditID) {
		this.auditID = auditID;
	}

	public void setAuditType(AuditEventType auditType) {
		this.auditType = auditType;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public final void setDescription(String description) {
		this.description = description;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	@Override
	public String toString() {
		return "AuditEvent[" + auditType.getId() + ",id=" + auditID + ",date=" + date + ",user=" + userName + "]";
	}
}
