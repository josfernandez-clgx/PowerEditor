package com.mindbox.pe.client.common.filter;

import java.awt.event.ActionListener;
import java.util.List;

import com.mindbox.pe.communication.ServerException;
import com.mindbox.pe.model.Persistent;

/**
 * Filter operation interface.
 * 
 * @since PowerEditor 1.0
 */
public interface IFilterSubpanel<T extends Persistent> {

	/**
	 * Performs filter operation and returns the results in a List.
	 * @return the result of the filter operation
	 */
	List<T> doFilter() throws ServerException;

	/**
	 * Adds action listener for this panel.
	 * @param actionlistener the new action listener
	 */
	void addActionListener(ActionListener actionlistener);
}
