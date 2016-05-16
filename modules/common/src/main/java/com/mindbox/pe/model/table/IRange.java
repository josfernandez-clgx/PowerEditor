package com.mindbox.pe.model.table;

/**
 * Range value interface.
 * @author Geneho Kim
 * @since PowerEditor
 */
public interface IRange extends GridCellValue {

	/**
	 * Formats the specified value appropriate for value this range accepts.
	 * @param value value
	 * @return formatted value
	 */
	String formatValue(Object value);

	Number getCeiling();

	Number getFloor();

	/**
	 * Tests if this is empty.
	 * @return <code>true</code> if this is empty; <code>false</code>, otherwise
	 */
	boolean isEmpty();

	/**
	 * Tests if this contains date values.
	 * @return <code>true</code> if this contains date values; <code>false</code>, otherwise
	 */
	boolean isForDate();

	boolean isLowerValueInclusive();

	boolean isUpperValueInclusive();

	/**
	 * Tests if this represennts a single value, not a range.
	 * This should return <code>false</code> if {@link #isEmpty()} returns <code>true</code>.
	 * @return <code>true</code> if this represents a single value; <code>false</code>, otherwise
	 */
	boolean representsSingleValue();

	void setLowerValueInclusive(boolean flag);

	void setUpperValueInclusive(boolean flag);
}
