package com.mindbox.pe.server.imexport.digest;

import org.xml.sax.Attributes;

import com.mindbox.pe.model.CBRCaseAction;

public class CBRCaseActionCreationFactory extends AbstractCBRCachedObjectCreationFactory {

	@Override
	public Object createObject(Attributes arg0) throws Exception {
		int id = Integer.parseInt(arg0.getValue("id"));
		CBRCaseAction caseAction = cbrManager.getCBRCaseAction(id);
		return (caseAction == null ? new CBRCaseAction(-1, String.valueOf(id), "") : caseAction);
	}

}
