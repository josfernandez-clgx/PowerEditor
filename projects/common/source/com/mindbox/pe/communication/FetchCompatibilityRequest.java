/*
 * Created on 2004. 4. 20.
 *  
 */
package com.mindbox.pe.communication;

import com.mindbox.pe.model.GenericEntityType;
import com.mindbox.pe.model.assckey.GenericEntityCompatibilityData;

/**
 * @author Geneho Kim
 * @author MindBox
 * @since PowerEditor 3.0.0
 */
public class FetchCompatibilityRequest extends SessionRequest<ListResponse<GenericEntityCompatibilityData>> {

	private static final long serialVersionUID = -6367589302016282668L;

	private final GenericEntityType sourceType, targetType;

	public FetchCompatibilityRequest(String userID, String sessionID, GenericEntityType sourceType, 
			GenericEntityType targetType) {
		super(userID, sessionID);
		this.sourceType = sourceType;
		this.targetType = targetType;
	}

	public GenericEntityType getSourceType() {
		return sourceType;
	}

	public GenericEntityType getTargetType() {
		return targetType;
	}
	
	public String toString() {
		return "FetchCompatibilityRequest[" + sourceType+","+targetType+"-"+super.getUserID()+"]";
	}
}