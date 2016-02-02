package com.mindbox.pe.client.applet.entities.generic;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.mindbox.pe.client.ClientUtil;
import com.mindbox.pe.client.EntityModelCacheFactory;
import com.mindbox.pe.client.applet.entities.EntityManagementButtonPanel;
import com.mindbox.pe.client.common.selection.IDNameObjectSelectionPanel;
import com.mindbox.pe.common.PrivilegeConstants;
import com.mindbox.pe.model.GenericEntity;
import com.mindbox.pe.model.GenericEntityType;

/**
 * @author Geneho Kim
 * @author MindBox
 * @since PowerEditor 3.0.0
 */
public class GenericEntitySelectionPanel extends IDNameObjectSelectionPanel<GenericEntity, EntityManagementButtonPanel<GenericEntity>> {

	private static final Map<GenericEntityType, GenericEntitySelectionPanel> instanceMap = new HashMap<GenericEntityType, GenericEntitySelectionPanel>();
	private static GenericEntityType currentType = null;
	private static boolean currentCanClone = false;

	public static GenericEntitySelectionPanel createInstance(GenericEntityType type, boolean canClone,
			GenericEntityDetailPanel detailPanel, boolean readOnly) {
		synchronized (instanceMap) {
			if (instanceMap.containsKey(type)) {
				return instanceMap.get(type);
			}
			else {
				currentType = type;
				currentCanClone = canClone;
				GenericEntitySelectionPanel instance = new GenericEntitySelectionPanel(type, detailPanel, readOnly);
				instanceMap.put(type, instance);
				return instance;
			}
		}
	}

	/**
	 * @param title
	 * @param selectionTable
	 */
	private GenericEntitySelectionPanel(GenericEntityType type, GenericEntityDetailPanel detailPanel, boolean readOnly) {
		super(type.getDisplayName(), new GenericEntitySelectionTable(GenericEntitySelectionTableModel.createInstance(type), true), readOnly);

		this.buttonPanel.setDetailPanel(detailPanel);
	}

	public void discardChanges() {
		buttonPanel.discardChanges();
	}

	public void setEnabledSelectionAwares(boolean enabled) {
		buttonPanel.setEnabledSelectionAwares(enabled);
	}

	protected void createButtonPanel() {
		this.buttonPanel = new EntityManagementButtonPanel<GenericEntity>(
				isReadOnly(),
				this,
				currentType,
				ClientUtil.getInstance().getLabel(currentType),
				currentCanClone,
				true);
		String editPrivilegeOnCurrentEntity = PrivilegeConstants.EDIT_PRIV_NAME_PREFIX + currentType.getName();
		buttonPanel.setEditPrivilege(editPrivilegeOnCurrentEntity);
	}

	public void add(GenericEntity object) {
		super.add(object);
		EntityModelCacheFactory.getInstance().add((GenericEntity) object);
	}

	public void remove(GenericEntity object) {
		if (object instanceof GenericEntity) {
			super.remove(object);
			EntityModelCacheFactory.getInstance().remove((GenericEntity) object);
		}
	}

	// TT 2072
	public void update(GenericEntity object) {
		if (object instanceof GenericEntity) {
			super.update(object);
			EntityModelCacheFactory.getInstance().update((GenericEntity) object);
		}
	}

	public void refresh() {
		selectionTable.refresh();
	}

	public void setCategoryOnDate(Date date) {
		((GenericEntitySelectionTableModel) selectionTable.getSelectionTableModel()).setCategoryOnDate(date);
	}
}