package com.mindbox.pe.server.validate.oval;

import java.util.HashMap;
import java.util.Map;

import net.sf.oval.Validator;
import net.sf.oval.configuration.annotation.AbstractAnnotationCheck;
import net.sf.oval.context.OValContext;
import net.sf.oval.exception.OValException;

import com.mindbox.pe.server.imexport.digest.EntityIdentityParentIDProperties;


/**
 * OVal contraint check for {@link HasValueForProperty} annotation.
 * This is thread safe.
 */
public class HasValueForPropertyCheck extends AbstractAnnotationCheck<HasValueForProperty> {

	private static final long serialVersionUID = -548549499608336314L;

	private String propertyName;

	/**
	 * @param valueToValidate must be {@link EntityIdentityParentIDProperties}; if not, this returns <code>true</code>
	 * @returns <code>true</code>, if valudateToValidate is <code>null</code>; otherwise, true only if valueToValidate is valid
	 */
	@Override
	public boolean isSatisfied(Object validatedObject, Object valueToValidate, OValContext context, Validator validator)
			throws OValException {
		if (valueToValidate == null) return true;
		if (validatedObject instanceof EntityIdentityParentIDProperties) {
			EntityIdentityParentIDProperties entityProperties = (EntityIdentityParentIDProperties) validatedObject;
			return entityProperties.getProperty(propertyName, null) != null;
		}
		return true;
	}

	@Override
	public void configure(HasValueForProperty constraintAnnotation) {
		super.configure(constraintAnnotation);
		propertyName = constraintAnnotation.property();
	}

	@Override
	public Map<String, String> getMessageVariables() {
		Map<String, String> messageVariables = new HashMap<String, String>();
		messageVariables.put("property", propertyName);
		return messageVariables;
	}
}
