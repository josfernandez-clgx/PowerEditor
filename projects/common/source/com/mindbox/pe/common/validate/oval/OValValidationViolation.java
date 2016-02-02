package com.mindbox.pe.common.validate.oval;

import java.util.ArrayList;
import java.util.Collection;

import net.sf.oval.ConstraintViolation;
import net.sf.oval.context.OValContext;

import com.mindbox.pe.common.validate.DefaultValidationViolation;
import com.mindbox.pe.common.validate.ValidationViolation;

public final class OValValidationViolation extends DefaultValidationViolation {

	private static final long serialVersionUID = 4407168360882360771L;

	private static Collection<ValidationViolation> asValidationViolations(ConstraintViolation[] constraintViolations) {
		if (constraintViolations == null) return null;
		Collection<ValidationViolation> list = new ArrayList<ValidationViolation>();
		for (int i = 0; i < constraintViolations.length; i++) {
			list.add(new OValValidationViolation(constraintViolations[i]));
		}
		return list;
	}

	private ConstraintViolation constraintViolation;

	public OValValidationViolation(ConstraintViolation constraintViolation) {
		super(
				constraintViolation.getValidatedObject(),
				constraintViolation.getMessage(),
				constraintViolation.getInvalidValue(),
				asValidationViolations(constraintViolation.getCauses()));
	}

	// TODO Kim: add convenience methods around context
	OValContext getOValContext() {
		return constraintViolation.getContext();
	}

}
