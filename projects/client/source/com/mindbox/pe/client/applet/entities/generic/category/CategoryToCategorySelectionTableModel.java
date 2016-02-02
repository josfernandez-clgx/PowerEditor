package com.mindbox.pe.client.applet.entities.generic.category;

import com.mindbox.pe.client.ClientUtil;
import com.mindbox.pe.client.EntityModelCacheFactory;
import com.mindbox.pe.client.common.table.AbstractSelectionTableModel;
import com.mindbox.pe.model.GenericCategory;
import com.mindbox.pe.model.assckey.MutableTimedAssociationKey;


/**
 * Model for category to category association.
 *
 * @since PowerEditor 5.1.0
 */
class CategoryToCategorySelectionTableModel extends AbstractSelectionTableModel<MutableTimedAssociationKey> {

	private final int categoryType;

	public CategoryToCategorySelectionTableModel(int categoryType) {
		super(
				ClientUtil.getInstance().getLabel("label.category.parent"),
				ClientUtil.getInstance().getLabel("label.date.activation"),
				ClientUtil.getInstance().getLabel("label.date.expiration"));
		this.categoryType = categoryType;
	}

	@Override
	public Object getValueAt(int row, int col) {
		if ((dataList == null) || (dataList.size() < row)) {
			ClientUtil.getLogger().error("getValueAt: Invalid entities: " + dataList);

			return null;
		}
		MutableTimedAssociationKey value = dataList.get(row);
		GenericCategory category = EntityModelCacheFactory.getInstance().getGenericCategory(categoryType, value.getAssociableID());
		switch (col) {
		case 0:
			return (category == null) ? "" : category.getName();

		case 1:
			return toDisplayString(value.getEffectiveDate());

		case 2:
			return toDisplayString(value.getExpirationDate());

		default:
			return value;
		}
	}
}
