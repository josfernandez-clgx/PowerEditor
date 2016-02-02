package com.mindbox.pe.client.applet.parameters;

import javax.swing.ListSelectionModel;

import com.mindbox.pe.client.common.CategoryOrEntityValuesTabelCellRenderer;
import com.mindbox.pe.client.common.table.AbstractSortableTable;
import com.mindbox.pe.model.ParameterGrid;
import com.mindbox.pe.model.table.CategoryOrEntityValues;

/**
 * @author Geneho Kim
 * @author MindBox
 * @since PowerEditor 2.2.0
 */
public class ParameterContextTable extends AbstractSortableTable<ParameterContextTableModel, ParameterGrid> {

	/**
	 * @param tableModel
	 */
	public ParameterContextTable(ParameterContextTableModel tableModel) {
		super(tableModel);
		setDefaultRenderer(CategoryOrEntityValues.class, new CategoryOrEntityValuesTabelCellRenderer());
		getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
	}
}
