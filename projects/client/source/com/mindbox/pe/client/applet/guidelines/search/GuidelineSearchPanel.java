package com.mindbox.pe.client.applet.guidelines.search;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import com.mindbox.pe.client.ClientUtil;
import com.mindbox.pe.client.applet.PowerEditorSwingTheme;
import com.mindbox.pe.client.applet.UIFactory;
import com.mindbox.pe.client.common.AbstractThreadedActionAdapter;
import com.mindbox.pe.client.common.PanelBase;
import com.mindbox.pe.client.common.filter.panel.DataFilterPanel;
import com.mindbox.pe.client.common.filter.panel.GuidelineFilterPanel;
import com.mindbox.pe.common.config.FeatureConfiguration;
import com.mindbox.pe.model.filter.GuidelineReportFilter;

/**
 * Guideline search panel.
 * @author MindBox
 */
final class GuidelineSearchPanel extends PanelBase {

	private static final long serialVersionUID = 4489985693297838626L;

	private final class SearchL extends AbstractThreadedActionAdapter {

		public void performAction(ActionEvent e) {
			GuidelineReportFilter filter = getFilter();
			try {
				resultPanel.setResult(filter.getSearchDescription(), ClientUtil.getCommunicator().search(filter));
			}
			catch (Exception ex) {
				ClientUtil.handleRuntimeException(ex);
			}
		}
	}

	private final JButton searchButton, searchButton2;
	private final JButton clearCriteriaButton, clearCriteriaButton2;
	private final GuidelineListPanel resultPanel;
	private final GuidelineFilterPanel guidelineFilterPanel;
	private final SearchL searchListener;
	private final JTabbedPane selectionTab;
	private DataFilterPanel dataFilterPanel = null;

	public GuidelineSearchPanel(GuidelineListPanel resultPanel) {
		super();
		this.resultPanel = resultPanel;
		this.guidelineFilterPanel = new GuidelineFilterPanel(false);
		this.guidelineFilterPanel.setAllowDisablingPanel(false);

		// only if there is parameter feature
		if (ClientUtil.getUserSession().getFeatureConfiguration().isFeatureEnabled(FeatureConfiguration.PARAMETER_FEATURE)) {
			dataFilterPanel = new DataFilterPanel(guidelineFilterPanel, false);
			// don't show specified components
			setSpecifiedDataFilterPanelComponetsInvisible(dataFilterPanel);
		}
		setBorder(UIFactory.createTitledBorder(ClientUtil.getInstance().getLabel("label.title.guideline.search.criteria")));
		selectionTab = new JTabbedPane();
		selectionTab.setFont(PowerEditorSwingTheme.smallTabFont);
		searchListener = new SearchL();
		searchButton = UIFactory.createButton(ClientUtil.getInstance().getLabel("button.search.guideline"), null, searchListener, null);
		searchButton2 = UIFactory.createButton(ClientUtil.getInstance().getLabel("button.search.guideline"), null, searchListener, null);
		clearCriteriaButton = UIFactory.createButton(
				ClientUtil.getInstance().getLabel("button.clear.criteria"),
				null,
				new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						guidelineFilterPanel.clearSelectionCriteria();
					}
				},
				null);
		clearCriteriaButton2 = UIFactory.createButton(
				ClientUtil.getInstance().getLabel("button.clear.criteria"),
				null,
				new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						guidelineFilterPanel.clearSelectionCriteria();
					}
				},
				null);
		initPanel();
	}

	@Override
	public Dimension getPreferredSize() {
		Dimension size = super.getMinimumSize();
		Dimension sizeOfPanel = guidelineFilterPanel.getMinimumSize();
		size.setSize(Math.max(size.getWidth(), sizeOfPanel.getWidth())+8, Math.max(size.getHeight(), sizeOfPanel.getHeight())+8);
		return size;
	}
	
	private void initPanel() {
		// Layout main panel
		JPanel topPanel = UIFactory.createFlowLayoutPanelLeftAlignment(2, 2);
		topPanel.add(clearCriteriaButton);
		topPanel.add(searchButton);
		if (dataFilterPanel != null) {
			topPanel.add(dataFilterPanel);
		}
		JPanel bottomPanel = UIFactory.createFlowLayoutPanelLeftAlignment(2, 2);
		bottomPanel.add(searchButton2);
		bottomPanel.add(clearCriteriaButton2);

		setLayout(new BorderLayout(0, 0));
		add(topPanel, BorderLayout.NORTH);
		add(guidelineFilterPanel, BorderLayout.CENTER);
		add(bottomPanel, BorderLayout.SOUTH);
	}

	public void setEnabled(boolean enabled) {
		super.setEnabled(enabled);
		searchButton.setEnabled(enabled);
		searchButton2.setEnabled(enabled);
		guidelineFilterPanel.setEnabled(enabled);
	}

	private String getFilterDescriptionPrefix() {
		StringBuilder buff = new StringBuilder();
		if (dataFilterPanel != null && dataFilterPanel.getParameterCheckBox().isSelected()) {
			buff.append(ClientUtil.getInstance().getLabel("label.add.parameters"));
			buff.append(": ");
			buff.append("Yes");
		}
		return buff.toString();
	}

	public GuidelineReportFilter getFilter() {
		GuidelineReportFilter filter;
		if (dataFilterPanel == null) {
			filter = guidelineFilterPanel.getFilter(getFilterDescriptionPrefix());
		}
		else {
			// this filter also contains guidelineFilterPanel filter information
			filter = dataFilterPanel.getFilter();

			// leave this for now until other codes updated, should use filter.includeParameters
			filter.setIncludeParameters(dataFilterPanel.getParameterCheckBox().isSelected());
		}
		return filter;
	}

	final void redoSearch() {
		// preserve selection, if there was one (TT 1029)
		searchListener.actionPerformed(new ActionEvent(searchButton, 0, null));
	}

	private void setSpecifiedDataFilterPanelComponetsInvisible(DataFilterPanel idataFilterPanel) {
		// don't show these components
		idataFilterPanel.getGuidelineCheckBoxField().setVisible(false);
		idataFilterPanel.getTemplateCheckBoxField().setVisible(false);
		idataFilterPanel.getEntitiesCheckBox().setVisible(false);
		idataFilterPanel.getGuidelineActionCheckBox().setVisible(false);
		idataFilterPanel.getTestConditionCheckBox().setVisible(false);
		idataFilterPanel.getDateSynonymCheckBox().setVisible(false);
		idataFilterPanel.getSecurityCheckBox().setVisible(false);
		idataFilterPanel.getCbrCheckBox().setVisible(false);
		idataFilterPanel.getProcessDataCheckBox().setVisible(false);
		idataFilterPanel.getPoliciesCheckBox().setVisible(false);
	}
}