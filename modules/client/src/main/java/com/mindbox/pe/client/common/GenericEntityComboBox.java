/*
 * Created on Jun 30, 2003
 * 
 */
package com.mindbox.pe.client.common;

import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;

import com.mindbox.pe.client.EntityModelCacheFactory;
import com.mindbox.pe.client.applet.UIFactory;
import com.mindbox.pe.model.GenericEntity;
import com.mindbox.pe.model.GenericEntityType;

/**
 * Generic entity combo box.
 * @author Gene Kim
 * @author MindBox, Inc
 * @since PowerEditor 1.10.0
 */
public final class GenericEntityComboBox extends JComboBox<GenericEntity> {

	private static final long serialVersionUID = -3951228734910107454L;

	private GenericEntityType type = null;
	private final boolean hasEmpty;
	private final boolean useCopyModel;

	public GenericEntityComboBox(GenericEntityType type, boolean hasEmptyItem, String iconKey) {
		this(type, hasEmptyItem, iconKey, false);
	}

	public GenericEntityComboBox(GenericEntityType type, boolean hasEmptyItem, String iconKey, boolean useCopyModel) {
		super();
		this.hasEmpty = hasEmptyItem;
		this.useCopyModel = useCopyModel;

		UIFactory.setLookAndFeel(this);
		setRenderer(new IDNameObjectCellRenderer<GenericEntity>(iconKey));
		setFocusable(true);

		setEntityType_internal(type);
	}

	/**
	 * Gets the generic entity in the model that has the passed-in ID.
	 * @since PowerEditor 4.2.0.
	 * @param entityID entityID
	 * @return the GenericEntity that has the passed-in id.
	 */
	public GenericEntity getGenericEntityForID(int entityID) {
		if (entityID == -1) {
			return null;
		}
		ComboBoxModel<GenericEntity> model = getModel();
		for (int i = 0; i < model.getSize(); i++) {
			GenericEntity item = model.getElementAt(i);
			if (item.getID() == entityID) {
				return item;
			}
		}

		return null;
	}

	public GenericEntityType getGenericEntityType() {
		return type;
	}

	public GenericEntity getSelectedGenericEntity() {
		Object obj = super.getSelectedItem();
		if (obj instanceof GenericEntity) {
			return (GenericEntity) obj;
		}
		else {
			return null;
		}
	}

	public int getSelectedGenericEntityID() {
		Object selected = getSelectedItem();
		if (selected instanceof GenericEntity) {
			return ((GenericEntity) selected).getID();
		}
		else {
			return -1;
		}
	}

	public boolean hasSelection() {
		return super.getSelectedIndex() >= 0;
	}

	/**
	 * @see GenericEntityComboBox#removeConflictingEntities_aux(GenericEntity)
	 * @param in the generic entity
	 * @since PowerEditor 4.2.0.
	 */
	public void removeConflictingEntities(GenericEntity in) {
		DefaultComboBoxModel<GenericEntity> model = removeConflictingEntities_aux(in);
		if (in != null) {
			model.removeElement(in);
		}
		this.setModel(model);
	}

	/**
	 * Removes entities that conflict with the passed-in entity.
	 * Conflicting entities include ones that have the passed-in entity as
	 * a parent already (or grandparent, etc). If an entity has this
	 * one as a parent, we will not display it on the parentCombo pull-down list
	 * (in {@link com.mindbox.pe.client.applet.entities.generic.GenericEntityDetailPanel}), 
	 * in order to eliminate the possibility of child->parent, parent->child relationships.<br>
	 * NOTE: If such relationships already exist, this may enter into an infinite
	 * recursion state!
	 * 
	 * @param in in
	 * @return A new ComboBoxModel with conflicting entities removed.
	 * @since PowerEditor 4.2.0.
	 */
	private DefaultComboBoxModel<GenericEntity> removeConflictingEntities_aux(GenericEntity in) {
		ComboBoxModel<GenericEntity> model = getModel();
		DefaultComboBoxModel<GenericEntity> retModel = (DefaultComboBoxModel<GenericEntity>) model;
		if (in == null) {
			return retModel;
		}

		for (int idx = 0; idx < model.getSize(); idx++) {
			GenericEntity entity = model.getElementAt(idx);
			if (entity.getParentID() == in.getID()) {
				removeConflictingEntities_aux(getGenericEntityForID(entity.getID()));
				retModel.removeElement(entity);
			}
		}

		return retModel;
	}

	/**
	 * After removal of conflicting entities, restores the
	 * entire list of entities to the model.
	 * @since PowerEditor 4.2.0.
	 * @see #removeConflictingEntities(GenericEntity)
	 */
	public void restoreAllEntities() {
		setModel(
				(useCopyModel
						? EntityModelCacheFactory.getInstance().copyGenericEntityComboModel(type, hasEmpty)
						: EntityModelCacheFactory.getInstance().getGenericEntityComboModel(type, hasEmpty)));
	}

	public final void selectGenericEntity(int entityID) {
		if (entityID == -1) {
			return;
		}
		ComboBoxModel<GenericEntity> model = getModel();
		for (int i = 0; i < model.getSize(); i++) {
			GenericEntity item = model.getElementAt(i);
			if (item.getID() == entityID) {
				setSelectedIndex(i);
				return;
			}
		}
	}

	public final void selectGenericEntity(String name) {
		if (name == null) return;
		ComboBoxModel<GenericEntity> model = getModel();
		for (int i = 0; i < model.getSize(); i++) {
			Object item = model.getElementAt(i);
			if (item instanceof GenericEntity) {
				if (((GenericEntity) item).getName().equals(name)) {
					setSelectedIndex(i);
					return;
				}
			}
		}
	}

	private void setEntityType_internal(GenericEntityType type) {
		this.type = type;
		if (type != null) {
			setModel(
					(useCopyModel
							? EntityModelCacheFactory.getInstance().copyGenericEntityComboModel(type, hasEmpty)
							: EntityModelCacheFactory.getInstance().getGenericEntityComboModel(type, hasEmpty)));
		}
		else {
			removeAllItems();
		}
	}

	public void setGenericEntityType(GenericEntityType type) {
		setEntityType_internal(type);
	}
}