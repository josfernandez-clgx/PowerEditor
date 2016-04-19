package com.mindbox.pe.model.assckey;

import com.mindbox.pe.model.DateSynonym;

/**
 * Concrete implementation of {@link TimedAssociationKey}.
 * @author Geneho Kim
 * @since 5.1.0
 */
public class DefaultTimedAssociationKey extends AbstractTimedAssociationKey {

	private static final long serialVersionUID = 8875247086494107540L;

	public DefaultTimedAssociationKey(int associableID, DateSynonym effDate, DateSynonym expDate) {
		super(associableID, effDate, expDate);
	}

	public DefaultTimedAssociationKey(TimedAssociationKey key) {
		this(key.getAssociableID(), key.getEffectiveDate(), key.getExpirationDate());
	}
}
