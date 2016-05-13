package com.mindbox.pe.client.applet.cbr;

import java.util.ArrayList;
import java.util.List;

import com.mindbox.pe.client.ClientUtil;
import com.mindbox.pe.common.ui.AbstractSelectionTableModel;
import com.mindbox.pe.model.cbr.CBRAttribute;
import com.mindbox.pe.model.cbr.CBRAttributeValue;

/**
 * Model for {@link com.mindbox.pe.client.applet.cbr.CBRAttributeValueTable}.
 * 
 * @since PowerEditor 4.2
 */
public class CBRAttributeValueTableModel extends AbstractSelectionTableModel<CBRAttributeValue> {
	/**
	 * 
	 */
	private static final long serialVersionUID = -3951228734910107454L;

	private boolean isEditable = false;

	/**
	 * Create new CBR attribute value table model.
	 */
	protected CBRAttributeValueTableModel() {
		super(
				ClientUtil.getInstance().getLabel("label.cbr.attribute"),
				ClientUtil.getInstance().getLabel("label.cbr.value"),
				ClientUtil.getInstance().getLabel("label.cbr.special.match.contribution"),
				ClientUtil.getInstance().getLabel("label.cbr.special.mismatch.penalty"),
				ClientUtil.getInstance().getLabel("label.desc"));
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
		CBRAttributeValue av = dataList.get(i);
		switch (j) {
		case 0:
			return av.getAttribute();
		case 1:
			return av.getName();
		case 2:
			return new Integer(av.getMatchContribution());
		case 3:
			return new Integer(av.getMismatchPenalty());
		case 4:
			return av.getDescription();
		}
		return av;
	}

	@Override
	public void setDataList(List<CBRAttributeValue> data) {
		List<CBRAttributeValue> copyList = new ArrayList<CBRAttributeValue>();
		for (CBRAttributeValue value : data) {
			CBRAttributeValue newAv = new CBRAttributeValue();
			newAv.copyFrom(value);
			copyList.add(newAv);
		}
		super.setDataList(copyList);
	}

	@Override
	public final Class<?> getColumnClass(int i) {
		switch (i) {
		case 0:
			return CBRAttribute.class;
		case 1:
			return String.class;
		case 2:
			return Integer.class;
		case 3:
			return Integer.class;
		case 4:
			return String.class;
		}
		return String.class;
	}

	@Override
	public void setValueAt(Object value, int row, int col) {
		if (row < dataList.size()) {
			CBRAttributeValue av = dataList.get(row);
			switch (col) {
			case 0:
				av.setAttribute((CBRAttribute) value);
				break;
			case 1:
				av.setName((String) value);
				break;
			case 2:
				av.setMatchContribution(((Integer) value).intValue());
				break;
			case 3:
				av.setMismatchPenalty(((Integer) value).intValue());
				break;
			case 4:
				av.setDescription((String) value);
				break;
			}
			fireTableDataChanged();
		}
	}

}
