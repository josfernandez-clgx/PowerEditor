package com.mindbox.pe.model.table;

import java.io.Serializable;


/**
 * DynamicString column value.
 * @author Geneho
 * @since PowerEditor 3.2.0
 */
public class DynamicStringValue implements Serializable, GridCellValue {

	private static final long serialVersionUID = 20070515000014L;

	public static String getDefaultValue() {
		return "";
	}

	public static DynamicStringValue parseValue(String s) {
		DynamicStringValue valueObj = new DynamicStringValue();
		valueObj.value = s;
		return valueObj;
	}


	private String value = null;
	private String[] deployValues = null;

	public DynamicStringValue() {
	}

	private DynamicStringValue(DynamicStringValue source) {
		this.value = source.value;
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

	public GridCellValue copy() {
		return new DynamicStringValue(this);
	}

	/**
	 * @return Returns the deployValues.
	 */
	public String[] getDeployValues() {
		return deployValues;
	}

	/**
	 * @param deployValues The deployValues to set.
	 */
	public void setDeployValues(String[] deployValues) {
		this.deployValues = deployValues;
	}

	public String toString() {
		return value;
	}

	public boolean equals(Object obj) {
		if (obj instanceof DynamicStringValue) {
			return (value == ((DynamicStringValue) obj).value) || (value != null && value.equals(((DynamicStringValue) obj).value));
		}
		else {
			return false;
		}
	}

}
