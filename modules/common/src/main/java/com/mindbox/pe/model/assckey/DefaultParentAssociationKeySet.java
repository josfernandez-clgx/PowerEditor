package com.mindbox.pe.model.assckey;

import java.util.Date;
import java.util.Iterator;

import com.mindbox.pe.model.Persistent;

/**
 * This is not thread-safe.
 * @author Geneho Kim
 * @since 5.1.0
 */
public class DefaultParentAssociationKeySet extends AbstractMutableTimedAssociationKeySet implements ParentAssociationKeySet {

	private static final long serialVersionUID = 2006121100002L;

	public DefaultParentAssociationKeySet() {
		super();
	}
	
	public DefaultParentAssociationKeySet(ParentAssociationKeySet source) {
		super(source);
	}

	/**
	 * @throws InvalidAssociationKeyException if there is an overlapping key
	 */
	public boolean add(MutableTimedAssociationKey key) {
		if (super.contains(key)) return false;
		checkForOverlap(key);
		return super.add(key);
	}

	private void checkForOverlap(MutableTimedAssociationKey key) {
		for (Iterator<MutableTimedAssociationKey> iter = super.iterator(); iter.hasNext();) {
			MutableTimedAssociationKey element = iter.next();
			if (element.overlapsWith(key)) {
				throw new InvalidAssociationKeyException("cannot have more than one parent at any point in time");
			}
		}
		if (!key.hasExpirationDate()) {
			MutableTimedAssociationKey current = getCurrent();
			if (current != null && !current.hasExpirationDate()) {
				throw new InvalidAssociationKeyException("cannot have more than one parent with no expiration date");
			}
		}
	}

	private MutableTimedAssociationKey getCurrent() {
		MutableTimedAssociationKey current = null;
		for (Iterator<MutableTimedAssociationKey> iter = super.iterator(); iter.hasNext();) {
			MutableTimedAssociationKey element = iter.next();
			if (!element.hasExpirationDate()) return element;
			if (current == null) {
				current = element;
			}
			else if (element.hasEffectiveDate() && element.getEffectiveDate().notBefore(current.getExpirationDate())) {
				current = element;
			}
		}
		assert isEmpty() || current != null; // there must be exactly one current unless this is empty
		return current;
	}

	public int getParent(Date date) {
		if (date == null) throw new NullPointerException("date cannot be null");
		for (Iterator<MutableTimedAssociationKey> iter = super.iterator(); iter.hasNext();) {
			MutableTimedAssociationKey element = iter.next();
			if (element.isEffectiveAt(date)) {
				return element.getAssociableID();
			}
		}
		return Persistent.UNASSIGNED_ID;
	}

	public TimedAssociationKey getParentAssociation(Date date) {
		if (date == null) throw new NullPointerException("date cannot be null");
		for (Iterator<MutableTimedAssociationKey> iter = super.iterator(); iter.hasNext();) {
			MutableTimedAssociationKey element = iter.next();
			if (element.isEffectiveAt(date)) {
				return new DefaultTimedAssociationKey(element);
			}
		}
		return null;
	}

	public boolean hasParent(Date date) {
		if (date == null) throw new NullPointerException("date cannot be null");
		for (Iterator<MutableTimedAssociationKey> iter = super.iterator(); iter.hasNext();) {
			MutableTimedAssociationKey element = iter.next();
			if (element.isEffectiveAt(date)) {
				return true;
			}
		}
		return false;
	}

	public String toString() {
		return "ParentAssocations:" + super.toString();
	}
}
