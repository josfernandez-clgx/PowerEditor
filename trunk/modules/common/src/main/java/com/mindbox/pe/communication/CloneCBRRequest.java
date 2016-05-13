/*
 * Created on Nov 2, 2004
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package com.mindbox.pe.communication;

/**
 * 
 * @author Inna Nill
 * @author MindBox, LLC
 * @since PowerEditor 4.1.0
 */
public class CloneCBRRequest extends SessionRequest<CloneCBRResponse> {

    private static final long serialVersionUID = 2004110211064000L;

    private final int oldCaseBaseID;
    private final String newCaseBaseName;
	
    /**
     * @param userID
     * @param sessionID
     */
    public CloneCBRRequest(String userID, String sessionID, int oldCaseBaseID, String newCaseBaseName) {
    	super(userID, sessionID);
    	this.oldCaseBaseID = oldCaseBaseID;
    	this.newCaseBaseName = newCaseBaseName;
    }

    public String getNewCaseBaseName() {
    	return newCaseBaseName;
    }
    public int getOldCaseBaseID() {
    	return oldCaseBaseID;
    }

}
