package com.mindbox.pe.common;

import com.mindbox.pe.model.GenericEntityType;
import com.mindbox.pe.model.GuidelineContext;

public class ContextUtil {

	/**
	 * Tests if the container context array contains the specified context.
	 * This returns true if there exists no context c in contexts such that c.entityType == context.entityType.
	 * If there is such c, then this returns true if all of entity ids in context is a member of entity ids of c.
	 * @param container container
	 * @param context context
	 * @return <code>true</code> if <code>contexts</code> logically contains <code>context</code>; <code>false</code>, otherwise
	 */
	public static boolean containsContext(GuidelineContext[] container, GuidelineContext context) {
		if (container == null) return false;
		boolean contextOfSameTypeFound = false;
		for (int i = 0; i < container.length; i++) {
			if (context.getGenericEntityType() == container[i].getGenericEntityType() && context.getGenericCategoryType() == container[i].getGenericCategoryType()) {
				contextOfSameTypeFound = true;
				int[] ids = context.getIDs();
				for (int j = 0; j < ids.length; j++) {
					if (!UtilBase.isMember(ids[j], container[i].getIDs())) {
						return false;
					}
				}
			}
		}
		return contextOfSameTypeFound;
	}

	/**
	 * @param container container
	 * @param contexts contexts
	 * @return true if the container contains all the specified contexts
	 */
	public static boolean containsContext(GuidelineContext[] container, GuidelineContext[] contexts) {
		return ContextUtil.containsContext(container, contexts, false);
	}

	/**
	 * @param guidelineContexts guidelineContexts
	 * @param contexts contexts
	 * @param includeEmptyEntities true if empty contexts should considered contained  
	 * @return <code>true</code> if context is contained; <code>false</code>, otherwise
	 */
	public static boolean containsContext(GuidelineContext[] guidelineContexts, GuidelineContext[] contexts, boolean includeEmptyEntities) {
		if (includeEmptyEntities && guidelineContexts.length == 0) {
			return true;
		}
		else {
			for (int i = 0; i < contexts.length; i++) {
				if (!ContextUtil.containsContext(guidelineContexts, (GuidelineContext) contexts[i])) {
					return false;
				}
			}
		}
		return true;
	}

	public static boolean contextContainsCategory(GuidelineContext context) {
		return context.getGenericEntityType() == null && context.getGenericCategoryType() > 0;
	}

	public static boolean contextContainsEntity(GuidelineContext context) {
		return context.getGenericEntityType() != null;
	}

	public static boolean contextOfSameType(GuidelineContext context1, GuidelineContext context2) {
		if (context1.getGenericEntityType() != null && context2.getGenericEntityType() != null) {
			return context1.getGenericEntityType() == context2.getGenericEntityType();
		}
		else if (context1.getGenericCategoryType() > 0 && context2.getGenericCategoryType() > 0) {
			return context1.getGenericCategoryType() == context2.getGenericCategoryType();
		}
		else if (context1.getGenericEntityType() != null && context2.getGenericEntityType() == null) {
			return context1.getGenericEntityType().getCategoryType() == context2.getGenericCategoryType();
		}
		else if (context1.getGenericEntityType() == null && context2.getGenericEntityType() != null) {
			return context2.getGenericEntityType().getCategoryType() == context1.getGenericCategoryType();
		}
		else {
			return false;
		}
	}

	/**
	 * @param context context
	 * @param entityTypeName Name of the entity type
	 * @return true if the context is of the same entity type as the entityTypeName
	 */
	public static boolean isSameEntityType(GuidelineContext context, String entityTypeName) {
		GenericEntityType genericType = GenericEntityType.forName(entityTypeName);
		if (genericType != null) {
			if (context.getGenericEntityType() != null) {
				return context.getGenericEntityType().equals(genericType);
			}
			else if (context.getGenericCategoryType() > 0) {
				GenericEntityType type = GenericEntityType.forCategoryType(context.getGenericCategoryType());
				return type != null && type.equals(genericType);
			}
		}
		return false;
	}

	private ContextUtil() {
	}
}
