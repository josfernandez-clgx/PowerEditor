package com.mindbox.pe.server.audit.command;

import static com.mindbox.pe.common.AuditConstants.KB_MOD_TYPE_GENERIC_ADD;
import static com.mindbox.pe.common.AuditConstants.getModTypeDescription;

import java.util.Date;

import com.mindbox.pe.model.Auditable;
import com.mindbox.pe.server.audit.AuditException;
import com.mindbox.pe.server.audit.AuditStorage;
import com.mindbox.pe.server.spi.ServiceException;
import com.mindbox.pe.xsd.audit.ChangeDetail;

abstract class AbstractCloneKBModAuditCommand extends AbstractSimpleKBModAuditCommand {

	protected final Auditable sourceAuditable;

	protected AbstractCloneKBModAuditCommand(Auditable auditable, Auditable sourceAuditable, Date date, String userID) {
		super(auditable, date, userID);
		if (sourceAuditable == null) throw new NullPointerException("sourceAuditable cannot be null");
		this.sourceAuditable = sourceAuditable;
	}

	protected void buildAuditData(AuditStorage auditStorage, AuditDataBuilder auditDataBuilder) throws AuditException, ServiceException {
		super.buildAuditData(auditStorage, auditDataBuilder);
		final ChangeDetail changeDetail = new ChangeDetail();
		changeDetail.setChangeType(getModTypeDescription(KB_MOD_TYPE_GENERIC_ADD));
		changeDetail.setChangeDescription(getDescription());
		changeDetail.setSource(String.valueOf(sourceAuditable.getID()));
		auditDataBuilder.addToLastAuditKBMaster(changeDetail);
	}

	protected String getDescPrefix() {
		return "New";
	}

	public String getDescription() {
		return super.getDescription() + "; cloned from " + sourceAuditable.getAuditDescription();
	}
}
