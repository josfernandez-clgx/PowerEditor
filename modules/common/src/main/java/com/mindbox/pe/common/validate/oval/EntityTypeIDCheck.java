package com.mindbox.pe.common.validate.oval;

import com.mindbox.pe.model.GenericEntityType;

import net.sf.oval.Validator;
import net.sf.oval.configuration.annotation.AbstractAnnotationCheck;
import net.sf.oval.context.OValContext;
import net.sf.oval.exception.OValException;

public class EntityTypeIDCheck extends AbstractAnnotationCheck<EntityTypeID> {

	private static final long serialVersionUID = -548549499608336316L;

	@Override
	public boolean isSatisfied(Object validatedObject, Object valueToValidate, OValContext arg2, Validator arg3) throws OValException {
		if (valueToValidate == null) return true;
		if (valueToValidate.getClass().equals(Integer.class)) {
			return GenericEntityType.hasTypeFor(((Integer) valueToValidate).intValue());
		}
		return false;
	}

}
