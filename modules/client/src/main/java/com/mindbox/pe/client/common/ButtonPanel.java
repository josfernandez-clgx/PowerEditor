package com.mindbox.pe.client.common;

import java.awt.FlowLayout;

import javax.swing.AbstractButton;
import javax.swing.JButton;
import javax.swing.JPanel;

import com.mindbox.pe.client.applet.UIFactory;

/**
 * A panel with buttons.
 * @since PowerEditor 1.0
 */
public class ButtonPanel extends JPanel {
	private static final long serialVersionUID = -3951228734910107454L;

	protected AbstractButton[] buttons;

	protected ButtonPanel() {
		buttons = new JButton[0];
		UIFactory.setLookAndFeel(this);
	}

	public ButtonPanel(AbstractButton[] buttons, int alignment) {
		super();
		UIFactory.setLookAndFeel(this);
		setButtons(buttons, alignment);
	}

	public void discardChanges() {
	}

	public void handleDoubleClick() {
	}

	// Allow inspection of dirty flag
	public boolean hasUnsavedChanges() {
		return false;
	}

	// returns false if the changes failed to save
	public boolean saveChanges() {
		return true;
	}

	protected final void setButtons(AbstractButton[] buttons, int alignment) {
		this.buttons = null;
		this.buttons = buttons;
		setLayout(new FlowLayout(alignment, 3, 3));
		for (int i = 0; i < buttons.length; i++) {
			add(buttons[i]);
		}
	}

	@Override
	public void setEnabled(boolean enabled) {
		super.setEnabled(enabled);
		if (buttons != null && buttons.length > 0) {
			buttons[0].setEnabled(enabled);
		}
	}

	public void setEnabledAll(boolean enabled) {
		super.setEnabled(enabled);
		if (buttons != null && buttons.length > 0) {
			for (int i = 0; i < buttons.length; i++) {
				if (buttons[i] != null) buttons[i].setEnabled(enabled);
			}
		}
	}

	/**
	 * Assumes the first button is the new button.
	 * The first button's status will not be modified.
	 * @param enabled enabled flag
	 */
	public void setEnabledSelectionAwareButtons(boolean enabled) {
		if (this.isEnabled()) {
			for (int i = 1; i < buttons.length; i++) {
				if (buttons[i] != null) buttons[i].setEnabled(enabled);
			}
		}
	}
}