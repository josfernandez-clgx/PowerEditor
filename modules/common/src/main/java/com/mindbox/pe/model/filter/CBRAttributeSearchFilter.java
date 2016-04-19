/*
 * Created on Oct 23, 2004
 */
package com.mindbox.pe.model.filter;

import com.mindbox.pe.model.PeDataType;
import com.mindbox.pe.model.Persistent;
import com.mindbox.pe.model.cbr.CBRAttribute;

/**
 * @author deklerk
 * @since PowerEditor 4.1.0
 */
public class CBRAttributeSearchFilter extends NameDescriptionSearchFilter<CBRAttribute> {

	private static final long serialVersionUID = 2004102319332100L;

	private int attributeTypeIDCriterion = Persistent.UNASSIGNED_ID;
	private int caseBaseID = Persistent.UNASSIGNED_ID;

	public CBRAttributeSearchFilter() {
		super(PeDataType.CBR_ATTRIBUTE);
	}

	/**
	 * @return Returns the attributeTypeCriterion.
	 */
	public int getAttributeTypeIDCriterion() {
		return attributeTypeIDCriterion;
	}

	/**
	 * @param attributeTypeCriterion The attributeTypeCriterion to set.
	 */
	public void setAttributeTypeIDCriterion(int attributeTypeCriterion) {
		this.attributeTypeIDCriterion = attributeTypeCriterion;
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
		return "CBRAttributeSearchFilter[name=" + getNameCriterion() + ",desc=" + this.getDescriptionCriterion() + ",type="
				+ attributeTypeIDCriterion + ",caseBaseID=" + caseBaseID + "]";
	}

	public boolean isAcceptable(CBRAttribute att) {
		if (att == null) throw new NullPointerException("CBRAttribute object is null");
		if (att.getCaseBase() == null) return false;
		if (att.getCaseBase().getID() != this.caseBaseID) return false;
		if (super.isAcceptable(att)) {
			if (this.attributeTypeIDCriterion == Persistent.UNASSIGNED_ID) {
				return true;
			}
			else {
				return att.getAttributeType().getId() == attributeTypeIDCriterion;
			}
		}
		else {
			return false;
		}

	}

}
