package com.mindbox.pe.model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import com.mindbox.pe.model.table.EnumValues;
import com.mindbox.pe.model.table.IRange;

/**
 * @author Beth Marvel
 * @author Mindbox
 */
public abstract class AbstractMessageKeyList implements Serializable {

	public static final String RANGE_VERBOSE_KEY = "verbose";
	public static final String RANGE_SYMBOLIC_KEY = "symbolic";
	public static final String RANGE_BRACKETED_KEY = "bracketed";
	public static final String TYPE_DEFAULT_KEY = "default";
	public static final String TYPE_ANY_KEY = "any";
	public static final String TYPE_INCLUDE_MULTIPLE_KEY = "enumIncludeMultiple";
	public static final String TYPE_INCLUDE_SINGLE_KEY = "enumIncludeSingle";
	public static final String TYPE_EXCLUDE_MULTIPLE_KEY = "enumExcludeMultiple";
	public static final String TYPE_EXCLUDE_SINGLE_KEY = "enumExcludeSingle";
	public static final String TYPE_RANGE_KEY = "range";
	public static final String ENUM_KEY = "enum";

	protected static final String[] validRangeStyles = { RANGE_VERBOSE_KEY, RANGE_SYMBOLIC_KEY, RANGE_BRACKETED_KEY };
	protected static final String[] validCellSelections = { TYPE_DEFAULT_KEY, TYPE_INCLUDE_MULTIPLE_KEY, TYPE_INCLUDE_SINGLE_KEY, TYPE_EXCLUDE_MULTIPLE_KEY, TYPE_EXCLUDE_SINGLE_KEY };


	/**
	 * Guaranteed to return a result or throw an exception.
	 * The returned value is used as a key for AbstractMessageKeyList.
	 * @return appropriate key string for the given messageFragmentDigest
	 */
	public static String getKey(ColumnMessageFragmentDigest msgFragment) {
		String key = msgFragment.getType();
		if (key == null) throw new IllegalArgumentException("Type attribute needed for <MessageFragment>: " + msgFragment);
		if (key.equals(AbstractMessageKeyList.ENUM_KEY)) key = msgFragment.getCellSelection();
		if (key == null)
			throw new IllegalArgumentException("cellSelection attribute needed for <MessageFragment type=\"enum\">: " + msgFragment);
		return key;
	}

	public static String getKey(Object cellValue) {
		String key = TYPE_ANY_KEY;
		if (cellValue instanceof EnumValues) {
			EnumValues<?> enumVal = (EnumValues<?>) cellValue;
			boolean isExclusion = enumVal.isSelectionExclusion();
			boolean isMultiSelect = enumVal.size() > 1;
			return getEnumKey(isExclusion, isMultiSelect);
		}
		else if (cellValue instanceof IRange) {
			key = TYPE_RANGE_KEY;
		}
		return key;
	}

	public static String getEnumKey(boolean isExclusion, boolean isMultiSelect) {
		String key = null;
		if (!isExclusion && isMultiSelect)
			key = TYPE_INCLUDE_MULTIPLE_KEY;
		else if (!isExclusion && !isMultiSelect)
			key = TYPE_INCLUDE_SINGLE_KEY;
		else if (isExclusion && isMultiSelect)
			key = TYPE_EXCLUDE_MULTIPLE_KEY;
		else if (isExclusion && !isMultiSelect) key = TYPE_EXCLUDE_SINGLE_KEY;
		return key;
	}

	/** 
	 * Checks the given range style for valdity.
	 * @param rangeStyle the rangeStyle string to check for validity.
	 * @return <code>true</code> if the given string is a valid rangeStyle; 
	 *         <code>false</code>, otherwise
	 */
	public static boolean validateRangeStyle(String rangeStyle) {
		for (int i = 0; i < validRangeStyles.length; i++)
			if (validRangeStyles[i].equals(rangeStyle)) return true;
		return false;
	}

	/** 
	 * Checks the given cellSelection for valdity.
	 * @param cellSel the cellSelection string to check for validity.
	 * @return true if the given string is a valid cellSelection
	 */
	public static boolean validateCellSelection(String cellSel) {
		for (int i = 0; i < validCellSelections.length; i++)
			if (validCellSelections[i].equals(cellSel)) return true;
		return false;
	}

	private final Map<String, Object> map = new HashMap<String, Object>();

	/**
	 * Adds it to the AbstractMessageKeyList, removing an existing one first
	 */
	protected void addEnumObject(String cellSelection, Object object) {
		map.remove(cellSelection);
		map.put(cellSelection, object);
	}

	/**
	 * Returns the object appropriate to the given enum parameters
	 * @param isExclusion Did the enum cell value have 'exclude selections' checked
	 * @param isMultiSelect Were there more than one cell-values selected.	 
	 * @return The appropriate enum config object
	 */
	public Object getEnumObject(boolean isExclusion, boolean isMultiSelect) {
		Object result = null;
		String key = getEnumKey(isExclusion, isMultiSelect);

		result = map.get(key);
		if (result == null) {
			result = map.get(TYPE_DEFAULT_KEY);
		}
		return result;
	}

	/**
	 * Creates a new RangeConfiguration and adds it to the AbstractMessageKeyList.
	 * Removes pre-existing ones first.
	 * @param obj
	 */
	public void addRangeObject(Object obj) {
		map.remove(TYPE_RANGE_KEY);
		map.put(TYPE_RANGE_KEY, obj);
	}


	/**
	 * @return The singleton range configuration object
	 */
	public Object getRangeObject() {
		return map.get(TYPE_RANGE_KEY);
	}

	/** 
	 * Gets object associated with default key.
	 * Should always return something.
	 * @return object associated with default key
	 */
	public Object getDefaultObject() {
		return map.get(TYPE_DEFAULT_KEY);
	}

	public Object getAnyObject() {
		return map.get(TYPE_ANY_KEY);
	}

	public String toString() {
		String result = "";
		Set<String> vals = map.keySet();
		Iterator<String> i = vals.iterator();
		while (i.hasNext()) {
			String key = i.next();
			result += "key= " + key + " value=[" + map.get(key) + "]\n";
		}
		return result;
	}

	protected final Object get(String key) {
		return map.get(key);
	}

	protected final void put(String key, Object value) {
		map.put(key, value);
	}

	protected final void putAll(AbstractMessageKeyList messageKeyList) {
		this.map.putAll(messageKeyList.map);
	}

	protected final Object remove(String key) {
		return map.remove(key);
	}

	protected final Set<Map.Entry<String, Object>> entrySet() {
		return map.entrySet();
	}

}
