package com.mindbox.pe.client.applet.options;

import javax.swing.JTabbedPane;

import com.mindbox.pe.client.ClientUtil;
import com.mindbox.pe.client.applet.PowerEditorSwingTheme;

public class OptionsTab extends JTabbedPane {
	/**
	 * 
	 */
	private static final long serialVersionUID = -3951228734910107454L;

	public OptionsTab() {
		setFont(PowerEditorSwingTheme.tabFont);

		addTab(
				ClientUtil.getInstance().getLabel("PreferencesTabLbl"),
				ClientUtil.getInstance().makeImageIcon("image.blank"),
				new PreferencePanel(),
				ClientUtil.getInstance().getLabel("PreferencesTabTooltip"));
		addTab(
				ClientUtil.getInstance().getLabel("ChangePasswordTabLbl"),
				ClientUtil.getInstance().makeImageIcon("image.blank"),
				new PasswordPanel(),
				ClientUtil.getInstance().getLabel("ChangePasswordTabTooltip"));

	}
}
