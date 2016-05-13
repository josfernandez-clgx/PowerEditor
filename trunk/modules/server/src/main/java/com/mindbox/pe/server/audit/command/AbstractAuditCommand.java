package com.mindbox.pe.server.audit.command;

import java.util.Date;

import org.apache.log4j.Logger;

import com.mindbox.pe.server.spi.audit.AuditEventType;

abstract class AbstractAuditCommand implements AuditCommand {

	protected final Logger logger = Logger.getLogger(getClass());
	protected final AuditEventType eventType;
	protected final Date date;
	protected final String auditDescription;

	public AbstractAuditCommand(AuditEventType eventType, Date date, final String auditDescription) {
		if (eventType == null) {
			throw new NullPointerException("eventType cannot be null");
		}
		if (date == null) {
			throw new NullPointerException("date cannot be null");
		}
		this.eventType = eventType;
		this.date = date;
		this.auditDescription = auditDescription;
	}

	public String getAuditDescription() {
		return auditDescription;
	}
}
