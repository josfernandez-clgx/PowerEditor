/*
 * Created on Jun 5, 2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package com.mindbox.pe.server.model.rule;

/**
 * AE Rule Action type-safe enumeration.
 * @author Gene Kim
 * @author MindBox, Inc
 * @since PowerEditor 1.0
 */
public final class RuleActionMethod {

	private static final String _addToRate = "addToRate";
	private static final String _addToFee = "addToFee";
	private static final String _nil = "nil";
	private static final String _disqualifyProduct = "disqualifyProduct";
	private static final String _acceptStipulation = "acceptStipulation";
	private static final String _setAttribute = "setAttribute";
	
	private static final String _peAction = "pe:action";
	
	public static final RuleActionMethod ADD_TO_RATE = new RuleActionMethod(_addToRate);
	public static final RuleActionMethod ADD_TO_FEE = new RuleActionMethod(_addToFee);
	public static final RuleActionMethod NIL = new RuleActionMethod(_nil);
	public static final RuleActionMethod DISQUALIFY_PRODUCT = new RuleActionMethod(_disqualifyProduct);
	public static final RuleActionMethod ACCEPT_STIPULATION = new RuleActionMethod(_acceptStipulation);
	public static final RuleActionMethod SET_ATTRBITUE = new RuleActionMethod(_setAttribute);
	
	public static final RuleActionMethod PE_ACTION = new RuleActionMethod(_peAction);


	/**
	 * Gets the instance of this for the specified value.
	 * If validate is false, this will return {@link #PE_ACTION} on unmatched string.
	 * This is to support peAction RHS generation.
	 * @param value the string representation of an instance of this
	 * @param validate the validate flag
	 * @return the instance of this for <code>value</code>
	 * @throws IllegalArgumentException if <code>validate</code> is true and <code>value</code> is invalid
	 */
	public static RuleActionMethod valueOf(String value, boolean validate) {
		if (value == null) throw new NullPointerException("value cannot be null");	
		
		if (value.equals(_addToRate)) return ADD_TO_RATE;
		if (value.equals(_addToFee)) return ADD_TO_FEE;
		if (value.equals(_nil)) return NIL;
		if (value.equals(_disqualifyProduct)) return DISQUALIFY_PRODUCT;
		if (value.equals(_acceptStipulation)) return ACCEPT_STIPULATION;
		if (value.equals(_setAttribute)) return SET_ATTRBITUE;
		if (value.equals(_peAction)) return PE_ACTION;
		
		if (validate) {
			throw new IllegalArgumentException("Invalid AE-Rule action: " + value);
		}
		else {
			return new RuleActionMethod(value);
		}
	}
	
	/**
	 * Gets the AE Rule action for the specified value.
	 * @param value the string value of the AE rule
	 * @return the AE rule
	 * @throws IllegalArgumentException if value is not a valid rule action value
	 */
	public static RuleActionMethod valueOf(String value) {
		return valueOf(value, true);
	}
	
	
	private final String value;
	
	private RuleActionMethod(String value) {
		this.value = value;
	}
	
	public boolean equals(Object obj) {
		if (obj instanceof RuleActionMethod) {
			return this.value.equals(((RuleActionMethod)obj).value);
		}
		else if (obj instanceof String) {
				return this.value.equals((String)obj);
		}
		else {
			return false;
		}
	}
	
	public String toString() {
		return value;
	}
}
