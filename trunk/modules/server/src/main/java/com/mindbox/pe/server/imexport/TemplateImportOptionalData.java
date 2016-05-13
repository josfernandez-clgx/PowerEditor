package com.mindbox.pe.server.imexport;

import java.util.List;
import java.util.Map;

final class TemplateImportOptionalData extends EntityImportOptionalData {

	private final List<Integer> unimportedTemplateIDs;
	private final Map<Integer, Integer> templateIDMap;
	private final Map<String, Integer> actionIDMap;

	public TemplateImportOptionalData(EntityImportOptionalData entityImportOptionalData, Map<Integer, Integer> templateIDMap,
			Map<String, Integer> actionIDMap, List<Integer> unimportedTemplateIDs) {
		super(entityImportOptionalData);
		this.templateIDMap = templateIDMap;
		this.actionIDMap = actionIDMap;
		this.unimportedTemplateIDs = unimportedTemplateIDs;
	}

	public List<Integer> getUnimportedTemplateIDs() {
		return unimportedTemplateIDs;
	}

	public Map<Integer, Integer> getTemplateIDMap() {
		return templateIDMap;
	}

	public Map<String, Integer> getActionIDMap() {
		return actionIDMap;
	}

}
