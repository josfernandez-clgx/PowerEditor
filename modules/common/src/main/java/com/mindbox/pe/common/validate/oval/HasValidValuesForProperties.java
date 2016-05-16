package com.mindbox.pe.common.validate.oval;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import net.sf.oval.configuration.annotation.Constraint;

@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.FIELD, ElementType.METHOD })
@Constraint(checkWith = HasValidValuesForPropertiesCheck.class)
/**
 */
public @interface HasValidValuesForProperties {

	/**
	 * Validate failure message
	 * @return default failure message
	 */
	String message() default "violated.HasValidValuesForProperties";
}
