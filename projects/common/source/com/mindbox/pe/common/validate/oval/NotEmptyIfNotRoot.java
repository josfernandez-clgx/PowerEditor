package com.mindbox.pe.common.validate.oval;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import net.sf.oval.configuration.annotation.Constraint;

/**
 * Checks that generic category has a parent if not root.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target( { ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER })
@Constraint(checkWith = NotEmptyIfNotRootCheck.class)
public @interface NotEmptyIfNotRoot {

	/**
	 * Default validation failure message.
	 */
	String message() default "violated.NotEmptyIfNotRoot";
}
