/*
 * Created on 2004. 1. 30.
 *
 */
package com.mindbox.pe.tools.templaterepair;

import java.awt.Dimension;

import com.mindbox.pe.client.common.table.AbstractSortableTable;

/**
 * @author Geneho Kim
 * @author MindBox
 * @since PowerEditor 2.3.0
 */
public class TemplateColumnRuleInfoTable extends AbstractSortableTable<TemplateColumnRuleInfoTableModel,TemplateColumnRuleInfo> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1323680452250010743L;

	/**
	 * @param tableModel
	 */
	public TemplateColumnRuleInfoTable(TemplateColumnRuleInfoTableModel tableModel) {
		super(tableModel);
		setPreferredScrollableViewportSize(new Dimension(getPreferredScrollableViewportSize().width, 200));
	}
}
