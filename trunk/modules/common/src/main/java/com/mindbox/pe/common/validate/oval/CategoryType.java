package com.mindbox.pe.common.validate.oval;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import net.sf.oval.configuration.annotation.Constraint;

/**
 * Checks that the value is a valid category type id.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target( { ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER })
@Constraint(checkWith = CategoryTypeCheck.class)
public @interface CategoryType {

	/**
	 * Default validation failure message.
	 */
	String message() default "violated.CategoryType";
}
