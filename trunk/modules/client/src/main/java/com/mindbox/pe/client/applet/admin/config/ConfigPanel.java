package com.mindbox.pe.client.applet.admin.config;

import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextPane;

import com.mindbox.pe.client.ClientUtil;
import com.mindbox.pe.client.applet.UIFactory;
import com.mindbox.pe.client.common.AbstractThreadedActionAdapter;
import com.mindbox.pe.client.common.PanelBase;
import com.mindbox.pe.communication.ServerException;

/**
 * Configuration panel.
 * @author Geneho Kim
 * @author MindBox
 * @since PowerEditor 2.5.0
 */
public class ConfigPanel extends PanelBase {
	/**
	 * 
	 */
	private static final long serialVersionUID = -3951228734910107454L;

	private class ConfigL extends AbstractThreadedActionAdapter {

		protected ConfigL() {
		}

		public void performAction(ActionEvent event) throws Exception {
			if (ClientUtil.getInstance().showConfirmation("msg.warning.reload.config")) {
				try {
					String errorMessage = ClientUtil.getCommunicator().reloadConfiguration();
					if (errorMessage == null)
						ClientUtil.getInstance().showInformation("msg.reload.config.success");
					else
						ClientUtil.getInstance().showErrorDialog("msg.error.reload.config", new Object[] { errorMessage });
				}
				catch (ServerException ex) {
					ClientUtil.getInstance().showErrorDialog("msg.error.reload.config", new Object[] { ex.getMessage() });
				}
			}
		}
	}

	private final JButton reloadConfigButton;

	public ConfigPanel() {
		super();

		reloadConfigButton = UIFactory.createJButton("button.reload.config", null, new ConfigL(), null);

		initPanel();
	}

	private void initPanel() {
		GridBagLayout bag = new GridBagLayout();
		JPanel panel = UIFactory.createJPanel(bag);
		panel.setBorder(UIFactory.createTitledBorder(ClientUtil.getInstance().getLabel("label.manage.config")));
		GridBagConstraints c = new GridBagConstraints();
		c.anchor = GridBagConstraints.NORTH;
		c.insets = new Insets(4, 4, 4, 4);
		c.fill = GridBagConstraints.BOTH;
		c.weighty = 0.0;
		c.gridheight = 1;

		c.weightx = 1.0;
		c.gridwidth = GridBagConstraints.REMAINDER;

		JTextPane textPane = new JTextPane();
		textPane.setText(ClientUtil.getInstance().getMessage("msg.inst.help.reload.config"));
		textPane.setEditable(false);
		addComponent(panel, bag, c, textPane);

		addComponent(panel, bag, c, reloadConfigButton);

		setLayout(new FlowLayout(FlowLayout.LEFT, 20, 20));
		add(panel);
	}

}