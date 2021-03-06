package com.mindbox.pe.model.cbr;


/**
 * @author Inna Nill
 * @since PowerEditor 4.1.0
 *
 * To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class CBRAttributeType extends AbstractCBRConfigClass {

	private static final long serialVersionUID = 20041006124800L;

	private int defaultMatchContribution;
	private int defaultMismatchPenalty;
	private int defaultAbsencePenalty;
	private String defaultValueRange;
	private Boolean askForMatchInterval;
	private Boolean askForNumericRange;

	public CBRAttributeType() {
		super(UNASSIGNED_ID, "", "");
	}

	/**
	 * Constructor
	 * @param id id
	 * @param symbol symbol
	 * @param displayName displayName
	 */
	public CBRAttributeType(int id, String symbol, String displayName) {
		super(id, symbol, displayName);
	}

	/**
	 * Constructor
	 * @param id id
	 * @param symbol symbol
	 * @param displayName displayName
	 * @param description description
	 */
	public CBRAttributeType(int id, String symbol, String displayName, String description) {
		super(id, symbol, displayName, description);
	}

	public synchronized void copyFrom(CBRAttributeType attrType) {
		super.copyFrom(attrType);
		this.defaultMatchContribution = attrType.defaultMatchContribution;
		this.defaultMismatchPenalty = attrType.defaultMismatchPenalty;
		this.defaultAbsencePenalty = attrType.defaultAbsencePenalty;
		this.defaultValueRange = attrType.defaultValueRange;
		this.askForMatchInterval = attrType.askForMatchInterval;
		this.askForNumericRange = attrType.askForNumericRange;
	}

	/**
	 * @return Returns the askForMatchInterval.
	 */
	public Boolean getAskForMatchInterval() {
		return askForMatchInterval;
	}

	/**
	 * @return Returns the askForNumericRange.
	 */
	public Boolean getAskForNumericRange() {
		return askForNumericRange;
	}

	/**
	 * @return Returns the defaultAbsencePenalty.
	 */
	public int getDefaultAbsencePenalty() {
		return defaultAbsencePenalty;
	}

	/**
	 * @return Returns the default match contribution.
	 */
	public int getDefaultMatchContribution() {
		return defaultMatchContribution;
	}

	/**
	 * @return Returns the defaultMismatchPenalty.
	 */
	public int getDefaultMismatchPenalty() {
		return defaultMismatchPenalty;
	}

	/**
	 * @return Returns the defaultValueRange.
	 */
	public String getDefaultValueRange() {
		return defaultValueRange;
	}

	/**
	 * @param askForMatchInterval The askForMatchInterval to set.
	 */
	public void setAskForMatchInterval(Boolean askForMatchInterval) {
		this.askForMatchInterval = askForMatchInterval;
	}

	/**
	 * @param askForNumericRange The askForNumericRange to set.
	 */
	public void setAskForNumericRange(Boolean askForNumericRange) {
		this.askForNumericRange = askForNumericRange;
	}

	/**
	 * @param defaultAbsencePenalty The defaultAbsencePenalty to set.
	 */
	public void setDefaultAbsencePenalty(int defaultAbsencePenalty) {
		this.defaultAbsencePenalty = defaultAbsencePenalty;
	}

	/**
	 * @param defaultMatchContribution The defaultMatchContribution to set.
	 */
	public void setDefaultMatchContribution(int defaultMatchContribution) {
		this.defaultMatchContribution = defaultMatchContribution;
	}

	/**
	 * @param defaultMismatchPenalty The defaultMismatchPenalty to set.
	 */
	public void setDefaultMismatchPenalty(int defaultMismatchPenalty) {
		this.defaultMismatchPenalty = defaultMismatchPenalty;
	}

	/**
	 * @param defaultValueRange The defaultValueRange to set.
	 */
	public void setDefaultValueRange(String defaultValueRange) {
		this.defaultValueRange = defaultValueRange;
	}

	@Override
	public String toString() {
		return super.toString() + ":   CBRAttributeType[" + ",def match contribution=" + defaultMatchContribution + ",def mismatch penalty=" + defaultMismatchPenalty
				+ ",def absense penalty=" + defaultAbsencePenalty + ",def value range=" + defaultValueRange + ",ask for match interval=" + askForMatchInterval
				+ ",ask for numeric range=" + askForNumericRange + "]";
	}

}
