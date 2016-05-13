package com.mindbox.pe.common.validate;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;


public class DefaultValidationViolation implements ValidationViolation, Serializable {

	private static final long serialVersionUID = 2007081612934800L;

	private Object validatedObject;
	private String message;
	private Object invalidValue;
	private Collection<ValidationViolation> causes;

	/**
	 * Equivalent to <code>DefaultValidationViolation(validatedObject, message, invalidValue, null)</code>.
	 * @param validatedObject validatedObject
	 * @param message message
	 * @param invalidValue invalidValue
	 */
	public DefaultValidationViolation(Object validatedObject, String message, Object invalidValue) {
		this(validatedObject, message, invalidValue, null);
	}

	public DefaultValidationViolation(Object validatedObject, String message, Object invalidValue, Collection<ValidationViolation> causes) {
		this.validatedObject = validatedObject;
		this.message = message;
		this.invalidValue = invalidValue;
		this.causes = (causes == null ? null : Collections.unmodifiableCollection(causes));
	}

	@Override
	public final Collection<ValidationViolation> getCauses() {
		return causes;
	}

	@Override
	public final Object getInvalidValue() {
		return invalidValue;
	}

	@Override
	public final String getMessage() {
		return message;
	}

	@Override
	public final Object getValidatedObject() {
		return validatedObject;
	}

	@Override
	public String toErrorString() {
		StringBuilder buff = new StringBuilder();
		buff.append(getMessage());
		if (getCauses() != null && !getCauses().isEmpty()) {
			buff.append(". Cause: ");
			for (ValidationViolation cause : getCauses()) {
				if (cause != null) {
					buff.append(cause.toErrorString());
					buff.append("; ");
				}
			}
		}
		return buff.toString();
	}

	@Override
	public String toString() {
		return "ValidationViolation[\"" + message + "\",value=" + invalidValue + "]";
	}
}
