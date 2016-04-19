package com.mindbox.pe.common.validate.oval;

import net.sf.oval.Validator;
import net.sf.oval.configuration.annotation.AbstractAnnotationCheck;
import net.sf.oval.context.OValContext;
import net.sf.oval.exception.OValException;

import com.mindbox.pe.model.DateSynonym;
import com.mindbox.pe.model.assckey.TimedAssociationKey;

public class EffectiveDateBeforeExpirationDateCheck extends AbstractAnnotationCheck<EffectiveDateBeforeExpirationDate> {

	private static final long serialVersionUID = 1894528706825079059L;

	@Override
	public boolean isSatisfied(Object validatedObject, Object valueToValidate, OValContext context, Validator validator)
			throws OValException {
		if (validatedObject instanceof TimedAssociationKey) {
			return isValidRange(
					((TimedAssociationKey) validatedObject).getEffectiveDate(),
					((TimedAssociationKey) validatedObject).getExpirationDate());
		}
		return false;
	}

	private boolean isValidRange(DateSynonym effDate, DateSynonym expDate) {
		return effDate == null || expDate == null || effDate.before(expDate);
	}
}
