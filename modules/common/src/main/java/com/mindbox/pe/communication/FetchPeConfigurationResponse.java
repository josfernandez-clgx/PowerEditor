package com.mindbox.pe.communication;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.mindbox.pe.common.config.UserManagementConfig;
import com.mindbox.pe.model.TypeEnumValue;
import com.mindbox.pe.model.UserProfile;


public class FetchPeConfigurationResponse extends ResponseComm {

	private static final long serialVersionUID = 200606131331000L;

	private final String sessionID;
	private final String configContent;
	private final Map<String, List<TypeEnumValue>> typeEnumValueMap = new HashMap<String, List<TypeEnumValue>>();
	private final UserProfile userProfile;
	private final UserManagementConfig userManagementConfig;

	public FetchPeConfigurationResponse(String sessionID, String configContent, UserProfile userProfile, Map<String, List<TypeEnumValue>> typeEnumValueMap, UserManagementConfig userManagementConfig) {
		super();
		this.sessionID = sessionID;
		this.configContent = configContent;
		this.userProfile = userProfile;
		this.userManagementConfig = userManagementConfig;
		if (typeEnumValueMap != null) {
			this.typeEnumValueMap.putAll(typeEnumValueMap);
		}
	}

	public final String getConfigContent() {
		return configContent;
	}

	public String getSessionID() {
		return sessionID;
	}

	public Map<String, List<TypeEnumValue>> getTypeEnumValueMap() {
		return typeEnumValueMap;
	}

	public UserManagementConfig getUserManagementConfig() {
		return userManagementConfig;
	}

	public UserProfile getUserProfile() {
		return userProfile;
	}

	@Override
	public String toString() {
		return "FetchPeConfigurationResponse[session=" + sessionID + "]";
	}

}