package com.mindbox.pe.server.bizlogic;

import java.util.List;

import com.mindbox.pe.common.UtilBase;
import com.mindbox.pe.common.validate.ValidationViolation;

public class DataValidationFailedException extends Exception {

	private static final long serialVersionUID = -1495587040754788929L;

	private final List<ValidationViolation> violations;

	public DataValidationFailedException(List<ValidationViolation> violations) {
		if (UtilBase.isEmpty(violations)) throw new IllegalArgumentException("violation cannot be null or empty");
		this.violations = violations;
	}

	public List<ValidationViolation> getViolations() {
		return violations;
	}

	@Override
	public String getMessage() {
		StringBuilder buff = new StringBuilder();
		buff.append("Validation Errors: ");
		for (ValidationViolation violation : violations) {
			buff.append(violation.getMessage());
			buff.append(" for ");
			buff.append(violation.getValidatedObject());
			buff.append("; ");
		}
		return buff.toString();
	}
}
