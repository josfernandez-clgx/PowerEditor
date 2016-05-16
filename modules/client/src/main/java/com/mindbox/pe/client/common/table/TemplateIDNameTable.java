package com.mindbox.pe.client.common.table;

import java.util.List;

import com.mindbox.pe.model.template.GridTemplate;


/**
 * @author Geneho Kim
 * @author MindBox
 */
public class TemplateIDNameTable extends IDNameObjectSelectionTable<TemplateIDNameTableModel, GridTemplate> {

	private static final long serialVersionUID = -3951228734910107454L;

	/**
	 * @param tableModel tableModel
	 */
	public TemplateIDNameTable(TemplateIDNameTableModel tableModel) {
		super(tableModel, true);
	}

	public List<Integer> getSelectedTemplateIDs() {
		return getSelectedObjectIDs();
	}

	public List<GridTemplate> getSelectedTemplates() {
		return getSelectedDataObjects();
	}

	@Override
	protected void initColumns(String[] columnNames) {
		super.initColumns(columnNames);
		if (columnNames.length > 2) {
			getColumnModel().getColumn(0).setPreferredWidth(300);
			getColumnModel().getColumn(1).setPreferredWidth(50);
		}
	}
}
