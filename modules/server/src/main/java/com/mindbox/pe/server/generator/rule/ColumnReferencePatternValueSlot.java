package com.mindbox.pe.server.generator.rule;

import com.mindbox.pe.model.DeployType;
import com.mindbox.pe.model.rule.Reference;

/**
 * A concrete implementation of {@link ValueSlot} of {@link ValueSlot.Type#COLUMN_REFERENCE}.
 * The {@link ValueSlot#getSlotValue()} method of this returns an instance of java.lang.Integer; 
 * {@link #getColumnNo} can be used as an alternative.
 * @author Geneho Kim
 * @since 5.1.0
 */
public final class ColumnReferencePatternValueSlot extends AbstractPatternValueSlot {

	private DeployType parameterDeployType;

	ColumnReferencePatternValueSlot(int columnNo) {
		this(null, 0, columnNo);
	}

	ColumnReferencePatternValueSlot(int columnNo, String slotText) {
		this(null, 0, columnNo, slotText);
	}

	ColumnReferencePatternValueSlot(Reference reference, int operator, int columnNo) {
		this(reference, operator, columnNo, null);
	}

	ColumnReferencePatternValueSlot(Reference reference, int operator, int columnNo, String slotText) {
		super(ValueSlot.Type.COLUMN_REFERENCE, reference, operator, new Integer(columnNo), slotText);
	}

	/**
	 * Convenience method for getting the slot value.
	 * Equivalent to <code>((Integer) getSlotValue()).intValue()</code>.
	 * @return the slot value as a column number
	 */
	public int getColumnNo() {
		return ((Integer) getSlotValue()).intValue();
	}

	public DeployType getParameterDeployType() {
		return parameterDeployType;
	}

	public void setParameterDeployType(DeployType parameterDeployType) {
		this.parameterDeployType = parameterDeployType;
	}
}
