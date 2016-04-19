/*
 * Created on 2005. 8. 16.
 *
 */
package com.mindbox.pe.client.common;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.SwingConstants;

import com.mindbox.pe.client.ClientUtil;


/**
 * JButton that show or hide a given JComponent (toggle action).
 * @author Geneho Kim
 * @since PowerEditor 4.3.6
 */
public class ShowHideImageButton extends JButton implements ActionListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = -3951228734910107454L;

	private static final ImageIcon EXPAND_IMAGE = ClientUtil.getInstance().makeImageIcon("image.btn.small.plus");
	private static final ImageIcon COMPRESS_IMAGE = ClientUtil.getInstance().makeImageIcon("image.btn.small.minus");

	private final JComponent component;

	public ShowHideImageButton(JComponent component) {
		if (component == null) throw new NullPointerException("component cannot be null");
		this.component = component;
		component.setVisible(false);
		setIcon(EXPAND_IMAGE);
		addActionListener(this);
		setBorderPainted(false);
		setFocusable(false);
		setHorizontalAlignment(SwingConstants.RIGHT);
		setOpaque(false);
	}

	public void actionPerformed(ActionEvent e) {
		synchronized (component) {
			setEnabled(false);
			try {
				component.setVisible(!component.isVisible());
				component.invalidate();
				setIcon((component.isVisible() ? COMPRESS_IMAGE : EXPAND_IMAGE));
			}
			finally {
				setEnabled(true);
			}
		}
	}

}
