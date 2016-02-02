package com.mindbox.pe.client.applet.entities.generic.category;

import javax.swing.ListSelectionModel;

import com.mindbox.pe.client.common.table.AbstractSortableTable;

/**
 * Table for category to entity relationships.
 * @author MindBox, Inc
 * @since PowerEditor 5.1.0
 */
class CategoryToEntitySelectionTable extends AbstractSortableTable<CategoryToEntitySelectionTableModel, CategoryToEntityAssociationData> {

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