package com.mindbox.pe.client.common.detail;

import static com.mindbox.pe.common.LogUtil.logDebug;

import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JSeparator;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import mseries.ui.MChangeEvent;
import mseries.ui.MChangeListener;

import com.mindbox.pe.client.ClientUtil;
import com.mindbox.pe.client.InputValidationException;
import com.mindbox.pe.client.applet.UIFactory;
import com.mindbox.pe.client.common.AbstractThreadedActionAdapter;
import com.mindbox.pe.client.common.ButtonPanel;
import com.mindbox.pe.client.common.PanelBase;
import com.mindbox.pe.client.common.dialog.ValidationErrorReportDialog;
import com.mindbox.pe.client.common.selection.AbstractSelectionPanel;
import com.mindbox.pe.client.common.tab.PowerEditorTabPanel;
import com.mindbox.pe.communication.ServerException;
import com.mindbox.pe.communication.ValidationException;
import com.mindbox.pe.model.CloneableEntity;
import com.mindbox.pe.model.GenericEntity;
import com.mindbox.pe.model.GenericEntityType;
import com.mindbox.pe.model.IDNameObject;
import com.mindbox.pe.model.PeDataType;
import com.mindbox.pe.model.exceptions.CanceledException;

/**
 * 
 * @author Gene Kim
 * @author MindBox, Inc
 * @since PowerEditor 1.10.0
 */
public abstract class AbstractDetailPanel<T extends IDNameObject, B extends ButtonPanel> extends PanelBase implements DocumentListener, PowerEditorTabPanel, MChangeListener {
	private class DetailChangeL implements DetailChangeListener {

		public void detailChanged() {
			setHasChangesStatus(true);
		}

		public void detailSaved() {
			setHasChangesStatus(false);
		}
	}

	private final class SaveL extends AbstractThreadedActionAdapter {

		public void performAction(ActionEvent event) {
			saveEntity_internal();
		}
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = -3951228734910107454L;

	private final JButton saveButton;
	protected final PeDataType entityType;
	private AbstractSelectionPanel<T, B> selectionPanel = null;
	private boolean wasSaveForUpdate = false;
	private List<DetailChangeListener> changeListenerList;
	private boolean hasChanges = false;

	/**
	 * @since 3.0.0
	 */
	protected final GenericEntityType genericEntityType;

	protected T currentObject = null;

	/**
	 * 
	 * @param genericEntityType
	 * @since 3.0.0
	 */
	protected AbstractDetailPanel(GenericEntityType genericEntityType) {
		this(genericEntityType, null);
	}

	/**
	 * 
	 * @param genericEntityType
	 * @param entityType
	 * @since 3.0.0
	 */
	protected AbstractDetailPanel(GenericEntityType genericEntityType, PeDataType entityType) {
		super();
		this.changeListenerList = new ArrayList<DetailChangeListener>();
		this.genericEntityType = genericEntityType;
		this.entityType = entityType;
		this.saveButton = UIFactory.createButton(ClientUtil.getInstance().getLabel("button.save"), "image.btn.small.save", new SaveL(), null);
		initPanel();

		setForViewOnly(true);

		addDetailChangeListener(new DetailChangeL());
		saveButton.setEnabled(false);
	}

	protected AbstractDetailPanel(PeDataType entityType) {
		this(null, entityType);
	}

	/**
	 * Adds additional UI components, if necessary.
	 * This should be overriden by sub-classes.
	 * This method implementation does nothing.
	 */
	protected abstract void addComponents(GridBagLayout bag, GridBagConstraints c);

	public final void addDetailChangeListener(DetailChangeListener dcl) {
		synchronized (changeListenerList) {
			if (!changeListenerList.contains(dcl)) {
				changeListenerList.add(dcl);
			}
		}
	}

	protected abstract void addDocumentListener(DocumentListener dl, MChangeListener mchangeListener);

	@Override
	public final void changedUpdate(DocumentEvent arg0) {
		// noop
	}

	public abstract void clearFields();

	@Override
	public void discardChanges() {
		resetFields();
		setHasChangesStatus(false);
		this.wasSaveForUpdate = false;
	}

	public final void fireDetailChanged() {
		synchronized (changeListenerList) {
			for (int i = 0; i < changeListenerList.size(); i++) {
				changeListenerList.get(i).detailChanged();
			}
		}
	}

	protected final void fireDetailSaved() {
		synchronized (changeListenerList) {
			for (int i = 0; i < changeListenerList.size(); i++) {
				changeListenerList.get(i).detailSaved();
			}
		}
	}

	public final int getCurrentEntityID() {
		return (currentObject == null ? -1 : currentObject.getID());
	}

	public synchronized boolean hasUnsavedChanges() {
		return hasChanges;
	}

	/**
	 * The save4update flag is set to false after a call to this.
	 * @return <code>true</code> if a save button was for an update
	 */
	public final boolean hasUpdateMade() {
		boolean returnVal = wasSaveForUpdate;
		wasSaveForUpdate = false;
		return returnVal;
	}

	private void initPanel() {
		GridBagLayout bag = new GridBagLayout();
		this.setLayout(bag);

		GridBagConstraints c = new GridBagConstraints();
		c.anchor = GridBagConstraints.NORTHWEST;
		c.insets = new Insets(4, 4, 4, 4);
		c.fill = GridBagConstraints.BOTH;
		c.weighty = 0.0;
		c.gridheight = 1;

		c.gridwidth = GridBagConstraints.REMAINDER;
		c.weightx = 1.0;

		ButtonPanel buttonPanel = new ButtonPanel(new JButton[] { saveButton }, FlowLayout.LEFT);
		addComponent(this, bag, c, buttonPanel);

		c.insets = new Insets(1, 1, 1, 1);
		addComponent(this, bag, c, new JSeparator());

		c.insets = new Insets(1, 2, 1, 1);

		// add impl-specific fields
		addComponents(bag, c);

		c.gridwidth = GridBagConstraints.REMAINDER;
		c.weightx = 1.0;
		c.gridheight = GridBagConstraints.REMAINDER;
		c.weighty = 1.0;
		addComponent(this, bag, c, Box.createVerticalGlue());

	}

	@Override
	public void insertUpdate(DocumentEvent arg0) {
		fireDetailChanged();
	}

	protected abstract void populateDetails(T object);

	public final void populateFields(T object) {
		this.currentObject = object;
		saveButton.setEnabled(false);

		removeDocumentListener(this, this);
		populateDetails(object);
		addDocumentListener(this, this);
	}

	public abstract void populateForClone(T object);

	public final void removeDetailChangeListener(DetailChangeListener dcl) {
		synchronized (changeListenerList) {
			if (changeListenerList.contains(dcl)) {
				changeListenerList.remove(dcl);
			}
		}
	}

	protected abstract void removeDocumentListener(DocumentListener dl, MChangeListener mchangeListener);

	@Override
	public void removeUpdate(DocumentEvent arg0) {
		fireDetailChanged();
	}

	public void resetFields() {
		List<DetailChangeListener> temp = this.changeListenerList;
		this.changeListenerList = new ArrayList<DetailChangeListener>();
		clearFields();
		this.changeListenerList = temp;
	}

	@Override
	public void saveChanges() throws CanceledException, ServerException {
		try {
			saveEntry_aux();
		}
		catch (InputValidationException ex) {
			ClientUtil.getInstance().showWarning("msg.warning.validation.input", new Object[] { ex.getMessage() });
			throw CanceledException.getInstance();
		}
	}

	public void saveEntity() {
		saveEntity_internal();
	}

	/**
	 * Assumes the entity has been locked already.
	 *
	 */
	private synchronized void saveEntity_internal() {
		try {
			saveEntry_aux();
		}
		catch (InputValidationException ex) {
			ClientUtil.getInstance().showWarning("msg.warning.validation.input", new Object[] { ex.getMessage() });
		}
		catch (ValidationException ex) {
			ValidationErrorReportDialog.showErrors(ex, false);
		}
		catch (ServerException ex) {
			ClientUtil.getInstance().showErrorDialog("msg.error.failure.save", new Object[] { entityType, ClientUtil.getInstance().getErrorMessage(ex) });
		}
	}

	private void saveEntry_aux() throws InputValidationException, ServerException {
		validateFields();

		wasSaveForUpdate = getCurrentEntityID() != -1;
		setCurrentObjectFromFields();

		int newID;
		if (currentObject instanceof CloneableEntity) {
			newID = CloneableEntity.class.cast(currentObject).isForClone() ? ClientUtil.getCommunicator().clone(
					(GenericEntity) currentObject,
					((CloneableEntity) currentObject).shouldCopyPolicies(),
					false) : ClientUtil.getCommunicator().save(currentObject, false);
		}
		else {
			newID = ClientUtil.getCommunicator().save(currentObject, false);
		}

		logDebug(ClientUtil.getLogger(), "newID [%d] generated for %s", newID, currentObject);

		// notify selection panel
		if (wasSaveForUpdate) {
			if (selectionPanel != null) {
				selectionPanel.updateDisplay(currentObject.getID());
				// TT 2072
				selectionPanel.update(currentObject);
			}
		}
		else {
			currentObject.setID(newID);
			// TT-22; clear forClone after save operation
			if (CloneableEntity.class.isInstance(currentObject) && CloneableEntity.class.cast(currentObject).isForClone()) {
				CloneableEntity.class.cast(currentObject).setForClone(false);
			}
			if (selectionPanel != null) {
				selectionPanel.add(currentObject);
				selectionPanel.selectEntity(currentObject.getID());
			}

			setForViewOnly(false);
			removeDocumentListener(this, this);
			addDocumentListener(this, this);
		}

		fireDetailSaved();
	}

	protected abstract void setCurrentObjectFromFields();

	protected abstract void setEnabledFields(boolean enabled);

	public final void setForViewOnly(boolean forViewOnly) {
		setEnabledFields(!forViewOnly);
	}

	private synchronized void setHasChangesStatus(boolean hasChanges) {
		this.hasChanges = hasChanges;
		saveButton.setEnabled(hasChanges);
	}

	public final void setSelectionPanel(AbstractSelectionPanel<T, B> selectionPanel) {
		this.selectionPanel = selectionPanel;
	}

	/**
	 * Overwrite to perform input validation.
	 * That is, validate values of the fields here.
	 * @throws InputValidationException if an input field contains an invalid entry
	 */
	protected void validateFields() throws InputValidationException {
	}

	@Override
	public void valueChanged(MChangeEvent arg0) {
		fireDetailChanged();
	}
}