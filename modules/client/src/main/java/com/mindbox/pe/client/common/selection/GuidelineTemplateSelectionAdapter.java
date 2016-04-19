/*
 * Created on 2004. 3. 4.
 *
 */
package com.mindbox.pe.client.common.selection;

import com.mindbox.pe.model.TemplateUsageType;
import com.mindbox.pe.model.exceptions.CanceledException;
import com.mindbox.pe.model.template.GridTemplate;
import com.mindbox.pe.xsd.config.GuidelineTab;

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

	public void usageGroupSelected(GuidelineTab GuidelineTab) throws CanceledException {
	}

}
