package com.mindbox.pe.server.tag;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

import com.mindbox.pe.server.spi.ServiceProviderFactory;

public class NotifySsoAuthenticationTag extends TagSupport {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6716085504938527214L;

	private String userId;

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	@Override
	public int doStartTag() throws JspException {
		try {
			ServiceProviderFactory.getUserAuthenticationProvider().notifySsoAuthentication(userId);
		}
		catch (Exception e) {
			throw new JspException("Failed to motify SSO authentication for " + userId, e);
		}
		return SKIP_BODY;
	}

}
