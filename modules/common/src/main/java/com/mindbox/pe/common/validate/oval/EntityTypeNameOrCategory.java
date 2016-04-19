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
@Target( { ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER })
@Constraint(checkWith = EntityTypeNameOrCategoryCheck.class)
public @interface EntityTypeNameOrCategory {

	/**
	 * Validate failure message
	 */
	String message() default "violated.EntityTypeNameOrCategory";
}
