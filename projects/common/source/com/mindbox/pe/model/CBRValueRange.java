/*
 * Created on Oct 15, 2004
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.mindbox.pe.model;

import java.util.Iterator;
import java.util.List;

/**
 * @author deklerk
 * @since PowerEditor 4.1.0
 *
 * To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class CBRValueRange extends AbstractCBRConfigClass {

	private static final long serialVersionUID = 200410150103400L;
	private boolean enumeratedValuesAllowed;
	private boolean anythingAllowed;
	private boolean numericAllowed;
	private boolean floatAllowed;
	private boolean negativeAllowed;
	/**
	 * @param symbol
	 * @param name
	 * @param desc
	 */
	public CBRValueRange(String symbol, String name, String desc, boolean enumeratedValuesAllowed,
			boolean anythingAllowed,
			boolean numericAllowed, boolean floatAllowed, boolean negativeAllowed) {
		super(UNASSIGNED_ID, symbol, name, desc);
		this.enumeratedValuesAllowed = enumeratedValuesAllowed;
		this.anythingAllowed = anythingAllowed;
		this.numericAllowed = numericAllowed;
		this.floatAllowed = floatAllowed;
		this.negativeAllowed = negativeAllowed;
	}

	/**
	 * @param id
	 * @param symbol
	 * @param name
	 * @param desc
	 */
	public CBRValueRange(int id, String symbol, String name, String desc, boolean enumeratedValuesAllowed, boolean anythingAllowed,
			boolean numericAllowed, boolean floatAllowed, boolean negativeAllowed) {
		super(id, symbol, name, desc);
		this.enumeratedValuesAllowed = enumeratedValuesAllowed;
		this.anythingAllowed = anythingAllowed;
		this.numericAllowed = numericAllowed;
		this.floatAllowed = floatAllowed;
		this.negativeAllowed = negativeAllowed;
	}
	
	public boolean isConforming(String value, List<CBREnumeratedValue> enumeratedValues) {
		if (numericAllowed) {
			try {
				double number = Double.parseDouble(value);
				if (!negativeAllowed && number < 0.0) return false;
				if (!floatAllowed && number != Math.rint(number)) return false;
				return true;
			} catch (Exception x) {
				return false;
			}
		} else if (enumeratedValuesAllowed) {
			Iterator<CBREnumeratedValue> it = enumeratedValues.iterator();
			while (it.hasNext()) if (it.next().getName().equals(value)) return true;
			return false;
		}
		return true;
	}

	/**
	 * @return Returns the enumeratedValuesAllowed.
	 */
	public boolean isEnumeratedValuesAllowed() {
		return enumeratedValuesAllowed;
	}
	/**
	 * @param enumeratedValuesAllowed The enumeratedValuesAllowed to set.
	 */
	public void setEnumeratedValuesAllowed(boolean enumeratedValuesAllowed) {
		this.enumeratedValuesAllowed = enumeratedValuesAllowed;
	}
	
	
	/**
	 * @return Returns the anythingAllowed.
	 */
	public boolean isAnythingAllowed() {
		return anythingAllowed;
	}
	/**
	 * @param anythingAllowed The anythingAllowed to set.
	 */
	public void setAnythingAllowed(boolean anythingAllowed) {
		this.anythingAllowed = anythingAllowed;
	}
	/**
	 * @return Returns the floatAllowed.
	 */
	public boolean isFloatAllowed() {
		return floatAllowed;
	}
	/**
	 * @param floatAllowed The floatAllowed to set.
	 */
	public void setFloatAllowed(boolean floatAllowed) {
		this.floatAllowed = floatAllowed;
	}
	/**
	 * @return Returns the negativeAllowed.
	 */
	public boolean isNegativeAllowed() {
		return negativeAllowed;
	}
	/**
	 * @param negativeAllowed The negativeAllowed to set.
	 */
	public void setNegativeAllowed(boolean negativeAllowed) {
		this.negativeAllowed = negativeAllowed;
	}
	/**
	 * @return Returns the numericAllowed.
	 */
	public boolean isNumericAllowed() {
		return numericAllowed;
	}
	/**
	 * @param numericAllowed The numericAllowed to set.
	 */
	public void setNumericAllowed(boolean numericAllowed) {
		this.numericAllowed = numericAllowed;
	}
}
