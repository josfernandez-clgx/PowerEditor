/*
 * Created on 2004. 3. 4.
 *
 */
package com.mindbox.pe.client.common.selection;

import com.mindbox.pe.common.config.GuidelineTabConfig;
import com.mindbox.pe.model.GridTemplate;
import com.mindbox.pe.model.TemplateUsageType;
import com.mindbox.pe.model.exceptions.CanceledException;

/**
 * Guideline template selection listener.
 * @author Geneho Kim
 * @author MindBox
 * @since PowerEditor 2.4.0
 */
public interface GuidelineTemplateSelectionListener {

	/**
	 * Called when a template has been selected.
	 * @param template the selected template
	 * @throws CanceledException if selection must be undone
	 */
	void templateSelected(GridTemplate template) throws CanceledException;
	
	/**
	 * Invoked when a usage type has been selected.
	 * @param usageType the selected usage type
	 * @throws CanceledException if selection must be undone
	 */
	void usageSelected(TemplateUsageType usageType) throws CanceledException;
	
	/**
	 * Invoked when a usage type has been selected.
	 * @param guidelineTabConfig the guideline tab representing the selected usage group
	 * @throws CanceledException if selection must be undone
	 * @since PowerEditor 4.2.0
	 */
	void usageGroupSelected(GuidelineTabConfig guidelineTabConfig) throws CanceledException;
	
	/**
	 * Invoked when selection has been cleared.
	 */
	void selectionCleared();
}
