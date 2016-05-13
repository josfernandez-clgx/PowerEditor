package com.mindbox.pe.client.common;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JPanel;
import javax.swing.JSeparator;

import org.apache.log4j.Logger;

import com.mindbox.pe.client.applet.UIFactory;

public abstract class PanelBase extends JPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = -3951228734910107454L;

	protected static final int HORIZONTAL_GAP = 3;
	protected static final int VERTICAL_GAP = 1;

	public static final void addComponent(JPanel panel, GridBagLayout bag, GridBagConstraints c, Component component) {
		bag.setConstraints(component, c);
		panel.add(component);
	}

	/**
	 * This changes grid bag constraint. Be sure to reset after calling this method.
	 * @param panel panel
	 * @param bag bag
	 * @param c c
	 */
	public static final void addFormSeparator(JPanel panel, GridBagLayout bag, GridBagConstraints c) {
		Insets prevInsets = c.insets;
		c.gridwidth = GridBagConstraints.REMAINDER;
		c.weightx = 1.0;
		c.insets = new Insets(7, 7, 7, 7);
		JSeparator separator = new JSeparator();
		bag.setConstraints(separator, c);
		panel.add(separator);
		c.insets = prevInsets;
	}

	protected static final String asValue(String value) {
		return (value == null ? null : value.trim());
	}

	protected static final boolean identical(Object value1, Object value2) {
		if (value1 == value2) {
			return true;
		}
		if (value1 != null && value2 != null) {
			return value1.equals(value2);
		}
		else {
			return false;
		}
	}

	protected final Logger logger;

	protected PanelBase() {
		UIFactory.setLookAndFeel(this);
		this.logger = Logger.getLogger(getClass());
	}

}
