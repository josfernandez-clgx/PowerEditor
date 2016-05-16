package com.mindbox.pe.server.generator;


/**
 * Thrown to indicate rule generation error.
 * All instances of this must be caught and reported in the rule generation error log.
 * @author Geneho Kim
 * @since PowerEditor 4.2.0
 */
public final class RuleGenerationException extends Exception {

	private static final long serialVersionUID = -8480600759755413878L;

	public RuleGenerationException(String errorMsg) {
		super(errorMsg);
	}

	public String getErrorMessage() {
		return getMessage();
	}

}