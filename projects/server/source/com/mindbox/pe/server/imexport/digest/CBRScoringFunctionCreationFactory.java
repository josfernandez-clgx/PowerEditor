package com.mindbox.pe.server.imexport.digest;

import org.xml.sax.Attributes;

import com.mindbox.pe.model.CBRScoringFunction;

public class CBRScoringFunctionCreationFactory extends AbstractCBRCachedObjectCreationFactory {

	@Override
	public Object createObject(Attributes arg0) throws Exception {
		int id = Integer.parseInt(arg0.getValue("id"));
		CBRScoringFunction scoringFunction = cbrManager.getCBRScoringFunction(id);
		return (scoringFunction == null ? new CBRScoringFunction(-1, String.valueOf(id), "") : scoringFunction);
	}

}
