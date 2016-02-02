package com.mindbox.pe.model.table;

import java.text.ParseException;
import java.util.Date;

import com.mindbox.pe.common.UtilBase;
import com.mindbox.pe.common.config.UIConfiguration;

public class DateDataHelper {

	public static Date asDateValue(String str) throws ParseException {
		if (!UtilBase.isEmptyAfterTrim(str)) {
			return UIConfiguration.FORMAT_DATE.parse(str.trim());
		}
		return null;
	}
}
