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
public abstract class AbstractGenericCategoryActionRequest<T extends ResponseComm> extends SessionRequest<T> {
	/**
	 * 
	 */
	private static final long serialVersionUID = 3656090025287504162L;

	protected final int categoryID;
	protected final int categoryType;

	/**
	 * @param userID
	 * @param sessionID
	 */
	public AbstractGenericCategoryActionRequest(String userID, String sessionID, int categoryID, int categoryType) {
		super(userID, sessionID);
		this.categoryID = categoryID;
		this.categoryType = categoryType;
	}

	public int getCategoryID() {
		return categoryID;
	}

	public int getCategoryType() {
		return categoryType;
	}

}
