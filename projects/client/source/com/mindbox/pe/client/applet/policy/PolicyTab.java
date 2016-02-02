package com.mindbox.pe.client.applet.policy;

import com.mindbox.pe.client.ClientUtil;
import com.mindbox.pe.client.applet.PowerEditorSwingTheme;
import com.mindbox.pe.client.applet.guidelines.GuidelinesTab;
import com.mindbox.pe.client.applet.guidelines.search.SearchGuidelinesTab;
import com.mindbox.pe.client.applet.parameters.ParameterManagerTab;
import com.mindbox.pe.client.applet.template.TemplateManagementTab;
import com.mindbox.pe.client.common.tab.PowerEditorTab;
import com.mindbox.pe.common.PrivilegeConstants;
import com.mindbox.pe.common.config.FeatureConfiguration;
import com.mindbox.pe.common.config.GuidelineTabConfig;
import com.mindbox.pe.communication.ServerException;

/**
 * Guideline Management Tab.
 * @author Geneho Kim
 * @author MindBox
 */
public class PolicyTab extends PowerEditorTab {

	public PolicyTab(GuidelineTabConfig[] tabConfigs, TemplateManagementTab templateManagementTab, boolean readOnly) throws ServerException {
		super();
		setFont(PowerEditorSwingTheme.tabFont);
		setFocusable(false);

		addTab(
				ClientUtil.getInstance().getLabel("tab.policies.search"),
				ClientUtil.getInstance().makeImageIcon("image.blank"),
				new SearchGuidelinesTab(readOnly),
				ClientUtil.getInstance().getLabel("tab.tooltip.policies.search"));

		addTab(
				ClientUtil.getInstance().getLabel("tab.guidelines"),
				ClientUtil.getInstance().makeImageIcon("image.blank"),
				new GuidelinesTab(tabConfigs, templateManagementTab, readOnly),
				ClientUtil.getInstance().getLabel("tab.tooltip.guidelines"));

		if (ClientUtil.getUserSession().getFeatureConfiguration().isFeatureEnabled(FeatureConfiguration.PARAMETER_FEATURE)) {
			if (ClientUtil.checkPermissionByPrivilegeName(PrivilegeConstants.PRIV_MANAGE_PARAMETERS)) {
				addTab(
						ClientUtil.getInstance().getLabel("tab.parameters"),
						ClientUtil.getInstance().makeImageIcon("image.blank.tab"),
						new ParameterManagerTab(readOnly),
						ClientUtil.getInstance().getLabel("tab.tooltip.parameters"));
			}
		}
	}
}