package com.mindbox.pe.server.imexport;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

class EntityImportOptionalData extends AbstractImportOptionalData {

	private final Map<String, Integer> entityIDMap;
	private final List<Integer> importedEntityIDs = new ArrayList<Integer>();
	private final ReplacementDateSynonymProvider replacementDateSynonymProvider;

	public EntityImportOptionalData(EntityImportOptionalData source) {
		this(source.replacementDateSynonymProvider, source.entityIDMap, source.getDateSynonymIDMap());
	}

	public EntityImportOptionalData(ReplacementDateSynonymProvider replacementDateSynonymProvider, Map<String, Integer> entityIDMap, Map<Integer, Integer> dateSynonymIDMap) {
		super(dateSynonymIDMap);
		this.replacementDateSynonymProvider = replacementDateSynonymProvider;
		this.entityIDMap = entityIDMap;
	}

	public Map<String, Integer> getEntityIDMap() {
		return entityIDMap;
	}

	public ReplacementDateSynonymProvider getReplacementDateSynonymProvider() {
		return replacementDateSynonymProvider;
	}

	public boolean isImported(final int entityID) {
		synchronized (importedEntityIDs) {
			return importedEntityIDs.contains(entityID);
		}
	}

	public void markAsImported(final int entityID) {
		synchronized (importedEntityIDs) {
			importedEntityIDs.add(entityID);
		}
	}

}
