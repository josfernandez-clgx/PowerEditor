package com.mindbox.pe.server.validate.oval;

import java.util.List;
import java.util.Map;

import net.sf.oval.Validator;
import net.sf.oval.context.OValContext;
import net.sf.oval.exception.OValException;

import com.mindbox.pe.common.config.ConfigUtil;
import com.mindbox.pe.common.validate.oval.ServerConstraintCheck;
import com.mindbox.pe.common.validate.oval.UniqueName;
import com.mindbox.pe.common.validate.oval.UniqueNameCheck;
import com.mindbox.pe.model.GenericCategory;
import com.mindbox.pe.model.GenericEntity;
import com.mindbox.pe.model.GenericEntityType;
import com.mindbox.pe.server.cache.EntityManager;
import com.mindbox.pe.server.config.ConfigurationManager;
import com.mindbox.pe.xsd.config.EntityType;

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
	public boolean isValid(Object validatedObject, Object valueToValidate, OValContext arg2, Validator arg3, Map<String, String> messageVarMap) throws OValException {
		if (valueToValidate == null) return true;
		if (valueToValidate instanceof String) {
			if (validatedObject instanceof GenericEntity) {
				final GenericEntityType entityType = GenericEntity.class.cast(validatedObject).getType();
				final EntityType entityTypeDefinition = ConfigurationManager.getInstance().getEntityConfigHelper().findEntityTypeDefinition(entityType);
				if (entityTypeDefinition != null && ConfigUtil.isUniqueEntityNames(entityTypeDefinition)) {
					// check generic entity with the same name
					GenericEntity cachedEntity = EntityManager.getInstance().getEntity(entityType, GenericEntity.class.cast(validatedObject).getName());
					if (cachedEntity != null && cachedEntity.getID() != GenericEntity.class.cast(validatedObject).getID()) {
						return false;
					}
					// check generic category with the same name
					if (ConfigUtil.isUniqueEntityNames(entityTypeDefinition)) {
						List<GenericCategory> categoryList = EntityManager.getInstance().getAllGenericCategoriesByName(
								entityType.getCategoryType(),
								GenericEntity.class.cast(validatedObject).getName());
						return categoryList.isEmpty();
					}
				}
				return true;
			}
			else if (validatedObject instanceof GenericCategory) {
				final GenericEntityType entityType = GenericEntityType.forCategoryType(GenericCategory.class.cast(validatedObject).getType());
				final EntityType entityTypeDefinition = (entityType == null ? null : ConfigurationManager.getInstance().getEntityConfigHelper().findEntityTypeDefinition(entityType));
				if (entityTypeDefinition != null && ConfigUtil.isUniqueCategoryNames(entityTypeDefinition)) {
					// check generic category with the same name
					List<GenericCategory> categoryList = EntityManager.getInstance().getAllGenericCategoriesByName(entityType.getCategoryType(), GenericCategory.class.cast(validatedObject).getName());
					if (categoryList.size() > 1) {
						return false;
					}
					else if (categoryList.size() == 1 && categoryList.get(0).getID() != GenericCategory.class.cast(validatedObject).getID()) {
						return false;
					}

					// check generic entity with the same name
					GenericEntity cachedEntity = EntityManager.getInstance().getEntity(entityType, GenericCategory.class.cast(validatedObject).getName());
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
