package com.mindbox.pe.server.audit.command;

import static com.mindbox.pe.common.AuditConstants.KB_MOD_TYPE_GENERIC_REMOVE;
import static com.mindbox.pe.common.AuditConstants.KB_MOD_TYPE_REMOVE_ALL_POLICIES;
import static com.mindbox.pe.common.AuditConstants.getModTypeDescription;

import java.util.Date;

import com.mindbox.pe.common.AuditConstants;
import com.mindbox.pe.model.template.GridTemplate;
import com.mindbox.pe.server.audit.AuditException;
import com.mindbox.pe.server.audit.AuditStorage;
import com.mindbox.pe.server.spi.ServiceException;
import com.mindbox.pe.xsd.audit.ChangeDetail;

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
		ChangeDetail changeDetail = new ChangeDetail();
		changeDetail.setChangeType(getModTypeDescription(KB_MOD_TYPE_GENERIC_REMOVE));
		changeDetail.setChangeDescription(String.format("Delete %s", template.getAuditDescription()));
		auditDataBuilder.addToLastAuditKBMaster(changeDetail);
		if (removePolicies) {
			changeDetail = new ChangeDetail();
			changeDetail.setChangeType(getModTypeDescription(KB_MOD_TYPE_REMOVE_ALL_POLICIES));
			changeDetail.setChangeDescription("Removed all guidelines for the template");
			auditDataBuilder.addToLastAuditKBMaster(changeDetail);
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
