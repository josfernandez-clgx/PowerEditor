package com.mindbox.pe.client.applet;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.concurrent.TimeUnit;

import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import com.mindbox.pe.client.ClientUtil;
import com.mindbox.pe.common.timeout.TimeOutListener;

public class DefaultTimeOutListener implements TimeOutListener {

	private TimeOutHandler timeOutHandler;
	private JDialog previousDialog = null;

	DefaultTimeOutListener(TimeOutHandler timeOutHandler) {
		super();
		this.timeOutHandler = timeOutHandler;
	}

	@Override
	public synchronized void aboutToTimeOut(final long remainingTime, final TimeUnit timeUnit) {
		System.out.println(String.format("About to time out in %d (%s)", remainingTime, timeUnit));
		showWarning(remainingTime, timeUnit);
	}

	private void closePreviousDialog() {
		if (previousDialog != null) {
			previousDialog.setVisible(false);
			previousDialog.dispose();
			previousDialog = null;
		}
	}

	private JPanel createWarningMessagePanel(final long remainingTime, final TimeUnit timeUnit) {
		final JLabel label = new JLabel(String.format(
				"<html><body><p>%s</p></body></html>",
				ClientUtil.getInstance().getMessage("msg.warning.about.to.timeout", new Object[] { TimeUnit.MINUTES.convert(remainingTime, timeUnit) })));
		label.setVerticalAlignment(JLabel.CENTER);
		label.setHorizontalAlignment(JLabel.CENTER);
		label.setMaximumSize(new Dimension(560, 80));

		final JPanel messagePanel = UIFactory.createFlowLayoutPanel(FlowLayout.CENTER, 2, 2);
		messagePanel.add(label);

		final JPanel panel = UIFactory.createBorderLayoutPanel(12, 12);
		panel.add(messagePanel, BorderLayout.CENTER);

		final JPanel buttonPanel = UIFactory.createFlowLayoutPanelCenterAlignment(2, 2);
		buttonPanel.add(UIFactory.createButton(" OK ", null, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent actionEvent) {
				closePreviousDialog();
			}
		}, null));
		panel.add(buttonPanel, BorderLayout.SOUTH);
		return panel;
	}

	private synchronized void showWarning(final long remainingTime, final TimeUnit timeUnit) {
		try {
			if (previousDialog != null) {
				System.out.println("Disposing previous dialog...");
				closePreviousDialog();
			}
			previousDialog = new JDialog(JOptionPane.getFrameForComponent(ClientUtil.getApplet()), false);
			previousDialog.setTitle(ClientUtil.getInstance().getLabel("label.title.timeout"));
			UIFactory.addToDialog(previousDialog, createWarningMessagePanel(remainingTime, timeUnit));
			previousDialog.setSize(600, 120);
			previousDialog.setVisible(true);
		}
		catch (Exception e) {
			System.out.println("Error: failed to display time out waring");
			e.printStackTrace();
		}
	}

	@Override
	public synchronized void timedOut() {
		System.out.println("Session timed out");
		closePreviousDialog();
		timeOutHandler.sessionTimedOut();
	}
}
