package com.mindbox.pe.model.filter;

import java.io.Serializable;

import com.mindbox.pe.model.GenericCategory;
import com.mindbox.pe.model.GenericEntityType;
import com.mindbox.pe.model.PeDataType;

/**
 * @author Gene Kim
 * @author MindBox, Inc
 * @since PowerEditor 3.0.0
 */
public abstract class AbstractGenericCategorySearchFilter implements SearchFilter<GenericCategory>, Serializable {

	private static final long serialVersionUID = 2004050570000L;

	protected final int categoryType;
	protected final boolean rootOnly;

	protected AbstractGenericCategorySearchFilter(int categoryType, boolean rootOnly) {
		super();
		this.categoryType = categoryType;
		this.rootOnly = rootOnly;
	}

	public final int getCategoryType() {
		return categoryType;
	}

	@Override
	public PeDataType getEntityType() {
		return null;
	}

	@Override
	public GenericEntityType getGenericEntityType() {
		return null;
	}

	@Override
	public boolean isAcceptable(GenericCategory object) {
		if (object == null) throw new NullPointerException();
		return (!rootOnly || object.isRoot());
	}

}