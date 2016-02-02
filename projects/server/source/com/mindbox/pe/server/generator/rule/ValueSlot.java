package com.mindbox.pe.server.generator.rule;


/**
 * Value slot of an attribute pattern.
 * @author Geneho Kim
 * @see AttributePattern
 * @see ColumnReferencePatternValueSlot
 * @see RowNumberValueSlot
 * @since 5.1.0
 */
public interface ValueSlot extends FunctionArgument {

	public enum Type {

		/**
		 * Column reference slot type. 
		 * @see ColumnReferencePatternValueSlot
		 */
		COLUMN_REFERENCE,

		/**
		 * Cell value slot type.
		 * @see CellValueValueSlot
		 */
		CELL_VALUE,

		/**
		 * Row number slot type.
		 * @see RowNumberValueSlot
		 */
		ROW_NUMBER,

		/**
		 * Date property slot type.
		 * @see DatePropertyValueSlot
		 */
		DATE_PROPERTY,

		/**
		 * Rule name slot type.
		 * @see RuleNameValueSlot
		 */
		RULE_NAME,

		/**
		 * Context slot type.
		 * @see ContextValueSlot
		 */
		CONTEXT,

		/**
		 * Category ID slot type.
		 * @see CategoryIDValueSlot
		 */
		CATEGORY_ID,

		/**
		 * Category name slot type.
		 * @see CategoryNameValueSlot
		 */
		CATEGORY_NAME,

		/**
		 * Focus of attention slot type.
		 * @see FocusOfAttentionPatternValueSlot
		 */
		FOCUS_OF_ATTENTION,

		/**
		 * Time slice slot type.
		 * @see TimeSlicePatternValueSlot
		 */
		TIME_SLICE,

		/**
		 * Context element slot type.
		 * @see TimeSlicePatternValueSlot
		 */
		CONTEXT_ELEMENT,

		/**
		 * String slot type.
		 * @see StringValuePatternValueSlot
		 */
		STRING,

		/**
		 * Category ID slot type.
		 * @see CategoryIDValueSlot
		 */
		ENTITY_ID,

		/**
		 * Rule name slot type.
		 * @see RuleNameValueSlot
		 */
		RULE_ID,

		/**
		 * Activation span slot type.
		 * @see ActivationSpanValueSlot
		 */
		ACTIVATION_SPAN;
	}

	/**
	 * Gets the type of this.
	 * @return type
	 */
	Type getType();

	/**
	 * Gets a type-specific slot value of this.
	 * For {@link #COLUMN_REFERENCE}, this returns an instance of java.lang.Integer.
	 * @return slot value
	 */
	Object getSlotValue();

	/**
	 * Gets the text for this slot.
	 * This is an optional text only used for certain conditions, such as math expressions and test conditions in LHS.
	 * @return slot text in the format supported by <code>java.lang.MessageFormat</code>, where {0} is the place holder for the slot value; 
	 *         may be <code>null</code>, if not to be used
	 */
	String getSlotText();
}
