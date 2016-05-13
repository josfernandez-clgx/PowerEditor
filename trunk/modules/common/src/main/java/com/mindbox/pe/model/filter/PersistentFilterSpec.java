package com.mindbox.pe.model.filter;

import java.util.Map;

import com.mindbox.pe.model.Persistent;

/**
 * Filter specification that can be persisted.
 * @author Geneho Kim
 *
 */
public interface PersistentFilterSpec extends Persistent  {

	public static final String PARAMETER_SEPARATOR = "|";
	public static final String FIELD_ASSIGNMENT = "=";
	
	String getName();
	String toParamString();
	int getEntityTypeID();
	boolean isForGenericEntity();

	/**
	 * Equivalent to <code>setInvariants(paramMap, null)</code>.
	 * @param paramMap
	 * @throws UnsupportedOperationException if this variant of setInvariants method is not supported
	 * @see #setInvariants(Map, Object)
	 */
	void setInvariants(Map<String,String> paramMap);
	
	/**
	 * Sets invariants using values from the specified map and the specified helper object.
	 * @param paramMap map of string to string
	 * @param helperObject helper object; can be <code>null</code>
	 * @throws UnsupportedOperationException if helperObject is required but was <code>null</code> or helperObject is of incorrect type
	 */
	void setInvariants(Map<String,String> paramMap, Object helperObject);
	
	void setName(String name);
	void setID(int id);
}
