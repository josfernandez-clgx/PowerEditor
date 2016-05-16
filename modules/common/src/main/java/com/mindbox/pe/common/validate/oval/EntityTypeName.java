package com.mindbox.pe.common.validate.oval;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import net.sf.oval.configuration.annotation.Constraint;

/**
 * Checks that the value is a valid entity type name.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER })
@Constraint(checkWith = EntityTypeNameCheck.class)
public @interface EntityTypeName {

	/**
	 * Validate failure message
	 * @return default failure message
	 */
	String message() default "violated.EntityTypeName";
}
