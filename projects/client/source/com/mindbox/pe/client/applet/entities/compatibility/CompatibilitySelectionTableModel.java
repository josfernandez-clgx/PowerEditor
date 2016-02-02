package com.mindbox.pe.client.applet.entities.compatibility;

import java.util.List;

import com.mindbox.pe.client.ClientUtil;
import com.mindbox.pe.client.EntityModelCacheFactory;
import com.mindbox.pe.client.common.table.AbstractSelectionTableModel;
import com.mindbox.pe.model.GenericEntityType;
import com.mindbox.pe.model.assckey.GenericEntityCompatibilityData;

/**
 * Model for IDName Object selection table.
 * 
 * @since PowerEditor 1.0
 */
class CompatibilitySelectionTableModel extends AbstractSelectionTableModel<GenericEntityCompatibilityData> {

	private GenericEntityType type1, type2 = null;

	public CompatibilitySelectionTableModel(GenericEntityType type1, GenericEntityType type2) {
		this.type1 = type1;
		this.type2 = type2;
	}

	public Object getValueAt(int row, int col) {
		if (dataList == null || dataList.size() < row) {
			ClientUtil.getLogger().error("getValueAt: Invalid entities: " + dataList);
			return null;
		}

		GenericEntityCompatibilityData value = dataList.get(row);
		switch (col) {
		case 0:
			return (type1 == null ? "" : EntityModelCacheFactory.getInstance().getGenericEntityName(type1, value.getSourceID(), ""));
		case 1:
			return (type2 == null ? "" : EntityModelCacheFactory.getInstance().getGenericEntityName(type2, value.getAssociableID(), ""));
		case 2:
			return toDisplayString(value.getEffectiveDate());
		case 3:
			return toDisplayString(value.getExpirationDate());
		default:
			return value;
		}
	}

	@Override
	public String[] getColumnNames() {
		return new String[] { getColumnName(0), getColumnName(1), getColumnName(2), getColumnName(3) };
	}

	public String getColumnName(int col) {
		switch (col) {
		case 0:
			return (type1 == null ? "" : ClientUtil.getInstance().getLabel(type1));
		case 1:
			return (type2 == null ? "" : ClientUtil.getInstance().getLabel(type2));
		case 2:
			return ClientUtil.getInstance().getLabel("label.date.activation");
		case 3:
			return ClientUtil.getInstance().getLabel("label.date.expiration");
		default:
			return String.valueOf(col);
		}
	}

	void setData(GenericEntityType type1, GenericEntityType type2, List<GenericEntityCompatibilityData> data) {
		this.type1 = type1;
		this.type2 = type2;
		super.setDataList(data);
		fireTableStructureChanged();
	}

	/**
	 * @return Returns the type1.
	 */
	public GenericEntityType getType1() {
		return type1;
	}

	/**
	 * @return Returns the type2.
	 */
	public GenericEntityType getType2() {
		return type2;
	}
}