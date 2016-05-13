package com.mindbox.pe.common;

import com.mindbox.pe.model.GenericCategory;
import com.mindbox.pe.model.GenericEntity;
import com.mindbox.pe.model.GuidelineContext;

/**
 * Guildeline Context holder.
 * 
 * @author Gene Kim
 * @author MindBox
 * @since PowerEditor 2.0.0
 */
public interface GuidelineContextHolder {

	/**
	 * 
	 * @param categories categories
	 * @since PowerEditor 3.1.0
	 */
	void addContext(GenericCategory[] categories);

	/**
	 * 
	 * @param entities entities
	 * @since 3.0.0
	 */
	void addContext(GenericEntity[] entities);

	void clearContext();

	/**
	 * 
	 * @return contexts
	 * @since PowerEditor 4.2.0
	 */
	GuidelineContext[] getGuidelineContexts();

	/**
	 * 
	 * @param categories categories
	 * @since PowerEditor 3.1.0
	 */
	void removeContext(GenericCategory[] categories);

	/**
	 * 
	 * @param entities entities
	 * @since 3.0.0
	 */
	void removeContext(GenericEntity[] entities);

	/**
	 * 
	 * @param contexts contexts
	 *            context elements to set
	 * @since PowerEditor 4.2.0
	 */
	void setContextElemens(GuidelineContext[] contexts);
}
