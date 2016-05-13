package com.mindbox.pe.common.validate.oval;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import net.sf.oval.configuration.annotation.Constraint;

/**
 * Checks that generic category has no parent if it is a root category.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER })
@Constraint(checkWith = EmptyIfRootCheck.class)
public @interface EmptyIfRoot {

	/**
	 * Default validation failure message.
	 * @return default failure message
	 */
	String message() default "violated.EmptyIfRoot";
}
