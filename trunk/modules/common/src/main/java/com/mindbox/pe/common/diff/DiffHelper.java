package com.mindbox.pe.common.diff;

import java.sql.Date;

import com.mindbox.pe.common.UtilBase;
import com.mindbox.pe.model.Auditable;
import com.mindbox.pe.model.Constants;
import com.mindbox.pe.model.EnumValue;
import com.mindbox.pe.model.table.EnumValues;

public final class DiffHelper {

	public static String convertToAuditStringValue(Object element) {
		if (element == null) {
			return "";
		}
		else if (Auditable.class.isInstance(element)) {
			return Auditable.class.cast(element).getAuditDescription();
		}
		else if (Date.class.isInstance(element)) {
			return Constants.THREADLOCAL_FORMAT_DATE_TIME_SEC.get().format(Date.class.cast(element));
		}
		else if (EnumValue.class.isInstance(element)) {
			return EnumValue.class.cast(element).getDisplayLabel();
		}
		else if (EnumValues.class.isInstance(element)) {
			final EnumValues<?> enumValues = EnumValues.class.cast(element);
			final StringBuilder buff = new StringBuilder();
			if (!enumValues.isEmpty()) {
				if (enumValues.isSelectionExclusion()) {
					buff.append("Not ");
				}
				for (int i = 0; i < enumValues.size(); i++) {
					if (i > 0) {
						buff.append(", ");
					}
					final Object value = enumValues.get(i);
					buff.append(EnumValue.class.isInstance(value) ? EnumValue.class.cast(value).getDisplayLabel() : value.toString());
				}
			}
			return buff.toString();
		}
		else {
			return element.toString();
		}
	}

	public static String convertToNativeValue(Object cellValue) {
		return UtilBase.convertCellValueToString(cellValue);
	}

	private DiffHelper() {
	}
}
