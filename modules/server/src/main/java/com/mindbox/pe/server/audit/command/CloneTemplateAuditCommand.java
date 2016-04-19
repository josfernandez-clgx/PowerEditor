package com.mindbox.pe.server.audit.command;


import static com.mindbox.pe.common.AuditConstants.KB_MOD_TYPE_GENERIC_ADD;
import static com.mindbox.pe.common.AuditConstants.getModTypeDescription;

import java.util.Date;

import com.mindbox.pe.common.AuditConstants;
import com.mindbox.pe.model.template.GridTemplate;
import com.mindbox.pe.server.audit.AuditException;
import com.mindbox.pe.server.audit.AuditStorage;
import com.mindbox.pe.server.cache.GuidelineTemplateManager;
import com.mindbox.pe.server.spi.ServiceException;
import com.mindbox.pe.xsd.audit.ChangeDetail;

public final class CloneTemplateAuditCommand extends AbstractKBModAuditCommand {

	public static CloneTemplateAuditCommand getInstance(int sourceTemplateID, GridTemplate templateToSave, Date date, String userID) {
		return new CloneTemplateAuditCommand(GuidelineTemplateManager.getInstance().getTemplate(sourceTemplateID), templateToSave, date, userID);
	}

	private GridTemplate sourceTemplate;
	private GridTemplate templateToSave;

	private CloneTemplateAuditCommand(GridTemplate sourceTemplate, GridTemplate templateToSave, Date date, String userID) {
		super(date, userID);
		this.sourceTemplate = sourceTemplate;
		this.templateToSave = templateToSave;
	}

	protected void buildAuditData(AuditStorage auditStorage, AuditDataBuilder auditDataBuilder) throws AuditException, ServiceException {
		super.buildAuditData(auditStorage, auditDataBuilder);
		final ChangeDetail changeDetail = new ChangeDetail();
		changeDetail.setChangeType(getModTypeDescription(KB_MOD_TYPE_GENERIC_ADD));
		changeDetail.setChangeDescription(getDescription());
		auditDataBuilder.addToLastAuditKBMaster(changeDetail);
	}

	public String getDescription() {
		return "New " + templateToSave.getAuditDescription() + " cloned from " + sourceTemplate.getAuditDescription();
	}

	protected int getKBChangedElementID() {
		return templateToSave.getID();
	}

	protected int getKBChangedElementTypeID() {
		return AuditConstants.KB_CHANGED_ELMENT_TYPE_GUIDELINE_TEMPLATE;
	}

}
