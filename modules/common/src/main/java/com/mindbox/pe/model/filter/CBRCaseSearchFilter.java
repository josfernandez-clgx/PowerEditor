/*
 * Created on Oct 23, 2004
 */
package com.mindbox.pe.model.filter;

import java.util.Iterator;

import com.mindbox.pe.model.Constants;
import com.mindbox.pe.model.PeDataType;
import com.mindbox.pe.model.Persistent;
import com.mindbox.pe.model.cbr.CBRAttributeValue;
import com.mindbox.pe.model.cbr.CBRCase;

/**
 * @author deklerk
 * @since PowerEditor 4.1.0
 */
public class CBRCaseSearchFilter extends NameDescriptionSearchFilter<CBRCase> {

	private static final long serialVersionUID = 2004102319332101L;

	// HACK ALERT: these have to be consecutive numbers in order for CBRCaseFilterPanel to work properly
	public final static int ANY_VALUE = 1;
	public final static int VALUE_EQUAL_TO = 2;
	public final static int VALUE_NOT_EQUAL_TO = 3;
	public final static int VALUE_CONTAINS = 4;
	public final static int VALUE_DOES_NOT_CONTAIN = 5;
	public final static int VALUE_BETWEEN = 6;
	public final static int VALUE_NOT_BETWEEN = 7;

	private int attributeIDCriterion = Persistent.UNASSIGNED_ID;
	private int valueSearchType = ANY_VALUE;
	private String valueSearchStringCriterion = null;
	private int valueSearchIntMinCriterion = Constants.CBR_NULL_DATA_EQUIVALENT_VALUE;
	private int valueSearchIntMaxCriterion = Constants.CBR_NULL_DATA_EQUIVALENT_VALUE;
	private int caseBaseID = Persistent.UNASSIGNED_ID;

	public CBRCaseSearchFilter() {
		super(PeDataType.CBR_CASE);
	}

	/**
	 * @return Returns the attributeIDCriterion.
	 */
	public int getAttributeIDCriterion() {
		return attributeIDCriterion;
	}

	/**
	 * @param attributeIDCriterion The attributeIDCriterion to set.
	 */
	public void setAttributeIDCriterion(int attributeIDCriterion) {
		this.attributeIDCriterion = attributeIDCriterion;
	}

	/**
	 * @return Returns the valueSearchIntMaxCriterion.
	 */
	public int getValueSearchIntMaxCriterion() {
		return valueSearchIntMaxCriterion;
	}

	/**
	 * @param valueSearchIntMaxCriterion The valueSearchIntMaxCriterion to set.
	 */
	public void setValueSearchIntMaxCriterion(int valueSearchIntMaxCriterion) {
		this.valueSearchIntMaxCriterion = valueSearchIntMaxCriterion;
	}

	/**
	 * @return Returns the valueSearchIntMinCriterion.
	 */
	public int getValueSearchIntMinCriterion() {
		return valueSearchIntMinCriterion;
	}

	/**
	 * @param valueSearchIntMinCriterion The valueSearchIntMinCriterion to set.
	 */
	public void setValueSearchIntMinCriterion(int valueSearchIntMinCriterion) {
		this.valueSearchIntMinCriterion = valueSearchIntMinCriterion;
	}

	/**
	 * @return Returns the valueSearchStringCriterion.
	 */
	public String getValueSearchStringCriterion() {
		return valueSearchStringCriterion;
	}

	/**
	 * @param valueSearchStringCriterion The valueSearchStringCriterion to set.
	 */
	public void setValueSearchStringCriterion(String valueSearchStringCriterion) {
		this.valueSearchStringCriterion = valueSearchStringCriterion;
	}

	/**
	 * @return Returns the valueSearchType.
	 */
	public int getValueSearchType() {
		return valueSearchType;
	}

	/**
	 * @param valueSearchType The valueSearchType to set.
	 */
	public void setValueSearchType(int valueSearchType) {
		this.valueSearchType = valueSearchType;
	}

	/**
	 * @return Returns the caseBaseID.
	 */
	public int getCaseBaseID() {
		return caseBaseID;
	}

	/**
	 * @param caseBaseID The caseBaseID to set.
	 */
	public void setCaseBaseID(int caseBaseID) {
		this.caseBaseID = caseBaseID;
	}

	public String toString() {
		return "CBRCaseSearchFilter[name=" + getNameCriterion() + ",desc=" + this.getDescriptionCriterion() + ",attributeID="
				+ attributeIDCriterion + ",valueSearchType=" + this.getValueSearchType() + "]";
	}

	private boolean valueIsAcceptable(String value) {
		switch (this.valueSearchType) {
		case ANY_VALUE:
			return true;
		case VALUE_EQUAL_TO:
			if (valueSearchStringCriterion == null || valueSearchStringCriterion.length() == 0)
				return value == null || value.length() == 0;
			else
				return valueSearchStringCriterion.equalsIgnoreCase(value);
		case VALUE_NOT_EQUAL_TO:
			if (valueSearchStringCriterion == null || valueSearchStringCriterion.length() == 0)
				return value != null && value.length() > 0;
			else
				return !valueSearchStringCriterion.equalsIgnoreCase(value);
		case VALUE_CONTAINS:
			if (valueSearchStringCriterion == null || valueSearchStringCriterion.length() == 0)
				return true;
			else
				return (value.toUpperCase().indexOf(valueSearchStringCriterion.toUpperCase()) != -1);
		case VALUE_DOES_NOT_CONTAIN:
			if (valueSearchStringCriterion == null || valueSearchStringCriterion.length() == 0)
				return false;
			else
				return (value.toUpperCase().indexOf(valueSearchStringCriterion.toUpperCase()) == -1);
		case VALUE_BETWEEN:
			int intValue = Constants.CBR_NULL_DATA_EQUIVALENT_VALUE;
			try {
				intValue = Integer.parseInt(value);
			}
			catch (Exception x) {
			}
			return intValue != Constants.CBR_NULL_DATA_EQUIVALENT_VALUE
					&& (valueSearchIntMinCriterion == Constants.CBR_NULL_DATA_EQUIVALENT_VALUE || intValue >= valueSearchIntMinCriterion)
					&& (valueSearchIntMaxCriterion == Constants.CBR_NULL_DATA_EQUIVALENT_VALUE || intValue <= valueSearchIntMaxCriterion);
		case VALUE_NOT_BETWEEN:
			intValue = Constants.CBR_NULL_DATA_EQUIVALENT_VALUE;
			try {
				intValue = Integer.parseInt(value);
			}
			catch (Exception x) {
			}
			return intValue == Constants.CBR_NULL_DATA_EQUIVALENT_VALUE
					|| ((valueSearchIntMinCriterion != Constants.CBR_NULL_DATA_EQUIVALENT_VALUE && intValue < valueSearchIntMinCriterion) || (valueSearchIntMaxCriterion != Constants.CBR_NULL_DATA_EQUIVALENT_VALUE && intValue > valueSearchIntMaxCriterion));
		}
		return false;
	}

	public boolean isAcceptable(CBRCase c) {
		if (c == null) throw new NullPointerException("CBRCase object is null");
		if (c.getCaseBase() == null) return false;
		if (c.getCaseBase().getID() != this.caseBaseID) return false;
		if (super.isAcceptable(c)) {
			if (this.attributeIDCriterion == -1) {
				Iterator<CBRAttributeValue> it = c.getAttributeValues().iterator();
				if (!it.hasNext()) return true;
				while (it.hasNext()) {
					CBRAttributeValue av = it.next();
					if (valueIsAcceptable(av.getName())) return true;
				}
				return false;
			}
			else {
				Iterator<CBRAttributeValue> it = c.getAttributeValues().iterator();
				while (it.hasNext()) {
					CBRAttributeValue av = it.next();
					if (av.getAttribute() != null && av.getAttribute().getId() == this.attributeIDCriterion)
						return valueIsAcceptable(av.getName());
				}
				return false;
			}
		}
		else {
			return false;
		}
	}

}
