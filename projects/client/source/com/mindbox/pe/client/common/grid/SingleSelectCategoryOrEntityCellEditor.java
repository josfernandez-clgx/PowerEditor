package com.mindbox.pe.client.common.grid;

import java.awt.BorderLayout;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeWillExpandListener;
import javax.swing.tree.ExpandVetoException;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import com.mindbox.pe.client.ClientUtil;
import com.mindbox.pe.client.EntityModelCacheFactory;
import com.mindbox.pe.client.applet.UIFactory;
import com.mindbox.pe.client.common.tree.GenericCategoryNode;
import com.mindbox.pe.client.common.tree.GenericCategorySelectionTree;
import com.mindbox.pe.model.GenericCategory;
import com.mindbox.pe.model.GenericEntity;
import com.mindbox.pe.model.GenericEntityType;
import com.mindbox.pe.model.table.CategoryOrEntityValue;

/**
 * Grid cell editor for single select category or entity columns.
 * @author Geneho Kim
 *
 */
public class SingleSelectCategoryOrEntityCellEditor extends AbstractSingleSelectCategoryOrEntityCellEditor {

	private GenericCategorySelectionTree selectionTree;
    private AbstractGridTableModel tableModel;
    private TreeWillExpandOrCollapseListener expansionListener = null;    

	public SingleSelectCategoryOrEntityCellEditor(String columnTitle, GenericEntityType entityType, boolean allowEntity, boolean viewOnly,
			AbstractGridTableModel tableModel, boolean sort) {
		super(columnTitle, entityType, allowEntity, viewOnly, sort);
		this.tableModel = tableModel;
	}

	protected void initEditPanel() {
		if (editPanel == null) {
			editPanel = UIFactory.createBorderLayoutPanel(2, 2);
			if (entityType.hasCategory()) {
				this.selectionTree = new GenericCategorySelectionTree(entityType.getCategoryType(), super.allowEntity, true, super.sort);
				editPanel.add(selectionTree.getJComponent(), BorderLayout.CENTER);
                if (allowEntity) {
                    expansionListener = new TreeWillExpandOrCollapseListener();
                    selectionTree.addTreeWillExpandListener(expansionListener);
                }
			} else {
				ClientUtil.getLogger().warn(entityType + " does not support categories!!!");
				this.selectionTree = null;
				JLabel label = new JLabel(ClientUtil.getInstance().getLabel("msg.warning.invalid.entity.no.category", new Object[]{entityType.toString()}));
				editPanel.add(new JScrollPane(label), BorderLayout.NORTH);
			}
		}
	}

	protected CategoryOrEntityValue getCategoryOrEntityValueFromGUI() {
		if (selectionTree == null) return null;
		GenericEntity entity = selectionTree.getSelectedGenericEntity();
		if (entity != null) {
			return new CategoryOrEntityValue(entityType, true, entity.getID());
		}
		else {
			GenericCategory category = selectionTree.getSelectedGenericCategory();
			if (category != null) {
				return new CategoryOrEntityValue(entityType, false, category.getID());
			}
			else {
				return null;
			}
		}
	}

	protected void setInternals(CategoryOrEntityValue categoryOrEntityValue) {
        selectionTree.removeTreeWillExpandListener(expansionListener);
		selectionTree.expandAll(false);        
		if (selectionTree == null) return;
		selectionTree.clearSelection();
		if (categoryOrEntityValue != null) {
			if (categoryOrEntityValue.isForEntity()) {
				// select entity
				selectionTree.selectGenericEntity(categoryOrEntityValue.getId());
                    selectionTree.removeTreeWillExpandListener(expansionListener);
                    loadEntitiesForSavedEntity(categoryOrEntityValue);            
 			}
			else {
				// select category
				selectionTree.selectGenericCategory(categoryOrEntityValue.getId());
			}
		}
        selectionTree.removeTreeWillExpandListener(expansionListener);        
	}
	
	/**
	 * Gets a list of the parent categories of the saved entities  
	 * and loads all the entities assoicated with those categories.
	 * @param entities Saved entities to select
	 */
	private void loadEntitiesForSavedEntity(CategoryOrEntityValue categoryOrEntityValue) {
	    if (categoryOrEntityValue != null) {
            GenericEntity entity = EntityModelCacheFactory.getInstance().getGenericEntity(entityType, categoryOrEntityValue.getId());            
            List<Integer> parentCatIDs = entity.getCategoryIDList(selectionTree.getDatedTreeModel().getDate());
            if (parentCatIDs != null && parentCatIDs.size() > 0) {
                loadEntitiesForSavedEntity(parentCatIDs);                
            }
	    }
	}
	
	
    private void loadEntitiesForSavedEntity(List<Integer> parentCatIDs) {
        GenericCategoryNode rootNode = (GenericCategoryNode) selectionTree.getDatedTreeModel().getRoot();
        loadEntitiesForSavedEntity(rootNode, parentCatIDs);
    }
    
    private void loadEntitiesForSavedEntity(GenericCategoryNode node, List<Integer> parentCatIDs) {
        GenericCategory category = node.getGenericCategory();
        if (node.entitiesNeedLoading() && category != null && parentCatIDs != null && parentCatIDs.contains(new Integer(category.getID()))) {
            selectionTree.getDatedTreeModel().addGenericEntityNodesForNodeOnly(node, entityType);
            node.setEntitiesNeedLoading(false);
            selectionTree.getDatedTreeModel().nodeStructureChanged(node);
        }

        for (int i = 0; i < node.getChildCount(); i++) {
            TreeNode child = node.getChildAt(i);
            if (child instanceof GenericCategoryNode) {
                loadEntitiesForSavedEntity((GenericCategoryNode) child, parentCatIDs);
            }
        }
    }

    protected void valueChanged() {
	    tableModel.setDirty(true);
	}
	
    /**
     * If a category is expanded and the model allows entities and the entities
     * are not yet loaded, load them.
     */
    private class TreeWillExpandOrCollapseListener implements TreeWillExpandListener {
        
        public void treeWillCollapse(TreeExpansionEvent event) throws ExpandVetoException {}
        
        public void treeWillExpand(TreeExpansionEvent event) throws ExpandVetoException {
            if (allowEntity) {
                try {
                    ClientUtil.getParent().setCursor(UIFactory.getWaitCursor());                    
                    TreePath path = event.getPath();
                    if (path != null) {
                        if (path.getLastPathComponent() instanceof GenericCategoryNode &&
                                ((GenericCategoryNode)path.getLastPathComponent()).entitiesNeedLoading()) {
                            GenericCategoryNode node = (GenericCategoryNode)path.getLastPathComponent();
                            selectionTree.getDatedTreeModel().addGenericEntityNodesForNodeOnly(node, entityType);
                            node.setEntitiesNeedLoading(false);
                            selectionTree.getDatedTreeModel().nodeStructureChanged(node);
                        }
                    }
                } catch (Exception e) {
                    ClientUtil.handleRuntimeException(e);
                } finally {
                    ClientUtil.getParent().setCursor(UIFactory.getDefaultCursor());                    
                }
            }            
        }
        
    }    

}
