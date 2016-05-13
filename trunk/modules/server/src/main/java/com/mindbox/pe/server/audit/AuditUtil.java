package com.mindbox.pe.server.audit;

import com.mindbox.pe.model.GenericCategory;
import com.mindbox.pe.model.GenericEntity;
import com.mindbox.pe.model.GuidelineContext;
import com.mindbox.pe.server.cache.EntityManager;

public class AuditUtil {

	public static String generateContextStringForAudit(final GuidelineContext[] guidelineContexts) {
		// generate context element string
		final StringBuilder contextBuilder = new StringBuilder();
		boolean firstContext = true;
		for (final GuidelineContext guidelineContext : guidelineContexts) {
			if (!firstContext) {
				contextBuilder.append("; ");
			}
			int[] ids = guidelineContext.getIDs();
			contextBuilder.append(guidelineContext.getGenericEntityTypeForContext().getDisplayName());
			if (guidelineContext.hasCategoryContext()) {
				contextBuilder.append(" Category");
			}
			contextBuilder.append(": ");
			boolean first = true;
			for (final int id : ids) {
				if (!first) {
					contextBuilder.append(", ");
				}
				contextBuilder.append(generateContextStringForAudit(guidelineContext, id));
				if (first) {
					first = false;
				}
			}

			if (firstContext) {
				firstContext = false;
			}
		}
		return contextBuilder.toString();
	}

	public static String generateContextStringForAudit(final GuidelineContext guidelineContext, final int contextId) {
		if (guidelineContext.hasCategoryContext()) {
			// category
			final GenericCategory genericCategory = EntityManager.getInstance().getGenericCategory(guidelineContext.getGenericCategoryType(), contextId);
			return genericCategory == null
					? String.format("Category of type %s of id %d", guidelineContext.getGenericEntityTypeForContext().getDisplayName(), contextId)
					: genericCategory.getAuditName();
		}
		else {
			// entity
			final GenericEntity entity = EntityManager.getInstance().getEntity(guidelineContext.getGenericEntityType(), contextId);
			return entity == null ? String.format("%s of id %d", guidelineContext.getGenericEntityTypeForContext().getDisplayName(), contextId) : entity.getAuditName();
		}
	}

	private AuditUtil() {
	}
}
