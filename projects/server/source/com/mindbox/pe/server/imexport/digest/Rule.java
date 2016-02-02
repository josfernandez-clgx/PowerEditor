/*
 * Created on 2004. 3. 24.
 *
 */
package com.mindbox.pe.server.imexport.digest;

/**
 * @author Geneho Kim
 * @author MindBox
 * @since PowerEditor 2.5.0
 */
public class Rule {

	private int id = -1;

	public int getId() {
		return id;
	}

	public void setId(int i) {
		id = i;
	}

	public String toString() {
		return "Rule("+id+")";
	}
}
