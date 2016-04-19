package com.mindbox.pe.model.filter;

import java.util.Date;

import com.mindbox.pe.model.GenericEntityType;

public final class GenericEntityByCategoryFilter extends AbstractGenericEntitySearchFilter  {

	private static final long serialVersionUID = 2006052590001L;

	private final int[] categoryIDs;
	private final Date date;
    private boolean includeDescendents;

	public GenericEntityByCategoryFilter(GenericEntityType type, int categoryIDs[], Date date, boolean includeDescendents) {
		super(type);
		this.categoryIDs = categoryIDs;
		this.date = date;
        this.includeDescendents = includeDescendents;
	}

	public int[] getCategoryIDs() {
		return categoryIDs;
	}

	
	public Date getDate() {
		return date;
	}
	
	public String toString() {
		return "GenericEntityByCategoryFilter[categorySize=" + categoryIDs.length + "]" + super.toString();
	}

    public boolean includeDescendents() {
        return includeDescendents;
    }
}
