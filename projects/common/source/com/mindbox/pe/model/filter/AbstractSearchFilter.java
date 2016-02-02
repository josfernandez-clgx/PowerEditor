package com.mindbox.pe.model.filter;

import java.io.Serializable;

import com.mindbox.pe.model.EntityType;
import com.mindbox.pe.model.GenericEntityType;
import com.mindbox.pe.model.Persistent;

/**
 * @author Gene Kim
 * @author MindBox, Inc
 * @since PowerEditor 1.0
 */
public abstract class AbstractSearchFilter<T extends Persistent> implements SearchFilter<T>, Serializable {

    private static final long serialVersionUID = 2003061612207000L;

    static final boolean contains(String src, String token) {
        if (token == null || token.length() == 0) { return true; }
        if (src != null) {
            String srcString = src.toUpperCase();
            String tokenUpper = token.toUpperCase();
            return srcString.indexOf(tokenUpper) != -1;
        }
        else {
            return false;
        }
    }

    protected final EntityType entityType;

    /**
     *  
     */
    public AbstractSearchFilter(EntityType entityType) {
        super();
        this.entityType = entityType;
    }

    public EntityType getEntityType() {
        return entityType;
    }

    public GenericEntityType getGenericEntityType() {
        return null;
    }

}