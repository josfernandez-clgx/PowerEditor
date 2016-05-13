package com.mindbox.pe.communication;

/**
 * @author Geneho Kim
 * @author MindBox
 * @since PowerEditor 2.1.0
 */
public abstract class AbstractTemplateColumnRequest<T extends ResponseComm> extends AbstractTemplateIDRequest<T> {

	private static final long serialVersionUID = 2003121611201000L;


	private final int columnID;
	
	/**
	 * @param userID
	 * @param sessionID
	 */
	protected AbstractTemplateColumnRequest(String userID, String sessionID, int templateID, int columnID) {
		super(userID, sessionID, templateID);
		this.columnID = columnID;
	}

	public final int getColumnID() {
		return columnID;
	}

}
