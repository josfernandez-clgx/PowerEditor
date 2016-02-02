package com.mindbox.pe.common.validate.oval;

import net.sf.oval.Validator;
import net.sf.oval.configuration.annotation.AbstractAnnotationCheck;
import net.sf.oval.context.OValContext;
import net.sf.oval.exception.OValException;

import com.mindbox.pe.model.Persistent;

public class PositiveOrUnassignedCheck extends AbstractAnnotationCheck<PositiveOrUnassigned> {

	private static final long serialVersionUID = 1894528706825079059L;

	@Override
	public boolean isSatisfied(Object validatedObject, Object valueToValidate, OValContext arg2, Validator arg3) throws OValException {
		if (valueToValidate == null) return true;
		if (valueToValidate instanceof Number) {
			int value = ((Number) valueToValidate).intValue();
			return value == Persistent.UNASSIGNED_ID || value > 0;
		}
		return false;
	}
}
