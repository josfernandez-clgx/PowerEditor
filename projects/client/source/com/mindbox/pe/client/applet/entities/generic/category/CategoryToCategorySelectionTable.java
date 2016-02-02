package com.mindbox.pe.client.applet.entities.generic.category;

import javax.swing.ListSelectionModel;

import com.mindbox.pe.client.common.table.AbstractSortableTable;
import com.mindbox.pe.model.assckey.MutableTimedAssociationKey;


/**
 * Table for category to category relationships.
 * @author MindBox, Inc
 * @since PowerEditor 5.1.0
 */
class CategoryToCategorySelectionTable extends AbstractSortableTable<CategoryToCategorySelectionTableModel, MutableTimedAssociationKey> {

	/**
	 * @param tableModel
	 */
	public CategoryToCategorySelectionTable(CategoryToCategorySelectionTableModel tableModel) {
		super(tableModel);
		setColumnSelectionAllowed(false);
		setRowSelectionAllowed(true);
		setAutoResizeMode(2);
		setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		setAutoCreateColumnsFromModel(false);
	}

}
