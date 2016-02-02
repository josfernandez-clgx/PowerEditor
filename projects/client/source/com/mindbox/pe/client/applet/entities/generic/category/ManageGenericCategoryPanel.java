package com.mindbox.pe.client.applet.entities.generic.category;

import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import com.mindbox.pe.client.ClientUtil;
import com.mindbox.pe.client.EntityModelCacheFactory;
import com.mindbox.pe.client.applet.UIFactory;
import com.mindbox.pe.client.applet.entities.generic.GenericEntityFilterPanel;
import com.mindbox.pe.client.applet.entities.generic.GenericEntityManagementPanel;
import com.mindbox.pe.client.common.AbstractThreadedActionAdapter;
import com.mindbox.pe.client.common.PanelBase;
import com.mindbox.pe.client.common.tree.GenericCategorySelectionTree;
import com.mindbox.pe.common.PrivilegeConstants;
import com.mindbox.pe.common.UtilBase;
import com.mindbox.pe.communication.ServerException;
import com.mindbox.pe.model.GenericCategory;
import com.mindbox.pe.model.GenericEntityType;
import com.mindbox.pe.model.GuidelineContext;
import com.mindbox.pe.model.ParameterGrid;
import com.mindbox.pe.model.ParameterTemplate;
import com.mindbox.pe.model.assckey.DefaultMutableTimedAssociationKey;
import com.mindbox.pe.model.assckey.MutableTimedAssociationKey;
import com.mindbox.pe.model.filter.GuidelineReportFilter;

/**
 * @author Geneho Kim
 * @author MindBox
 * @since PowerEditor 2.4.0
 */
public class ManageGenericCategoryPanel extends PanelBase implements TreeSelectionListener {

	private class AddL extends AbstractThreadedActionAdapter {

		public void performAction(ActionEvent event) {
			GenericCategory category = NewGenericCategoryDialog.newGenericCategory(
					JOptionPane.getFrameForComponent(ClientUtil.getApplet()),
					categoryType,
					categoryTree.getSelectedGenericCategory());
			if (category != null) {
				categoryTree.getTreeSelectionModel().removeTreeSelectionListener(ManageGenericCategoryPanel.this);
				try {
					// update cache
					addChildCategoryLinksToParents(category);
					EntityModelCacheFactory.getInstance().addGenericCategory(category);

					// expand the node
					TreePath treePath = categoryTree.getTreePath(category.getID());

					if (treePath != null) {
						categoryTree.expandPath(treePath.getParentPath());
					}
				}
				finally {
					categoryTree.getTreeSelectionModel().addTreeSelectionListener(ManageGenericCategoryPanel.this);
				}
			}
		}
	}

	private class DeleteL extends AbstractThreadedActionAdapter {

		public void performAction(ActionEvent event) {
			GenericCategory selectedGenericCategory = categoryTree.getSelectedGenericCategory();
			try {
                if (selectedGenericCategory != null && !hasProductionRestrictions(selectedGenericCategory)) {
                    boolean confirmedDelete = false;
                    if (categoryUsedInContext(selectedGenericCategory)) {
                        confirmedDelete = 
                            ClientUtil.getInstance().showConfirmation("msg.question.delete.category.contexts", new Object[] { selectedGenericCategory.getName()});
                    } else {
                        confirmedDelete = 
                            ClientUtil.getInstance().showConfirmation("msg.question.delete.category", new Object[] { selectedGenericCategory.getName()});
                        
                    }
                    if (confirmedDelete) {
                		deleteGenericCategory(selectedGenericCategory);
                	}
                }
            } catch (ServerException e) {
                ClientUtil.handleRuntimeException(e);
            }
		}
	}

	private class EditL extends AbstractThreadedActionAdapter {

		public void performAction(ActionEvent event) {
			GenericCategory selectedGenericCategory = categoryTree.getSelectedGenericCategory();
			if (selectedGenericCategory == null) {
				ClientUtil.getInstance().showWarning("msg.warning.select.category.one");
				return;
			}
			GenericEntityType genericType = GenericEntityType.forCategoryType(selectedGenericCategory.getType());
			if (selectedGenericCategory != null) {
				List<MutableTimedAssociationKey> oldParentAssocationList = selectedGenericCategory.getAllParentAssociations();
				GenericCategory category = null;
				try {
					ClientUtil.getCommunicator().lock(selectedGenericCategory.getId(), genericType);
					category = GenericCategoryEditDialog.editGenericCategory(
							JOptionPane.getFrameForComponent(ClientUtil.getApplet()),
							selectedGenericCategory);
				}
				catch (ServerException e) {
					ClientUtil.getInstance().showErrorDialog(
							"msg.error.failure.lock",
							new Object[] {
									selectedGenericCategory.getName(),
									ClientUtil.getInstance().getErrorMessage(e) });
					return;
				}
				if (category != null) {
					try {
						categoryTree.getTreeSelectionModel().removeTreeSelectionListener(
								ManageGenericCategoryPanel.this);
						// Update cached category-category associations
						removeAllChildCatgoryLinksFromParents(oldParentAssocationList, category);
						addChildCategoryLinksToParents(category);

						// update in cache
						EntityModelCacheFactory.getInstance().editGenericCategory(category);
						categoryTree.selectGenericCategory(category.getID());
					}
					finally {
						categoryTree.getTreeSelectionModel().addTreeSelectionListener(ManageGenericCategoryPanel.this);
					}
				}
				try {
					ClientUtil.getCommunicator().unlock(selectedGenericCategory.getId(), genericType);
				}
				catch (ServerException e) {
					ClientUtil.getInstance().showErrorDialog(
							"msg.error.generic.service",
							new Object[] { ClientUtil.getInstance().getErrorMessage(e) });
				}
			}
		}
	}
	

	private class ShowEntityL extends AbstractThreadedActionAdapter {
		public void performAction(ActionEvent event) throws Exception {
            try {
                setCursor(UIFactory.getWaitCursor());
                categoryTree.getJComponent().setCursor(UIFactory.getWaitCursor());
                categoryTree.updateTreeForEntities(showEntityCheckBox.isSelected());
            } finally {
                setCursor(UIFactory.getDefaultCursor());
                categoryTree.getJComponent().setCursor(UIFactory.getDefaultCursor());                
            }
		}
	}
	
	private final GenericCategorySelectionTree categoryTree;
	private final JButton addButton, editButton, deleteButton;
	private final int categoryType;
	private final JCheckBox showEntityCheckBox;
	private final GenericEntityManagementPanel genericEntityManagementPanel; // TT 2021
	
	/**
	 * @param categoryType
	 * @param hasEditEntityPrivilege
	 */
	public ManageGenericCategoryPanel(int categoryType,boolean hasEditEntityPrivilege, GenericEntityManagementPanel genericEntityManagementPanel) {
		super();
		this.categoryType = categoryType;
		
		// TT 2021
		this.genericEntityManagementPanel = genericEntityManagementPanel;

		showEntityCheckBox = UIFactory.createCheckBox("checkbox.show.entities");
		showEntityCheckBox.addActionListener(new ShowEntityL());

		addButton = UIFactory.createButton(ClientUtil.getInstance().getLabel("button.add"), "image.btn.small.add", new AddL(), null);
		editButton = UIFactory.createButton(ClientUtil.getInstance().getLabel("button.edit"), "image.btn.small.edit", new EditL(), null);
		deleteButton = UIFactory.createButton(ClientUtil.getInstance().getLabel("button.delete"), "image.btn.small.delete", new DeleteL(), null);
		/// TODO DJG @@@@
		categoryTree = new GenericCategorySelectionTree(categoryType, false, true, true);
		categoryTree.getTreeSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		
		initPanel();
		
		// make add,edit and delete button if we have edit permission on this entity to which this category belongs to
		JButton buttonArray[]={addButton,editButton,deleteButton};
		ClientUtil.updateVisibileAndEnableOfButtons(buttonArray,hasEditEntityPrivilege,hasEditEntityPrivilege);

		categoryTree.getTreeSelectionModel().addTreeSelectionListener(this);
		editButton.setEnabled(false);
		
	}

	private boolean hasProductionRestrictions(GenericCategory category) throws ServerException {
        if (ClientUtil.checkPermissionByPrivilegeName(PrivilegeConstants.PRIV_EDIT_PRODUCTION_DATA)) {
            return false;
        }
	    GuidelineReportFilter filter = new GuidelineReportFilter();
	    filter.setIncludeEmptyContexts(false);
	    filter.setIncludeChildrenCategories(true);
	    filter.setIncludeParentCategories(true);
	    filter.setSearchInColumnData(true);
	    
	    GuidelineContext context = new GuidelineContext(category.getType());
	    context.setIDs(new int[] { category.getId() });
	    filter.addContext(context);
	    filter.addStatus(ClientUtil.getHighestStatus());
	    if (ClientUtil.getCommunicator().search(filter).size() > 0) {
            ClientUtil.getInstance().showErrorDialog("msg.error.entityusedinproduction", 
                    new Object[] {ClientUtil.getHighestStatusDisplayLabel() });
	        return true;
	    }
       
        // check parameter references
        List<ParameterTemplate> parameterTemplates = EntityModelCacheFactory.getInstance().getAllParameterTemplates();
        for (Iterator<ParameterTemplate> i = parameterTemplates.iterator(); i.hasNext();) {
            ParameterTemplate template = i.next();
            List<ParameterGrid> paramGridList = ClientUtil.getCommunicator().fetchParameters(template.getID());
            for (Iterator<ParameterGrid> it = paramGridList.iterator(); it.hasNext();) {
                ParameterGrid grid = it.next();
                if (ClientUtil.isHighestStatus(grid.getStatus())) {
                    // check context
                    int[] ids = grid.getGenericCategoryIDs(GenericEntityType.forCategoryType(category.getType()));
                    if (ids != null && ids.length > 0 && UtilBase.contains(new int[] { category.getId() }, ids)) {
                        ClientUtil.getInstance().showErrorDialog("msg.error.categoryusedinproduction.parameter", 
                                new Object[] {ClientUtil.getHighestStatusDisplayLabel() });
                        return true;
                    }
                }
            }
        }
        return false;
	}

    private boolean categoryUsedInContext(GenericCategory category) throws ServerException {
        if (ClientUtil.checkPermissionByPrivilegeName(PrivilegeConstants.PRIV_EDIT_PRODUCTION_DATA)) {
            return false;
        }
        GuidelineReportFilter filter = new GuidelineReportFilter();
        filter.setIncludeEmptyContexts(false);
        filter.setIncludeChildrenCategories(false);
        filter.setIncludeParentCategories(false);
        filter.setSearchInColumnData(true);
        // TT 2021
        filter.setIncludeParameters(true);
        
        GuidelineContext context = new GuidelineContext(category.getType());
        context.setIDs(new int[] { category.getId() });
        filter.addContext(context);
        if (ClientUtil.getCommunicator().search(filter).size() > 0) {
            return true;
        }
        return false;
    }

	private void deleteGenericCategory(GenericCategory cat) {
		categoryTree.getTreeSelectionModel().removeTreeSelectionListener(this);
		try {
			ClientUtil.getCommunicator().deleteGenericCategory(cat.getType(), cat.getID());
			EntityModelCacheFactory.getInstance().removeGenericCategory(cat);
			GenericCategory parent = EntityModelCacheFactory.getInstance().getGenericCategory(cat.getType(), cat.getParentID(getSelectedDateForCategoryTree()));
			if (parent != null) {
				parent.removeAllChildAssociations(cat.getID());
			}
			// TT 2021
			EntityModelCacheFactory.getInstance().removeEntityCategoryAssociation(cat);
			GenericEntityFilterPanel filterPanel = null;
			if (genericEntityManagementPanel != null) filterPanel = genericEntityManagementPanel.getGenericEntityFilterPanel();
			// refresh the panel
			if (filterPanel != null) filterPanel.search();

		}
		catch (Exception ex) {
			ClientUtil.handleRuntimeException(ex);
		}
		finally {
			categoryTree.getTreeSelectionModel().addTreeSelectionListener(this);
		}
	}

	private void removeAllChildCatgoryLinksFromParents(List<MutableTimedAssociationKey> oldParentAssocationList, GenericCategory category) {
		for (Iterator<MutableTimedAssociationKey> iter = oldParentAssocationList.iterator(); iter.hasNext();) {
			MutableTimedAssociationKey key = iter.next();
			GenericCategory oldParent = EntityModelCacheFactory.getInstance().getGenericCategory(category.getType(), key.getAssociableID());
			if (oldParent != null) {
				oldParent.removeAllChildAssociations(category.getID());
			}
		}
	}
	private void addChildCategoryLinksToParents(GenericCategory category) {
		for (Iterator<MutableTimedAssociationKey> iter = category.getParentKeyIterator(); iter.hasNext();) {
			MutableTimedAssociationKey parentKey = iter.next();
			GenericCategory parentCategory = EntityModelCacheFactory.getInstance().getGenericCategory(category.getType(), parentKey.getAssociableID());
			if (parentCategory != null) {
				parentCategory.addChildAssociation(new DefaultMutableTimedAssociationKey(
						category.getId(),
						parentKey.getEffectiveDate(),
						parentKey.getExpirationDate()));
			}
		}
		
	}
	
	private void initPanel() {
		JPanel buttonPanel = UIFactory.createJPanel(new FlowLayout(FlowLayout.LEFT));
		buttonPanel.add(addButton);
		buttonPanel.add(editButton);
		buttonPanel.add(deleteButton);

		showEntityCheckBox.setHorizontalAlignment(SwingConstants.RIGHT);

		GridBagLayout bag = new GridBagLayout();
		setLayout(bag);
		
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;
		c.anchor = GridBagConstraints.NORTHWEST;
		c.insets = new Insets(2,2,2,2);
		c.weightx = 0.0;
		c.weighty = 0.0;
		c.gridwidth = 1;
		c.gridheight = 1;
		
		addComponent(this, bag, c, buttonPanel);
		addComponent(this, bag, c, showEntityCheckBox);
		c.weightx = 1.0;
		c.gridwidth = GridBagConstraints.REMAINDER;
		addComponent(this, bag, c, Box.createHorizontalGlue());
		c.weightx = 1.0;
		c.weighty = 1.0;
		c.gridwidth = GridBagConstraints.REMAINDER;
		c.gridheight = GridBagConstraints.REMAINDER;
		addComponent(this, bag, c, categoryTree.getJComponent());
	}

	private Date getSelectedDateForCategoryTree() {
		return categoryTree.getSelectedDate();
	}
	
	private void refreshButtons(GenericCategory category) {
		deleteButton.setEnabled(category != null && !category.isRoot() && category.hasNoChild());
		editButton.setEnabled(category != null);
	}

	public void valueChanged(TreeSelectionEvent arg0) {
		refreshButtons(categoryTree.getSelectedGenericCategory());
	}
}