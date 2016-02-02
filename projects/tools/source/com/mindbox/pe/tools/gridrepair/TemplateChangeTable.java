/*
 * Created on 2004. 1. 30.
 *
 */
package com.mindbox.pe.tools.gridrepair;

import java.awt.Dimension;

import com.mindbox.pe.client.common.table.AbstractSortableTable;

/**
 * @author Geneho Kim
 * @author MindBox
 * @since PowerEditor 2.3.0
 */
public class TemplateChangeTable extends AbstractSortableTable<TemplateChangeTableModel,TemplateColumnChangeSpec> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6480174669373378227L;

	/**
	 * @param tableModel
	 */
	public TemplateChangeTable(TemplateChangeTableModel tableModel) {
		super(tableModel);
		setPreferredScrollableViewportSize(new Dimension(getPreferredScrollableViewportSize().width, 200));
	}
}
