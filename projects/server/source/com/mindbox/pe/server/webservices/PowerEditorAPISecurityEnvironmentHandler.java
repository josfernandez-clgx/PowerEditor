package com.mindbox.pe.server.webservices;

import java.io.IOException;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.UnsupportedCallbackException;

import org.apache.log4j.Logger;

import com.sun.xml.wss.impl.callback.PasswordCallback;
import com.sun.xml.wss.impl.callback.PasswordValidationCallback;
import com.sun.xml.wss.impl.callback.UsernameCallback;
import java.io.BufferedReader;
import java.io.InputStreamReader;
//import java.util.StringTokenizer;
import com.mindbox.pe.server.servlet.handlers.LoginAttempt;
import com.mindbox.pe.server.servlet.ServletActionException;

public class PowerEditorAPISecurityEnvironmentHandler implements CallbackHandler {
   
	private Logger logger = Logger.getLogger(getClass());
	private String userName = null;
    private static final UnsupportedCallbackException unsupported =
    new UnsupportedCallbackException(null, "Unsupported Callback Type Encountered");
    
    /** Creates a new instance of PowerEditorAPISecurityEnvironmentHandler */
    public PowerEditorAPISecurityEnvironmentHandler(String arg) {
    }
    
    private String readLine() throws IOException {
        return new BufferedReader
            (new InputStreamReader(System.in)).readLine();
    }

    public void handle(Callback[] callbacks) throws IOException, UnsupportedCallbackException {
        for (int i=0; i < callbacks.length; i++) {
            if (callbacks[i] instanceof PasswordValidationCallback) {
                PasswordValidationCallback cb = (PasswordValidationCallback) callbacks[i];
                if (cb.getRequest() instanceof PasswordValidationCallback.PlainTextPasswordRequest) {
                    cb.setValidator(new PlainTextPasswordValidator());
                    
                } else if (cb.getRequest() instanceof PasswordValidationCallback.DigestPasswordRequest) {
                    PasswordValidationCallback.DigestPasswordRequest request =
                            (PasswordValidationCallback.DigestPasswordRequest) cb.getRequest();
                    //String username = request.getUsername();
                    
                    String pw = request.getPassword(); // SGS added
                    request.setPassword(pw);
                        cb.setValidator(new PasswordValidationCallback.DigestPasswordValidator());
                }
            } else if (callbacks[i] instanceof UsernameCallback) {
                UsernameCallback cb = (UsernameCallback)callbacks[i];
            	logger.debug("Username: ");
                String username= readLine();
                if (username != null) {
                    cb.setUsername(username);
                }
                
            } else if (callbacks[i] instanceof PasswordCallback) {
                PasswordCallback cb = (PasswordCallback)callbacks[i];
            	logger.debug("Password: ");

                String password = readLine();
                if (password != null) {
                    cb.setPassword(password);
                }
            } else {
                throw unsupported;
            }
        }
    }
    
     private class PlainTextPasswordValidator implements PasswordValidationCallback.PasswordValidator { 
        public boolean validate(PasswordValidationCallback.Request request)
        throws PasswordValidationCallback.PasswordValidationException {
            
            PasswordValidationCallback.PlainTextPasswordRequest plainTextRequest =
            (PasswordValidationCallback.PlainTextPasswordRequest) request;
            String un = plainTextRequest.getUsername();
            String pw = plainTextRequest.getPassword();
        	logger.debug("Username intercepted on server: " + un);
            
            boolean authenticated = false;
            try {
            	// SGS - Authenticate using PowerEditor mechanisms
            	LoginAttempt loginAttempt = new LoginAttempt(un, pw);
            	logger.debug("Checking login attempt " + loginAttempt);
            	authenticated = !loginAttempt.failed();
            } catch (ServletActionException sae) {
            	logger.error("ServletActionException occurred in WebService PlainTextPasswordValidator");
            } catch (Exception ex) {
            	
            }

            if (authenticated) {
            	setUserName(un);
                return true;
            }
            return false;
        }
    }

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}
}

