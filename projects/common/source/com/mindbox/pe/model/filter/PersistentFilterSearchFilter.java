package com.mindbox.pe.model.filter;

import com.mindbox.pe.model.EntityType;
import com.mindbox.pe.model.GenericEntityType;
import com.mindbox.pe.model.Persistent;

/**
 * 
 * @author Gene Kim
 * @author MindBox, Inc
 * @since PowerEditor 1.10.0
 */
public interface PersistentFilterSearchFilter<T extends Persistent> extends SearchFilter<T> {
	EntityType getFilterEntityType();
	boolean isForGenericEntity();
	GenericEntityType getFilterGenericEntityType();
}
