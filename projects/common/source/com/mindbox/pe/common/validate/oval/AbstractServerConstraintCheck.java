package com.mindbox.pe.common.validate.oval;

import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.Map;

import net.sf.oval.Validator;
import net.sf.oval.configuration.annotation.AbstractAnnotationCheck;
import net.sf.oval.context.OValContext;
import net.sf.oval.exception.OValException;

public abstract class AbstractServerConstraintCheck<ConstraintAnnotation extends Annotation> extends
		AbstractAnnotationCheck<ConstraintAnnotation> {

	private final String serverConstraintCheckClassName;
	private final Map<String, String> messageVarMap = new HashMap<String, String>();

	protected AbstractServerConstraintCheck(String serverConstraintCheckClassName) {
		this.serverConstraintCheckClassName = serverConstraintCheckClassName;
	}

	@Override
	public boolean isSatisfied(Object validatedObject, Object valueToValidate, OValContext context, Validator validator)
			throws OValException {
		if (valueToValidate == null) return true;
		try {
			Class<?> constraintCheckClass = Class.forName(serverConstraintCheckClassName);
			if (ServerConstraintCheck.class.isAssignableFrom(constraintCheckClass)) {
				ServerConstraintCheck instance = (ServerConstraintCheck) constraintCheckClass.newInstance();
				synchronized (messageVarMap) {
					messageVarMap.clear();
					boolean result = instance.isValid(validatedObject, valueToValidate, context, validator, messageVarMap);
					return result;
				}
			}
			else {
				throw new OValException(serverConstraintCheckClassName + " does not implement "
						+ ServerConstraintCheck.class.getName());
			}
		}
		catch (OValException ex) {
			throw ex;
		}
		catch (Exception ex) {
			throw new OValException(ex);
		}
	}

	@Override
	public Map<String, String> getMessageVariables() {
		synchronized (messageVarMap) {
			return messageVarMap;
		}
	}
}
