package com.mindbox.pe.server.validate.oval;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import net.sf.oval.configuration.annotation.Constraint;

@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.FIELD, ElementType.METHOD })
@Constraint(checkWith = HasValueForPropertyCheck.class)
/**
 */
public @interface HasValueForProperty {

	/**
	 * Validate failure message
	 * @return message
	 */
	String message() default "violated.HasValueForProperty";

	/**
	 * The name of the property to check for
	 * @return property
	 */
	String property();
}
