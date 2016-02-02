package com.mindbox.pe.server.imexport;

import java.util.Map;

class EntityImportOptionalData extends AbstractImportOptionalData {

	private final Map<String, Integer> entityIDMap;
	private final ReplacementDateSynonymProvider replacementDateSynonymProvider;

	public EntityImportOptionalData(EntityImportOptionalData source) {
		this(source.replacementDateSynonymProvider, source.entityIDMap, source.getDateSynonymIDMap());
	}

	public EntityImportOptionalData(ReplacementDateSynonymProvider replacementDateSynonymProvider, Map<String, Integer> entityIDMap,
			Map<Integer, Integer> dateSynonymIDMap) {
		super(dateSynonymIDMap);
		this.replacementDateSynonymProvider = replacementDateSynonymProvider;
		this.entityIDMap = entityIDMap;
	}

	public ReplacementDateSynonymProvider getReplacementDateSynonymProvider() {
		return replacementDateSynonymProvider;
	}

	public Map<String, Integer> getEntityIDMap() {
		return entityIDMap;
	}

}
