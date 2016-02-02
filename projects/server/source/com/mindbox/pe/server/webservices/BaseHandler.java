package com.mindbox.pe.server.webservices;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.xml.ws.handler.MessageContext;

import org.apache.log4j.Logger;

public class BaseHandler<T extends MessageContext> {
	protected String HandlerName = null;

	private Logger logger = Logger.getLogger(getClass());

	@PostConstruct
	public void init() {
		logger.debug("------------------------------------");
		logger.debug("In Handler " + HandlerName + ":init()");
		logger.debug("Exiting Handler " + HandlerName + ":init()");
		logger.debug("------------------------------------");
	}

	@PreDestroy
	public void destroy() {
		logger.debug("------------------------------------");
		logger.debug("In Handler " + HandlerName + ":destroy()");
		logger.debug("Exiting Handler " + HandlerName + ":destroy()");
		logger.debug("------------------------------------");
	}


	public boolean handleFault(T mc) {
		logger.debug("------------------------------------");
		logger.debug("In Handler " + HandlerName + ":handleFault()");
		logger.debug("Exiting Handler " + HandlerName + ":handleFault()");
		logger.debug("------------------------------------");
		return true;
	}
	
	public void close(MessageContext mc) {
		logger.debug("------------------------------------");
		logger.debug("In Handler " + HandlerName + ":close()");
		logger.debug("Exiting Handler " + HandlerName + ":close()");
		logger.debug("------------------------------------");
	}

	public void setHandlerName(String handlerName) {
		HandlerName = handlerName;
	}
	
}
