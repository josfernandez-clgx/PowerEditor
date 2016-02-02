/*
 * Created on Jun 16, 2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package com.mindbox.pe.communication;

import com.mindbox.pe.model.Persistent;
import com.mindbox.pe.model.filter.SearchFilter;

/**
 * T represents type of objects to be found in the {@link ListResponse}.
 * @author Gene Kim
 * @author MindBox, Inc
 * @since PowerEditor 1.0
 */
public class NonSessionSearchRequest<T extends Persistent> extends RequestComm<ListResponse<T>> {

	private static final long serialVersionUID = 2003061611405009L;


	private final SearchFilter<T> filter;

	/**
	 * 
	 */
	public NonSessionSearchRequest(SearchFilter<T> filter) {
		super();
		this.filter = filter;
	}

	public SearchFilter<T> getSearchFilter() {
		return filter;
	}
}
