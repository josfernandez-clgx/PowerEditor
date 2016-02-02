/*
 * Created on 2004. 4. 15.
 *
 */
package com.mindbox.pe.common.config;

import java.util.Iterator;
import java.util.List;

import com.mindbox.pe.model.GenericEntityType;

/**
 * @author Geneho Kim
 * @author MindBox
 * @since PowerEditor 3.0.0
 */
public final class EntityConfiguration extends AbstractDigestedObjectHolder {

	private static final long serialVersionUID = 200404150001L;

	public CategoryTypeDefinition[] getCategoryTypeDefinitions() {
		return super.getObjects(CategoryTypeDefinition.class).toArray(
			new CategoryTypeDefinition[0]);
	}

	public EntityTypeDefinition[] getEntityTypeDefinitions() {
		return super.getObjects(EntityTypeDefinition.class).toArray(
			new EntityTypeDefinition[0]);
	}

	/**
	 * Gets the entity type definition for message context.
	 * @return EntityTypeDefinition for message context
	 */
	public EntityTypeDefinition getEntityTypeForMessageContext() {
		List<EntityTypeDefinition> list = super.getObjects(EntityTypeDefinition.class);
		for (Iterator<EntityTypeDefinition> iter = list.iterator(); iter.hasNext();) {
			EntityTypeDefinition element = iter.next();
			if (element.useInMessageContext()) return element;
		}
		return null;
	}
	
	/**
	 * 
	 * @param id
	 * @return null if not found
	 */
	public EntityTypeDefinition findEntityTypeDefinition(int id) {
		for (Iterator<EntityTypeDefinition> iter = getObjects(EntityTypeDefinition.class).iterator(); iter.hasNext();) {
			EntityTypeDefinition element = iter.next();
			if (element.getTypeID() == id) {
				return element;
			}
		}
		return null;
	}
	
	/**
	 * 
	 * @param type
	 * @return null if not found
	 */
	public EntityTypeDefinition findEntityTypeDefinition(GenericEntityType type) {
		return findEntityTypeDefinition(type.getID());
	}
	
	/**
	 * 
	 * @param typeID
	 * @return category type definition with id of <code>typeID</code>, if found; <code>null</code>, otherwise
	 * @since PowerEditor 3.1.0
	 */
	public CategoryTypeDefinition findCategoryTypeDefinition(int typeID) {
		for (Iterator<CategoryTypeDefinition> iter = getObjects(CategoryTypeDefinition.class).iterator(); iter.hasNext();) {
			CategoryTypeDefinition element = iter.next();
			if (element.getTypeID() == typeID) {
				return element;
			}
		}
		return null;
	}
	
	/**
	 * 
	 * @param type
	 * @return category type definition for <code>type</code>, if found; <code>null</code>, otherwise
	 * @since PowerEditor 3.1.0
	 */
	public CategoryTypeDefinition getCategoryDefinition(GenericEntityType type) {
		EntityTypeDefinition typeDef = findEntityTypeDefinition(type);
		if (typeDef != null&& typeDef.hasCategory()) {
			return findCategoryTypeDefinition(typeDef.getCategoryType());
		}
		else {
			return null;
		}
	}
	
	public GenericEntityType findEntityTypeForCategoryType(int categoryType) {
		for (Iterator<EntityTypeDefinition> iter = getObjects(EntityTypeDefinition.class).iterator(); iter.hasNext();) {
			EntityTypeDefinition element = iter.next();
			if (element.getCategoryType() == categoryType) {
				return GenericEntityType.forID(element.getTypeID());
			}
		}
		return null;
	}
}
