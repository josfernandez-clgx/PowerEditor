/*
 * Created on 2003. 12. 31.
 *
 */
package com.mindbox.pe.client.applet.parameters;

import com.mindbox.pe.client.common.grid.AbstractGridTable;
import com.mindbox.pe.model.AbstractTemplateColumn;
import com.mindbox.pe.model.ParameterTemplate;

/**
 * @author Geneho Kim
 * @author MindBox
 * @since PowerEditor 2.2.0
 */
public class ParameterGridTable extends AbstractGridTable<ParameterTemplate> {

	private ParameterTemplate template = null;

	public ParameterGridTable(ParameterGridTableModel tableModel) {
		super(tableModel);
	}

	public void setTemplate(ParameterTemplate template) {
		this.template = template;
		((ParameterGridTableModel) super.tableModel).setTemplate(template);
		if (template != null) {
			initTable();
		}
	}

	public ParameterTemplate getTemplate() {
		return template;
	}

	protected AbstractTemplateColumn getTemplateColumn(int col) {
		return (template == null ? null : template.getColumn(col));
	}

	protected int getTemplateColumnCount() {
		return (template == null ? 0 : template.getColumnCount());
	}

	protected String getColumnTitle(int col) {
		return (template == null ? "" : template.getColumn(col).getTitle());
	}
}
