package com.mindbox.pe.server.imexport;

import java.util.Map;

abstract class AbstractImportOptionalData {

	private final Map<Integer, Integer> dateSynonymIDMap;

	protected AbstractImportOptionalData(Map<Integer, Integer> dateSynonymIDMap) {
		this.dateSynonymIDMap = dateSynonymIDMap;
	}

	public Map<Integer, Integer> getDateSynonymIDMap() {
		return dateSynonymIDMap;
	}
}
