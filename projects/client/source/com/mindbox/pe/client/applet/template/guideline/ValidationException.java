/*
 * Created on 2004. 8. 8.
 */
package com.mindbox.pe.client.applet.template.guideline;

import com.mindbox.pe.client.ClientUtil;


/**
 * @author kim
 * @author MindBox
 * @since PowerEditor 
 */
class ValidationException extends Exception {

	private final Object[] args;

	public ValidationException(String messageKey, Object arg) {
		this(messageKey, new Object[]{arg});
	}

	public ValidationException(String messageKey, Object[] args) {
		super(messageKey);
		this.args = args;
	}

	public void showAsWarning() {
		ClientUtil.getInstance().showWarning(super.getMessage(), args);
	}
}