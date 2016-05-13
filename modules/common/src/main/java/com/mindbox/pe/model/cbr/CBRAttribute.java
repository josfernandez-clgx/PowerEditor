package com.mindbox.pe.model.cbr;

import java.util.ArrayList;
import java.util.List;

import com.mindbox.pe.model.AbstractIDNameDescriptionObject;
import com.mindbox.pe.model.Auditable;
import com.mindbox.pe.model.Constants;

/**
 * @author deklerk
 * @since PowerEditor 4.1.0
 *
 * To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */

public class CBRAttribute extends AbstractIDNameDescriptionObject implements Auditable {

	private static final long serialVersionUID = 200410150094300L;

	public static final int PERFECT_VALUE = -2;
	private CBRCaseBase caseBase = null;
	private CBRAttributeType attributeType = null;
	private int matchContribution = Constants.CBR_NULL_DATA_EQUIVALENT_VALUE;
	private int mismatchPenalty = Constants.CBR_NULL_DATA_EQUIVALENT_VALUE;
	private int absencePenalty = Constants.CBR_NULL_DATA_EQUIVALENT_VALUE;
	private double lowestValue = Constants.CBR_NULL_DOUBLE_VALUE;
	private double highestValue = Constants.CBR_NULL_DOUBLE_VALUE;
	private double matchInterval = Constants.CBR_NULL_DOUBLE_VALUE;
	private CBRValueRange valueRange = null;
	private List<CBREnumeratedValue> enumeratedValues = null;

	public CBRAttribute() {
		super(UNASSIGNED_ID, "", null);
		enumeratedValues = new ArrayList<CBREnumeratedValue>();
	}

	/**
	 * @param id id
	 * @param name name
	 * @param desc desc
	 */
	public CBRAttribute(int id, String name, String desc) {
		super(id, name, desc);
		enumeratedValues = new ArrayList<CBREnumeratedValue>();
	}

	/**
	 * @param name name
	 * @param desc desc
	 */
	public CBRAttribute(String name, String desc) {
		super(name, desc);
		enumeratedValues = new ArrayList<CBREnumeratedValue>();
	}

	public synchronized void copyFrom(CBRAttribute att) {
		this.setName(att.getName());
		this.setDescription(att.getDescription());
		this.setCaseBase(att.getCaseBase());
		this.setAttributeType(att.getAttributeType());
		this.setMatchContribution(att.getMatchContribution());
		this.setMismatchPenalty(att.getMismatchPenalty());
		this.setAbsencePenalty(att.getAbsencePenalty());
		this.setLowestValue(att.getLowestValue());
		this.setHighestValue(att.getHighestValue());
		this.setMatchInterval(att.getMatchInterval());
		this.setValueRange(att.getValueRange());
		this.enumeratedValues.clear();
		this.enumeratedValues.addAll(att.getEnumeratedValues());
	}

	@Override
	public Auditable deepCopy() {
		CBRAttribute newAttribute = new CBRAttribute();
		newAttribute.setID(getID());
		newAttribute.copyFrom(this);
		return newAttribute;
	}

	/**
	 * @return Returns the absencePenalty.
	 */
	public int getAbsencePenalty() {
		return absencePenalty;
	}

	/**
	 * @return Returns the attributeType.
	 */
	public CBRAttributeType getAttributeType() {
		return attributeType;
	}

	@Override
	public String getAuditDescription() {
		return toString();
	}

	/**
	 * @return Returns the caseBase.
	 */
	public CBRCaseBase getCaseBase() {
		return caseBase;
	}

	/**
	 * @return Returns the enumeratedValues.
	 */
	public List<CBREnumeratedValue> getEnumeratedValues() {
		return enumeratedValues;
	}

	/**
	 * @return Returns the highestValue.
	 */
	public double getHighestValue() {
		return highestValue;
	}

	/**
	 * @return Returns the lowestValue.
	 */
	public double getLowestValue() {
		return lowestValue;
	}

	/**
	 * @return Returns the matchContribution.
	 */
	public int getMatchContribution() {
		return matchContribution;
	}

	/**
	 * @return Returns the matchInterval.
	 */
	public double getMatchInterval() {
		return matchInterval;
	}

	/**
	 * @return Returns the mismatchPenalty.
	 */
	public int getMismatchPenalty() {
		return mismatchPenalty;
	}

	/**
	 * @return Returns the valueRange.
	 */
	public CBRValueRange getValueRange() {
		return valueRange;
	}

	/**
	 * @param absencePenalty The absencePenalty to set.
	 */
	public void setAbsencePenalty(int absencePenalty) {
		this.absencePenalty = absencePenalty;
	}

	/**
	 * @param attributeType The attributeType to set.
	 */
	public void setAttributeType(CBRAttributeType attributeType) {
		this.attributeType = attributeType;
	}

	/**
	 * @param caseBase The caseBase to set.
	 */
	public void setCaseBase(CBRCaseBase caseBase) {
		this.caseBase = caseBase;
	}

	/**
	 * @param enumeratedValues The enumeratedValues to set.
	 */
	public void setEnumeratedValues(List<CBREnumeratedValue> enumeratedValues) {
		this.enumeratedValues.clear();
		this.enumeratedValues.addAll(enumeratedValues);
	}

	/**
	 * @param highestValue The highestValue to set.
	 */
	public void setHighestValue(double highestValue) {
		this.highestValue = highestValue;
	}

	/**
	 * @param lowestValue The lowestValue to set.
	 */
	public void setLowestValue(double lowestValue) {
		this.lowestValue = lowestValue;
	}

	/**
	 * @param matchContribution The matchContribution to set.
	 */
	public void setMatchContribution(int matchContribution) {
		this.matchContribution = matchContribution;
	}

	/**
	 * @param matchInterval The matchInterval to set.
	 */
	public void setMatchInterval(double matchInterval) {
		this.matchInterval = matchInterval;
	}

	/**
	 * @param mismatchPenalty The mismatchPenalty to set.
	 */
	public void setMismatchPenalty(int mismatchPenalty) {
		this.mismatchPenalty = mismatchPenalty;
	}

	/**
	 * @param valueRange The valueRange to set.
	 */
	public void setValueRange(CBRValueRange valueRange) {
		this.valueRange = valueRange;
	}

	@Override
	public String toString() {
		return "CBRAttribute[" + getID() + ",name=" + getName() + "]";
	}
}
