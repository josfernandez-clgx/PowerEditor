package com.mindbox.pe.server.audit.command;

import java.util.Date;

import com.mindbox.pe.model.Auditable;
import com.mindbox.pe.server.audit.AuditException;
import com.mindbox.pe.server.audit.AuditStorage;
import com.mindbox.pe.server.spi.ServiceException;

abstract class AbstractUpdateKBModAuditCommand extends AbstractSimpleKBModAuditCommand {

	protected final Auditable oldAuditable;

	protected AbstractUpdateKBModAuditCommand(Auditable auditable, Auditable oldAuditable, Date date, String userID) {
		super(auditable, date, userID);
		if (oldAuditable == null) throw new NullPointerException("oldAuditable cannot be null");
		this.oldAuditable = oldAuditable;
	}

	protected final void buildAuditData(AuditStorage auditStorage, AuditDataBuilder auditDataBuilder) throws AuditException, ServiceException {
		super.buildAuditData(auditStorage, auditDataBuilder);
		buildAuditDetailForUpdate(auditStorage, auditDataBuilder);
	}

	protected abstract void buildAuditDetailForUpdate(AuditStorage auditStorage, AuditDataBuilder auditDataBuilder) throws AuditException,
			ServiceException;

	protected String getDescPrefix() {
		return "Update";
	}

}
