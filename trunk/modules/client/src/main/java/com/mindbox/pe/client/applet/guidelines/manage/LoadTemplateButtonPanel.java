/*
 * Created on Jun 24, 2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package com.mindbox.pe.client.applet.guidelines.manage;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.util.List;
import java.util.Vector;

import javax.swing.JButton;

import com.mindbox.pe.client.ClientUtil;
import com.mindbox.pe.client.applet.UIFactory;
import com.mindbox.pe.client.common.AbstractThreadedActionAdapter;
import com.mindbox.pe.client.common.ButtonPanel;
import com.mindbox.pe.common.GuidelineContextProvider;
import com.mindbox.pe.communication.ServerException;
import com.mindbox.pe.model.GridSummary;
import com.mindbox.pe.model.GuidelineContext;
import com.mindbox.pe.model.TemplateUsageType;
import com.mindbox.pe.xsd.config.GuidelineTab;

/**
 * 
 * @author Gene Kim
 * @author MindBox, Inc
 * @since PowerEditor 1.10.0
 */
public class LoadTemplateButtonPanel extends ButtonPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = -3951228734910107454L;

	private class ButtonL extends AbstractThreadedActionAdapter {

		private final TemplateUsageType usageType;

		public ButtonL(TemplateUsageType usageType) {
			this.usageType = usageType;
		}

		public void performAction(ActionEvent e) {
			GuidelineContext[] contexts = guidelineContextProvider.getGuidelineContexts();
			try {
				List<GridSummary> summaryList = ClientUtil.getCommunicator().fetchGridSummaries(usageType, contexts);

				templatePanel.setTemplates(usageType, contexts, summaryList);
			}
			catch (ServerException ex) {
				ClientUtil.getLogger().error("failed to get template summaries for " + contexts, ex);
				ClientUtil.getInstance().showErrorDialog("msg.error.generic.service", new Object[] { ClientUtil.getInstance().getErrorMessage(ex) });
			}
		}
	}

	private TemplateSelectionPanel templatePanel = null;
	private GuidelineContextProvider guidelineContextProvider = null;

	public LoadTemplateButtonPanel() {
	}

	private void setButtons(GuidelineTab tabConfig) {
		Vector<JButton> vectButtons = new Vector<JButton>();
		for (GuidelineTab.UsageType guidelineUsageType : tabConfig.getUsageType()) {
			TemplateUsageType usageType = TemplateUsageType.valueOf(guidelineUsageType.getName());

			if (ClientUtil.checkViewOrEditGuidelinePermissionOnUsageType(usageType)) {
				vectButtons.add(UIFactory.createButton(ClientUtil.getInstance().getLabel("button.load", new Object[] { usageType.getDisplayName() }), "image.btn.load.guidelines", new ButtonL(
						usageType), null));
			}
		}

		if (vectButtons.size() > 0) {
			JButton[] buttons = new JButton[vectButtons.size()];
			vectButtons.toArray(buttons);
			super.setButtons(buttons, FlowLayout.CENTER);
		}
	}

	public final void setTemplatePanel(TemplateSelectionPanel templatePanel) {
		this.templatePanel = templatePanel;
	}

	public final void setTabConfiguration(GuidelineTab tabConfig) {
		setButtons(tabConfig);
		setEnabledButtons(false);
	}

	public void setEnabledButtons(boolean enabled) {
		for (int i = 0; i < super.buttons.length; i++) {
			super.buttons[i].setEnabled(enabled);
		}
	}

	public void setContextProvider(GuidelineContextProvider guidelineContextProvider) {
		this.guidelineContextProvider = guidelineContextProvider;
	}
}