package com.mindbox.pe.model.assckey;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.mindbox.pe.model.DateSynonym;

import net.sf.oval.constraint.AssertValid;

/**
 * Abstract implementation of {@link MutableTimedAssociationKeySet} instances.
 * This is <b>not</b> thread-safe.
 * @author Geneho Kim
 * @since 5.1.0
 */
public abstract class AbstractMutableTimedAssociationKeySet implements MutableTimedAssociationKeySet, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3656090025287504162L;
	
	@AssertValid(requireValidElements = true)
	private final Set<MutableTimedAssociationKey> set = new HashSet<MutableTimedAssociationKey>();

	protected AbstractMutableTimedAssociationKeySet() {
	}
	
	protected AbstractMutableTimedAssociationKeySet(MutableTimedAssociationKeySet source) {
		// deep copy set
		for (Iterator<MutableTimedAssociationKey> iter = source.iterator(); iter.hasNext();) {
			MutableTimedAssociationKey element = iter.next();
			set.add(element.copy());
		}
	}

	@Override
	public boolean isInUse(DateSynonym dateSynonym) {
		if (dateSynonym == null) throw new NullPointerException("dateSynonym cannot be null");
		for (MutableTimedAssociationKey timedAssociationKey : set) {
			if (dateSynonym.equals(timedAssociationKey.getEffectiveDate()) || dateSynonym.equals(timedAssociationKey.getExpirationDate())) {
				return true;
			}
		}
		return false;
	}

	public void addAll(MutableTimedAssociationKeySet set) {
		for (Iterator<MutableTimedAssociationKey> iter = set.iterator(); iter.hasNext();) {
			MutableTimedAssociationKey element = iter.next();
			add(element);
		}
	}

	public final List<MutableTimedAssociationKey> getAll(int id) {
		List<MutableTimedAssociationKey> list = new ArrayList<MutableTimedAssociationKey>();
		for (MutableTimedAssociationKey element : set) {
			if (element.getAssociableID() == id) {
				list.add(element);
			}
		}
		return list;
	}

	public void removeAll(int id) {
		for (Iterator<MutableTimedAssociationKey> iter = set.iterator(); iter.hasNext();) {
			TimedAssociationKey element = iter.next();
			if (element.getAssociableID() == id) {
				iter.remove();
			}
		}
	}

	public final int size() {
		return set.size();
	}

	public boolean add(MutableTimedAssociationKey key) {
		return set.add(key);
	}

	public final void clear() {
		set.clear();
	}

	public boolean contains(MutableTimedAssociationKey key) {
		return set.contains(key);
	}
	
	public final boolean isEmpty() {
		return set.isEmpty();
	}

	public final Iterator<MutableTimedAssociationKey> iterator() {
		return set.iterator();
	}

	public boolean remove(MutableTimedAssociationKey key) {
		return set.remove(key);
	}
	
	public String toString() {
		StringBuilder buff = new StringBuilder();
		for (MutableTimedAssociationKey element : set) {
			buff.append(element.toString());
		}		
		return buff.toString();
	}
}
