package com.mindbox.pe.client.applet.guidelines.search;

import com.mindbox.pe.client.applet.guidelines.manage.AbstractGridPanel;
import com.mindbox.pe.model.AbstractGuidelineGrid;
import com.mindbox.pe.model.GuidelineContext;

/**
 * Guideline edit panel.
 * @author Geneho Kim
 * @since PowerEditor
 */
final class GuidelineEditPanel extends AbstractGridPanel {

	public GuidelineEditPanel(boolean readOnly) {
		super(readOnly);
	}

	void showActivation(GuidelineContext[] context, AbstractGuidelineGrid grid) {
		setCurrentContext(context);
		activationsCombo.setSelectedItem(grid);
		lastSelectedGrid = grid;
		setActivation(grid);
	}
}