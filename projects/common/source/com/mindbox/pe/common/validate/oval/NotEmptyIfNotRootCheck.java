package com.mindbox.pe.common.validate.oval;

import net.sf.oval.Validator;
import net.sf.oval.configuration.annotation.AbstractAnnotationCheck;
import net.sf.oval.context.OValContext;
import net.sf.oval.exception.OValException;

import com.mindbox.pe.model.GenericCategory;
import com.mindbox.pe.model.assckey.MutableTimedAssociationKeySet;

public class NotEmptyIfNotRootCheck extends AbstractAnnotationCheck<NotEmptyIfNotRoot> {

	private static final long serialVersionUID = 1894528706825079059L;

	@Override
	public boolean isSatisfied(Object validatedObject, Object valueToValidate, OValContext arg2, Validator arg3) throws OValException {
		if (valueToValidate == null) return true;
		if (valueToValidate instanceof MutableTimedAssociationKeySet && validatedObject instanceof GenericCategory) {
			return ((GenericCategory)validatedObject).isRootIndicator() || !((MutableTimedAssociationKeySet)valueToValidate).isEmpty();
		}
		return false;
	}
}
