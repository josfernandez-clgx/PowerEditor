package com.mindbox.pe.server.tag;

import javax.servlet.jsp.JspException;

import com.mindbox.pe.server.cache.ParameterTemplateManager;

public class GetParameterTemplatesTag extends AbstractVarTag {

	private static final long serialVersionUID = 2010083110440000L;

	@Override
	public int doStartTag() throws JspException {
		setVarObject(ParameterTemplateManager.getInstance().getTemplates());

		return SKIP_BODY;
	}
}
