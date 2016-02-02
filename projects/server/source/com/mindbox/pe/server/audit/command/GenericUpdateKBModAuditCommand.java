package com.mindbox.pe.server.audit.command;

import java.util.Date;

import com.mindbox.pe.model.Auditable;
import com.mindbox.pe.server.audit.AuditConstants;
import com.mindbox.pe.server.audit.AuditException;
import com.mindbox.pe.server.audit.AuditStorage;
import com.mindbox.pe.server.spi.ServiceException;

class GenericUpdateKBModAuditCommand extends AbstractUpdateKBModAuditCommand {

	protected GenericUpdateKBModAuditCommand(Auditable auditable, Auditable oldAuditable, Date date, String userID) {
		super(auditable, oldAuditable, date, userID);
	}

	protected void buildAuditDetailForUpdate(AuditStorage auditStorage, AuditDataBuilder auditDataBuilder) throws AuditException,
			ServiceException {
		int kbAuditDetailID = auditStorage.getNextAuditID();
		auditDataBuilder.getBuildForLastKBMaster().insertAuditDetailLog(kbAuditDetailID, AuditConstants.KB_MOD_TYPE_GENERIC_UPDATE, this.getDescription());
	}

}
