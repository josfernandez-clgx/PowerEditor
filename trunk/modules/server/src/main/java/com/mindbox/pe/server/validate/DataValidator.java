package com.mindbox.pe.server.validate;

import java.util.List;

import com.mindbox.pe.common.validate.ValidationViolation;

public interface DataValidator {

	List<ValidationViolation> validate(Object objectToValidate);
}
