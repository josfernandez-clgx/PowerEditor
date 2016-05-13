package com.mindbox.pe.server.audit.command;

import static com.mindbox.pe.common.AuditConstants.KB_MOD_TYPE_CUTOVER_POLICIES;
import static com.mindbox.pe.common.AuditConstants.KB_MOD_TYPE_GENERIC_ADD;
import static com.mindbox.pe.common.AuditConstants.getModTypeDescription;

import java.util.Date;

import com.mindbox.pe.common.AuditConstants;
import com.mindbox.pe.model.DateSynonym;
import com.mindbox.pe.model.template.GridTemplate;
import com.mindbox.pe.server.audit.AuditException;
import com.mindbox.pe.server.audit.AuditStorage;
import com.mindbox.pe.server.cache.GuidelineTemplateManager;
import com.mindbox.pe.server.spi.ServiceException;
import com.mindbox.pe.xsd.audit.ChangeDetail;

public final class NewTemplateWithCutoverAuditCommand extends AbstractKBModAuditCommand {

	public static NewTemplateWithCutoverAuditCommand getInstance(int sourceTemplateID, GridTemplate templateToSave, DateSynonym cutoverDate, Date date, String userID) {
		return new NewTemplateWithCutoverAuditCommand(GuidelineTemplateManager.getInstance().getTemplate(sourceTemplateID), templateToSave, cutoverDate, date, userID);
	}

	private GridTemplate sourceTemplate;
	private GridTemplate templateToSave;
	private DateSynonym cutoverDate;

	private NewTemplateWithCutoverAuditCommand(GridTemplate sourceTemplate, GridTemplate templateToSave, DateSynonym cutoverDate, Date date, String userID) {
		super(date, userID);
		this.sourceTemplate = sourceTemplate;
		this.templateToSave = templateToSave;
		this.cutoverDate = cutoverDate;
	}

	protected void buildAuditData(AuditStorage auditStorage, AuditDataBuilder auditDataBuilder) throws AuditException, ServiceException {
		super.buildAuditData(auditStorage, auditDataBuilder);
		ChangeDetail changeDetail = new ChangeDetail();
		changeDetail.setChangeType(getModTypeDescription(KB_MOD_TYPE_GENERIC_ADD));
		changeDetail.setChangeDescription(String.format("New %s", templateToSave.getAuditDescription()));
		auditDataBuilder.addToLastAuditKBMaster(changeDetail);

		changeDetail = new ChangeDetail();
		changeDetail.setChangeType(getModTypeDescription(KB_MOD_TYPE_CUTOVER_POLICIES));
		changeDetail.setChangeDescription(String.format("guidelines cutover from %s at %s", sourceTemplate.getAuditDescription(), cutoverDate.getName()));
		auditDataBuilder.addToLastAuditKBMaster(changeDetail);
	}

	public String getDescription() {
		return "New template " + templateToSave.getAuditDescription() + " with guidelines cutover from " + sourceTemplate.getAuditDescription() + " at " + cutoverDate.getName();
	}

	protected int getKBChangedElementID() {
		return templateToSave.getID();
	}

	protected int getKBChangedElementTypeID() {
		return AuditConstants.KB_CHANGED_ELMENT_TYPE_GUIDELINE_TEMPLATE;
	}


}
