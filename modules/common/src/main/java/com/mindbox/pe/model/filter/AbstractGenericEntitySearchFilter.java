package com.mindbox.pe.model.filter;

import java.io.Serializable;

import com.mindbox.pe.model.PeDataType;
import com.mindbox.pe.model.GenericEntity;
import com.mindbox.pe.model.GenericEntityType;

/**
 * @author Gene Kim
 * @author MindBox, Inc
 * @since PowerEditor 3.0.0
 */
public abstract class AbstractGenericEntitySearchFilter implements SearchFilter<GenericEntity>, Serializable {

	private static final long serialVersionUID = 2004042370002L;

	static final boolean contains(String src, String token) {
		if (token == null || token.length() == 0) {
			return true;
		}
		if (src != null) {
			String srcString = src.toUpperCase();
			String tokenUpper = token.toUpperCase();
			return srcString.indexOf(tokenUpper) != -1;
		}
		else {
			return false;
		}
	}

	protected final GenericEntityType entityType;

	/**
	 *  
	 */
	protected AbstractGenericEntitySearchFilter(GenericEntityType entityType) {
		super();
		if (entityType == null) throw new NullPointerException();
		this.entityType = entityType;
	}

	public boolean isAcceptable(GenericEntity object) {
		if (object == null) throw new NullPointerException();
		return object.getType() == entityType;
	}

	public final PeDataType getEntityType() {
		return null;
	}

	public final GenericEntityType getGenericEntityType() {
		return entityType;
	}

}