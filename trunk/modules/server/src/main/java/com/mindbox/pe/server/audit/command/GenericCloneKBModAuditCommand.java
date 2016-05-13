package com.mindbox.pe.server.audit.command;

import java.util.Date;

import com.mindbox.pe.model.Auditable;

class GenericCloneKBModAuditCommand extends AbstractCloneKBModAuditCommand {

	public GenericCloneKBModAuditCommand(Auditable auditable, Auditable sourceAuditable, Date date, String userID) {
		super(auditable, sourceAuditable, date, userID);
	}

}
