package com.mindbox.pe.tools.migration;

import com.mindbox.pe.client.common.table.AbstractSortableTable;

/**
 * Template Version mapping table.
 * @author Gene Kim
 * @author MindBox
 * @since PowerEditor 4.2.0
 */
class ActivationDateMappingTable extends AbstractSortableTable<ActivationDateMappingTableModel, GuidelineDateMap> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3572436034057638594L;

	public ActivationDateMappingTable(ActivationDateMappingTableModel tableModel) {
		super(tableModel);
	}

	public GuidelineDateMap getSelectedData() {
		return getSelectedDataObject();
	}

}