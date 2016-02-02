package com.mindbox.pe.server.imexport.digest;

import org.xml.sax.Attributes;

import com.mindbox.pe.model.CBRAttributeType;

public class CBRAttributeTypeCreationFactory extends AbstractCBRCachedObjectCreationFactory {

	@Override
	public Object createObject(Attributes arg0) throws Exception {
		int id = Integer.parseInt(arg0.getValue("id"));
		CBRAttributeType attrType = cbrManager.getCBRAttributeType(id);
		return (attrType == null ? new CBRAttributeType(-1, String.valueOf(id), "") : attrType);
	}

}
