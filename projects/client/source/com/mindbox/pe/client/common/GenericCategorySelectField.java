package com.mindbox.pe.client.common;

import java.awt.BorderLayout;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;

import com.mindbox.pe.client.EntityModelCacheFactory;
import com.mindbox.pe.client.applet.UIFactory;
import com.mindbox.pe.client.common.tree.GenericCategorySelectionTree;
import com.mindbox.pe.client.common.tree.GenericCategoryTreeWithCheckBox;
import com.mindbox.pe.model.GenericCategory;
import com.mindbox.pe.model.GenericEntityType;

/**
 * @author Geneho Kim
 * @author MindBox
 * @since PowerEditor 3.1.0
 */
public class GenericCategorySelectField extends AbstractDropSelectField implements TreeSelectionListener {

	private final List<Integer> categoryIDList;
	private GenericCategoryTreeWithCheckBox multiCatTree = null;
	private GenericCategorySelectionTree singleCatTree = null;
	private GenericCategory selectedGenericCategory = null;
	private final int categoryType;

	public GenericCategorySelectField(int categoryType, boolean forMultiSelect) {
		super(forMultiSelect);
		categoryIDList = new LinkedList<Integer>();
		this.categoryType = categoryType;
	}

	/**
	 * 
	 * @return list of Integer objects
	 */
	public final List<Integer> getValue() {
		return categoryIDList;
	}

	public final GenericCategory getGenericCategory() {
		return selectedGenericCategory;
	}

	public final int getGenericCategoryID() {
		return (selectedGenericCategory == null ? -1 : selectedGenericCategory.getID());
	}

	public final boolean hasValue() {
		if (forMultiSelect) {
			return categoryIDList != null && !categoryIDList.isEmpty();
		}
		else {
			return selectedGenericCategory != null;
		}
	}

	private void refreshTextField() {
		if (forMultiSelect) {
			if (categoryIDList.isEmpty()) {
				textField.setText("");
			}
			else {
				textField.setText(EntityModelCacheFactory.getInstance().toGenericCategoryIDStr(categoryType, categoryIDList));
			}
		}
		else {
			textField.setText((selectedGenericCategory == null ? "" : selectedGenericCategory.getName()));
		}
	}

	public final void setValue(List<Integer> categoryIDList) {
		this.categoryIDList.clear();
		if (categoryIDList != null) {
			this.categoryIDList.addAll(categoryIDList);
		}
		refreshTextField();
	}

	public final void setValue(GenericCategory category) {
		if (forMultiSelect) {
			this.categoryIDList.clear();
			this.categoryIDList.add(category.getID());
		}
		else {
			this.selectedGenericCategory = category;
		}
		refreshTextField();
	}

	private void updateFields() {
		if (forMultiSelect) {
			categoryIDList.clear();
			categoryIDList.addAll(multiCatTree.getSelectedGenericCategoryIDs());
		}
		else {
			selectedGenericCategory = singleCatTree.getSelectedGenericCategory();
		}
		refreshTextField();
	}

	protected JComponent createSelectorComponent() {
		JPanel selectorPanel = UIFactory.createBorderLayoutPanel(4,4);
		if (forMultiSelect && multiCatTree == null) {
			multiCatTree = new GenericCategoryTreeWithCheckBox(GenericEntityType.forCategoryType(categoryType), false, true);
		}
		else if (!forMultiSelect && singleCatTree == null) {
			singleCatTree = new GenericCategorySelectionTree(this.categoryType, false, true, true);
			singleCatTree.getTreeSelectionModel().addTreeSelectionListener(this);
		}

		if (forMultiSelect) {
			selectorPanel.add(multiCatTree.getJComponent(), BorderLayout.CENTER);
		}
		else {
			selectorPanel.add(singleCatTree.getJComponent(), BorderLayout.CENTER);
		}
		return selectorPanel;
	}

	protected void selectSelectedValues() {
		if (forMultiSelect) {
			if (categoryIDList == null || categoryIDList.isEmpty()) {
                multiCatTree.setSelectedCategoriesAndEntities(null, null);                
			}
			else {
                multiCatTree.setSelectedCategoriesAndEntities(categoryIDList, null);                
			}
		}
		else {
			if (selectedGenericCategory != null) {
				singleCatTree.selectGenericCategory(selectedGenericCategory.getID());
			}
			else {
				singleCatTree.selectGenericCategory(-1);
			}
		}
	}

	protected void selectorClosed() {
		updateFields();
	}

	protected void valueDeleted() {
		if (forMultiSelect) {
			categoryIDList.clear();
		}
		else {
			selectedGenericCategory = null;
		}
	}

	public void valueChanged(TreeSelectionEvent arg0) {
		try {
			Thread.sleep(400);
		}
		catch (InterruptedException e) {
		}
		closeWindow();
	}

}