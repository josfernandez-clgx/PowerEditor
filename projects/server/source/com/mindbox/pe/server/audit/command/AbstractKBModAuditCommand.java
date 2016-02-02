package com.mindbox.pe.server.audit.command;

import java.util.Date;

import com.mindbox.pe.server.audit.AuditException;
import com.mindbox.pe.server.audit.AuditStorage;
import com.mindbox.pe.server.spi.ServiceException;
import com.mindbox.pe.server.spi.audit.AuditEventType;

abstract class AbstractKBModAuditCommand extends AbstractAuditCommandWithUser {

	protected AbstractKBModAuditCommand(Date date, String userID) {
		super(AuditEventType.KB_MOD, date, userID);
	}

	protected void buildAuditData(AuditStorage auditStorage, AuditDataBuilder auditDataBuilder) throws AuditException,
			ServiceException {
		int kbAuditID = auditStorage.getNextAuditID();
		auditDataBuilder.insertAuditMasterLog(kbAuditID, getKBChangedElementTypeID(), getKBChangedElementID());
	}

	protected abstract int getKBChangedElementTypeID();

	protected abstract int getKBChangedElementID();
}
