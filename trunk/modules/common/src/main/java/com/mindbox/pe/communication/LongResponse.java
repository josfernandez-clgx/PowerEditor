package com.mindbox.pe.communication;

/**
 * @author Geneho Kim
 * @author MindBox
 */
public class LongResponse extends ResponseComm {

	private static final long serialVersionUID = 2007071200001L;

	private final Long result;

	public LongResponse(Long result) {
		if (result == null) throw new NullPointerException("result cannot be null");
		this.result = result;
	}
	
	public Long getLong() {
		return result;
	}
}
