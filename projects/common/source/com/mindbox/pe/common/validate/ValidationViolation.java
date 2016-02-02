package com.mindbox.pe.common.validate;

import java.util.Collection;

public interface ValidationViolation {

	Object getInvalidValue();

	String getMessage();

	Object getValidatedObject();

	Collection<ValidationViolation> getCauses();
	
	String toErrorString();
}
