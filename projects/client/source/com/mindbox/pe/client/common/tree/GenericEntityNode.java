package com.mindbox.pe.client.common.tree;

import com.mindbox.pe.model.GenericEntity;

/**
 * Tree node that represents an instance of {@link com.mindbox.pe.model.GenericEntity}.
 * @author Geneho Kim
 * @author MindBox
 * @since PowerEditor 3.1.0
 */
public final class GenericEntityNode extends AbstractSelectableMutableTreeNode {
	
	private static class SelectableGenericEntity {
		private GenericEntity entity;
		private boolean selected;

		public SelectableGenericEntity(GenericEntity entity) {
			selected = false;
			this.entity = entity;
		}
		public String toString() {
			return (entity == null ? "" : entity.getName());
		}
	}

	public GenericEntityNode(GenericEntity entity) {
		setUserObject(new SelectableGenericEntity(entity));
	}

	public void setSelected(boolean flag) {
		((SelectableGenericEntity) getUserObject()).selected = flag;
	}

	public String toString() {
		return (getGenericEntity() == null ? "" : getGenericEntity().getName());
	}

	public boolean isSelected() {
		return ((SelectableGenericEntity) getUserObject()).selected;
	}

	public GenericEntity getGenericEntity() {
		return ((SelectableGenericEntity) getUserObject()).entity;
	}
	
	public int getGenericEntityID() {
		return ((SelectableGenericEntity) getUserObject()).entity.getId();
	}

	public boolean isLeaf() {
		// GenericEntity node is always a leaf
		return true;
	}

}
