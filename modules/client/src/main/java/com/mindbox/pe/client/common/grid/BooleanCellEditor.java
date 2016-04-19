package com.mindbox.pe.client.common.grid;

import java.awt.Component;
import java.util.EventObject;

import javax.swing.DefaultCellEditor;
import javax.swing.JComboBox;
import javax.swing.JTable;

import com.mindbox.pe.model.table.BooleanDataHelper;

class BooleanCellEditor extends DefaultCellEditor {
	/**
	 * 
	 */
	private static final long serialVersionUID = -3951228734910107454L;

	public static BooleanCellEditor newInstance(boolean blankAllowed, boolean viewOnly) {
		JComboBox combo = new JComboBox();
		return new BooleanCellEditor(combo, blankAllowed, viewOnly);
	}

	private JComboBox combo;
	private boolean viewOnly;
	private final boolean blankAllowed;

	private BooleanCellEditor(JComboBox jcombobox, boolean blankAllowed, boolean viewOnly) {
		super(jcombobox);
		this.combo = jcombobox;
		this.blankAllowed = blankAllowed;
		this.viewOnly = viewOnly;
		initEditor();
		setClickCountToStart(2);
	}

	public Object getCellEditorValue() {
		return combo.getSelectedItem();
	}

	public boolean isCellEditable(EventObject eventobject) {
		return !viewOnly;
	}

	private void initEditor() {
		combo.addItem(BooleanDataHelper.TRUE_VALUE);
		combo.addItem(BooleanDataHelper.FALSE_VALUE);
		if (blankAllowed) {
			combo.addItem(BooleanDataHelper.ANY_VALUE);
		}
	}

	public Component getTableCellEditorComponent(JTable arg0, Object arg1, boolean arg2, int arg3, int arg4) {
		Object object = (arg1 instanceof String ? BooleanDataHelper.toStringValue((String) arg1) : arg1);
		Component component = super.getTableCellEditorComponent(arg0, object, arg2, arg3, arg4);
		return component;
	}
}