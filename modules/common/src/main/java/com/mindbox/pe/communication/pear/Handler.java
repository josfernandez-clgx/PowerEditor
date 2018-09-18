package com.mindbox.pe.communication.pear;

import java.lang.reflect.Method;
import java.util.HashMap;

import org.apache.log4j.Logger;

public class Handler {

    private static final Logger LOG = Logger.getLogger(Handler.class);

    private static final HashMap<Class<?>, Method> handlerMap = new HashMap<Class<?>, Method> ();

    public static Response process(Request<?> request) throws Exception {
        final String message = "process() not implemented";
        LOG.error(message);
        throw new Exception(message);
    }

    public static Method findHandler (Request<?> request) throws Exception {
        Class<?> requestClass = request.getClass();
        Method handlerMethod = handlerMap.get(requestClass);
        if (null == handlerMethod) {
            String requestClassName = requestClass.getName();
            String handlerName = requestClassName.replace("Request", "") + "Handler";
            LOG.debug("findHandler() handlerName=" + handlerName);
            Class<?> handlerClass = Class.forName(handlerName);
            handlerMethod = handlerClass.getMethod("process", new Class[] {requestClass});
            handlerMap.put(requestClass, handlerMethod);
        }

        return handlerMethod;
    }
}
