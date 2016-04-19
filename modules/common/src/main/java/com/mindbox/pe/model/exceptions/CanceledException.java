package com.mindbox.pe.model.exceptions;


/**
 * Thrown to indicate an operation has canceled.
 * @author Geneho Kim
 * @since PowerEditor 4.0
 */
public class CanceledException extends Exception {

	private static final long serialVersionUID = -7915480944430663262L;

	private static CanceledException instance = null;

	/**
	 * Gets the one and only instance of this class.
	 * @return the only instance
	 */
	public static CanceledException getInstance() {
		if (instance == null) {
			instance = new CanceledException();
		}
		return instance;
	}

	private CanceledException() {
	}
}
