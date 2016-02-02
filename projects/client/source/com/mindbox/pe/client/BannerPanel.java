package com.mindbox.pe.client;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.FlowLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.mindbox.pe.client.applet.PowerEditorSwingTheme;
import com.mindbox.pe.client.applet.UIFactory;
import com.mindbox.pe.client.common.ButtonPanel;
import com.mindbox.pe.client.common.LinePanel;
import com.mindbox.pe.client.common.PanelBase;

/**
 * Banner panel for main frame.
 * 
 * @since PowerEditor 1.0
 */
final class BannerPanel extends PanelBase {

	private class URLRefListener implements ActionListener {

		public void actionPerformed(ActionEvent actionevent) {
			try {
				setCursor(Cursor.getPredefinedCursor(3));
			}
			catch (Exception exception) {
				exception.printStackTrace();
			}
			finally {
				setCursor(Cursor.getPredefinedCursor(0));
			}
		}

		URLRefListener() {
		}
	}

	private class ExitListener implements ActionListener {
		public void actionPerformed(ActionEvent actionevent) {
			try {
				setCursor(Cursor.getPredefinedCursor(3));
				exit();
			}
			catch (Exception exception) {
				exception.printStackTrace();
			}
			finally {
				setCursor(Cursor.getPredefinedCursor(0));
			}
		}
	}

	public BannerPanel() {
		JButton mbButton = new JButton();
		mbButton.setFocusable(false);
		mbButton.setFocusPainted(false);
		mbButton.setBorderPainted(false);
		mbButton.addActionListener(new URLRefListener());

		mStatusBar = new JLabel("  " + ClientUtil.getInstance().getMessage("WelcomeMsg"));
		mStatusBar.setFont(PowerEditorSwingTheme.bannelFont);
		mStatusBar.setOpaque(true);

		JPanel statusPanel = new JPanel(new BorderLayout(0, 0));
		statusPanel.add(mStatusBar, BorderLayout.CENTER);

		JButton exitButton = UIFactory.createButton("Exit", "image.btn.small.exit", new ExitListener(), null);

		ButtonPanel buttonPanel = new ButtonPanel(new JButton[] { exitButton }, FlowLayout.RIGHT);

		setLayout(new BorderLayout());
		add(mbButton, BorderLayout.WEST);
		add(statusPanel, BorderLayout.CENTER);
		add(buttonPanel, BorderLayout.EAST);

		add(new LinePanel(), BorderLayout.SOUTH);
	}

	public final Insets getInsets() {
		return new Insets(0, 0, 0, 0);
	}

	void exit() {
		ClientUtil.printInfo("Calling Exit!");
		if (ClientUtil.getParent().confirmExit()) ClientUtil.getParent().dispose();
	}

	public void setStatus(String s) {
		mStatusBar.setText(s);
		mStatusBar.setIcon(null);
	}

	public void setStatus(String s, ImageIcon imageicon) {
		mStatusBar.setText(s);
		mStatusBar.setIcon(imageicon);
	}

	public void setStatus(String s, Color color) {
		mStatusBar.setForeground(color);
		mStatusBar.setText(s);
		mStatusBar.setIcon(null);
	}

	private JLabel mStatusBar;
}
