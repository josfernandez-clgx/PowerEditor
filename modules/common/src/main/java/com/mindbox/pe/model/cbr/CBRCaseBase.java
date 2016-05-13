/*
 * Created on Oct 5, 2004
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.mindbox.pe.model.cbr;

import com.mindbox.pe.model.AbstractIDNameDescriptionObject;
import com.mindbox.pe.model.Auditable;
import com.mindbox.pe.model.Constants;
import com.mindbox.pe.model.DateSynonym;

/**
 * @author deklerk
 * @since PowerEditor 4.1.0
 *
 * To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */

public class CBRCaseBase extends AbstractIDNameDescriptionObject implements Auditable {

	private static final long serialVersionUID = 20041005083100L;

	private CBRCaseClass caseClass;
	private String indexFile;
	private CBRScoringFunction scoringFunction;
	private String namingAttribute;
	private int matchThreshold = Constants.CBR_NULL_DATA_EQUIVALENT_VALUE;
	private int maximumMatches = Constants.CBR_NULL_DATA_EQUIVALENT_VALUE;
	private DateSynonym effDate, expDate = null;

	public CBRCaseBase() {
		super(UNASSIGNED_ID, "", null);
	}

	public CBRCaseBase(CBRCaseBase source) {
		super(source);
		copyFrom(source);
	}

	/**
	 * @param id id
	 * @param name name
	 * @param desc desc
	 */
	public CBRCaseBase(int id, String name, String desc) {
		super(id, name, desc);
	}

	/**
	 * @param name name
	 * @param desc desc
	 */
	public CBRCaseBase(String name, String desc) {
		super(name, desc);
	}

	public synchronized void copyFrom(CBRCaseBase caseBase) {
		setName(caseBase.getName());
		setDescription(caseBase.getDescription());
		this.caseClass = caseBase.caseClass;
		this.indexFile = caseBase.indexFile;
		this.scoringFunction = caseBase.scoringFunction;
		this.namingAttribute = caseBase.namingAttribute;
		this.matchThreshold = caseBase.matchThreshold;
		this.maximumMatches = caseBase.maximumMatches;
		this.effDate = caseBase.effDate;
		this.expDate = caseBase.expDate;
	}

	@Override
	public Auditable deepCopy() {
		CBRCaseBase newCaseBase = new CBRCaseBase();
		newCaseBase.setID(getID());
		newCaseBase.copyFrom(this);
		return newCaseBase;
	}

	@Override
	public String getAuditDescription() {
		return toString();
	}

	/**
	 * @return Returns the caseClass.
	 */
	public CBRCaseClass getCaseClass() {
		return caseClass;
	}

	public final DateSynonym getEffectiveDate() {
		return effDate;
	}

	public final DateSynonym getExpirationDate() {
		return expDate;
	}

	/**
	 * @return Returns the indexFile.
	 */
	public String getIndexFile() {
		return indexFile;
	}

	/**
	 * @return Returns the matchThreshold.
	 */
	public int getMatchThreshold() {
		return matchThreshold;
	}

	/**
	 * @return Returns the maximumMatches.
	 */
	public int getMaximumMatches() {
		return maximumMatches;
	}

	/**
	 * @return Returns the namingAttribute.
	 */
	public String getNamingAttribute() {
		return namingAttribute;
	}

	/**
	 * @return Returns the scoringFunction.
	 */
	public CBRScoringFunction getScoringFunction() {
		return scoringFunction;
	}

	/**
	 * @param caseClass The caseClass to set.
	 */
	public void setCaseClass(CBRCaseClass caseClass) {
		this.caseClass = caseClass;
	}

	public final void setEffectiveDate(DateSynonym effDate) {
		this.effDate = effDate;
	}

	public final void setExpirationDate(DateSynonym expDate) {
		this.expDate = expDate;
	}

	/**
	 * @param indexFile The indexFile to set.
	 */
	public void setIndexFile(String indexFile) {
		this.indexFile = indexFile;
	}

	/**
	 * @param matchThreshold The matchThreshold to set.
	 */
	public void setMatchThreshold(int matchThreshold) {
		this.matchThreshold = matchThreshold;
	}

	/**
	 * @param maximumMatches The maximumMatches to set.
	 */
	public void setMaximumMatches(int maximumMatches) {
		this.maximumMatches = maximumMatches;
	}

	/**
	 * @param namingAttribute The namingAttribute to set.
	 */
	public void setNamingAttribute(String namingAttribute) {
		this.namingAttribute = namingAttribute;
	}

	/**
	 * @param scoringFunction The scoringFunction to set.
	 */
	public void setScoringFunction(CBRScoringFunction scoringFunction) {
		this.scoringFunction = scoringFunction;
	}

	@Override
	public String toString() {
		return "CBRcaseBase[" + getID() + ",name=" + getName() + ", act=" + effDate + "-" + expDate + ", indexFile=" + indexFile + ",matchThreshold=" + getMatchThreshold()
				+ ",maximumMatches=" + getMaximumMatches() + "]";
	}

}