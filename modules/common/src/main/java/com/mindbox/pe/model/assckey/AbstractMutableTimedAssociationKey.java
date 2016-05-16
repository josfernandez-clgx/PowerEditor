package com.mindbox.pe.model.assckey;

import com.mindbox.pe.model.DateSynonym;

/**
 * Mutable concrete implementation of {@link TimedAssociationKey}.
 * @author Geneho Kim
 * @since 5.1.0
 */
public abstract class AbstractMutableTimedAssociationKey extends AbstractTimedAssociationKey implements MutableTimedAssociationKey {

	private static final long serialVersionUID = 20061206100001L;

	protected AbstractMutableTimedAssociationKey(AbstractMutableTimedAssociationKey source) {
		super(source);
	}

	/**
	 * Equivalent to <code>new AbstractMutableTimedAssociationKey(associableID, null, null)</code>
	 * @param associableID associableID
	 */
	public AbstractMutableTimedAssociationKey(int associableID) {
		this(associableID, null, null);
	}

	public AbstractMutableTimedAssociationKey(int associableID, DateSynonym effDate, DateSynonym expDate) {
		super(associableID, effDate, expDate);
	}

	@Override
	public boolean hasEffectiveDate() {
		return super.getEffectiveDate() != null;
	}

	@Override
	public boolean hasExpirationDate() {
		return super.getExpirationDate() != null;
	}

	@Override
	public final void setAssociableID(int id) {
		super.setAssociableID(id);
	}

	@Override
	public final void setEffectiveDate(DateSynonym effectiveDate) {
		super.setEffectiveDate(effectiveDate);
	}

	@Override
	public final void setExpirationDate(DateSynonym expirationDate) {
		super.setExpirationDate(expirationDate);
	}
}
