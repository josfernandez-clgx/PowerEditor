package com.mindbox.pe.model.assckey;

import com.mindbox.pe.model.DateSynonym;

/**
 * A concrete implementation of {@link MutableTimedAssociationKey}.
 * @author Geneho Kim
 * @since 5.1.0
 */
public class DefaultMutableTimedAssociationKey extends AbstractMutableTimedAssociationKey {

	private static final long serialVersionUID = -2013458238171574793L;

	public DefaultMutableTimedAssociationKey(int associableID) {
		super(associableID);
	}

	public DefaultMutableTimedAssociationKey(int associableID, DateSynonym effDate, DateSynonym expDate) {
		super(associableID, effDate, expDate);
	}

	private DefaultMutableTimedAssociationKey(DefaultMutableTimedAssociationKey source) {
		super(source);
	}

	public MutableTimedAssociationKey copy() {
		return new DefaultMutableTimedAssociationKey(this);
	}

}
