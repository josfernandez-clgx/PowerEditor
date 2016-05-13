package com.mindbox.pe.communication;


/**
 * Request to check if the specified template has at least one guideline.
 * @author Geneho Kim
 * @since PowerEditor 4.3.7
 */
public class TemplateHasGuidelineRequest extends AbstractTemplateIDRequest<BooleanResponse> {

	private static final long serialVersionUID = 2005092340000002L;

	/**
	 * @param userID userID
	 * @param sessionID sessionID
	 * @param templateID templateID
	 */
	public TemplateHasGuidelineRequest(String userID, String sessionID, int templateID) {
		super(userID, sessionID, templateID);
	}

}
