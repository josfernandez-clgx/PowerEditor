package com.mindbox.pe.model.table;

import java.io.Serializable;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;

import com.mindbox.pe.model.EnumValue;

/**
 * Value encapsulating zero or more enum values. 
 * This is used for multi-select enum columns of a guideline grid table.
 * <b>Note: This may contain any objects.</b> Do not assume this only contains {@link com.mindbox.pe.model.EnumValue} objects.
 * @author Geneho Kim
 * @since PowerEditor
 */
public class EnumValues<T> implements Serializable, GridCellValue {

	private static final long serialVersionUID = 20070515000015L;

	public static final String EXCLUSION_PREFIX = "%~%[";
	public static final String EXCLUSION_PREFIX_REGEX = "%~%\\[";

	// used prior to PE 4.2 build 14 (4/06/2005)
	public static final String OLD_EXCLUSION_PREFIX = "Not ";

	public static String getDefaultValue() {
		return "";
	}

	/**
	 * 
	 * @param s
	 * @param checkOldExclusionPrefix
	 * @param enumValues can be <code>null</code>
	 * @return boolean 
	 */
	public static boolean isExclusionEnumValueString(String s, boolean checkOldExclusionPrefix, List<EnumValue> enumValues) {
		if (s == null) return false;
		if (s.length() < 4) return false;
		if (!s.startsWith(EXCLUSION_PREFIX) && !(checkOldExclusionPrefix && s.startsWith(OLD_EXCLUSION_PREFIX))) return false;
		if (enumValues == null || enumValues.isEmpty()) return true;
		int index = s.indexOf(",");
		String strToCheck = (index > 0 ? s.substring(0, index) : s);
		for (EnumValue enumVal : enumValues) {
			String enumDisplayVal = enumVal.getDisplayLabel();
			if (strToCheck.equals(enumDisplayVal)) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Parses the specified string to a EnumValues object.
	 * @param s the string to parse
	 * @return the parsed EnumValues object
	 */
	public static EnumValues<String> parseValue(String s) {
		return parseValue(s, false, null);
	}

	/**
	 * Parses the specified string to a EnumValues object, using the specified enum values
	 * @param s the string to parse
	 * @param checkOldExclusionPrefix set to <code>true</code> if check for old exclusion prefix is required
	 * @param enumValueList list of enum values
	 * @return the parsed EnumValues object
	 */
	public static EnumValues<String> parseValue(String s, boolean checkOldExclusionPrefix, List<EnumValue> enumValueList) {
		EnumValues<String> enumValues = new EnumValues<String>();
		try {
			String strToParse = s;
			if (isExclusionEnumValueString(strToParse, checkOldExclusionPrefix, enumValueList)) {
				enumValues.excludeSelection = true;
				strToParse = s.substring(4);
			}

			String[] strs = (strToParse == null ? new String[0] : strToParse.split(","));
			for (int i = 0; i < strs.length; i++) {
				if (strs[i] != null && strs[i].trim().length() > 0) enumValues.add(strs[i]);
			}
		}
		catch (Exception exception) {
			Logger.getLogger(EnumValues.class).warn("Failed to parse for EnumValues: " + s, exception);
		}
		return enumValues;
	}

	public static String replaceExclusionPrefixIfOld(String value, List<EnumValue> enumValues) {
		if (value == null | value.length() == 0) return value;
		if (enumValues == null || enumValues.isEmpty()) return value;
		if (!value.startsWith(OLD_EXCLUSION_PREFIX)) return value;
		int index = value.indexOf(",");
		String strToCheck = (index > 0 ? value.substring(0, index) : value);
		for (Iterator<EnumValue> iter = enumValues.iterator(); iter.hasNext();) {
			EnumValue element = iter.next();
			if (strToCheck.equals(element.getDisplayLabel())) {
				return value;
			}
		}
		return EXCLUSION_PREFIX + value.substring(4);
	}

	private boolean excludeSelection = false;
	private final List<T> list;

	public EnumValues() {
		this.list = new LinkedList<T>();
	}

	@SuppressWarnings("unchecked")
	protected EnumValues(EnumValues<T> source) {
		this();
		if (source == null) throw new NullPointerException("source cannot be null");
		this.excludeSelection = source.excludeSelection;
		for (int i = 0; i < source.size(); i++) {
			T value = source.get(i);
			if (value instanceof GridCellValue) {
				T copy = (T) ((GridCellValue) value).copy();
				list.add(copy);
			}
			else {
				list.add(value);
			}
		}
	}

	public EnumValues(List<T> list) {
		this.list = new LinkedList<T>(list);
	}

	public final void add(T obj) {
		list.add(obj);
	}

	// Composition Methods

	public final void clear() {
		list.clear();
	}

	public final boolean contains(Object obj) {
		return list.contains(obj);
	}

	/**
	 * Checks if the given DeployID exists in the current EnumValues object. 
	 * This method assumes that this only contains {@link com.mindbox.pe.model.EnumValue} objects.
	 * @param deployID
	 * @return <code> true </code> if given DeployID exists in the current EnumValues object.
	 * Else returns <code> false. </code>
	 * 
	 */
	public boolean containsDeployID_ForEnumValueOnly(Integer deployID) {
		for (int i = 0; i < list.size(); i++) {
			if (list.get(i) instanceof EnumValue) {
				EnumValue ev = (EnumValue) list.get(i);
				if (ev.hasDeployID() && ev.getDeployID().equals(deployID)) {
					return true;
				}
			}
		}
		return false;
	}

	public final boolean containsStringValueForDisplayLabel(final String value, final boolean ignoreCase) {
		if (value == null) {
			throw new IllegalArgumentException("value cannot be null");
		}
		for (T item : list) {
			if (EnumValue.class.isInstance(item)) {
				if ((ignoreCase && EnumValue.class.cast(item).getDisplayLabel().equalsIgnoreCase(value)) || (!ignoreCase && EnumValue.class.cast(item).getDisplayLabel().equals(value))) {
					return true;
				}
			}
			else {
				if ((ignoreCase && item.toString().equalsIgnoreCase(value)) || (!ignoreCase && item.toString().equals(value))) {
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public GridCellValue copy() {
		return new EnumValues<T>(this);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (obj == this) {
			return true;
		}
		return obj instanceof EnumValues && this.excludeSelection == ((EnumValues<?>) obj).excludeSelection && this.list.equals(((EnumValues<?>) obj).list);
	}

	public final T get(int index) {
		return list.get(index);
	}

	public T getEnumValue(int i) {
		return list.get(i);
	}

	public String getEnumValueAsString(int i) {
		T value = list.get(i);
		if (value instanceof String) {
			return (String) value;
		}
		else {
			return value.toString();
		}
	}

	/** based on mutable values, use in Set/HashMap/Hashtable with extreme caution */
	@Override
	public int hashCode() {
		return super.hashCode() + (excludeSelection ? 17 : 0);
	}

	public boolean hasOverlap(EnumValues<T> enumvalues) {
		if (enumvalues == null) return false;
		boolean flag = false;
		for (Iterator<T> iterator = list.iterator(); iterator.hasNext();)
			if (enumvalues.contains(iterator.next())) {
				flag = true;
				break;
			}

		return flag;
	}

	public final boolean isEmpty() {
		return list.isEmpty();
	}

	public boolean isSelectionExclusion() {
		return excludeSelection;
	}

	public boolean isSubsumedBy(EnumValues<T> enumvalues) {
		boolean flag = false;
		if (size() > enumvalues.size()) return false;
		if (enumvalues.list.containsAll(this.list)) flag = true;
		return flag;
	}

	public final Iterator<T> iterator() {
		return list.iterator();
	}

	public void setSelectionExclusion(boolean exclude) {
		this.excludeSelection = exclude;
	}

	public final int size() {
		return list.size();
	}

	@Override
	public String toString() {
		StringBuilder buff = new StringBuilder();
		if (excludeSelection && size() > 0) {
			buff.append(EXCLUSION_PREFIX);
		}
		if (size() > 0) {
			for (int i = 0; i < size(); i++) {
				if (i > 0) buff.append(',');
				buff.append(get(i));
			}
		}
		return buff.toString();
	}

	public String[] toStringArray() {
		return (String[]) list.toArray(new String[0]);
	}

	public List<T> toUnmodifiableList() {
		return Collections.unmodifiableList(list);
	}

}