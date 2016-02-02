package com.mindbox.pe.client.common.filter.panel;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.swing.Box;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSpinner;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.mindbox.pe.client.ClientUtil;
import com.mindbox.pe.client.EntityModelCacheFactory;
import com.mindbox.pe.client.MainPanel;
import com.mindbox.pe.client.applet.PowerEditorSwingTheme;
import com.mindbox.pe.client.applet.UIFactory;
import com.mindbox.pe.client.common.AbstractThreadedActionAdapter;
import com.mindbox.pe.client.common.AttributeReferenceSelectField;
import com.mindbox.pe.client.common.CheckList;
import com.mindbox.pe.client.common.DateSelectorComboField;
import com.mindbox.pe.client.common.NumberTextField;
import com.mindbox.pe.client.common.PanelBase;
import com.mindbox.pe.client.common.StatusSelectorComboField;
import com.mindbox.pe.client.common.TypeEnumCheckList;
import com.mindbox.pe.client.common.context.GuidelineContextPanel;
import com.mindbox.pe.client.common.dialog.MDateDateField;
import com.mindbox.pe.client.common.selection.GuidelineTypeTemplateCheckBoxSelectionPanel;
import com.mindbox.pe.common.UtilBase;
import com.mindbox.pe.common.config.GuidelineTabConfig;
import com.mindbox.pe.common.config.UIConfiguration;
import com.mindbox.pe.model.Constants;
import com.mindbox.pe.model.GridTemplate;
import com.mindbox.pe.model.GuidelineContext;
import com.mindbox.pe.model.ParameterTemplate;
import com.mindbox.pe.model.TemplateUsageType;
import com.mindbox.pe.model.TypeEnumValue;
import com.mindbox.pe.model.filter.GuidelineReportFilter;

/**
 * Guideline search panel.
 * @author MindBox
 */
public final class GuidelineFilterPanel extends PanelBase {

	private static final long serialVersionUID = 4489985693297838626L;
	
	private final class StatusAndAboveRadioItemL implements ItemListener {
		@Override
		public void itemStateChanged(ItemEvent e) {
			if (e.getStateChange() == ItemEvent.SELECTED) {
				statusComboBox.setEnabled(true);
			}
			else {
				statusComboBox.setEnabled(false);
			}
		}
	}
	
	private final class StatusOneOrMoreRadioItemL implements ItemListener {
		@Override
		public void itemStateChanged(ItemEvent e) {
			if (e.getStateChange() == ItemEvent.SELECTED) {
				statusCheckList.setEnabled(true);
			}
			else {
				statusCheckList.setEnabled(false);
			}
		}
	}

	private class ExpDateLimitSpinnerL implements ChangeListener {

		public void stateChanged(ChangeEvent event) {
			Date now = new Date();
			expDate.setTime(now.getTime() - (((Integer) expDateLimitSpinner.getValue()).longValue() * UIConfiguration.DAY_ADJUSTMENT));
			expDateLabel.setText(UIConfiguration.FORMAT_DATE.format(expDate));
		}
	}

	private final class ExpDateRadioL extends AbstractThreadedActionAdapter {

		public void performAction(ActionEvent e) {
			if (((JRadioButton) e.getSource()).isSelected()) {
				setEnabledExpDatePanel(true);
				actDateField.setEnabled(false);
				changesOnDateField.setEnabled(false);
			}
		}
	}

	private final class ActiveOnDateRadioL extends AbstractThreadedActionAdapter {

		public void performAction(ActionEvent e) {
			if (((JRadioButton) e.getSource()).isSelected()) {
				setEnabledExpDatePanel(false);
				actDateField.setEnabled(true);
				changesOnDateField.setEnabled(false);
			}
		}
	}

	private final class ChangesOnDateRadioL extends AbstractThreadedActionAdapter {

		public void performAction(ActionEvent e) {
			if (((JRadioButton) e.getSource()).isSelected()) {
				setEnabledExpDatePanel(false);
				actDateField.setEnabled(false);
				changesOnDateField.setEnabled(true);
			}
		}
	}

	private GuidelineReportFilter getGuidelineReportFilter(String descPrefix) {
		filter = new GuidelineReportFilter();

		StringBuffer buff = new StringBuffer("<html>");
		buff.append(" <font color='#4848ca'>");
		buff.append(ClientUtil.getInstance().getLabel("label.search.criteria"));
		buff.append("</font>: ");

		boolean isBeginning = true;

		// handle prefix
		if (!UtilBase.isEmpty(descPrefix)) {
			isBeginning = false;
			buff.append(descPrefix);
			buff.append("; ");
		}

		// handle filter policies expired before specified number of days
		if (expDateRadio.isSelected() && expDateLimitSpinner.getValue() != null) {
			isBeginning = false;
			int daysAgo = ((Integer) expDateLimitSpinner.getValue()).intValue();
			filter.setDaysAgo(daysAgo);

			if (!isBeginning) {
				buff.append("; ");
			}
			buff.append("Filter policies expired before ");
			buff.append(daysAgo);
			isBeginning = false;
		}

		// handle activation date
		if (activeOnDateRadio.isSelected() && actDateField.getDate() != null) {
			isBeginning = false;
			try {
				filter.setActiveDate(actDateField.getDate());
				buff.append("Active on ");
				buff.append(actDateField.getValue());
			}
			catch (Exception ex) {
				ClientUtil.getInstance().showWarning("msg.warning.invalid.date", new Object[] { "MM/dd/yyyy" });
				return null;
			}
		}

		// handle changes-on date
		if (changesOnDateRadio.isSelected() && changesOnDateField.getDate() != null) {
			isBeginning = false;
			try {
				filter.setChangesOnDate(changesOnDateField.getDate());
				if (!isBeginning) {
					buff.append("; ");
				}
				buff.append("Changes on ");
				buff.append(changesOnDateField.getValue());
				isBeginning = false;
			}
			catch (Exception ex) {
				ClientUtil.getInstance().showWarning("msg.warning.invalid.date", new Object[] { "MM/dd/yyyy" });
				return null;
			}
		}

		filter.setIncludeEmptyContexts(guidelineContextPanel.includeEmptyContexts());
		filter.setIncludeChildrenCategories(guidelineContextPanel.includeChildrenCategories());
		filter.setIncludeParentCategories(guidelineContextPanel.includeParentCategories());
		filter.setSearchInColumnData(guidelineContextPanel.searchInColumnCheckbox());

		// handle context
		GuidelineContext[] contexts = guidelineContextPanel.getGuidelineContexts();
		if (contexts != null) {
			for (int i = 0; i < contexts.length; i++) {
				filter.addContext(contexts[i]);
			}
		}

		// handle attribute and value
		if (attributeField.hasValue()) {
			if (!isBeginning) {
				buff.append("; ");
			}
			isBeginning = false;
			buff.append(attributeField.getValue());
			buff.append(" = ");

			String[] strs = attributeField.getValue().split("\\.");
			assert (strs.length > 1);
			filter.setClassName(strs[0]);
			filter.setAttributeName(strs[1]);

			if (valueField.getText() != null) {
				buff.append(valueField.getText());
				filter.setValue(valueField.getText().trim());
			}
			else {
				buff.append(ClientUtil.getInstance().getLabel("label.value.any"));
			}
		}

		// handle status
		if (statusAndAboveRadio.isSelected()) {
			String value = statusComboBox.getSelectedEnumValueValue();
			if (value != null && value.trim().length() != 0) {

				filter.setThisStatusAndAbove(value);

				if (!isBeginning) {
					buff.append("; ");
				}
				buff.append("Status this and above: ");
				buff.append(value);
				isBeginning = false;
			}

		}
		else {
			Object[] values = statusCheckList.getSelectedValues();
			if (values != null && values.length > 0) {
				if (!isBeginning) {
					buff.append("; ");
				}
				buff.append("Status select one or more: ");
				isBeginning = false;
				for (int i = 0; i < values.length; i++) {
					if (i > 0) buff.append(",");
					if (values[i] instanceof TypeEnumValue) {
						filter.addStatus(((TypeEnumValue) values[i]).getValue());
						buff.append(((TypeEnumValue) values[i]).getDisplayLabel());
					}
				}
			}
		}

		// handle rule id
		Long ruleID = ruleIDField.getLongValue();
		if (ruleID != null && ruleID.longValue() > 0L) {
			if (!isBeginning) {
				buff.append("; ");
			}
			isBeginning = false;
			buff.append(ClientUtil.getInstance().getLabel("label.rule.id"));
			buff.append(": ");
			buff.append(ruleID);
			filter.setRuleID(ruleID);
		}

		// handle templates & usage types
		List<GuidelineTabConfig> usageGroupList = usageTypeTemplateSelectionPanel.getSelectedUsageGroups();
		List<TemplateUsageType> usageTypeList = usageTypeTemplateSelectionPanel.getSelectedUsageTypes();
		List<GridTemplate> templateList = usageTypeTemplateSelectionPanel.getSelectedTemplates();
		if (!usageGroupList.isEmpty()) {
			if (!isBeginning) {
				buff.append("; ");
			}
			isBeginning = false;
			buff.append(ClientUtil.getInstance().getLabel("label.guideline.type.group"));
			buff.append(": ");
			for (Iterator<GuidelineTabConfig> iterator = usageGroupList.iterator(); iterator.hasNext();) {
				GuidelineTabConfig tabConfig = iterator.next();
				filter.addAllUsageTypes(tabConfig.getUsageTypes());
				buff.append(tabConfig.getTitle());
				if (iterator.hasNext()) {
					buff.append(",");
				}
			}
		}
		if (!usageTypeList.isEmpty()) {
			if (!isBeginning) {
				buff.append("; ");
			}
			isBeginning = false;
			buff.append(ClientUtil.getInstance().getLabel("label.guideline.type"));
			buff.append(": ");
			for (Iterator<TemplateUsageType> iterator = usageTypeList.iterator(); iterator.hasNext();) {
				TemplateUsageType usageType = iterator.next();
				filter.addUsageType(usageType);
				buff.append(usageType.getDisplayName());
				if (iterator.hasNext()) {
					buff.append(",");
				}
			}
		}
		if (!templateList.isEmpty()) {
			if (!isBeginning) {
				buff.append("; ");
			}
			isBeginning = false;
			buff.append(ClientUtil.getInstance().getLabel("label.template"));
			buff.append(": ");
			for (Iterator<GridTemplate> iterator = templateList.iterator(); iterator.hasNext();) {
				GridTemplate template = iterator.next();
				filter.addGuidelineTemplateID(new Integer(template.getID()));
				buff.append(template.getName());
				if (iterator.hasNext()) {
					buff.append(",");
				}
			}
		}

		// handle parameter selection
		if (templateTabPane.isEnabledAt(1)) {
			Object[] selection = paramTemplateCheckList.getSelectedValues();			
			if (selection != null && selection.length > 0) {
				if (!isBeginning) {
					buff.append("; ");
				}
				isBeginning = false;
				buff.append(ClientUtil.getInstance().getLabel("label.template.parameter"));
				buff.append(": ");
				
				for (int i = 0; i < selection.length; i++) {
					ParameterTemplate template = (ParameterTemplate)selection[i];
					filter.addParameterTemplateID(new Integer(template.getID()));
					buff.append(template.getName());
					if (i+1 < selection.length) {
						buff.append(",");
					}
				}
			}	
		}

		buff.append("</html>");
		filter.setSearchDescription(buff.toString());
		return filter;
	}

	private int getDefaultExpDays() {
		return ((MainPanel) ClientUtil.getParent()).getUserSession().getDefaultExpirationDays();
	}

	private final MDateDateField actDateField;
	private final DateSelectorComboField changesOnDateField;
	private final GuidelineContextPanel guidelineContextPanel;
	private final GuidelineTypeTemplateCheckBoxSelectionPanel usageTypeTemplateSelectionPanel;
	private final JTextField valueField;
	private final AttributeReferenceSelectField attributeField;
	private GuidelineReportFilter filter;
	private final JTabbedPane selectionTab;
	private final JButton clearCriteriaButton;
	private final TypeEnumCheckList statusCheckList;
	private final NumberTextField ruleIDField;

	private final StatusSelectorComboField statusComboBox;
	private final JLabel expDateDaysAgoLabel;
	private final JSpinner expDateLimitSpinner;
	private JLabel expDateLabel; // The date: changes every time the spinner changes.
	private Date expDate;
	private List<Component> enabledGuidelineFilterPanelComponetList = null;
	private List<Component> enabledParameterFilterTabComponetList = null;
	private List<Component> enabledGuidelineFilterTabComponetList = null;
	private List<Component> excludeComponetList = new ArrayList<Component>();
	private boolean allowDisablingPanel = true;

	private JPanel dateSelectionTogglePanel;
	private JPanel attributeStatusTogglePanel;
	private JPanel contextTogglePanel;
	private JPanel templateTogglePanel;
	private JRadioButton statusAndAboveRadio;
	private JRadioButton statusOneOrMoreRadio;
	private JRadioButton expDateRadio;
	private JRadioButton activeOnDateRadio;
	private JRadioButton changesOnDateRadio;
	private JLabel attributeFieldLabel, attributeValueFieldLabel, attributeStatusTogglePanelLable;
	private final CheckList paramTemplateCheckList;
	private final JPanel parameterSelectionPanel;
	private JTabbedPane templateTabPane;

	public GuidelineFilterPanel(boolean showClearButton) {
		super();
		this.guidelineContextPanel = new GuidelineContextPanel("button.select.entities", true, true, true);
		selectionTab = new JTabbedPane();
		selectionTab.setFont(PowerEditorSwingTheme.smallTabFont);
		filter = null;
		if (showClearButton) {
			clearCriteriaButton = UIFactory.createButton(
					ClientUtil.getInstance().getLabel("button.clear.criteria"),
					null,
					new ActionListener() {
						public void actionPerformed(ActionEvent e) {
							clearSelectionCriteria();
						}
					},
					null);
		}
		else {
			clearCriteriaButton = null;
		}

		statusComboBox = new StatusSelectorComboField(false);
		expDateDaysAgoLabel = UIFactory.createFormLabel("label.expire.days.ago");
		int defaultDays = getDefaultExpDays();
		SpinnerModel numberModel = new SpinnerNumberModel(new Integer(defaultDays), new Integer(0), null, new Integer(1));
		expDateLimitSpinner = new JSpinner(numberModel);

		expDate = new Date();
		long delta = (long) defaultDays * UIConfiguration.DAY_ADJUSTMENT;
		expDate.setTime(expDate.getTime() - delta);
		expDateLabel = new JLabel(UIConfiguration.FORMAT_DATE.format(expDate));

		this.statusCheckList = new TypeEnumCheckList(TypeEnumValue.TYPE_STATUS, false);

		actDateField = new MDateDateField(true, true, true);
		actDateField.setValue(null);
		changesOnDateField = new DateSelectorComboField(false, true, true);
		changesOnDateField.setValue(null);
		valueField = new JTextField(10);
		attributeField = new AttributeReferenceSelectField();
		ruleIDField = new NumberTextField(12);
		usageTypeTemplateSelectionPanel = new GuidelineTypeTemplateCheckBoxSelectionPanel(true);
		usageTypeTemplateSelectionPanel.setMinimumSize(new Dimension(40, 20));
		usageTypeTemplateSelectionPanel.setPreferredSize(new Dimension(usageTypeTemplateSelectionPanel.getPreferredSize().width, 100));

		paramTemplateCheckList = new CheckList();
		paramTemplateCheckList.setModel(EntityModelCacheFactory.getInstance().getParameterTemplateComboModel(false));
		parameterSelectionPanel = createParameterSelectionPanel();
		parameterSelectionPanel.setMinimumSize(new Dimension(40, 20));
		parameterSelectionPanel.setPreferredSize(new Dimension(parameterSelectionPanel.getPreferredSize().width, 100));
		
		excludeComponetList.add(usageTypeTemplateSelectionPanel);
		excludeComponetList.add(parameterSelectionPanel);

		initPanel();
	}

	public void clearSelectionCriteria() {
		actDateField.setValue(null);
		changesOnDateField.setValue(null);
		attributeField.setValue(null, null);
		valueField.setText(null);
		guidelineContextPanel.clearContext();
		guidelineContextPanel.clearSearchOptions();
		usageTypeTemplateSelectionPanel.clearSelection();
		statusCheckList.clearSelection();
		ruleIDField.clearValue();

		statusComboBox.setSelectedStatus(Constants.DRAFT_STATUS);
		setExpDateLimitSpinnerDefaultDays();
		paramTemplateCheckList.clearSelection();

	}

	private void setExpDateLimitSpinnerWidth() {
		JFormattedTextField textField = null;
		JComponent editor = expDateLimitSpinner.getEditor();
		if (editor instanceof JSpinner.DefaultEditor) {
			textField = ((JSpinner.DefaultEditor) editor).getTextField();
		}
		else {
			ClientUtil.getLogger().warn(
					"Unexpected editor type: " + expDateLimitSpinner.getEditor().getClass() + " isn't a descendent of DefaultEditor");
		}
		if (textField != null) {
			textField.setColumns(4);
		}
	}

	private void setEnabledExpDatePanel(boolean enabled) {
		expDateLimitSpinner.setEnabled(enabled);
		expDateDaysAgoLabel.setEnabled(enabled);
		expDateLabel.setEnabled(enabled);
	}

	private void setExpDateLimitSpinnerDefaultDays() {
		int defaultDays = getDefaultExpDays();
		expDateLimitSpinner.setValue(new Integer(defaultDays));
		expDate = new Date();
		long delta = (long) defaultDays * UIConfiguration.DAY_ADJUSTMENT;
		expDate.setTime(expDate.getTime() - delta);
		expDateLabel.setText(UIConfiguration.FORMAT_DATE.format(expDate));
	}

	private void initPanel() {
		GridBagConstraints c = new GridBagConstraints();
		c.anchor = GridBagConstraints.NORTH;
		c.insets = new Insets(1, 1, 1, 1);
		c.fill = GridBagConstraints.BOTH;
		c.weighty = 0.0;
		c.gridheight = 1;
		GridBagLayout bag = new GridBagLayout();

		JPanel dateSelectionPanel = UIFactory.createJPanel(bag);

		// set up the width of the field inside the spinner.
		setExpDateLimitSpinnerWidth();
		expDateLimitSpinner.addChangeListener(new ExpDateLimitSpinnerL());

		// create expired date limit spinner panel
		JPanel expDatePanel = UIFactory.createJPanel(new FlowLayout(FlowLayout.LEFT, 1, 1));
		expDatePanel.add(expDateLimitSpinner);
		expDatePanel.add(expDateDaysAgoLabel);
		expDatePanel.add(expDateLabel);

		// create date radio buttons	
		expDateRadio = UIFactory.createRaiodButton(ClientUtil.getInstance().getLabel("label.filter.policy.expired.before"));
		expDateRadio.addActionListener(new ExpDateRadioL());
		expDateRadio.setFocusable(true);
		expDateRadio.setSelected(true);
		setEnabledExpDatePanel(true);

		activeOnDateRadio = UIFactory.createRaiodButton(ClientUtil.getInstance().getLabel("label.active.on"));
		activeOnDateRadio.addActionListener(new ActiveOnDateRadioL());
		activeOnDateRadio.setFocusable(false);
		activeOnDateRadio.setSelected(false);
		actDateField.setEnabled(false);

		changesOnDateRadio = UIFactory.createRaiodButton(ClientUtil.getInstance().getLabel("label.changes.on"));
		changesOnDateRadio.addActionListener(new ChangesOnDateRadioL());
		changesOnDateRadio.setFocusable(false);
		changesOnDateRadio.setSelected(false);
		changesOnDateField.setEnabled(false);

		ButtonGroup groupDate = new ButtonGroup();
		groupDate.add(expDateRadio);
		groupDate.add(activeOnDateRadio);
		groupDate.add(changesOnDateRadio);

		// add components to date selection panel				
		c.weightx = 0.0;
		c.gridwidth = 1;
		addComponent(dateSelectionPanel, bag, c, expDateRadio);
		c.weightx = 1.0;
		c.gridwidth = GridBagConstraints.REMAINDER;
		addComponent(dateSelectionPanel, bag, c, expDatePanel);

		c.weightx = 0.0;
		c.gridwidth = 1;
		addComponent(dateSelectionPanel, bag, c, activeOnDateRadio);
		c.weightx = 1.0;
		c.gridwidth = GridBagConstraints.REMAINDER;
		addComponent(dateSelectionPanel, bag, c, actDateField);

		c.weightx = 0.0;
		c.gridwidth = 1;
		addComponent(dateSelectionPanel, bag, c, changesOnDateRadio);
		c.weightx = 1.0;
		c.gridwidth = GridBagConstraints.REMAINDER;
		addComponent(dateSelectionPanel, bag, c, changesOnDateField);

		bag = new GridBagLayout();
		JPanel attributeStatusPanel = UIFactory.createJPanel(bag);
		//add components to attribute, status, and rule ID selection panel			
		c.weightx = 0.0;
		c.gridwidth = 1;
		attributeFieldLabel = UIFactory.createFormLabel("label.attribute");
		addComponent(attributeStatusPanel, bag, c, attributeFieldLabel);
		c.weightx = 1.0;
		c.gridwidth = GridBagConstraints.REMAINDER;
		addComponent(attributeStatusPanel, bag, c, attributeField);

		c.weightx = 0.0;
		c.gridwidth = 1;
		attributeValueFieldLabel = UIFactory.createFormLabel("label.value");
		addComponent(attributeStatusPanel, bag, c, attributeValueFieldLabel);
		c.weightx = 1.0;
		c.gridwidth = GridBagConstraints.REMAINDER;
		addComponent(attributeStatusPanel, bag, c, valueField);

		// create radio status buttons	
		statusAndAboveRadio = UIFactory.createRaiodButton(ClientUtil.getInstance().getLabel("radio.status.and.above"));
		statusAndAboveRadio.addItemListener(new StatusAndAboveRadioItemL());
		statusAndAboveRadio.setFocusable(false);
		statusAndAboveRadio.setSelected(false);
		statusComboBox.setEnabled(false);
		statusComboBox.setSelectedStatus(Constants.DRAFT_STATUS);

		statusOneOrMoreRadio = UIFactory.createRaiodButton(ClientUtil.getInstance().getLabel("radio.status.one.or.more"));
		statusOneOrMoreRadio.addItemListener(new StatusOneOrMoreRadioItemL());
		statusOneOrMoreRadio.setFocusable(false);
		statusOneOrMoreRadio.setSelected(true);

		ButtonGroup groupStatus = new ButtonGroup();
		groupStatus.add(statusAndAboveRadio);
		groupStatus.add(statusOneOrMoreRadio);

		// add components to attribute, status, and other selection panel			
		c.weightx = 0.0;
		c.gridwidth = GridBagConstraints.REMAINDER;
		addComponent(attributeStatusPanel, bag, c, UIFactory.createFormLabel("label.status"));

		c.weightx = 0.0;
		c.gridwidth = 2;
		addComponent(attributeStatusPanel, bag, c, statusAndAboveRadio);
		c.weightx = 1.0;
		c.gridwidth = GridBagConstraints.REMAINDER;
		addComponent(attributeStatusPanel, bag, c, statusComboBox);

		c.weightx = 0.0;
		c.gridwidth = 2;
		addComponent(attributeStatusPanel, bag, c, statusOneOrMoreRadio);
		c.weightx = 1.0;
		c.gridwidth = GridBagConstraints.REMAINDER;
		addComponent(attributeStatusPanel, bag, c, statusCheckList);

		c.weightx = 0.0;
		c.gridwidth = 1;
		addComponent(attributeStatusPanel, bag, c, UIFactory.createFormLabel("label.rule.id"));
		c.weightx = 1.0;
		c.gridwidth = GridBagConstraints.REMAINDER;
		addComponent(attributeStatusPanel, bag, c, ruleIDField);
	
		dateSelectionTogglePanel = UIFactory.createTogglePanel(UIFactory.createLabel("label.policy.date.selection"), dateSelectionPanel, false);

		attributeStatusTogglePanelLable = UIFactory.createLabel("label.policy.attr.status.other.selection");
		attributeStatusTogglePanel = UIFactory.createTogglePanel(attributeStatusTogglePanelLable, attributeStatusPanel, false);

		contextTogglePanel = UIFactory.createTogglePanel(UIFactory.createLabel("label.context.selection"), guidelineContextPanel.getJPanel(), false);

		Box verticalBox = Box.createVerticalBox();
		if (clearCriteriaButton != null) {
			JPanel topPanel = UIFactory.createBorderLayoutPanel(0, 0);
			topPanel.add(clearCriteriaButton, BorderLayout.WEST);
			verticalBox.add(topPanel);
		}
		verticalBox.add(Box.createVerticalStrut(1));
		verticalBox.add(new JSeparator());
		verticalBox.add(Box.createVerticalStrut(2));
		verticalBox.add(dateSelectionTogglePanel);
		verticalBox.add(Box.createVerticalStrut(1));
		verticalBox.add(attributeStatusTogglePanel);
		verticalBox.add(Box.createVerticalStrut(1));
		verticalBox.add(contextTogglePanel);
		verticalBox.add(Box.createVerticalStrut(1));

		templateTabPane = new JTabbedPane();
		templateTabPane.addTab(ClientUtil.getInstance().getLabel("label.template.guideline"), usageTypeTemplateSelectionPanel);
		templateTabPane.addTab(ClientUtil.getInstance().getLabel("label.template.parameter"), parameterSelectionPanel);
		setEnabledParameterFilterTab(false);
		templateTogglePanel = UIFactory.createTogglePanel(UIFactory.createLabel("label.template.selection"), templateTabPane, false);
		
		setLayout(new BorderLayout(0, 0));
		add(verticalBox, BorderLayout.NORTH);
		add(templateTogglePanel, BorderLayout.CENTER);
	}

	private JPanel createParameterSelectionPanel() {	
		JPanel parameterSelectionPanel = UIFactory.createBorderLayoutPanel(0, 0);
		JPanel bPanel = UIFactory.createFlowLayoutPanelLeftAlignment(2, 2);
		
		JButton button = UIFactory.createButton(
				ClientUtil.getInstance().getLabel("button.select.all"),
				null,
				new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						paramTemplateCheckList.selectAll();
					}
				},
				null);
		bPanel.add(button);
			
		button = UIFactory.createButton(
				ClientUtil.getInstance().getLabel("button.clear.selection"),
				null,
				new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						paramTemplateCheckList.clearSelection();
					}
				},
				null);
		bPanel.add(button);
				
		parameterSelectionPanel.add(bPanel, BorderLayout.NORTH);
		parameterSelectionPanel.add(new JScrollPane(paramTemplateCheckList), BorderLayout.CENTER);
		
		return parameterSelectionPanel;
	}
	
	public GuidelineReportFilter getFilter(String prefix) {
		return getGuidelineReportFilter(prefix);
	}

	public void setEnabledPanel(boolean enabled) {
		if (allowDisablingPanel) {
			if (enabled && !this.isEnabled() && enabledGuidelineFilterPanelComponetList != null) {
				UIFactory.enableContainerComponents(enabledGuidelineFilterPanelComponetList);
			}
			else if (!enabled && this.isEnabled()) {
				enabledGuidelineFilterPanelComponetList = UIFactory.disableContainerComponents(this, excludeComponetList);
			}
		}
	}
	public void setEnabledGuidelineFilterTab(boolean enabled) {
		if (enabled && enabledGuidelineFilterTabComponetList != null) {
			UIFactory.enableContainerComponents(enabledGuidelineFilterTabComponetList);
			templateTabPane.setEnabledAt(0, enabled);					
		}
		else if (!enabled && usageTypeTemplateSelectionPanel.isEnabled()) {
			enabledGuidelineFilterTabComponetList = UIFactory.disableContainerComponents(usageTypeTemplateSelectionPanel);
			templateTabPane.setEnabledAt(0, enabled);					
		}
	}
	
	public void setEnabledParameterFilterTab(boolean enabled) {
		if (enabled && !parameterSelectionPanel.isEnabled() && enabledParameterFilterTabComponetList != null) {
			UIFactory.enableContainerComponents(enabledParameterFilterTabComponetList);
			templateTabPane.setEnabledAt(1, enabled);					
		}
		else if (!enabled && parameterSelectionPanel.isEnabled()) {
			enabledParameterFilterTabComponetList = UIFactory.disableContainerComponents(parameterSelectionPanel);
			templateTabPane.setEnabledAt(1, enabled);					
		}		
	}

	public void setThisStatusOrAboveStatus(String status) {
		this.statusAndAboveRadio.setSelected(true);
		this.statusComboBox.setSelectedStatus(status);
	}
	
	public void setVisibleChangesOnDateField(boolean enable) {
		changesOnDateRadio.setVisible(enable);
		changesOnDateField.setVisible(enable);		
	}
		
	public void setVisibleStatusAndAboveField(boolean visible) {
		statusAndAboveRadio.setVisible(visible);
		statusComboBox.setVisible(visible);
	}	

	public void setVisibleStatusOneOrMoreField(boolean visible) {
		statusOneOrMoreRadio.setVisible(visible);
		statusCheckList.setVisible(visible);
	}	
	
	public void setVisibleAttributeField(boolean enable) {
		attributeFieldLabel.setVisible(enable);
		attributeField.setVisible(enable);
		attributeValueFieldLabel.setVisible(enable);
		valueField.setVisible(enable);
		if (enable)
			attributeStatusTogglePanelLable.setText(ClientUtil.getInstance().getLabel("label.policy.attr.status.other.selection"));		
		else
			attributeStatusTogglePanelLable.setText(ClientUtil.getInstance().getLabel("label.policy.status.other.selection"));
	}
	
	public boolean isAllowDisablingPanel() {
		return allowDisablingPanel;
	}

	public void setAllowDisablingPanel(boolean allowDisablingPanel) {
		this.allowDisablingPanel = allowDisablingPanel;
	}
	
}