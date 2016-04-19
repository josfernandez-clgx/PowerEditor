package com.mindbox.pe.server.generator.rule;

import com.mindbox.pe.common.UtilBase;
import com.mindbox.pe.server.generator.RuleGenerationException;

public final class OptimizingObjectPattern extends AbstractObjectPattern {

	private static String extractTrueVarName(String var) {
		if (var == null) return "";
		if (var.indexOf(" & ") > 0) {
			return var.substring(0, var.indexOf(" & "));
		}
		else {
			return var;
		}
	}

	static boolean isMatchingVariableName(String var1, String var2) {
		String ve1 = extractTrueVarName(var1);
		String ve2 = extractTrueVarName(var2);
		return UtilBase.isSame(ve1, ve2);
	}

	/**
	 * Tests if the specified object pattern has an attribute pattern that conflicts with the specified attribute pattern.
	 * @param objectPattern the object pattern
	 * @param attributePattern the attribute pattern to check for
	 * @return <code>true</code> if <code>objectPattern</code> has an attribute pattern that conflicts with <code>attributePattern</code>
	 */
	public static boolean hasConflictingAttributePattern(ObjectPattern objectPattern, AttributePattern attributePattern) {
		if (objectPattern == null) throw new NullPointerException("objectPattern cannot be null");
		if (!attributePattern.isEmpty() && objectPattern.containsNonEmptyAttribute(attributePattern.getVariableName())) {
			return true;
		}
		else if (objectPattern.containsAttributeName(attributePattern.getAttributeName())) {
			// check if objectPattern contains an attribute pattern with the same attribute name but with different variable name
			for (int j = 0; j < objectPattern.size(); j++) {
				if (objectPattern.get(j).getAttributeName().equals(attributePattern.getAttributeName())
						&& !isMatchingVariableName(objectPattern.get(j).getVariableName(), attributePattern.getVariableName())) {
					return true;
				}
			}
		}
		return false;
	}

	static OptimizingObjectPattern createNormalInstance(String className, String variableName) {
		return new OptimizingObjectPattern(className, variableName, false);
	}

	static OptimizingObjectPattern createShouldBeFirstInstance(String className, String variableName) {
		return new OptimizingObjectPattern(className, variableName, true);
	}

	private OptimizingObjectPattern(String className, String variableName, boolean shouldBeFirst) {
		super(className, variableName, shouldBeFirst);
	}

	public void add(AttributePattern attributePattern) throws RuleGenerationException {
		if (contains(attributePattern)) {
			AttributePattern existingPattern = find(attributePattern);
			// add if arg is more restrictive than the existing one
			if (attributePattern.isMoreRestrictive(existingPattern)) {
				if (!existingPattern.canBeSkipped() && attributePattern.canBeSkipped()) {
					attributePattern.setCanBeSkipped(false);
				}
				super.replace(existingPattern, attributePattern);
			}
			// if adding the same attribute pattern, ignore it!
			else if (attributePattern.hasSameValue(existingPattern)) {
				if (!attributePattern.canBeSkipped() && existingPattern.canBeSkipped()) {
					existingPattern.setCanBeSkipped(false);
				}
				return;
			}
			// if both are not empty, throw an exception. 
			// Having more that one attribute pattern for the same attribute is illegal in ART*Enterprise
			else if (!existingPattern.isEmpty() && !attributePattern.isEmpty()) {
				throw new RuleGenerationException("Cannot add another non-empty pattern: " + attributePattern
						+ "; already has an attribute pattern for the same attribute " + existingPattern);
			}
			else {
				if (!attributePattern.canBeSkipped() && existingPattern.canBeSkipped()) {
					existingPattern.setCanBeSkipped(false);
				}
			}
		}
		else if (containsAttributeName(attributePattern.getAttributeName())) {
			// If there is an existing pattern with the same attribute name, throws an exception
			throw new RuleGenerationException("Cannot add another attribute pattern with the same attribute name: "
					+ attributePattern.getAttributeName() + " (var = " + attributePattern.getVariableName() + ")");
		}
		else {
			super.add(attributePattern);
		}
	}
	
	public void insert(AttributePattern attributePattern) throws RuleGenerationException {
		if (contains(attributePattern)) {
			AttributePattern existingPattern = find(attributePattern);
			// add if arg is more restrictive than the existing one
			if (attributePattern.isMoreRestrictive(existingPattern)) {
				if (!existingPattern.canBeSkipped() && attributePattern.canBeSkipped()) {
					attributePattern.setCanBeSkipped(false);
				}
				super.replace(existingPattern, attributePattern);
			}
			// if adding the same attribute pattern, ignore it!
			else if (attributePattern.hasSameValue(existingPattern)) {
				if (!attributePattern.canBeSkipped() && existingPattern.canBeSkipped()) {
					existingPattern.setCanBeSkipped(false);
				}
				return;
			}
			// if both are not empty, throw an exception. 
			// Having more that one attribute pattern for the same attribute is illegal in ART*Enterprise
			else if (!existingPattern.isEmpty() && !attributePattern.isEmpty()) {
				throw new RuleGenerationException("Cannot add another non-empty pattern: " + attributePattern
						+ "; already has an attribute pattern for the same attribute " + existingPattern);
			}
			else {
				if (!attributePattern.canBeSkipped() && existingPattern.canBeSkipped()) {
					existingPattern.setCanBeSkipped(false);
				}
			}
		}
		else if (containsAttributeName(attributePattern.getAttributeName())) {
			// If there is an existing pattern with the same attribute name, throws an exception
			throw new RuleGenerationException("Cannot add another attribute pattern with the same attribute name: "
					+ attributePattern.getAttributeName() + " (var = " + attributePattern.getVariableName() + ")");
		}
		else {
			super.insert(attributePattern);
		}
	}
}
