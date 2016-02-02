package com.mindbox.pe.common.validate.oval;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import net.sf.oval.configuration.annotation.Constraint;

/**
 * Checks that the object has a valid date range.
 */
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target( { ElementType.FIELD, ElementType.METHOD })
@Constraint(checkWith = EffectiveDateBeforeExpirationDateCheck.class)
public @interface EffectiveDateBeforeExpirationDate {

	/**
	 * Default validation failure message.
	 */
	String message() default "violated.EffectiveDateBeforeExpirationDate";
}
