package com.mindbox.pe.server.tag;

import javax.servlet.jsp.JspException;

import org.apache.log4j.Logger;

import com.mindbox.pe.common.XmlUtil;
import com.mindbox.pe.server.config.ConfigurationManager;
import com.mindbox.pe.xsd.config.LogFileConfig;
import com.mindbox.pe.xsd.config.LogFileType;
import com.mindbox.pe.xsd.config.PowerEditorConfiguration;

public class GetConfigurationTag extends AbstractVarTag {

	private static final long serialVersionUID = 2010083110440000L;

	private String serverLogVar;
	private String configXmlContentVar;

	@Override
	public int doStartTag() throws JspException {
		setVarObject(ConfigurationManager.getInstance());

		if (serverLogVar != null) {
			for (final LogFileConfig logFileConfig : ConfigurationManager.getInstance().getPowerEditorConfiguration().getServer().getLog().getLogFile()) {
				if (logFileConfig.getType() == LogFileType.SERVER) {
					pageContext.setAttribute(serverLogVar, logFileConfig);
					break;
				}
			}
		}

		if (configXmlContentVar != null) {
			try {
				pageContext.setAttribute(configXmlContentVar, XmlUtil.marshal(ConfigurationManager.getInstance().getPowerEditorConfiguration(), false, PowerEditorConfiguration.class));
			}
			catch (Exception e) {
				Logger.getLogger(getClass()).error("Failed to get PE config XML", e);
				pageContext.setAttribute(configXmlContentVar, String.format("Error while getting config file: %s", e.getMessage()));
			}
		}
		return SKIP_BODY;
	}

	public final String getConfigXmlContentVar() {
		return configXmlContentVar;
	}

	public String getServerLogVar() {
		return serverLogVar;
	}

	public final void setConfigXmlContentVar(String configXmlContentVar) {
		this.configXmlContentVar = configXmlContentVar;
	}

	public void setServerLogVar(String serverLogVar) {
		this.serverLogVar = serverLogVar;
	}

}
