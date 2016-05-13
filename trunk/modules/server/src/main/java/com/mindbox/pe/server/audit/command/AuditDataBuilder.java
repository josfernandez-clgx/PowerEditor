package com.mindbox.pe.server.audit.command;

import java.util.Date;
import java.util.LinkedList;

import com.mindbox.pe.server.spi.audit.AuditEvent;
import com.mindbox.pe.server.spi.audit.AuditEventType;
import com.mindbox.pe.server.spi.audit.AuditKBMaster;
import com.mindbox.pe.server.spi.audit.DefaultAuditKBMaster;
import com.mindbox.pe.server.spi.audit.MutableAuditEvent;
import com.mindbox.pe.xsd.audit.ChangeDetail;

/**
 * Helper class to generate audit data.
 * This is not thread-safe. Discard the instance when done.
 * <p>
 * <b>Usage:</b><br/>
 * Call methods in the following order (or IllegalStateException will be thrown):
 * <ol>
 * <li>Create a new instance{@link #AuditDataBuilder(int, AuditEventType, Date, String)}</li>
 * <li>Any number of {@link #insertAuditMasterLog(int, int, int)}</li>
 * <li>Use {@link #getLastAuditKBMaster()}</li> to build each audit KB master</li>
 * <li>Call {@link #freeze()} when done.</li>
 * <li>Finally, {@link #getAuditEvent()} to get the {@link AuditEvent} that this represents.</li>
 * </ol>
 * @see AuditEvent
 * @see AuditKBMaster
 */
class AuditDataBuilder {

	static MutableAuditEvent asAuditEvent(int auditID, AuditEventType auditType, Date date, String user, String description) {
		if (auditType == null) throw new NullPointerException("auditType cannot be null");
		if (date == null) throw new NullPointerException("date cannot be null");
		MutableAuditEvent auditEvent = new MutableAuditEvent();
		auditEvent.setAuditID(auditID);
		auditEvent.setAuditType(auditType);
		auditEvent.setUserName(user);
		auditEvent.setDate(date);
		auditEvent.setDescription(description);
		return auditEvent;
	}

	private final MutableAuditEvent auditEvent;
	private boolean isDone = false;
	private final LinkedList<DefaultAuditKBMaster> auditKBMasterList = new LinkedList<DefaultAuditKBMaster>();

	public AuditDataBuilder(int auditID, AuditEventType auditType, Date date, String user, String description) {
		this.auditEvent = asAuditEvent(auditID, auditType, date, user, description);
	}

	public synchronized AuditEvent getAuditEvent() {
		if (!isFrozen()) throw new IllegalStateException("Call freeze() first");
		return auditEvent;
	}

	private void addKBMasterToEventIfAny() {
		for (final AuditKBMaster auditKBMaster : auditKBMasterList) {
			auditEvent.add(auditKBMaster);
		}
	}

	public synchronized void insertAuditMasterLog(int kbAuditID, int kbChangedTypeID, int elementID) {
		if (isFrozen()) throw new IllegalStateException("Already frozen");
		DefaultAuditKBMaster kbMaster = new DefaultAuditKBMaster(kbAuditID, kbChangedTypeID, elementID);
		auditKBMasterList.add(kbMaster);
	}

	public synchronized AuditKBMaster getLastAuditKBMaster() {
		if (isFrozen()) throw new IllegalStateException("Already frozen");
		return auditKBMasterList.getLast();
	}

	public synchronized void addToLastAuditKBMaster(final ChangeDetail changeDetail) {
		if (isFrozen()) throw new IllegalStateException("Already frozen");
		auditKBMasterList.getLast().add(changeDetail);
	}

	public synchronized void freeze() {
		addKBMasterToEventIfAny();
		isDone = true;
	}

	private synchronized boolean isFrozen() {
		return isDone;
	}
}
