package com.mindbox.pe.client.common.dialog;

import java.util.ArrayList;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import com.mindbox.pe.client.ClientUtil;
import com.mindbox.pe.common.validate.ValidationViolation;

/**
 * Template id name table model.
 * @author Gene Kim
 * @author MindBox, Inc
 * @since PowerEditor 2.1.0
 */
final class ValidationErrorTableModel extends AbstractTableModel {
	/**
	 * 
	 */
	private static final long serialVersionUID = -3951228734910107454L;

	private static final String[] COLUMN_NAMES = new String[] {
			ClientUtil.getInstance().getLabel("label.message"),
			ClientUtil.getInstance().getLabel("label.invalid.value"),
			ClientUtil.getInstance().getLabel("label.invalid.data") };

	private final List<ValidationViolation> dataList = new ArrayList<ValidationViolation>();

	private boolean showInvalidData;

	public ValidationErrorTableModel(boolean showInvalidData) {
		this.showInvalidData = showInvalidData;
	}

	public int getColumnCount() {
		return (showInvalidData ? 3 : 1);
	}

	@Override
	public String getColumnName(int column) {
		return COLUMN_NAMES[column];
	}

	@Override
	public Object getValueAt(int row, int col) {
		if (dataList == null || dataList.size() < row) {
			ClientUtil.printError("getValueAt: Invalid entities: " + dataList);
			return null;
		}

		ValidationViolation violation = dataList.get(row);
		switch (col) {
		case 0:
			return getMessage(violation, null, null, false);
		case 1:
			return violation.getInvalidValue();
		case 2:
			return violation.getValidatedObject();
		default:
			return violation;
		}
	}

	// TODO Kim, 2007-09-28: display cause tree
	private String getMessage(ValidationViolation violation, String openParan, String closeParan, boolean skipInvalidText) {
		StringBuilder buff = new StringBuilder();
		boolean isForInvalid = violation.getMessage().indexOf("is not valid") > 0;
		if (!skipInvalidText || !isForInvalid) {
			if (openParan != null) buff.append(openParan);
			buff.append(violation.getMessage());
		}
		if (violation.getCauses() != null && !violation.getCauses().isEmpty()) {
			boolean isFirst = true;
			for (ValidationViolation cause : violation.getCauses()) {
				if (!isFirst) buff.append("; ");
				buff.append(getMessage(cause, "[", "]", true));
				if (isFirst) isFirst = false;
			}
		}
		if (!skipInvalidText || !isForInvalid) {
			if (closeParan != null) buff.append(closeParan);
		}
		return buff.toString();
	}

	@Override
	public int getRowCount() {
		return dataList.size();
	}

	public void setData(List<ValidationViolation> violations) {
		dataList.clear();
		dataList.addAll(violations);
	}
}