package com.mindbox.pe.model.assckey;

import java.util.Date;

import com.mindbox.pe.model.DateSynonym;

/**
 * {@link AssociationKey} with effective and expiration dates.
 * @author Gene Kim
 * @author MindBox, Inc
 * @since PowerEditor 1.0
 */
public interface TimedAssociationKey extends AssociationKey {

	DateSynonym getEffectiveDate();

	DateSynonym getExpirationDate();
	
	/**
	 * Tests if this association is active on the specified date/time.
	 * If the date is the same of the expiration date, this returns <code>false</code>.
	 * @param date
	 * @return <code>true</code> if is active on <code>date</code>; <code>false</code>, otherwise
	 * @throws NullPointerException if <code>date</code> is <code>null</code>
	 * @since 5.1.0
	 */
	boolean isEffectiveAt(Date date);
	
	/**
	 * Tests if this overlaps with the specified key, except on the same date.
	 * That is, if key's effective date is the same as the expiration date of this,
	 * this returns <code>false</code>.
	 * @param key
	 * @return <code>true</code> if this overlaps with <code>key</code> or the same as this; <code>false</code>, otherwise
	 * @throws NullPointerException if <code>date</code> is <code>null</code>
	 * @since 5.1.0
	 */
	boolean overlapsWith(TimedAssociationKey key);
}
