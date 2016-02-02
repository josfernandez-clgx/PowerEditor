/*
 * Created on Oct 5, 2004
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.mindbox.pe.model;

/**
 * @author Inna Nill
 * @since PowerEditor 4.0.1
 *
 * To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class CBRCaseAction extends AbstractCBRConfigClass {

	private static final long serialVersionUID = 20041005125700L;
	
	public CBRCaseAction() {
		super(UNASSIGNED_ID, "", "");
	}	
	/**
	 * Constructor
	 * @param symbol
	 * @param displayName
	 */
	public CBRCaseAction(int id, String symbol, String displayName) {
		super(id, symbol, displayName);
	}
	
	public synchronized void copyFrom(CBRCaseAction caseAction) {
		super.copyFrom(caseAction);
	}

	public String toString() {
		return this.getName();
	}

}
