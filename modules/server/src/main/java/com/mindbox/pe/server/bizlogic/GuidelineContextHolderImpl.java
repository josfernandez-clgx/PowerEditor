package com.mindbox.pe.server.bizlogic;

import com.mindbox.pe.common.AbstractGuidelineContextHolder;
import com.mindbox.pe.common.config.EntityConfigHelper;
import com.mindbox.pe.model.GenericCategory;
import com.mindbox.pe.model.GenericEntity;
import com.mindbox.pe.model.GenericEntityType;
import com.mindbox.pe.server.cache.EntityManager;
import com.mindbox.pe.server.config.ConfigurationManager;


/**
 * Concrete implementation of {@link com.mindbox.pe.common.GuidelineContextHolder} for PE server side use.
 * @author Geneho Kim
 * @since PowerEditor 4.2.0
 */
class GuidelineContextHolderImpl extends AbstractGuidelineContextHolder {

	protected EntityConfigHelper getEntityConfiguration() {
		return ConfigurationManager.getInstance().getEntityConfigHelper();
	}

	protected GenericEntity getGenericEntity(GenericEntityType type, int id) {
		return EntityManager.getInstance().getEntity(type, id);
	}

	protected GenericCategory getGenericCategory(int genericCategoryType, int categoryID) {
		return EntityManager.getInstance().getGenericCategory(genericCategoryType, categoryID);
	}

}
