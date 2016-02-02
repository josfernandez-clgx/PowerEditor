/*
 * Created on 2003. 12. 17.
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package com.mindbox.pe.common.validate;

/**
 * 
 * @author Gene Kim
 * @author MindBox, Inc
 * @since PowerEditor 2.1.0
 */
public interface WarningConsumer {

	void addWarning(int level, String message);
	void addWarning(int level, String message, String resource);
}
