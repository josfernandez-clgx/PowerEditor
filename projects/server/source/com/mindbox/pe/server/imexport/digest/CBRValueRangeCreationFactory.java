package com.mindbox.pe.server.imexport.digest;

import org.xml.sax.Attributes;

import com.mindbox.pe.model.CBRValueRange;

public class CBRValueRangeCreationFactory extends AbstractCBRCachedObjectCreationFactory {

	@Override
	public Object createObject(Attributes arg0) throws Exception {
		int id = Integer.parseInt(arg0.getValue("id"));
		CBRValueRange valueRange = cbrManager.getCBRValueRange(id);
		return (valueRange == null ? new CBRValueRange(-1, String.valueOf(id), "", "", false, false, false, false, false) : valueRange);
	}

}
