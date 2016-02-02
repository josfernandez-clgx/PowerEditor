/*
 * Created on 2005. 3. 30.
 *
 */
package com.mindbox.pe.server.imexport.digest;


/**
 * @author Geneho Kim
 * @since PowerEditor 4.2.0
 */
public class RulePrecondition {

	private int columnID = -1;


	public int getColumnID() {
		return columnID;
	}

	public void setColumnID(int columnID) {
		this.columnID = columnID;
	}
	
	public String toString() {
		return "RulePrecondition[column="+columnID+"]";
	}
}