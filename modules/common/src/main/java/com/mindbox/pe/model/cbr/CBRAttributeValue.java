/*
 * Created on Oct 19, 2004
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.mindbox.pe.model.cbr;

import com.mindbox.pe.common.UtilBase;
import com.mindbox.pe.model.AbstractIDNameDescriptionObject;
import com.mindbox.pe.model.Constants;

/**
 * @author Inna Nill
 * @since PowerEditor 4.1.0
 *
 * To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */

public class CBRAttributeValue extends AbstractIDNameDescriptionObject {

	private static final long serialVersionUID = 200410190140700L;

	private CBRAttribute attribute = null;
	private int matchContribution = Constants.CBR_NULL_DATA_EQUIVALENT_VALUE;
	private int mismatchPenalty = Constants.CBR_NULL_DATA_EQUIVALENT_VALUE;

	/**
	 * Default constructor.
	 * Assigns UNASSIGNED_ID to <code>id</code> attribute.
	 * Assigns "" to <code>name</code> attribute.
	 * Assigns null to <code>desc</code> attribute.
	 */
	public CBRAttributeValue() {
		super(UNASSIGNED_ID, "", null);
	}

	/**
	 * Copy constructor.
	 * @param in in
	 */
	public CBRAttributeValue(CBRAttributeValue in) {
		super(in.getId(), in.getName(), in.getDescription());
		copyFrom(in);
	}

	/**
	 * @param id id
	 * @param name name
	 * @param desc desc
	 */
	public CBRAttributeValue(int id, String name, String desc) {
		super(id, name, desc);
	}

	/**
	 * @param name name
	 * @param desc desc
	 */
	public CBRAttributeValue(String name, String desc) {
		super(name, desc);
	}

	/**
	 * Copy values from passed-in CBRAttributeValue to this one.
	 * There are no deep copies here.
	 * @param attVal attVal
	 */
	public synchronized void copyFrom(CBRAttributeValue attVal) {
		this.setName(attVal.getName());
		this.setDescription(attVal.getDescription());
		this.setAttribute(attVal.getAttribute());
		this.setMatchContribution(attVal.getMatchContribution());
		this.setMismatchPenalty(attVal.getMismatchPenalty());
	}

	/**
	 * @return Returns the attribute.
	 */
	public CBRAttribute getAttribute() {
		return attribute;
	}

	/**
	 * @return Returns the matchContribution.
	 */
	public int getMatchContribution() {
		return matchContribution;
	}

	/**
	 * @return Returns the mismatchPenalty.
	 */
	public int getMismatchPenalty() {
		return mismatchPenalty;
	}

	public boolean isValid() {
		if (this.getAttribute() != null && this.getName() != null && UtilBase.trim(this.getName()).length() > 0)
			return this.getAttribute().getValueRange().isConforming(this.getName(), this.getAttribute().getEnumeratedValues());
		else
			return false;
	}

	/**
	 * @param attribute The attribute to set.
	 */
	public void setAttribute(CBRAttribute attribute) {
		this.attribute = attribute;
	}

	/**
	 * @param matchContribution The matchContribution to set.
	 */
	public void setMatchContribution(int matchContribution) {
		this.matchContribution = matchContribution;
	}

	/**
	 * @param mismatchPenalty The mismatchPenalty to set.
	 */
	public void setMismatchPenalty(int mismatchPenalty) {
		this.mismatchPenalty = mismatchPenalty;
	}

	@Override
	public String toString() {
		return super.toString() + "CBRAttributeValue[attribute=" + getAttribute() + ", match contribution=" + getMatchContribution() + ", mismatch penalty=" + getMismatchPenalty()
				+ "]";
	}
}
