package com.mindbox.pe.server.generator.rule;

import com.mindbox.pe.server.generator.RuleGenerationException;

public final class OptimizingLHSPatternList extends AbstractLHSPatternList {

	OptimizingLHSPatternList(int type) {
		super(type);
	}

	final boolean hasConflictingAttributePattern(ObjectPattern objectPattern) {
		if (objectPattern == null) throw new NullPointerException("objectPattern cannot be null");
		for (int i = 0; i < size(); i++) {
			LHSPattern element = (LHSPattern) get(i);
			if (element instanceof ObjectPattern) {
				ObjectPattern objectPatternInThis = (ObjectPattern) element;
				if (OptimizingObjectPattern.isMatchingVariableName(objectPattern.getVariableName(), objectPatternInThis.getVariableName())) {
					for (int j = 0; j < objectPattern.size(); j++) {
						if (OptimizingObjectPattern.hasConflictingAttributePattern(objectPatternInThis, objectPattern.get(j))) {
							return true;
						}
					}
				}
			}
			// DO NOT check embedded LHSPatternList
		}
		return false;
	}

	public void append(ObjectPattern objectPattern) throws RuleGenerationException {
		if (hasConflictingAttributePattern(objectPattern)) {
			super.append(objectPattern);
		}
		else if (hasPatternForVariableName(objectPattern.getVariableName())) {
			ObjectPattern existingPattern = find(objectPattern.getVariableName());
			if (existingPattern.getClassName().equalsIgnoreCase(objectPattern.getClassName())) {
				existingPattern.addAll(objectPattern);
			}
			else {
				super.append(objectPattern);
			}
		}
		else {
			super.append(objectPattern);
		}
	}

	public void insert(ObjectPattern objectPattern, boolean preserveOrderIfPatternExists) throws RuleGenerationException {
		// NOTE: this should NOT call insert(ObjectPattern), as that will result in infinite loop
		if (canMergeIntoExistingPattern(objectPattern)) {
			ObjectPattern existingPattern = find(objectPattern.getVariableName());
			existingPattern.addAll(objectPattern);
			if (!preserveOrderIfPatternExists) {
				super.remove(existingPattern);
				super.insert(existingPattern, false);
			}
		}
		else {
			super.insert(objectPattern, preserveOrderIfPatternExists);
		}
	}

	public void insertBefore(ObjectPattern objectPattern, String variableName) throws RuleGenerationException {
		if (canMergeIntoExistingPattern(objectPattern)) {
			int index = indexOfPatternWithVariable(variableName);
			if (index >= 0) {
				ObjectPattern existingPatternForObjectPattern = find(objectPattern.getVariableName());
				existingPatternForObjectPattern.addAll(objectPattern);
				int indexOfExistingPattern = indexOfPatternWithVariable(existingPatternForObjectPattern.getVariableName());
				// if existing pattern is after the object pattern, move it before the object pattern, if possible
				if (indexOfExistingPattern > index) {
					 //&& existingPatternForObjectPattern.canBeAfter(variableName)) {
					ObjectPattern existingPatternForVariableName = find(variableName);
					if (existingPatternForVariableName != null && existingPatternForVariableName.canBeAfter(objectPattern.getVariableName())) {
						// switch the order
						super.remove(existingPatternForObjectPattern);
						super.insertBefore(existingPatternForObjectPattern, variableName);
					}
				}
				existingPatternForObjectPattern.addMustBeBeforeVariable(variableName);
			}
			else {
				super.insertBefore(objectPattern, variableName);
			}
		}
		else {
			super.insertBefore(objectPattern, variableName);
		}
	}

	private boolean canMergeIntoExistingPattern(ObjectPattern objectPattern) {
		if (hasConflictingAttributePattern(objectPattern)) {
			return false;
		}
		else {
			int index = indexOfPatternWithVariable(objectPattern.getVariableName());
			if (index < 0) {
				return false;
			}
			else {
				if (objectPattern.shouldBeFirst()) {
					// return false if the would-be-merged-pattern is not first,
					// and any preceding pattern is not an object pattern
					for (int i = 0; i < index; i++) {
						LHSPattern pattern = get(i);
						if (!(pattern instanceof ObjectPattern)) {
							return false;
						}
					}
				}
				return true;
			}
		}

	}
}
