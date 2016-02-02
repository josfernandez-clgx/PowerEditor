package com.mindbox.pe.server.audit.command;

import java.util.Date;

import com.mindbox.pe.model.GridTemplate;
import com.mindbox.pe.server.audit.AuditConstants;
import com.mindbox.pe.server.audit.AuditException;
import com.mindbox.pe.server.audit.AuditStorage;
import com.mindbox.pe.server.cache.GuidelineTemplateManager;
import com.mindbox.pe.server.spi.ServiceException;

public final class CloneTemplateAuditCommand extends AbstractKBModAuditCommand {

	public static CloneTemplateAuditCommand getInstance(int sourceTemplateID, GridTemplate templateToSave, Date date, String userID) {
		return new CloneTemplateAuditCommand(
				GuidelineTemplateManager.getInstance().getTemplate(sourceTemplateID),
				templateToSave,
				date,
				userID);
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
		auditDataBuilder.getBuildForLastKBMaster().insertAuditDetailDataLog(
				auditStorage.getNextAuditID(),
				AuditConstants.KB_MOD_TYPE_GENERIC_ADD,
				getDescription());
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
