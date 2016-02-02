package com.mindbox.pe.model.filter;

import java.util.Map;

import com.mindbox.pe.model.CBRCase;
import com.mindbox.pe.model.Constants;
import com.mindbox.pe.model.EntityType;
import com.mindbox.pe.model.Persistent;



public final class CBRCaseSearchFilterSpec extends NameDescriptionFilterSpec<CBRCase> {

	private static final long serialVersionUID = 20041022120000010L;

	private int attributeIDCriterion = Constants.CBR_NULL_DATA_EQUIVALENT_VALUE;
	private int valueSearchType = CBRCaseSearchFilter.ANY_VALUE;
	private String valueSearchStringCriterion = null;
	private int valueSearchIntMinCriterion = Constants.CBR_NULL_DATA_EQUIVALENT_VALUE;
	private int valueSearchIntMaxCriterion = Constants.CBR_NULL_DATA_EQUIVALENT_VALUE;

	public CBRCaseSearchFilterSpec() {
		this (Persistent.UNASSIGNED_ID, "");
	}
	
	public CBRCaseSearchFilterSpec(int id, String name) {
		super(EntityType.CBR_CASE, null, id, name);
	}
	
	public CBRCaseSearchFilterSpec(String name) {
		super(EntityType.CBR_CASE, null, name);
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
	
	public SearchFilter<CBRCase> asSearchFilter() {
		CBRCaseSearchFilter filter = new CBRCaseSearchFilter();
		filter.setAttributeIDCriterion(this.getAttributeIDCriterion());
		filter.setDescriptionCriterion(this.getDescCriterion());
		filter.setNameCriterion(this.getNameCriterion());
		filter.setValueSearchType(this.getValueSearchType());
		filter.setValueSearchStringCriterion(this.getValueSearchStringCriterion());
		filter.setValueSearchIntMaxCriterion(this.getValueSearchIntMaxCriterion());
		filter.setValueSearchIntMinCriterion(this.getValueSearchIntMinCriterion());
		return filter;
	}

	public void setInvariants(Map<String,String> paramMap) {
		super.setInvariants(paramMap);
		try {
			attributeIDCriterion = Integer.parseInt(paramMap.get("attributeID"));
		} catch (Exception x) {
		}
		try {
			 this.valueSearchType = Integer.parseInt(paramMap.get("valueSearchType"));
		} catch (Exception x) {
		}
		try {
			 this.valueSearchIntMaxCriterion= Integer.parseInt(paramMap.get("valueSearchIntMax"));
		} catch (Exception x) {
		}
		try {
			 this.valueSearchIntMinCriterion= Integer.parseInt(paramMap.get("valueSearchIntMin"));
		} catch (Exception x) {
		}
		this.valueSearchStringCriterion = paramMap.get("valueSearchString");
	}

	public String toParamString() {
		StringBuffer buff = new StringBuffer();
		buff.append(super.toParamString());
		if (attributeIDCriterion != Constants.CBR_NULL_DATA_EQUIVALENT_VALUE) {
			buff.append("attributeID");
			buff.append(FIELD_ASSIGNMENT);	
			buff.append(attributeIDCriterion);
			buff.append(PARAMETER_SEPARATOR);
		}
		buff.append("valueSearchType");
		buff.append(FIELD_ASSIGNMENT);	
		buff.append(valueSearchType);
		buff.append(PARAMETER_SEPARATOR);
		switch (valueSearchType) {
		case CBRCaseSearchFilter.VALUE_EQUAL_TO:
		case CBRCaseSearchFilter.VALUE_NOT_EQUAL_TO:
		case CBRCaseSearchFilter.VALUE_CONTAINS:
		case CBRCaseSearchFilter.VALUE_DOES_NOT_CONTAIN:
			if (this.valueSearchStringCriterion != null) {
				buff.append("valueSearchString");
				buff.append(FIELD_ASSIGNMENT);	
				buff.append(valueSearchStringCriterion);
				buff.append(PARAMETER_SEPARATOR);
			}
			break;
			case CBRCaseSearchFilter.VALUE_BETWEEN:
			case CBRCaseSearchFilter.VALUE_NOT_BETWEEN:
			{
				buff.append("valueSearchIntMin");
				buff.append(FIELD_ASSIGNMENT);
				buff.append(valueSearchIntMinCriterion);
				buff.append(PARAMETER_SEPARATOR);
				buff.append("valueSearchIntMax");
				buff.append(FIELD_ASSIGNMENT);
				buff.append(valueSearchIntMaxCriterion);
				buff.append(PARAMETER_SEPARATOR);
			}
		}
		return buff.toString();
	}

	public String toString() {
		return getName();
	}

}
