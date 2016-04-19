package com.mindbox.pe.model.filter;

import java.util.Map;

import com.mindbox.pe.model.PeDataType;
import com.mindbox.pe.model.Persistent;
import com.mindbox.pe.model.cbr.CBRAttribute;


public final class CBRAttributeSearchFilterSpec extends NameDescriptionFilterSpec<CBRAttribute> {

	private static final long serialVersionUID = 20041022120000009L;
	private int attributeTypeID = Persistent.UNASSIGNED_ID;

	public CBRAttributeSearchFilterSpec() {
		this (Persistent.UNASSIGNED_ID, "");
	}
	
	public CBRAttributeSearchFilterSpec(int id, String name) {
		super(PeDataType.CBR_ATTRIBUTE, null, id, name);
	}
	
	public CBRAttributeSearchFilterSpec(String name) {
		super(PeDataType.CBR_ATTRIBUTE, null, name);
	}
	
	/**
	 * @return Returns the attributeType.
	 */
	public int getAttributeTypeIDCriterion() {
		return attributeTypeID;
	}
	/**
	 * @param attributeTypeID The attributeTypeID to set.
	 */
	public void setAttributeTypeIDCriterion(int attributeTypeID) {
		this.attributeTypeID = attributeTypeID;
	}
	public SearchFilter<CBRAttribute> asSearchFilter() {
		CBRAttributeSearchFilter filter = new CBRAttributeSearchFilter();
		filter.setAttributeTypeIDCriterion(this.attributeTypeID);
		filter.setDescriptionCriterion(this.getDescCriterion());
		filter.setNameCriterion(this.getNameCriterion());
		return filter;
	}

	public void setInvariants(Map<String,String> paramMap) {
		super.setInvariants(paramMap);
		try {
			attributeTypeID = Integer.parseInt(paramMap.get("attributeTypeID"));
		} catch (Exception x) {
		}
	}

	public String toParamString() {
		StringBuilder buff = new StringBuilder();
		buff.append(super.toParamString());
		if (attributeTypeID != Persistent.UNASSIGNED_ID) {
			buff.append("attributeTypeID");
			buff.append(FIELD_ASSIGNMENT);	
			buff.append(attributeTypeID);
			buff.append(PARAMETER_SEPARATOR);
		}
		return buff.toString();
	}

	public String toString() {
		return getName();
	}

}
