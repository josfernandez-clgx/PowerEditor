package com.mindbox.pe.communication;

import java.util.Collections;
import java.util.List;

import com.mindbox.pe.common.validate.ValidationViolation;

public class ValidationErrorResponse extends ErrorResponse {

	private static final long serialVersionUID = -3242123895132047462L;

	private final List<ValidationViolation> violations;

	public ValidationErrorResponse(String message, List<ValidationViolation> violations) {
		super(VALIDATION_ERROR, message);
		this.violations = violations;
	}

	@Override
	public List<ValidationViolation> getViolations() {
		return Collections.unmodifiableList(violations);
	}

}
