package com.mindbox.pe.client.applet.parameters;

import javax.swing.ListSelectionModel;

import com.mindbox.pe.client.common.CategoryOrEntityValuesTabelCellRenderer;
import com.mindbox.pe.common.ui.AbstractSortableTable;
import com.mindbox.pe.model.grid.ParameterGrid;
import com.mindbox.pe.model.table.CategoryOrEntityValues;

/**
 * @author Geneho Kim
 * @author MindBox
 * @since PowerEditor 2.2.0
 */
public class ParameterContextTable extends AbstractSortableTable<ParameterContextTableModel, ParameterGrid> {

	private static final long serialVersionUID = -3951228734910107454L;

	public ParameterContextTable(ParameterContextTableModel tableModel) {
		super(tableModel);
		setDefaultRenderer(CategoryOrEntityValues.class, new CategoryOrEntityValuesTabelCellRenderer());
		getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
	}
}
