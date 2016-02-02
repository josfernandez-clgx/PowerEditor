package com.mindbox.pe.server.validate.oval;

import java.util.List;
import java.util.Map;

import net.sf.oval.Validator;
import net.sf.oval.context.OValContext;
import net.sf.oval.exception.OValException;

import com.mindbox.pe.common.config.EntityTypeDefinition;
import com.mindbox.pe.common.validate.oval.ServerConstraintCheck;
import com.mindbox.pe.common.validate.oval.UniqueName;
import com.mindbox.pe.common.validate.oval.UniqueNameCheck;
import com.mindbox.pe.model.GenericCategory;
import com.mindbox.pe.model.GenericEntity;
import com.mindbox.pe.model.GenericEntityType;
import com.mindbox.pe.server.cache.EntityManager;

/**
 * Server implementation of {@link UniqueName} annotation.
 * @author kim
 * @see UniqueNameCheck
 */
public class UniqueNameConstraintCheck implements ServerConstraintCheck {

	/**
	 * @param validatedObject must be {@link GenericEntity} or {@link GenericCategory}; if not, this returns <code>false</code>
	 * @param valueToValidate must be String; if not, this returns <code>false</code>
	 */
	@Override
	public boolean isValid(Object validatedObject, Object valueToValidate, OValContext arg2, Validator arg3,
			Map<String, String> messageVarMap) throws OValException {
		if (valueToValidate == null) return true;
		if (valueToValidate instanceof String) {
			if (validatedObject instanceof GenericEntity) {
				GenericEntityType entityType = ((GenericEntity) validatedObject).getType();
				EntityTypeDefinition entityTypeDefinition = GenericEntityType.getEntityTypeDefinition(entityType);
				if (entityTypeDefinition != null && entityTypeDefinition.uniqueEntityNames()) {
					// check generic entity with the same name
					GenericEntity cachedEntity = EntityManager.getInstance().getEntity(
							entityType,
							((GenericEntity) validatedObject).getName());
					if (cachedEntity != null && cachedEntity.getID() != ((GenericEntity) validatedObject).getID()) {
						return false;
					}
					// check generic category with the same name
					if (entityTypeDefinition.uniqueCategoryNames()) {
						List<GenericCategory> categoryList = EntityManager.getInstance().getAllGenericCategoriesByName(
								entityType.getCategoryType(),
								((GenericEntity) validatedObject).getName());
						return categoryList.isEmpty();
					}
				}
				return true;
			}
			else if (validatedObject instanceof GenericCategory) {
				GenericEntityType entityType = GenericEntityType.forCategoryType(((GenericCategory) validatedObject).getType());
				EntityTypeDefinition entityTypeDefinition = GenericEntityType.getEntityTypeDefinition(entityType);
				if (entityTypeDefinition != null && entityTypeDefinition.uniqueCategoryNames()) {
					// check generic category with the same name
					List<GenericCategory> categoryList = EntityManager.getInstance().getAllGenericCategoriesByName(
							entityType.getCategoryType(),
							((GenericCategory) validatedObject).getName());
					if (categoryList.size() > 1) {
						return false;
					}
					else if (categoryList.size() == 1 && categoryList.get(0).getID() != ((GenericCategory) validatedObject).getID()) {
						return false;
					}

					// check generic entity with the same name
					GenericEntity cachedEntity = EntityManager.getInstance().getEntity(
							entityType,
							((GenericCategory) validatedObject).getName());
					return cachedEntity == null;
				}
				else {
					return true;
				}
			}
		}
		return false;
	}
}
