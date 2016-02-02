/*
 * PEWSClientSecurityEnvironmentHandler.java
 *
 */

package com.mindbox.test.pe.webservices.client;

import java.io.IOException;

import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.UnsupportedCallbackException;
import javax.xml.ws.BindingProvider;

import com.sun.xml.wss.impl.callback.PasswordCallback;
import com.sun.xml.wss.impl.callback.UsernameCallback;


/**
 *
 * @author Schneider, adapted from example by sk112103
 */
public class PEWSClientSecurityEnvironmentHandler implements CallbackHandler {
   
    private static final UnsupportedCallbackException unsupported =
    new UnsupportedCallbackException(null, "Unsupported Callback Type Encountered");
    

    /** Creates a new instance of PEWSClientSecurityEnvironmentHandler */
    public PEWSClientSecurityEnvironmentHandler() {
    }
    
    public void handle(Callback[] callbacks) throws IOException, UnsupportedCallbackException {
    	System.out.println("In client side callbacks");
        for (int i=0; i < callbacks.length; i++) {
        	System.out.println("callbacks[" + i + "] = " + callbacks[i]);
            if (callbacks[i] instanceof UsernameCallback) {
                UsernameCallback cb = (UsernameCallback)callbacks[i];
                String username = (String)cb.getRuntimeProperties().get(BindingProvider.USERNAME_PROPERTY);
                System.out.println("Got Username (Client)......... : " + username);
                cb.setUsername(username);
                
            } else if (callbacks[i] instanceof PasswordCallback) {
                PasswordCallback cb = (PasswordCallback)callbacks[i];
                String password = (String)cb.getRuntimeProperties().get(BindingProvider.PASSWORD_PROPERTY);
                System.out.println("Got Password (Client)......... : " + password);
                cb.setPassword(password);
                
            } else {
                throw unsupported;
            }
        }
    }
}
