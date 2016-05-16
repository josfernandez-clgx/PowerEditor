/*
 * Created on Oct 9, 2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package com.mindbox.pe.client.applet.guidelines.manage;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.mindbox.pe.client.ClientUtil;
import com.mindbox.pe.client.applet.UIFactory;
import com.mindbox.pe.client.common.context.AbstractContextDetailPanel;
import com.mindbox.pe.xsd.config.GuidelineTab;

/**
 * 
 * @author Gene Kim
 * @author MindBox, Inc
 */
public class GuidelineContextDetailPanel extends AbstractContextDetailPanel {

	private static final long serialVersionUID = -3951228734910107454L;

	private final List<GuidelineTab> tabConfigs;
	private List<LoadTemplateButtonPanel> templatePanelList;

	public GuidelineContextDetailPanel(final List<GuidelineTab> tabConfigs, TemplateSelectionPanel templatePanel) {
		this.templatePanelList = new ArrayList<LoadTemplateButtonPanel>();
		this.tabConfigs = tabConfigs;

		initPanel(templatePanel);
	}

	private void initPanel(final TemplateSelectionPanel templatePanel) {
		JTabbedPane tab = new JTabbedPane(JTabbedPane.BOTTOM);
		tab.setFocusable(false);
		tab.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
		tab.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				templatePanel.clearTemplates();
			}
		});

		for (final GuidelineTab guidelineTab : tabConfigs) {
			if (ClientUtil.checkViewOrEditGuidelinePermission(guidelineTab)) {
				LoadTemplateButtonPanel tbPanel = new LoadTemplateButtonPanel();
				tbPanel.setTabConfiguration(guidelineTab);
				tbPanel.setTemplatePanel(templatePanel);
				tbPanel.setContextProvider(super.contextPanel);
				tbPanel.setEnabledButtons(true);
				// Wrap it in a scrollable pane so the buttons scroll if they need to.
				JScrollPane wrapper = new JScrollPane(tbPanel);

				tab.addTab(guidelineTab.getDisplayName(), wrapper);

				templatePanelList.add(tbPanel);
			}
		}

		JPanel typePanel = new JPanel(new GridLayout(1, 1, 0, 0));
		typePanel.add(tab);
		typePanel.setBorder(UIFactory.createTitledBorder(ClientUtil.getInstance().getLabel("label.title.guideline.type.selection")));

		add(typePanel, BorderLayout.SOUTH);
	}

	void setEnabledTemplateButtons(boolean enabled) {
		for (Iterator<LoadTemplateButtonPanel> iter = templatePanelList.iterator(); iter.hasNext();) {
			LoadTemplateButtonPanel element = iter.next();
			element.setEnabledButtons(enabled);
		}
	}

}
