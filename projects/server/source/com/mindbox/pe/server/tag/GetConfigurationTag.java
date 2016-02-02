package com.mindbox.pe.server.tag;

import javax.servlet.jsp.JspException;

import com.mindbox.pe.server.config.ConfigurationManager;

public class GetConfigurationTag extends AbstractVarTag {

	private static final long serialVersionUID = 2010083110440000L;

	private String serverLogVar;

	@Override
	public int doStartTag() throws JspException {
		setVarObject(ConfigurationManager.getInstance());

		pageContext.setAttribute(serverLogVar, ConfigurationManager.getInstance().getServerConfiguration().getLogConfig("server"));
		return SKIP_BODY;
	}

	public String getServerLogVar() {
		return serverLogVar;
	}

	public void setServerLogVar(String serverLogVar) {
		this.serverLogVar = serverLogVar;
	}
	
}
