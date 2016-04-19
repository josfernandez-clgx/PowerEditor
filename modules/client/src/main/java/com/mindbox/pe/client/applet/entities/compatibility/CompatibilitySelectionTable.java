package com.mindbox.pe.client.applet.entities.compatibility;

import java.util.Arrays;

import javax.swing.ListSelectionModel;

import com.mindbox.pe.common.ui.AbstractSortableTable;
import com.mindbox.pe.model.GenericEntityType;
import com.mindbox.pe.model.assckey.GenericEntityCompatibilityData;

/**
 * @author Gene Kim
 * @author MindBox, Inc
 * @since PowerEditor 1.10.0
 */
class CompatibilitySelectionTable extends AbstractSortableTable<CompatibilitySelectionTableModel, GenericEntityCompatibilityData> {
	/**
	 * 
	 */
	private static final long serialVersionUID = -3951228734910107454L;

	/**
	 * @param tableModel
	 */
	public CompatibilitySelectionTable(CompatibilitySelectionTableModel tableModel) {
		super(tableModel);
		setColumnSelectionAllowed(false);
		setRowSelectionAllowed(true);
		setAutoResizeMode(2);
		setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		setShowHorizontalLines(true);
		setAutoCreateColumnsFromModel(false);
		setDefaultRenderer(String.class, new CompatibilityDataTabelCellRenderer());
	}

	protected void initColumns(String[] columnNames) {
		super.initColumns(columnNames);
		getColumnModel().getColumn(0).setPreferredWidth(160);
		getColumnModel().getColumn(1).setPreferredWidth(160);
		getColumnModel().getColumn(2).setPreferredWidth(100);
		getColumnModel().getColumn(3).setPreferredWidth(100);
	}

	void setData(GenericEntityType type1, GenericEntityType type2, GenericEntityCompatibilityData[] data) {
		getSelectionTableModel().setData(type1, type2, Arrays.asList(data));
	}

}