package com.mindbox.pe.model.assckey;

import java.util.Iterator;
import java.util.List;

import com.mindbox.pe.model.DateSynonym;

/**
 * A set of {@link TimedAssociationKey} instances.
 * This is <b>not</b> thread-safe.
 * @author Geneho Kim
 * @since 5.1.0
 */
public interface MutableTimedAssociationKeySet {

	/**
	 * Tests if the specified date synonym is used by at least one timed association.
	 * @param dateSynonym
	 * @return <code>true</code> if <code>dateSynonym</code> is used by at least one timed association; 
	 *         <code>false</code>, otherwise
	 * @throws NullPointerException if <code>dateSynonym</code> is <code>null</code>
	 */
	 boolean isInUse(DateSynonym dateSynonym);
	
	/**
	 * Adds the specified timed association key to this.
	 * @param key
	 * @return <code>true</code> if this set did not already contain <code>key</code>
	 * @throws InvalidAssociationKeyException if this does not accept <code>key</code>
	 * @throws NullPointerException if <code>key</code> is <code>null</code>
	 */
	boolean add(MutableTimedAssociationKey key);

	/**
	 * Removes the specified timed association key to this.
	 * @param key
	 * @return
	 * @throws NullPointerException if <code>key</code> is <code>null</code>
	 */
	boolean remove(MutableTimedAssociationKey key);
	
	/**
	 * Adds all timed association keys in the specified set to this.
	 * @param set the source set
	 * @throws InvalidAssociationKeyException if <code>set</code> contains an element that this does not accept
	 * @throws NullPointerException if <code>set</code> is <code>null</code>
	 */
	void addAll(MutableTimedAssociationKeySet set);

	/**
	 * Gets all timed association keys in this that references the specified id.
	 * Note that modifying the returns instances modifies instances in this. That is,
	 * this method returns instances themselves, not copies.
	 * @param id
	 * @return a list of {@link MutableTimedAssociationKey} instances that whose {@link AssociationKey#getAssociableID()} returns <code>id</code>, if found;
	 *         an empty list; otherwise
	 */
	List<MutableTimedAssociationKey> getAll(int id);
	
	/**
	 * Removes all timed association keys from this that references the specified id.
	 * This there is no such association keys in this, this is a no-op.
	 * @param id the id
	 */
	void removeAll(int id);

	void clear();

	boolean contains(MutableTimedAssociationKey key);
	
	boolean isEmpty();
	
	Iterator<MutableTimedAssociationKey> iterator();
	
	int size();
}
