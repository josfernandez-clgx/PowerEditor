package com.mindbox.pe.server.model;

import static com.mindbox.pe.common.AuditConstants.KB_CHANGED_ELMENT_TYPE_CATEGORY;
import static com.mindbox.pe.common.AuditConstants.KB_CHANGED_ELMENT_TYPE_CBR_ATTRIBUTE;
import static com.mindbox.pe.common.AuditConstants.KB_CHANGED_ELMENT_TYPE_CBR_CASE;
import static com.mindbox.pe.common.AuditConstants.KB_CHANGED_ELMENT_TYPE_CBR_CASE_BASE;
import static com.mindbox.pe.common.AuditConstants.KB_CHANGED_ELMENT_TYPE_DATE_SYNONYM;
import static com.mindbox.pe.common.AuditConstants.KB_CHANGED_ELMENT_TYPE_ENTITY;
import static com.mindbox.pe.common.AuditConstants.KB_CHANGED_ELMENT_TYPE_ENTITY_CAT_RELATIONSHIP;
import static com.mindbox.pe.common.AuditConstants.KB_CHANGED_ELMENT_TYPE_ENTITY_COMPATIBILITY;
import static com.mindbox.pe.common.AuditConstants.KB_CHANGED_ELMENT_TYPE_GUIDELINE_ACTION;
import static com.mindbox.pe.common.AuditConstants.KB_CHANGED_ELMENT_TYPE_GUIDELINE_ACTIVATION;
import static com.mindbox.pe.common.AuditConstants.KB_CHANGED_ELMENT_TYPE_GUIDELINE_TEMPLATE;
import static com.mindbox.pe.common.AuditConstants.KB_CHANGED_ELMENT_TYPE_PARAMETER_ACTIVATION;
import static com.mindbox.pe.common.AuditConstants.KB_CHANGED_ELMENT_TYPE_PARAMETER_TEMPLATE;
import static com.mindbox.pe.common.AuditConstants.KB_CHANGED_ELMENT_TYPE_PHASE;
import static com.mindbox.pe.common.AuditConstants.KB_CHANGED_ELMENT_TYPE_PROCESS;
import static com.mindbox.pe.common.AuditConstants.KB_CHANGED_ELMENT_TYPE_ROLE;
import static com.mindbox.pe.common.AuditConstants.KB_CHANGED_ELMENT_TYPE_TEST_CONDITION;
import static com.mindbox.pe.common.AuditConstants.KB_CHANGED_ELMENT_TYPE_USER;
import static com.mindbox.pe.common.AuditConstants.getChangedTypeDescription;
import static com.mindbox.pe.server.audit.AuditUtil.generateContextStringForAudit;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.mindbox.pe.common.AuditConstants;
import com.mindbox.pe.common.DateUtil;
import com.mindbox.pe.model.EnumValue;
import com.mindbox.pe.model.GenericCategory;
import com.mindbox.pe.model.GenericEntity;
import com.mindbox.pe.model.admin.Role;
import com.mindbox.pe.model.grid.ParameterGrid;
import com.mindbox.pe.model.grid.ProductGrid;
import com.mindbox.pe.model.template.GridTemplate;
import com.mindbox.pe.server.cache.DateSynonymManager;
import com.mindbox.pe.server.cache.EntityManager;
import com.mindbox.pe.server.cache.GridManager;
import com.mindbox.pe.server.cache.GuidelineFunctionManager;
import com.mindbox.pe.server.cache.GuidelineTemplateManager;
import com.mindbox.pe.server.cache.ParameterManager;
import com.mindbox.pe.server.cache.ParameterTemplateManager;
import com.mindbox.pe.server.cache.SecurityCacheManager;
import com.mindbox.pe.server.report.ReportException;
import com.mindbox.pe.server.spi.audit.AuditEvent;
import com.mindbox.pe.server.spi.audit.AuditKBMaster;
import com.mindbox.pe.xsd.audit.AuditReport;
import com.mindbox.pe.xsd.audit.ChangeDetails;
import com.mindbox.pe.xsd.audit.ChangedElement;
import com.mindbox.pe.xsd.audit.EventType;
import com.mindbox.pe.xsd.extenm.EnumValueType;

public class ModelUtil {

	private static final Logger LOG = Logger.getLogger(ModelUtil.class);

	private static ActivationDetail asActivationDetail(final AuditKBMaster kbMaster) throws ReportException {
		if (isActivation(kbMaster.getKbChangedTypeID())) {
			final int kbChangedTypeID = kbMaster.getKbChangedTypeID();
			final int elementID = kbMaster.getElementID();
			try {
				boolean isParameter = kbChangedTypeID == AuditConstants.KB_CHANGED_ELMENT_TYPE_PARAMETER_ACTIVATION;
				if (isParameter) {
					ParameterGrid grid = ParameterManager.getInstance().getGrid(elementID);
					return ActivationDetail.createInstance(AuditConstants.PARAMETER_USAGE_TYPE, grid, elementID, true);
				}
				else {
					ProductGrid grid = GridManager.getInstance().getProductGrid(elementID);
					return ActivationDetail.createInstance((grid == null ? "UNKNOWN" : grid.getTemplate().getUsageType().getDisplayName()), grid, elementID, false);
				}
			}
			catch (Exception ex) {
				LOG.error("Failed to get activation detail", ex);
				throw new ReportException(ex.getMessage());
			}
		}
		else {
			return null;
		}
	}

	public static AuditReport.AuditEvent asAuditReportAuditEvent(final AuditEvent auditEvent) throws ReportException {
		final AuditReport.AuditEvent auditReportAuditEvent = new AuditReport.AuditEvent();
		auditReportAuditEvent.setType(EventType.fromValue(auditEvent.getAuditType().getName()));
		auditReportAuditEvent.setAuditID(auditEvent.getAuditID());
		auditReportAuditEvent.setAuditDate(DateUtil.toXMLGregorianCalendar(auditEvent.getDate()));
		auditReportAuditEvent.setDescription(auditEvent.getDescription());
		auditReportAuditEvent.setUsername(auditEvent.getUserName());

		final List<ChangeDetails> changeDetailsList = new ArrayList<ChangeDetails>();
		for (int i = 0; i < auditEvent.kbMasterCount(); i++) {
			changeDetailsList.add(asChangeDetails(auditEvent.getKBMaster(i)));
		}
		auditReportAuditEvent.getChangeDetails().addAll(changeDetailsList);

		return auditReportAuditEvent;
	}

	public static ChangedElement asChangedElement(final AuditKBMaster kbMaster) throws ReportException {
		final ChangedElement changedElement = new ChangedElement();
		changedElement.setKbAuditID(kbMaster.getKbAuditID());
		changedElement.setType(getChangedTypeDescription(kbMaster.getKbChangedTypeID()));

		final ActivationDetail activationDetail = asActivationDetail(kbMaster);
		if (activationDetail != null) {
			if (activationDetail.getEffectiveDate() != null) {
				changedElement.setActivationDate(DateUtil.toXMLGregorianCalendar(activationDetail.getEffectiveDate()));
			}
			changedElement.setActivationDateName(activationDetail.getEffectiveDateName());
			if (activationDetail.getContextContainer() != null) {
				changedElement.setContext(generateContextStringForAudit(activationDetail.getContextContainer().extractGuidelineContext()));
			}
			if (activationDetail.getExpirationDate() != null) {
				changedElement.setExpirationDate(DateUtil.toXMLGregorianCalendar(activationDetail.getExpirationDate()));
			}
			changedElement.setExpirationDateName(activationDetail.getExpirationDateName());
			changedElement.setTemplateName(activationDetail.getTemplateName());
			changedElement.setUsageType(activationDetail.getUsageType());
			changedElement.setName(activationDetail.getAuditName());
			changedElement.setVersion(activationDetail.getVersion());
		}
		else {
			changedElement.setName(getElementName(kbMaster.getKbChangedTypeID(), kbMaster.getElementID()));
		}
		return changedElement;
	}

	private static ChangeDetails asChangeDetails(final AuditKBMaster auditKBMaster) throws ReportException {
		final ChangeDetails changeDetails = new ChangeDetails();
		changeDetails.setChangedElement(asChangedElement(auditKBMaster));
		changeDetails.getChangeDetail().addAll(auditKBMaster.getChangeDetails());
		return changeDetails;
	}

	public static EnumValue asEnumValue(final EnumValueType enumValueType) {
		EnumValue enumValue = new EnumValue();
		enumValue.setDeployValue(enumValueType.getValue());
		enumValue.setDisplayLabel(enumValueType.getDisplayLabel());
		enumValue.setInactive((enumValueType.isInactive() == null ? "false" : String.valueOf(enumValueType.isInactive())));
		return enumValue;
	}


	private static String getElementName(final int kbChangedTypeID, final int elementID) {
		try {
			switch (kbChangedTypeID) {
			case KB_CHANGED_ELMENT_TYPE_GUIDELINE_TEMPLATE: {
				GridTemplate template = GuidelineTemplateManager.getInstance().getTemplate(elementID);
				return template == null ? "Guideline Template " + elementID : template.getAuditName();
			}
			case KB_CHANGED_ELMENT_TYPE_ENTITY: {
				GenericEntity entity = EntityManager.getInstance().findFirstEntity(elementID);
				return entity == null ? "Entity " + elementID : entity.getAuditName();
			}
			case KB_CHANGED_ELMENT_TYPE_CATEGORY: {
				GenericCategory category = EntityManager.getInstance().findFirstCategory(elementID);
				return category == null ? "Category " + elementID : category.getAuditName();
			}
			case KB_CHANGED_ELMENT_TYPE_ENTITY_CAT_RELATIONSHIP: {
				return "Entity-To-Category Relationship";
			}
			case KB_CHANGED_ELMENT_TYPE_PARAMETER_TEMPLATE: {
				return ParameterTemplateManager.getInstance().getTemplate(elementID).getAuditName();
			}
			case KB_CHANGED_ELMENT_TYPE_ENTITY_COMPATIBILITY: {
				return "Entity Compatibility " + elementID;
			}
			case KB_CHANGED_ELMENT_TYPE_DATE_SYNONYM: {
				return DateSynonymManager.getInstance().getDateSynonym(elementID).getAuditName();
			}
			case KB_CHANGED_ELMENT_TYPE_GUIDELINE_ACTION: {
				return GuidelineFunctionManager.getInstance().getActionTypeDefinition(elementID).getAuditName();
			}
			case KB_CHANGED_ELMENT_TYPE_TEST_CONDITION: {
				return GuidelineFunctionManager.getInstance().getTestTypeDefinition(elementID).getAuditName();
			}
			case KB_CHANGED_ELMENT_TYPE_ROLE: {
				Role role = SecurityCacheManager.getInstance().getRole(elementID);
				return role == null ? "Role " + elementID : role.getAuditName();
			}
			case KB_CHANGED_ELMENT_TYPE_USER: {
				User user = SecurityCacheManager.getInstance().findUserByID(elementID);
				return user == null ? "User " + elementID : user.getUserID();
			}
			case KB_CHANGED_ELMENT_TYPE_PHASE:
				return "Phase " + elementID;
			case KB_CHANGED_ELMENT_TYPE_PROCESS:
				return "Process " + elementID;
			case KB_CHANGED_ELMENT_TYPE_CBR_ATTRIBUTE:
				return "CBR Attribute " + elementID;
			case KB_CHANGED_ELMENT_TYPE_CBR_CASE:
				return "CBR Case " + elementID;
			case KB_CHANGED_ELMENT_TYPE_CBR_CASE_BASE:
				return "CBR Case Base " + elementID;
			default:
				return String.valueOf(elementID);
			}
		}
		catch (Exception ex) {
			return String.valueOf(elementID) + " (" + ex + ")";
		}
	}

	private static boolean isActivation(final int kbChangedTypeID) {
		return kbChangedTypeID == KB_CHANGED_ELMENT_TYPE_GUIDELINE_ACTIVATION || kbChangedTypeID == KB_CHANGED_ELMENT_TYPE_PARAMETER_ACTIVATION;
	}

	private ModelUtil() {
	}
}
