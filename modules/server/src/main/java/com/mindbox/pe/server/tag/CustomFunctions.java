package com.mindbox.pe.server.tag;

import java.util.Calendar;
import java.util.Date;

import javax.xml.datatype.XMLGregorianCalendar;

public class CustomFunctions {

	public static Date convertToDate(Object obj) {
		if (XMLGregorianCalendar.class.isInstance(obj)) {
			return XMLGregorianCalendar.class.cast(obj).toGregorianCalendar().getTime();
		}
		else if (Calendar.class.isInstance(obj)) {
			return Calendar.class.cast(obj).getTime();
		}
		else if (Date.class.isInstance(obj)) {
			return Date.class.cast(obj);
		}
		else {
			return null;
		}
	}

	private CustomFunctions() {
	}

}
