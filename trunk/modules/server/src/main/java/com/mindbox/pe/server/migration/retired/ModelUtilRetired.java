package com.mindbox.pe.server.migration.retired;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.mindbox.pe.common.AuditConstants;
import com.mindbox.pe.common.DateUtil;
import com.mindbox.pe.model.DateSynonym;
import com.mindbox.pe.model.EnumValue;
import com.mindbox.pe.model.GenericCategory;
import com.mindbox.pe.model.GenericEntity;
import com.mindbox.pe.model.admin.Role;
import com.mindbox.pe.model.grid.AbstractGrid;
import com.mindbox.pe.model.grid.ParameterGrid;
import com.mindbox.pe.model.grid.ProductGrid;
import com.mindbox.pe.model.table.CategoryOrEntityValue;
import com.mindbox.pe.model.template.GridTemplate;
import com.mindbox.pe.model.template.ParameterTemplate;
import com.mindbox.pe.server.Util;
import com.mindbox.pe.server.cache.DateSynonymManager;
import com.mindbox.pe.server.cache.EntityManager;
import com.mindbox.pe.server.cache.GridManager;
import com.mindbox.pe.server.cache.GuidelineFunctionManager;
import com.mindbox.pe.server.cache.GuidelineTemplateManager;
import com.mindbox.pe.server.cache.ParameterManager;
import com.mindbox.pe.server.cache.ParameterTemplateManager;
import com.mindbox.pe.server.cache.SecurityCacheManager;
import com.mindbox.pe.server.model.ActivationDetail;
import com.mindbox.pe.server.model.User;
import com.mindbox.pe.server.report.ReportException;
import com.mindbox.pe.server.tag.WriteContextTag;
import com.mindbox.pe.xsd.audit.AuditReport;
import com.mindbox.pe.xsd.audit.ChangeDetail;
import com.mindbox.pe.xsd.audit.ChangeDetails;
import com.mindbox.pe.xsd.audit.ChangedElement;
import com.mindbox.pe.xsd.audit.EventType;
import com.mindbox.pe.xsd.extenm.EnumValueType;

@Deprecated
public class ModelUtilRetired {

	private static final Logger LOG = Logger.getLogger(ModelUtilRetired.class);

	public static ActivationDetail asActivationDetail(final AuditKBMasterRetired kbMaster) throws ReportException {
		if (isActivation(kbMaster.getKbChangedTypeID())) {
			final int kbChangedTypeID = kbMaster.getKbChangedTypeID();
			final int elementID = kbMaster.getElementID();
			try {
				boolean isParameter = kbChangedTypeID == AuditConstants.KB_CHANGED_ELMENT_TYPE_PARAMETER_ACTIVATION;
				if (isRemoved(kbMaster)) {
					final AuditKBDetailRetired auditKBDetail = kbMaster.getDetail(0);
					int templateID = Integer.parseInt(getDetailValue(auditKBDetail, AuditConstants.KB_ELEMENT_TYPE_TEMPLATE_ID));
					DateSynonym effDS = null;
					try {
						int dsID = Integer.parseInt(getDetailValue(auditKBDetail, AuditConstants.KB_ELEMENT_TYPE_EFFECTIVE_DATESYNONYM_ID));
						effDS = DateSynonymManager.getInstance().getDateSynonym(dsID);
					}
					catch (Exception ex) {
						// ignore
					}
					DateSynonym expDS = null;
					try {
						int dsID = Integer.parseInt(getDetailValue(auditKBDetail, AuditConstants.KB_ELEMENT_TYPE_EXPIRATION_DATESYNONYM_ID));
						expDS = DateSynonymManager.getInstance().getDateSynonym(dsID);
					}
					catch (Exception ex) {
						// ignore
					}
					String usageType;
					String templateName;
					AbstractGrid<?> grid;
					if (isParameter) {
						ParameterTemplate template = ParameterTemplateManager.getInstance().getTemplate(templateID);
						templateName = (template == null ? "Template ID " + templateID : template.getAuditDescription());
						usageType = AuditConstants.PARAMETER_USAGE_TYPE;
						ParameterGrid parameterGrid = new ParameterGrid(elementID, templateID, effDS, expDS);
						parameterGrid.setTemplate(template);
						grid = parameterGrid;
					}
					else {
						GridTemplate template = GuidelineTemplateManager.getInstance().getTemplate(templateID);
						usageType = template.getUsageType().getDisplayName();
						templateName = (template == null ? "Template ID " + templateID : template.getAuditDescription());
						grid = new ProductGrid(elementID, template, effDS, expDS);
					}
					populateContext(auditKBDetail, grid);

					return ActivationDetail.createInstance(
							usageType,
							templateID,
							templateName,
							effDS,
							expDS,
							grid,
							isParameter,
							getDetailValue(auditKBDetail, AuditConstants.KB_ELEMENT_TYPE_STATUS),
							"");
				}
				else if (isParameter) {
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

	public static AuditReport.AuditEvent asAuditReportAuditEvent(final AuditEventRetired auditEvent) throws ReportException {
		final AuditReport.AuditEvent auditReportAuditEvent = new AuditReport.AuditEvent();
		auditReportAuditEvent.setType(EventType.fromValue(auditEvent.getAuditType().getName()));
		auditReportAuditEvent.setAuditID(Integer.valueOf(auditEvent.getAuditID()));
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

	public static ChangedElement asChangedElement(final AuditKBMasterRetired kbMaster) throws ReportException {
		final ChangedElement changedElement = new ChangedElement();
		changedElement.setKbAuditID(Integer.valueOf(kbMaster.getKbAuditID()));
		changedElement.setType(getChangedTypeDescription(kbMaster.getKbChangedTypeID()));

		final ActivationDetail activationDetail = asActivationDetail(kbMaster);
		if (activationDetail != null) {
			changedElement.setActivationDate(DateUtil.toXMLGregorianCalendar(activationDetail.getEffectiveDate()));
			changedElement.setActivationDateName(activationDetail.getEffectiveDateName());
			if (activationDetail.getContextContainer() != null) {
				changedElement.setContext(activationDetail.getContextContainer().toString());
			}
			changedElement.setExpirationDate(DateUtil.toXMLGregorianCalendar(activationDetail.getExpirationDate()));
			changedElement.setExpirationDateName(activationDetail.getExpirationDateName());
			changedElement.setTemplateName(activationDetail.getTemplateName());
			changedElement.setUsageType(activationDetail.getUsageType());
			changedElement.setName(activationDetail.getAuditName());
		}
		else {
			changedElement.setName(getElementName(kbMaster.getKbChangedTypeID(), kbMaster.getElementID()));
		}
		return changedElement;
	}

	public static ChangeDetail asChangeDetail(final AuditKBDetailRetired kbDetail) {
		final ChangeDetail changeDetail = new ChangeDetail();
		changeDetail.setChangeType(AuditConstants.getModTypeDescription(kbDetail.getKbModTypeID()));
		changeDetail.setChangeDescription(kbDetail.getDescription());
		for (int i = 0; i < kbDetail.detailDataCount(); i++) {
			final AuditKBDetailDataRetired auditKBDetailData = kbDetail.getDetailData(i);
			setChangeDetailElementName(auditKBDetailData.getElementTypeID(), auditKBDetailData.getElementValue(), changeDetail);
		}
		return changeDetail;
	}

	public static ChangeDetails asChangeDetails(final AuditKBMasterRetired auditKBMaster) throws ReportException {
		final ChangeDetails changeDetails = new ChangeDetails();

		changeDetails.setChangedElement(asChangedElement(auditKBMaster));

		for (int i = 0; i < auditKBMaster.detailCount(); i++) {
			final AuditKBDetailRetired auditKBDetail = auditKBMaster.getDetail(i);
			changeDetails.getChangeDetail().add(asChangeDetail(auditKBDetail));
		}
		return changeDetails;
	}

	public static EnumValue asEnumValue(final EnumValueType enumValueType) {
		EnumValue enumValue = new EnumValue();
		enumValue.setDeployValue(enumValueType.getValue());
		enumValue.setDisplayLabel(enumValueType.getDisplayLabel());
		enumValue.setInactive((enumValueType.isInactive() == null ? "false" : String.valueOf(enumValueType.isInactive())));
		return enumValue;
	}

	private static String getChangedTypeDescription(int typeID) {
		switch (typeID) {
		case AuditConstants.KB_CHANGED_ELMENT_TYPE_GUIDELINE_ACTIVATION:
			return "Guideline Activation";
		case AuditConstants.KB_CHANGED_ELMENT_TYPE_GUIDELINE_TEMPLATE:
			return "Guideline Template";
		case AuditConstants.KB_CHANGED_ELMENT_TYPE_ENTITY:
			return "Entity";
		case AuditConstants.KB_CHANGED_ELMENT_TYPE_CATEGORY:
			return "Category";
		case AuditConstants.KB_CHANGED_ELMENT_TYPE_ENTITY_CAT_RELATIONSHIP:
			return "Entity-To-Category Relationship";
		case AuditConstants.KB_CHANGED_ELMENT_TYPE_PARAMETER_ACTIVATION:
			return "Parameter Activation";
		case AuditConstants.KB_CHANGED_ELMENT_TYPE_PARAMETER_TEMPLATE:
			return "Parameter Template";
		case AuditConstants.KB_CHANGED_ELMENT_TYPE_ENTITY_COMPATIBILITY:
			return "Entity Compatibility";
		case AuditConstants.KB_CHANGED_ELMENT_TYPE_DATE_SYNONYM:
			return "Date Synonym";
		case AuditConstants.KB_CHANGED_ELMENT_TYPE_GUIDELINE_ACTION:
			return "Guideline Action";
		case AuditConstants.KB_CHANGED_ELMENT_TYPE_TEST_CONDITION:
			return "Test Condition";
		case AuditConstants.KB_CHANGED_ELMENT_TYPE_ROLE:
			return "Role";
		case AuditConstants.KB_CHANGED_ELMENT_TYPE_USER:
			return "User";
		case AuditConstants.KB_CHANGED_ELMENT_TYPE_PHASE:
			return "Phase";
		case AuditConstants.KB_CHANGED_ELMENT_TYPE_PROCESS:
			return "Process";
		case AuditConstants.KB_CHANGED_ELMENT_TYPE_CBR_ATTRIBUTE:
			return "CBR Attribute";
		case AuditConstants.KB_CHANGED_ELMENT_TYPE_CBR_CASE:
			return "CBR Case";
		case AuditConstants.KB_CHANGED_ELMENT_TYPE_CBR_CASE_BASE:
			return "CBR Case Base";
		default:
			return "[Invalid KBChanged Type: " + typeID + ']';
		}
	}

	private static String getDetailValue(final AuditKBDetailRetired auditKBDetail, int elementTypeID) {
		for (int i = 0; i < auditKBDetail.detailDataCount(); i++) {
			final AuditKBDetailDataRetired auditKBDetailData = auditKBDetail.getDetailData(i);
			if (auditKBDetailData.getElementTypeID() == elementTypeID) {
				return auditKBDetailData.getElementValue();
			}
		}
		return null;
	}

	private static String getElementName(final int kbChangedTypeID, final int elementID) {
		try {
			switch (kbChangedTypeID) {
			case AuditConstants.KB_CHANGED_ELMENT_TYPE_GUIDELINE_TEMPLATE: {
				GridTemplate template = GuidelineTemplateManager.getInstance().getTemplate(elementID);
				return template == null ? "Guideline Template " + elementID : template.getAuditName();
			}
			case AuditConstants.KB_CHANGED_ELMENT_TYPE_ENTITY: {
				GenericEntity entity = EntityManager.getInstance().findFirstEntity(elementID);
				return entity == null ? "Entity " + elementID : entity.getAuditName();
			}
			case AuditConstants.KB_CHANGED_ELMENT_TYPE_CATEGORY: {
				GenericCategory category = EntityManager.getInstance().findFirstCategory(elementID);
				return category == null ? "Category " + elementID : category.getAuditName();
			}
			case AuditConstants.KB_CHANGED_ELMENT_TYPE_ENTITY_CAT_RELATIONSHIP: {
				return "Entity-To-Category Relationship";
			}
			case AuditConstants.KB_CHANGED_ELMENT_TYPE_PARAMETER_TEMPLATE: {
				return ParameterTemplateManager.getInstance().getTemplate(elementID).getAuditName();
			}
			case AuditConstants.KB_CHANGED_ELMENT_TYPE_ENTITY_COMPATIBILITY: {
				return "Entity Compatibility " + elementID;
			}
			case AuditConstants.KB_CHANGED_ELMENT_TYPE_DATE_SYNONYM: {
				return DateSynonymManager.getInstance().getDateSynonym(elementID).getAuditName();
			}
			case AuditConstants.KB_CHANGED_ELMENT_TYPE_GUIDELINE_ACTION: {
				return GuidelineFunctionManager.getInstance().getActionTypeDefinition(elementID).getAuditName();
			}
			case AuditConstants.KB_CHANGED_ELMENT_TYPE_TEST_CONDITION: {
				return GuidelineFunctionManager.getInstance().getTestTypeDefinition(elementID).getAuditName();
			}
			case AuditConstants.KB_CHANGED_ELMENT_TYPE_ROLE: {
				Role role = SecurityCacheManager.getInstance().getRole(elementID);
				return role == null ? "Role " + elementID : role.getAuditName();
			}
			case AuditConstants.KB_CHANGED_ELMENT_TYPE_USER: {
				User user = SecurityCacheManager.getInstance().findUserByID(elementID);
				return user == null ? "User " + elementID : user.getUserID();
			}
			case AuditConstants.KB_CHANGED_ELMENT_TYPE_PHASE:
				return "Phase " + elementID;
			case AuditConstants.KB_CHANGED_ELMENT_TYPE_PROCESS:
				return "Process " + elementID;
			case AuditConstants.KB_CHANGED_ELMENT_TYPE_CBR_ATTRIBUTE:
				return "CBR Attribute " + elementID;
			case AuditConstants.KB_CHANGED_ELMENT_TYPE_CBR_CASE:
				return "CBR Case " + elementID;
			case AuditConstants.KB_CHANGED_ELMENT_TYPE_CBR_CASE_BASE:
				return "CBR Case Base " + elementID;
			default:
				return String.valueOf(elementID);
			}
		}
		catch (Exception ex) {
			return String.valueOf(elementID) + " (" + ex + ")";
		}
	}

	public static String getElementTypeDescription(int elementTypeID) {
		switch (elementTypeID) {
		case AuditConstants.KB_ELEMENT_TYPE_ENTITY_ID:
			return "Entity";
		case AuditConstants.KB_ELEMENT_TYPE_CATEGORY_ID:
			return "Category";
		case AuditConstants.KB_ELEMENT_TYPE_BEFORE_VALUE:
			return "Before Value";
		case AuditConstants.KB_ELEMENT_TYPE_AFTER_VALUE:
			return "New Value";
		case AuditConstants.KB_ELEMENT_TYPE_ROW_NUMBER:
			return "Row Number";
		case AuditConstants.KB_ELEMENT_TYPE_COLUMN_NAME:
			return "Column Name";
		case AuditConstants.KB_ELEMENT_TYPE_TEMPLATE_ID:
			return "Template";
		case AuditConstants.KB_ELEMENT_TYPE_SOURCE_ID:
			return "Source";
		case AuditConstants.KB_ELEMENT_TYPE_EFFECTIVE_DATESYNONYM_ID:
			return "Effective Date";
		case AuditConstants.KB_ELEMENT_TYPE_EXPIRATION_DATESYNONYM_ID:
			return "Expiration Date";
		default:
			return "[Invalid element type id " + elementTypeID + "]";
		}
	}

	private static boolean isActivation(final int kbChangedTypeID) {
		return kbChangedTypeID == AuditConstants.KB_CHANGED_ELMENT_TYPE_GUIDELINE_ACTIVATION || kbChangedTypeID == AuditConstants.KB_CHANGED_ELMENT_TYPE_PARAMETER_ACTIVATION;
	}

	public static boolean isRemoved(final AuditKBMasterRetired kbMaster) {
		if (kbMaster.detailCount() == 1) {
			return kbMaster.getDetail(0).getKbModTypeID() == AuditConstants.KB_MOD_TYPE_GENERIC_REMOVE;
		}
		else {
			return false;
		}
	}

	private static void populateContext(final AuditKBDetailRetired auditKBDetail, final AbstractGrid<?> abstractGrid) {
		for (int i = 0; i < auditKBDetail.detailDataCount(); i++) {
			final AuditKBDetailDataRetired auditKBDetailData = auditKBDetail.getDetailData(i);
			if (auditKBDetailData.getElementTypeID() == AuditConstants.KB_ELEMENT_TYPE_ENTITY_ID || auditKBDetailData.getElementTypeID() == AuditConstants.KB_ELEMENT_TYPE_CATEGORY_ID) {
				CategoryOrEntityValue categoryOrEntityValue = CategoryOrEntityValue.valueOf(auditKBDetailData.getElementValue());
				if (categoryOrEntityValue.isForEntity()) {
					abstractGrid.addGenericEntityID(categoryOrEntityValue.getEntityType(), categoryOrEntityValue.getId());
				}
				else {
					abstractGrid.addGenericCategoryID(categoryOrEntityValue.getEntityType(), categoryOrEntityValue.getId());
				}
			}
		}
	}

	private static void setChangeDetailElementName(final int elementTypeID, final String detailString, final ChangeDetail changeDetail) {
		switch (elementTypeID) {
		case AuditConstants.KB_ELEMENT_TYPE_BEFORE_VALUE:
			changeDetail.setPreviousValue(detailString);
			break;
		case AuditConstants.KB_ELEMENT_TYPE_AFTER_VALUE:
			changeDetail.setNewValue(detailString);
			break;
		case AuditConstants.KB_ELEMENT_TYPE_ROW_NUMBER:
			changeDetail.setRowNumber(Integer.valueOf(detailString));
			break;
		case AuditConstants.KB_ELEMENT_TYPE_COLUMN_NAME:
			changeDetail.setColumnName(detailString);
			break;
		case AuditConstants.KB_ELEMENT_TYPE_TEMPLATE_ID:
			changeDetail.setTemplate(detailString);
			break;
		case AuditConstants.KB_ELEMENT_TYPE_SOURCE_ID:
			changeDetail.setSource(detailString);
			break;
		case AuditConstants.KB_ELEMENT_TYPE_EFFECTIVE_DATESYNONYM_ID:
			try {
				int dsID = Integer.parseInt(detailString);
				final DateSynonym ds = DateSynonymManager.getInstance().getDateSynonym(dsID);
				changeDetail.setEffectiveDate((ds == null ? "Date Synonym " + dsID : ds.getName()));
			}
			catch (Exception ex) {
				changeDetail.setEffectiveDate("Date Synonym " + Util.xmlify(detailString));
			}
			break;
		case AuditConstants.KB_ELEMENT_TYPE_EXPIRATION_DATESYNONYM_ID:
			try {
				int dsID = Integer.parseInt(detailString);
				final DateSynonym ds = DateSynonymManager.getInstance().getDateSynonym(dsID);
				changeDetail.setExpirationDate((ds == null ? "Date Synonym " + dsID : ds.getName()));
			}
			catch (Exception ex) {
				changeDetail.setExpirationDate("Date Synonym " + Util.xmlify(detailString));
			}
			break;
		case AuditConstants.KB_ELEMENT_TYPE_ENTITY_ID:
		case AuditConstants.KB_ELEMENT_TYPE_CATEGORY_ID:
			changeDetail.setContextElement(WriteContextTag.toContextTagValue(CategoryOrEntityValue.valueOf(detailString)));
			break;
		}
	}


	private ModelUtilRetired() {
	}
}
