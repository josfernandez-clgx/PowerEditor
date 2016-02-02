package com.mindbox.pe.server.audit.command;

import java.util.Date;

import com.mindbox.pe.model.Auditable;
import com.mindbox.pe.server.audit.AuditConstants;
import com.mindbox.pe.server.audit.AuditException;
import com.mindbox.pe.server.audit.AuditStorage;
import com.mindbox.pe.server.spi.ServiceException;

class GenericDeleteKBModAuditCommand extends AbstractSimpleKBModAuditCommand {

	protected GenericDeleteKBModAuditCommand(Auditable auditable, Date date, String userID) {
		super(auditable, date, userID);
	}

	protected void buildAuditData(AuditStorage auditStorage, AuditDataBuilder auditDataBuilder) throws AuditException, ServiceException {
		super.buildAuditData(auditStorage, auditDataBuilder);
		int kbAuditDetailID = auditStorage.getNextAuditID();
		auditDataBuilder.getBuildForLastKBMaster().insertAuditDetailLog(kbAuditDetailID, AuditConstants.KB_MOD_TYPE_GENERIC_REMOVE, getDescription());
	}

	protected String getDescPrefix() {
		return "Delete";
	}

}
