package com.mindbox.pe.client;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.mindbox.pe.common.config.EntityConfigHelper;
import com.mindbox.pe.common.config.UserManagementConfig;
import com.mindbox.pe.model.GenericEntityType;
import com.mindbox.pe.xsd.config.EntityTab;
import com.mindbox.pe.xsd.config.GuidelineTab;
import com.mindbox.pe.xsd.config.PowerEditorConfiguration;
import com.mindbox.pe.xsd.config.UserInterfaceConfig;

public class ConfigHelper {

	private final PowerEditorConfiguration powerEditorConfiguration;
	private final EntityConfigHelper entityConfigHelper;
	private final UserManagementConfig userManagementConfig;
	private final Map<GenericEntityType, EntityTab> entityTabMap = new HashMap<GenericEntityType, EntityTab>();

	public ConfigHelper(final PowerEditorConfiguration powerEditorConfiguration, final UserManagementConfig userManagementConfig) {
		if (powerEditorConfiguration == null) {
			throw new IllegalArgumentException("configuration cannot be null");
		}
		if (powerEditorConfiguration.getEntityConfig() == null) {
			throw new IllegalArgumentException("entityConfig cannot be null");
		}

		this.powerEditorConfiguration = powerEditorConfiguration;
		this.entityConfigHelper = new EntityConfigHelper(powerEditorConfiguration.getEntityConfig());
		this.userManagementConfig = userManagementConfig;

		if (powerEditorConfiguration.getUserInterface().getEntity() != null) {
			for (final EntityTab entityTab : powerEditorConfiguration.getUserInterface().getEntity().getEntityTab()) {
				final GenericEntityType genericEntityType = GenericEntityType.forName(entityTab.getType());
				if (genericEntityType != null) {
					entityTabMap.put(genericEntityType, entityTab);
				}
			}
		}
	}

	public final EntityConfigHelper getEntityConfigHelper() {
		return entityConfigHelper;
	}

	public Map<GenericEntityType, EntityTab> getEntityTabMap() {
		return entityTabMap;
	}

	public List<GuidelineTab> getGuidelineTabs() {
		return powerEditorConfiguration.getUserInterface().getGuideline().getGuidelineTab();
	}

	public final PowerEditorConfiguration getPowerEditorConfiguration() {
		return powerEditorConfiguration;
	}

	public UserInterfaceConfig getUserInterfaceConfig() {
		return powerEditorConfiguration.getUserInterface();
	}

	public UserManagementConfig getUserManagementConfig() {
		return userManagementConfig;
	}
}
