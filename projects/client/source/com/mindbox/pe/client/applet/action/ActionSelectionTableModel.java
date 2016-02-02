package com.mindbox.pe.client.applet.action;

import com.mindbox.pe.client.ClientUtil;
import com.mindbox.pe.client.common.table.IDNameDescriptionObjectSelectionTableModel;
import com.mindbox.pe.model.TemplateUsageType;
import com.mindbox.pe.model.rule.ActionTypeDefinition;

/**
 * Model for IDName Object selection table.
 * 
 * @since PowerEditor 1.0
 */
public class ActionSelectionTableModel extends IDNameDescriptionObjectSelectionTableModel<ActionTypeDefinition> {


	/**
	 * Create new IDName Object selection table model with the specified column names.
	 * @param columnNames
	 */
	public ActionSelectionTableModel() {
		super(
				ClientUtil.getInstance().getLabel("label.name"),
				ClientUtil.getInstance().getLabel("label.desc"),
				ClientUtil.getInstance().getLabel("label.usage.types"));
	}

	@Override
	public Object getValueAt(int row, int col) {
		if (dataList == null || dataList.size() < row) {
			ClientUtil.printError("getValueAt: Invalid entities: " + dataList);
			return null;
		}

		ActionTypeDefinition value = dataList.get(row);
		switch (col) {
		case 0:
			return value.getName();
		case 1:
			return value.getDescription();
		case 2:
			return getUsageTypesString(value);
		default:
			return value;
		}
	}

	private String getUsageTypesString(ActionTypeDefinition action) {
		TemplateUsageType[] usages = (TemplateUsageType[]) action.getUsageTypes();
		StringBuffer buff = new StringBuffer();
		for (int i = 0; i < usages.length; i++) {
			if (i != 0) buff.append(", ");
			buff.append(usages[i].getDisplayName());
		}
		return buff.toString();
	}
}
