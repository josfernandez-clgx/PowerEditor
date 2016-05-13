/*
 * Created on Jun 19, 2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package com.mindbox.pe.server.servlet;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import com.mindbox.pe.communication.RequestComm;
import com.mindbox.pe.server.servlet.handlers.IRequestCommHandler;

/**
 * Factory for request comm handlers.
 * This is a static singleton class.
 * @author Gene Kim
 * @author MindBox, Inc
 * @since PowerEditor 1.0
 */
final class HandlerFactory {

	@SuppressWarnings("rawtypes")
	private static final Map<Class<? extends RequestComm>, IRequestCommHandler> handlerMap = new HashMap<Class<? extends RequestComm>, IRequestCommHandler>();
	
	private static String parseClassTailName(String s) {
		int i = s.lastIndexOf('.');
		String s1;
		if (i == -1)
			s1 = "";
		else
			s1 = s.substring(i + 1);
		return s1;
	}


	/**
	 * Gets the request handler for the specified request.
	 * @param requestcomm the request for which to find the handler
	 * @return the handler for the specified request. Never <code>null</code>
	 * @throws IllegalArgumentException if a hander cannot be found for the specified request
	 */
	@SuppressWarnings("rawtypes")
	static IRequestCommHandler getHandler(RequestComm<?> requestcomm) {

		IRequestCommHandler requestHandler = null;
		requestHandler = handlerMap.get(requestcomm.getClass());
		if (requestHandler == null) {
			try {
				Class<? extends RequestComm> class1 = requestcomm.getClass();
				String s = parseClassTailName(class1.getName());
				String s1 = "com.mindbox.pe.server.servlet.handlers" + "." + s + "Handler";
				Class<?> class2 = Class.forName(s1);
				
				requestHandler = (IRequestCommHandler) class2.newInstance();
				handlerMap.put(requestcomm.getClass(), requestHandler);
			}
			catch (Exception exception) {
				Logger.getLogger(HandlerFactory.class).error("Error getting handler for " + requestcomm, exception);
				throw new IllegalArgumentException("Handler cannot be obtained for " + requestcomm.getClass().getName() + ": " + exception.getMessage());
			}
		}
		return requestHandler;
	}
	
	private HandlerFactory() {
	}
}
