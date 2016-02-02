package com.mindbox.pe.server.audit.command;

import java.util.Date;

import com.mindbox.pe.server.audit.AuditException;
import com.mindbox.pe.server.audit.AuditStorage;
import com.mindbox.pe.server.spi.ServiceException;
import com.mindbox.pe.server.spi.audit.AuditEventType;

public final class SimpleEventAuditCommand extends AbstractAuditCommandWithUser {

	public SimpleEventAuditCommand(AuditEventType eventType, Date date, String userID) {
		super(eventType, date, userID);
	}

	protected void buildAuditData(AuditStorage auditStorage, AuditDataBuilder auditDataBuilder) throws AuditException, ServiceException {
		// noop
	}

}
