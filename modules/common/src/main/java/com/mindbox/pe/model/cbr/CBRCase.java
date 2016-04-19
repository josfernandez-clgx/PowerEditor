package com.mindbox.pe.model.cbr;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.mindbox.pe.model.AbstractIDNameDescriptionObject;
import com.mindbox.pe.model.Auditable;
import com.mindbox.pe.model.DateSynonym;

/**
 * @author Inna Nill
 * @since PowerEditor 4.1.0
 *
 * To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */

public class CBRCase extends AbstractIDNameDescriptionObject implements Auditable {

	private static final long serialVersionUID = 200410190134400L;

	private CBRCaseBase caseBase = null;
	private List<CBRCaseAction> caseActions = null;
	private List<CBRAttributeValue> attributeValues = null;
	private DateSynonym effDate, expDate = null;

	public CBRCase() {
		super(UNASSIGNED_ID, "", null);
		attributeValues = new ArrayList<CBRAttributeValue>();
		caseActions = new ArrayList<CBRCaseAction>();
	}

	/**
	 * Constructor
	 * @param name
	 * @param desc
	 */
	public CBRCase(String name, String desc) {
		super(name, desc);
		attributeValues = new ArrayList<CBRAttributeValue>();
		caseActions = new ArrayList<CBRCaseAction>();
	}

	/**
	 * Constructor
	 * @param id
	 * @param name
	 * @param desc
	 */
	public CBRCase(int id, String name, String desc) {
		super(id, name, desc);
		attributeValues = new ArrayList<CBRAttributeValue>();
		caseActions = new ArrayList<CBRCaseAction>();
	}

	public CBRCase(CBRCase source) {
		super(source);
		attributeValues = new ArrayList<CBRAttributeValue>();
		caseActions = new ArrayList<CBRCaseAction>();
		copyFrom(source);
	}
	
	public final DateSynonym getEffectiveDate() {
		return effDate;
	}

	public final DateSynonym getExpirationDate() {
		return expDate;
	}

	public final void setEffectiveDate(DateSynonym effDate) {
		this.effDate = effDate;
	}

	public final void setExpirationDate(DateSynonym expDate) {
		this.expDate = expDate;
	}


	public Auditable deepCopy() {
		CBRCase newCase = new CBRCase();
		newCase.setID(getID());
		newCase.copyFrom(this);
		return newCase;
	}

	public String getAuditDescription() {
		return toString();
	}

	/**
	 * Copies the values from the passed-in CBRCase into this CBRCase.
	 * The CBRAttributeValue objects must be a deep-copy, since they are unique for
	 * each case and are not copied anywhere else.
	 * The CBRCaseBase is not a deep copy, and neither is the activation label.
	 * @param cbrCase
	 */
	public synchronized void copyFrom(CBRCase cbrCase) {
		this.setName(cbrCase.getName());
		this.setDescription(cbrCase.getDescription());
		this.setCaseBase(cbrCase.getCaseBase());
		this.setCaseActions(cbrCase.getCaseActions());
		this.attributeValues.clear();

		// AttributeValues should be deep-copied here as well.
		Iterator<CBRAttributeValue> it = cbrCase.getAttributeValues().iterator();
		while (it.hasNext()) {
			CBRAttributeValue newValue = new CBRAttributeValue((CBRAttributeValue) it.next());
			this.attributeValues.add(newValue);
		}
		this.effDate = cbrCase.effDate;
		this.expDate = cbrCase.expDate;
	}

	/**
	 * Formats the attributes of this class and returns a string.
	 * @return String
	 */
	public String toString() {
		return super.toString() + "CBRCase[CaseBase: " + getCaseBase() + ", case action=" + getCaseActions() + ", Attribute Values="
				+ getAttributeValues() + ", act=" + effDate + "-" + expDate + "]";
	}

	/**
	 * @return Returns the list of case actions.
	 */
	public List<CBRCaseAction> getCaseActions() {
		return caseActions;
	}

	/**
	 * @param caseActions The list of caseActions to set.
	 */
	public void setCaseActions(List<CBRCaseAction> caseActions) {
		this.caseActions.clear();
		if (caseActions != null) {
			this.caseActions.addAll(caseActions);
		}
	}

	/**
	 * @return Returns the caseBase.
	 */
	public CBRCaseBase getCaseBase() {
		return caseBase;
	}

	/**
	 * @param caseBase The caseBase to set.
	 */
	public void setCaseBase(CBRCaseBase caseBase) {
		this.caseBase = caseBase;
	}

	/**
	 * @return Returns the attributeValues.
	 */
	public List<CBRAttributeValue> getAttributeValues() {
		return attributeValues;
	}

	/**
	 * @param attributeValues The attributeValues to set.
	 */
	public void setAttributeValues(List<CBRAttributeValue> attributeValues) {
		this.attributeValues.clear();
		this.attributeValues.addAll(attributeValues);
	}

}