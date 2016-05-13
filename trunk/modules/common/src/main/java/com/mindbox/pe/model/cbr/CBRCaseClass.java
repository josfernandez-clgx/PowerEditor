/*
 * Created on Oct 5, 2004
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.mindbox.pe.model.cbr;

/**
 * @author Inna Nill
 * @since PowerEditor 4.1.0
 *
 * To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class CBRCaseClass extends AbstractCBRConfigClass {

	private static final long serialVersionUID = 20041005135800L;

	public CBRCaseClass() {
		super(UNASSIGNED_ID, "","");
	}	
	/**
	 * Constructor that takes 2 params
	 * @param symbol
	 * @param displayName
	 */
	public CBRCaseClass(int id, String symbol, String displayName) {
		super(id, symbol, displayName);
	}
	
	/**
	 * Copy
	 * @param caseClass
	 */
	public synchronized void copyFrom(CBRCaseClass caseClass) {
		super.copyFrom(caseClass);
	}
	
	public String toString() {
		return super.toString() + "   CBRCaseClass[]";
	}

}
