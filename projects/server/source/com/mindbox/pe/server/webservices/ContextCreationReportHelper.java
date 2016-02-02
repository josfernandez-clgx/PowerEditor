package com.mindbox.pe.server.webservices;

import com.mindbox.pe.server.report.ErrorMessageStorage;

public class ContextCreationReportHelper implements ErrorMessageStorage {

	private PowerEditorInterfaceReturnStructure retStruct = null;
	//private final Date endDate;

	public ContextCreationReportHelper(PowerEditorInterfaceReturnStructure rs) {
		retStruct = rs;
//		this.endDate = (UtilBase.isEmpty(endDate) ? null : reportFilterDataHolder.parseDate(endDate));
	}

	public void addErrorMessage(String message) {
		if (retStruct != null) {
			retStruct.addErrorMessage(message);
		}
	}
}
