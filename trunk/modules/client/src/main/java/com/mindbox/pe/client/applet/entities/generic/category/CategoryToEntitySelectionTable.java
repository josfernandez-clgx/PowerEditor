package com.mindbox.pe.client.applet.entities.generic.category;

import javax.swing.ListSelectionModel;

import com.mindbox.pe.common.ui.AbstractSortableTable;

/**
 * Table for category to entity relationships.
 * @author MindBox, Inc
 * @since PowerEditor 5.1.0
 */
class CategoryToEntitySelectionTable extends AbstractSortableTable<CategoryToEntitySelectionTableModel, CategoryToEntityAssociationData> {
	/**
	 * 
	 */
	private static final long serialVersionUID = -3951228734910107454L;

	/**
	* @param tableModel
	*/
	public CategoryToEntitySelectionTable(CategoryToEntitySelectionTableModel tableModel) {
		super(tableModel);
		setColumnSelectionAllowed(false);
		setRowSelectionAllowed(true);
		setAutoResizeMode(2);
		setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		setShowHorizontalLines(true);
		setAutoCreateColumnsFromModel(false);
	}
}