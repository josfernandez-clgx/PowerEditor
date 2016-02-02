/*
 * Created on Jun 24, 2003
 */
package com.mindbox.pe.tools.migration;

import com.mindbox.pe.client.common.table.AbstractSortableTable;


/**
 * Guideline action parameter table.
 * @author Gene Kim
 * @author MindBox
 * @since PowerEditor 4.2.0
 */
class GuidelineActionParameterTable extends AbstractSortableTable<GuidelineActionParameterTableModel,GuidelineActionParameterRow> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4159765298974761557L;

	public GuidelineActionParameterTable(GuidelineActionParameterTableModel tableModel) {
		super(tableModel);
	}

	public GuidelineActionParameterRow getSelectedData() {
		return getSelectedDataObject();
	}

}