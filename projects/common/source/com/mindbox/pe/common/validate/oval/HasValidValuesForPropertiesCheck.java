package com.mindbox.pe.common.validate.oval;


/**
 * OVal contraint check for {@link HasValidValuesForProperties} annotation.
 * This is thread safe.
 * @author kim
 *
 */
public class HasValidValuesForPropertiesCheck extends AbstractServerConstraintCheck<HasValidValuesForProperties> {

	private static final long serialVersionUID = -548549499608336317L;

	public HasValidValuesForPropertiesCheck() {
		super("com.mindbox.pe.server.validate.oval.HasValidValuesForPropertiesConstraintCheck");
	}
}
