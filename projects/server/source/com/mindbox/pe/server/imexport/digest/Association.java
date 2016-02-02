package com.mindbox.pe.server.imexport.digest;

import net.sf.oval.constraint.AssertValid;
import net.sf.oval.constraint.NotNull;


/**
 * @author Geneho Kim
 * @author MindBox
 * @since PowerEditor 2.5.0
 */
public class Association {

	@NotNull
	@AssertValid
	private EntityIdentity entityLink = null;
	
	@AssertValid
	private ActivationDates activationDates = null;	// can be null
	
	public EntityIdentity getEntityLink() {
		return entityLink;
	}

	public void setEntityLink(EntityIdentity identity) {
		entityLink = identity;
	}

	public ActivationDates getActivationDates() {
		return activationDates;
	}

	public void setActivationDates(ActivationDates dates) {
		activationDates = dates;
	}

	public String toString() {
		return "Assc["+entityLink.toString()+",activationDates="+activationDates+"]";
	}
}
