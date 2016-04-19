package com.mindbox.pe.server.generator.rule;

import com.mindbox.pe.common.UtilBase;

/**
 * Abstract immutable implementation of {@link AttributePattern}.
 * 
 * @author Geneho Kim
 * @since 5.1.0
 */
public abstract class AbstractAttributePattern implements AttributePattern {

	private String attributeName;
	private String variableName;
	private boolean hasValueSlot;
	private String valueText;
	private ValueSlot valueSlot;
	private int hashCode;
	private boolean skippable;

	/**
	 * Equivalent to <code>AbstractAttributePattern(attributeName, varName,hasValueSlot,valueText,valueSlot, false)</code>.
	 * @param attributeName
	 * @param varName
	 * @param hasValueSlot
	 * @param valueText
	 * @param valueSlot
	 */
	protected AbstractAttributePattern(String attributeName, String varName, boolean hasValueSlot, String valueText, ValueSlot valueSlot) {
		this(attributeName, varName, hasValueSlot, valueText, valueSlot, false);
	}

	protected AbstractAttributePattern(String attributeName, String varName, boolean hasValueSlot, String valueText, ValueSlot valueSlot, boolean canBeSkipped) {
		if (varName == null) throw new NullPointerException();
		this.attributeName = attributeName;
		this.variableName = varName;
		this.hasValueSlot = hasValueSlot;
		this.valueText = valueText;
		this.valueSlot = valueSlot;
		this.hashCode = varName.hashCode();
		this.skippable = canBeSkipped;
	}

	@Override
	public boolean canBeSkipped() {
		return skippable;
	}
	
	@Override
	public void setCanBeSkipped(boolean value) {
		this.skippable = value;
	}
	
	public final String getAttributeName() {
		return attributeName;
	}

	public final String getVariableName() {
		return variableName;
	}

	public final ValueSlot getValueSlot() {
		return valueSlot;
	}

	public final String getValueText() {
		return valueText;
	}

	public boolean hasSameValue(AttributePattern pattern) {
		if (hasValueSlot) {
			return pattern.hasValueSlot() && UtilBase.isSame(valueSlot, pattern.getValueSlot());
		}
		else {
			return UtilBase.isSame(valueText, pattern.getValueText());
		}
	}

	public final boolean hasValueSlot() {
		return hasValueSlot;
	}

	public boolean isEmpty() {
		return !hasValueSlot && (UtilBase.isEmpty(valueText) || variableName.equals(valueText));
	}

	public boolean isMoreRestrictive(AttributePattern pattern) {
		return pattern.isEmpty() && !this.isEmpty();
	}

	/**
	 * Checks equality based on {@link #getVariableName()}.
	 * @return <code>true</code> if <code>obj</code> is the same as this, 
	 *                           or if <code>obj</code> is an instance of {@link AttributePattern} 
	 *                           and its variableName is equal to variableName of this; 
	 *                           <code>false</code>, otherwise
	 */
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj instanceof AttributePattern) {
			return variableName.equals(((AttributePattern) obj).getVariableName());
		}
		else {
			return false;
		}
	}

	public int hashCode() {
		return hashCode;
	}

	public String toString() {
		return getClass().getName() + "[attr=" + attributeName + ",var=" + variableName + ",text=" + valueText + ",slot=" + valueSlot + ']';
	}
}
