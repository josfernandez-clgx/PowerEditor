/*
 * Created on 2004. 6. 29.
 */
package com.mindbox.pe.client.applet.admin.request;

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
import com.mindbox.pe.model.process.ProcessRequest;

/**
 * 
 *
 * @author kim
 * @since PowerEditor  
 */
class RequestEditDialog extends JPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = -3951228734910107454L;

	public static ProcessRequest editRequest(Frame owner, ProcessRequest request) {
		JDialog dialog = new JDialog(owner, true);
		dialog.setTitle(ClientUtil.getInstance().getLabel("d.title.edit.request"));
		RequestEditDialog instance = new RequestEditDialog(dialog, request);
		UIFactory.addToDialog(dialog, instance);

		dialog.setVisible(true);

		return instance.request;
	}

	public static ProcessRequest newRequest() {
		return editRequest(JOptionPane.getFrameForComponent(ClientUtil.getApplet()), null);
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
			detailPanel.setRequest(null);
			request = null;
			dialog.dispose();
		}
	}

	private final JDialog dialog;
	private final RequestDetailPanel detailPanel;
	private ProcessRequest request;

	private RequestEditDialog(JDialog dialog, ProcessRequest request) {
		this.dialog = dialog;
		this.request = request;
		this.detailPanel = new RequestDetailPanel();
		detailPanel.setRequest(request);

		setSize(400, 300);
		initPanel();
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
		ProcessRequest requestFromPanel = detailPanel.getRequest();

		if (requestFromPanel.getName() == null || requestFromPanel.getName().length() == 0) {
			ClientUtil.getInstance().showWarning("msg.warning.empty.field", new Object[] { ClientUtil.getInstance().getLabel("label.name") });
			return false;
		}
		if (requestFromPanel.getDisplayName() == null || requestFromPanel.getDisplayName().length() == 0) {
			ClientUtil.getInstance().showWarning("msg.warning.empty.field", new Object[] { ClientUtil.getInstance().getLabel("label.name.display") });
			return false;
		}
		if (requestFromPanel.getRequestType() == null || requestFromPanel.getRequestType().length() == 0) {
			ClientUtil.getInstance().showWarning("msg.warning.empty.field", new Object[] { ClientUtil.getInstance().getLabel("label.type.request") });
			return false;
		}
		if (requestFromPanel.getInitFunction() == null || requestFromPanel.getInitFunction().length() == 0) {
			ClientUtil.getInstance().showWarning("msg.warning.empty.field", new Object[] { ClientUtil.getInstance().getLabel("label.init.function") });
			return false;
		}
		if (requestFromPanel.getPurpose() == null || requestFromPanel.getPurpose().length() == 0) {
			ClientUtil.getInstance().showWarning("msg.warning.empty.field", new Object[] { ClientUtil.getInstance().getLabel("label.purpose") });
			return false;
		}
		if (requestFromPanel.getPhase() == null) {
			ClientUtil.getInstance().showWarning("msg.warning.empty.field", new Object[] { ClientUtil.getInstance().getLabel("label.phase") });
			return false;
		}
		request = requestFromPanel;

		return true;

	}
}