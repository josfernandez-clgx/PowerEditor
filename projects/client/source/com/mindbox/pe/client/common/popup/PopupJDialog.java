/*
 * Created on 2004. 12. 28.
 *
 */
package com.mindbox.pe.client.common.popup;

import java.awt.Dialog;
import java.awt.Frame;
import java.awt.HeadlessException;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.JDialog;


/**
 * JDialog used as a popup.
 * Use this instead of {@link PopupJWindow} on fields used on a JDialog.
 * @author Geneho Kim
 * @since PowerEditor 4.2.0
 * @see PopupJWindow
 */
public final class PopupJDialog extends JDialog {

	private class FocusL implements FocusListener {
		public void focusGained(FocusEvent arg0) {
		}

		public void focusLost(FocusEvent arg0) {
		}
	}
	
	
	public PopupJDialog() throws HeadlessException {
		super();
		initDialog();
	}

	public PopupJDialog(Dialog arg0) throws HeadlessException {
		super(arg0);
		initDialog();
	}

	public PopupJDialog(Frame arg0) throws HeadlessException {
		super(arg0);
		initDialog();
	}

	private void initDialog() {
		setUndecorated(true);
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