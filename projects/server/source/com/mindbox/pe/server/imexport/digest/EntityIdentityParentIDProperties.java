/*
 * Created on Apr 19, 2006
 *
 */
package com.mindbox.pe.server.imexport.digest;

import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import net.sf.oval.configuration.annotation.IsInvariant;
import net.sf.oval.constraint.AssertValid;

import com.mindbox.pe.common.digest.DigestedObjectHolder;
import com.mindbox.pe.common.validate.oval.PositiveOrUnassigned;
import com.mindbox.pe.server.validate.oval.HasValidValuesForEntityProperties;
import com.mindbox.pe.server.validate.oval.HasValueForProperty;


/**
 * Digest object to hold entity with parent id and properties.
 * 
 * @author Geneho Kim
 * @since PowerEditor 4.5.0
 */
public abstract class EntityIdentityParentIDProperties extends EntityIdentity {

	@PositiveOrUnassigned
	private int parentID = -1;

	@HasValidValuesForEntityProperties
	@HasValueForProperty(property = "name")
	private final DigestedObjectHolder holder = new DigestedObjectHolder();

	public final void addObject(Object object) {
		holder.addObject(object);
	}

	protected final <T> List<T> getObjects(Class<T> objClass) {
		return holder.getObjects(objClass);
	}

	protected final <T> List<T> getObjects(Class<T> objClass, Comparator<T> comparator) {
		return holder.getObjects(objClass, comparator);
	}

	public final int getIntProperty(String name) {
		return getIntProperty(name, 0);
	}

	public final int getIntProperty(String name, int defValue) {
		try {
			return Integer.parseInt(getProperty(name));
		}
		catch (Exception ex) {
			return defValue;
		}
	}

	public final float getFloatProperty(String name) {
		try {
			return Float.valueOf(getProperty(name)).floatValue();
		}
		catch (Exception ex) {
			return 0.0f;
		}
	}

	public final String getProperty(String name) {
		for (Iterator<Property> iter = holder.getObjects(Property.class).iterator(); iter.hasNext();) {
			Property element = (Property) iter.next();
			if (element.getName().equals(name)) {
				return element.getValue();
			}
		}
		return null;
	}

	public final String getProperty(String name, String defValue) {
		String value = getProperty(name);
		return (value == null ? defValue : value);
	}

	@IsInvariant
	@AssertValid(requireValidElements=true)
	public final List<Property> getAllProperties() {
		return Collections.unmodifiableList(holder.getObjects(Property.class));
	}

	public final int getParentID() {
		return parentID;
	}

	public final void setParentID(int i) {
		parentID = (i == 0 ? -1 : i);
	}

	public String toString() {
		StringBuffer buff = new StringBuffer();
		buff.append("[id=");
		buff.append(getId());
		buff.append(",type=");
		buff.append(getType());
		buff.append(",parent=");
		buff.append(parentID);
		buff.append(",");
		for (Iterator<Property> iter = holder.getObjects(Property.class).iterator(); iter.hasNext();) {
			buff.append(iter.next());
		}
		buff.append("]");
		return buff.toString();
	}
}
