package com.mindbox.pe.client.applet.guidelines.search;

import java.text.SimpleDateFormat;
import java.util.LinkedList;
import java.util.List;

import com.mindbox.pe.client.ClientUtil;
import com.mindbox.pe.client.common.table.AbstractCategoryEntityCacheTableModel;
import com.mindbox.pe.common.config.ConfigUtil;
import com.mindbox.pe.model.Constants;
import com.mindbox.pe.model.DateSynonym;
import com.mindbox.pe.model.GenericEntityType;
import com.mindbox.pe.model.GuidelineReportData;
import com.mindbox.pe.model.table.CategoryOrEntityValues;
import com.mindbox.pe.xsd.config.CategoryType;
import com.mindbox.pe.xsd.config.EntityType;

/**
 * Model for Guideline report table. This caches channels, investors, products, and categories for
 * displaying names of them.
 * 
 * @since PowerEditor 2.1.0
 */
public class TemplateReportTableModel extends AbstractCategoryEntityCacheTableModel<GuidelineReportData> {
	/**
	 * 
	 */
	private static final long serialVersionUID = -3951228734910107454L;

	private static String[] initColumnNames() {
		if (columnNames == null) {
			List<String> nameList = new LinkedList<String>();
			List<GenericEntityType> typeList = new LinkedList<GenericEntityType>();
			nameList.add(ClientUtil.getInstance().getLabel("label.guideline.type"));
			nameList.add(ClientUtil.getInstance().getLabel("label.template"));

			for (final EntityType entityType : ClientUtil.getEntityConfigHelper().getEntityTypeDefinitions()) {
				if (ConfigUtil.isUseInContext(entityType)) {
					GenericEntityType type = GenericEntityType.forID(entityType.getTypeID().intValue());
					CategoryType catDef = ClientUtil.getEntityConfigHelper().getCategoryDefinition(type);
					if (catDef != null) {
						nameList.add(ClientUtil.getInstance().getLabel(type, catDef));
					}
					else {
						nameList.add(ClientUtil.getInstance().getLabel(type));
					}
					typeList.add(type);
				}
			}
			nameList.add(ClientUtil.getInstance().getLabel("label.date.activation"));
			nameList.add(ClientUtil.getInstance().getLabel("label.created"));
			nameList.add(ClientUtil.getInstance().getLabel("label.status"));
			nameList.add(ClientUtil.getInstance().getLabel("label.row"));
			columnNames = nameList.toArray(new String[0]);
			extraTypes = typeList.toArray(new GenericEntityType[0]);
		}
		return columnNames;
	}

	private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
	private static String[] columnNames = null;
	private static GenericEntityType[] extraTypes = null;

	/**
	 * Create new IDName Object selection table model with the specified column names.
	 */
	public TemplateReportTableModel() {
		super(initColumnNames());
	}

	@Override
	public Class<?> getColumnClass(int col) {
		if (col > 1 && col < extraTypes.length + 2) {
			return CategoryOrEntityValues.class;
		}
		else {
			return String.class;
		}
	}

	@Override
	public Object getValueAt(int row, int col) {
		if (dataList == null || dataList.size() < row) {
			ClientUtil.printError("getValueAt: Invalid entities: " + dataList);
			return null;
		}

		GuidelineReportData data = dataList.get(row);
		if (row < 0) return data;

		switch (col) {
		case 0: {
			if (data.getDataType().equals("ParameterGrid")) {
				return "";
			}
			else {
				return (data.getUsageType() != null ? data.getUsageType().getDisplayName() : null);
			}
		}
		case 1:
			return data.getName() + (data.getTemplateVersion() == null ? "" : " (" + data.getTemplateVersion() + ")");

		default:
			if (col < 0) return data;
			if (col < extraTypes.length + 2) {
				GenericEntityType type = (GenericEntityType) extraTypes[col - 2];
				if (data.getDataType().equals("GuidelineGrid")) {
					return getGenericEntityContext(type, data.getContext());
				}
				else if (data.getDataType().equals("ParameterGrid")) {
					return getGenericEntityContext(type, data.getParameterGrid().extractGuidelineContext());
				}
				else {
					return null;
				}
			}
			else if (col == extraTypes.length + 2) {
				return toActivationStr(data.getActivationDate(), data.getExpirationDate());
			}
			else if (col == extraTypes.length + 3) {
				return (data.getCreationDate() == null ? "" : dateFormat.format(data.getCreationDate()));
			}
			else if (col == extraTypes.length + 4) {
				return ClientUtil.getStatusDisplayLabel(data.getStatus());
			}
			else if (col == extraTypes.length + 5) {
				return data.getMatchingRowNumbers();
			}

			return data;
		}
	}

	private String toActivationStr(DateSynonym actDate, DateSynonym expDate) {
		if (actDate == null && expDate == null) {
			return "";
		}
		else {
			// TT 1982  format yyyy-mm-dd-time-sec
			return toDisplayString(actDate, Constants.THREADLOCAL_FORMAT_YYYY_MM_DD_TIME_SEC) + " - " + toDisplayString(expDate, Constants.THREADLOCAL_FORMAT_YYYY_MM_DD_TIME_SEC);
		}
	}

}