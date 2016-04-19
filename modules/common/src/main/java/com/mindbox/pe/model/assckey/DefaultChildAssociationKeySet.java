package com.mindbox.pe.model.assckey;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

/**
 * Concrete implementation of {@link ChildAssociationKeySet}. 
 * @author Geneho Kim
 * @see MutableTimedAssociationKeySet
 */
public class DefaultChildAssociationKeySet extends AbstractMutableTimedAssociationKeySet implements ChildAssociationKeySet {

	private static final long serialVersionUID = 2006121100001L;

	public DefaultChildAssociationKeySet() {
		super();
	}

	public DefaultChildAssociationKeySet(ChildAssociationKeySet source) {
		super(source);
	}

	/**
	 * 
	 * @throws InvalidAssociationKeyException if there is at least one overlapping key in this
	 */
	public boolean add(MutableTimedAssociationKey key) {
		if (key == null) throw new NullPointerException("key cannot be null");
		if (super.contains(key)) return false;
		checkForOverlap(key);
		return super.add(key);
	}

	private void checkForOverlap(MutableTimedAssociationKey key) {
		for (Iterator<MutableTimedAssociationKey> iter = super.iterator(); iter.hasNext();) {
			MutableTimedAssociationKey element = iter.next();
			if (element.getAssociableID() == key.getAssociableID() && element.overlapsWith(key)) {
				throw new InvalidAssociationKeyException("cannot have overlapping children");
			}
		}
	}

	public List<Integer> getChildrendAsOf(Date date) {
		if (date == null) throw new NullPointerException("date cannot be null");
		List<Integer> list = new ArrayList<Integer>();
		for (Iterator<MutableTimedAssociationKey> iter = super.iterator(); iter.hasNext();) {
			MutableTimedAssociationKey element = iter.next();
			if (element.isEffectiveAt(date)) {
				list.add(element.getAssociableID());
			}
		}
		return list;
	}

	public boolean hasAnyChildAsOf(Date date) {
		if (date == null) throw new NullPointerException("date cannot be null");
		for (Iterator<MutableTimedAssociationKey> iter = super.iterator(); iter.hasNext();) {
			MutableTimedAssociationKey element = iter.next();
			if (element.isEffectiveAt(date)) {
				return true;
			}
		}
		return false;
	}

	public List<MutableTimedAssociationKey> getAssociationsForChild(int childID) {
		List<MutableTimedAssociationKey> list = new ArrayList<MutableTimedAssociationKey>();
		for (Iterator<MutableTimedAssociationKey> iter = super.iterator(); iter.hasNext();) {
			MutableTimedAssociationKey element = iter.next();
			if (element.getAssociableID() == childID) {
				list.add(element);
			}
		}
		return list;
	}

}
