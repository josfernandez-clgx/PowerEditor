package com.mindbox.pe.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import com.mindbox.pe.common.validate.oval.CategoryType;
import com.mindbox.pe.common.validate.oval.EmptyIfRoot;
import com.mindbox.pe.common.validate.oval.NoSelfAssociation;
import com.mindbox.pe.common.validate.oval.NotEmptyIfNotRoot;
import com.mindbox.pe.common.validate.oval.PositiveOrUnassigned;
import com.mindbox.pe.common.validate.oval.UniqueName;
import com.mindbox.pe.model.assckey.ChildAssociationKeySet;
import com.mindbox.pe.model.assckey.DefaultChildAssociationKeySet;
import com.mindbox.pe.model.assckey.DefaultParentAssociationKeySet;
import com.mindbox.pe.model.assckey.InvalidAssociationKeyException;
import com.mindbox.pe.model.assckey.MutableTimedAssociationKey;
import com.mindbox.pe.model.assckey.ParentAssociationKeySet;
import com.mindbox.pe.model.assckey.TimedAssociationKey;

import net.sf.oval.constraint.AssertValid;
import net.sf.oval.constraint.Min;
import net.sf.oval.constraint.NotBlank;
import net.sf.oval.constraint.NotNull;


/**
 * Generic category. 
 * 
 * @author Geneho Kim
 * @author MindBox
 * @since PowerEditor 3.1.0
 */
public final class GenericCategory extends AbstractIDObject implements GenericContextElement, IDNameObject, Auditable {

	private static final long serialVersionUID = 2007051500003L;

	@PositiveOrUnassigned
	private int parentID = -1;

	@Min(value = 1)
	@CategoryType
	private final int type;

	@AssertValid
	@NoSelfAssociation
	private final ChildAssociationKeySet childCategories;

	@AssertValid
	@NoSelfAssociation
	@NotEmptyIfNotRoot
	@EmptyIfRoot
	private final ParentAssociationKeySet parentCategories;

	@NotNull
	@NotBlank
	@UniqueName
	private String name;

	private int sortOrderIndex;
	private boolean rootIndicator = false;

	private GenericCategory(GenericCategory source) {
		this(source.getID(), source.getName(), source.type, new DefaultChildAssociationKeySet(source.childCategories), new DefaultParentAssociationKeySet(source.parentCategories));
	}

	/**
	 * Creates a new generic category with the specified parameters.
	 * @param id id
	 * @param name name
	 * @param type type
	 */
	public GenericCategory(int id, String name, int type) {
		this(id, name, type, new DefaultChildAssociationKeySet(), new DefaultParentAssociationKeySet());
	}

	private GenericCategory(int id, String name, int type, ChildAssociationKeySet childAssociationKeySet, ParentAssociationKeySet parentAssociationKeySet) {
		super(id);
		this.name = name;
		this.type = type;
		this.childCategories = childAssociationKeySet;
		this.parentCategories = parentAssociationKeySet;
	}

	/**
	 * Adds the specified child relationship to this.
	 * @param childKey child association to add
	 * @throws InvalidAssociationKeyException if <code>childKey</code> is invalid
	 * @throws NullPointerException if <code>childKey</code> is <code>null</code>
	 * @since 5.1.0
	 */
	public void addChildAssociation(MutableTimedAssociationKey childKey) {
		childCategories.add(childKey);
	}

	/**
	 * Adds the specified parent relationship to this.
	 * @param parentKey parent relationship to add
	 * @throws InvalidAssociationKeyException if <code>parentKey</code> is invalid
	 * @throws NullPointerException if <code>parentKey</code> is <code>null</code>
	 * @since 5.1.0
	 */
	public void addParentKey(MutableTimedAssociationKey parentKey) {
		parentCategories.add(parentKey);
	}

	@Override
	public Auditable deepCopy() {
		return new GenericCategory(this);
	}

	/**
	 * Returns a list of all parent associations.
	 * @return List of {@link MutableTimedAssociationKey} instances
	 * @since 5.1.0
	 */
	public List<MutableTimedAssociationKey> getAllParentAssociations() {
		List<MutableTimedAssociationKey> list = new ArrayList<MutableTimedAssociationKey>();
		for (Iterator<MutableTimedAssociationKey> i = parentCategories.iterator(); i.hasNext();) {
			list.add(i.next());
		}
		return list;
	}

	@Override
	public String getAuditDescription() {
		return GenericEntityType.forCategoryType(type).getDisplayName() + " category '" + getName() + "'";
	}

	@Override
	public String getAuditName() {
		return getName();
	}

	/**
	 * Gets all child relationships for the specified child id. 
	 * @param childID the child id
	 * @return list of {@link MutableTimedAssociationKey} instances, if found; an empty list, otherwise
	 * @since 5.1.0
	 */
	public List<MutableTimedAssociationKey> getChildAssociations(int childID) {
		return childCategories.getAssociationsForChild(childID);
	}

	/**
	 * Gets a list of child ids as of the specified date.
	 * @param date the date
	 * @return list of Integer, if found; an empty list, otherwise
	 * @throws NullPointerException if <code>date</code> is <code>null</code>
	 * @since 5.1.0
	 */
	public List<Integer> getChildIDs(Date date) {
		return childCategories.getChildrendAsOf(date);
	}

	/**
	 * Gets the iterator for all child associations.
	 * @return child association iterator
	 * @since 5.1.0
	 */
	public Iterator<MutableTimedAssociationKey> getChildrenKeyIterator() {
		return childCategories.iterator();
	}

	@Override
	public String getName() {
		return name;
	}

	/**
	 * Gets the parent assocation as of date
	 * @param date date
	 * @return the parent association if found; <code>null</code>, otherwise
	 * @throws NullPointerException if <code>date</code> is <code>null</code>
	 * @since 5.1.0
	 */
	public TimedAssociationKey getParentAssociation(Date date) {
		return parentCategories.getParentAssociation(date);
	}

	/**
	 * Gets the id of the parent as of date
	 * @param date date
	 * @return the id of the parent if found; -1, otherwise
	 * @throws NullPointerException if <code>date</code> is <code>null</code>
	 * @since 5.1.0
	 */
	public int getParentID(Date date) {
		return parentCategories.getParent(date);
	}

	/**
	 * Gets an iterator of all parent relationships
	 * @return iterator of {@link MutableTimedAssociationKey} instances
	 * @since 5.1.0
	 */
	public Iterator<MutableTimedAssociationKey> getParentKeyIterator() {
		return parentCategories.iterator();
	}

	public int getSortOrderIndex() {
		return sortOrderIndex;
	}

	/**
	 * @return generic entity type of this
	 */
	public int getType() {
		return type;
	}

	/**
	 * Tests if this category has no child categories on any date.
	 * @return <code>true</code> if this has no child regardless of <code>date</code>; <code>false</code>, otherwise
	 * @since 5.1.0
	 */
	public boolean hasNoChild() {
		return childCategories.isEmpty();
	}

	/**
	 * Tests if this has no child as of the specified date.
	 * @param date the date to check
	 * @return <code>true</code> if this has no child at <code>date</code>; <code>false</code>, otherwise
	 * @throws NullPointerException if <code>date</code> is <code>null</code>
	 * @since 5.1.0
	 */
	public boolean hasNoChild(Date date) {
		return !childCategories.hasAnyChildAsOf(date);
	}

	/**
	 * Tests if this has a parent as of the specified date.
	 * @param date the date to check
	 * @return <code>true</code> if this has a parent as of <code>date</code>; <code>false</code>, otherwise
	 * @throws NullPointerException if <code>date</code> is <code>null</code>
	 * @since 5.1.0
	 */
	public boolean hasParent(Date date) {
		return parentCategories.hasParent(date);
	}

	/**
	 * Tests if this has the same parent associations as the specified category.
	 * @param category the category to check against
	 * @return true if same parent associations; false, otherwise
	 * @throws NullPointerException if <code>category</code> is <code>null</code>
	 * @since 5.1.0
	 */
	public boolean hasSameParentAssociations(GenericCategory category) {
		if (this.parentCategories.size() == category.parentCategories.size()) {
			for (Iterator<MutableTimedAssociationKey> iter = this.parentCategories.iterator(); iter.hasNext();) {
				MutableTimedAssociationKey key = iter.next();
				if (!category.parentCategories.contains(key)) {
					return false;
				}
			}
			return true;
		}
		else {
			return false;
		}
	}

	/**
	 * Tests if the specified date synonym is used by at least one child association.
	 * @param dateSynonym dateSynonym
	 * @return <code>true</code> if <code>dateSynonym</code> is used by at least one child association; 
	 *         <code>false</code>, otherwise
	 * @throws NullPointerException if <code>dateSynonym</code> is <code>null</code>
	 */
	public boolean isInUseForChildAssociation(DateSynonym dateSynonym) {
		return childCategories.isInUse(dateSynonym);
	}

	/**
	 * Tests if this has no parent at all.
	 * @return <code>true</code> if this has no parents at all; <code>false</code>, otherwise
	 * @since 5.1.0
	 */
	public boolean isRoot() {
		return parentCategories.isEmpty();
	}

	public boolean isRootIndicator() {
		return rootIndicator;
	}

	/**
	 * Removes all relationships to the specified child.
	 * If no such relationships are found, this is a no-op.
	 * @param childID the id of the child to remove
	 * @since 5.1.0
	 */
	public void removeAllChildAssociations(int childID) {
		childCategories.removeAll(childID);
	}

	/**
	 * Removes all relationships parent relationships.
	 * @since 5.1.0
	 */
	public void removeAllParentAssociations() {
		parentCategories.clear();
	}

	/**
	 * Removes the specified child relationship from this.
	 * @param childKey the child relationship to remove
	 * @throws NullPointerException if <code>childKey</code> is <code>null</code>
	 * @since 5.1.0
	 */
	public void removeChildAssociation(MutableTimedAssociationKey childKey) {
		childCategories.remove(childKey);
	}

	/**
	 * Removes the specified parent relationship from this.
	 * @param parentKey parent relationship to remove
	 * @throws NullPointerException if <code>parentKey</code> is <code>null</code>
	 * @since 5.1.0
	 */
	public void removeParentKey(MutableTimedAssociationKey parentKey) {
		parentCategories.remove(parentKey);
	}

	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Makes sure this has the same parent associations as the specified source category.
	 * @param source the source category to copy parent associations from
	 * @throws NullPointerException if <code>category</code> is <code>null</code>
	 * @since 5.1.0
	 */
	public void setParentAssociations(GenericCategory source) {
		this.parentCategories.clear();
		this.parentCategories.addAll(source.parentCategories);
	}

	public void setRootIndicator(boolean rootIndicator) {
		this.rootIndicator = rootIndicator;
	}

	public void setSortOrderIndex(int sortOrderIndex) {
		this.sortOrderIndex = sortOrderIndex;
	}

	@Override
	public String toString() {
		return "GenericCategory" + "[name=" + name + ",type=" + type + ",parent=" + parentID + "]" + super.toString();
	}

	// TT 2029 update association date
	public void updateAssociationDates(DateSynonym ds) {
		synchronized (parentCategories) {
			for (Iterator<MutableTimedAssociationKey> i = parentCategories.iterator(); i.hasNext();) {
				MutableTimedAssociationKey key = i.next();
				key.updateEffExpDates(ds);
			}
		}

		synchronized (childCategories) {
			for (Iterator<MutableTimedAssociationKey> i = childCategories.iterator(); i.hasNext();) {
				MutableTimedAssociationKey key = i.next();
				key.updateEffExpDates(ds);
			}
		}
	}
}