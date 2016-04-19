package com.mindbox.pe.communication;

/**
 * @author Geneho Kim
 * @author MindBox
 */
public class BooleanResponse extends ResponseComm {

	private static final long serialVersionUID = 2003121516303000L;

	private final boolean result;

	public BooleanResponse(boolean result) {
		this.result = result;
	}
	
	public boolean isTrue() {
		return result;
	}
}
