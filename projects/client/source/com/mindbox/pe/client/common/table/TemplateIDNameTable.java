package com.mindbox.pe.client.common.table;

import java.util.List;

import com.mindbox.pe.model.GridTemplate;


/**
 * @author Geneho Kim
 * @author MindBox
 */
public class TemplateIDNameTable extends IDNameObjectSelectionTable<TemplateIDNameTableModel, GridTemplate> {

	/**
	 * @param tableModel
	 */
	public TemplateIDNameTable(TemplateIDNameTableModel tableModel) {
		super(tableModel, true);
	}

	@Override
	protected void initColumns(String[] columnNames) {
		super.initColumns(columnNames);
		if (columnNames.length > 2) {
			getColumnModel().getColumn(0).setPreferredWidth(300);
			getColumnModel().getColumn(1).setPreferredWidth(50);
		}
	}

	public List<GridTemplate> getSelectedTemplates() {
		return getSelectedDataObjects();
	}

	public List<Integer> getSelectedTemplateIDs() {
		return getSelectedObjectIDs();
	}
}
