package com.mindbox.pe.common.config;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

import com.mindbox.pe.model.GenericEntityType;

/**
 * Entity Tab configuration. This represents a configuration of the Entity tab, as specified in the
 * <code>PowerEditorConfiguration.xml</code> file.
 * 
 * @author Gene Kim
 * @author MindBox, Inc
 * @since PowerEditor 2.0.0
 * @see UIConfiguration
 */
public class EntityTabConfig implements Serializable {

	private static final long serialVersionUID = 2003100240000L;

	private GenericEntityType entityType = null;

	private boolean visible;

	private String type;

	private final List<EntityPropertyTabDefinition> propertyTagDefList = new LinkedList<EntityPropertyTabDefinition>();

	public EntityTabConfig() {
	}

	public String toString() {
		return "EntityTabConfig[" + type + ",visible?=" + visible + ",propSize=" + propertyTagDefList.size() + ']';
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public void setShowTab(String value) {
		this.visible = ConfigUtil.asBoolean(value);
	}

	public void addEntityPropertyTabDefinition(EntityPropertyTabDefinition tagDefinition) {
		propertyTagDefList.add(tagDefinition);
	}

	public EntityPropertyTabDefinition[] getEntityPropertyTagDefinitions() {
		return propertyTagDefList.toArray(new EntityPropertyTabDefinition[0]);
	}

	/**
	 * @return the entity type for this configuration
	 */
	public final GenericEntityType getEntityType() {
		if (entityType == null) {
			entityType = GenericEntityType.forName(type);
		}
		return entityType;
	}

	/**
	 * @return <code>true</code>, if the tab for this configuration is to be shown;
	 *         <code>false</code>, otherwise
	 */
	public final boolean isVisible() {
		return this.visible;
	}
}
