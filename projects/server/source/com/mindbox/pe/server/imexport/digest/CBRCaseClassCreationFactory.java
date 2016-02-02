package com.mindbox.pe.server.imexport.digest;

import org.xml.sax.Attributes;

import com.mindbox.pe.model.CBRCaseClass;

public class CBRCaseClassCreationFactory extends AbstractCBRCachedObjectCreationFactory {

	@Override
	public Object createObject(Attributes arg0) throws Exception {
		int id = Integer.parseInt(arg0.getValue("id"));
		CBRCaseClass caseClass = cbrManager.getCBRCaseClass(id);
		return (caseClass == null ? new CBRCaseClass(-1, String.valueOf(id), "") : caseClass);
	}

}
