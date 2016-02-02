package com.mindbox.pe.client.applet.cbr;

import com.mindbox.pe.client.ClientUtil;
import com.mindbox.pe.client.common.table.AbstractSelectionTableModel;
import com.mindbox.pe.model.CBREnumeratedValue;

/**
 * Model for {@link com.mindbox.pe.client.applet.cbr.CBREnumeratedValueTable}.
 * 
 * @since PowerEditor 4.2
 */
public class CBREnumeratedValueTableModel extends AbstractSelectionTableModel<CBREnumeratedValue> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8995212598637325741L;

	private boolean isEditable = false;

	/**
	 * Create new CBR enumerated value table model .
	 */
	protected CBREnumeratedValueTableModel() {
		super(ClientUtil.getInstance().getLabel("label.cbr.enumerated.value"));
	}

	protected void setEditable(boolean value) {
		isEditable = value;
	}

	protected boolean isEditable() {
		return isEditable;
	}

	@Override
	public final boolean isCellEditable(int row, int col) {
		return isEditable;
	}

	@Override
	public final Object getValueAt(int i, int j) {
		if (dataList == null || dataList.size() < i) {
			ClientUtil.printError("getValueAt: Invalid entities: " + dataList);
			return null;
		}
		switch (j) {
		case 0:
			return dataList.get(i).getName();
		default:
			return dataList.get(i);
		}
	}

	@Override
	public void setValueAt(Object value, int row, int col) {
		if (row < dataList.size()) {
			if (value instanceof CBREnumeratedValue)
				dataList.set(row, (CBREnumeratedValue) value);
			else if (value instanceof String) dataList.set(row, new CBREnumeratedValue((String) value));
			fireTableDataChanged();
		}
	}
}
