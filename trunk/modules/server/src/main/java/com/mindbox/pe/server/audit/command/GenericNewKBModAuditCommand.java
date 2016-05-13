package com.mindbox.pe.server.audit.command;

import static com.mindbox.pe.common.AuditConstants.KB_MOD_TYPE_GENERIC_ADD;
import static com.mindbox.pe.common.AuditConstants.getModTypeDescription;

import java.util.Date;

import com.mindbox.pe.model.Auditable;
import com.mindbox.pe.server.audit.AuditException;
import com.mindbox.pe.server.audit.AuditStorage;
import com.mindbox.pe.server.spi.ServiceException;
import com.mindbox.pe.xsd.audit.ChangeDetail;

class GenericNewKBModAuditCommand extends AbstractSimpleKBModAuditCommand {

	public GenericNewKBModAuditCommand(Auditable auditable, Date date, String userID) {
		super(auditable, date, userID);
	}

	protected void buildAuditData(AuditStorage auditStorage, AuditDataBuilder auditDataBuilder) throws AuditException, ServiceException {
		super.buildAuditData(auditStorage, auditDataBuilder);
		ChangeDetail changeDetail = new ChangeDetail();
		changeDetail.setChangeType(getModTypeDescription(KB_MOD_TYPE_GENERIC_ADD));
		changeDetail.setChangeDescription(getDescription());
		auditDataBuilder.addToLastAuditKBMaster(changeDetail);
	}

	protected String getDescPrefix() {
		return "New";
	}

}
