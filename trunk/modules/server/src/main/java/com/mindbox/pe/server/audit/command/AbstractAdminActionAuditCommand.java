package com.mindbox.pe.server.audit.command;

import java.util.Date;

import com.mindbox.pe.server.spi.audit.AuditEventType;

public abstract class AbstractAdminActionAuditCommand extends AbstractAuditCommandWithUser {

	protected AbstractAdminActionAuditCommand(AuditEventType eventType, Date date, String userID, String description) {
		super(eventType, date, userID, description);
	}

}
