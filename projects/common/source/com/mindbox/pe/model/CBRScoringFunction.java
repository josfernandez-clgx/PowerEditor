/*
 * Created on Oct 5, 2004
 */
package com.mindbox.pe.model;

/**
 * @author Inna Nill
 * @since PowerEditor 4.1.0
 */
public class CBRScoringFunction extends AbstractCBRConfigClass {

	private static final long serialVersionUID = 20041005130000L;
	
	/**
	 * Default constructor.
	 * Equivalent to <code>new CBRScoringFunction(-1, "", "")</code>.
	 */
	public CBRScoringFunction() {
		super(UNASSIGNED_ID, "","");
	}	

	/**
	 * 
	 * @param id
	 * @param symbol
	 * @param displayName
	 */
	public CBRScoringFunction(int id, String symbol, String displayName) {
		super(id, symbol, displayName);
	}
	
	public synchronized void copyFrom(CBRScoringFunction scoringFunction) {
		super.copyFrom(scoringFunction);
	}
	
	public String toString() {
		return super.toString() + "   CBRScoringFunction[]";
	}
}
