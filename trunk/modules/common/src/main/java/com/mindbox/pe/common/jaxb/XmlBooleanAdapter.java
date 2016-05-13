package com.mindbox.pe.common.jaxb;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import com.mindbox.pe.model.Constants;

public class XmlBooleanAdapter extends XmlAdapter<String, Boolean> {

	@Override
	public String marshal(Boolean value) {
		return value == null ? null : value.toString();
	}

	@Override
	public Boolean unmarshal(String value) {
		return value == null ? Boolean.FALSE : (Constants.VALUE_YES.equalsIgnoreCase(value) ? Boolean.TRUE : Boolean.valueOf(value.toLowerCase()));
	}
}
