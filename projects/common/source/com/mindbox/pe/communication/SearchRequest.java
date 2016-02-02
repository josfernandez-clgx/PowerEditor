package com.mindbox.pe.communication;

import com.mindbox.pe.model.EntityType;
import com.mindbox.pe.model.Persistent;
import com.mindbox.pe.model.filter.SearchFilter;

/**
 * T represents the type of objects in the {@link ListResponse}.
 * @author Gene Kim
 * @author MindBox, Inc
 * @since PowerEditor 1.0
 */
public class SearchRequest<T extends Persistent> extends SessionRequest<ListResponse<T>> {

	private static final long serialVersionUID = 2003061611405000L;


	private final SearchFilter<T> filter;
	
	/**
	 * @param userID the user ID
	 * @param sessionID the session ID
	 */
	public SearchRequest(String userID, String sessionID, SearchFilter<T> filter) {
		super(userID, sessionID);
		this.filter = filter;
	}

	public SearchFilter<T> getSearchFilter() {
		return filter;
	}
	
	/**
	 * Convenicence method to get the entity type of the filter for this request.
	 * @return the filter entity type
	 */
	public EntityType getEntityType() {
		return filter.getEntityType();
	}
}
