package com.mindbox.pe.client.applet.entities;

import java.util.List;
import java.util.Map;

import com.mindbox.pe.client.ClientUtil;
import com.mindbox.pe.client.applet.PowerEditorSwingTheme;
import com.mindbox.pe.client.applet.entities.compatibility.CompatibilityManagementPanel;
import com.mindbox.pe.client.applet.entities.generic.GenericEntityTab;
import com.mindbox.pe.client.common.tab.PowerEditorTab;
import com.mindbox.pe.common.UtilBase;
import com.mindbox.pe.common.config.ConfigUtil;
import com.mindbox.pe.model.GenericEntityType;
import com.mindbox.pe.model.PeDataType;
import com.mindbox.pe.xsd.config.EntityTab;
import com.mindbox.pe.xsd.config.EntityType;

/**
 * Entities Tab.
 * 
 * @since PowerEditor 1.0
 */
public class EntitiesTab extends PowerEditorTab {
	/**
	 * 
	 */
	private static final long serialVersionUID = -3951228734910107454L;

	/**
	 * Tests if the specified entity type is configured to be displayed.
	 * 
	 * @param entityType
	 *            the entity type
	 * @return <code>true</code> if entityType should be displayed; <code>false</code>,
	 *         otherwise
	 * @since PowerEditor 3.3.0p4
	 */
	public static final boolean isEntityEnabledAndVisible(PeDataType entityType) {
		EntityTab etConfig = ClientUtil.getEntityTabMap().get(entityType);
		return (etConfig != null && UtilBase.asBoolean(etConfig.isShowTab(), true));
	}

	private static EntityType findEntityType(GenericEntityType entityType, List<EntityType> entityTypes) {
		for (int i = 0; i < entityTypes.size(); i++) {
			if (entityTypes.get(i).getTypeID().intValue() == entityType.getID()) {
				return entityTypes.get(i);
			}
		}
		return null;
	}

	public EntitiesTab(boolean readOnly) {
		super();
		setFont(PowerEditorSwingTheme.tabFont);
		setFocusable(false);

		ClientUtil clientUtil = ClientUtil.getInstance();

		final Map<GenericEntityType, EntityTab> EntityTabMap = ClientUtil.getEntityTabMap();
		final List<EntityType> entityTypes = ClientUtil.getEntityConfigHelper().getEntityTypeDefinitions();
		EntityTab entityTabConfig = null;

		if (ClientUtil.checkViewOrEditAnyEntityPermission()) {
			boolean hasGenericEntity = false;
			boolean useInCompatibility = false;

			for (Map.Entry<GenericEntityType, EntityTab> entry : EntityTabMap.entrySet()) {
				GenericEntityType type = entry.getKey();
				entityTabConfig = entry.getValue();
				EntityType entityTypeDef = findEntityType(type, entityTypes);

				if (ClientUtil.checkViewOrEditEntityPermission(entityTypeDef)) {
					if (entityTabConfig != null && UtilBase.asBoolean(entityTabConfig.isShowTab(), true) && entityTypeDef != null) {
						boolean canEditEntities = !readOnly && ClientUtil.checkEditEntityPermission(entityTypeDef);

						ClientUtil.getLogger().info(String.format("Can user edit %s? = %b", type.getName(), canEditEntities));

						String defaultLabel = "Manage " + entityTypeDef.getDisplayName();
						addTab(
								clientUtil.getLabel("tab.entity." + entityTypeDef.getName(), defaultLabel),
								clientUtil.makeImageIcon("image.blank"),
								GenericEntityTab.createInstance(type, ConfigUtil.isCanClone(entityTypeDef), canEditEntities),
								clientUtil.getLabel("tab.tooltip.entity." + entityTypeDef.getName(), defaultLabel));
						if (!hasGenericEntity) {
							hasGenericEntity = true;
						}
					}
				}

				// Check for useInCompatibility for each one. If even one is true, that's
				// enough.
				if (!useInCompatibility && ConfigUtil.isUseInCompatibility(entityTypeDef)) {
					useInCompatibility = true;
				}
			}

			if (hasGenericEntity && useInCompatibility) {
				CompatibilityManagementPanel cmPanel = new CompatibilityManagementPanel(readOnly);
				addTab(clientUtil.getLabel("tab.entity.compatibility"), clientUtil.makeImageIcon("image.blank"), cmPanel, clientUtil.getLabel("tab.tooltip.entity.compatibility"));
			}
		}
	}
}