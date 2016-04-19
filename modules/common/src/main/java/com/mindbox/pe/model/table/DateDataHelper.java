package com.mindbox.pe.model.table;

import java.text.ParseException;
import java.util.Date;

import com.mindbox.pe.common.UtilBase;
import com.mindbox.pe.model.Constants;

public class DateDataHelper {

	public static Date asDateValue(String str) throws ParseException {
		if (!UtilBase.isEmptyAfterTrim(str)) {
			return Constants.THREADLOCAL_FORMAT_DATE.get().parse(str.trim());
		}
		return null;
	}
}
