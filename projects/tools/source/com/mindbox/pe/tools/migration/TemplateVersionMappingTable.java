/*
 * Created on Jun 24, 2003
 */
package com.mindbox.pe.tools.migration;

import com.mindbox.pe.client.common.table.AbstractSortableTable;

/**
 * Template Version mapping table.
 * @author Gene Kim
 * @author MindBox
 * @since PowerEditor 4.2.0
 */
class TemplateVersionMappingTable extends AbstractSortableTable<TemplateVersionMappingTableModel, TemplateVersionMap> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -169973308520731602L;

	public TemplateVersionMappingTable(TemplateVersionMappingTableModel tableModel) {
		super(tableModel);
	}

	public TemplateVersionMap getSelectedData() {
		return getSelectedDataObject();
	}

}