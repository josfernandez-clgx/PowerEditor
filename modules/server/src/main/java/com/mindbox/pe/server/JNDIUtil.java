/*
 * Created on Mar 30, 2006
 *
 */
package com.mindbox.pe.server;

import java.io.UnsupportedEncodingException;
import java.util.Date;

import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;


/**
 * Provides utility methods for JNDI api.
 * @author Geneho Kim
 * @since PowerEditor 4.5.0
 */
public final class JNDIUtil {

	public static String asPassword(Attribute attr) throws NamingException {
		if (attr == null) return null;
		Object pwdValue = attr.get();
		if (pwdValue == null)
			return null;
		else if (pwdValue instanceof String) {
			return (String) pwdValue;
		}
		else if (pwdValue instanceof byte[]) {
			try {
				return new String((byte[]) pwdValue, "utf-8");
			}
			catch (UnsupportedEncodingException e) {
				// this should not happen as all JVM should support UTF-8 encoding
				return new String((byte[]) pwdValue);
			}
		}
		else {
			return pwdValue.toString();
		}
	}

	public static String getRequiredAttributeAsString(Attributes attrs, String attributeName) throws NamingException {
		Object value = getAttributeValue(attrs, attributeName);
		if (value == null)
			throw new NamingException(attributeName + " attribute has null value");
		else if (value instanceof String) {
			return (String) value;
		}
		else {
			return value.toString();
		}
	}

	public static String getAttributeAsString(Attributes attrs, String attributeName) throws NamingException {
		Object value = getAttributeValue(attrs, attributeName);
		if (value == null)
			return null;
		else if (value instanceof String) {
			return (String) value;
		}
		else {
			return value.toString();
		}
	}

	public static boolean getAttributeAsBoolean (Attributes attrs, String attributeName) throws NamingException {
		Object value = getAttributeValue(attrs, attributeName);
		if (value == null){
			throw new NamingException(attributeName + " attribute has null value");
		}
		else if(!value.toString().equalsIgnoreCase("true") && !value.toString().equalsIgnoreCase("false")){
			throw new NamingException(attributeName + " attribute has invalid value");
		}
		else {
				boolean b = new Boolean(value.toString()).booleanValue();
			return b;
		}
	}
	
	public static int getAttributeAsInteger (Attributes attrs, String attributeName) throws NamingException {
		Object value = getAttributeValue(attrs, attributeName);
		if (value == null){
			throw new NamingException(attributeName + " attribute has null value");
		}
		else{
			 try{
				 int i = new Integer(value.toString()).intValue();
				 return i;
			 }catch(NumberFormatException e){
				 throw new NamingException(attributeName + " attribute has invalid value"); 
			 }			
		}
	}

//TODO Vineet !!! implement string to date conversion
	public static Date getAttributeAsDate (Attributes attrs, String attributeName) throws NamingException {
		Object value = getAttributeValue(attrs, attributeName);
		if (value == null){
			throw new NamingException(attributeName + " attribute has null value");
		}
		else{
			return new Date();
		}
	}
	
	
	private static Object getAttributeValue(Attributes attrs, String attributeName) throws NamingException {
		Attribute attr = attrs.get(attributeName);
		return (attr == null ? null : attr.get());
	}

	private JNDIUtil() {
	}
	
	
	
}
