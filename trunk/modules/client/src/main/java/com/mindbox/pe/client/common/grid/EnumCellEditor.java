package com.mindbox.pe.client.common.grid;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EventObject;
import java.util.Iterator;
import java.util.List;

import javax.swing.DefaultCellEditor;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JTable;

import com.mindbox.pe.client.ClientUtil;
import com.mindbox.pe.client.applet.UIFactory;
import com.mindbox.pe.client.applet.validate.DomainModel;
import com.mindbox.pe.client.common.EnumValueCellRenderer;
import com.mindbox.pe.common.UtilBase;
import com.mindbox.pe.model.EnumValue;
import com.mindbox.pe.model.table.EnumValuesDataHelper;
import com.mindbox.pe.model.template.ColumnDataSpecDigest;
import com.mindbox.pe.model.template.ColumnDataSpecDigest.EnumSourceType;

public class EnumCellEditor extends DefaultCellEditor {

	private class ActionL implements ActionListener {
		@Override
		public synchronized void actionPerformed(ActionEvent e) {
			if (!UtilBase.isSame(tableModel.getCellValueAt(currentRow, columnName), getCellEditorValue())) {
				if (!UtilBase.isEmptyCellValue(tableModel.getCellValueAt(currentRow, columnName)) && tableModel.hasNonEmptyDepedentCell(currentRow, columnName)) {
					// display a warning dialog with Yes/No/Cancel option.
					List<Integer> columnIDs = tableModel.getNonEmptyDependentColumnIDs(currentRow, columnName);
					StringBuilder buff = new StringBuilder();
					for (Iterator<Integer> iterator = columnIDs.iterator(); iterator.hasNext();) {
						buff.append(tableModel.getTemplate().getColumn(iterator.next()).getTitle());
						if (iterator.hasNext()) buff.append(", ");
					}
					if (ClientUtil.getInstance().showConfirmation("msg.warning.enum.column.clear.dependent.cells", buff.toString())) {
						tableModel.clearNonEmptyDependentColumns(currentRow, columnName);
					}
					else {
						cancelCellEditing();
					}
				}
			}
		}

	}

	private static final long serialVersionUID = -3951228734910107454L;

	public static final List<EnumValue> fetchSortedApplicableEnumValueListIfConfigured(ColumnDataSpecDigest columnDataSpecDigest, String selectorValue) {
		List<EnumValue> enumValueList = new ArrayList<EnumValue>();
		if (columnDataSpecDigest.getEnumSourceType() == EnumSourceType.EXTERNAL && columnDataSpecDigest.isEnumSelectorColumnSet()) {
			enumValueList.addAll(ClientUtil.getEnumerationSourceProxy().getApplicableEnumValues(columnDataSpecDigest.getEnumSourceName(), selectorValue));
		}
		if (columnDataSpecDigest.isEnumValueNeedSorted()) {
			Collections.sort(enumValueList);
		}
		return enumValueList;
	}

	public static final List<EnumValue> fetchSortedEnumValueListIfConfigured(ColumnDataSpecDigest columnDataSpecDigest) {
		List<EnumValue> enumValueList = new ArrayList<EnumValue>();
		if (!(columnDataSpecDigest.getEnumSourceType() == EnumSourceType.EXTERNAL && columnDataSpecDigest.isEnumSelectorColumnSet())) {
			enumValueList.addAll(EnumValuesDataHelper.getAllEnumValues(columnDataSpecDigest, DomainModel.getInstance(), ClientUtil.getEnumerationSourceProxy()));
			if (columnDataSpecDigest.isEnumValueNeedSorted()) {
				Collections.sort(enumValueList);
			}
		}
		return enumValueList;
	}

	private boolean viewOnly;
	private final ColumnDataSpecDigest columnDataSpecDigest;
	private final AbstractGridTableModel<?> tableModel;
	private DefaultComboBoxModel<EnumValue> comboModel;
	private final String columnName;
	private int currentRow;

	public EnumCellEditor(ColumnDataSpecDigest columnDataSpecDigest, String columnName, boolean viewOnly, AbstractGridTableModel<?> tableModel) {
		super(UIFactory.createComboBox());
		this.viewOnly = viewOnly;
		this.columnDataSpecDigest = columnDataSpecDigest;
		this.tableModel = tableModel;
		this.comboModel = new DefaultComboBoxModel<EnumValue>();
		this.columnName = columnName;
		initEditor(columnDataSpecDigest);
	}

	@Override
	public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
		resetListModelForSelectionIfNeeded(row);
		this.currentRow = row;
		return super.getTableCellEditorComponent(table, value, isSelected, row, column);
	}

	@SuppressWarnings("unchecked")
	private void initEditor(ColumnDataSpecDigest columnDataSpecDigest) {
		List<EnumValue> enumValueList = fetchSortedEnumValueListIfConfigured(columnDataSpecDigest);
		resetComboModel(enumValueList);

		JComboBox<EnumValue> combo = (JComboBox<EnumValue>) getComponent();
		combo.setRenderer(new EnumValueCellRenderer(enumValueList));
		combo.setModel(comboModel);
		combo.addActionListener(new ActionL());
		setClickCountToStart(1);
	}

	@Override
	public boolean isCellEditable(EventObject eventobject) {
		return !viewOnly;
	}

	private void resetComboModel(List<EnumValue> enumValueList) {
		comboModel.removeAllElements();
		if (columnDataSpecDigest.isBlankAllowed() && !columnDataSpecDigest.isMultiSelectAllowed()) {
			comboModel.addElement(EnumValue.BLANK);
			// It is necessary to use a BLANK instance of EnumValue, rather than a whitespace String,
			// in order to get both
			// 1. JCombo to display a blank line, and 
			// 2. The persistence tier to store an empty/null value for a blank cell.
		}
		for (EnumValue ev : enumValueList) {
			if (ev.isActive()) {
				comboModel.addElement(ev);
			}
		}
	}

	private void resetListModelForSelectionIfNeeded(int row) {
		// reset listModel if columnDataSpecDigest has a selector
		if (columnDataSpecDigest.getEnumSourceType() == EnumSourceType.EXTERNAL && columnDataSpecDigest.isEnumSelectorColumnSet()) {
			Object cellValue = tableModel.getCellValueAt(row, columnDataSpecDigest.getEnumSelectorColumnName());
			String selectorValue = (cellValue == null ? null : (cellValue instanceof EnumValue ? ((EnumValue) cellValue).getDeployValue() : cellValue.toString()));

			List<EnumValue> list = EnumCellEditor.fetchSortedApplicableEnumValueListIfConfigured(columnDataSpecDigest, selectorValue);
			resetComboModel(list);
		}
	}

	@Override
	public boolean shouldSelectCell(EventObject anEvent) {
		if ((comboModel.getSize() == 0 || (comboModel.getSize() == 1 && columnDataSpecDigest.isBlankAllowed())) && columnDataSpecDigest.isEnumSelectorColumnSet()) {
			String columnName = this.tableModel.getColumnName(tableModel.getColumnIndex(columnDataSpecDigest.getEnumSelectorColumnName()));
			ClientUtil.getInstance().showWarning("msg.warning.select.column.selector", new Object[] { columnName });
			stopCellEditing();
			return true;
		}
		else {
			return super.shouldSelectCell(anEvent);
		}
	}
}