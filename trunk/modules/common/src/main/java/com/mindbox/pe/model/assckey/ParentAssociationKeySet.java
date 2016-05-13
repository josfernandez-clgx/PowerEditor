package com.mindbox.pe.model.assckey;

import java.util.Date;

/**
 * A variation of {@link MutableTimedAssociationKeySet} that does not allow more than one associated id at any point in time.
 * This allows, however, gaps; i.e., there may be no associated id for a period of time.
 * @author Geneho Kim
 * @since 5.1.0
 */
public interface ParentAssociationKeySet extends MutableTimedAssociationKeySet {

	/**
	 * Tests if this has a parent as of the specified date.
	 * @param date the date
	 * @return <code>true</code> if this has a parent as of <code>date</code>; <code>false</code>, otherwise
	 * @throws NullPointerException if <code>date</code> is <code>null</code>
	 */
	boolean hasParent(Date date);

	/**
	 * Gets the parent id for the specified date.
	 * @param date the date
	 * @return parent id, if found; -1, otherwise
	 * @throws NullPointerException if <code>date</code> is <code>null</code>
	 */
	int getParent(Date date);

	/**
	 * Gets the parent association as of the specified date.
	 * Note: this returns an instanceof {@link TimedAssociationKey}, not {@link MutableTimedAssociationKey}.
	 * Otherwise you can modify content of this by mutating the returned instance.
	 * @param date
	 * @return the parent association if found; <code>null</code>, otherwise
	 * @throws NullPointerException if <code>date</code> is <code>null</code>
	 */
	TimedAssociationKey getParentAssociation(Date date);
}
