package com.mindbox.pe.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.mindbox.pe.common.UtilBase;
import com.mindbox.pe.common.validate.oval.HasRequiredProperties;
import com.mindbox.pe.common.validate.oval.HasValidValuesForProperties;
import com.mindbox.pe.common.validate.oval.PositiveOrUnassigned;
import com.mindbox.pe.common.validate.oval.UniqueName;
import com.mindbox.pe.model.assckey.ChildAssociationKeySet;
import com.mindbox.pe.model.assckey.DefaultChildAssociationKeySet;
import com.mindbox.pe.model.assckey.InvalidAssociationKeyException;
import com.mindbox.pe.model.assckey.MutableTimedAssociationKey;

import net.sf.oval.constraint.AssertValid;
import net.sf.oval.constraint.NotBlank;
import net.sf.oval.constraint.NotNull;

/**
 * Generic entity. Note that type-specific property getter methods cast values
 * into appropriate objects directly. Use corresponding type-specific setter
 * methods to set values for these properties.
 * 
 * @author Geneho Kim
 * @author MindBox
 * @since PowerEditor 3.0.0
 */
public final class GenericEntity extends AbstractIDObject implements GenericContextElement, CloneableEntity, IDNameObject, Auditable {

	private static final String MULTI_ENUM_DELIMITER = "|";
	private static final String MULTI_ENUM_DELIMITER_REGEX = "\\|";
	private static final String MULTI_ENUM_DELIMITER_ESCAPE = "&vb;";

	private static final long serialVersionUID = 2007051500004L;

	public static String toMultiEnumPropertyValue(List<String> values) {
		if (values == null || values.isEmpty()) return "";
		StringBuilder buff = new StringBuilder();
		for (int i = 0; i < values.size(); i++) {
			if (i > 0) buff.append(MULTI_ENUM_DELIMITER);
			buff.append(values.get(i).replaceAll(MULTI_ENUM_DELIMITER_REGEX, MULTI_ENUM_DELIMITER_ESCAPE));
		}
		return buff.toString();
	}

	public static List<String> toMultiEnumValues(String multiEnumPropertyValueStr) {
		if (UtilBase.isEmpty(multiEnumPropertyValueStr)) return new ArrayList<String>();
		String[] strs = multiEnumPropertyValueStr.split(MULTI_ENUM_DELIMITER_REGEX);
		for (int i = 0; i < strs.length; i++) {
			strs[i] = strs[i].replaceAll(MULTI_ENUM_DELIMITER_ESCAPE, MULTI_ENUM_DELIMITER);
		}
		return Arrays.asList(strs);
	}

	@PositiveOrUnassigned
	private int parentID = -1;

	@NotNull
	private final GenericEntityType type;

	@HasRequiredProperties
	@HasValidValuesForProperties
	private final Map<String, Object> propertyMap;

	@AssertValid
	private final ChildAssociationKeySet categoryAssociations;

	private boolean forClone = false;
	private boolean copyPolicies = false;

	@NotNull
	@NotBlank
	@UniqueName
	private String name;

	/**
	 * Make a deep copy of this.
	 * @param source the source
	 */
	public GenericEntity(GenericEntity source) {
		this(source.getID(), source.getType(), source.getName(), new DefaultChildAssociationKeySet(source.categoryAssociations));
		this.forClone = source.forClone;
		this.copyPolicies = source.copyPolicies;
		// Note: property values themselves do not need to be deep copied
		this.propertyMap.putAll(source.propertyMap);
	}

	/**
	 * 
	 * @param id id
	 * @param type type
	 * @param name name
	 */
	public GenericEntity(int id, GenericEntityType type, String name) {
		this(id, type, name, new DefaultChildAssociationKeySet());
	}

	private GenericEntity(int id, GenericEntityType type, String name, ChildAssociationKeySet childAssociationKeySet) {
		super(id);
		this.name = name;
		this.type = type;
		this.propertyMap = new HashMap<String, Object>();
		this.categoryAssociations = childAssociationKeySet;
	}

	/**
	 * Adds the specified category relationship to this.
	 * @param categoryKey category association to add
	 * @throws InvalidAssociationKeyException if <code>childKey</code> is invalid
	 * @throws NullPointerException if <code>childKey</code> is <code>null</code>
	 * @since 5.1.0
	 */
	public void addCategoryAssociation(MutableTimedAssociationKey categoryKey) {
		categoryAssociations.add(categoryKey);
	}

	public void clearProperty(String key) {
		if (key == null) throw new NullPointerException("key cannot be null");
		propertyMap.remove(key);
	}

	public void copyFrom(GenericEntity entity) {
		if (this == entity) return;
		if (getID() != entity.getID()) {
			throw new IllegalArgumentException("id mismatch: expected " + getID() + " but was " + entity.getID());
		}
		synchronized (this) {
			setName(entity.getName());
			this.parentID = entity.parentID;
			this.propertyMap.clear();
			this.propertyMap.putAll(entity.propertyMap);
			this.setCategoryAssociations(entity);
		}
	}

	@Override
	public Auditable deepCopy() {
		return new GenericEntity(this);
	}

	@Override
	public String getAuditDescription() {
		return type.getDisplayName() + " '" + getName() + "'";
	}

	@Override
	public String getAuditName() {
		return getName();
	}

	public boolean getBooleanProperty(String key) {
		if (key == null) throw new NullPointerException("key cannot be null");
		if (propertyMap.containsKey(key)) {
			return ((Boolean) getProperty(key)).booleanValue();
		}
		else {
			return false;
		}
	}

	/**
	 * Gets all category relationships for the specified child id. 
	 * @param categoryID the category id
	 * @return list of {@link MutableTimedAssociationKey} instances, if found; an empty list, otherwise
	 * @since 5.1.0
	 */
	public List<MutableTimedAssociationKey> getCategoryAssociations(int categoryID) {
		return categoryAssociations.getAssociationsForChild(categoryID);
	}

	/**
	 * Gets a list of category ids as of the specified date.
	 * @param date the date
	 * @return list of Integer, if found; an empty list, otherwise
	 * @throws NullPointerException if <code>date</code> is <code>null</code>
	 * @since 5.1.0
	 */
	public List<Integer> getCategoryIDList(Date date) {
		return categoryAssociations.getChildrendAsOf(date);
	}

	/**
	 * Returns iterator for category associations
	 * @return category association iterator
	 * @since 5.1.0
	 */
	public Iterator<MutableTimedAssociationKey> getCategoryIterator() {
		return categoryAssociations.iterator();
	}

	public Date getDateProperty(String key) {
		if (key == null) throw new NullPointerException("key cannot be null");
		return (Date) getProperty(key);
	}

	/**
	 * Gets a set of date synonyms used in category associations.
	 * @return set of {@link DateSynonym} objects; nevern <code>null</code> 
	 */
	public Set<DateSynonym> getDateSynonymsUsedInCategoryAssociations() {
		Set<DateSynonym> set = new HashSet<DateSynonym>();
		for (Iterator<MutableTimedAssociationKey> iter = categoryAssociations.iterator(); iter.hasNext();) {
			MutableTimedAssociationKey element = iter.next();
			if (element.getEffectiveDate() != null) set.add(element.getEffectiveDate());
			if (element.getExpirationDate() != null) set.add(element.getExpirationDate());
		}
		return set;
	}

	public double getDoubleProperty(String key) {
		if (key == null) throw new NullPointerException("key cannot be null");
		if (propertyMap.containsKey(key)) {
			return ((Double) getProperty(key)).doubleValue();
		}
		else {
			return 0.0;
		}
	}

	public float getFloatProperty(String key) {
		if (key == null) throw new NullPointerException("key cannot be null");
		if (propertyMap.containsKey(key)) {
			return ((Float) getProperty(key)).floatValue();
		}
		else {
			return 0.0f;
		}
	}

	public int getIntProperty(String key) {
		if (key == null) throw new NullPointerException("key cannot be null");
		if (propertyMap.containsKey(key)) {
			return ((Integer) getProperty(key)).intValue();
		}
		else {
			return 0;
		}
	}

	public long getLongProperty(String key) {
		if (key == null) throw new NullPointerException("key cannot be null");
		if (propertyMap.containsKey(key)) {
			return ((Long) getProperty(key)).longValue();
		}
		else {
			return 0L;
		}
	}

	@Override
	public String getName() {
		return name;
	}

	public int getParentID() {
		return parentID;
	}

	public String[] getProperties() {
		return propertyMap.keySet().toArray(new String[0]);
	}

	public Object getProperty(String key) {
		return propertyMap.get(key);
	}

	public Map<String, Object> getPropertyMap() {
		return Collections.unmodifiableMap(propertyMap);
	}

	public String getStringProperty(String key) {
		if (key == null) throw new NullPointerException("key cannot be null");
		Object value = getProperty(key);
		return (value == null ? null : value.toString());
	}

	/**
	 * @return generic entity type of this
	 */
	public GenericEntityType getType() {
		return type;
	}

	/**
	 * Tests if this has any category associations.
	 * @return <code>true</code> if this has at least one category association; <code>false</code>, otherwise
	 */
	public boolean hasCategoryAssociation() {
		return !categoryAssociations.isEmpty();
	}

	/**
	 * Tests if this has any existing category association that overlaps with categoryKey 
	 * @param categoryKey categoryKey
	 * @return  <code>true</code> if this has at least one category association that
	 * overlaps with categoryKey; <code>false</code>, otherwise
	 */
	public boolean hasOverlappingCategoryAssociation(MutableTimedAssociationKey categoryKey) {
		boolean results = false;
		for (Iterator<MutableTimedAssociationKey> i = this.getCategoryIterator(); i.hasNext();) {
			MutableTimedAssociationKey key = i.next();
			if (key.overlapsWith(categoryKey)) {
				results = true;
				break;
			}
		}
		return results;
	}

	public boolean hasProperty(String key) {
		if (key == null) throw new NullPointerException("key cannot be null");
		return propertyMap.containsKey(key) && propertyMap.get(key) != null;
	}

	/**
	 * Tests if this has the same category associations as the specified entity.
	 * @param entity the entity to check against
	 * @return true if same association; false, otherwise
	 * @throws NullPointerException if <code>entity</code> is <code>null</code>
	 * @since 5.1.0
	 */
	public boolean hasSameCategoryAssociations(GenericEntity entity) {
		if (this.categoryAssociations.size() == entity.categoryAssociations.size()) {
			for (Iterator<MutableTimedAssociationKey> iter = this.categoryAssociations.iterator(); iter.hasNext();) {
				MutableTimedAssociationKey key = iter.next();
				if (!entity.categoryAssociations.contains(key)) {
					return false;
				}
			}
			return true;
		}
		else {
			return false;
		}
	}

	@Override
	public boolean isForClone() {
		return forClone;
	}

	/**
	 * Removes all category relationships.
	 * If no such relationships are found, this is a no-op.
	 * @since 5.1.0
	 */
	public void removeAllCategoryAssociations() {
		categoryAssociations.clear();
	}

	/**
	 * Removes all relationships to the specified child.
	 * If no such relationships are found, this is a no-op.
	 * @param categoryID the id of the child to remove
	 * @since 5.1.0
	 */
	public void removeAllCategoryAssociations(int categoryID) {
		categoryAssociations.removeAll(categoryID);
	}

	/**
	 * Removes the specified category relationship from this.
	 * @param categoryKey the category relationship to remove
	 * @throws NullPointerException if <code>childKey</code> is <code>null</code>
	 * @since 5.1.0
	 */
	public void removeCategoryAssociation(MutableTimedAssociationKey categoryKey) {
		categoryAssociations.remove(categoryKey);
	}

	/**
	 * Makes sure this has the same category associations as the specified source entity.
	 * @param source the source entity to copy category associations from
	 * @throws NullPointerException if <code>category</code> is <code>null</code>
	 * @since 5.1.0
	 */
	public void setCategoryAssociations(GenericEntity source) {
		this.categoryAssociations.clear();
		this.categoryAssociations.addAll(source.categoryAssociations);
	}

	@Override
	public void setCopyPolicies(boolean copyPolicies) {
		this.copyPolicies = copyPolicies;
	}

	@Override
	public void setForClone(boolean forClone) {
		this.forClone = forClone;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setParentID(int i) {
		parentID = i;
	}

	public void setProperty(String key, boolean value) {
		if (key == null) throw new NullPointerException("key cannot be null");
		propertyMap.put(key, Boolean.valueOf(value));
	}

	public void setProperty(String key, Date value) {
		if (key == null) throw new NullPointerException("key cannot be null");
		propertyMap.put(key, value);
	}

	public void setProperty(String key, double value) {
		if (key == null) throw new NullPointerException("key cannot be null");
		propertyMap.put(key, new Double(value));
	}

	public void setProperty(String key, float value) {
		if (key == null) throw new NullPointerException("key cannot be null");
		propertyMap.put(key, new Float(value));
	}

	public void setProperty(String key, int value) {
		if (key == null) throw new NullPointerException("key cannot be null");
		propertyMap.put(key, new Integer(value));
	}

	public void setProperty(String key, long value) {
		if (key == null) throw new NullPointerException("key cannot be null");
		propertyMap.put(key, new Long(value));
	}

	public void setProperty(String key, Object value) {
		if (key == null) throw new NullPointerException("key cannot be null");
		propertyMap.put(key, value);
	}

	@Override
	public boolean shouldCopyPolicies() {
		return copyPolicies;
	}

	@Override
	public String toString() {
		return "GenericEntity" + "[name=" + name + ",parent=" + parentID + ",clone?=" + forClone + "]" + super.toString();
	}

	// TT 2029 update association date
	public void updateAssociationDates(DateSynonym ds) {
		synchronized (categoryAssociations) {
			for (Iterator<MutableTimedAssociationKey> i = categoryAssociations.iterator(); i.hasNext();) {
				MutableTimedAssociationKey key = i.next();
				key.updateEffExpDates(ds);
			}
		}
	}

}