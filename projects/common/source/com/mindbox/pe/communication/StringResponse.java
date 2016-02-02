/*
 * Created on 2003. 12. 15.
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package com.mindbox.pe.communication;

/**
 * @author Geneho Kim
 * @author MindBox
 */
public class StringResponse extends ResponseComm {

	private static final long serialVersionUID = 2003121611300000L;

	private final String result;

	public StringResponse(String result) {
		this.result = result;
	}
	
	public String getString() {
		return result;
	}
}
