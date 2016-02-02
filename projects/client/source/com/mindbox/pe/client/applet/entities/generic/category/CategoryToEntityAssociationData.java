/*
 * Created on Dec 12, 2006
 */
package com.mindbox.pe.client.applet.entities.generic.category;

import com.mindbox.pe.model.GenericEntity;
import com.mindbox.pe.model.assckey.MutableTimedAssociationKey;


/**
 * Value object used to hold a list of catgory associations that belong to
 * a single entity.   
 * @author MindBox
 * @since PowerEditor 5.1.0
 */
public class CategoryToEntityAssociationData {

    private static final long serialVersionUID = 2004042980000L;

    private GenericEntity entity;    
    private MutableTimedAssociationKey associationKey;

    public CategoryToEntityAssociationData(GenericEntity entity, MutableTimedAssociationKey associationKey) {
        this.entity = entity;
        this.associationKey = associationKey;
    }

    public MutableTimedAssociationKey getAssociationKey() {
        return associationKey;
    }

    public GenericEntity getEntity() {
        return entity;
    }
    
    public void setEntity(GenericEntity entity) {
        this.entity = entity;
    }

    public void setMutableTimedAssociationKey(MutableTimedAssociationKey key) {
        this.associationKey = key;
    }

}