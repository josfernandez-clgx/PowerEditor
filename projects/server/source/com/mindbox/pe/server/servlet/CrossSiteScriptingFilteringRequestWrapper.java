package com.mindbox.pe.server.servlet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

class CrossSiteScriptingFilteringRequestWrapper extends HttpServletRequestWrapper {

	private static String cleanXSS(String value) {
		value = value.replaceAll("<", "&lt;").replaceAll(">", "&gt;");
		value = value.replaceAll("'", "&#039;").replaceAll("\"", "&quot;");
		value = value.replaceAll("\\(", "&#040;").replaceAll("\\)", "&#041;").replaceAll("\\[", "&#091;").replaceAll("\\]", "&#093;");
		value = value.replaceAll("=", "&#061;").replaceAll("%", "&#037;");
		value = value.replaceAll("\\|", "&#124;").replaceAll("&", "&amp;").replaceAll(";", "&#059;").replaceAll("\\*", "&#042;").replaceAll(
				"\\$",
				"&#036;").replaceAll("©", "&copy;").replaceAll("\\^", "&#094;").replaceAll("\\+", "&#043;").replaceAll("\\,", "&#130;").replaceAll(
				"\\\\",
				"&#092;").replaceAll("/", "&#047;");
		value = value.replaceAll("eval\\((.*)\\)", "");
		value = value.replaceAll("[\\\"\\\'][\\s]*javascript:(.*)[\\\"\\\']", "\"\"");
		value = value.replaceAll("script", "");
		value = value.replace('\r', ' ').replace('\n', ' ');
		return value;
	}

	public CrossSiteScriptingFilteringRequestWrapper(HttpServletRequest servletRequest) {
		super(servletRequest);
	}

	public String[] getParameterValues(String parameter) {
		String[] values = super.getParameterValues(parameter);
		if (values == null) {
			return null;
		}
		int count = values.length;
		String[] encodedValues = new String[count];
		for (int i = 0; i < count; i++) {
			encodedValues[i] = cleanXSS(values[i]);
		}
		return encodedValues;
	}

	public String getParameter(String parameter) {
		String value = super.getParameter(parameter);
		if (value == null) {
			return null;
		}
		return cleanXSS(value);
	}

	public String getHeader(String name) {
		String value = super.getHeader(name);
		if (value == null) {
			return null;
		}

		return cleanXSS(value);
	}
}
