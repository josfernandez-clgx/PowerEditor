package com.mindbox.pe.model.filter;

import com.mindbox.pe.model.GenericEntity;
import com.mindbox.pe.model.GenericEntityType;
import com.mindbox.pe.model.Persistent;

/**
 * @author Gene Kim
 * @author MindBox, Inc
 * @since PowerEditor 3.0.0
 */
public class GenericEntityBasicSearchFilter extends AbstractGenericEntitySearchFilter {

    private static final long serialVersionUID = 2004042370001L;

    
    protected String nameCriterion = null;
    protected int parentIDCriterion = Persistent.UNASSIGNED_ID;

    /**
     *  
     */
    public GenericEntityBasicSearchFilter(GenericEntityType entityType) {
        super(entityType);
    }

    /**
     * @return the name criteria
     */
    public final String getNameCriterion() {
        return nameCriterion;
    }

    /**
     * @param string
     *            the new name criteria
     */
    public final void setNameCriterion(String string) {
        nameCriterion = string;
    }

    public boolean isAcceptable(GenericEntity object) {
    	if (!super.isAcceptable(object)) return false;
        if (nameCriterion != null && !contains(object.getName(), this.nameCriterion)) { return false; }
        if (parentIDCriterion > -1 && object.getParentID() != parentIDCriterion) { return false; }
        return true;
    }

    /**
     * @return Returns the parentIDCriteria
     */
    public final int getParentIDCriteria() {
        return parentIDCriterion;
    }

    /**
     * @param parentIDCriteria
     *            The parentIDCriteria to set
     */
    public final void setParentIDCriteria(int parentIDCriteria) {
        this.parentIDCriterion = parentIDCriteria;
    }
}