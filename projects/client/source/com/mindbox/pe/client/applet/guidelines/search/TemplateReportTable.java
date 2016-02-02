package com.mindbox.pe.client.applet.guidelines.search;

import com.mindbox.pe.client.common.CategoryOrEntityValuesTabelCellRenderer;
import com.mindbox.pe.client.common.table.AbstractSortableTable;
import com.mindbox.pe.model.GuidelineReportData;
import com.mindbox.pe.model.table.CategoryOrEntityValues;

/**
 * @author Gene Kim
 * @author MindBox, Inc
 * @since PowerEditor 1.10.0
 */
public final class TemplateReportTable extends AbstractSortableTable<TemplateReportTableModel, GuidelineReportData> {

	public TemplateReportTable(TemplateReportTableModel tableModel) {
		super(tableModel);
		setRowSelectionAllowed(true);
		setAutoResizeMode(2);
		setDefaultRenderer(CategoryOrEntityValues.class, new CategoryOrEntityValuesTabelCellRenderer());
		for (int c = 0; c < getColumnCount(); c++) {
			getColumnModel().getColumn(c).setPreferredWidth(20);
			getColumnModel().getColumn(c).setMinWidth(10);
		}
	}

	public synchronized GuidelineReportData getSelectedData(int rowInView) {
		return (GuidelineReportData) getModel().getValueAt(convertRowIndexToModel(rowInView), -1);
	}

	public synchronized void selectIfFound(GuidelineReportData data) {
		if (data != null) {
			for (int i = 0; i < getModel().getRowCount(); i++) {
				GuidelineReportData rowData = (GuidelineReportData) getModel().getValueAt(i, -1);
				if (rowData.equals(data)) {
					int viewIndex = convertRowIndexToView(i);
					getSelectionModel().setSelectionInterval(viewIndex, viewIndex);
					return;
				}
			}
		}
	}
}