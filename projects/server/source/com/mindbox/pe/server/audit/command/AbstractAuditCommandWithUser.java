package com.mindbox.pe.server.audit.command;

import java.util.Date;

import com.mindbox.pe.server.audit.AuditException;
import com.mindbox.pe.server.audit.AuditStorage;
import com.mindbox.pe.server.spi.ServiceException;
import com.mindbox.pe.server.spi.audit.AuditEventType;

abstract class AbstractAuditCommandWithUser extends AbstractAuditCommand {

	protected final String userID;

	protected AbstractAuditCommandWithUser(AuditEventType eventType, Date date, String userID) {
		super(eventType, date);
		this.userID = userID;
	}

	public void execute(AuditStorage auditStorage) throws AuditException, ServiceException {
		logger.debug("--> execute: " + this);
		AuditDataBuilder auditDataBuilder = new AuditDataBuilder(auditStorage.getNextAuditID(), eventType, date, userID);
		buildAuditData(auditStorage, auditDataBuilder);
		auditDataBuilder.freeze();
		auditStorage.log(auditDataBuilder.getAuditEvent());
	}

	/**
	 * Be sure to call <code>super.buildAuditData()</code> from subclass implementations.
	 * @param auditDataBuilder
	 * @throws AuditException
	 * @throws ServiceException
	 */
	protected abstract void buildAuditData(AuditStorage auditStorage, AuditDataBuilder auditDataBuilder) throws AuditException, ServiceException;

	public String getDescription() {
		return eventType + " event at " + date + (userID == null ? "" : " for " + userID);
	}
}
