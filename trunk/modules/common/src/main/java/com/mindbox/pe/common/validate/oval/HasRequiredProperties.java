package com.mindbox.pe.common.validate.oval;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import net.sf.oval.configuration.annotation.Constraint;

@Retention(RetentionPolicy.RUNTIME)
@Target( { ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER })
@Constraint(checkWith = HasRequiredPropertiesCheck.class)
/**
 */
public @interface HasRequiredProperties {

	/**
	 * Validate failure message
	 */
	String message() default "violated.HasRequiredProperties";
}
