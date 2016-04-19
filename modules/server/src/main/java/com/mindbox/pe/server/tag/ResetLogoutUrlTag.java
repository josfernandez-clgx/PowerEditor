package com.mindbox.pe.server.tag;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

import com.mindbox.pe.server.config.ConfigurationManager;

public class ResetLogoutUrlTag extends TagSupport {

	private static final long serialVersionUID = 2010083110440000L;

	@Override
	public int doStartTag() throws JspException {
		ConfigurationManager.getInstance().resetLogoutUrlToUse(HttpServletRequest.class.cast(pageContext.getRequest()));
		return SKIP_BODY;
	}
}
