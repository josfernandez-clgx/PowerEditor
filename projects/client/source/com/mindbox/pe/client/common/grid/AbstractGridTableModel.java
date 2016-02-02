package com.mindbox.pe.client.common.grid;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.swing.table.DefaultTableModel;

import org.apache.log4j.Logger;

import com.mindbox.pe.client.ClientUtil;
import com.mindbox.pe.common.UtilBase;
import com.mindbox.pe.common.config.UIConfiguration;
import com.mindbox.pe.communication.ServerException;
import com.mindbox.pe.model.AbstractTemplateColumn;
import com.mindbox.pe.model.AbstractTemplateCore;
import com.mindbox.pe.model.ColumnDataSpecDigest;
import com.mindbox.pe.model.EnumValue;
import com.mindbox.pe.model.table.CategoryOrEntityValue;
import com.mindbox.pe.model.table.CategoryOrEntityValues;
import com.mindbox.pe.model.table.EnumValues;
import com.mindbox.pe.model.table.FloatRange;
import com.mindbox.pe.model.table.IntegerRange;
import com.mindbox.pe.model.table.TimeRange;

public abstract class AbstractGridTableModel<T extends AbstractTemplateCore<?>> extends DefaultTableModel {

	private static final boolean equalAsString(Object obj, Object obj1) {
		if ((obj != null && obj1 == null) || (obj == null && obj1 != null)) return false;
		if (obj == null && obj1 == null)
			return true;
		else {
			return obj.toString().equals(obj1.toString());
		}
	}

	private static long getNextRuleID() throws ServerException {
		return ClientUtil.getCommunicator().getNextRuleID();
	}

	protected abstract ColumnDataSpecDigest getColumnDataSpecDigest(int columnNo);

	protected abstract int getTemplateMaxRow();

	protected abstract int getTemplateColumnCount();

	protected abstract T getTemplate();

	private boolean isDirty;
	private final Logger logger;

	protected AbstractGridTableModel() {
		logger = Logger.getLogger(getClass());
		isDirty = false;
	}

	private final boolean isNonEmptyDependentColumn(int row, String sourceColumnName, AbstractTemplateColumn column, int columnNo) {
		return (!column.getName().equals(sourceColumnName) && 
				column.getColumnDataSpecDigest().isEnumListAndSelectorSetFor(sourceColumnName) && 
				!UtilBase.isEmptyCellValue(getValueAt(row,columnNo - 1)));
	}

	/**
	 * Tests if there is a dependent column of this with a value in it. 
	 * 
	 * @return <code>true</code> if there is a non-empty dependent column; <code>false</code>, otherwise
	 */
	public boolean hasNonEmptyDepedentCell(int row, String columnName) {
		for (int c = 1; c <= getTemplateColumnCount(); c++) {
			AbstractTemplateColumn column = getTemplate().getColumn(c);
			if (isNonEmptyDependentColumn(row, columnName, column, c)) {
				return true;
			}
		}
		return false;
	}

	public List<Integer> getNonEmptyDependentColumnIDs(int row, String columnName) {
		List<Integer> list = new ArrayList<Integer>();
		for (int c = 1; c <= getTemplateColumnCount(); c++) {
			AbstractTemplateColumn column = getTemplate().getColumn(c);
			if (isNonEmptyDependentColumn(row, columnName, column, c)) {
				list.add(c);
			}
		}
		return list;
	}

	public void clearNonEmptyDependentColumns(int row, String columnName) {
		for (int c = 1; c <= getTemplateColumnCount(); c++) {
			AbstractTemplateColumn column = getTemplate().getColumn(c);
			if (isNonEmptyDependentColumn(row, columnName, column, c)) {
				setCellValue(null, row, c - 1);
			}
		}
		fireTableRowsUpdated(row, row);
	}

	private Object getDefaultValue(int i) throws ServerException {
		try {
			ColumnDataSpecDigest dataSpecDigest = getColumnDataSpecDigest(i + 1);
			if (dataSpecDigest.getType().equals(ColumnDataSpecDigest.TYPE_ENTITY)) {
				return null;
			}
			else if (dataSpecDigest.isRuleIDType()) {
				return new Long(getNextRuleID());
			}
			if (dataSpecDigest.isBlankAllowed()) {
				return "";
			}
			if (dataSpecDigest.getType().equals(ColumnDataSpecDigest.TYPE_FLOAT_RANGE)
					|| dataSpecDigest.getType().equals(ColumnDataSpecDigest.TYPE_CURRENCY_RANGE)) {
				return FloatRange.getDefaultValue(dataSpecDigest);
			}
			if (dataSpecDigest.getType().equals(ColumnDataSpecDigest.TYPE_TIME_RANGE)) {
				return TimeRange.getDefaultValue(dataSpecDigest);
			}
			if (dataSpecDigest.getType().equals(ColumnDataSpecDigest.TYPE_INTEGER_RANGE)) {
				return IntegerRange.getDefaultValue(dataSpecDigest);
			}
			if (dataSpecDigest.getType().equals(ColumnDataSpecDigest.TYPE_ENUM_LIST)) {
				return EnumValues.getDefaultValue();
			}
			if (getColumnClass(i).equals(Integer.class)) return new Integer(0);
			if (getColumnClass(i).equals(Double.class)) return new Double(0.0d);
			if (getColumnClass(i).equals(Float.class)) return new Float(0.0f);
			if (getColumnClass(i).equals(Boolean.class)) return Boolean.FALSE;
			if (getColumnClass(i).equals(Date.class)) return null;
			if (getColumnClass(i).equals(String.class))
				return "";
			else
				return getColumnClass(i).newInstance();
		}
		catch (ServerException ex) {
			throw ex;
		}
		catch (Exception ex) {
			Logger.getLogger(getClass()).error("Exception in getDefaultValue for " + i, ex);
			return null;
		}
	}

	/**
	 * Update the dirty flag.
	 * When overriding this, make sure to call <code>super.setDirty(flag)</code>.
	 * @param flag
	 */
	public void setDirty(boolean flag) {
		isDirty = flag;
	}

	/**
	 * @return A String of the values of of the rows and columns in a "grid" display.
	 * @since PowerEditor 4.2.0
	 */
	public String toString() {
		StringBuffer retString = new StringBuffer();

		for (int idx = 0; idx < super.getRowCount(); idx++) {
			retString.append("     ");
			for (int jdx = 0; jdx < super.getColumnCount(); jdx++) {
				retString.append(getValueAt(idx, jdx));
				retString.append(",   ");
			}
			retString.append("\n");
		}
		return retString.toString();
	}

	public final void setValueAt(Object value, int row, int col) {
		if (!UtilBase.isSameGridCellValue(value, getValueAt(row, col))) {
			setCellValue(value, row, col);
		}
	}

	public final synchronized boolean hasCell(int row, int col) {
		return row >= 0 && col >= 0 && row < getRowCount() && col < getColumnCount();
	}

	// Returns true if and only if 
	// (1) a cell exists at [row,col], and 
	// (2) after setCellValue returns the cell's value is equivalent to the argument "obj".
	private final synchronized boolean setCellValue(Object obj, int row, int col) {
		if (!hasCell(row, col)) {
			return false; // no such cell
		}

		if (equalAsString(obj, super.getValueAt(row, col))) {
			return true; // value unchanged
		}
		try { // else value has changed, try to set it
			Object objToSet = obj; // default to "obj", unless overridden below...

			ColumnDataSpecDigest columnDataSpecDigest = getColumnDataSpecDigest(col + 1);
			Class<?> colClass = getColumnClass(col);

			if (ExcelAdapter.isNullEmptyString(obj)) { // new value is empty
				if (columnDataSpecDigest.getType().equals(ColumnDataSpecDigest.TYPE_BOOLEAN)) {
					objToSet = Boolean.FALSE; // blank boolean value is always interpreted as false

				}
				else if (!columnDataSpecDigest.isBlankAllowed()) {
					logger.warn("column " + String.valueOf(col + 1) + " does not allow null");
					return false;
				}
				// else empty allowed, set it
			}
			else { // new value *not* empty
				if (columnDataSpecDigest.getType().equals(ColumnDataSpecDigest.TYPE_ENTITY)) {
					objToSet = obj;
				}
				else if (Boolean.class.equals(colClass)) {
					objToSet = Boolean.valueOf(obj.toString());
				}
				else if (Date.class.equals(colClass)) {
					if (obj instanceof Date) {
						objToSet = (Date) obj;
					}
					else {
						DateFormat formatter = columnDataSpecDigest.getType().equals(ColumnDataSpecDigest.TYPE_DATE_TIME)
								? UIConfiguration.FORMAT_DATE_TIME_MIN
								: UIConfiguration.FORMAT_DATE;
						objToSet = formatter.parse(obj.toString());
					}
				}
				else if (columnDataSpecDigest.getType().equals(ColumnDataSpecDigest.TYPE_FLOAT_RANGE)
						|| columnDataSpecDigest.getType().equals(ColumnDataSpecDigest.TYPE_CURRENCY_RANGE)) {
					// TT 1651
					if (obj instanceof FloatRange) {
						objToSet = (FloatRange) obj;
					}
					else {
						if (!CellValidator.validateValue(obj.toString(), columnDataSpecDigest)) {
							logger.warn("invalid float range value [" + obj + "] for column [" + String.valueOf(col + 1) + "].");
							ClientUtil.getInstance().showErrorDialog(
									"msg.warning.invalid.value.cell",
									new Object[] { obj, String.valueOf(col + 1) });
							return false;
						}
						objToSet = FloatRange.parseValue(obj.toString());
					}
				}
				else if (columnDataSpecDigest.getType().equals(ColumnDataSpecDigest.TYPE_TIME_RANGE)) {
					// TT 2134
					if (obj instanceof TimeRange) {
						objToSet = (TimeRange) obj;
					}
					else {
						if (!CellValidator.validateValue(obj.toString(), columnDataSpecDigest)) {
							logger.warn("invalid time range value [" + obj + "] for column [" + String.valueOf(col + 1) + "].");
							ClientUtil.getInstance().showErrorDialog(
									"msg.warning.invalid.value.cell",
									new Object[] { obj, String.valueOf(col + 1) });
							return false;
						}
						objToSet = TimeRange.parseTimeRangeValue(obj.toString());
					}
				}
				else if (columnDataSpecDigest.getType().equals(ColumnDataSpecDigest.TYPE_INTEGER_RANGE)) {
					// TT 1911 
					if (obj instanceof IntegerRange) {
						objToSet = (IntegerRange) obj;
					}
					else {
						if (!CellValidator.validateValue(obj.toString(), columnDataSpecDigest)) {
							logger.warn("invalid integer range value [" + obj + "] for column [" + String.valueOf(col + 1) + "].");
							ClientUtil.getInstance().showErrorDialog(
									"msg.warning.invalid.value.cell",
									new Object[] { obj, String.valueOf(col + 1) });
							return false;
						}
						objToSet = IntegerRange.parseValue(obj.toString());
					}
				}
				else if (columnDataSpecDigest.isDoubleType()) {
					Number number = (obj instanceof Number ? (Number) obj : Double.valueOf(obj.toString()));
					double f = number.doubleValue();
					if (!CellValidator.validateValue(f, columnDataSpecDigest)) {
						logger.warn("invalid float value [" + f + "] for column [" + String.valueOf(col + 1) + "].");
						ClientUtil.getInstance().showErrorDialog(
								"msg.warning.invalid.value.cell",
								new Object[] { String.valueOf(f), String.valueOf(col + 1) });
						return false;
					}
					objToSet = number;
				}
				else if (columnDataSpecDigest.getType().equals(ColumnDataSpecDigest.TYPE_INTEGER)) {
					Integer integer = obj instanceof Integer ? (Integer) obj : (obj instanceof Number ? new Integer(
							((Number) obj).intValue()) : Integer.parseInt(obj.toString()));
					int i = integer.intValue();
					if (!CellValidator.validateValue(i, columnDataSpecDigest)) {
						logger.warn("invalid integer value [" + i + "] for column [" + String.valueOf(col + 1) + "].");
						ClientUtil.getInstance().showErrorDialog(
								"msg.warning.invalid.value.cell",
								new Object[] { String.valueOf(i), String.valueOf(col + 1) });
						return false;
					}
					// GKim: TT 2186 - Set value to Integer
					objToSet = integer;
				}
			}

			// value is changed and valid, set it.
			super.setValueAt(objToSet, row, col);
			setDirty(true);
			return true;

		}
		catch (Exception exception) {
			logger.error(obj, exception);
			return false;
		}
	}

	public final Object getCellValueAt(int row, String columnName) {
		return getValueAt(row, getColumnIndex(columnName));
	}

	public final int getColumnIndex(String columnName) {
		return getTemplate().getColumn(columnName).getColumnNumber() - 1;
	}

	public final synchronized Object getValueAt(int row, int col) {
		if (super.getRowCount() == 0 || row >= super.getRowCount() || col >= super.getColumnCount()) {
			return null;
		}
		Object obj = super.getValueAt(row, col);
		ColumnDataSpecDigest columnDataSpecDigest = getColumnDataSpecDigest(col + 1);
		if (getColumnClass(col).equals(Boolean.class)) {
			obj = (obj == null ? Boolean.FALSE : Boolean.valueOf(obj.toString()));
		}
		else if (getColumnClass(col).equals(Date.class)) {
			if (obj != null) {
				try {
					if (obj instanceof String && ((String) obj).length() == 0) {
						obj = null;
					}
					else if (!(obj instanceof Date)) {
						String str = obj.toString();
						if (columnDataSpecDigest.getType().equals(ColumnDataSpecDigest.TYPE_DATE_TIME)) {
							obj = UIConfiguration.FORMAT_DATE_TIME_MIN.parse(str);
						}
						else if (columnDataSpecDigest.getType().equals(ColumnDataSpecDigest.TYPE_DATE)) {
							obj = UIConfiguration.FORMAT_DATE.parse(str);
						}
						else {
							logger.warn("Invalid object for date field: " + obj);
							obj = null;
						}
					}
				}
				catch (Exception exception) {
					logger.error("failed to parse as date: " + obj, exception);
					obj = null;
				}
			}
		}
		return obj;
	}

	public final void setData(Object[][] data, List<String> columnNameList) {
		setDataVector(data == null ? new Object[0][0] : data, columnNameList.toArray());
		setDirty(false);
	}

	public boolean isCellEditable(int i, int j) {
		ColumnDataSpecDigest dataSpecDigest = getColumnDataSpecDigest(j + 1);
		return !dataSpecDigest.isRuleIDType();
	}

	public final boolean isDirty() {
		return isDirty;
	}

	final boolean addRow(int row, List<Object> list) {
		int maxRow = getTemplateMaxRow();
		if (maxRow > 0 && getRowCount() >= maxRow) {
			ClientUtil.getInstance().showErrorDialog("MaxRowsExceededMsg");
			return false;
		}

		if (row >= 0 && row < getRowCount()) {
			super.insertRow(row, list.toArray());
		}
		else {
			super.addRow(list.toArray());
		}
		setDirty(true);
		return true;
	}

	final boolean addRow(int row) throws ServerException {
		int colCount = getTemplateColumnCount();
		List<Object> list = new ArrayList<Object>(colCount);
		for (int i = 0; i < colCount; i++)
			list.add(getDefaultValue(i));
		return addRow(row, list);
	}

	public final void setAndValidateValueAt(Object obj, int i, int j) throws RuntimeException {
		if (!UtilBase.isSameGridCellValue(obj, getValueAt(i, j))) {
			if (validateValue(obj, j)) {
				setDirty(true);
				setCellValue(obj, i, j);
			}
			else {
				logger.warn("Invalid value " + obj + " for col " + j);
				throw new RuntimeException("Invalid Paste Value = '" + obj + "'");
			}
		}
	}

	private boolean validateValue(Object obj, int col) {
		boolean flag = true;
		try {
			ColumnDataSpecDigest columnDataSpecDigest = getColumnDataSpecDigest(col + 1);
			// check for null
			if (ExcelAdapter.isNullEmptyString(obj)) {
				return columnDataSpecDigest.isBlankAllowed();
			}
			else if (columnDataSpecDigest.getType().equals(ColumnDataSpecDigest.TYPE_ENTITY)) {
				// table cell editors for Entity type validates data; just return true
				return true;
			}
			else if (columnDataSpecDigest.getType().equals(ColumnDataSpecDigest.TYPE_ENUM_LIST)) {
				if (obj instanceof EnumValues || obj instanceof EnumValue) {
					flag = true;
				}
				else {
					flag = CellValidator.validateValue((String) obj, columnDataSpecDigest);
				}
			}
			else if (columnDataSpecDigest.getType().equals(ColumnDataSpecDigest.TYPE_FLOAT)
					|| columnDataSpecDigest.getType().equals(ColumnDataSpecDigest.TYPE_PERCENT)) {
				if (obj instanceof String)
					flag = CellValidator.validateValue((String) obj, columnDataSpecDigest);
				else
					flag = CellValidator.validateValue(((Double) obj).floatValue(), columnDataSpecDigest);
			}
			else if (columnDataSpecDigest.getType().equals(ColumnDataSpecDigest.TYPE_INTEGER)) {
				if (obj instanceof String)
					flag = CellValidator.validateValue((String) obj, columnDataSpecDigest);
				else
					flag = CellValidator.validateValue(((Integer) obj).intValue(), columnDataSpecDigest);
			}
			else {
				flag = CellValidator.validateValue(obj.toString(), columnDataSpecDigest);
			}
		}
		catch (Exception exception) {
			exception.printStackTrace();
			flag = false;
		}
		return flag;
	}

	public final Class<?> getColumnClass(int i) {
		ColumnDataSpecDigest dataSpecDigest = getColumnDataSpecDigest(i + 1);
		if (dataSpecDigest != null && dataSpecDigest.getType().equals(ColumnDataSpecDigest.TYPE_ENTITY)) {
			if (dataSpecDigest.isMultiSelectAllowed()) {
				return CategoryOrEntityValues.class;
			}
			else {
				return CategoryOrEntityValue.class;
			}
		}
		else if (dataSpecDigest.isRuleIDType()) {
			return Long.class;
		}
		else if (dataSpecDigest.getType().equals(ColumnDataSpecDigest.TYPE_BOOLEAN)) {
			return (dataSpecDigest.isBlankAllowed() ? String.class : Boolean.class);
		}
		else if (dataSpecDigest.getType().equals(ColumnDataSpecDigest.TYPE_INTEGER_RANGE)) {
			return IntegerRange.class;
		}
		else if (dataSpecDigest.getType().equals(ColumnDataSpecDigest.TYPE_INTEGER)) {
			return Integer.class;
		}
		else if (dataSpecDigest.getType().equals(ColumnDataSpecDigest.TYPE_FLOAT_RANGE)
				|| dataSpecDigest.getType().equals(ColumnDataSpecDigest.TYPE_CURRENCY_RANGE)) {
			return FloatRange.class;
		}
		else if (dataSpecDigest.isDoubleType()) {
			return Double.class;
		}
		else if (dataSpecDigest.getType().equals(ColumnDataSpecDigest.TYPE_ENUM_LIST)) {
			if (dataSpecDigest.isMultiSelectAllowed()) {
				return EnumValues.class;
			}
			else {
				return String.class;
			}
		}
		else if (dataSpecDigest.getType().equals(ColumnDataSpecDigest.TYPE_DATE_RANGE)
				|| dataSpecDigest.getType().equals(ColumnDataSpecDigest.TYPE_DATE_TIME_RANGE)) {
			return String.class;
		}
		else if (dataSpecDigest.getType().equals(ColumnDataSpecDigest.TYPE_DATE)) {
			return Date.class;
		}
		return String.class;
	}

}
