package com.mindbox.pe.client.applet.guidelines.search;

import com.mindbox.pe.client.applet.guidelines.manage.AbstractGridPanel;
import com.mindbox.pe.model.GuidelineContext;
import com.mindbox.pe.model.grid.AbstractGuidelineGrid;

/**
 * Guideline edit panel.
 * @author Geneho Kim
 * @since PowerEditor
 */
final class GuidelineEditPanel extends AbstractGridPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = -3951228734910107454L;

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