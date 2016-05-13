package com.mindbox.pe.model.filter;

import com.mindbox.pe.model.PeDataType;
import com.mindbox.pe.model.GenericEntityType;
import com.mindbox.pe.model.Persistent;

/**
 * Entity Filter interface.
 * 
 * @since PowerEditor 1.0
 */
public interface SearchFilter<T extends Persistent> {
	
	/**
	 * 
	 * @param object
	 * @return
	 * @throws NullPointerException if object is <code>null</code>
	 */
	boolean isAcceptable(T object);
	
	PeDataType getEntityType();
	
	/**
	 * @return generic entity type of this search filter
	 * @since 3.0.0
	 */
	GenericEntityType getGenericEntityType();
}
