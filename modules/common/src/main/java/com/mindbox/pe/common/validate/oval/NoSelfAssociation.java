package com.mindbox.pe.common.validate.oval;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import net.sf.oval.configuration.annotation.Constraint;

@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER })
@Constraint(checkWith = NoSelfAssociationCheck.class)
/**
 * Checks the value does not have a reference to the id of the object being validated.
 * The object must be an instance of {@link AbstractIDNameObject}.
 * The value must be an instance of {@link AssociationKey} or {@link MutableTimedAssociationKeySet}.
 */
public @interface NoSelfAssociation {

	/**
	 * Validate failure message
	 * @return default failure message
	 */
	String message() default "violated.NoSelfAssocation";
}
