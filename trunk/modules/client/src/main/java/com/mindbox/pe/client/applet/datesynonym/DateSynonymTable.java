package com.mindbox.pe.client.applet.datesynonym;

import javax.swing.ListSelectionModel;

import com.mindbox.pe.client.common.table.DateCellRenderer;
import com.mindbox.pe.common.ui.AbstractSortableTable;
import com.mindbox.pe.model.DateSynonym;

/**
 * @author Gene Kim
 * @author MindBox, Inc
 * @since PowerEditor 1.10.0
 */
public final class DateSynonymTable extends AbstractSortableTable<DateSynonymTableModel, DateSynonym> {
	/**
	 * 
	 */
	private static final long serialVersionUID = -3951228734910107454L;

	public DateSynonymTable(DateSynonymTableModel tableModel) {
		super(tableModel, 1);

		setRowSelectionAllowed(true);
		setAutoResizeMode(2);
		getSelectionModel().setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
	}

	protected void initColumns(String[] columnNames) {
		super.initColumns(columnNames);
		getColumnModel().getColumn(1).setCellRenderer(new DateCellRenderer());
		getColumnModel().getColumn(2).setPreferredWidth(400);
	}

}
