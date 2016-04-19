package com.mindbox.pe.client.applet.action;

import java.awt.Insets;

import javax.swing.DefaultCellEditor;
import javax.swing.JComboBox;
import javax.swing.ListSelectionModel;
import javax.swing.table.TableColumn;

import com.mindbox.pe.common.ui.AbstractSortableTable;
import com.mindbox.pe.model.DeployType;
import com.mindbox.pe.model.rule.FunctionParameterDefinition;

public final class FunctionParameterTable extends AbstractSortableTable<FunctionParameterTableModel, FunctionParameterDefinition> {
	/**
	 * 
	 */
	private static final long serialVersionUID = -3951228734910107454L;

	public FunctionParameterTable(FunctionParameterTableModel tableModel) {
		super(tableModel);
		setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		setAutoResizeMode(2);
		setRowSelectionAllowed(true);
		setRowHeight(22);
		getSelectionTableModel().setDataList(new java.util.LinkedList<FunctionParameterDefinition>());

		String[] deployTypeStrings = new String[DeployType.VALID_VALUES.length];
		for (int i = 0; i < deployTypeStrings.length; i++)
			deployTypeStrings[i] = DeployType.VALID_VALUES[i].getName();
		JComboBox deployTypeCombo = new JComboBox(deployTypeStrings);
		TableColumn valueColumn = this.getColumnModel().getColumn(2);
		valueColumn.setCellEditor(new DefaultCellEditor(deployTypeCombo));
	}

	public FunctionParameterDefinition getFunctionParameterDefinitionAt(int row) {
		return getDateObjectAt(row);
	}

	public Insets getInsets() {
		return new Insets(1, 2, 1, 1);
	}

	protected void displaySelectedRowDetails() {
		getSelectedRow();
	}
}
