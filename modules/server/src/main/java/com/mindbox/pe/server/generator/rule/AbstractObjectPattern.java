package com.mindbox.pe.server.generator.rule;

import java.util.ArrayList;
import java.util.List;

import com.mindbox.pe.server.generator.RuleGenerationException;

/**
 * Abstract implementation of {@link ObjectPattern}.
 * @author Geneho Kim
 *
 */
public abstract class AbstractObjectPattern extends AbstractAttributePatternList implements ObjectPattern {

	private boolean skippable;
	private final boolean shouldBeFirst;
	private String className;
	private String variableName;
	private final List<String> mustBeBeforeVariableList = new ArrayList<String>();

	/**
	 * Equivalent to <code>AbstractObjectPattern(className,variableName,false)</code>.
	 * @param className className
	 * @param variableName variableName
	 * @throws NullPointerException if className or variableName is <code>null</code>
	 */
	protected AbstractObjectPattern(String className, String variableName) {
		this(className, variableName, false);
	}

	/**
	 * 
	 * @param className className
	 * @param variableName variableName
	 * @param shouldBeFirst shouldBeFirst
	 * @throws NullPointerException if className or variableName is <code>null</code>
	 */
	protected AbstractObjectPattern(String className, String variableName, boolean shouldBeFirst) {
		if (className == null) throw new NullPointerException("className cannot be null");
		if (variableName == null) throw new NullPointerException("variableName cannot be null");
		this.className = className;
		this.variableName = variableName;
		this.shouldBeFirst = shouldBeFirst;
	}

	@Override
	public void addAll(ObjectPattern objectPattern) throws RuleGenerationException {
		for (int i = 0; i < objectPattern.size(); i++) {
			add(objectPattern.get(i));
		}
		if (!objectPattern.canBeSkipped() && skippable) {
			this.skippable = false;
		}
	}

	@Override
	public void addMustBeBeforeVariable(String variableName) {
		synchronized (mustBeBeforeVariableList) {
			if (!mustBeBeforeVariableList.contains(variableName)) {
				mustBeBeforeVariableList.add(variableName);
			}
		}
	}

	@Override
	public boolean canBeAfter(String variableName) {
		if (variableName == null || variableName.equalsIgnoreCase(this.variableName)) {
			return false;
		}
		else {
			synchronized (mustBeBeforeVariableList) {
				return !mustBeBeforeVariableList.contains(variableName);
			}
		}
	}

	@Override
	public final boolean canBeSkipped() {
		return /*!isEmpty() && */skippable;
	}

	@Override
	public final boolean containsAttribute(String variableName) {
		if (variableName == null) throw new NullPointerException("variableName cannot be null");
		for (int i = 0; i < size(); i++) {
			AttributePattern element = get(i);
			if (element.getVariableName().equals(variableName)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public final boolean containsAttributeName(String attributeName) {
		if (attributeName == null) throw new NullPointerException("attributeName cannot be null");
		for (int i = 0; i < size(); i++) {
			AttributePattern element = get(i);
			if (element.getAttributeName().equals(attributeName)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public final boolean containsNonEmptyAttribute(String variableName) {
		for (int i = 0; i < size(); i++) {
			AttributePattern element = get(i);
			if (element.getVariableName().equals(variableName) && !element.isEmpty()) {
				return true;
			}
		}
		return false;
	}

	@Override
	public final String getClassName() {
		return className;
	}

	@Override
	public final String getVariableName() {
		return variableName;
	}

	@Override
	public final void setCanBeSkipped(boolean value) {
		this.skippable = value;
	}

	@Override
	public final boolean shouldBeFirst() {
		return shouldBeFirst;
	}

	@Override
	public String toString() {
		return getClass() + "[class=" + className + ",var=" + variableName + ']';
	}
}
