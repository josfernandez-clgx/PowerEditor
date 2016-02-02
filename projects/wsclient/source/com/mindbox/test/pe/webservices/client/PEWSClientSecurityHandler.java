/*
 * PEWSClientSecurityHandler.java
 */

package com.mindbox.test.pe.webservices.client;

import java.io.FileInputStream;
import java.util.Set;

import javax.xml.namespace.QName;
import javax.xml.soap.SOAPMessage;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.ws.handler.soap.SOAPMessageContext;

import com.sun.xml.wss.ProcessingContext;
import com.sun.xml.wss.XWSSProcessor;
import com.sun.xml.wss.XWSSProcessorFactory;
import com.sun.xml.wss.XWSSecurityException;

/**
 *
 * @author Schneider adapted from example by Kumar Jayanti
 */

public class PEWSClientSecurityHandler implements SOAPHandler<SOAPMessageContext> {

	private XWSSProcessor cprocessor = null;

	/** Creates a new instance of PEWSClientSecurityHandler */
	public PEWSClientSecurityHandler() {
		FileInputStream clientConfig = null;
		try {
			//read client side security config
			clientConfig = new java.io.FileInputStream(new java.io.File("user-pass-authenticate-client.xml"));
			//Create a XWSSProcessFactory.
			XWSSProcessorFactory factory = XWSSProcessorFactory.newInstance();
			cprocessor = factory.createProcessorForSecurityConfiguration(clientConfig, new PEWSClientSecurityEnvironmentHandler()); //"client"));
			clientConfig.close();
		}
		catch (Exception e) {
			//e.printStackTrace();
			throw new RuntimeException(e);
		}

	}

	public Set<QName> getHeaders() {
		return null;
	}

	public boolean handleFault(SOAPMessageContext messageContext) {
		return true;
	}

	public boolean handleMessage(SOAPMessageContext messageContext) {
		secureClient(messageContext);
		return true;
	}

	public void close(MessageContext messageContext) {
	}

	@SuppressWarnings("unchecked")
	private void secureClient(SOAPMessageContext messageContext) {
		Boolean outMessageIndicator = (Boolean) messageContext.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY);
		SOAPMessage message = messageContext.getMessage();
		System.out.println("Came to Secure Client.............");
		if (outMessageIndicator.booleanValue()) {
			System.out.println("\nOutbound SOAP:");
			ProcessingContext context;
			try {
				context = cprocessor.createProcessingContext(message);
				context.getExtraneousProperties().putAll(messageContext);
				context.setSOAPMessage(message);
				SOAPMessage secureMsg = cprocessor.secureOutboundMessage(context);
				secureMsg.writeTo(System.out);
				messageContext.setMessage(secureMsg);
			}
			catch (XWSSecurityException ex) {
				ex.printStackTrace();
				throw new RuntimeException(ex);
			}
			catch (Exception e) {
				e.printStackTrace();
				throw new RuntimeException(e);
			}

			return;
		}
		else {
			System.out.println("\nInbound SOAP:");
			System.out.println("DO Nothing in Secure Client.............");
			//do nothing 
			return;
		}

	}
}
