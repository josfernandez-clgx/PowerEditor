package com.mindbox.pe.server.audit.command;

import java.util.Date;

import com.mindbox.pe.common.AuditConstants;
import com.mindbox.pe.model.Auditable;
import com.mindbox.pe.model.DateSynonym;
import com.mindbox.pe.model.GenericCategory;
import com.mindbox.pe.model.GenericEntity;
import com.mindbox.pe.model.admin.Role;
import com.mindbox.pe.model.assckey.GenericEntityCompatibilityData;
import com.mindbox.pe.model.cbr.CBRAttribute;
import com.mindbox.pe.model.cbr.CBRCase;
import com.mindbox.pe.model.cbr.CBRCaseBase;
import com.mindbox.pe.model.grid.AbstractGrid;
import com.mindbox.pe.model.process.Phase;
import com.mindbox.pe.model.process.ProcessRequest;
import com.mindbox.pe.model.rule.ActionTypeDefinition;
import com.mindbox.pe.model.rule.TestTypeDefinition;
import com.mindbox.pe.model.template.GridTemplate;
import com.mindbox.pe.model.template.ParameterTemplate;
import com.mindbox.pe.server.model.User;

abstract class AbstractSimpleKBModAuditCommand extends AbstractKBModAuditCommand {

	protected final Auditable auditable;

	protected AbstractSimpleKBModAuditCommand(Auditable auditable, Date date, String userID) {
		super(date, userID);
		if (auditable == null) throw new NullPointerException("auditable cannot be null");
		this.auditable = auditable;
	}

	protected abstract String getDescPrefix();

	protected final int getKBChangedElementID() {
		return auditable.getID();
	}

	protected final int getKBChangedElementTypeID() {
		if (auditable instanceof GenericCategory) {
			return AuditConstants.KB_CHANGED_ELMENT_TYPE_CATEGORY;
		}
		else if (auditable instanceof DateSynonym) {
			return AuditConstants.KB_CHANGED_ELMENT_TYPE_DATE_SYNONYM;
		}
		else if (auditable instanceof GenericEntity) {
			return AuditConstants.KB_CHANGED_ELMENT_TYPE_ENTITY;
		}
		else if (auditable instanceof GenericEntityCompatibilityData) {
			return AuditConstants.KB_CHANGED_ELMENT_TYPE_ENTITY_COMPATIBILITY;
		}
		else if (auditable instanceof ActionTypeDefinition) {
			return AuditConstants.KB_CHANGED_ELMENT_TYPE_GUIDELINE_ACTION;
		}
		else if (AbstractGrid.class.isInstance(auditable)) {
			return ((AbstractGrid<?>) auditable).isParameterGrid() ? AuditConstants.KB_CHANGED_ELMENT_TYPE_PARAMETER_ACTIVATION : AuditConstants.KB_CHANGED_ELMENT_TYPE_GUIDELINE_ACTIVATION;
		}
		else if (auditable instanceof GridTemplate) {
			return AuditConstants.KB_CHANGED_ELMENT_TYPE_GUIDELINE_TEMPLATE;
		}
		else if (auditable instanceof ParameterTemplate) {
			return AuditConstants.KB_CHANGED_ELMENT_TYPE_PARAMETER_TEMPLATE;
		}
		else if (auditable instanceof Phase) {
			return AuditConstants.KB_CHANGED_ELMENT_TYPE_PHASE;
		}
		else if (auditable instanceof ProcessRequest) {
			return AuditConstants.KB_CHANGED_ELMENT_TYPE_PROCESS;
		}
		else if (auditable instanceof Role) {
			return AuditConstants.KB_CHANGED_ELMENT_TYPE_ROLE;
		}
		else if (auditable instanceof TestTypeDefinition) {
			return AuditConstants.KB_CHANGED_ELMENT_TYPE_TEST_CONDITION;
		}
		else if (auditable instanceof User) {
			return AuditConstants.KB_CHANGED_ELMENT_TYPE_USER;
		}
		else if (auditable instanceof CBRAttribute) {
			return AuditConstants.KB_CHANGED_ELMENT_TYPE_CBR_ATTRIBUTE;
		}
		else if (auditable instanceof CBRCase) {
			return AuditConstants.KB_CHANGED_ELMENT_TYPE_CBR_CASE;
		}
		else if (auditable instanceof CBRCaseBase) {
			return AuditConstants.KB_CHANGED_ELMENT_TYPE_CBR_CASE_BASE;
		}
		throw new IllegalArgumentException("Unsupported auditable class: " + auditable.getClass().getName());
	}

	public String getDescription() {
		return getDescPrefix() + " " + auditable.getAuditDescription();
	}
}
