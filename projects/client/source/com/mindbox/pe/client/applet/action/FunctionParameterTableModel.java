package com.mindbox.pe.client.applet.action;

import java.util.Iterator;
import java.util.List;

import com.mindbox.pe.client.ClientUtil;
import com.mindbox.pe.client.common.table.AbstractSelectionTableModel;
import com.mindbox.pe.model.DeployType;
import com.mindbox.pe.model.rule.FunctionParameterDefinition;

/**
 * Model for {@link com.mindbox.pe.client.applet.action.FunctionParameterTable}.
 * 
 * @since PowerEditor 3.2
 */
public class FunctionParameterTableModel extends AbstractSelectionTableModel<FunctionParameterDefinition> {

	private boolean isEditable = false;

	/**
	 * Create new function parameter table model.
	 */
	protected FunctionParameterTableModel() {
		super(
				ClientUtil.getInstance().getLabel("label.parameter.no"),
				ClientUtil.getInstance().getLabel("label.name"),
				ClientUtil.getInstance().getLabel("label.deploy.type"));
	}

	protected void setEditable(boolean value) {
		isEditable = value;
	}

	protected boolean isEditable() {
		return isEditable;
	}

	@Override
	public final boolean isCellEditable(int row, int col) {
		return col > 0 && isEditable;
	}

	@Override
	public final Object getValueAt(int i, int j) {
		if (dataList == null || dataList.size() < i) {
			ClientUtil.printError("getValueAt: Invalid entities: " + dataList);
			return null;
		}
		FunctionParameterDefinition apd = dataList.get(i);
		switch (j) {
		case 0:
			return new Integer(i + 1);
		case 1:
			return apd.getName();
		case 2:
			if (apd.getDeployType() == null)
				return null;
			else
				return apd.getDeployType().getName();
		}
		return apd;
	}

	@Override
	public final void setDataList(List<FunctionParameterDefinition> data) {
		dataList.clear();
		Iterator<FunctionParameterDefinition> it = data.iterator();
		while (it.hasNext()) {
			FunctionParameterDefinition apd = it.next();
			FunctionParameterDefinition newApd = new FunctionParameterDefinition();
			newApd.copyFrom(apd);
			dataList.add(newApd);
		}
		fireTableDataChanged();
	}

	@Override
	public final Class<?> getColumnClass(int i) {
		switch (i) {
		case 0:
			return Integer.class;
		case 1:
			return String.class;
		case 2:
			return String.class;
		}
		return String.class;
	}

	@Override
	public void setValueAt(Object value, int row, int col) {
		if (row < dataList.size()) {
			FunctionParameterDefinition apd = dataList.get(row);
			switch (col) {
			case 0:
				break;
			case 1:
				apd.setName((String) value);
				break;
			case 2:
				apd.setDeployType(DeployType.valueOf((String) value));
				break;
			}
			fireTableDataChanged();
		}
	}

}
