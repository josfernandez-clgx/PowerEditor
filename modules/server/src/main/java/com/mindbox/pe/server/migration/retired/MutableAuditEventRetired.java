package com.mindbox.pe.server.migration.retired;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import com.mindbox.pe.server.spi.audit.AuditEventType;


/**
 * Encapsulates audit event.
 *
 */
@Deprecated
public final class MutableAuditEventRetired implements AuditEventRetired {

	private int auditID;
	private AuditEventType auditType;
	private String userName;
	private Date date;
	private String description;
	private final List<AuditKBMasterRetired> kbMasterList = new LinkedList<AuditKBMasterRetired>();

	public void add(AuditKBMasterRetired auditKBMaster) {
		synchronized (kbMasterList) {
			kbMasterList.add(auditKBMaster);
		}
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof MutableAuditEventRetired) {
			return auditID == ((MutableAuditEventRetired) obj).auditID;
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
	public AuditKBMasterRetired getKBMaster(int index) {
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
