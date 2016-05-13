package com.mindbox.pe.model.table;

import java.io.Serializable;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.mindbox.pe.model.exceptions.InvalidDataException;


/**
 * DynamicString column value.
 * @author Geneho
 * @since PowerEditor 3.2.0
 */
public class DynamicStringValue implements Serializable, GridCellValue {

	private static final long serialVersionUID = 20070515000014L;
	public static final Pattern COLUMN_REF_PATTERN = Pattern.compile("%column ([0-9]+)%");

	public static String getDefaultValue() {
		return "";
	}

	public static DynamicStringValue parseValue(final String value) {
		DynamicStringValue valueObj = new DynamicStringValue(value);
		return valueObj;
	}


	private String value;
	private String[] deployValues = null;

	private DynamicStringValue(final DynamicStringValue source) {
		this(source.value);
		if (source.deployValues == null) {
			this.deployValues = null;
		}
		else {
			this.deployValues = new String[source.deployValues.length];
			for (int i = 0; i < this.deployValues.length; i++) {
				this.deployValues[i] = source.deployValues[i];
			}
		}
	}

	public DynamicStringValue(final String value) {
		this.value = value;
	}

	@Override
	public GridCellValue copy() {
		return new DynamicStringValue(this);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof DynamicStringValue) {
			return (value == ((DynamicStringValue) obj).value) || (value != null && value.equals(((DynamicStringValue) obj).value));
		}
		else {
			return false;
		}
	}

	/**
	 * @return Returns the deployValues.
	 */
	public String[] getDeployValues() {
		return deployValues;
	}

	// TT-19: Update values for rearranged columns
	/**
	 * Updates column referens for rearranged columns.
	 * @param rearrangedColumnMap key=old-id, value=new-id
	 * @return <code>true</code> if this has changed as a result of this call; <code>false</code>, otherwise
	 * @throws InvalidDataException on error
	 */
	public synchronized boolean replaceColumnReferences(final Map<Integer, Integer> rearrangedColumnMap) throws InvalidDataException {
		final Matcher matcher = COLUMN_REF_PATTERN.matcher(value);
		final StringBuilder buff = new StringBuilder();

		boolean updated = false;
		int prevIndex = 0;
		while (matcher.find()) {
			// add text upto the match
			buff.append(value.substring(prevIndex, matcher.start()));
			prevIndex = matcher.end();

			buff.append("%column ");
			int column = Integer.parseInt(matcher.group(1));
			if (rearrangedColumnMap.containsKey(column)) {
				buff.append(rearrangedColumnMap.get(column));
				if (!updated) {
					updated = true;
				}
			}
			else {
				buff.append(column);
			}
			buff.append("%");
		}

		if (updated) {
			if (prevIndex == 0) {
				buff.append(value);
			}
			else if (prevIndex > 0 && prevIndex < value.length() - 1) {
				buff.append(value.substring(prevIndex));
			}
			this.value = buff.toString();
		}
		return updated;
	}

	/**
	 * @param deployValues The deployValues to set.
	 */
	public void setDeployValues(String[] deployValues) {
		this.deployValues = deployValues;
	}

	@Override
	public String toString() {
		return value;
	}
}
