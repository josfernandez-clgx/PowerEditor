package com.mindbox.pe.client.applet.cbr;

import java.awt.Insets;

import javax.swing.DefaultCellEditor;
import javax.swing.JOptionPane;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumn;

import com.mindbox.pe.client.common.CBRAttributeComboBox;
import com.mindbox.pe.client.common.CBRValueCellEditor;
import com.mindbox.pe.client.common.PerfectNumberComboBox;
import com.mindbox.pe.client.common.table.AbstractSortableTable;
import com.mindbox.pe.model.CBRAttribute;
import com.mindbox.pe.model.CBRAttributeValue;
import com.mindbox.pe.model.Constants;

public final class CBRAttributeValueTable extends AbstractSortableTable<CBRAttributeValueTableModel, CBRAttributeValue> {


	private CBRAttributeComboBox aCombo;

	static class PerfectNumberRenderer extends DefaultTableCellRenderer {
		public PerfectNumberRenderer() {
			super();
		}

		public void setValue(Object value) {
			if (value == null || ((Integer) value).intValue() == Constants.CBR_NULL_DATA_EQUIVALENT_VALUE)
				setText("");
			else if (((Integer) value).intValue() == CBRAttribute.PERFECT_VALUE)
				setText("Perfect");
			else
				setText("" + ((Integer) value).intValue());
		}
	}

	static class AttributeRenderer extends DefaultTableCellRenderer {
		public AttributeRenderer() {
			super();
		}

		public void setValue(Object value) {
			if (value == null)
				setText("");
			else
				setText(((CBRAttribute) value).getName());
		}
	}

	public CBRAttributeValueTable(CBRAttributeValueTableModel tableModel) {
		super(tableModel);
		setDefaultRenderer(CBRAttribute.class, new AttributeRenderer());
		try {
			aCombo = CBRAttributeComboBox.createInstance();
			setDefaultEditor(CBRAttribute.class, new DefaultCellEditor(aCombo));
		}
		catch (Exception x) {
			JOptionPane.showMessageDialog(null, x.getMessage());
		}
		setDefaultRenderer(Integer.class, new PerfectNumberRenderer());
		setDefaultEditor(Integer.class, new DefaultCellEditor(new PerfectNumberComboBox()));

		setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		initTable(tableModel.getColumnNames());
		TableColumn valueColumn = this.getColumnModel().getColumn(1);
		valueColumn.setCellEditor(new CBRValueCellEditor());
	}

	public void populateAttributeCombo(int caseBaseID) {
		aCombo.populateAttributes(caseBaseID);
	}

	public CBRAttributeValue getCBRAttributeValueAt(int rowInView) {
		return (CBRAttributeValue) getModel().getValueAt(convertRowIndexToModel(rowInView), -1);
	}

	public Insets getInsets() {
		return new Insets(1, 2, 1, 1);
	}

	protected void displaySelectedRowDetails() {
		getSelectedRow();
	}

	protected void initTable(String[] columnNames) {
		setAutoResizeMode(2);
		setRowSelectionAllowed(true);
		setRowHeight(22);
		setDataList(new java.util.ArrayList<CBRAttributeValue>());
		initColumns(columnNames);
	}

}
