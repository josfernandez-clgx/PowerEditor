package com.mindbox.pe.client.common.filter.panel;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.Box;
import javax.swing.JCheckBox;
import javax.swing.JPanel;

import com.mindbox.pe.client.ClientUtil;
import com.mindbox.pe.client.applet.UIFactory;
import com.mindbox.pe.client.common.AbstractThreadedActionAdapter;
import com.mindbox.pe.client.common.PanelBase;
import com.mindbox.pe.model.filter.GuidelineReportFilter;
import com.mindbox.pe.xsd.config.FeatureNameType;

/**
 * Guideline search panel.
 * @author MindBox
 */
public final class DataFilterPanel extends PanelBase {

	private final class EntityCheckBoxItemL implements ItemListener {
		@Override
		public void itemStateChanged(ItemEvent e) {
			if (dateSynonymCheckBox.isVisible()) {
				if (e.getStateChange() == ItemEvent.SELECTED) {
					dateSynonymCheckBox.setEnabled(false);
					dateSynonymCheckBox.setSelected(true);
				}
				else if (!guidelineCheckBox.isSelected()) {
					dateSynonymCheckBox.setEnabled(true);
					dateSynonymCheckBox.setSelected(false);
				}
			}
		}
	}

	private final class GuidelineCheckBoxItemL implements ItemListener {
		@Override
		public void itemStateChanged(ItemEvent e) {
			if (dateSynonymCheckBox.isVisible()) {
				if (e.getStateChange() == ItemEvent.SELECTED) {
					dateSynonymCheckBox.setEnabled(false);
					dateSynonymCheckBox.setSelected(true);
				}
				else if (!entitiesCheckBox.isSelected()) {
					dateSynonymCheckBox.setEnabled(true);
					dateSynonymCheckBox.setSelected(false);
				}
			}

			if (guidelineFilterPanel != null) {
				if (e.getStateChange() == ItemEvent.SELECTED) {
					guidelineFilterPanel.setEnabledGuidelineFilterTab(true);
				}
				else if (!(templateCheckBoxField.isVisible() && templateCheckBox.isSelected())) {
					guidelineFilterPanel.setEnabledGuidelineFilterTab(false);
				}
			}
		}
	}
	private final class ParameterCheckBoxItemL implements ItemListener {
		@Override
		public void itemStateChanged(ItemEvent e) {
			if (guidelineFilterPanel != null) {
				if (e.getStateChange() == ItemEvent.SELECTED) {
					guidelineFilterPanel.setEnabledParameterFilterTab(true);
				}
				else {
					guidelineFilterPanel.setEnabledParameterFilterTab(false);
				}
			}
		}
	}
	private final class PoliciesCheckBoxL extends AbstractThreadedActionAdapter {
		public void performAction(ActionEvent e) {
			if (((JCheckBox) e.getSource()).isSelected()) {
				if (guidelineFilterPanel != null) guidelineFilterPanel.setEnabledPanel(true);

				if (guidelineCheckBoxField.isVisible()) guidelineCheckBox.setSelected(true);
				if (templateCheckBoxField.isVisible()) templateCheckBox.setSelected(true);
				if (hasParameterFeature && parameterCheckBoxField.isVisible()) {
					parameterCheckBox.setSelected(true);
				}

			}
			else {
				if (guidelineFilterPanel != null) guidelineFilterPanel.setEnabledPanel(false);

				if (guidelineCheckBoxField.isVisible()) guidelineCheckBox.setSelected(false);
				if (templateCheckBoxField.isVisible()) templateCheckBox.setSelected(false);
				if (hasParameterFeature && parameterCheckBoxField.isVisible()) {
					parameterCheckBox.setSelected(false);
				}
			}

		}
	}
	
	private final class PoliciesSubsSectionCheckBoxL extends AbstractThreadedActionAdapter {
		public void performAction(ActionEvent e) {
			if (((JCheckBox) e.getSource()).isSelected()) {
				policiesCheckBox.setSelected(true);
				if (guidelineFilterPanel != null) guidelineFilterPanel.setEnabledPanel(true);
			}
			else if (!(guidelineCheckBoxField.isVisible() && guidelineCheckBox.isSelected()) && !(templateCheckBoxField.isVisible() && templateCheckBox.isSelected())
					&& (!hasParameterFeature || !(parameterCheckBoxField.isVisible() && parameterCheckBox.isSelected()))) {
				policiesCheckBox.setSelected(false);
				if (guidelineFilterPanel != null) guidelineFilterPanel.setEnabledPanel(false);
			}
		}
	}
	
	private final class TemplateCheckBoxItemL implements ItemListener {
		@Override
		public void itemStateChanged(ItemEvent e) {
			if (guidelineFilterPanel != null) {
				if (e.getStateChange() == ItemEvent.SELECTED) {
					guidelineFilterPanel.setEnabledGuidelineFilterTab(true);
				}
				else if (!(guidelineCheckBoxField.isVisible() && guidelineCheckBox.isSelected())) {
					guidelineFilterPanel.setEnabledGuidelineFilterTab(false);
				}
			}
		}
	}
	
	
	private static final long serialVersionUID = 4489985693297838626L;
	
	private final JCheckBox policiesCheckBox, guidelineCheckBox, templateCheckBox, parameterCheckBox;
	private final JCheckBox entitiesCheckBox, guidelineActionCheckBox, testConditionCheckBox, dateSynonymCheckBox;
	private final JCheckBox securityCheckBox, cbrCheckBox, processDataCheckBox;
	private boolean hasParameterFeature = false;
	private boolean hasCbrFeature = false;
	private boolean hasProcessDataFeature = false;
	private GuidelineReportFilter filter = null;
	private final GuidelineFilterPanel guidelineFilterPanel;
	private JPanel guidelineCheckBoxField, parameterCheckBoxField, templateCheckBoxField;

	public DataFilterPanel(GuidelineFilterPanel guidelineFilterPanel, boolean useBorder) {
		super();

		this.guidelineFilterPanel = guidelineFilterPanel;
		// only enable when PoliciesCheckBox or any of its sub-items are selected.
		if (guidelineFilterPanel != null) {
			guidelineFilterPanel.setEnabledPanel(false);
		}

		policiesCheckBox = UIFactory.createCheckBox("checkbox.data.filter.policies");
		policiesCheckBox.addActionListener(new PoliciesCheckBoxL());

		guidelineCheckBox = UIFactory.createCheckBox("checkbox.data.filter.guideline");
		guidelineCheckBox.addActionListener(new PoliciesSubsSectionCheckBoxL());
		guidelineCheckBox.addItemListener(new GuidelineCheckBoxItemL());

		templateCheckBox = UIFactory.createCheckBox("checkbox.data.filter.template");
		templateCheckBox.addActionListener(new PoliciesSubsSectionCheckBoxL());
		templateCheckBox.addItemListener(new TemplateCheckBoxItemL());

		parameterCheckBox = UIFactory.createCheckBox("checkbox.data.filter.parameter");
		parameterCheckBox.addActionListener(new PoliciesSubsSectionCheckBoxL());
		parameterCheckBox.addItemListener(new ParameterCheckBoxItemL());

		entitiesCheckBox = UIFactory.createCheckBox("checkbox.data.filter.entity");
		entitiesCheckBox.addItemListener(new EntityCheckBoxItemL());

		guidelineActionCheckBox = UIFactory.createCheckBox("checkbox.data.filter.action");
		testConditionCheckBox = UIFactory.createCheckBox("checkbox.data.filter.test");
		dateSynonymCheckBox = UIFactory.createCheckBox("checkbox.data.filter.dateSynonym");
		securityCheckBox = UIFactory.createCheckBox("checkbox.data.filter.security");
		cbrCheckBox = UIFactory.createCheckBox("checkbox.data.filter.cbr");
		processDataCheckBox = UIFactory.createCheckBox("checkbox.data.filter.processData");

		initPanel(useBorder);
	}


	public void clearSelectionCriteria() {
		setAllSelectionCriteria(false);
	}

	public JCheckBox getCbrCheckBox() {
		return cbrCheckBox;
	}

	public JCheckBox getDateSynonymCheckBox() {
		return dateSynonymCheckBox;
	}

	public JCheckBox getEntitiesCheckBox() {
		return entitiesCheckBox;
	}

	public GuidelineReportFilter getFilter() {
		return getGuidelineReportFilter();
	}

	public JCheckBox getGuidelineActionCheckBox() {
		return guidelineActionCheckBox;
	}

	public JCheckBox getGuidelineCheckBox() {
		return guidelineCheckBox;
	}

	public JPanel getGuidelineCheckBoxField() {
		return guidelineCheckBoxField;
	}

	private GuidelineReportFilter getGuidelineReportFilter() {
		// use guideline filter if there is one
		if (guidelineFilterPanel != null && guidelineFilterPanel.isEnabled() && guidelineFilterPanel.isVisible())
			filter = guidelineFilterPanel.getFilter(null);
		else
			filter = new GuidelineReportFilter();

		filter.setIncludeGuidelines(guidelineCheckBox.isSelected());
		filter.setIncludeTemplates(templateCheckBox.isSelected());
		if (hasParameterFeature) filter.setIncludeParameters(parameterCheckBox.isSelected());

		filter.setIncludeEntities(entitiesCheckBox.isSelected());
		filter.setIncludeGuidelineActions(guidelineActionCheckBox.isSelected());
		filter.setIncludeTestConditions(testConditionCheckBox.isSelected());
		filter.setIncludeDateSynonyms(dateSynonymCheckBox.isSelected());

		filter.setIncludeSecurityData(securityCheckBox.isSelected());
		if (hasCbrFeature) filter.setIncludeCBR(cbrCheckBox.isSelected());
		if (hasProcessDataFeature) filter.setIncludeProcessData(processDataCheckBox.isSelected());

		return filter;
	}

	public JCheckBox getParameterCheckBox() {
		return parameterCheckBox;
	}

	public JPanel getParameterCheckBoxField() {
		return parameterCheckBoxField;
	}

	public JCheckBox getPoliciesCheckBox() {
		return policiesCheckBox;
	}

	public JCheckBox getProcessDataCheckBox() {
		return processDataCheckBox;
	}

	public JCheckBox getSecurityCheckBox() {
		return securityCheckBox;
	}

	public JCheckBox getTemplateCheckBox() {
		return templateCheckBox;
	}

	public JPanel getTemplateCheckBoxField() {
		return templateCheckBoxField;
	}

	public JCheckBox getTestConditionCheckBox() {
		return testConditionCheckBox;
	}

	public boolean hasSelection() {
		GuidelineReportFilter filter = getFilter();
		if (filter.isIncludeGuidelines() || filter.isIncludeTemplates() || filter.isIncludeParameters() || filter.isIncludeEntities() || filter.isIncludeGuidelineActions()
				|| filter.isIncludeTestConditions() || filter.isIncludeDateSynonyms() || filter.isIncludeSecurityData() || filter.isIncludeCBR() || filter.isIncludeProcessData())
			return true;
		else
			return false;
	}

	private void initPanel(boolean useBorder) {
		int strutWidth = (useBorder ? 20 : 1);
		hasParameterFeature = ClientUtil.isFeatureEnabled(FeatureNameType.PARAMETER);
		hasCbrFeature = ClientUtil.isFeatureEnabled(FeatureNameType.CBR);
		hasProcessDataFeature = ClientUtil.isFeatureEnabled(FeatureNameType.PHASE);

		GridBagLayout bag = new GridBagLayout();
		setLayout(bag);
		if (useBorder) {
			setBorder(UIFactory.createTitledBorder(ClientUtil.getInstance().getLabel("label.data.filter.selection")));
		}
		GridBagConstraints c = new GridBagConstraints();
		c.anchor = GridBagConstraints.NORTH;
		c.insets = new Insets(1, 1, 1, 0);
		c.fill = GridBagConstraints.BOTH;
		c.weighty = 0.0;
		c.gridheight = 1;

		// set the panel minimum width			
		c.weightx = 1.0;
		c.gridwidth = GridBagConstraints.REMAINDER;
		if (useBorder) {
			addComponent(this, bag, c, Box.createHorizontalStrut(130));
		}
		// add policy components			
		c.weightx = 1.0;
		c.gridwidth = GridBagConstraints.REMAINDER;
		addComponent(this, bag, c, policiesCheckBox);

		guidelineCheckBoxField = UIFactory.createFlowLayoutPanelLeftAlignment(1, 1);
		guidelineCheckBoxField.add(Box.createHorizontalStrut(strutWidth));
		guidelineCheckBoxField.add(guidelineCheckBox);
		c.weightx = 1.0;
		c.gridwidth = GridBagConstraints.REMAINDER;
		addComponent(this, bag, c, guidelineCheckBoxField);

		templateCheckBoxField = UIFactory.createFlowLayoutPanelLeftAlignment(1, 1);
		templateCheckBoxField.add(Box.createHorizontalStrut(strutWidth));
		templateCheckBoxField.add(templateCheckBox);
		c.weightx = 1.0;
		c.gridwidth = GridBagConstraints.REMAINDER;
		addComponent(this, bag, c, templateCheckBoxField);

		if (hasParameterFeature) {
			parameterCheckBoxField = UIFactory.createFlowLayoutPanelLeftAlignment(1, 1);
			parameterCheckBoxField.add(Box.createHorizontalStrut(strutWidth));
			parameterCheckBoxField.add(parameterCheckBox);
			c.weightx = 1.0;
			c.gridwidth = GridBagConstraints.REMAINDER;
			addComponent(this, bag, c, parameterCheckBoxField);
		}

		// add other components			
		c.weightx = 1.0;
		c.gridwidth = GridBagConstraints.REMAINDER;
		addComponent(this, bag, c, entitiesCheckBox);

		c.weightx = 1.0;
		c.gridwidth = GridBagConstraints.REMAINDER;
		addComponent(this, bag, c, guidelineActionCheckBox);

		c.weightx = 1.0;
		c.gridwidth = GridBagConstraints.REMAINDER;
		addComponent(this, bag, c, testConditionCheckBox);

		c.weightx = 1.0;
		c.gridwidth = GridBagConstraints.REMAINDER;
		addComponent(this, bag, c, dateSynonymCheckBox);

		c.weightx = 1.0;
		c.gridwidth = GridBagConstraints.REMAINDER;
		addComponent(this, bag, c, securityCheckBox);

		if (hasCbrFeature) {
			c.weightx = 1.0;
			c.gridwidth = GridBagConstraints.REMAINDER;
			addComponent(this, bag, c, cbrCheckBox);
		}
		if (hasProcessDataFeature) {
			c.weightx = 1.0;
			c.gridwidth = GridBagConstraints.REMAINDER;
			addComponent(this, bag, c, processDataCheckBox);
		}

		c.weighty = 1.0;
		c.gridheight = GridBagConstraints.REMAINDER;
		addComponent(this, bag, c, Box.createVerticalGlue());
	}

	public void setAllSelectionCriteria(boolean selectState) {
		if (policiesCheckBox.isVisible()) policiesCheckBox.setSelected(selectState);
		if (guidelineCheckBoxField.isVisible()) guidelineCheckBox.setSelected(selectState);
		if (templateCheckBoxField.isVisible()) templateCheckBox.setSelected(selectState);
		if (hasParameterFeature && parameterCheckBoxField.isVisible()) parameterCheckBox.setSelected(selectState);

		if (entitiesCheckBox.isVisible()) entitiesCheckBox.setSelected(selectState);
		if (guidelineActionCheckBox.isVisible()) guidelineActionCheckBox.setSelected(selectState);
		if (testConditionCheckBox.isVisible()) testConditionCheckBox.setSelected(selectState);
		if (dateSynonymCheckBox.isVisible()) dateSynonymCheckBox.setSelected(selectState);
		if (securityCheckBox.isVisible()) securityCheckBox.setSelected(selectState);

		if (hasCbrFeature && cbrCheckBox.isVisible()) cbrCheckBox.setSelected(selectState);
		if (hasProcessDataFeature && processDataCheckBox.isVisible()) processDataCheckBox.setSelected(selectState);
		guidelineFilterPanel.setEnabledPanel(selectState);
	}

	@Override
	public void setEnabled(boolean enabled) {
		super.setEnabled(enabled);

		policiesCheckBox.setEnabled(enabled);
		guidelineCheckBox.setEnabled(enabled);
		templateCheckBox.setEnabled(enabled);
		if (hasParameterFeature) parameterCheckBox.setEnabled(enabled);

		entitiesCheckBox.setEnabled(enabled);
		guidelineActionCheckBox.setEnabled(enabled);
		testConditionCheckBox.setEnabled(enabled);
		dateSynonymCheckBox.setEnabled(enabled);
		securityCheckBox.setEnabled(enabled);
		if (hasCbrFeature) cbrCheckBox.setEnabled(enabled);
		if (hasProcessDataFeature) processDataCheckBox.setEnabled(enabled);
	}

}