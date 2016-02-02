package com.mindbox.pe.client.common.grid;

import java.awt.Component;
import java.util.EventObject;

import javax.swing.DefaultCellEditor;
import javax.swing.JTable;

import com.mindbox.pe.client.common.GenericEntityComboBox;
import com.mindbox.pe.model.GenericEntity;
import com.mindbox.pe.model.GenericEntityType;
import com.mindbox.pe.model.table.CategoryOrEntityValue;

public class SingleSelectEntityCellEditor extends DefaultCellEditor {

	public static SingleSelectEntityCellEditor createInstance(GenericEntityType entityType, boolean allowNull, boolean viewOnly) {
		GenericEntityComboBox comboBox = new GenericEntityComboBox(entityType, allowNull, null, false);
		return new SingleSelectEntityCellEditor(comboBox, viewOnly);
	}

	private boolean viewOnly;

	private SingleSelectEntityCellEditor(GenericEntityComboBox comboBox, boolean viewOnly) {
		super(comboBox);
		this.viewOnly = viewOnly;
		setClickCountToStart(2);
	}

	public boolean isCellEditable(EventObject eventobject) {
		return !viewOnly;
	}

	public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int col) {
		return super.getTableCellEditorComponent(table, value, isSelected, row, col);
	}
	
	public Object getCellEditorValue() {
		Object obj = super.getCellEditorValue();
		if (obj instanceof GenericEntity) {
			return new CategoryOrEntityValue((GenericEntity) obj);
		}
		else {
			return null;
		}
	}
}