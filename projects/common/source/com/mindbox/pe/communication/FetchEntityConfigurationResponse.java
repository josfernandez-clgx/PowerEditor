package com.mindbox.pe.communication;

import java.util.List;
import java.util.Map;

import com.mindbox.pe.common.config.EntityConfiguration;
import com.mindbox.pe.common.config.KbDateFilterConfig;
import com.mindbox.pe.model.TypeEnumValue;

public class FetchEntityConfigurationResponse extends ResponseComm {

	private static final long serialVersionUID = 200606131331000L;

	private final String sessionID;

	private final Map<String, List<TypeEnumValue>> typeEnumValueMap;

	private final EntityConfiguration entityConfiguration;

	private final KbDateFilterConfig kbDateFilterConfig;

	public FetchEntityConfigurationResponse(String sessionID, EntityConfiguration entityConfiguration,
			KbDateFilterConfig kbDateFilterConfig, Map<String, List<TypeEnumValue>> typeEnumValueMap) {
		this.sessionID = sessionID;
		this.entityConfiguration = entityConfiguration;
		this.typeEnumValueMap = typeEnumValueMap;
		this.kbDateFilterConfig = kbDateFilterConfig;
	}

	public Map<String, List<TypeEnumValue>> getTypeEnumValueMap() {
		return typeEnumValueMap;
	}

	public String getSessionID() {
		return sessionID;
	}

	public String toString() {
		return "FetchEntityTypeDefinitionResponse[session=" + sessionID + "]";
	}

	public EntityConfiguration getEntityConfiguration() {
		return entityConfiguration;
	}

	public KbDateFilterConfig getKbDateFilterConfig() {
		return kbDateFilterConfig;
	}

}