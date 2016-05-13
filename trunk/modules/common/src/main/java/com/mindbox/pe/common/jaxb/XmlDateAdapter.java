package com.mindbox.pe.common.jaxb;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.xml.bind.annotation.adapters.XmlAdapter;

public class XmlDateAdapter extends XmlAdapter<String, Date> {

	private static final ThreadLocal<DateFormat> THREADLOCAL_DATE_FORMAT = new ThreadLocal<DateFormat>() {
		protected DateFormat initialValue() {
			return new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
		}
	};

	@Override
	public String marshal(Date date) throws Exception {
		return date == null ? null : THREADLOCAL_DATE_FORMAT.get().format(date);
	}

	@Override
	public Date unmarshal(String value) throws Exception {
		try {
			return value == null ? null : THREADLOCAL_DATE_FORMAT.get().parse(value);
		}
		catch (ParseException e) {
			throw new IllegalArgumentException(e);
		}
	}
}
