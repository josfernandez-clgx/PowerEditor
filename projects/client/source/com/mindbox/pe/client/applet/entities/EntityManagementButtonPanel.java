package com.mindbox.pe.client.applet.entities;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.swing.JButton;

import mseries.Calendar.MFieldListener;
import mseries.ui.MChangeEvent;
import mseries.ui.MChangeListener;

import com.mindbox.pe.client.ClientUtil;
import com.mindbox.pe.client.EntityModelCacheFactory;
import com.mindbox.pe.client.applet.UIFactory;
import com.mindbox.pe.client.common.AbstractThreadedActionAdapter;
import com.mindbox.pe.client.common.ButtonPanel;
import com.mindbox.pe.client.common.detail.AbstractDetailPanel;
import com.mindbox.pe.client.common.detail.DetailChangeListener;
import com.mindbox.pe.client.common.dialog.MDateDateField;
import com.mindbox.pe.client.common.selection.AbstractSelectionPanel;
import com.mindbox.pe.common.PrivilegeConstants;
import com.mindbox.pe.common.UtilBase;
import com.mindbox.pe.common.config.CategoryTypeDefinition;
import com.mindbox.pe.communication.ServerException;
import com.mindbox.pe.model.CloneableEntity;
import com.mindbox.pe.model.EntityType;
import com.mindbox.pe.model.GenericEntityType;
import com.mindbox.pe.model.GuidelineContext;
import com.mindbox.pe.model.IDNameObject;
import com.mindbox.pe.model.ParameterGrid;
import com.mindbox.pe.model.ParameterTemplate;
import com.mindbox.pe.model.exceptions.CanceledException;
import com.mindbox.pe.model.filter.GuidelineReportFilter;

/**
 * 
 * @author Gene Kim
 * @author MindBox, Inc
 * @since PowerEditor 1.10.0
 * 
 * As of PowerEditor 4.2.0, the "New" button appears on all EntityManagementButtonPanels.
 * Previously the button did not appear if the clone button was present.
 * The "New" button is always the first button.
 * The code below assumes all of the above. If anything regarding the "New" button
 * and/or the arrangement changes, some assumptions will have to be modified
 * (as well as the code).
 */
public final class EntityManagementButtonPanel<T extends IDNameObject> extends ButtonPanel {

	private class NewL implements ActionListener {

		public void actionPerformed(ActionEvent e) {
			_newEntity();
		}
	}

	private class EditL implements ActionListener {

		public void actionPerformed(ActionEvent e) {
			_editEntity();
		}
	}

	private class ViewL implements ActionListener {

		public void actionPerformed(ActionEvent e) {
			_viewEntity();
		}
	}

	private class RemoveL implements ActionListener {

		public void actionPerformed(ActionEvent e) {
			_removeEntity();
		}
	}

	private class CloneL implements ActionListener {

		public void actionPerformed(ActionEvent e) {
			_cloneEntity();
		}
	}

	private class CopyL implements ActionListener {

		public void actionPerformed(ActionEvent e) {
			_copyEntity();
		}
	}

	private class DetailChangeL implements DetailChangeListener {

		public void detailChanged() {
			setHasUnsavedChanges(true);
		}

		public void detailSaved() {
			setHasUnsavedChanges(false);
		}
	}

	private final boolean readOnly;
	private final AbstractSelectionPanel<T, EntityManagementButtonPanel<T>> selectionPanel;
	private AbstractDetailPanel<T, EntityManagementButtonPanel<T>> detailPanel = null;
	private final EntityType entityType;
	private final String entityName;
	private boolean hasUnsavedChanges = false;
	private JButton editButton;
	private JButton copyButton;
	private JButton cloneButton;
	private JButton newButton;
	private JButton removeButton;
	private JButton viewButton;
	private MDateDateField categoryOnDateField;
	private JButton categoryRefreshButton;

	/**
	 * @since 3.0.0
	 */
	private final GenericEntityType genericEntityType;

	public EntityManagementButtonPanel(boolean readOnly, AbstractSelectionPanel<T, EntityManagementButtonPanel<T>> selectionPanel,
			EntityType entityType, String entityName, boolean hasCloneButton, boolean hasRemoveButton) {
		this(readOnly, selectionPanel, null, entityType, entityName, hasCloneButton, hasRemoveButton);
	}

	/**
	 * 
	 * @param selectionPanel
	 * @param entityType
	 * @param entityName
	 * @param hasCloneButton
	 * @param hasRemoveButton
	 * @since 3.0.0
	 */
	public EntityManagementButtonPanel(boolean readOnly, AbstractSelectionPanel<T, EntityManagementButtonPanel<T>> selectionPanel,
			GenericEntityType entityType, String entityName, boolean hasCloneButton, boolean hasRemoveButton) {
		this(readOnly, selectionPanel, entityType, null, entityName, hasCloneButton, hasRemoveButton);
	}

	/**
	 * 
	 * @param selectionPanel
	 * @param genericEntityType
	 * @param entityType
	 * @param entityName
	 * @param hasCloneButton
	 * @param hasRemoveButton
	 * @since 3.0.0
	 */
	private EntityManagementButtonPanel(boolean readOnly, final AbstractSelectionPanel<T, EntityManagementButtonPanel<T>> selectionPanel,
			GenericEntityType genericEntityType, EntityType entityType, String entityName, boolean hasCloneButton, boolean hasRemoveButton) {
		super();
		this.readOnly = readOnly;
		this.selectionPanel = selectionPanel;
		this.genericEntityType = genericEntityType;
		this.entityType = entityType;
		this.entityName = entityName;

		super.setButtons(createButtons(hasCloneButton, hasRemoveButton), FlowLayout.LEFT);
		if (genericEntityType != null) {
			CategoryTypeDefinition catDef = ClientUtil.getEntityConfiguration().getCategoryDefinition(genericEntityType);
			if (catDef != null && catDef.useInSelectionTable()) {

				categoryOnDateField = new MDateDateField(true, false, true);
				categoryOnDateField.setValue(new Date());
				selectionPanel.setCategoryOnDate(categoryOnDateField.getDate());

				categoryRefreshButton = UIFactory.createJButton("label.refresh", null, new AbstractThreadedActionAdapter() {
					public void performAction(ActionEvent event) throws Exception {
						if (categoryOnDateField.getDate() != null) {
							selectionPanel.setCategoryOnDate(categoryOnDateField.getDate());
							selectionPanel.refresh();
						}
					}
				}, null);
				categoryRefreshButton.addFocusListener(new FocusListener() {
					public void focusGained(FocusEvent e) {
						categoryRefreshButton.setEnabled(categoryOnDateField.getDate() != null);
					}

					public void focusLost(FocusEvent e) {

					}
				});
				categoryOnDateField.addMChangeListener(new MChangeListener() {
					public void valueChanged(MChangeEvent arg0) {
						categoryRefreshButton.setEnabled(categoryOnDateField.getDate() != null);
					}
				});
				categoryOnDateField.addMFieldListener(new MFieldListener() {
					public void fieldEntered(FocusEvent arg0) {
						categoryRefreshButton.setEnabled(categoryOnDateField.getDate() != null);
					}

					public void fieldExited(FocusEvent arg0) {
						categoryRefreshButton.setEnabled(categoryOnDateField.getDate() != null);
					}
				});
				add(UIFactory.createFormLabel("label.category.on.date"));
				add(categoryOnDateField);
				add(categoryRefreshButton);
			}
		}
	}

	/** 
	 * If EditEntity Nameprivilege does not exist, then CRUD buttons
	 * are disabled.
	 * @param priv
	 */
	public void setEditPrivilege(String priv) {
		if (priv != null) {
			if (!ClientUtil.checkPermissionByPrivilegeName(priv)) {
				hideAndDisableButton(editButton);
				hideAndDisableButton(newButton);
				hideAndDisableButton(copyButton);
				hideAndDisableButton(cloneButton);
				hideAndDisableButton(removeButton);
			}
		}
	}

	private void hideAndDisableButton(JButton button) {
		if (button != null) {
			button.setEnabled(false);
			button.setVisible(false);
		}
	}

	private synchronized void setHasUnsavedChanges(boolean hasChanges) {
		this.hasUnsavedChanges = hasChanges;
	}

	// Allow inspection of dirty flag
	public boolean hasUnsavedChanges() {
		return this.hasUnsavedChanges;
	}

	public void setDetailPanel(AbstractDetailPanel<T, EntityManagementButtonPanel<T>> detailPanel) {
		this.detailPanel = detailPanel;
		this.detailPanel.addDetailChangeListener(new DetailChangeL());
	}

	private final JButton[] createButtons(boolean hasCloneButton, boolean hasRemoveButton) {
		JButton[] buttons = null;

		viewButton = UIFactory.createButton(ClientUtil.getInstance().getLabel("button.view"), "image.btn.small.view", new ViewL(), null);
		editButton = UIFactory.createButton(ClientUtil.getInstance().getLabel("button.edit"), "image.btn.small.edit", new EditL(), null);
		newButton = UIFactory.createButton(ClientUtil.getInstance().getLabel("button.new"), "image.btn.small.new", new NewL(), null);
		copyButton = UIFactory.createButton(ClientUtil.getInstance().getLabel("button.copy"), "image.btn.small.add", new CopyL(), null);

		if (readOnly) {
			buttons = new JButton[] { viewButton };
		}
		else if (hasCloneButton && hasRemoveButton) {
			cloneButton = UIFactory.createButton(
					ClientUtil.getInstance().getLabel("button.clone"),
					"image.btn.small.copy",
					new CloneL(),
					null);
			removeButton = UIFactory.createButton(
					ClientUtil.getInstance().getLabel("button.remove"),
					"image.btn.small.delete",
					new RemoveL(),
					null);
			buttons = new JButton[] { newButton, copyButton, editButton, viewButton, cloneButton, removeButton };
		}
		else if (hasCloneButton) {
			cloneButton = UIFactory.createButton(
					ClientUtil.getInstance().getLabel("button.clone"),
					"image.btn.small.copy",
					new CloneL(),
					null);
			buttons = new JButton[] { copyButton, editButton, viewButton, cloneButton };
		}
		else if (hasRemoveButton) {
			removeButton = UIFactory.createButton(
					ClientUtil.getInstance().getLabel("button.remove"),
					"image.btn.small.delete",
					new RemoveL(),
					null);
			buttons = new JButton[] { newButton, copyButton, editButton, viewButton, removeButton };
		}
		else {
			buttons = new JButton[] { newButton, copyButton, editButton, viewButton };
		}
		return buttons;
	}

	private synchronized void _newEntity() {
		if (verifyUnlockPrevious()) {

			selectionPanel.clearSelection();
			detailPanel.resetFields();
			detailPanel.setForViewOnly(false);
			detailPanel.fireDetailChanged();
		}
	}

	private synchronized void _viewEntity() {
		if (verifyUnlockPrevious()) {
			List<T> values = selectionPanel.getSelectedObjects();
			if (values != null && values.size() > 0) {
				detailPanel.populateFields(values.get(0));
				detailPanel.setForViewOnly(true);
			}
		}
	}

	private synchronized void _editEntity() {
		if (verifyUnlockPrevious()) {
			List<T> values = selectionPanel.getSelectedObjects();
			if (values != null && values.size() > 0) {
				try {
					if (entityType != null) {
						ClientUtil.getCommunicator().lock(values.get(0).getID(), entityType);
					}
					else {
						ClientUtil.getCommunicator().lock(values.get(0).getID(), genericEntityType);
					}

					if (values.get(0) instanceof CloneableEntity) {
						((CloneableEntity) values.get(0)).setForClone(false);
					}
					detailPanel.populateFields(values.get(0));
					detailPanel.setForViewOnly(false);
				}
				catch (ServerException ex) {
					ClientUtil.getInstance().showErrorDialog(
							"msg.error.failure.lock",
							new Object[] { entityName, ClientUtil.getInstance().getErrorMessage(ex) });
				}
			}
			else {
				ClientUtil.getInstance().showWarning("msg.warning.select.generic");
			}
		}
	}

	private synchronized void _removeEntity() {
		if (verifyUnlockPrevious()) {
			List<T> values = selectionPanel.getSelectedObjects();
			if (values != null && values.size() > 0) {
				if (ClientUtil.getInstance().showConfirmation("msg.question.remove.entity", new Object[] { entityName })) {
					try {
						if (hasProductionRestrictions(values.get(0).getID())) {
							return;
						}

						if (entityType != null) {
							ClientUtil.getCommunicator().delete(values.get(0).getID(), entityType);
						}
						else {
							ClientUtil.getCommunicator().delete(values.get(0).getID(), genericEntityType);
						}
						selectionPanel.remove(values.get(0));

						detailPanel.resetFields();
					}
					catch (ServerException ex) {
						ClientUtil.getInstance().showErrorDialog(
								"msg.error.failure.remove",
								new Object[] { entityName, ClientUtil.getInstance().getErrorMessage(ex) });
					}
				}
			}
			else {
				ClientUtil.getInstance().showWarning("msg.warning.select.generic");
			}
		}
	}

	private boolean hasProductionRestrictions(int entityID) throws ServerException {
		if (ClientUtil.checkPermissionByPrivilegeName(PrivilegeConstants.PRIV_EDIT_PRODUCTION_DATA)) {
			return false;
		}
		// check guideline references
		GuidelineReportFilter filter = new GuidelineReportFilter();
		filter.setIncludeEmptyContexts(false);
		filter.setIncludeChildrenCategories(true);
		filter.setIncludeParentCategories(true);
		filter.setSearchInColumnData(true);

		GuidelineContext context = new GuidelineContext(genericEntityType);
		context.setIDs(new int[] { entityID });
		filter.addContext(context);
		filter.addStatus(ClientUtil.getHighestStatus());
		List<?> productionGuidelineReferences = ClientUtil.getCommunicator().search(filter);
		if (productionGuidelineReferences.size() > 0) {
			ClientUtil.getInstance().showErrorDialog(
					"msg.error.entityusedinproduction",
					new Object[] { ClientUtil.getHighestStatusDisplayLabel() });
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
					int[] ids = grid.getGenericEntityIDs(genericEntityType);
					if (ids != null && ids.length > 0 && UtilBase.contains(new int[] { entityID }, ids)) {
						ClientUtil.getInstance().showErrorDialog(
								"msg.error.entityusedinproduction.parameter",
								new Object[] { ClientUtil.getHighestStatusDisplayLabel() });
						return true;
					}
				}
			}
		}
		return false;
	}

	private synchronized void _copyEntity() {
		if (verifyUnlockPrevious()) {
			List<T> values = selectionPanel.getSelectedObjects();
			if (values != null && values.size() > 0) {
				detailPanel.setForViewOnly(false);
				if (values.get(0) instanceof CloneableEntity) {
					((CloneableEntity) values.get(0)).setForClone(true);
					((CloneableEntity) values.get(0)).setCopyPolicies(false);
				}
				detailPanel.populateForClone(values.get(0));
				hasUnsavedChanges = true;
				detailPanel.fireDetailChanged();
			}
		}
	}

	private synchronized void _cloneEntity() {
		if (verifyUnlockPrevious()) {
			List<T> values = selectionPanel.getSelectedObjects();
			if (values != null && values.size() > 0) {
				detailPanel.setForViewOnly(false);
				if (values.get(0) instanceof CloneableEntity) {
					((CloneableEntity) values.get(0)).setForClone(true);
					((CloneableEntity) values.get(0)).setCopyPolicies(true);
				}
				detailPanel.populateForClone(values.get(0));
				hasUnsavedChanges = true;
				detailPanel.fireDetailChanged();
			}
		}
	}

	private synchronized boolean verifyUnlockPrevious() {
		int currentID = detailPanel.getCurrentEntityID();
		try {
			if (currentID == -1 || !hasUnsavedChanges) {
				return true;
			}
			else {
				Boolean result = ClientUtil.getInstance().showSaveDiscardCancelDialog();
				if (result == null) {
					return false;
				}
				else {
					if (result.booleanValue()) {
						try {
							detailPanel.saveChanges();
						}
						catch (CanceledException ex) {
							return false;
						}
						catch (ServerException ex) {
							ClientUtil.getInstance().showErrorDialog(
									"msg.error.failure.save",
									new Object[] { ClientUtil.getInstance().getErrorMessage(ex) });
							return false;
						}
					}
					else {
						detailPanel.discardChanges();
					}
					hasUnsavedChanges = false;
					return true;
				}
			}
		}
		finally {
			unlockPreviousIfPossible(currentID);
		}
	}

	private void unlockPreviousIfPossible(int currentID) {
		if (currentID > 0) {
			try {
				if (entityType != null) {
					ClientUtil.getCommunicator().unlock(currentID, entityType);
				}
				else {
					ClientUtil.getCommunicator().unlock(currentID, genericEntityType);
				}
			}
			catch (ServerException ex) {
				ClientUtil.getInstance().showErrorDialog(
						"msg.error.generic.service",
						new Object[] { ClientUtil.getInstance().getErrorMessage(ex) });
			}
		}
	}

	public void handleDoubleClick() {
		if (editButton.isVisible()) {
			_editEntity();
		}
	}

	public void setEnabledNewButton(boolean enabled) {
		buttons[0].setEnabled(enabled);
	}

	public void discardChanges() {
		setHasUnsavedChanges(false);
		setEnabledSelectionAwares(false);
		detailPanel.discardChanges(); // allow disable save button
	}

	public void setEnabledSelectionAwares(boolean enabled) {
		// Start this loop with 1 rather than 0, because at this point all screens have "New" as the
		// first button. "New" can be enabled while others are disabled, so it can be skipped in this loop.
		for (int i = 1; i < buttons.length; i++) {
			buttons[i].setEnabled(enabled);
		}
		if (selectionPanel.getSelectedObjects() != null && selectionPanel.getSelectedObjects().size() > 1) {
			editButton.setEnabled(false);
			copyButton.setEnabled(false);
			viewButton.setEnabled(false);
			if (cloneButton != null) cloneButton.setEnabled(false);
			if (removeButton != null) removeButton.setEnabled(false);
		}

		if (!enabled) {
			if (!detailPanel.hasUpdateMade()) {
				detailPanel.resetFields();
				if (detailPanel.getCurrentEntityID() != -1 && hasUnsavedChanges) {
					// unlock the entity
					if (entityType != null) {
						try {
							ClientUtil.getCommunicator().unlock(detailPanel.getCurrentEntityID(), entityType);
						}
						catch (ServerException ex) {
							ClientUtil.getLogger().error("Failed to unlock " + entityType + "  - " + detailPanel.getCurrentEntityID(), ex);
						}
					}
					else {
						try {
							ClientUtil.getCommunicator().unlock(detailPanel.getCurrentEntityID(), genericEntityType);
						}
						catch (ServerException ex) {
							ClientUtil.getLogger().error(
									"Failed to unlock " + genericEntityType + "  - " + detailPanel.getCurrentEntityID(),
									ex);
						}
					}
					hasUnsavedChanges = false;
				}
			}
		}

	}

	public MDateDateField getCategoryOnDateField() {
		return categoryOnDateField;
	}

	// returns false if the changes failed to save
	public synchronized boolean saveChanges() {
		try {
			detailPanel.saveChanges();
		}
		catch (CanceledException ex) {
			return false;
		}
		catch (ServerException ex) {
			ClientUtil.getInstance().showErrorDialog(
					"msg.error.failure.save",
					new Object[] { ClientUtil.getInstance().getErrorMessage(ex) });
			return false;
		}
		hasUnsavedChanges = false;
		detailPanel.resetFields(); // disable save button
		return true;

	}
}