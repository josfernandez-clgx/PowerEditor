/*
 * Created on Jul 1, 2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package com.mindbox.pe.client.common;

import javax.swing.JComboBox;

import com.mindbox.pe.client.ClientUtil;
import com.mindbox.pe.client.applet.UIFactory;

/**
 * 
 * @author Gene Kim
 * @author MindBox, Inc
 * @since PowerEditor 1.10.0
 */
public class BooleanComboBox extends JComboBox<ComboBoolean> {
	/**
	 * 
	 */
	private static final long serialVersionUID = -3951228734910107454L;
	static String GENERIC_ANY = "Any";
	static String GENERIC_YES = "Yes";

	static String GENERIC_NO = "No";

	private static boolean hasInitialized = false;

	private static void initialize() {
		if (!hasInitialized) {
			GENERIC_ANY = ClientUtil.getInstance().getValueLiteral("GenericAnyVal");
			GENERIC_YES = ClientUtil.getInstance().getValueLiteral("GenericYesVal");
			GENERIC_NO = ClientUtil.getInstance().getValueLiteral("GenericNoVal");

			hasInitialized = true;
		}
	}

	private ComboBoolean NO_SEL;

	private ComboBoolean YES;

	private ComboBoolean NO;

	public BooleanComboBox() {
		NO_SEL = new ComboBoolean(null);
		YES = new ComboBoolean(new Boolean(true));
		NO = new ComboBoolean(new Boolean(false));
		UIFactory.setLookAndFeel(this);
		init();
	}

	public Boolean getSelectedValue() {
		return ((ComboBoolean) getSelectedItem()).getValue();
	}

	void init() {
		initialize();
		addItem(NO_SEL);
		addItem(YES);
		addItem(NO);
	}

	public void setSelectedItem(Boolean boolean1) {
		if (boolean1 == null)
			super.setSelectedItem(NO_SEL);
		else if (boolean1.booleanValue())
			super.setSelectedItem(YES);
		else
			super.setSelectedItem(NO);
	}
}
