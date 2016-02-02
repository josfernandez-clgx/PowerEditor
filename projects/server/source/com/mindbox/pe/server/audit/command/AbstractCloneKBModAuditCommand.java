package com.mindbox.pe.server.audit.command;

import java.util.Date;

import com.mindbox.pe.model.Auditable;
import com.mindbox.pe.server.audit.AuditConstants;
import com.mindbox.pe.server.audit.AuditException;
import com.mindbox.pe.server.audit.AuditStorage;
import com.mindbox.pe.server.spi.ServiceException;

abstract class AbstractCloneKBModAuditCommand extends AbstractSimpleKBModAuditCommand {

	protected final Auditable sourceAuditable;

	protected AbstractCloneKBModAuditCommand(Auditable auditable, Auditable sourceAuditable, Date date, String userID) {
		super(auditable, date, userID);
		if (sourceAuditable == null) throw new NullPointerException("sourceAuditable cannot be null");
		this.sourceAuditable = sourceAuditable;
	}
	
	protected void buildAuditData(AuditStorage auditStorage, AuditDataBuilder auditDataBuilder) throws AuditException, ServiceException {
		super.buildAuditData(auditStorage, auditDataBuilder);
		int kbAuditDetailID = auditStorage.getNextAuditID();
		auditDataBuilder.getBuildForLastKBMaster().insertAuditDetailLog(kbAuditDetailID, AuditConstants.KB_MOD_TYPE_GENERIC_ADD, this.getDescription());
		auditDataBuilder.getBuildForLastKBMaster().insertAuditDetailDataLog(kbAuditDetailID, AuditConstants.KB_ELEMENT_TYPE_SOURCE_ID, String.valueOf(sourceAuditable.getID()));
	}
	
	protected String getDescPrefix() {
		return "New";
	}
	
	public String getDescription() {
		return super.getDescription() + "; cloned from " + sourceAuditable.getAuditDescription();
	}
}
