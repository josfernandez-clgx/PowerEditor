package com.mindbox.pe.client.common.rowheader;

import java.awt.Dimension;

import javax.swing.DefaultCellEditor;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.LookAndFeel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableColumn;

import com.mindbox.pe.client.common.AbstractCellEditor;

public class RowHeaderTable extends JTable {

	public void tableChanged(TableModelEvent tablemodelevent) {
		if (tablemodelevent.getSource() == getModel())
			super.tableChanged(tablemodelevent);
		else if (getModel() != null && sourceTable != null && sourceTable.getModel() != null
				&& sourceTable.getModel().getRowCount() != getModel().getRowCount())
			populate(sourceTable.getModel().getRowCount());
	}

	public RowHeaderTable(JTable jtable, JScrollPane jscrollpane) {
		super(new RowHeaderTableModel());
		sourceTable = jtable;
		LookAndFeel.installColorsAndFont(this, "TableHeader.background", "TableHeader.foreground", "TableHeader.font");
		initColumns();
		setAutoCreateColumnsFromModel(false);
		setRowSelectionAllowed(true);
		setCellSelectionEnabled(false);
		setIntercellSpacing(new Dimension(0, 0));
		setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
		Dimension dimension = getPreferredScrollableViewportSize();
		dimension.width = getPreferredSize().width;
		setPreferredScrollableViewportSize(dimension);
		setRowHeight(jtable.getRowHeight());
		setDefaultRenderer(java.lang.Object.class, new RowHeaderRenderer());
		jscrollpane.setRowHeaderView(this);
		JTableHeader jtableheader = getTableHeader();
		jtableheader.setReorderingAllowed(false);
		jtableheader.setResizingAllowed(false);
		jscrollpane.setCorner("UPPER_LEFT_CORNER", jtableheader);
		sourceTable.getModel().addTableModelListener(this);
		this.getSelectionModel().addListSelectionListener(new SelectionL());
	}

	public void populate(int i) {
		((RowHeaderTableModel) getModel()).setRowCount(i);
	}

	protected void initColumns() {
		DefaultTableColumnModel defaulttablecolumnmodel = new DefaultTableColumnModel();
		TableColumn tablecolumn = new TableColumn();
		tablecolumn.setHeaderValue("Row");
		tablecolumn.setPreferredWidth(30);
		defaulttablecolumnmodel.addColumn(tablecolumn);
		setColumnModel(defaulttablecolumnmodel);
	}

	private class SelectionL implements ListSelectionListener {

		public void valueChanged(ListSelectionEvent arg0) {
			ListSelectionModel lsm = (ListSelectionModel) arg0.getSource();
			if (!arg0.getValueIsAdjusting() && !lsm.isSelectionEmpty()) {
				// cancel editing first
				int editingCol = sourceTable.getEditingColumn();
				int editingRow = sourceTable.getEditingRow();
				if (editingRow >= 0 && editingCol >= 0) {
					TableCellEditor editor = sourceTable.getCellEditor(editingRow, editingCol);
					if (editor != null && editor instanceof javax.swing.AbstractCellEditor) {
						((DefaultCellEditor) editor).stopCellEditing();
					}
					else if (editor != null && editor instanceof AbstractCellEditor) {
						((AbstractCellEditor) editor).stopCellEditing();
					}
				}
				// update source table row selections
				sourceTable.clearSelection();
				int[] rows = RowHeaderTable.this.getSelectedRows();
				if (rows != null) {
					for (int i = 0; i < rows.length; i++) {
						sourceTable.addRowSelectionInterval(rows[i], rows[i]);
					}
				}
			}
		}

	}

	private JTable sourceTable;
}
