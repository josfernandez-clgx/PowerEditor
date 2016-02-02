/*
 * Created on 2004. 4. 20.
 *
 */
package com.mindbox.pe.communication;


/**
 * @author Geneho Kim
 * @author MindBox
 * @since PowerEditor 3.0.0
 */
public class GenericCategoryResponselessActionRequest extends AbstractGenericCategoryActionRequest<AbstractSimpleResponse> {

	private static final long serialVersionUID = 7831090318036430612L;
	private final int actionType;

	/**
	 * 
	 * @param userID
	 * @param sessionID
	 * @param categoryID
	 * @param categoryType
	 * @param actionType
	 */
	public GenericCategoryResponselessActionRequest(String userID, String sessionID, int categoryID, int categoryType, int actionType) {
		super(userID, sessionID, categoryID, categoryType);
		this.actionType = actionType;
	}

	public int getActionType() {
		return actionType;
	}

}