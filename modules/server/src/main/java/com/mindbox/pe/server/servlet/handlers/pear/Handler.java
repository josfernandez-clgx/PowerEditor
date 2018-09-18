package com.mindbox.pe.server.servlet.handlers.pear;

import java.lang.reflect.Method;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;

import com.mindbox.pe.communication.pear.Request;
import com.mindbox.pe.communication.pear.Response;

public class Handler {
    private static final Logger LOG = Logger.getLogger(Handler.class);

    private static final HashMap<Class<?>, Method> handlerMap = new HashMap<Class<?>, Method> ();

    public static Response process(Request<?> request, HttpServletRequest servletRequest) throws Exception {
        final String message = "process() not implemented";
        LOG.error(message);
        throw new Exception(message);
    }

    public static Method findHandler (Request<?> request) throws Exception {
        Class<?> requestClass = request.getClass();
        LOG.info("findHandler(): requestClass=" + requestClass.getName());
        Method handlerMethod = handlerMap.get(requestClass);
        if (null == handlerMethod) {
            String requestFQClassName = requestClass.getName(); // Fully qualified class name
            String requestClassName = requestFQClassName.substring(requestFQClassName.lastIndexOf('.') + 1); // Class name without package
            String handlerFQClassName = Handler.class.getName();   // Fully qualified name of class Handler
            String handlerPackageName = handlerFQClassName.substring(0, handlerFQClassName.lastIndexOf('.') + 1);
            String handlerFQName = handlerPackageName + requestClassName.replace("Request", "Handler");
            LOG.debug("findHandler() handlerFQName=" + handlerFQName);
            Class<?> handlerClass = Class.forName(handlerFQName);
            handlerMethod = handlerClass.getMethod("process", new Class[] {requestClass, HttpServletRequest.class});
            handlerMap.put(requestClass, handlerMethod);
        }

        return handlerMethod;
    }
}
