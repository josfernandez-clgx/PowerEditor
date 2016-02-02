package com.mindbox.pe.server.audit.command;

import java.util.Date;

import com.mindbox.pe.model.Auditable;
import com.mindbox.pe.model.GenericEntity;
import com.mindbox.pe.server.audit.AuditConstants;
import com.mindbox.pe.server.audit.AuditException;
import com.mindbox.pe.server.audit.AuditStorage;
import com.mindbox.pe.server.spi.ServiceException;

public class CloneEntityAuditCommand extends AbstractCloneKBModAuditCommand {

	private boolean copyPolicies;

	public CloneEntityAuditCommand(GenericEntity newEntity, GenericEntity sourceEntity, boolean copyPolicies, Date date, String userID) {
		super(newEntity, sourceEntity, date, userID);
		this.copyPolicies = copyPolicies;
	}

	protected void buildAuditData(AuditStorage auditStorage, AuditDataBuilder auditDataBuilder) throws AuditException, ServiceException {
		super.buildAuditData(auditStorage, auditDataBuilder);
		if (copyPolicies) {
			auditDataBuilder.getBuildForLastKBMaster().insertAuditDetailDataLog(
					auditStorage.getNextAuditID(),
					AuditConstants.KB_MOD_TYPE_COPY_POLICIES,
					"copied poiclies of " + auditable.getAuditDescription());
		}

	}

	protected String getDescPrefix() {
		return "Cloned ";
	}

	protected String getDescription(Auditable auditable) {
		return auditable.getAuditDescription()
				+ (copyPolicies ? "; copied policies for " + ((GenericEntity) sourceAuditable).getName() : "");
	}
}
