package com.mindbox.pe.client.applet.parameters;

import java.util.LinkedList;
import java.util.List;

import com.mindbox.pe.client.ClientUtil;
import com.mindbox.pe.client.common.table.AbstractCategoryEntityCacheTableModel;
import com.mindbox.pe.common.config.CategoryTypeDefinition;
import com.mindbox.pe.common.config.EntityTypeDefinition;
import com.mindbox.pe.model.GenericEntityType;
import com.mindbox.pe.model.ParameterGrid;
import com.mindbox.pe.model.table.CategoryOrEntityValues;

public class ParameterContextTableModel extends AbstractCategoryEntityCacheTableModel<ParameterGrid> {

	private static String[] initColumnNames() {
		if (columnNames == null) {
			List<String> nameList = new LinkedList<String>();
			List<GenericEntityType> typeList = new LinkedList<GenericEntityType>();

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

			nameList.add(ClientUtil.getInstance().getLabel("label.active.on"));
			nameList.add(ClientUtil.getInstance().getLabel("label.expire.on"));
			nameList.add(ClientUtil.getInstance().getLabel("label.status"));

			columnNames = nameList.toArray(new String[0]);
			extraTypes = typeList.toArray(new GenericEntityType[0]);
		}
		return columnNames;
	}

	private static String[] columnNames = null;
	private static GenericEntityType[] extraTypes = null;

	public ParameterContextTableModel() {
		super(initColumnNames());
	}

	public synchronized void addParameterGrid(ParameterGrid context) {
		super.addData(context);
	}

	public synchronized void removeParameterGrid(ParameterGrid context) {
		int index = -1;
		for (int i = 0; i < dataList.size(); i++) {
			if (context.getID() == dataList.get(i).getID()) {
				index = i;
				break;
			}
		}
		if (index != -1) {
			dataList.remove(index);
		}
		refreshData();
	}

	@Override
	public Object getValueAt(int i, int col) {
		if (dataList.size() < i) {
			ClientUtil.printError("getValueAt: Invalid gridSummaryList: " + dataList);
			return null;
		}
		ParameterGrid context = dataList.get(i);
		if (col < 0)
			return context;

		if (col < extraTypes.length) {
			GenericEntityType type = (GenericEntityType) extraTypes[col];
			return getGenericEntityContext(type, context.extractGuidelineContext());
		}
		else if (col == extraTypes.length) {
			return toDisplayString(context.getEffectiveDate());
		}
		else if (col == extraTypes.length + 1) {
			return toDisplayString(context.getExpirationDate());
		}
		else if (col == extraTypes.length + 2) {
			return ClientUtil.getStatusDisplayLabel(context.getStatus());
		}
		return context;
	}

	@Override
	public Class<?> getColumnClass(int col) {
		if (col >= 0 && col < extraTypes.length) {
			return CategoryOrEntityValues.class;
		}
		else {
			return String.class;
		}
	}

}
