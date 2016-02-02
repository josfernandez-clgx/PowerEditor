package com.mindbox.pe.client.common;

import java.awt.BorderLayout;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;

import com.mindbox.pe.client.applet.PowerEditorSwingTheme;
import com.mindbox.pe.client.applet.UIFactory;
import com.mindbox.pe.client.common.filter.AbstractFilterPanel;
import com.mindbox.pe.client.common.selection.AbstractSelectionPanel;
import com.mindbox.pe.client.common.tab.PowerEditorTabPanel;
import com.mindbox.pe.communication.ServerException;
import com.mindbox.pe.model.EntityType;
import com.mindbox.pe.model.GenericEntityType;
import com.mindbox.pe.model.Persistent;
import com.mindbox.pe.model.exceptions.CanceledException;

/**
 * Three Tier Panel.
 * @author Gene Kim
 * @author MindBox, Inc
 * @since PowerEditor 1.10.0
 */
public abstract class ThreeTierPanel<T extends Persistent,B extends ButtonPanel> extends JPanel implements PowerEditorTabPanel {

	protected final AbstractFilterPanel<T,B> filterPanel;
	protected final AbstractSelectionPanel<T,B> selectionPanel;
	protected final JPanel workPanel;
	protected final EntityType entityType;
	protected final JTabbedPane tabPane;

	/**
	 * Generic entity type for this.
	 * @since 3.0.0
	 */
	protected final GenericEntityType genericEntityType;

	protected ThreeTierPanel(EntityType entityType, AbstractFilterPanel<T,B> filterPanel, AbstractSelectionPanel<T,B> selectionPanel, JPanel workPanel) {
		this(null, entityType, filterPanel, selectionPanel, workPanel);
	}

	protected ThreeTierPanel(GenericEntityType genericEntityType, AbstractFilterPanel<T,B> filterPanel, AbstractSelectionPanel<T,B> selectionPanel,
			JPanel workPanel) {
		this(genericEntityType, null, filterPanel, selectionPanel, workPanel);
	}

	private ThreeTierPanel(GenericEntityType genericEntityType, EntityType entityType, AbstractFilterPanel<T,B> filterPanel,
			AbstractSelectionPanel<T,B> selectionPanel, JPanel workPanel) {
		UIFactory.setLookAndFeel(this);
		this.entityType = entityType;
		this.filterPanel = filterPanel;
		this.selectionPanel = selectionPanel;
		this.workPanel = workPanel;
		this.tabPane = new JTabbedPane();
		this.tabPane.setFont(PowerEditorSwingTheme.smallTabFont);
		this.genericEntityType = genericEntityType;

		tabPane.addTab("Filter", null, new JScrollPane(filterPanel));

		initPanel();
	}

	protected final void doLayout(JPanel panel) {
		JSplitPane spOne = UIFactory.createSplitPane(JSplitPane.HORIZONTAL_SPLIT);

		JSplitPane spTwo = UIFactory.createSplitPane(JSplitPane.VERTICAL_SPLIT);
		spTwo.setTopComponent(new JScrollPane(selectionPanel));
		spTwo.setBottomComponent(workPanel);
		spTwo.setResizeWeight(0.5);

		spOne.setLeftComponent(tabPane);
		spOne.setRightComponent(spTwo);
		spOne.setResizeWeight(0.125);

		panel.setLayout(new BorderLayout(0, 0));
		panel.add(spOne, BorderLayout.CENTER);
	}

	protected void initPanel() {
		doLayout(this);
	}


	public boolean hasUnsavedChanges() {
		if (workPanel instanceof PowerEditorTabPanel) {
			return ((PowerEditorTabPanel) workPanel).hasUnsavedChanges();
		}
		else {
			return false;
		}
	}

	public void saveChanges() throws CanceledException, ServerException {
		if (workPanel instanceof PowerEditorTabPanel) {
			((PowerEditorTabPanel) workPanel).saveChanges();
		}
	}

	public void discardChanges() {
		selectionPanel.clearSelection();
		selectionPanel.discardChanges();
		
		if (workPanel instanceof PowerEditorTabPanel) {
			((PowerEditorTabPanel) workPanel).discardChanges();
		}
	}
}