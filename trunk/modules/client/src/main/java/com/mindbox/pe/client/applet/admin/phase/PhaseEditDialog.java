/*
 * Created on 2004. 6. 29.
 */
package com.mindbox.pe.client.applet.admin.phase;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import com.mindbox.pe.client.ClientUtil;
import com.mindbox.pe.client.applet.UIFactory;
import com.mindbox.pe.model.process.Phase;
import com.mindbox.pe.model.process.PhaseFactory;

/**
 * 
 *
 * @author kim
 * @since PowerEditor  
 */
class PhaseEditDialog extends JPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = -3951228734910107454L;

	public static Phase editPhase(Frame owner, Phase phase) {
		JDialog dialog = new JDialog(owner, true);
		dialog.setTitle(ClientUtil.getInstance().getLabel("d.title.edit.phase"));
		PhaseEditDialog instance = new PhaseEditDialog(dialog, phase);
		UIFactory.addToDialog(dialog, instance);

		dialog.setVisible(true);

		return instance.phase;
	}

	public static Phase newPhase(Phase parentPhase) {
		Phase phase = PhaseFactory.createPhase(PhaseFactory.TYPE_SEQUENCE, -1, "", "");
		phase.setParent(parentPhase);
		return editPhase(JOptionPane.getFrameForComponent(ClientUtil.getApplet()), phase);
	}

	private class AcceptL implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			if (updateRequest()) {
				dialog.dispose();
			}
		}
	}

	private class CancelL implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			detailPanel.setPhase(null);
			phase = null;
			dialog.dispose();
		}
	}

	private final JDialog dialog;
	private final PhaseDetailPanel detailPanel;
	private Phase phase;

	private PhaseEditDialog(JDialog dialog, Phase phase) {
		this.dialog = dialog;
		this.phase = phase;
		this.detailPanel = new PhaseDetailPanel();

		setSize(400, 300);
		initPanel();

		detailPanel.setPhase(phase);
	}

	private void initPanel() {
		JButton createButton = new JButton("Accept");
		createButton.addActionListener(new AcceptL());
		JButton cancelButton = new JButton("Cancel");
		cancelButton.addActionListener(new CancelL());

		JPanel buttonPanel = UIFactory.createJPanel(new FlowLayout(FlowLayout.CENTER));
		buttonPanel.add(createButton);
		buttonPanel.add(cancelButton);

		setLayout(new BorderLayout(4, 4));
		add(detailPanel, BorderLayout.CENTER);
		add(buttonPanel, BorderLayout.SOUTH);
	}

	private boolean updateRequest() {
		Phase phaseFromPanel = detailPanel.getPhase();

		if (phaseFromPanel.getName() == null || phaseFromPanel.getName().length() == 0) {
			ClientUtil.getInstance().showWarning("msg.warning.empty.field", new Object[] { ClientUtil.getInstance().getLabel("label.name") });
			return false;
		}
		if (phaseFromPanel.getDisplayName() == null || phaseFromPanel.getDisplayName().length() == 0) {
			ClientUtil.getInstance().showWarning("msg.warning.empty.field", new Object[] { ClientUtil.getInstance().getLabel("label.name.display") });
			return false;
		}
		this.phase = phaseFromPanel;

		return true;

	}
}