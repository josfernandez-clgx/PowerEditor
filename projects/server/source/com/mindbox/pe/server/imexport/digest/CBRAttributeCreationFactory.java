package com.mindbox.pe.server.imexport.digest;

import org.xml.sax.Attributes;

import com.mindbox.pe.model.CBRAttribute;

public class CBRAttributeCreationFactory extends AbstractCBRCachedObjectCreationFactory {

	@Override
	public Object createObject(Attributes arg0) throws Exception {
		int id = Integer.parseInt(arg0.getValue("id"));
		CBRAttribute attribute = cbrManager.getCBRAttribute(id);
		return (attribute == null ? new CBRAttribute(-1, String.valueOf(id), "") : attribute);
	}

}
