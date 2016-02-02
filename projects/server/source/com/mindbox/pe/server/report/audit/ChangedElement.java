package com.mindbox.pe.server.report.audit;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.log4j.Logger;

import com.mindbox.pe.model.AbstractGrid;
import com.mindbox.pe.model.DateSynonym;
import com.mindbox.pe.model.GenericCategory;
import com.mindbox.pe.model.GenericEntity;
import com.mindbox.pe.model.GridTemplate;
import com.mindbox.pe.model.ParameterGrid;
import com.mindbox.pe.model.ParameterTemplate;
import com.mindbox.pe.model.ProductGrid;
import com.mindbox.pe.model.admin.Role;
import com.mindbox.pe.server.audit.AuditConstants;
import com.mindbox.pe.server.cache.DateSynonymManager;
import com.mindbox.pe.server.cache.EntityManager;
import com.mindbox.pe.server.cache.GridManager;
import com.mindbox.pe.server.cache.GuidelineFunctionManager;
import com.mindbox.pe.server.cache.GuidelineTemplateManager;
import com.mindbox.pe.server.cache.ParameterManager;
import com.mindbox.pe.server.cache.ParameterTemplateManager;
import com.mindbox.pe.server.cache.SecurityCacheManager;
import com.mindbox.pe.server.model.User;
import com.mindbox.pe.server.report.ReportException;
import com.mindbox.pe.server.spi.audit.AuditKBMaster;

public class ChangedElement {

	public static ChangedElement valueOf(AuditKBMaster kbMaster) {
		return new ChangedElement(kbMaster);
	}

	public static String getChangedTypeDescription(int typeID) {
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

	private final int kbAuditID;
	private final int kbChangedTypeID;
	private final int elementID;
	private final List<ChangeDetail> detailList;

	private ChangedElement(AuditKBMaster kbMaster) {
		this.kbAuditID = kbMaster.getKbAuditID();
		this.kbChangedTypeID = kbMaster.getKbChangedTypeID();
		this.elementID = kbMaster.getElementID();
		List<ChangeDetail> tempList = new ArrayList<ChangeDetail>();
		for (int i = 0; i < kbMaster.detailCount(); i++) {
			tempList.add(ChangeDetail.valueOf(kbMaster.getDetail(i)));
		}
		// Make sure the list is sorted by kb detail id
		Collections.sort(tempList, new Comparator<ChangeDetail>() {
			public int compare(ChangeDetail cd1, ChangeDetail cd2) {
				if (cd1 == cd2) return 0;
				return new Integer(cd1.getKbAuditDetailID()).compareTo(new Integer(cd2.getKbAuditDetailID()));
			}
		});
		this.detailList = Collections.unmodifiableList(tempList);
	}

	public int getElementID() {
		return elementID;
	}

	public int getKbAuditID() {
		return kbAuditID;
	}

	public int getKbChangedTypeID() {
		return kbChangedTypeID;
	}

	public List<ChangeDetail> getChangeDetails() {
		return detailList;
	}

	public boolean isActivation() {
		return kbChangedTypeID == AuditConstants.KB_CHANGED_ELMENT_TYPE_GUIDELINE_ACTIVATION
				|| kbChangedTypeID == AuditConstants.KB_CHANGED_ELMENT_TYPE_PARAMETER_ACTIVATION;
	}

	public ActivationDetail getActivationDetail() throws ReportException {
		if (isActivation()) {
			try {
			boolean isParameter = kbChangedTypeID == AuditConstants.KB_CHANGED_ELMENT_TYPE_PARAMETER_ACTIVATION;
			if (isRemoved()) {
				ChangeDetail detail = detailList.get(0);
				int templateID = Integer.parseInt(detail.getDetailDataString(AuditConstants.KB_ELEMENT_TYPE_TEMPLATE_ID));
				DateSynonym effDS = null;
				try {
					int dsID = Integer.parseInt(detail.getDetailDataString(AuditConstants.KB_ELEMENT_TYPE_EFFECTIVE_DATESYNONYM_ID));
					effDS = DateSynonymManager.getInstance().getDateSynonym(dsID);
				}
				catch (Exception ex) {
					// ignore
				}
				DateSynonym expDS = null;
				try {
					int dsID = Integer.parseInt(detail.getDetailDataString(AuditConstants.KB_ELEMENT_TYPE_EXPIRATION_DATESYNONYM_ID));
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
				detail.populateContext(grid);
				return ActivationDetail.createInstance(
						usageType,
						templateID,
						templateName,
						effDS,
						expDS,
						grid,
						isParameter,
						detail.getDetailDataString(AuditConstants.KB_ELEMENT_TYPE_STATUS));
			}
			else if (isParameter) {
				ParameterGrid grid = ParameterManager.getInstance().getGrid(elementID);
				return ActivationDetail.createInstance(AuditConstants.PARAMETER_USAGE_TYPE, grid, elementID, true);
			}
			else {
				ProductGrid grid = GridManager.getInstance().getProductGrid(elementID);
				return ActivationDetail.createInstance(
						(grid == null ? "UNKNOWN" : grid.getTemplate().getUsageType().getDisplayName()),
						grid,
						elementID,
						false);
			}
			}
			catch (Exception ex) {
				Logger.getLogger(getClass()).error("Failed to get activation detail", ex);
				throw new ReportException(ex.getMessage());
			}
		}
		else {
			return null;
		}
	}

	public String getKbChangedTypeDescription() {
		return getChangedTypeDescription(kbChangedTypeID);
	}

	public boolean isRemoved() {
		if (detailList.size() == 1) {
			ChangeDetail detail = detailList.get(0);
			return detail.getKbModTypeID() == AuditConstants.KB_MOD_TYPE_GENERIC_REMOVE;
		}
		else {
			return false;
		}
	}

	public String getElementName() {
		// TODO Kim: if action was delete, do this differently
		try {
			switch (kbChangedTypeID) {
			case AuditConstants.KB_CHANGED_ELMENT_TYPE_GUIDELINE_ACTIVATION: {
				return getActivationDetail().getAuditName();
			}
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
			case AuditConstants.KB_CHANGED_ELMENT_TYPE_PARAMETER_ACTIVATION: {
				return getActivationDetail().getAuditName();
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
}
