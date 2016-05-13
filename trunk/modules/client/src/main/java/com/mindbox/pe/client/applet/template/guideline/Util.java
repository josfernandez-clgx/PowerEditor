/*
 * Created on 2004. 8. 8.
 */
package com.mindbox.pe.client.applet.template.guideline;

import javax.swing.JComboBox;
import javax.swing.text.JTextComponent;

import com.mindbox.pe.client.ClientUtil;

/**
 * @author kim
 * @author MindBox
 * @since PowerEditor 
 */
class Util {

	static void checkEmpty(JComboBox<?> comp, String labelKey) throws ValidationException {
		if (comp.getSelectedIndex() < 0) {
			throw new ValidationException("msg.errors.required", ClientUtil.getInstance().getLabel(labelKey));
		}
	}

	static void checkEmpty(JTextComponent comp, String labelKey) throws ValidationException {
		if (comp.getText() == null || comp.getText().trim().length() == 0) {
			throw new ValidationException("msg.errors.required", ClientUtil.getInstance().getLabel(labelKey));
		}
	}


	private Util() {
	}
}
