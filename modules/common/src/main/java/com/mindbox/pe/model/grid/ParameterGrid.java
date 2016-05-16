package com.mindbox.pe.model.grid;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.StringTokenizer;

import org.apache.log4j.Logger;

import com.mindbox.pe.model.Auditable;
import com.mindbox.pe.model.Constants;
import com.mindbox.pe.model.DateSynonym;
import com.mindbox.pe.model.exceptions.InvalidDataException;
import com.mindbox.pe.model.template.AbstractTemplateColumn;
import com.mindbox.pe.model.template.ParameterTemplateColumn;

/**
 * @author Geneho Kim
 * @author MindBox
 * @since PowerEditor 2.2.0
 */
public final class ParameterGrid extends AbstractGrid<ParameterTemplateColumn> {

	private static final long serialVersionUID = 2007051500007L;

	private String cellValueStr;

	/**
	 * 
	 * @param gridID gridID
	 * @param templateID templateID
	 * @param effDate effDate
	 * @param expDate expDate
	 */
	public ParameterGrid(int gridID, int templateID, DateSynonym effDate, DateSynonym expDate) {
		super(gridID, templateID, effDate, expDate);
		cellValueStr = "";
	}

	private ParameterGrid(ParameterGrid source) {
		super(source, source.getTemplate(), source.getEffectiveDate(), source.getExpirationDate());
		setCreationDate(source.getCreationDate());
		setCloneOf(source.getCloneOf());
		setID(source.getID());
		copyEntireContext(source);
		this.cellValueStr = source.cellValueStr;
	}

	/**
	 * 
	 * @param grid grid
	 * @param effDate effDate
	 * @param expDate expDate
	 */
	public ParameterGrid(ParameterGrid grid, DateSynonym effDate, DateSynonym expDate) {
		super(grid, effDate, expDate);
		this.cellValueStr = grid.getCellValues();
	}

	@Override
	public void clearValues() {
		this.cellValueStr = "";
	}

	// not used by Parameter grid.
	@Override
	public void copyCellValue(GridValueContainable source) {
		// noop
	}

	@Override
	public Auditable deepCopy() {
		return new ParameterGrid(this);
	}

	private String[] extractColumnNames() {
		List<String> list = new ArrayList<String>();
		for (int i = 1; i <= getTemplate().getNumColumns(); i++) {
			AbstractTemplateColumn element = getTemplate().getColumn(i);
			list.add(element.getName());
		}
		return list.toArray(new String[0]);
	}

	public final String[][] getCellArrays() {
		int i = getNumRows();
		int j = getColumnCount();
		String as[][] = new String[i][j];
		StringTokenizer stringtokenizer = new StringTokenizer(getCellValues(), "~", false);
		for (int k = 0; stringtokenizer.hasMoreTokens(); k++) {
			String s = stringtokenizer.nextToken();
			StringTokenizer stringtokenizer1 = new StringTokenizer(s, "|", false);
			for (int l = 0; stringtokenizer1.hasMoreTokens(); l++) {
				String s1 = stringtokenizer1.nextToken();
				as[k][l] = new String(s1);
			}

		}

		return as;
	}

	public final String getCellValue(int i, int j) throws InvalidDataException {
		return getCellValue(i, j, "");
	}

	public final String getCellValue(int i, int j, String defaultValue) throws InvalidDataException {
		if (i > getNumRows()) throw new InvalidDataException("Row Number", "" + i, "Invalid row number passed in");
		if (j > getColumnCount()) throw new InvalidDataException("Col Number", "" + j, "Invalid column number passed in");
		StringTokenizer stringtokenizer = new StringTokenizer(getCellValues(), "~", false);
		int k = 0;
		while (stringtokenizer.hasMoreTokens()) {
			String s = stringtokenizer.nextToken();
			if (++k == i) {
				StringTokenizer stringtokenizer1 = new StringTokenizer(s, "|", true);
				int l = 0;
				boolean flag = false;
				while (stringtokenizer1.hasMoreTokens()) {
					String s1 = stringtokenizer1.nextToken();
					if (!flag) {
						l++;
						if (s1.equals("|"))
							s1 = "";
						else
							flag = true;
					}
					else {
						flag = false;
					}
					if (l == j) return s1;
				}
			}
		}
		return defaultValue;
	}

	@Override
	public Object getCellValue(int row, String columnName) {
		try {
			return getCellValueObject(row, toColumnNumber(columnName), null);
		}
		catch (InvalidDataException e) {
			Logger.getLogger(getClass()).warn(e);
			return null;
		}
	}

	@Override
	public Object getCellValueObject(int i, int j, Object defaultValue) throws InvalidDataException {
		String value = getCellValue(i, j, null);
		return (value == null ? defaultValue : value);
	}

	public final String getCellValues() {
		return cellValueStr;
	}

	@Override
	public String[] getColumnNames() {
		return extractColumnNames();
	}

	@Override
	public final Object[][] getDataObjects() {
		int i = getNumRows();
		int j = getColumnCount();
		List<Object[]> list = new ArrayList<Object[]>(i);
		for (StringTokenizer st = new StringTokenizer(getCellValues(), "~", false); st.hasMoreTokens();) {
			List<String> list1 = new ArrayList<String>(j);
			String s = st.nextToken();
			StringTokenizer st1 = new StringTokenizer(s, "|", true);
			boolean flag = false;
			while (st1.hasMoreTokens()) {
				String s1 = st1.nextToken();
				if (!flag) {
					if (s1.equals("|"))
						s1 = "";
					else
						flag = true;
					list1.add(s1);
				}
				else {
					flag = false;
				}
			}
			list.add(list1.toArray(new Object[0]));
		}
		return list.toArray(new Object[0][0]);
	}

	@Override
	protected boolean hasSameCellValues(AbstractGrid<ParameterTemplateColumn> abstractgrid) {
		if (abstractgrid instanceof ParameterGrid) {
			return isSame(((ParameterGrid) abstractgrid).getCellValues(), getCellValues());
		}
		else {
			return false;
		}
	}

	// not used by Parameter grid.
	@Override
	public boolean hasSameCellValues(GridValueContainable valueContainer) {
		return false;
	}

	@Override
	public boolean hasSameRow(int row, String[] columns, int targeRow, GridValueContainable valueContainable) {
		//return false for parameter grid
		return false;
	}

	@Override
	public boolean isEmpty() {
		return cellValueStr == null || cellValueStr.length() == 0;
	}

	@Override
	public boolean isEmptyRow(int row) {
		// return false for parameter grid
		return false;
	}

	@Override
	public boolean isParameterGrid() {
		return true;
	}

	public final void setCellValues(String s) {
		cellValueStr = s;
	}

	public final void setDataList(List<List<Object>> list) {
		if (list == null) {
			cellValueStr = "";
			setNumRows(0);
			return;
		}
		setNumRows(list.size());
		StringBuilder stringbuffer = new StringBuilder();
		for (int i = 0; i < list.size(); i++) {
			List<Object> list1 = list.get(i);
			for (int j = 0; j < list1.size(); j++) {
				boolean flag1 = false;
				if (list1.size() - 1 == j) flag1 = true;
				Object obj = list1.get(j);
				stringbuffer.append(obj == null ? "" : (obj instanceof Date ? Constants.THREADLOCAL_FORMAT_DATE_TIME_SEC.get().format((Date) obj) : obj.toString()));
				if (!flag1) stringbuffer.append("|");
			}

			boolean flag = false;
			if (list.size() - 1 == i) flag = true;
			if (!flag) stringbuffer.append("~");
		}

		cellValueStr = stringbuffer.toString();
		setNumRows(list.size());
	}

	@Override
	public void setValue(int rowID, int col, Object value) {
		// not used by Parameter grid.
	}

	@Override
	public void setValue(int rowID, String columnName, Object value) {
		// not used by Parameter grid.
	}

	private int toColumnNumber(String columnName) {
		String[] columnNames = getColumnNames();
		for (int i = 0; i < columnNames.length; i++) {
			if (columnNames[i].equals(columnName)) {
				return i + 1;
			}
		}
		return -1;
	}

	public String toDetailString() {
		return "ParameterGrid[{" + super.toString() + "}";
	}

	@Override
	public String toString() {
		return "ParameterGrid" + super.toString();
	}
}
