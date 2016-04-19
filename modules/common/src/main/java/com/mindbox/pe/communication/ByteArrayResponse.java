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
public class ByteArrayResponse extends ResponseComm {

	private static final long serialVersionUID = 2003121611300000L;

	private final byte[] result;

	public ByteArrayResponse(byte[] result) {
		this.result = result;
	}
	
	public byte[] getByteArray() {
		return result;
	}
}
