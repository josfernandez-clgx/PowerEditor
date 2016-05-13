package com.mindbox.pe.client.applet.admin.lock;

import java.awt.BorderLayout;

import javax.swing.JPanel;
import javax.swing.JScrollPane;

import com.mindbox.pe.client.applet.UIFactory;

/**
 * 
 * @author Gene Kim
 * @author MindBox, Inc
 * @since PowerEditor 1.10.0
 */
public class LockManagementPanel extends JPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = -3951228734910107454L;


	public LockManagementPanel() {
		super(new BorderLayout(0, 0));
		UIFactory.setLookAndFeel(this);

		initPanel();
	}


	private void initPanel() {
		JPanel lmPanel = UIFactory.createJPanel();
		add(new JScrollPane(lmPanel), BorderLayout.CENTER);
	}
}