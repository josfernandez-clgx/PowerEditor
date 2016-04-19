/*
 * Created on 2004. 10. 6.
 */
package com.mindbox.pe.client.common.tab;

import com.mindbox.pe.communication.ServerException;
import com.mindbox.pe.model.exceptions.CanceledException;

/**
 * Interface all top level panel in a tab of a {@link PowerEditorTab} instance.
 * This provides framework for consistent UI for detecting unsaved changes on tab changes.
 * @author kim
 * @author MindBox
 * @since PowerEditor 4.0.0
 */
public interface PowerEditorTabPanel {

	/**
	 * Tests if this has any unsaved changes.
	 * @return <code>true</code> if there are unsaved changes;
	 *         <code>false</code>, otherwise
	 */
	boolean hasUnsavedChanges();
	
	/**
	 * Saves changes in this panel.
	 * This should throw an instance of <code>java.lang.RuntimeException</code> on any other
	 * errors that do not correspond with any of the declared exceptions. Such errors 
	 * will be reported to the user as a client error.
	 * <p>
	 * If this throws an exception, tag change will not occur.
	 * 
	 * @throws CanceledException when save operation must be canceled; this means that 
	 *                           the tab change event is discarded and the tab 
	 *                           previous selected remain selected
	 * @throws ServerException when an error occurs while communicating with the server
	 */
	void saveChanges() throws CanceledException, ServerException;

	/**
	 * Discards all the changes in this panel.
	 * Sets all "save" and "dirty" indicators to false.
	 */
	void discardChanges();
}
