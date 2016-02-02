package com.mindbox.pe.client.applet.parameters;

import com.mindbox.pe.client.common.grid.AbstractGridTableModel;
import com.mindbox.pe.model.ColumnDataSpecDigest;
import com.mindbox.pe.model.ParameterTemplate;

/**
 * Table model for Parameter template grids.
 * @author Geneho Kim
 * @author MindBox
 * @since PowerEditor 2.2.0
 */
public class ParameterGridTableModel extends AbstractGridTableModel<ParameterTemplate> {

	private ParameterTemplate template = null;
	private final ParameterDetailPanel parameterDetailPanel;

	public ParameterGridTableModel(ParameterDetailPanel parameterDetailPanel) {
		this.parameterDetailPanel = parameterDetailPanel;
	}

	public void setDirty(boolean flag) {
		super.setDirty(flag);
		if ( parameterDetailPanel != null ) parameterDetailPanel.setDirty(flag);
	}

	public void setTemplate(ParameterTemplate template) {
		this.template = template;
	}

	@Override
	public ParameterTemplate getTemplate() {
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
			return template.getColumnCount();
		}
		else {
			return 0;
		}
	}

	protected ColumnDataSpecDigest getColumnDataSpecDigest(int columnNo) {
		return template.getColumn(columnNo).getColumnDataSpecDigest();
	}

}