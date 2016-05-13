package com.mindbox.pe.common.validate.oval;

import java.util.Map;

import net.sf.oval.Validator;
import net.sf.oval.context.OValContext;
import net.sf.oval.exception.OValException;

public interface ServerConstraintCheck {

	/**
	 * 
	 * @param validatedObject validatedObject
	 * @param valueToValidate valueToValidate
	 * @param arg2 arg2
	 * @param arg3 arg3
	 * @param messageVarMap message var map never <code>null</code>
	 * @return true if valid; false, otherwise
	 * @throws OValException on error
	 */
	boolean isValid(Object validatedObject, Object valueToValidate, OValContext arg2, Validator arg3, Map<String, String> messageVarMap) throws OValException;

}
