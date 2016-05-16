package com.mindbox.pe.server.generator.rule;

import com.mindbox.pe.model.rule.Reference;

/**
 * Encapsulates a generated ART*Enterprise guideline rule.
 * 
 * @since 5.1.0
 */
public final class GuidelineRule {

	private LHSPatternList patternList;
	private FunctionCallPattern rhsPattern;

	public LHSPatternList getLHSPatternList() {
		return patternList;
	}

	public FunctionCallPattern getRHSFunctionCall() {
		return rhsPattern;
	}

	/**
	 * Tests if the LHS of this rule contains a pattern that uses the specified reference.
	 * @param reference reference
	 * @return true if has pattern; false, otherwise
	 * @throws NullPointerException if reference is <code>null</code>
	 */
	public boolean hasPatternForReference(Reference reference) {
		if (reference == null) throw new NullPointerException("reference cannot be null");
		if (patternList == null || patternList.isEmpty()) return false;
		return patternList.hasPatternForReference(reference);
	}

	void setLHSPatternList(LHSPatternList patternList) {
		this.patternList = patternList;
	}

	void setRHSFunctionCall(FunctionCallPattern rhsPattern) {
		this.rhsPattern = rhsPattern;
	}
}
