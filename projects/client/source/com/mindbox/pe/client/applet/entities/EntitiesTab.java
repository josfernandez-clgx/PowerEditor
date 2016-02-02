package com.mindbox.pe.client.applet.entities;

import java.util.Map;

import com.mindbox.pe.client.ClientUtil;
import com.mindbox.pe.client.applet.PowerEditorSwingTheme;
import com.mindbox.pe.client.applet.entities.compatibility.CompatibilityManagementPanel;
import com.mindbox.pe.client.applet.entities.generic.GenericEntityTab;
import com.mindbox.pe.client.common.tab.PowerEditorTab;
import com.mindbox.pe.common.config.EntityTabConfig;
import com.mindbox.pe.common.config.EntityTypeDefinition;
import com.mindbox.pe.model.EntityType;
import com.mindbox.pe.model.GenericEntityType;

/**
 * Entities Tab.
 * 
 * @since PowerEditor 1.0
 */
public class EntitiesTab extends PowerEditorTab {

	/**
	 * Tests if the specified entity type is configured to be displayed.
	 * 
	 * @param entityType
	 *            the entity type
	 * @return <code>true</code> if entityType should be displayed; <code>false</code>,
	 *         otherwise
	 * @since PowerEditor 3.3.0p4
	 */
	public static final boolean isEntityEnabledAndVisible(EntityType entityType) {
		EntityTabConfig etConfig = (EntityTabConfig) ClientUtil.getUserSession().getEntityTabConfigMap().get(entityType);
		return (etConfig != null && etConfig.isVisible());
	}

	private static EntityTypeDefinition findEntityTypeDefinition(GenericEntityType entityType, EntityTypeDefinition[] entityTypes) {
		for (int i = 0; i < entityTypes.length; i++) {
			if (entityTypes[i].getTypeID() == entityType.getID()) {
				return entityTypes[i];
			}
		}
		return null;
	}

	public EntitiesTab(boolean readOnly) {
		super();
		setFont(PowerEditorSwingTheme.tabFont);
		setFocusable(false);

		ClientUtil clientUtil = ClientUtil.getInstance();

		Map<GenericEntityType, EntityTabConfig> entityTabConfigMap = ClientUtil.getUserSession().getEntityTabConfigMap();
		EntityTypeDefinition[] entityTypes = ClientUtil.getEntityConfiguration().getEntityTypeDefinitions();
		EntityTabConfig entityTabConfig = null;

		if (ClientUtil.checkViewOrEditAnyEntityPermission()) {
			boolean hasGenericEntity = false;
			boolean useInCompatibility = false;

			for (Map.Entry<GenericEntityType, EntityTabConfig> entry : entityTabConfigMap.entrySet()) {
				GenericEntityType type = entry.getKey();
				entityTabConfig = entry.getValue();
				EntityTypeDefinition entityTypeDef = findEntityTypeDefinition(type, entityTypes);

				if (ClientUtil.checkViewOrEditEntityPermission(entityTypeDef)) {
					if (entityTabConfig != null && entityTabConfig.isVisible() && entityTypeDef != null) {
						boolean canEditEntities = !readOnly && ClientUtil.checkEditEntityPermission(entityTypeDef);

						ClientUtil.getLogger().info(String.format("Can user edit %s? = %b", type.getName(), canEditEntities));
						
						String defaultLabel = "Manage " + entityTypeDef.getDisplayName();
						addTab(
								clientUtil.getLabel("tab.entity." + entityTypeDef.getName(), defaultLabel),
								clientUtil.makeImageIcon("image.blank"),
								GenericEntityTab.createInstance(type, entityTypeDef.canClone(), canEditEntities),
								clientUtil.getLabel("tab.tooltip.entity." + entityTypeDef.getName(), defaultLabel));
						if (!hasGenericEntity) {
							hasGenericEntity = true;
						}
					}
				}

				// Check for useInCompatibility for each one. If even one is true, that's
				// enough.
				if (!useInCompatibility && entityTypeDef.useInCompatibility()) {
					useInCompatibility = true;
				}
			}

			if (hasGenericEntity && useInCompatibility) {
				CompatibilityManagementPanel cmPanel = new CompatibilityManagementPanel(readOnly);
				addTab(
						clientUtil.getLabel("tab.entity.compatibility"),
						clientUtil.makeImageIcon("image.blank"),
						cmPanel,
						clientUtil.getLabel("tab.tooltip.entity.compatibility"));
			}
		}
	}
}