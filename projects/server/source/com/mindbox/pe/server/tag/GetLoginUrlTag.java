package com.mindbox.pe.server.tag;

import static com.mindbox.pe.common.UtilBase.isEmptyAfterTrim;

import javax.servlet.jsp.JspException;

import com.mindbox.pe.server.config.ConfigurationManager;

public class GetLoginUrlTag extends AbstractVarTag {

	private static final long serialVersionUID = 2010083110440000L;

	@Override
	public int doStartTag() throws JspException {
		String loginUrl = ConfigurationManager.getInstance().getSessionConfiguration().getLoginUrl();
		if (isEmptyAfterTrim(loginUrl)) {
			loginUrl = "/login.jsp";
		}

		setVarObject(loginUrl);

		return SKIP_BODY;
	}
}
