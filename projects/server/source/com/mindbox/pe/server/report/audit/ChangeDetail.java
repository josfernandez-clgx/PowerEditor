package com.mindbox.pe.server.report.audit;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import com.mindbox.pe.model.AbstractGrid;
import com.mindbox.pe.model.table.CategoryOrEntityValue;
import com.mindbox.pe.server.audit.AuditConstants;
import com.mindbox.pe.server.spi.audit.AuditKBDetail;
import com.mindbox.pe.server.spi.audit.AuditKBDetailData;

public class ChangeDetail {

	public static ChangeDetail valueOf(AuditKBDetail kbDetail) {
		return new ChangeDetail(kbDetail);
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

	public static String getModTypeDescription(int kbModTypeID) {
		switch (kbModTypeID) {
		case AuditConstants.KB_MOD_TYPE_GENERIC_ADD:
			return "Add";
		case AuditConstants.KB_MOD_TYPE_GENERIC_REMOVE:
			return "Remove";
		case AuditConstants.KB_MOD_TYPE_GENERIC_UPDATE:
			return "Update";
		case AuditConstants.KB_MOD_TYPE_GENERIC_IMPORT:
			return "Import";
		case AuditConstants.KB_MOD_TYPE_ADD_CONTEXT_ELEMENT:
			return "Add Context Element";
		case AuditConstants.KB_MOD_TYPE_REMOVE_CONTEXT_ELEMENT:
			return "Remove Context Element";
		case AuditConstants.KB_MOD_TYPE_ADD_GRID_ROW:
			return "Add Row";
		case AuditConstants.KB_MOD_TYPE_REMOVE_GRID_ROW:
			return "Remove Row";
		case AuditConstants.KB_MOD_TYPE_MODIFY_GRID_CELL:
			return "Modify Cell";
		case AuditConstants.KB_MOD_TYPE_MODIFY_ENTITY_PROPERTY:
			return "Modify Entity Property";
		case AuditConstants.KB_MOD_TYPE_ADD_ENTITY_CAT_RELATIONSHIP:
			return "Add Entity-To-Category Relationship";
		case AuditConstants.KB_MOD_TYPE_REMOVE_ENTITY_CAT_RELATIONSHIP:
			return "Remove Entity-To-Category Relationship";
		case AuditConstants.KB_MOD_TYPE_COPY_POLICIES:
			return "Copy Policies";
		case AuditConstants.KB_MOD_TYPE_CUTOVER_POLICIES:
			return "Cutover Policies";
		case AuditConstants.KB_MOD_TYPE_REMOVE_ALL_POLICIES:
			return "Remove All Policies";
		case AuditConstants.KB_MOD_TYPE_MODIFY_COMMENTS:
			return "Modify Activation Comments";
		case AuditConstants.KB_MOD_TYPE_MODIFY_EFFECTIVE_DATE:
			return "Modify Effective Date";
		case AuditConstants.KB_MOD_TYPE_MODIFY_EXPIRATION_DATE:
			return "Modify Expiration Date";
		case AuditConstants.KB_MOD_TYPE_MODIFY_STATUS:
			return "Modify Status";
		default:
			return "[Invalid KB Mod Type: " + kbModTypeID + "]";
		}
	}

	private int kbAuditDetailID;
	private int kbAuditID;
	private int kbModTypeID;
	private String description;
	private final Map<Integer, String> detailDataMap = new HashMap<Integer, String>(); /*<int,String>*/

	private ChangeDetail(AuditKBDetail kbDetail) {
		this.kbAuditDetailID = kbDetail.getKbAuditDetailID();
		this.kbAuditID = kbDetail.getKbAuditID();
		this.kbModTypeID = kbDetail.getKbModTypeID();
		this.description = kbDetail.getDescription();
		for (int i = 0; i < kbDetail.detailDataCount(); i++) {
			AuditKBDetailData detailData = kbDetail.getDetailData(i);
			detailDataMap.put(new Integer(detailData.getElementTypeID()), detailData.getElementValue());
		}
	}

	public void populateContext(AbstractGrid<?> abstractGrid) {
		for (Iterator<Integer> iter = detailDataMap.keySet().iterator(); iter.hasNext();) {
			Integer key = iter.next();
			if (key.intValue() == AuditConstants.KB_ELEMENT_TYPE_ENTITY_ID || key.intValue() == AuditConstants.KB_ELEMENT_TYPE_CATEGORY_ID) {
				CategoryOrEntityValue categoryOrEntityValue = CategoryOrEntityValue.valueOf(getDetailDataString(key));
				if (categoryOrEntityValue.isForEntity()) {
					abstractGrid.addGenericEntityID(categoryOrEntityValue.getEntityType(), categoryOrEntityValue.getId());
				}
				else {
					abstractGrid.addGenericCategoryID(categoryOrEntityValue.getEntityType(), categoryOrEntityValue.getId());
				}
			}
		}
	}

	public String getDescription() {
		return description;
	}

	public int getKbAuditDetailID() {
		return kbAuditDetailID;
	}

	public int getKbAuditID() {
		return kbAuditID;
	}

	public int getKbModTypeID() {
		return kbModTypeID;
	}

	public String getKbModTypeDescription() {
		return getModTypeDescription(kbModTypeID);
	}

	public Set<Map.Entry<Integer,String>> getDetailDataEntrySet() {
		return detailDataMap.entrySet();
	}

	public String getDetailDataString(int elementType) {
		return getDetailDataString(new Integer(elementType));
	}

	private String getDetailDataString(Integer elementType) {
		return detailDataMap.get(elementType);
	}
}
