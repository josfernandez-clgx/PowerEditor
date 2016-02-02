package com.mindbox.pe.server.imexport.digest;

import org.xml.sax.Attributes;

import com.mindbox.pe.model.CBRCaseBase;

public class CBRCaseBaseCreationFactory extends AbstractCBRCachedObjectCreationFactory {

	@Override
	public Object createObject(Attributes arg0) throws Exception {
		int id = Integer.parseInt(arg0.getValue("id"));
		CBRCaseBase caseBase = cbrManager.getCBRCaseBase(id);
		return (caseBase == null ? new CBRCaseBase(-1, String.valueOf(id), "") : caseBase);
	}

}
