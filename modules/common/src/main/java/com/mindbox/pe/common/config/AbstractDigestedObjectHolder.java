/*
 * Created on 2004. 3. 18.
 *
 */
package com.mindbox.pe.common.config;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Contains objects created by Digester.
 * This is a serializable version of {@link com.mindbox.pe.server.imexport.digest.AbstractObjectHolder}, 
 * which must reside in its package. So, instead of using it, this class is introduced 
 * as a serializable version so that PE server can pass this to the client.
 * 
 * @author Geneho Kim
 * @author MindBox
 * @since PowerEditor 3.0.0
 */
public abstract class AbstractDigestedObjectHolder implements Serializable {
	
	private static final long serialVersionUID = 200404150000L;

	private final Map<Class<?>,List<Object>> objectMap;

	public AbstractDigestedObjectHolder() {
		objectMap = new HashMap<Class<?>,List<Object>>();
	}

	private List<Object> getList(Class<?> c) {
		if (objectMap.containsKey(c)) {
			return objectMap.get(c);
		}
		else {
			List<Object> list = new LinkedList<Object>();
			objectMap.put(c, list);
			return list;
		}
	}

	public final void addObject(Object object) {
		getList(object.getClass()).add(object);
	}

	public final void addObjects(Collection<?> objects) {
		for (Object object : objects) {
			addObject(object);
		}
	}
	
	/**
	 * Gets a list of objects of the specified class as an unmodifible list.
	 * @param c
	 * @return list of those objects that are instances of <code>c</code>
	 */
	@SuppressWarnings("unchecked")
	public final <T> List<T> getObjects(Class<T> c) {
		List<T> list = (List<T>) getList(c);
		return Collections.unmodifiableList(list);
	}

	/**
	 * Gets a list of objects of the specified class.
	 * If <code>comparator</code> is not <code>null</code>, this returns the list sorted using the specified comparator;
	 * otherwise, this is identical to {@link #getObjects(Class)}. 
	 * @param c
	 * @param comparator
	 * @return list of those objects that are instances of <code>c</code>
	 */
	@SuppressWarnings("unchecked")
	public final <T> List<T> getObjects(Class<T> c, Comparator<T> comparator) {
		if (comparator != null) {
			List<T> list = (List<T>) getList(c);
			Collections.sort(list, comparator);
			return list;
		}
		else {
			return getObjects(c);
		}
	}

	public final Set<Class<?>> getClassKeySet() {
		return Collections.unmodifiableSet(objectMap.keySet());
	}
	
	protected final void removeObjects(Class<?> c) {
		objectMap.remove(c);
	}
}
