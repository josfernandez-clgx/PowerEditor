package com.mindbox.pe.common.validate.oval;

import java.util.Map;

import net.sf.oval.Validator;
import net.sf.oval.context.OValContext;
import net.sf.oval.exception.OValException;

public interface ServerConstraintCheck {

	/**
	 * 
	 * @param validatedObject
	 * @param valueToValidate
	 * @param arg2
	 * @param arg3
	 * @param messageVarMap; never <code>null</code>
	 * @return
	 * @throws OValException
	 */
	boolean isValid(Object validatedObject, Object valueToValidate, OValContext arg2, Validator arg3, Map<String, String> messageVarMap) throws OValException;
	
}
