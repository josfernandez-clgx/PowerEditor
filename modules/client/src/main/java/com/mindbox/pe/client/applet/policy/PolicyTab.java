package com.mindbox.pe.client.applet.policy;

import java.util.List;

import com.mindbox.pe.client.ClientUtil;
import com.mindbox.pe.client.applet.PowerEditorSwingTheme;
import com.mindbox.pe.client.applet.guidelines.GuidelinesTab;
import com.mindbox.pe.client.applet.guidelines.search.SearchGuidelinesTab;
import com.mindbox.pe.client.applet.parameters.ParameterManagerTab;
import com.mindbox.pe.client.applet.template.TemplateManagementTab;
import com.mindbox.pe.client.common.tab.PowerEditorTab;
import com.mindbox.pe.common.PrivilegeConstants;
import com.mindbox.pe.communication.ServerException;
import com.mindbox.pe.xsd.config.FeatureNameType;
import com.mindbox.pe.xsd.config.GuidelineTab;

/**
 * Guideline Management Tab.
 * @author Geneho Kim
 * @author MindBox
 */
public class PolicyTab extends PowerEditorTab {
	/**
	 * 
	 */
	private static final long serialVersionUID = -3951228734910107454L;

	public PolicyTab(final List<GuidelineTab> tabConfigs, TemplateManagementTab templateManagementTab, boolean readOnly) throws ServerException {
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

		if (ClientUtil.isFeatureEnabled(FeatureNameType.PARAMETER)) {
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