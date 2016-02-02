package com.mindbox.pe.common.validate.oval;

import java.util.Iterator;

import net.sf.oval.Validator;
import net.sf.oval.configuration.annotation.AbstractAnnotationCheck;
import net.sf.oval.context.OValContext;
import net.sf.oval.exception.OValException;

import com.mindbox.pe.model.AbstractIDObject;
import com.mindbox.pe.model.assckey.AssociationKey;
import com.mindbox.pe.model.assckey.MutableTimedAssociationKey;
import com.mindbox.pe.model.assckey.MutableTimedAssociationKeySet;

public class NoSelfAssociationCheck extends AbstractAnnotationCheck<NoSelfAssociation> {

	private static final long serialVersionUID = -548549499608336316L;

	@Override
	public boolean isSatisfied(Object validatedObject, Object valueToValidate, OValContext arg2, Validator arg3) throws OValException {
		if (valueToValidate == null) return true;
		if (validatedObject instanceof AbstractIDObject) {
			if (valueToValidate instanceof AssociationKey) {
				return ((AssociationKey) valueToValidate).getAssociableID() != ((AbstractIDObject) validatedObject).getID();
			}
			else if (valueToValidate instanceof MutableTimedAssociationKeySet) {
				for (Iterator<MutableTimedAssociationKey> iter = ((MutableTimedAssociationKeySet) valueToValidate).iterator(); iter.hasNext();) {
					if (iter.next().getAssociableID() == ((AbstractIDObject) validatedObject).getID()) {
						return false;
					}
				}
				return true;
			}
		}
		return false;
	}

}
