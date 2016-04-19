package com.mindbox.pe.client.common.grid;

import com.mindbox.pe.client.IClientConstants;
import com.mindbox.pe.model.template.ColumnDataSpecDigest;
import com.mindbox.pe.model.template.GridTemplate;

public class GridTableModel extends AbstractGridTableModel<GridTemplate> implements IClientConstants {
	/**
	 * 
	 */
	private static final long serialVersionUID = -3951228734910107454L;

	protected GridTemplate template;

	private boolean editable = true;

	public GridTableModel() {
		template = null;
	}

	public boolean isCellEditable(int i, int j) {
		return editable && super.isCellEditable(i, j);
	}

	public void setTemplate(GridTemplate gridtemplate) {
		this.template = gridtemplate;
	}

	@Override
	public GridTemplate getTemplate() {
		return template;
	}

	protected int getTemplateMaxRow() {
		if (template != null) {
			return template.getMaxNumOfRows();
		}
		else {
			return 0;
		}
	}

	protected int getTemplateColumnCount() {
		if (template != null) {
			return template.getNumColumns();
		}
		else {
			return 0;
		}
	}

	public void setEditable(boolean editable) {
		this.editable = editable;
	}

	protected ColumnDataSpecDigest getColumnDataSpecDigest(int columnNo) {
		if (template != null) {
			return template.getColumn(columnNo).getColumnDataSpecDigest();
		}
		return null;
	}

}
