package com.mindbox.pe.server.tag;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.servlet.jsp.JspException;

public class GetSystemPropertiesTag extends AbstractVarTag {

	private static final long serialVersionUID = 2010083110440000L;

	@Override
	public int doStartTag() throws JspException {
		Map<String, String> map = new HashMap<String, String>();
		Properties systemProperties = System.getProperties();
		List<String> keyList = new ArrayList<String>();
		keyList.addAll(systemProperties.stringPropertyNames());
		Collections.sort(keyList);

		for (String key : keyList) {
			map.put(key, systemProperties.getProperty(key));
		}
		setVarObject(map);
		
		return SKIP_BODY;
	}
}
