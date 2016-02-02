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
 * @author Geneho Kim
 * @author MindBox
 * @since PowerEditor 2.4.0
 */
public class GuidelineTemplateSelectionAdapter implements GuidelineTemplateSelectionListener {

	public void templateSelected(GridTemplate template) throws CanceledException {
	}

	public void usageSelected(TemplateUsageType usageType) {
	}
	
	public void selectionCleared() {
	}

	public void usageGroupSelected(GuidelineTabConfig guidelineTabConfig) throws CanceledException {
	}

}
