package com.mindbox.pe.client.common.tree;

import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeWillExpandListener;
import javax.swing.tree.ExpandVetoException;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import com.mindbox.pe.client.ClientUtil;
import com.mindbox.pe.client.applet.UIFactory;

/**
 * Generic category selection tree.
 * This only allows a single selection.
 * @author Gene Kim
 * @author MindBox, Inc
 * @since PowerEditor 3.1.0
 */
public class GenericCategorySelectionTree extends AbstractGenericCategorySelectionTree {

    private TreeWillExpandOrCollapseListener expansionListener = null;    

	public GenericCategorySelectionTree(int categoryType, boolean allowEntity, boolean showRoot, boolean sort) {
		super(categoryType, allowEntity, TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION, showRoot, true, true, sort);
		expansionListener = new TreeWillExpandOrCollapseListener();
		tree.addTreeWillExpandListener(expansionListener);
	}

    /**
     * If a category is expanded and the model allows entities and the entities
     * are not yet loaded, load them.
     */
    private class TreeWillExpandOrCollapseListener implements TreeWillExpandListener {
        
        public void treeWillCollapse(TreeExpansionEvent event) throws ExpandVetoException {
        }
        
        public void treeWillExpand(TreeExpansionEvent event) throws ExpandVetoException {
            if (getDatedTreeModel().isShowEntities()) {            
                try {
                    ClientUtil.getParent().setCursor(UIFactory.getWaitCursor());                    
                    TreePath path = event.getPath();
                    if (path != null) {
                        if (path.getLastPathComponent() instanceof GenericCategoryNode &&
                                ((GenericCategoryNode)path.getLastPathComponent()).entitiesNeedLoading()) {
                            GenericCategoryNode node = (GenericCategoryNode)path.getLastPathComponent();
                            getDatedTreeModel().addGenericEntityNodesForNodeOnly(node, entityType);
                            node.setEntitiesNeedLoading(false);
                            //getDatedTreeModel().nodeStructureChanged(node);
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