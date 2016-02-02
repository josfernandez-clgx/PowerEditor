package com.mindbox.pe.server.report.audit;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import com.mindbox.pe.server.spi.audit.AuditEvent;
import com.mindbox.pe.server.spi.audit.AuditEventType;

/**
 * Audit log.
 * Wrapper for {@link AuditEvent} for reporting API.
 * @author kim
 *
 */
public class AuditLog {

	public static AuditLog valueOf(AuditEvent auditEvent) {
		return new AuditLog(auditEvent);
	}

	private int auditID;
	private final AuditEventType auditType;
	private final String userName;
	private final Date date;
	private final List<ChangedElement> changedElementList;

	private AuditLog(AuditEvent auditEvent) {
		this.auditID = auditEvent.getAuditID();
		this.auditType = auditEvent.getAuditType();
		this.userName = auditEvent.getUserName();
		this.date = auditEvent.getDate();
		List<ChangedElement> tempList = new ArrayList<ChangedElement>();
		for (int i = 0; i < auditEvent.kbMasterCount(); i++) {
			tempList.add(ChangedElement.valueOf(auditEvent.getKBMaster(i)));
		}
		// Make sure the list is sorted by kb audit id
		Collections.sort(tempList, new Comparator<ChangedElement>() {
			public int compare(ChangedElement ce1, ChangedElement ce2) {
				if (ce1 == ce2) return 0;
				return new Integer(ce1.getKbAuditID()).compareTo(new Integer(ce2.getKbAuditID()));
			}
		});
		this.changedElementList = Collections.unmodifiableList(tempList);
	}

	public int getAuditID() {
		return auditID;
	}

	public AuditEventType getAuditType() {
		return auditType;
	}

	public Date getDate() {
		return date;
	}

	public String getUserName() {
		return userName;
	}

	public List<ChangedElement> getChangedElements() {
		return changedElementList;
	}

}
