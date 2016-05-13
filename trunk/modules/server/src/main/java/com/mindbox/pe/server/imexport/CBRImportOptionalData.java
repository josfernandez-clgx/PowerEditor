package com.mindbox.pe.server.imexport;

import java.util.Map;

class CBRImportOptionalData extends AbstractImportOptionalData {

	private final Map<String, Integer> cbrDataIdMap;

	protected CBRImportOptionalData(Map<String, Integer> cbrDataIdMap, Map<Integer, Integer> dateSynonymIDMap) {
		super(dateSynonymIDMap);
		this.cbrDataIdMap = cbrDataIdMap;
	}

	public Map<String, Integer> getCbrDataIdMap() {
		return cbrDataIdMap;
	}
}
