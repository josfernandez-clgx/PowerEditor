package com.mindbox.pe.client.common.grid;

import javax.swing.JTable;

import com.mindbox.pe.model.template.AbstractTemplateColumn;
import com.mindbox.pe.model.template.GridTemplate;

/**
 * Guideline Grid table.
 * @author Geneho Kim
 * @author MindBox
 */
public final class GridTable extends AbstractGridTable<GridTemplate> {
	/**
	 * 
	 */
	private static final long serialVersionUID = -3951228734910107454L;
	private GridTemplate gridTemplate;

	public GridTable() {
		super(new GridTableModel());
	}

	public void setTemplate(GridTemplate gridtemplate) {
		this.gridTemplate = gridtemplate;
		((GridTableModel) super.tableModel).setTemplate(gridtemplate);
		if (gridTemplate != null) {
			initTable();
		}
		setAutoResizeMode((gridtemplate.fitToScreen() ? JTable.AUTO_RESIZE_SUBSEQUENT_COLUMNS : JTable.AUTO_RESIZE_OFF));
	}

	public GridTemplate getTemplate() {
		return gridTemplate;
	}

	protected AbstractTemplateColumn getTemplateColumn(int col) {
		return gridTemplate.getColumn(col);
	}

	protected int getTemplateColumnCount() {
		return gridTemplate.getNumColumns();
	}

	protected String getColumnTitle(int col) {
		return gridTemplate.getColumn(col).getTitle();
	}

	public void setEnabled(boolean flag) {
		((GridTableModel) super.tableModel).setEditable(flag);
	}

}