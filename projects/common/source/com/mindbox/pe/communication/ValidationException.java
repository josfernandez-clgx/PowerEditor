package com.mindbox.pe.communication;

import java.util.Collections;
import java.util.List;

import com.mindbox.pe.common.validate.ValidationViolation;


/**
 * 
 * @author Gene Kim
 * @author MindBox, Inc
 * @since PowerEditor 1.0
 */
public class ValidationException extends ServerException {

	private static final long serialVersionUID = 8191039379235941661L;

	private final List<ValidationViolation> violations;

	public ValidationException() {
		this(null);
	}

	public ValidationException(List<ValidationViolation> violations) {
		super("ValidationErrorMsg", null);
		this.violations = Collections.unmodifiableList(violations);
	}

	public List<ValidationViolation> getViolations() {
		return violations;
	}

}
