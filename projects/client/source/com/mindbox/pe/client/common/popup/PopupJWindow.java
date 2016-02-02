/*
 * Created on 2004. 12. 28.
 *
 */
package com.mindbox.pe.client.common.popup;

import java.awt.Frame;
import java.awt.Window;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.JWindow;


/**
 * JWindow used as a popup.
 * Use this instead of {@link PopupJDialog} on fields used on an applet or a window component.
 * @author Geneho Kim
 * @since PowerEditor 4.2.0
 */
public final class PopupJWindow extends JWindow {

	private class FocusL implements FocusListener {

		public void focusGained(FocusEvent arg0) {
		}

		public void focusLost(FocusEvent arg0) {
		}
	}


	public PopupJWindow() {
		super();
		initWindow();
	}

	public PopupJWindow(Frame arg0) {
		super(arg0);
		initWindow();
	}

	public PopupJWindow(Window arg0) {
		super(arg0);
		initWindow();
	}

	private void initWindow() {
		setFocusableWindowState(false);
		addFocusListener(new FocusL());
	}

	public void setVisible(boolean visible) {
		super.setVisible(visible);
		toFront();
	}

	public void setEnabled(boolean enabled) {
		super.setEnabled(enabled);
	}
}