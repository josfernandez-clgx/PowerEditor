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
	 * @param entities
	 * @since 3.0.0
	 */
	void addContext(GenericEntity[] entities);

	/**
	 * 
	 * @param categories
	 * @since PowerEditor 3.1.0
	 */
	void addContext(GenericCategory[] categories);

	void clearContext();

	/**
	 * 
	 * @param entities
	 * @since 3.0.0
	 */
	void removeContext(GenericEntity[] entities);

	/**
	 * 
	 * @param categories
	 * @since PowerEditor 3.1.0
	 */
	void removeContext(GenericCategory[] categories);

	/**
	 * 
	 * @return contexts
	 * @since PowerEditor 4.2.0
	 */
	GuidelineContext[] getGuidelineContexts();

	/**
	 * 
	 * @param contexts
	 *            context elements to set
	 * @since PowerEditor 4.2.0
	 */
	void setContextElemens(GuidelineContext[] contexts);
}
