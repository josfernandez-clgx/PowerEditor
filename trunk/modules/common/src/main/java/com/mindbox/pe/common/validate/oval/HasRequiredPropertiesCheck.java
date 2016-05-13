package com.mindbox.pe.common.validate.oval;


/**
 * OVal contraint check for {@link HasRequiredProperties} annotation.
 * This is thread safe.
 * @author kim
 *
 */
public class HasRequiredPropertiesCheck extends AbstractServerConstraintCheck<HasRequiredProperties> {

	private static final long serialVersionUID = -548549499608336316L;

	public HasRequiredPropertiesCheck() {
		super("com.mindbox.pe.server.validate.oval.HasRequiredPropertiesConstraintsCheck");
	}
}
