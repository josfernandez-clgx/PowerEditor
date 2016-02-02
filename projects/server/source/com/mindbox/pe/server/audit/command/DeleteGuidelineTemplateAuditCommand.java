package com.mindbox.pe.server.audit.command;

import java.util.Date;

import com.mindbox.pe.model.GridTemplate;
import com.mindbox.pe.server.audit.AuditConstants;
import com.mindbox.pe.server.audit.AuditException;
import com.mindbox.pe.server.audit.AuditStorage;
import com.mindbox.pe.server.spi.ServiceException;

public class DeleteGuidelineTemplateAuditCommand extends AbstractKBModAuditCommand {

	private GridTemplate template;
	private boolean removePolicies;

	public DeleteGuidelineTemplateAuditCommand(GridTemplate template, boolean removePolicies, Date date, String userID) {
		super(date, userID);
		if (template == null) throw new NullPointerException("template cannot be null");
		this.template = template;
		this.removePolicies = removePolicies;
	}

	protected void buildAuditData(AuditStorage auditStorage, AuditDataBuilder auditDataBuilder) throws AuditException, ServiceException {
		super.buildAuditData(auditStorage, auditDataBuilder);
		auditDataBuilder.getBuildForLastKBMaster().insertAuditDetailLog(
				auditStorage.getNextAuditID(),
				AuditConstants.KB_MOD_TYPE_GENERIC_REMOVE,
				"Delete " + template.getAuditDescription());
		if (removePolicies) {
			auditDataBuilder.getBuildForLastKBMaster().insertAuditDetailLog(
					auditStorage.getNextAuditID(),
					AuditConstants.KB_MOD_TYPE_REMOVE_ALL_POLICIES,
					"Removed all guidelines for the template");
		}
	}

	public String getDescription() {
		return "Delete " + template.getAuditDescription() + (removePolicies ? " and removed all guidelines for the template" : "");
	}

	protected int getKBChangedElementID() {
		return template.getID();
	}

	protected int getKBChangedElementTypeID() {
		return AuditConstants.KB_CHANGED_ELMENT_TYPE_GUIDELINE_TEMPLATE;
	}

}
