package com.mindbox.pe.client.applet.entities.generic.category;

import com.mindbox.pe.client.ClientUtil;
import com.mindbox.pe.client.common.table.AbstractSelectionTableModel;

/**
 * Model for category to entity association.
 * 
 * @since PowerEditor 5.1.0
 */
class CategoryToEntitySelectionTableModel extends AbstractSelectionTableModel<CategoryToEntityAssociationData> {

	public CategoryToEntitySelectionTableModel() {
		super(
				ClientUtil.getInstance().getLabel("label.entity"),
				ClientUtil.getInstance().getLabel("label.date.activation"),
				ClientUtil.getInstance().getLabel("label.date.expiration"));
	}

	@Override
	public Object getValueAt(int row, int col) {
		if (dataList == null || dataList.size() < row) {
			ClientUtil.getLogger().error("getValueAt: Invalid entities: " + dataList);
			return null;
		}

		CategoryToEntityAssociationData value = dataList.get(row);
		switch (col) {
		case 0:
			return value.getEntity() == null ? "" : value.getEntity().getName();
		case 1:
			return toDisplayString(value.getAssociationKey().getEffectiveDate());
		case 2:
			return toDisplayString(value.getAssociationKey().getExpirationDate());
		default:
			return value;
		}
	}
}