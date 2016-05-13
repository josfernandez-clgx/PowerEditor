package com.mindbox.pe.model.exceptions;

/**
 * Base exception for PE specific exceptions.
 * @author Geneho Kim
 * @author MindBox
 */
public class SapphireException extends Exception {

	private static final long serialVersionUID = 5020004651124584235L;

	public SapphireException() {}

	public SapphireException(String s) {
		super(s);
	}
}
