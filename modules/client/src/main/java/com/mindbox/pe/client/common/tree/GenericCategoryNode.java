package com.mindbox.pe.client.common.tree;

import javax.swing.tree.TreeNode;

import com.mindbox.pe.model.GenericCategory;
import com.mindbox.pe.model.comparator.GenericCategoryComparator;

/**
 * Tree node that represents {@link com.mindbox.pe.model.GenericCategory}.
 * @author Geneho Kim
 * @author MindBox
 * @since PowerEditor 3.1.0
 */
public final class GenericCategoryNode extends AbstractSelectableMutableTreeNode {
	/**
	 * 
	 */
	private static final long serialVersionUID = -3951228734910107454L;

	private static class SelectableGenericCategory {
		private GenericCategory category;
		private boolean selected;

		public SelectableGenericCategory(GenericCategory category) {
			selected = false;
			this.category = category;
		}

		public String toString() {
			return (category == null ? "" : category.getName());
		}
	}

	private final boolean sort;
	private final GenericCategoryComparator comparator;
	private boolean entitiesNeedLoading;

	public GenericCategoryNode(GenericCategory category) {
		this(category, false, null, false);
	}


	public GenericCategoryNode(GenericCategory category, boolean entitiesNeedLoading) {
		this(category, false, null, entitiesNeedLoading);
	}

	public GenericCategoryNode(GenericCategory category, GenericCategoryComparator comparator) {
		this(category, true, comparator, false);
	}

	public GenericCategoryNode(GenericCategory category, GenericCategoryComparator comparator, boolean entitiesNeedLoading) {
		this(category, true, comparator, entitiesNeedLoading);
	}

	private GenericCategoryNode(GenericCategory category, boolean sort, GenericCategoryComparator comparator, boolean entitiesNeedLoading) {
		if (sort && comparator == null) throw new NullPointerException("comparator cannot be null");
		setUserObject(new SelectableGenericCategory(category));
		this.sort = sort;
		this.comparator = comparator;
		this.entitiesNeedLoading = entitiesNeedLoading;
	}

	public void add(GenericCategoryNode newChild) {
		if (newChild == null) throw new NullPointerException("newChild cannot be null");
		if (sort) {
			super.insert(newChild, getIndexForSortedAdd(newChild));
		}
		else {
			super.add(newChild);
		}
	}

	private int getIndexForSortedAdd(GenericCategoryNode newChild) {
		for (int i = 0; i < super.getChildCount(); i++) {
			TreeNode childNode = super.getChildAt(i);
			if (!(childNode instanceof GenericCategoryNode) || comparator.compare(newChild.getGenericCategory(), ((GenericCategoryNode) childNode).getGenericCategory()) < 0) {
				return i;
			}
		}
		return super.getChildCount();
	}

	public void setSelected(boolean flag) {
		((SelectableGenericCategory) getUserObject()).selected = flag;
	}

	public String toString() {
		return (getGenericCategory() == null ? "" : getGenericCategory().getName());
	}

	public boolean isSelected() {
		return ((SelectableGenericCategory) getUserObject()).selected;
	}

	public GenericCategory getGenericCategory() {
		return ((SelectableGenericCategory) getUserObject()).category;
	}

	public int getGenericCategoryID() {
		return ((SelectableGenericCategory) getUserObject()).category.getId();
	}

	//	public boolean isLeaf() {
	//		GenericCategory category = getGenericCategory();
	//		if (category == null) return true;
	//        if (entitiesNeedLoading) {
	//            return false;
	//        } else {
	//            return super.isLeaf();            
	//        }
	//	}
	//
	public final void replaceGenericCategory(GenericCategory category) {
		((SelectableGenericCategory) getUserObject()).category = category;
	}

	public boolean entitiesNeedLoading() {
		return entitiesNeedLoading;
	}

	public void setEntitiesNeedLoading(boolean entitiesLoaded) {
		this.entitiesNeedLoading = entitiesLoaded;
	}
}
