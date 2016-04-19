package com.mindbox.pe.client.applet.guidelines.manage;

import java.util.List;

import com.mindbox.pe.model.GuidelineContext;
import com.mindbox.pe.model.grid.AbstractGuidelineGrid;
import com.mindbox.pe.model.grid.ProductGrid;
import com.mindbox.pe.model.template.GridTemplate;

/**
 * 
 * @author Gene Kim
 * @author MindBox
 * @since PowerEditor 1.0
 */
public interface GuidelineGridEditor {
	
	TemplateSelectionPanel getTemplatePanel();
	
	void populate(GuidelineContext[] contexts, List<ProductGrid> list, GridTemplate gridTemplate, boolean isSubContext, AbstractGuidelineGrid currentGrid);
	
	void setViewOnly(boolean isForEdit);

	void showGridManagementPanel();

	void showGridSelectionPanel();
	
	void resetContext(GuidelineContext[] newContext);
}
