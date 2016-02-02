package com.mindbox.pe.client.common.tree;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;

import com.mindbox.pe.client.ClientUtil;
import com.mindbox.pe.communication.ServerException;
import com.mindbox.pe.model.GenericCategory;
import com.mindbox.pe.model.GenericEntity;
import com.mindbox.pe.model.GenericEntityType;
import com.mindbox.pe.model.comparator.GenericCategoryComparator;
import com.mindbox.pe.model.comparator.IDNameObjectComparator;

/**
 * A thread-safe implementation of TreeModel that manages categories with dated associations.
 * @author Geneho Kim
 * @since 5.1.0
 */
public class DatedCategoryTreeModel extends DefaultTreeModel {

	public interface DataProvider {

		/**
		 * Gets the specified generic category of the specified type.
		 * @param type
		 * @param categoryID
		 * @return generic category, if found; <code>null</code>, otherwise
		 */
		GenericCategory getGenericCategory(GenericEntityType type, int categoryID);

		GenericCategory getGenericCategory(int typeID, int categoryID);

		List<GenericEntity> getGenericEntitiesInCategory(GenericEntityType type, int categoryID, Date date, boolean includeDescendents) throws ServerException;

	}

	private Date date;
	private boolean showEntities;
	private boolean sort;
	private final DataProvider dataProvider;
	private final GenericCategoryComparator genericCategoryComparator = GenericCategoryComparator.getSortByNameInstance();
    private static boolean SESSION_EXPIRED = false;

	/**
	 * Equivalent to <code>new DatedCategoryTreeModel(root,initialDate,dataProvider,showEntities,false)</code>.
	 */
	protected DatedCategoryTreeModel(GenericCategoryNode root, Date initialDate, DataProvider dataProvider, boolean showEntities) {
		this(root, initialDate, dataProvider, showEntities, false);
	}

	public DatedCategoryTreeModel(GenericCategoryNode root, Date initialDate, DataProvider dataProvider, 
            boolean showEntities, boolean sort) {
		super(root);
		if (root == null) throw new NullPointerException("root cannot be null");
		if (initialDate == null) throw new NullPointerException("initialDate cannot be null");
		if (dataProvider == null) throw new NullPointerException("categoryProvider cannot be null");
		this.dataProvider = dataProvider;
		this.date = initialDate;
		this.showEntities = showEntities;
		this.sort = sort;
		rebuildModel();
	}

	public synchronized void rebuild(GenericCategory rootCategory) {
		getRootNode().replaceGenericCategory(rootCategory);
		rebuildModel();
	}
	
	public synchronized Date getDate() {
		return date;
	}

	public synchronized void recalibrate(Date date) {
		this.date = date;
		rebuildModel();
	}

	public synchronized void resetSort(boolean sort) {
		if (this.sort != sort) {
			this.sort = sort;
			rebuildModel();
		}
	}

	public synchronized void resetShowEntities(boolean showEntities, List<GenericCategoryNode> categories) {
        GenericCategoryNode rootNode = getRootNode();        
		if (this.showEntities && !showEntities) {         
			removeAllGenericEntityNodes(rootNode);
            setCategoriesNeedLoading(rootNode, false);            
		} else if (showEntities && !this.showEntities) {
            addGenericEntityNodes(GenericEntityType.forCategoryType(rootNode.getGenericCategory().getType()), categories);
		}
		this.showEntities = showEntities;
	}

    public synchronized void resetShowEntitiesOld(boolean showEntities) {
		this.showEntities = showEntities;
		rebuildModel();
	}

	/**
	 * Adds the specified category to this model.
	 * @throws NullPointerException if <code>category</code> is <code>null</code>
	 */
	public synchronized void addGenericCategory(GenericCategory category) {
		if (category == null) throw new NullPointerException("category cannot be null");
		// create tree node object
		GenericCategoryNode childNode = createGenericCategoryNode(category);

		GenericCategory parentCategory = dataProvider.getGenericCategory(category.getType(), category.getParentID(date));
		GenericCategoryNode parentNode = null;
		// if no parent node is found for the date, do not add to the model, per TT 1769
		if (parentCategory != null) {
			GenericCategoryNode rootNode = getRootNode();
			parentNode = findGenericCategoryNode(parentCategory, rootNode);
			if (parentNode == null) {
				parentNode = rootNode;
			}
			// add to the parent node
			addGenericCategoryChildNode(parentNode, childNode);
			childNode.setParent(parentNode);
		}
	}

	/**
	 * Makes sure the model is updated to reflect the changes to the specified category in this model.
	 * This is a noop is the specified category is not found in this.
	 * @throws NullPointerException if <code>category</code> is <code>null</code>
	 */
	public synchronized void editGenericCategory(GenericCategory category) {
		// find the node
		GenericCategoryNode nodeToMove = findGenericCategoryNode(category);
		if (nodeToMove != null) {
			if (nodeToMove.getGenericCategory().isRoot()) {
				nodeChanged(nodeToMove);
			}
			else {
				GenericCategoryNode newParentNode = (category.getParentID(date) == -1 ? null : findGenericCategoryNode(category
						.getParentID(date)));
				if (newParentNode == null) {
					removeNodeFromParent(nodeToMove);
				}
				else {
					if (sort || newParentNode != nodeToMove.getParent()) {
						// remove from old parent
						removeNodeFromParent(nodeToMove);

						// insert into the new parent
						addGenericCategoryChildNode(newParentNode, nodeToMove);
						nodeToMove.setParent(newParentNode);
					}
					else {
						nodeChanged(nodeToMove);
					}
					// Update entity nodes, if entities are shown
					if (showEntities) {
						// remove generic entity nodes from this parent, if found
						removeGenericEntityNodes(nodeToMove);
						addGenericEntityNodesForNodeOnly(nodeToMove, GenericEntityType.forCategoryType(category.getType()));
						nodeStructureChanged(nodeToMove);
					}
				}
			}
		}
	}

	/**
	 * Removes the specified category from this model.
	 * This is a noop is the specified category is not found in this.
	 * @throws NullPointerException if <code>category</code> is <code>null</code>
	 * @throws IllegalArgumentException if <code>category</code> is represented by a root node
	 */
	public synchronized void removeGenericCategory(GenericCategory category) {
		GenericCategoryNode nodeToRemove = findGenericCategoryNode(category);
		// if not found, ignore
		if (nodeToRemove != null) {
			if (category.isRoot()) throw new IllegalArgumentException("Cannot remove the root node");
			removeNodeFromParent(nodeToRemove);
		}
	}

	/**
	 * Removes the specified entity from this model.
	 * This is a noop is the specified entity is not found in this or if entity is turned off.
	 * @throws NullPointerException if <code>entity</code> is <code>null</code>
	 */
	public void removeGenericEntity(GenericEntity entity) {
		if (entity == null) throw new NullPointerException("entity cannot be null");
		synchronized (this) {
			if (showEntities) {
				List<DefaultMutableTreeNode> nodeList = findGenericEntityNodes(entity);
				// if not found, ignore
				for (Iterator<DefaultMutableTreeNode> iter = nodeList.iterator(); iter.hasNext();) {
					GenericEntityNode nodeToRemove = (GenericEntityNode) iter.next();
					removeNodeFromParent(nodeToRemove);
				}
			}
		}
	}

	private void addGenericCategoryChildNode(GenericCategoryNode parent, GenericCategoryNode newChild) {
//		insertNodeInto(newChild, parent, parent.getChildCount());
		parent.add(newChild);
		nodesWereInserted(parent, new int[]{parent.getIndex(newChild)});
	}

	private GenericCategoryNode findGenericCategoryNode(GenericCategory category, GenericCategoryNode node) {
		return findGenericCategoryNode(category.getID(), node);
	}

	private GenericCategoryNode findGenericCategoryNode(GenericCategory category) {
		return findGenericCategoryNode(category.getID(), getRootNode());
	}

	private GenericCategoryNode findGenericCategoryNode(int categoryID) {
		return findGenericCategoryNode(categoryID, getRootNode());
	}

	private GenericCategoryNode findGenericCategoryNode(int categoryID, GenericCategoryNode categoryNode) {
		if (categoryID == categoryNode.getGenericCategory().getID()) {
			return categoryNode;
		}
		GenericCategoryNode result = null;
		for (int i = 0; i < categoryNode.getChildCount() && result == null; i++) {
			DefaultMutableTreeNode child = (DefaultMutableTreeNode) categoryNode.getChildAt(i);
			if (child instanceof GenericCategoryNode) {
				result = findGenericCategoryNode(categoryID, (GenericCategoryNode) child);
			}
		}
		return result;
	}

	private List<DefaultMutableTreeNode> findGenericEntityNodes(GenericEntity entity) {
		List<DefaultMutableTreeNode> list = new ArrayList<DefaultMutableTreeNode>();
		findGenericEntityNodes_aux(list, entity, getRootNode());
		return list;
	}

	private void findGenericEntityNodes_aux(List<DefaultMutableTreeNode> list, GenericEntity entity, GenericCategoryNode categoryNode) {
		for (int i = 0; i < categoryNode.getChildCount(); i++) {
			DefaultMutableTreeNode child = (DefaultMutableTreeNode) categoryNode.getChildAt(i);
			if (child instanceof GenericCategoryNode) {
				findGenericEntityNodes_aux(list, entity, (GenericCategoryNode) child);
			}
			else if (child instanceof GenericEntityNode && ((GenericEntityNode) child).getGenericEntityID() == entity.getID()) {
				list.add(child);
			}
		}
	}

	private final GenericCategoryNode getRootNode() {
		return (GenericCategoryNode) getRoot();
	}

	private void rebuildModel() {
		GenericCategoryNode rootNode = getRootNode();
		if (rootNode.getGenericCategory() != null) {
			rootNode.removeAllChildren();
			processGenericCategories(rootNode.getGenericCategory(), rootNode);			
			nodeStructureChanged(getRootNode());
			
			// TT 2018
			if (showEntities) {
				addGenericEntityNodesForNodeOnly(rootNode, GenericEntityType.forCategoryType(rootNode.getGenericCategory().getType()));
			}			
		}
	}
 
	private void processGenericCategories(GenericCategory category, GenericCategoryNode node) {
		List<Integer> children = category.getChildIDs(date);
		if (children != null && !children.isEmpty()) {
			for (Iterator<Integer> iterator = children.iterator(); iterator.hasNext();) {
				int childID = iterator.next().intValue();
				GenericCategory childCat = dataProvider.getGenericCategory(GenericEntityType.forCategoryType(category.getType()), childID);

				// build node structure
				GenericCategoryNode childNode = createGenericCategoryNode(childCat);
				node.add(childNode);
				processGenericCategories(childCat, childNode);
			}
		}
	}

	private GenericCategoryNode createGenericCategoryNode(GenericCategory category) {
		return (sort ? new GenericCategoryNode(category, genericCategoryComparator, showEntities) : new GenericCategoryNode(category, showEntities));
	}

    private void addGenericEntityNodes(GenericEntityType entityType, List<GenericCategoryNode> categoriesToUpdate) {
        for (Iterator<GenericCategoryNode> i = categoriesToUpdate.iterator(); i.hasNext();) {
            GenericCategoryNode category = i.next(); 
            addGenericEntityNodesForNodeOnly(category, entityType);            
        }
    }

    public void addAllGenericEntityNodes(GenericCategoryNode parent, GenericEntityType entityType) {
        int childCountBefore = parent.getChildCount();
        addGenericEntityNodesForNodeOnly(parent, entityType);
        int childCountAfter = parent.getChildCount();
        if (childCountBefore != childCountAfter) {
            nodeStructureChanged(parent);
        }
        if (parent instanceof GenericCategoryNode) {
            if (!parent.isLeaf()) {
                for (Enumeration<?> enumeration = parent.children(); enumeration.hasMoreElements();) {
                    Object obj = enumeration.nextElement();
                    if (obj instanceof GenericCategoryNode) {
                        addAllGenericEntityNodes((GenericCategoryNode) obj, entityType);
                    }
                }
            }
        }
    }

	public void addGenericEntityNodesForNodeOnly(GenericCategoryNode parent, GenericEntityType entityType) {
		List<GenericEntity> result = null;
        try {
            result = dataProvider.getGenericEntitiesInCategory(entityType, parent.getGenericCategoryID(), date, false);
        } catch (ServerException e) {
            ClientUtil.handleRuntimeException(e);

        }
		if (result != null && !result.isEmpty()) {
			Collections.sort(result, new IDNameObjectComparator<GenericEntity>());
			for (Iterator<GenericEntity> iter = result.iterator(); iter.hasNext();) {
				GenericEntity element = iter.next();
                GenericEntityNode newChild = new GenericEntityNode(element);
                parent.add(newChild);                    
                nodesWereInserted(parent, new int[]{parent.getIndex(newChild)});
			}
		}
	}

    private void removeAllGenericEntityNodes(GenericCategoryNode categoryNode) {
        List<GenericEntityNode> itemsToRemove = new ArrayList<GenericEntityNode>();
        for (Enumeration<?> enumeration = categoryNode.children(); enumeration.hasMoreElements();) {
            Object obj = enumeration.nextElement();
            if (obj instanceof GenericEntityNode) {
                itemsToRemove.add((GenericEntityNode) obj);
            }
            else if (obj instanceof GenericCategoryNode) {
                removeAllGenericEntityNodes((GenericCategoryNode) obj);
            }
        }       
        for (Iterator<GenericEntityNode> iter = itemsToRemove.iterator(); iter.hasNext();) { 
            GenericEntityNode child = iter.next();
            GenericCategoryNode parent = (GenericCategoryNode)child.getParent();
            int index = categoryNode.getIndex(child);            
            categoryNode.remove(child);
            nodesWereRemoved(parent, new int[] { index }, new Object[] { child });
        }
    }
    
	private void removeGenericEntityNodes(GenericCategoryNode parent) {
		List<GenericEntityNode> itemsToRemove = new ArrayList<GenericEntityNode>();
		for (Enumeration<?> enumeration = parent.children(); enumeration.hasMoreElements();) {
			Object obj = enumeration.nextElement();
			if (obj instanceof GenericEntityNode) {
				itemsToRemove.add((GenericEntityNode) obj);
			}
		}
		for (Iterator<GenericEntityNode> iter = itemsToRemove.iterator(); iter.hasNext();) {
			GenericEntityNode element = iter.next();
			parent.remove(element);
		}
	}

    public boolean isShowEntities() {
        return showEntities;
    }
    
    /**
     * Updated the needsLoading flag on all the categories in the model
     * @param parent
     * @param needLoading
     */
    public void setCategoriesNeedLoading(GenericCategoryNode parent, boolean needLoading) {
        parent.setEntitiesNeedLoading(needLoading);
        for (Enumeration<?> en = parent.children(); en.hasMoreElements();) {
            Object child= en.nextElement();
            if (child instanceof GenericCategoryNode) {                
                setCategoriesNeedLoading((GenericCategoryNode)child, needLoading);
            }
        }
    }
    
    /** 
     * Returns whether the specified node is a leaf node.
     * The way the test is performed depends on the
     * <code>askAllowsChildren</code> setting.
     *
     * @param node the node to check
     * @return true if the node is a leaf node
     *
     * @see #asksAllowsChildren
     * @see TreeModel#isLeaf
     */
    public boolean isLeaf(Object node) {
        if (SESSION_EXPIRED) return true;
        if (node instanceof GenericCategoryNode) {
            GenericCategoryNode gnode = (GenericCategoryNode)node;
            GenericCategory category = gnode.getGenericCategory();
            if (category == null) return true;
            if (gnode.entitiesNeedLoading()) {
                try {
                    List<GenericEntity> result = dataProvider.getGenericEntitiesInCategory(GenericEntityType.forCategoryType(category.getType()), category.getId(), date, false);
                    return result != null && result.size() == 0 && category.hasNoChild(date);
                } catch (ServerException e){
                    SESSION_EXPIRED = true;                    
                    ClientUtil.handleRuntimeException(e);                    
                    return true;
                }
            } else {
                return gnode.isLeaf();
            }
        } else {
            return true;
        }
    }
}
