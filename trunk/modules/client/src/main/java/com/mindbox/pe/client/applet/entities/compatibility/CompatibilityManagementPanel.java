/*
 * Created on 2004. 4. 20.
 *  
 */
package com.mindbox.pe.client.applet.entities.compatibility;

import javax.swing.JSplitPane;

/**
 * @author Geneho Kim
 * @author MindBox
 * @since PowerEditor 3.0.0
 */
public class CompatibilityManagementPanel extends JSplitPane {
	/**
	 * 
	 */
	private static final long serialVersionUID = -3951228734910107454L;

	public CompatibilityManagementPanel(boolean readOnly) {
		super(JSplitPane.HORIZONTAL_SPLIT);

		CompatibilityListPanel listPanel = new CompatibilityListPanel(readOnly);
		if (readOnly) {
			listPanel.setEnabled(false);
		}

		CompatibilitySearchPanel searchPanel = new CompatibilitySearchPanel(listPanel);
		setTopComponent(searchPanel);
		setBottomComponent(listPanel);
	}
}