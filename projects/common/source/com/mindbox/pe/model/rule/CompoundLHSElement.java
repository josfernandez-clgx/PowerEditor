package com.mindbox.pe.model.rule;

/**
 * @author Geneho Kim
 * @author MindBox
 * @since PowerEditor 2.3.0
 */
public interface CompoundLHSElement extends LHSElement, CompoundRuleElement<LHSElement> {
	
	public static final int TYPE_AND = 0;
	public static final int TYPE_OR  = 1;
	public static final int TYPE_NOT = -1;
	
	/**
	 * Returns the type of this compound condition.
	 * @return type of this. Must be one of the defined type constants
	 */
	int getType();
	
	void setType(int type);
}
