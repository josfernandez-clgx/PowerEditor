package com.mindbox.pe.client.applet.guidelines.search;

import java.text.SimpleDateFormat;
import java.util.LinkedList;
import java.util.List;

import com.mindbox.pe.client.ClientUtil;
import com.mindbox.pe.client.common.table.AbstractCategoryEntityCacheTableModel;
import com.mindbox.pe.common.config.CategoryTypeDefinition;
import com.mindbox.pe.common.config.EntityTypeDefinition;
import com.mindbox.pe.common.config.UIConfiguration;
import com.mindbox.pe.model.DateSynonym;
import com.mindbox.pe.model.GenericEntityType;
import com.mindbox.pe.model.GuidelineReportData;
import com.mindbox.pe.model.table.CategoryOrEntityValues;

/**
 * Model for Guideline report table. This caches channels, investors, products, and categories for
 * displaying names of them.
 * 
 * @since PowerEditor 2.1.0
 */
public class TemplateReportTableModel extends AbstractCategoryEntityCacheTableModel<GuidelineReportData> {

	private static String[] initColumnNames() {
		if (columnNames == null) {
			List<String> nameList = new LinkedList<String>();
			List<GenericEntityType> typeList = new LinkedList<GenericEntityType>();
			nameList.add(ClientUtil.getInstance().getLabel("label.guideline.type"));
			nameList.add(ClientUtil.getInstance().getLabel("label.template"));

			EntityTypeDefinition[] entityTypes = ClientUtil.getEntityConfiguration().getEntityTypeDefinitions();
			for (int i = 0; i < entityTypes.length; i++) {
				if (entityTypes[i].useInContext()) {
					GenericEntityType type = GenericEntityType.forID(entityTypes[i].getTypeID());
					CategoryTypeDefinition catDef = ClientUtil.getEntityConfiguration().getCategoryDefinition(type);
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
		if (row < 0)
			return data;

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
			if (col < 0)
				return data;
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
			return toDisplayString(actDate, UIConfiguration.FORMAT_YYYY_MM_DD_TIME_SEC) + " - " + toDisplayString(expDate, UIConfiguration.FORMAT_YYYY_MM_DD_TIME_SEC);
		}
	}

}