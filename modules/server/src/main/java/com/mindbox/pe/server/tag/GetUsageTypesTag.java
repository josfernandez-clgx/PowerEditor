package com.mindbox.pe.server.tag;

import java.util.Arrays;
import java.util.Comparator;

import javax.servlet.jsp.JspException;

import com.mindbox.pe.model.TemplateUsageType;

public class GetUsageTypesTag extends AbstractVarTag {

	private static final long serialVersionUID = 2010083110440000L;

	@Override
	public int doStartTag() throws JspException {
		TemplateUsageType[] usageTypes = TemplateUsageType.getAllInstances();
		Arrays.sort(usageTypes, new Comparator<TemplateUsageType>() {
			public int compare(TemplateUsageType arg0, TemplateUsageType arg1) {
				return arg0.toString().compareTo(arg1.toString());
			}
		});
		setVarObject(usageTypes);
		return SKIP_BODY;
	}
}
