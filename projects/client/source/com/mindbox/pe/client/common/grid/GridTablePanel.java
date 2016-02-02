package com.mindbox.pe.client.common.grid;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ToolTipManager;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelListener;

import com.mindbox.pe.client.ClientUtil;
import com.mindbox.pe.client.applet.UIFactory;
import com.mindbox.pe.client.common.AbstractThreadedActionAdapter;
import com.mindbox.pe.client.common.PanelBase;
import com.mindbox.pe.client.common.rowheader.RowHeaderTable;
import com.mindbox.pe.model.AbstractGuidelineGrid;
import com.mindbox.pe.model.GridTemplate;

public class GridTablePanel extends PanelBase implements IGridDataCard {

	private class GridAdapter extends AbstractThreadedActionAdapter {

		public void performAction(ActionEvent actionevent) {
			Object obj = actionevent.getSource();
			if (obj == appendRowButton) {
				addRow(-1);
				gridTable.changeSelection((gridTable.getRowCount() - 1), -1, false, false);
			}
			else if (obj == addRowButton) {
				int selectedRow = gridTable.getSelectedRow();
				addRow(selectedRow);
				if (selectedRow == -1) selectedRow = gridTable.getRowCount() - 1;
				gridTable.changeSelection(selectedRow, -1, false, false);
			}
			else if (obj == deleteRowButton) {
				if (ClientUtil.getInstance().showConfirmation("msg.question.remove.grid.row")) {
					gridTable.removeRow();
					if (gridTable.getModel() instanceof GridTableModel)
						((GridTableModel) gridTable.getModel()).setDirty(true);
				}
			}
			else if (obj == cutButton) {
				excelAdapter.cut();
				if (gridTable.getModel() instanceof GridTableModel)
					((GridTableModel) gridTable.getModel()).setDirty(true);
			}
			else if (obj == copyButton) {
				excelAdapter.copy();
			}
			else if (obj == pasteButton) {
				excelAdapter.paste();
			}
			else {
				ClientUtil.getLogger().warn("Unknown Action Event= " + actionevent);
			}
			updateButtonStates();
		}

		GridAdapter() {
		}
	}

	public GridTablePanel() {
		appendRowButton = null;
		addRowButton = null;
		deleteRowButton = null;
		cutButton = null;
		copyButton = null;
		pasteButton = null;
		gridTable = null;
		mViewOnly = false;
		mListener = new GridAdapter();
		initComponents();
		setEnabled(false);
	}

	public void toggleRuleIDColumns() {
		gridTable.toggleRuleIDColumns();
	}

	public void addRow(int row) {
		gridTable.addRow(row);
	}

	public void addGridRowSelectionListener(ListSelectionListener listener) {
		gridTable.getSelectionModel().addListSelectionListener(listener);
	}

	public void removeGridRowSelectionListener(ListSelectionListener listener) {
		gridTable.getSelectionModel().removeListSelectionListener(listener);
	}

	public void addGridTableModelListener(TableModelListener listener) {
		gridTable.getModel().addTableModelListener(listener);
	}

	public void removeGridTableModelListener(TableModelListener listener) {
		gridTable.getModel().removeTableModelListener(listener);
	}

	/**
	 * Updates the value of the specified grid cell.
	 * @param row 1-based row number
	 * @param col 1-based column number
	 * @param value new cell value
	 * @since PowerEditor 4.1.0
	 */
	public void updateCellValue(int row, int col, Object value) {
		gridTable.updateCellValue(row, col, value);
	}

	/**
	 * Gets the 1-based selected row, which is never zero.
	 * @return 1-based selected row, if a selection is made; -1, otherwise
	 */
	public int getSelectedRow() {
		return (gridTable.getSelectedRow() < 0 ? -1 : (gridTable.getSelectedRow() + 1));
	}

	void updateButtonStates() {
		boolean flag = !mViewOnly && (gridTable.getSelectedRowCount() > 0);
		deleteRowButton.setEnabled(flag);
		copyButton.setEnabled(gridTable.getSelectedRowCount() > 0);
		cutButton.setEnabled(flag);
		pasteButton.setEnabled(flag);
	}

	private void initGridTablePanel() {
		gridTable = new GridTable();

		gridTable.setRowHeight(24);
		appendRowButton = UIFactory.createButton("", "image.btn.grid.row.append", null, "button.tooltip.row.append", false);
		addRowButton = UIFactory.createButton("", "image.btn.grid.row.insert", null, "button.tooltip.row.add", false);
		deleteRowButton = UIFactory.createButton("", "image.btn.grid.row.delete", null, "button.tooltip.row.delete", false);
		cutButton = UIFactory.createButton("", "image.btn.grid.row.cut", null, "button.tooltip.row.cut", false);
		copyButton = UIFactory.createButton("", "image.btn.grid.row.copy", null, "button.tooltip.row.copy", false);
		pasteButton = UIFactory.createButton("", "image.btn.grid.row.paste", null, "button.tooltip.row.paste", false);

		JPanel jpanel = new JPanel();
		jpanel.setLayout(new BoxLayout(jpanel, 1));
		JButton ajbutton[] = { appendRowButton, addRowButton, deleteRowButton, cutButton, copyButton, pasteButton };
		for (int i = 0; i < ajbutton.length; i++) {
			if (ajbutton[i] != null) {
				jpanel.add(ajbutton[i]);
				ajbutton[i].addActionListener(mListener);
			}
		}

		gridTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {

			public void valueChanged(ListSelectionEvent listselectionevent) {
				if (!listselectionevent.getValueIsAdjusting()) updateButtonStates();
			}

		});
		ToolTipManager.sharedInstance().unregisterComponent(gridTable);
		ToolTipManager.sharedInstance().unregisterComponent(gridTable.getTableHeader());
		excelAdapter = new ExcelAdapter(gridTable);

		gridScrollPane = new JScrollPane(gridTable);
		new RowHeaderTable(gridTable, gridScrollPane);

		setLayout(new BorderLayout());
		add(jpanel, BorderLayout.EAST);
		add(gridScrollPane, BorderLayout.CENTER);
	}

	public void populate(AbstractGuidelineGrid grid, boolean hideRuleIDColumns) {
		cancelEdits();
		if (grid == null) {
			gridTable.populate(null);
		}
		else {
			gridTable.populate(grid.getDataObjects());
			int[] columnWidths = ClientUtil.getPreferenceManager().getStoredGridColumnWidths(grid.getTemplateID());
			if (columnWidths != null) {
				setColumnWidths(columnWidths);
			}
			if (hideRuleIDColumns) {
				gridTable.hideRuleIDColumns();
			}
			else {
				gridTable.showRuleIDColumns();
			}
		}
	}

	public AbstractGridTableModel getGridTableModel() {
		return gridTable.tableModel;
	}

	protected void setEditable(boolean flag) {
		JButton ajbutton[] = { appendRowButton, addRowButton, deleteRowButton, cutButton, pasteButton };
		for (int i = 0; i < ajbutton.length; i++)
			if (ajbutton[i] != null) ajbutton[i].setEnabled(flag);

		gridTable.setEnabled(flag);
	}

	public boolean isDirty() {
		return gridTable.isDirty();
	}

	public String toString() {
		return gridTable.toString();
	}

	@SuppressWarnings("unchecked")
	public List<List<Object>> getDataVector() {
		return (List<List<Object>>) getGridTableModel().getDataVector();
	}

	private void initComponents() {
		ClientUtil.getInstance();
		initGridTablePanel();
		setEditable(false);
	}

	public void cancelEdits() {
		if (gridTable.isEditing()) gridTable.getCellEditor().stopCellEditing();
	}

	public void setTemplate(GridTemplate gridtemplate) {
		cancelEdits();
		gridTable.setTemplate(gridtemplate);
		gridScrollPane.setHorizontalScrollBarPolicy((gridtemplate.fitToScreen()
				? JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED
				: JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS));
	}

	public void setEnabled(boolean flag) {
		JButton ajbutton[] = { appendRowButton, addRowButton };
		boolean flag1 = flag && !mViewOnly;
		for (int i = 0; i < ajbutton.length; i++)
			if (ajbutton[i] != null) ajbutton[i].setEnabled(flag1);

		gridTable.setEnabled(flag1);
		updateButtonStates();
		super.setEnabled(flag1);
	}

	public void setViewOnly(boolean flag) {
		mViewOnly = flag;
		setEditable(!flag);
	}

	public final int[] getColumnWidths() {
		return gridTable.getColumnWidths();
	}

	public final void setColumnWidths(int[] widths) {
		gridTable.setColumnWidths(widths);
	}

	public GridTable getGridTable() {
		return gridTable;
	}

	private JButton appendRowButton;
	private JButton addRowButton;
	private JButton deleteRowButton;
	private JButton cutButton;
	private JButton copyButton;
	private JButton pasteButton;
	private GridTable gridTable;
	private boolean mViewOnly;
	private GridAdapter mListener;
	private ExcelAdapter excelAdapter;
	private JScrollPane gridScrollPane;
}