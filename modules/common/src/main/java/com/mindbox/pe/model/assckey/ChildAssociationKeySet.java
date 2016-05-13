package com.mindbox.pe.model.assckey;

import java.util.Date;
import java.util.List;

/**
 * A variation of {@link MutableTimedAssociationKeySet} that does not allow overlapping ids.
 * That is, for a given child id, no overlapping associations are allowed.
 * This allows, however, gaps; i.e., there may be no associated id for a period of time.
 * @author Geneho Kim
 * @since 5.1.0
 */
public interface ChildAssociationKeySet extends MutableTimedAssociationKeySet {

	/**
	 * Gets a list of timed association keys for the specified child id.
	 * This returns an empty list of no association for the specified id is found.
	 * @param childID child id
	 * @return list of {@link MutableTimedAssociationKey} instances, if found; never <code>null</code>
	 */
	List<MutableTimedAssociationKey> getAssociationsForChild(int childID);

	/**
	 * Gets a list of ids of children as of the specified date.
	 * @param date the date
	 * @return list of Integer
	 * @throws NullPointerException if <code>date</code> is <code>null</code>
	 */
	List<Integer> getChildrendAsOf(Date date);

	/**
	 * Tests if this has any child as of the specified date.
	 * @param date the date to check
	 * @return <code>true</code> if this has any child at <code>date</code>; <code>false</code>, otherwise
	 * @throws NullPointerException if <code>date</code> is <code>null</code>
	 */
	boolean hasAnyChildAsOf(Date date);
}
