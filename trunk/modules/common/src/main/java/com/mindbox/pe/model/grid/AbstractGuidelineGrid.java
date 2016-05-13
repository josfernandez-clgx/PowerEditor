package com.mindbox.pe.model.grid;

import java.util.List;
import java.util.Map;

import com.mindbox.pe.model.DateSynonym;
import com.mindbox.pe.model.exceptions.InvalidDataException;
import com.mindbox.pe.model.template.AbstractTemplateCore;
import com.mindbox.pe.model.template.GridTemplate;
import com.mindbox.pe.model.template.GridTemplateColumn;

/**
 * A grid.
 * 
 * @author Gene Kim
 * @author MindBox
 * @since PowerEditor 1.0
 */
public abstract class AbstractGuidelineGrid extends AbstractGrid<GridTemplateColumn> {

	private static final long serialVersionUID = 2004010211120000L;

	private final GridValueContainer<GridTemplateColumn> valueContainer;

	/**
	 * Construts a new guideline grid that is a copy of the specified source grid. This creates an
	 * identical grid except:
	 * <ol>
	 * <li>The id is set to <code>-1</code>, not <code>sourceGrid.getID()</code>.</li>
	 * <li>The cloneOf is set to <code>sourceGrid.getID()</code>, not
	 * <code>sourceGrid.getCloneOf()</code>.</li>
	 * <li>The creation date is set to <code>new Date()</code>, not
	 * <code>sourceGrid.getCreationDate()</code>.</li>
	 * <li>The status is set to "Draft", not <code>sourceGrid.getStatus()</code>.</li>
	 * <li>The last status change date is set to <code>new Date()</code>, not
	 * <code>sourceGrid.getStatusChangeDate()</code>.</li>
	 * </ol>
	 * This replaces old clone() method.
	 * 
	 * @param sourceGrid
	 *            source grid
	 * @since PowerEditor 4.2.0
	 */
	protected AbstractGuidelineGrid(AbstractGuidelineGrid sourceGrid) {
		super(sourceGrid);
		this.valueContainer = new GridValueContainer<GridTemplateColumn>(sourceGrid.getTemplate());
		this.valueContainer.copyCellValue(sourceGrid.valueContainer);
	}

	protected AbstractGuidelineGrid(AbstractGuidelineGrid grid, DateSynonym effDate, DateSynonym expDate) {
		super(grid, effDate, expDate);
		this.valueContainer = new GridValueContainer<GridTemplateColumn>(grid.getTemplate());
		this.valueContainer.copyCellValue(grid.valueContainer);
	}

	/**
	 * Constructs a new instance copying the specified grid, differing in effective dating and the
	 * template.
	 * 
	 * @param grid
	 * @param template
	 * @param effDate
	 * @param expDate
	 * @since PowerEditor 4.2.0
	 */
	protected AbstractGuidelineGrid(AbstractGuidelineGrid grid, GridTemplate template, DateSynonym effDate, DateSynonym expDate) {
		super(grid, template, effDate, expDate);
		this.valueContainer = new GridValueContainer<GridTemplateColumn>(template);
		this.valueContainer.copyCellValue(grid.valueContainer);
	}

	protected AbstractGuidelineGrid(int gridID, GridTemplate template, DateSynonym effDate, DateSynonym expDate) {
		super(gridID, template, effDate, expDate);
		this.valueContainer = new GridValueContainer<GridTemplateColumn>(template);
	}

	@Override
	public void clearValues() {
		valueContainer.clearValues();
	}

	@Override
	public final void copyCellValue(GridValueContainable source) {
		valueContainer.copyCellValue(source);
	}

	public void copyColumns(List<String> columnNames, GridValueContainable sourceGrid) {
		for (int row = 1; row <= getNumRows(); row++) {
			for (String columnName : columnNames) {
				setValue(row, columnName, sourceGrid.getCellValue(row, columnName));
			}
		}
	}

	@Override
	public final Object getCellValue(int row, String column) {
		return valueContainer.getCellValue(row, column);
	}

	@Override
	public Object getCellValueObject(int i, int j, Object defaultValue) throws InvalidDataException {
		Object value = valueContainer.getCellValueObject(i, j, defaultValue);
		return value;
	}

	@Override
	public final String[] getColumnNames() {
		return valueContainer.getColumnNames();
	}

	@Override
	public final Object[][] getDataObjects() {
		return valueContainer.getDataObjects();
	}

	@Override
	public final int getNumRows() {
		return valueContainer.getNumRows();
	}

	public boolean hasRuleID(long ruleID) {
		if (getTemplate().hasRuleIDColumn()) {
			List<String> columnNames = getTemplate().getRuleIDColumnNames();
			for (String column : columnNames) {
				for (int row = 1; row <= getNumRows(); row++) {
					Object cellValue = getCellValue(row, column);
					if (cellValue instanceof Number && ((Number) cellValue).longValue() == ruleID) {
						return true;
					}
					else if (cellValue instanceof String && String.valueOf(ruleID).equals(cellValue)) {
						return true;
					}
				}
			}
		}
		return false;
	}

	// GridValueContainable implementations -------------------------

	@Override
	protected final boolean hasSameCellValues(AbstractGrid<GridTemplateColumn> abstractgrid) {
		if (abstractgrid instanceof AbstractGuidelineGrid) {
			return valueContainer.hasSameCellValues(((AbstractGuidelineGrid) abstractgrid).valueContainer);
		}
		else {
			return false;
		}
	}

	@Override
	public final boolean hasSameCellValues(GridValueContainable valueContainer) {
		return this.valueContainer.hasSameCellValues(valueContainer);
	}

	@Override
	public final boolean hasSameRow(int row, String[] columnNames, int targetRow, GridValueContainable valueContainable) {
		return this.valueContainer.hasSameRow(row, columnNames, targetRow, valueContainable);
	}

	@Override
	public boolean identical(Object obj) {
		if (obj instanceof AbstractGuidelineGrid) {
			AbstractGuidelineGrid abstractgrid = (AbstractGuidelineGrid) obj;
			return equals(abstractgrid) && isSame(abstractgrid.getComments(), getComments());
		}
		else {
			return false;
		}
	}

	@Override
	public final boolean isEmpty() {
		return valueContainer.isEmpty();
	}

	@Override
	public final boolean isEmptyRow(int row) {
		return valueContainer.isEmptyRow(row);
	}

	public void setDataList(List<List<Object>> list) {
		valueContainer.setDataList(list);
	}

	@Override
	public final void setNumRows(int rows) {
		valueContainer.setNumRows(rows);
	}

	@Override
	public final void setTemplate(AbstractTemplateCore<GridTemplateColumn> template) {
		super.setTemplate(template);
		this.valueContainer.setTemplate(template);
	}

	@Override
	public final void setValue(int i, int j, Object value) {
		valueContainer.setValue(i, j, value);
	}

	@Override
	public final void setValue(int rowID, String columnName, Object value) {
		valueContainer.setValue(rowID, columnName, value);
	}

	@Override
	public String toString() {
		return "GuidelineGrid" + super.toString() + "@" + Integer.toHexString(hashCode());
	}

	// TT-19: Update values for rearranged columns
	/**
	 * Updates values for rearranged columns.
	 * @param rearrangedColumnMap key=old-id, value=new-id
	 * @return true if at least one cell was updated; false, otherwise
	 */
	public boolean updateValuesForRearrangedColumns(final Map<Integer, Integer> rearrangedColumnMap) {
		return valueContainer.updateValuesForRearrangedColumns(rearrangedColumnMap);
	}
}