package com.mindbox.pe.model.assckey;

import com.mindbox.pe.model.DateSynonym;

/**
 * A mutable variation of {@link TimedAssociationKey}.
 * @author Geneho Kim
 * @since 5.1.0
 */
public interface MutableTimedAssociationKey extends TimedAssociationKey, MutableAssociationKey {

	boolean hasEffectiveDate();

	boolean hasExpirationDate();

	void setEffectiveDate(DateSynonym effectiveDate);

	void setExpirationDate(DateSynonym expirationDate);
	
	MutableTimedAssociationKey copy();

    // TT 2029 
	void updateEffExpDates(DateSynonym ds);
	
}
