package com.mindbox.pe.client.common.grid;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.swing.DefaultCellEditor;
import javax.swing.JFormattedTextField;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.ChangeEvent;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import org.apache.log4j.Logger;

import com.mindbox.pe.client.ClientUtil;
import com.mindbox.pe.client.common.formatter.FormatterFactory;
import com.mindbox.pe.model.Constants;
import com.mindbox.pe.model.GenericEntityType;
import com.mindbox.pe.model.template.AbstractTemplateColumn;
import com.mindbox.pe.model.template.AbstractTemplateCore;
import com.mindbox.pe.model.template.ColumnDataSpecDigest;

/**
 * Grid table base.
 * @author Geneho Kim
 * @author MindBox
 * @since PowerEditor 2.2.0
 */
// See TT CR #72 and #814 for info about table sorting, etc.
// These tables were changed to be sorted (CR #72) but subsequent issues have
// caused a removal of the sort (CR #814), at least temporarily.
// The sort code has been left in, but commented out, for ease of future
// reimplementation.
public abstract class AbstractGridTable<T extends AbstractTemplateCore<?>> extends JTable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -3951228734910107454L;

	private boolean viewOnly;
	protected final Logger logger = Logger.getLogger(getClass());
	private final List<String> columnNameList = new ArrayList<String>();
	protected final AbstractGridTableModel<T> tableModel;

	protected AbstractGridTable(AbstractGridTableModel<T> model) {
		super(model);
		viewOnly = false;
		tableModel = model;
		putClientProperty("terminateEditOnFocusLost", Boolean.TRUE);
	}

	public boolean addRow(int rowID) {
		try {
			boolean rowAdded = tableModel.addRow(rowID);
			if (rowAdded) {
				clearSelection();
			}
			return rowAdded;
		}
		catch (Exception ex) {
			ClientUtil.handleRuntimeException(ex);
			return false;
		}
	}

	/**
	 * This is needed as a fix for java bug #: 4330950 (and related bugs).
	 * Apparently, starting with java 1.4.0, a value that is just entered into a table cell will
	 * disappear if focus is lost from that cell (by resizing or clicking on a table header).
	 * The code below is a workaround. It may not be necessary after java 1.5... depending on whether
	 * that issue is fixed there.
	 * <p>
	 * See http://bugs.sun.com/bugdatabase/view_bug.do;:YfiG?bug_id=4330950 for more info.
	 * @author Inna Nill
	 * @author MindBox, LLC.
	 * @since PowerEditor 4.2.0.
	 */
	public void columnMarginChanged(ChangeEvent e) {
		int col = getEditingColumn();
		int row = getEditingRow();
		editCellAt(row, col);
		super.columnMarginChanged(e);
	}

	protected abstract String getColumnTitle(int col);

	public final int[] getColumnWidths() {
		int[] widths = new int[getTemplateColumnCount()];
		for (int i = 0; i < widths.length; i++) {
			widths[i] = getColumnModel().getColumn(i).getWidth();
		}
		return widths;
	}

	protected final TableCellEditor getEditor(AbstractTemplateColumn templateColumn) {
		Object obj = null;
		ColumnDataSpecDigest dataSpecDigest = templateColumn.getColumnDataSpecDigest();
		if (dataSpecDigest.getType().equals(ColumnDataSpecDigest.TYPE_ENTITY)) {
			GenericEntityType entityType = GenericEntityType.forName(dataSpecDigest.getEntityType());
			if (entityType == null) {
				return null;
			}
			boolean isCategoryAllowed = entityType.hasCategory() && dataSpecDigest.isCategoryAllowed();
			if (isCategoryAllowed && dataSpecDigest.isEntityAllowed()) {
				if (dataSpecDigest.isMultiSelectAllowed()) {
					return new MultiSelectCategoryOrEntityCellEditor(
							templateColumn.getTitle(),
							GenericEntityType.forName(dataSpecDigest.getEntityType()),
							true,
							viewOnly,
							tableModel,
							dataSpecDigest.isEnumValueNeedSorted());
				}
				else {
					return new SingleSelectCategoryOrEntityCellEditor(
							templateColumn.getTitle(),
							GenericEntityType.forName(dataSpecDigest.getEntityType()),
							true,
							viewOnly,
							tableModel,
							dataSpecDigest.isEnumValueNeedSorted());
				}
			}
			else if (isCategoryAllowed) {
				if (dataSpecDigest.isMultiSelectAllowed()) {
					return new MultiSelectCategoryOrEntityCellEditor(
							templateColumn.getTitle(),
							GenericEntityType.forName(dataSpecDigest.getEntityType()),
							false,
							viewOnly,
							tableModel,
							dataSpecDigest.isEnumValueNeedSorted());
				}
				else {
					return new SingleSelectCategoryOrEntityCellEditor(
							templateColumn.getTitle(),
							GenericEntityType.forName(dataSpecDigest.getEntityType()),
							false,
							viewOnly,
							tableModel,
							dataSpecDigest.isEnumValueNeedSorted());
				}
			}
			else {
				if (dataSpecDigest.isMultiSelectAllowed()) {
					return new MultiSelectEntityCellEditor(templateColumn.getTitle(), GenericEntityType.forName(dataSpecDigest.getEntityType()), viewOnly, tableModel);
				}
				else {
					return SingleSelectEntityCellEditor.createInstance(GenericEntityType.forName(dataSpecDigest.getEntityType()), dataSpecDigest.isBlankAllowed(), viewOnly);
				}
			}
		}
		else if (dataSpecDigest.getType().equals(ColumnDataSpecDigest.TYPE_SYMBOL)) {
			obj = new DefaultCellEditor(new JFormattedTextField(FormatterFactory.getSymbolFormatter()));
		}
		else if (dataSpecDigest.getType().equals(ColumnDataSpecDigest.TYPE_BOOLEAN)) {
			if (dataSpecDigest.isBlankAllowed()) {
				obj = BooleanCellEditor.newInstance(dataSpecDigest.isBlankAllowed(), viewOnly);
			}
			else {
				obj = null;
			}
		}
		else if (dataSpecDigest.getType().equals(ColumnDataSpecDigest.TYPE_ENUM_LIST)) {
			if (dataSpecDigest.isMultiSelectAllowed()) {
				obj = new MultiSelectEnumCellEditor(templateColumn.getTitle(), dataSpecDigest, viewOnly, tableModel);
			}
			else {
				obj = new EnumCellEditor(dataSpecDigest, templateColumn.getName(), viewOnly, tableModel);
			}
		}
		else if (dataSpecDigest.getType().equals(ColumnDataSpecDigest.TYPE_DATE_RANGE)) {
			obj = new DateRangeCellEditor(dataSpecDigest, viewOnly);
		}
		else if (dataSpecDigest.getType().equals(ColumnDataSpecDigest.TYPE_DATE)) {
			obj = new DateCellEditor(dataSpecDigest, viewOnly);
		}
		else if (dataSpecDigest.getType().equals(ColumnDataSpecDigest.TYPE_TIME_RANGE)) {
			obj = new TimeRangeCellEditor(dataSpecDigest, viewOnly);
		}
		else if (dataSpecDigest.getType().equals(ColumnDataSpecDigest.TYPE_INTEGER_RANGE)) {
			obj = new IntegerRangeCellEditor(dataSpecDigest, viewOnly);
		}
		else if (dataSpecDigest.getType().equals(ColumnDataSpecDigest.TYPE_CURRENCY_RANGE)) {
			obj = new FloatRangeCellEditor(dataSpecDigest, true, viewOnly);
		}
		else if (dataSpecDigest.getType().equals(ColumnDataSpecDigest.TYPE_FLOAT_RANGE)) {
			obj = new FloatRangeCellEditor(dataSpecDigest, false, viewOnly);
		}
		else if (dataSpecDigest.getType().equals(ColumnDataSpecDigest.TYPE_CURRENCY)) {
			obj = new CurrencyCellEditor();
		}
		else if (dataSpecDigest.getType().equals(ColumnDataSpecDigest.TYPE_FLOAT) || dataSpecDigest.getType().equals(ColumnDataSpecDigest.TYPE_PERCENT)) {
			obj = new FloatCellEditor();
		}
		else if (dataSpecDigest.getType().equals(ColumnDataSpecDigest.TYPE_DYNAMIC_STRING)) {
			obj = new DynamicStringCellEditor(columnNameList.toArray(new String[0]), templateColumn.getTitle(), dataSpecDigest, viewOnly);
		}
		return ((TableCellEditor) (obj));
	}

	protected final TableCellRenderer getRenderer(AbstractTemplateColumn templateColumn) {
		ColumnDataSpecDigest dataSpecDigest = templateColumn.getColumnDataSpecDigest();
		if (dataSpecDigest.getType().equals(ColumnDataSpecDigest.TYPE_ENTITY)) {
			if (dataSpecDigest.isMultiSelectAllowed()) {
				return new CategoryEntityMultiSelectCellRenderer(dataSpecDigest);
			}
			else {
				return new CategoryEntitySingleSelectCellRenderer(dataSpecDigest);
			}
		}
		if (dataSpecDigest.getType().equals(ColumnDataSpecDigest.TYPE_BOOLEAN)) {
			return dataSpecDigest.isBlankAllowed() ? new BooleanCellRenderer(true) : null;
		}
		if (dataSpecDigest.getType().equals(ColumnDataSpecDigest.TYPE_CURRENCY_RANGE)) {
			return new CurrencyRangeCellRenderer(dataSpecDigest.getPrecision());
		}
		if (dataSpecDigest.getType().equals(ColumnDataSpecDigest.TYPE_CURRENCY)) {
			return new CurrencyCellRenderer(dataSpecDigest.getPrecision());
		}
		if (dataSpecDigest.getType().equals(ColumnDataSpecDigest.TYPE_FLOAT_RANGE)) {
			return new FloatRangeCellRenderer(dataSpecDigest.getPrecision());
		}
		if (dataSpecDigest.getType().equals(ColumnDataSpecDigest.TYPE_FLOAT) || dataSpecDigest.getType().equals(ColumnDataSpecDigest.TYPE_PERCENT)) {
			return new FloatCellRenderer(dataSpecDigest.getPrecision());
		}
		if (dataSpecDigest.getType().equals(ColumnDataSpecDigest.TYPE_DATE)) {
			TableCellRenderer renderer = (TableCellRenderer) getEditor(templateColumn);
			return renderer == null ? new DateCellEditor(dataSpecDigest, viewOnly) : renderer;
		}
		if (dataSpecDigest.getType().equals(ColumnDataSpecDigest.TYPE_INTEGER)) {
			return new IntegerCellRenderer();
		}
		if (dataSpecDigest.getType().equals(ColumnDataSpecDigest.TYPE_ENUM_LIST)) {
			if (dataSpecDigest.isMultiSelectAllowed()) {
				return new MultiSelectEnumCellRenderer();
			}
			else {
				return new EnumCellRenderer(dataSpecDigest);
			}
		}
		return new DefaultTableCellRenderer();
	}

	protected abstract AbstractTemplateColumn getTemplateColumn(int col);

	protected abstract int getTemplateColumnCount();

	public String getToolTipText(MouseEvent e) {
		Point p = e.getPoint();
		int rowIndex = rowAtPoint(p);
		int colIndex = columnAtPoint(p);
		int realColumnIndex = convertColumnIndexToModel(colIndex);
		if (rowIndex >= 0 && realColumnIndex >= 0) {
			Object value = tableModel.getValueAt(rowIndex, realColumnIndex);
			if (value == null) {
				return null;
			}
			else if (value instanceof Date) {
				return Constants.THREADLOCAL_FORMAT_DATE_TIME_SEC.get().format((Date) value);
			}
			else {
				return value.toString();
			}
		}
		return super.getToolTipText(e);
	}

	private void hideColumn(TableColumn tableColumn) {
		if (tableColumn.getWidth() > 0) {
			tableColumn.setIdentifier(tableColumn.getWidth());
		}
		tableColumn.setMinWidth(0);
		tableColumn.setMaxWidth(0);
		tableColumn.setWidth(0);
	}

	public void hideRuleIDColumns() {
		for (int i = 0; i < columnModel.getColumnCount(); i++) {
			if (getTemplateColumn(i + 1).getColumnDataSpecDigest().isRuleIDType()) {
				hideColumn(getColumnModel().getColumn(i));
			}
		}
	}

	private void initColumnNames() {
		columnNameList.clear();
		for (int i = 0; i < getTemplateColumnCount(); i++) {
			columnNameList.add(getColumnTitle(i + 1));
		}
	}

	private void initColumns() {
		DefaultTableColumnModel defaulttablecolumnmodel = new DefaultTableColumnModel();
		for (int i = 0; i < getTemplateColumnCount(); i++) {
			TableColumn tableColumn = new TableColumn(i);
			tableColumn.setHeaderValue(columnNameList.get(i));
			AbstractTemplateColumn templateColumn = getTemplateColumn(i + 1);
			int j = templateColumn.getColumnWidth();
			if (j > 0) tableColumn.setPreferredWidth(j);
			TableCellEditor tablecelleditor = getEditor(templateColumn);
			if (tablecelleditor != null) tableColumn.setCellEditor(tablecelleditor);
			TableCellRenderer tablecellrenderer = getRenderer(templateColumn);
			if (tablecellrenderer != null) tableColumn.setCellRenderer(tablecellrenderer);
			defaulttablecolumnmodel.addColumn(tableColumn);
			if (getTemplateColumn(i + 1).getColumnDataSpecDigest().isRuleIDType()) {
				tableColumn.setMinWidth(0);
				tableColumn.setIdentifier(tableColumn.getPreferredWidth());
			}
		}

		setColumnModel(defaulttablecolumnmodel);
	}

	protected final void initTable() {
		setAutoResizeMode(JTable.AUTO_RESIZE_SUBSEQUENT_COLUMNS);
		setRowSelectionAllowed(false);
		setColumnSelectionAllowed(true);
		setCellSelectionEnabled(true);
		setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
		setShowHorizontalLines(true);
		setAutoCreateColumnsFromModel(false);
		initColumnNames();
		tableModel.setData(null, columnNameList);
		initColumns();
		getTableHeader().setReorderingAllowed(false);
		setPreferredScrollableViewportSize(new Dimension(400, 216));
		doLayout();
	}

	public boolean isDirty() {
		return tableModel.isDirty();
	}

	public final void populate(Object[][] data) {
		tableModel.setData(data, columnNameList);
	}

	public final void removeRow() {
		int[] ai = getSelectedRows();
		if (ai != null && ai.length > 0) {
			for (int i = ai.length; i > 0; i--) {
				int rowID = ai[i - 1];
				tableModel.removeRow(rowID);

				// if not last row, cancel all editors
				if (tableModel.getRowCount() > rowID) {
					for (int col = 0; col < tableModel.getColumnCount(); col++) {
						TableCellEditor editor = getCellEditor(rowID, col);
						if (editor != null) {
							editor.cancelCellEditing();
						}
					}
				}
			}
		}
	}

	public final void setAndValidateValueAt(Object obj, int i, int j) throws RuntimeException {
		// cancel editing if cell is in edit mode
		int editRow = getEditingRow();
		int editCol = getEditingColumn();

		if (i == editRow && j == editCol) {
			TableCellEditor cellEditor = getCellEditor(i, j);
			if (cellEditor != null) {
				cellEditor.cancelCellEditing();
			}
		}
		tableModel.setAndValidateValueAt(obj, i, j);
		// to correct defect that paste doesn't work for first column if type is Enum
		if (j == 0 && obj instanceof String) {
			Object editor = getCellEditor(i, j);
			if (editor instanceof MultiSelectEnumCellEditor) {
				((MultiSelectEnumCellEditor) editor).setCellEditorValue(tableModel.getValueAt(i, j));
			}
		}
	}

	public final void setColumnWidths(int[] widths) {
		TableColumnModel columnModel = getColumnModel();
		for (int i = 0; i < widths.length && i < columnModel.getColumnCount(); i++) {
			getColumnModel().getColumn(i).setPreferredWidth(widths[i]);
		}
	}

	private void showColumn(TableColumn tableColumn) {
		tableColumn.setMaxWidth(Integer.MAX_VALUE);
		tableColumn.setWidth((Integer) tableColumn.getIdentifier());
		tableColumn.setPreferredWidth(tableColumn.getWidth());
		tableColumn.setMinWidth(12);
	}

	public void showRuleIDColumns() {
		for (int i = 0; i < columnModel.getColumnCount(); i++) {
			if (getTemplateColumn(i + 1).getColumnDataSpecDigest().isRuleIDType()) {
				showColumn(getColumnModel().getColumn(i));
			}
		}
	}

	public void toggleRuleIDColumns() {
		for (int i = 0; i < columnModel.getColumnCount(); i++) {
			if (getTemplateColumn(i + 1).getColumnDataSpecDigest().isRuleIDType()) {
				TableColumn tableColumn = getColumnModel().getColumn(i);
				if (tableColumn.getMaxWidth() > 0) {
					hideColumn(tableColumn);
				}
				else {
					tableColumn.setMaxWidth(Integer.MAX_VALUE);
					tableColumn.setWidth((Integer) tableColumn.getIdentifier());
					tableColumn.setPreferredWidth(tableColumn.getWidth());
					tableColumn.setMinWidth(12);
				}
			}
		}
	}

	public String toString() {
		return tableModel.toString();
	}

	/**
	 * Updates the specified cell.
	 * @param row 1-based row number
	 * @param col 1-based column number
	 * @param value new cell value
	 */
	public void updateCellValue(int row, int col, Object value) {
		if (row > 0 && col > 0) {
			tableModel.setValueAt(value, row - 1, col - 1);
			tableModel.fireTableCellUpdated(row - 1, col - 1);
		}
	}
}