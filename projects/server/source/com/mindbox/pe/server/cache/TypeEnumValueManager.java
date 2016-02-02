/*
 * Created on 2005. 5. 23.
 *
 */
package com.mindbox.pe.server.cache;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.mindbox.pe.model.TypeEnumValue;


/**
 * Cache manager for type enum values.
 * @author Geneho Kim
 * @since PowerEditor 4.3.1
 */
public class TypeEnumValueManager extends AbstractCacheManager {

	private static TypeEnumValueManager instance = null;

	/**
	 * Gets the one and only instance of this class.
	 * @return the only instance
	 */
	public static TypeEnumValueManager getInstance() {
		if (instance == null) {
			instance = new TypeEnumValueManager();
		}
		return instance;
	}

	private Map<String,List<TypeEnumValue>> typeEnumMap = new HashMap<String,List<TypeEnumValue>>();
	
	private TypeEnumValueManager() {
	}
	
	public void startLoading() {
		typeEnumMap.clear();
	}
	
	public void finishLoading() {
		
	}
	
	public void insert(String typeKey, TypeEnumValue enumValue) {
		getEnumValueList(typeKey).add(enumValue);
	}
	
	public List<TypeEnumValue> getAllEnumValues(String typeKey) {
		return Collections.unmodifiableList(getEnumValueList(typeKey));
	}
	
	public TypeEnumValue getEnumValue(String typeKey, int enumID) {
		for (Iterator<TypeEnumValue> iter = getEnumValueList(typeKey).iterator(); iter.hasNext();) {
			TypeEnumValue element = iter.next();
			if (element.getID() == enumID) {
				return element;
			}
		}
		return null;
	}
	
	public TypeEnumValue getEnumValueForDispLabel(String typeKey, String displayLabel) {
		for (Iterator<TypeEnumValue> iter = getEnumValueList(typeKey).iterator(); iter.hasNext();) {
			TypeEnumValue element = iter.next();
			if (element.getDisplayLabel().equals(displayLabel)) {
				return element;
			}
		}
		return null;
	}
	
	public TypeEnumValue getEnumValueForValue(String typeKey, String value) {
		for (Iterator<TypeEnumValue> iter = getEnumValueList(typeKey).iterator(); iter.hasNext();) {
			TypeEnumValue element = iter.next();
			if (element.getValue().equals(value)) {
				return element;
			}
		}
		return null;
	}
	
	public int getEnumValueIDForValue(String typeKey, String value) {
		for (Iterator<TypeEnumValue> iter = getEnumValueList(typeKey).iterator(); iter.hasNext();) {
			TypeEnumValue element = iter.next();
			if (element.getValue().equals(value)) {
				return element.getID();
			}
		}
		return -1;
	}
	
	public int getEnumValueIDForDispLabel(String typeKey, String displayLabel) {
		for (Iterator<TypeEnumValue> iter = getEnumValueList(typeKey).iterator(); iter.hasNext();) {
			TypeEnumValue element = iter.next();
			if (element.getDisplayLabel().equals(displayLabel)) {
				return element.getID();
			}
		}
		return -1;
	}
	
	private List<TypeEnumValue> getEnumValueList(String typeKey) {
		synchronized(typeEnumMap) {
			if (!typeEnumMap.containsKey(typeKey)) {
				typeEnumMap.put(typeKey, new ArrayList<TypeEnumValue>());
			}
			return typeEnumMap.get(typeKey);
		}
	}
	
	public Map<String, List<TypeEnumValue>> getTypeEnumValueMap() {
		synchronized (typeEnumMap) {
			return Collections.unmodifiableMap(typeEnumMap);
		}
	}
}
