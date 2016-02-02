package com.mindbox.pe.server.generator;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import com.mindbox.pe.model.ContextContainer;
import com.mindbox.pe.model.DateSynonym;
import com.mindbox.pe.model.GenericEntityType;
import com.mindbox.pe.model.GridTemplate;
import com.mindbox.pe.model.GuidelineContext;
import com.mindbox.pe.model.ProductGrid;
import com.mindbox.pe.model.TemplateUsageType;
import com.mindbox.pe.model.exceptions.InvalidDataException;
import com.mindbox.pe.model.table.CategoryOrEntityValue;
import com.mindbox.pe.model.table.CategoryOrEntityValues;
import com.mindbox.pe.server.cache.EntityManager;
import com.mindbox.pe.server.model.GenericEntityIdentity;
import com.mindbox.pe.server.model.TimeSlice;

public class GuidelineGenerateParams extends AbstractGenerateParms implements ContextContainer {

	/**
	 * Gets the cell value objects for the specified row.
	 * Note that row is not zero-based. first row is 1, not 0.
	 * To get the string representation of the cell values, use
	 * {@link #getCellValue(int,int)} method, instead.
	 * @param grid the grid
	 * @param row the row number
	 * @return the cell values of row <code>i</code> as List
	 * @throws InvalidDataException
	 */
	private static List<Object> getRow(ProductGrid grid, int row) throws InvalidDataException {
		List<Object> list = new LinkedList<Object>();
		if (row > grid.getNumRows()) throw new InvalidDataException("Row Number", "" + row, "Invalid row number passed in");

		for (int i = 1; i <= grid.getTemplate().getNumColumns(); i++) {
			list.add(grid.getCellValueObject(row, i, null));
		}

		return list;
	}

	private final ContextContainer contextContainer;
	private final boolean spansMultipleActivations;

	/**
	 * Equivalent to <code>this(sunrise, sunset, grid, columnNo, rowNum, false)</code>.
	 * @param sunrise
	 * @param sunset
	 * @param grid
	 * @param columnNo
	 * @param rowNum
	 * @throws InvalidDataException
	 */
	public GuidelineGenerateParams(DateSynonym sunrise, DateSynonym sunset, ProductGrid grid, int columnNo, int rowNum)
			throws InvalidDataException {
		this(sunrise, sunset, grid, columnNo, rowNum, false);
	}

	/**
	 * Creates a new guideline generate param instance.
	 * @param sunrise
	 * @param sunset
	 * @param grid
	 * @param columnNo
	 * @param rowNum
	 * @param spansMultipleActivations
	 * @throws InvalidDataException
	 */
	public GuidelineGenerateParams(DateSynonym sunrise, DateSynonym sunset, ProductGrid grid, int columnNo, int rowNum,
			boolean spansMultipleActivations) throws InvalidDataException {
		super(grid.getID(), sunrise, sunset, (GridTemplate) grid.getTemplate(), columnNo, rowNum, getRow(grid, rowNum), grid.getStatus());
		this.contextContainer = grid;
		this.spansMultipleActivations = spansMultipleActivations;
	}

	public boolean spansMultipleActivations() {
		return spansMultipleActivations;
	}

	public GuidelineContext[] extractGuidelineContext() {
		return contextContainer.extractGuidelineContext();
	}

	// TODO Kim: add test
	public int[] extractEntityIDsForControlPattern(TimeSlice timeSlice, GenericEntityType type) {
		if (hasGenericEntityContext(type)) {
			return getGenericEntityIDs(type);
		}
		else if (hasGenericCategoryContext(type)) {
			int[] genericCatIDs = getGenericCategoryIDs(type);
			GenericEntityIdentity[] entities = EntityManager.getInstance().getGenericEntitiesInCategorySetAsOfWithDescendents(
					type.getCategoryType(),
					genericCatIDs,
					timeSlice.getAsOfDate(),
					false); // false to indicate that we care about dates on entity-category associations
			if (entities != null && entities.length > 0) {
				int[] ids = new int[entities.length];
				for (int i = 0; i < ids.length; i++) {
					ids[i] = entities[i].getEntityID();
				}
				return ids;
			}
		}
		return null;
	}

	public TemplateUsageType getUsage() {
		return getTemplate().getUsageType();
	}

	public boolean hasAnyGenericCategoryContext() {
		return contextContainer.hasAnyGenericCategoryContext();
	}

	public boolean hasGenericCategoryContext(GenericEntityType type) {
		return contextContainer.hasGenericCategoryContext(type);
	}

	public GenericEntityType[] getGenericCategoryEntityTypesInUse() {
		return contextContainer.getGenericCategoryEntityTypesInUse();
	}

	public int[] getGenericCategoryIDs(GenericEntityType type) {
		return contextContainer.getGenericCategoryIDs(type);
	}

	public boolean hasAnyGenericEntityContext() {
		return contextContainer.hasAnyGenericEntityContext();
	}

	public boolean hasGenericEntityContext(GenericEntityType type) {
		return contextContainer.hasGenericEntityContext(type);
	}

	public GenericEntityType[] getGenericEntityTypesInUse() {
		return contextContainer.getGenericEntityTypesInUse();
	}

	public int[] getGenericEntityIDs(GenericEntityType type) {
		return contextContainer.getGenericEntityIDs(type);
	}

	public boolean hasGenericCategoryAsCellValue() {
		for (Iterator<Object> iter = getRowData().iterator(); iter.hasNext();) {
			Object cellValue = (Object) iter.next();
			if (cellValue != null) {
				if (cellValue instanceof CategoryOrEntityValue && !((CategoryOrEntityValue) cellValue).isForEntity()) {
					return true;
				}
				else if (cellValue instanceof CategoryOrEntityValues && ((CategoryOrEntityValues) cellValue).hasGenericCategoryReference()) {
					return true;
				}
			}
		}
		return false;
	}
}
