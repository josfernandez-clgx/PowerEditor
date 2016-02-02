package com.mindbox.pe.model.assckey;

import com.mindbox.pe.model.DateSynonym;

/**
 * Mutable concrete implementation of {@link TimedAssociationKey}.
 * @author Geneho Kim
 * @since 5.1.0
 */
public abstract class AbstractMutableTimedAssociationKey extends AbstractTimedAssociationKey implements MutableTimedAssociationKey {

	private static final long serialVersionUID = 20061206100001L;

	/**
	 * Equivalent to <code>new AbstractMutableTimedAssociationKey(associableID, null, null)</code>
	 * @param associableID
	 */
	public AbstractMutableTimedAssociationKey(int associableID) {
		this(associableID, null, null);
	}
	
	public AbstractMutableTimedAssociationKey(int associableID, DateSynonym effDate, DateSynonym expDate) {
		super(associableID, effDate, expDate);
	}

	protected AbstractMutableTimedAssociationKey(AbstractMutableTimedAssociationKey source) {
		super(source);
	}
	
	public final void setAssociableID(int id) {
		super.setAssociableID(id);
	}

    public final void setEffectiveDate(DateSynonym effectiveDate) {
        super.setEffectiveDate(effectiveDate);
    }
    
    public final void setExpirationDate(DateSynonym expirationDate) {
        super.setExpirationDate(expirationDate);
    }

	public boolean hasEffectiveDate() {
		return super.getEffectiveDate() != null;
	}

	public boolean hasExpirationDate() {
		return super.getExpirationDate() != null;
	}
}
