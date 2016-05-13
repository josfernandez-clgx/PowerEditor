package com.mindbox.pe.client.applet.guidelines.manage;

import java.awt.CardLayout;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.JSplitPane;

import com.mindbox.pe.client.EntityModelCacheFactory;
import com.mindbox.pe.client.applet.PowerEditorSwingTheme;
import com.mindbox.pe.client.applet.UIFactory;
import com.mindbox.pe.client.common.event.ContextChangeEvent;
import com.mindbox.pe.client.common.event.ContextChangeListener;
import com.mindbox.pe.client.common.event.EntityDeleteEvent;
import com.mindbox.pe.client.common.event.EntityDeleteListener;
import com.mindbox.pe.client.common.tab.PowerEditorTabPanel;
import com.mindbox.pe.communication.ServerException;
import com.mindbox.pe.model.GenericEntity;
import com.mindbox.pe.model.GuidelineContext;
import com.mindbox.pe.model.exceptions.CanceledException;
import com.mindbox.pe.model.grid.AbstractGuidelineGrid;
import com.mindbox.pe.model.grid.ProductGrid;
import com.mindbox.pe.model.template.GridTemplate;
import com.mindbox.pe.xsd.config.GuidelineTab;

public class ManageGuidelineTab extends JPanel implements GuidelineGridEditor, ContextChangeListener, EntityDeleteListener, PowerEditorTabPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = -3951228734910107454L;

	private static final String LAYOUT_KEY_SELECTION = "GRIDSELECTION";
	private static final String LAYOUT_KEY_MANAGEMENT = "GRIDMANAGEMENT";

	private final ProductGridPanel gridPanel;
	private final TemplateSelectionPanel templatePanel;
	private final GuidelineContextDetailPanel detailPanel;

	public ManageGuidelineTab(final List<GuidelineTab> tabConfigs, boolean readOnly) {
		setFont(PowerEditorSwingTheme.tabFont);
		this.gridPanel = new ProductGridPanel(readOnly);
		gridPanel.setGridEditor(this);

		templatePanel = new TemplateSelectionPanel(readOnly);
		templatePanel.setGridEditor(this);

		detailPanel = new GuidelineContextDetailPanel(tabConfigs, templatePanel);
		detailPanel.addContextChangeListener(this);

		GuidelineContextSelectionPanel selectionPanel = new GuidelineContextSelectionPanel(detailPanel.getContextHolder());

		JSplitPane eastPane = UIFactory.createSplitPane(JSplitPane.VERTICAL_SPLIT, detailPanel, templatePanel);
		JSplitPane mainPane = UIFactory.createSplitPane(JSplitPane.HORIZONTAL_SPLIT, selectionPanel, eastPane);
		mainPane.setDividerLocation(300);

		setLayout(new CardLayout(0, 0));
		add(mainPane, LAYOUT_KEY_SELECTION);
		add(gridPanel, LAYOUT_KEY_MANAGEMENT);

		EntityModelCacheFactory.getInstance().addEntityDeleteListener(this);
	}

	public void resetContext(GuidelineContext[] newContext) {
		detailPanel.removeContextChangeListener(this);
		try {
			detailPanel.getContextHolder().setContextElemens(newContext);
		}
		finally {
			detailPanel.addContextChangeListener(this);
		}
	}

	public boolean hasUnsavedChanges() {
		return gridPanel.hasUnsavedChanges();
	}

	public void saveChanges() throws CanceledException, ServerException {
		gridPanel.saveChanges();
	}

	public void discardChanges() {
		gridPanel.discardChanges();
	}

	public void contextChanged(ContextChangeEvent e) {
		templatePanel.clearTemplates();
	}

	public final void showGridManagementPanel() {
		CardLayout cl = (CardLayout) getLayout();
		cl.show(this, LAYOUT_KEY_MANAGEMENT);
	}

	public final void showGridSelectionPanel() {
		CardLayout cl = (CardLayout) getLayout();
		cl.show(this, LAYOUT_KEY_SELECTION);
		// re-load template list to refresh
		if (templatePanel != null) templatePanel.reloadTemplates();
	}

	public void populate(GuidelineContext[] contexts, List<ProductGrid> list, GridTemplate gridTemplate, boolean isSubcontext, AbstractGuidelineGrid currentGrid) {
		gridPanel.populate(contexts, list, gridTemplate, isSubcontext, currentGrid);
	}

	public void setViewOnly(boolean flag) {
		gridPanel.setViewOnly(flag);
	}

	public TemplateSelectionPanel getTemplatePanel() {
		return templatePanel;
	}

	public void checkTemplateExistence() {
		GridTemplate template = gridPanel.getTemplate();
		if (template != null && !EntityModelCacheFactory.getInstance().getAllGuidelineTemplates().contains(template)) showGridSelectionPanel();
	}

	public void entityDeleted(EntityDeleteEvent e) {
		if (detailPanel.isVisible()) {
			detailPanel.getContextHolder().removeContext(new GenericEntity[] { e.getEntity() });
		}
		if (gridPanel.isVisible()) {
			gridPanel.getContextHolder().removeContext(new GenericEntity[] { e.getEntity() });
		}
	}
}