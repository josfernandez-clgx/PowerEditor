package com.mindbox.pe.model.filter;

import com.mindbox.pe.model.PeDataType;
import com.mindbox.pe.model.Persistent;

/**
 * Filter that accepts all.
 * @author Gene Kim
 * @author MindBox, Inc
 * @since PowerEditor 1.0
 */
public final class AllSearchFilter<T extends Persistent> extends AbstractSearchFilter<T> {

	private static final long serialVersionUID = 596656890514743748L;

	public AllSearchFilter(PeDataType entityType) {
		super(entityType);
	}

	public boolean isAcceptable(T object) {
		return true;
	}

}
