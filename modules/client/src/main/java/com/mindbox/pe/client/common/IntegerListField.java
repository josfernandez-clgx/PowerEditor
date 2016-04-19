package com.mindbox.pe.client.common;

import com.mindbox.pe.client.common.dialog.EditIntListDialog;
import com.mindbox.pe.common.UtilBase;

/**
 * Integer list field widget.
 * 
 * @author Geneho Kim
 * @since 4.5.0
 */
public class IntegerListField extends AbstractListField {
	/**
	 * 
	 */
	private static final long serialVersionUID = -3951228734910107454L;

	public IntegerListField(String dialogTitle) {
		super(dialogTitle);
	}

	private int[] getIntArrayFromField() {
		String value = getStringValue();
		return value == null ? null : UtilBase.toIntArray(value);
	}

	public boolean hasValue() {
		int[] ia = getIntArrayFromField();
		return (ia != null && ia.length > 0);
	}

	public int[] getValue() {
		return getIntArrayFromField();
	}

	public synchronized void setValue(int[] value) {
		setValue(UtilBase.toString(value));
	}

	protected void handleEditAction() {
		int[] initArray = getIntArrayFromField();
		int[] newArray = EditIntListDialog.editIntArray(dialogTitle, initArray);

		if (!UtilBase.equals(initArray, newArray)) {
			synchronized (IntegerListField.this) {
				setValue(UtilBase.toString(newArray));
			}
		}
	}
}
