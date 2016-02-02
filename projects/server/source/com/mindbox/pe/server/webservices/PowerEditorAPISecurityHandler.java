package com.mindbox.pe.server.webservices;

//import java.util.Map;
import java.io.FileInputStream;
import java.util.Set;

import javax.xml.namespace.QName;
import javax.xml.soap.SOAPMessage;
import javax.xml.ws.WebServiceException;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.ws.handler.soap.SOAPMessageContext;

import org.apache.log4j.Logger;

import com.mindbox.pe.server.config.ConfigurationManager;
import com.sun.xml.wss.ProcessingContext;
import com.sun.xml.wss.SubjectAccessor;
import com.sun.xml.wss.XWSSProcessor;
import com.sun.xml.wss.XWSSProcessorFactory;
import com.sun.xml.wss.XWSSecurityException;

public class PowerEditorAPISecurityHandler extends
		BaseHandler<SOAPMessageContext> implements
		SOAPHandler<SOAPMessageContext> {
	private final String HANDLER_NAME = "PowerEditorAPISecurityHandler";
	private Logger logger = Logger.getLogger(getClass());

	XWSSProcessor sprocessor = null;
	PowerEditorAPISecurityEnvironmentHandler peSecurityEnvHandler = null;

	public PowerEditorAPISecurityHandler() {
		super();
		super.setHandlerName(HANDLER_NAME);
	}
	
	// Moved this code from constructor since config manager is not available here.
	private void createSecurityConfigProcessor() {

        FileInputStream serverConfig = null;
        try {

            //read server side security configuration
        	String webInfDir = ConfigurationManager.getInstance().getServerBasePath() + "/WEB-INF";
        	String fullServerConfigPath = webInfDir + "/" + "user-pass-authenticate-server.xml";
            serverConfig = new java.io.FileInputStream(new java.io.File(fullServerConfigPath));
            //Create a XWSSProcessFactory.
            XWSSProcessorFactory factory = XWSSProcessorFactory.newInstance();
            peSecurityEnvHandler = new PowerEditorAPISecurityEnvironmentHandler("server");
            sprocessor = factory.createProcessorForSecurityConfiguration(
                    serverConfig, peSecurityEnvHandler);
            serverConfig.close();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
	}

	public boolean handleMessage(SOAPMessageContext smc) {
		logger.debug("------------------------------------");
		logger.debug("In SOAPHandler " + HandlerName + ":handleMessage()");
		secureServer(smc);
		return true;
	}

	public Set<QName> getHeaders() {
		return null;
	}

    public boolean handleFault(SOAPMessageContext messageContext) {
        return true;
    }
    
    //splice to end
    public void close(MessageContext messageContext) {}

    @SuppressWarnings("unchecked")
	private synchronized void secureServer(SOAPMessageContext messageContext) 
    {
    	if (sprocessor == null) {
    		createSecurityConfigProcessor();
    	}
        Boolean outMessageIndicator = (Boolean)        
        messageContext.get (MessageContext.MESSAGE_OUTBOUND_PROPERTY);
        SOAPMessage message = messageContext.getMessage();
        
        if (outMessageIndicator.booleanValue()) {
    		logger.debug("\nOutbound SOAP:");
            // do nothing....
            return;
        } else {
    		logger.debug("\nInbound SOAP:");
             //verify the secured message.
            try{
                ProcessingContext context =  sprocessor.createProcessingContext(message);
                context.getExtraneousProperties().putAll(messageContext);
                context.setSOAPMessage(message);
                SOAPMessage verifiedMsg= null;
                verifiedMsg= sprocessor.verifyInboundMessage(context);
                String userName = peSecurityEnvHandler.getUserName();
                
                // Very tough to get hold of user name by "pushing it" from the Callback.  Instead we pull it
                // from within a synchronized method.
                peSecurityEnvHandler.setUserName(null); // a little kludgy, but with callbacks, it's hard to know when it is no longer needed.
                //verifiedMsg.getMimeHeaders().addHeader("username", userName);

                //System.out.println("\nRequester Subject " + SubjectAccessor.getRequesterSubject(context));
        		logger.debug("\nRequester Subject " + SubjectAccessor.getRequesterSubject(context));
                messageContext.setMessage(verifiedMsg);
                
           
                // A little kludgy, but need to make sure that there is no user name set.
                // This service should not get heavy volume.  We need a way to pass back the user name.
                // This should work because if there are two of these processes going, the first will get in
                // and set the name and be able to continue.  The second process will delay here and go next or fail.
                // The process in PowerEditorAPIInterface (the WebService) will start only once this process completes.
                int pushTryCount = 0;
                boolean pushSuccess = false;
                while (!pushSuccess && pushTryCount < 10) {
                	pushSuccess = PowerEditorAPIInterface.pushUsername(userName);
                	pushTryCount++;
                	if (!pushSuccess) { 
                		Thread.sleep (250); // in milliseconds
                	}
                }
                if (!pushSuccess) {
                	logger.error("Concurrency problem running request for user " + userName);
                	throw new Exception("Concurrency problem running request for user " + userName);
                }
        		
            } catch (XWSSecurityException ex) {
                //create a Message with a Fault in it
                ex.printStackTrace();
                throw new WebServiceException(ex);
            } catch(Exception ex){
                ex.printStackTrace();
                throw new WebServiceException(ex);
            }
        }
    }    

}
