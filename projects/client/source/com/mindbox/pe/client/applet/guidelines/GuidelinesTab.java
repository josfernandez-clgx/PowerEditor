package com.mindbox.pe.client.applet.guidelines;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.mindbox.pe.client.ClientUtil;
import com.mindbox.pe.client.applet.PowerEditorSwingTheme;
import com.mindbox.pe.client.applet.action.ActionDetailsPanel;
import com.mindbox.pe.client.applet.action.ActionSelectionPanel;
import com.mindbox.pe.client.applet.action.FunctionFilterPanel;
import com.mindbox.pe.client.applet.action.TestDetailsPanel;
import com.mindbox.pe.client.applet.action.TestSelectionPanel;
import com.mindbox.pe.client.applet.entities.EntityManagementButtonPanel;
import com.mindbox.pe.client.applet.entities.EntityManagementPanel;
import com.mindbox.pe.client.applet.guidelines.manage.ManageGuidelineTab;
import com.mindbox.pe.client.applet.template.TemplateManagementTab;
import com.mindbox.pe.client.common.tab.PowerEditorTab;
import com.mindbox.pe.common.PrivilegeConstants;
import com.mindbox.pe.common.config.GuidelineTabConfig;
import com.mindbox.pe.communication.ServerException;
import com.mindbox.pe.model.EntityType;
import com.mindbox.pe.model.rule.ActionTypeDefinition;
import com.mindbox.pe.model.rule.TestTypeDefinition;

/**
 * Guideline Management Tab.
 * 
 * @author Geneho Kim
 * @author MindBox
 */
public class GuidelinesTab extends PowerEditorTab implements ChangeListener {

	ManageGuidelineTab mgTab;

	public GuidelinesTab(GuidelineTabConfig[] tabConfigs, TemplateManagementTab templateManagementTab, boolean readOnly)
			throws ServerException {
		super();
		setFont(PowerEditorSwingTheme.tabFont);
		setFocusable(false);
		mgTab = new ManageGuidelineTab(tabConfigs, readOnly);
		this.addChangeListener(this);

		if (ClientUtil.checkViewOrEditAnyTemplatePermission()) {
			//Manage template tab
			addTab(
					ClientUtil.getInstance().getLabel("tab.template.manage"),
					ClientUtil.getInstance().makeImageIcon("image.blank"),
					templateManagementTab,
					ClientUtil.getInstance().getLabel("tab.tooltip.template.manage"));
		}

		// manage guideline tab
		if (ClientUtil.checkViewOrEditAnyGuidelinePermission()) {
			addTab(
					ClientUtil.getInstance().getLabel("tab.guidelines.manage"),
					ClientUtil.getInstance().makeImageIcon("image.blank"),
					mgTab,
					ClientUtil.getInstance().getLabel("tab.tooltip.guidelines.manage"));
		}

		// manage guideline actions tab
		if (ClientUtil.checkPermissionByPrivilegeName(PrivilegeConstants.PRIV_MANAGE_GUIDELINE_ACTIONS)) {

			ActionDetailsPanel detailPanel = new ActionDetailsPanel();
			ActionSelectionPanel selectionPanel = new ActionSelectionPanel(detailPanel, readOnly);
			addTab(
					ClientUtil.getInstance().getLabel("tab.guideline.actions"),
					ClientUtil.getInstance().makeImageIcon("image.blank"),
					new EntityManagementPanel<ActionTypeDefinition, EntityManagementButtonPanel<ActionTypeDefinition>>(
							EntityType.GUIDELINE_ACTION,
							new FunctionFilterPanel<ActionTypeDefinition>(selectionPanel, EntityType.GUIDELINE_ACTION),
							selectionPanel,
							detailPanel),
					ClientUtil.getInstance().getLabel("tab.tooltip.guideline.actions"));
		}

		// manage guideline test conditions tab
		if (ClientUtil.checkPermissionByPrivilegeName(PrivilegeConstants.PRIV_MANAGE_GUIDELINE_ACTIONS)) {
			TestDetailsPanel detailPanel = new TestDetailsPanel();
			TestSelectionPanel selectionPanel = new TestSelectionPanel(detailPanel, readOnly);
			addTab(
					ClientUtil.getInstance().getLabel("tab.guideline.tests"),
					ClientUtil.getInstance().makeImageIcon("image.blank"),
					new EntityManagementPanel<TestTypeDefinition, EntityManagementButtonPanel<TestTypeDefinition>>(
							EntityType.GUIDELINE_TEST_CONDITION,
							new FunctionFilterPanel<TestTypeDefinition>(selectionPanel, EntityType.GUIDELINE_TEST_CONDITION),
							selectionPanel,
							detailPanel),
					ClientUtil.getInstance().getLabel("tab.tooltip.guideline.tests"));
		}

	}

	public void stateChanged(ChangeEvent arg0) {
		if (this.getSelectedComponent() instanceof ManageGuidelineTab) ((ManageGuidelineTab) mgTab).checkTemplateExistence();
	}
}