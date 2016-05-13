package com.mindbox.pe.client.applet.template.guideline;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.zip.GZIPInputStream;

import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

import mseries.ui.MChangeEvent;
import mseries.ui.MChangeListener;

import com.mindbox.pe.client.ClientUtil;
import com.mindbox.pe.client.InputValidationException;
import com.mindbox.pe.client.applet.UIFactory;
import com.mindbox.pe.client.applet.template.rule.RuleChangeListener;
import com.mindbox.pe.client.applet.validate.DomainModel;
import com.mindbox.pe.client.common.AbstractThreadedActionAdapter;
import com.mindbox.pe.client.common.PanelBase;
import com.mindbox.pe.client.common.detail.DetailChangeListener;
import com.mindbox.pe.client.common.selection.GuidelineTemplateSelectionListener;
import com.mindbox.pe.client.common.selection.GuidelineTemplateSelectionPanel;
import com.mindbox.pe.client.common.tab.PowerEditorTabPanel;
import com.mindbox.pe.common.UtilBase;
import com.mindbox.pe.common.validate.TemplateValidator;
import com.mindbox.pe.communication.ServerException;
import com.mindbox.pe.model.AbstractIDObject;
import com.mindbox.pe.model.GuidelineContext;
import com.mindbox.pe.model.GuidelineReportData;
import com.mindbox.pe.model.PeDataType;
import com.mindbox.pe.model.TemplateUsageType;
import com.mindbox.pe.model.exceptions.CanceledException;
import com.mindbox.pe.model.filter.GuidelineReportFilter;
import com.mindbox.pe.model.filter.TemplateByNameVersionFilter;
import com.mindbox.pe.model.grid.AbstractGuidelineGrid;
import com.mindbox.pe.model.grid.GridCellCoordinates;
import com.mindbox.pe.model.grid.ProductGrid;
import com.mindbox.pe.model.report.GuidelineReportSpec;
import com.mindbox.pe.model.table.CategoryOrEntityValues;
import com.mindbox.pe.model.table.EnumValues;
import com.mindbox.pe.model.template.AbstractTemplateColumn;
import com.mindbox.pe.model.template.AbstractTemplateCore;
import com.mindbox.pe.model.template.ColumnDataSpecDigest;
import com.mindbox.pe.model.template.GridTemplate;
import com.mindbox.pe.model.template.GridTemplateColumn;
import com.mindbox.pe.xsd.config.GuidelineTab;

/**
 * Template detail panel.
 * @author Geneho Kim
 * @author MindBox
 * @since PowerEditor 4.0.0
 */
public class TemplateDetailPanel extends PanelBase implements GuidelineTemplateSelectionListener, PowerEditorTabPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = -3951228734910107454L;


	private final class NewL extends AbstractThreadedActionAdapter {

		public void performAction(ActionEvent e) {
			try {
				GridTemplate template = TemplateWizardDialog.createTemplate((lastSelectedUsage == null ? templateSelectionPanel.getLastSelectedUsageType() : lastSelectedUsage));

				if (template != null) {
					template.setVersion(GridTemplate.DEFAULT_VERSION);
					templateSelected(template);

					setEnabled(true);
					isInDB = false;
					setHasChangesStatus(true);
				}
			}
			catch (Exception ex) {
				ClientUtil.handleRuntimeException(ex);
			}
		}
	}

	private final class MakeVersionL extends AbstractThreadedActionAdapter {

		public void performAction(ActionEvent e) {
			if (template != null) {
				GridTemplate clonedTemplate = new GridTemplate();
				clonedTemplate.copyFrom(template);
				clonedTemplate.setID(-1);
				final NewTemplateCutOverDetail newTemplateCutOverDetail = TemplateNewVersionDialog.newTemplateVersion(template.getID(), clonedTemplate);
				if (newTemplateCutOverDetail.getDateSynonym() == null) {
					clonedTemplate = null;
				}
				else {
					try {
						final int newID = ClientUtil.getCommunicator().makeNewVersion(
								template.getID(),
								clonedTemplate,
								newTemplateCutOverDetail.getDateSynonym(),
								newTemplateCutOverDetail.getGuidelinesToCutOver());
						clonedTemplate.setID(newID);

						templateSelectionPanel.addTemplate(clonedTemplate);

						isInDB = true;
						setEnabled(true);
						setHasChangesStatus(false);

						templateSelected(clonedTemplate);
					}
					catch (Exception ex) {
						ClientUtil.handleRuntimeException(ex);
					}
				}
			}
		}
	}

	private final class CopyL extends AbstractThreadedActionAdapter {

		public void performAction(ActionEvent e) {
			if (template == null) return;
			String newName = JOptionPane.showInputDialog(
					ClientUtil.getApplet(),
					ClientUtil.getInstance().getMessage("msg.inst.new.name.template") + ":",
					ClientUtil.getInstance().getLabel("d.title.copy.template"),
					JOptionPane.PLAIN_MESSAGE);
			if (!ClientUtil.isEmpty(newName)) {
				try {
					if (ClientUtil.getCommunicator().checkNameForUniqueness(PeDataType.TEMPLATE, newName)) {
						GridTemplate clonedTemplate = new GridTemplate();
						clonedTemplate.copyFrom(template);
						clonedTemplate.setID(-1);
						clonedTemplate.setName(newName);
						clonedTemplate.setVersion(GridTemplate.DEFAULT_VERSION);

						int newID = ClientUtil.getCommunicator().save(clonedTemplate, false);
						clonedTemplate.setID(newID);

						templateSelectionPanel.addTemplate(clonedTemplate);

						isInDB = true;
						setEnabled(true);
						setHasChangesStatus(false);
						templateSelected(clonedTemplate);
					}
					else {
						ClientUtil.getInstance().showErrorDialog("msg.warning.not.unique", new Object[] { "The specified template name" });
					}
				}
				catch (Exception ex) {
					ClientUtil.handleRuntimeException(ex);
				}
			}
		}
	}

	private final class SaveL extends AbstractThreadedActionAdapter {

		public void performAction(ActionEvent e) {
			try {
				saveTemplate_internal();
				// this is needed in case a new column has been added.
				ruleMessagePanel.refreshColumns();

				isInDB = true;
			}
			catch (ValidationException ex) {
				ex.showAsWarning();
			}
			catch (ServerException ex) {
				ClientUtil.handleRuntimeException(ex);
			}
			catch (InputValidationException ex) {
				ClientUtil.getInstance().showWarning("msg.warning.validation.input", new Object[] { ex.getMessage() });
			}
		}
	}

	private final class EditL extends AbstractThreadedActionAdapter {

		public void performAction(ActionEvent e) {
			if (template != null) {
				try {
					ClientUtil.getCommunicator().lock(template.getID(), PeDataType.TEMPLATE);

					setEnabled(true);
					editButton.setEnabled(false);
					//setHasChangesStatus(true);
				}
				catch (ServerException ex) {
					ClientUtil.handleRuntimeException(ex);
				}
			}
		}
	}

	private final class DeleteL extends AbstractThreadedActionAdapter {

		public void performAction(ActionEvent e) {
			// TT 953 - ask to delete guidelines for the template as well
			//Boolean deleteGuidelines = ClientUtil.getInstance().showConfirmationWithCancel("msg.question.delete.guidelines");
			//if (deleteGuidelines == null) return;

			// delete template
			if (template != null) {
				try {
					boolean templateHasGuidelines = ClientUtil.getCommunicator().hasGuidelines(template.getID());
					// TT 1151 -- this nullifies TT 953
					if (ClientUtil.getInstance().showConfirmation(
							(templateHasGuidelines ? "msg.question.delete.template.with.guideline" : "msg.question.delete.template.guideline"),
							new Object[] { template.getName(), template.getVersion() })) {
						if (isInDB == true) {
							ClientUtil.getCommunicator().deleteTemplate(template.getID(), true);
						}

						templateSelectionPanel.removeTemplate(template);
						setTemplate(null, true);
						isInDB = false;
					}
				}
				catch (ServerException ex) {
					ClientUtil.handleRuntimeException(ex);
				}
			}
		}
	}

	private class DetailChangeL implements DetailChangeListener {

		public void detailChanged() {
			setHasChangesStatus(true);
			//saveButton.setEnabled(true);
		}

		public void detailSaved() {
			setHasChangesStatus(false);
			//saveButton.setEnabled(false);
		}
	}

	public class FieldChangeListener implements ActionListener, DocumentListener, MChangeListener, ListSelectionListener, RuleChangeListener, TableModelListener, ChangeListener, ListDataListener {

		public final void changedUpdate(DocumentEvent arg0) {
		}

		public final void insertUpdate(DocumentEvent arg0) {
			fireDetailChanged();
		}

		public final void removeUpdate(DocumentEvent arg0) {
			fireDetailChanged();
		}

		public void actionPerformed(ActionEvent e) {
			fireDetailChanged();
		}

		public void valueChanged(MChangeEvent arg0) {
			fireDetailChanged();
		}

		public void valueChanged() {
			fireDetailChanged();
		}

		public void valueChanged(ListSelectionEvent arg0) {
			fireDetailChanged();
		}

		public void tableChanged(TableModelEvent arg0) {
			fireDetailChanged();
		}

		public void ruleChanged() {
			fireDetailChanged();
		}

		public void stateChanged(ChangeEvent arg0) {
			fireDetailChanged();
		}

		public void intervalAdded(ListDataEvent arg0) {
			fireDetailChanged();
		}

		public void intervalRemoved(ListDataEvent arg0) {
			fireDetailChanged();
		}

		public void contentsChanged(ListDataEvent arg0) {
			fireDetailChanged();
		}
	}

	private final JButton newButton;
	private final JButton editButton;
	private final JButton saveButton;
	private final JButton deleteButton;
	private final JButton cloneButton;
	private final JButton versionButton;

	private GridTemplate template = null;
	private TemplateUsageType lastSelectedUsage = null;

	private final TemplateDescriptionPanel descriptionPanel;
	private final TemplateColumnsPanel columnPanel;
	private final TemplateRuleMessagePanel ruleMessagePanel;
	private final GuidelineTemplateSelectionPanel templateSelectionPanel;
	private final JTabbedPane templateTab;

	private final List<DetailChangeListener> changeListenerList;
	private boolean hasChanges = false;
	private boolean isInDB = true;
	private FieldChangeListener fieldChangeListener = null;

	/**
	 * 
	 * @param templateSelectionPanel
	 * @throws ServerException
	 */
	public TemplateDetailPanel(GuidelineTemplateSelectionPanel templateSelectionPanel) throws ServerException {
		this.templateSelectionPanel = templateSelectionPanel;
		this.changeListenerList = new ArrayList<DetailChangeListener>();
		descriptionPanel = new TemplateDescriptionPanel();
		columnPanel = new TemplateColumnsPanel(this);
		ruleMessagePanel = new TemplateRuleMessagePanel(this);
		templateTab = new JTabbedPane();

		versionButton = UIFactory.createButton(ClientUtil.getInstance().getLabel("button.new.version"), null, new MakeVersionL(), "button.tooltip.new.template.version");
		newButton = UIFactory.createButton(ClientUtil.getInstance().getLabel("button.new.template"), null, new NewL(), "button.tooltip.new.template");
		cloneButton = UIFactory.createButton(ClientUtil.getInstance().getLabel("button.copy"), null, new CopyL(), null);
		editButton = UIFactory.createButton(ClientUtil.getInstance().getLabel("button.edit"), null, new EditL(), null);
		saveButton = UIFactory.createButton(ClientUtil.getInstance().getLabel("button.save"), null, new SaveL(), null);
		deleteButton = UIFactory.createButton(ClientUtil.getInstance().getLabel("button.delete.template"), null, new DeleteL(), null);

		initPanel();
		clearFields();

		descriptionPanel.addDocumentListener(getFieldChangeListener());

		addDetailChangeListener(new DetailChangeL());

		newButton.setEnabled(false);
		versionButton.setEnabled(false);
		editButton.setEnabled(false);
		deleteButton.setEnabled(false);
		cloneButton.setEnabled(false);
		saveButton.setEnabled(false);
	}

	public JButton[] getTemplateButtons() {
		return templateSelectionPanel.isReadOnly() ? new JButton[0] : new JButton[] { newButton, cloneButton, versionButton, deleteButton };
	}

	FieldChangeListener getFieldChangeListener() {
		if (fieldChangeListener == null) {
			fieldChangeListener = new FieldChangeListener();
		}
		return fieldChangeListener;
	}

	void columnAdded() {
		descriptionPanel.columnAdded(template);
	}

	void columnDeleted() {
		descriptionPanel.columnDeleted(template);
		ruleMessagePanel.refreshColumns();
	}

	void columnsSwapped() {
		descriptionPanel.refreshColumns(template);
		ruleMessagePanel.refreshColumns();
	}

	private void initPanel() {
		JPanel buttonPanel = null;
		if (!templateSelectionPanel.isReadOnly()) {
			buttonPanel = UIFactory.createJPanel(new FlowLayout(FlowLayout.LEFT));
			buttonPanel.add(editButton);
			buttonPanel.add(saveButton);
		}

		templateTab.addTab(" Description", descriptionPanel);
		templateTab.addTab(" Columns", columnPanel);
		templateTab.addTab(" Rules & Messages", ruleMessagePanel);

		setLayout(new BorderLayout(0, 0));
		if (buttonPanel != null) {
			add(buttonPanel, BorderLayout.NORTH);
		}
		add(templateTab, BorderLayout.CENTER);
		setBorder(UIFactory.createTitledBorder(ClientUtil.getInstance().getLabel("label.title.template.detail")));
	}

	public void setEnabled(boolean enabled) {
		descriptionPanel.setEditable(enabled);
		columnPanel.setEnabled(enabled);
		ruleMessagePanel.setEditable(enabled);
	}

	private void clearFields() {
		setEnabled(false);
		descriptionPanel.clearFields();
		columnPanel.clearFields();
		ruleMessagePanel.clearFields();
		saveButton.setEnabled(false);
	}

	public void discardChanges() {
		if (template != null) {
			// this unlock the template as well
			setTemplate(this.templateSelectionPanel.reloadTemplate(template), false);
		}
		hasChanges = false;
		isInDB = false;
	}

	private void unlockIfLocked() {
		if (this.template != null && this.template.getID() > 0 && !editButton.isEnabled()) {
			try {
				ClientUtil.getCommunicator().unlock(template.getID(), PeDataType.TEMPLATE);
			}
			catch (Exception ex) {
				ClientUtil.handleRuntimeException(ex);
			}
		}
	}

	/**
	 * @param template
	 */
	private void setTemplate(GridTemplate template, boolean dataChanged) {
		unlockIfLocked();

		this.template = template;
		if (template == null) {
			clearFields();
		}
		else {
			descriptionPanel.populateFields(template);
			columnPanel.populateFields(template, dataChanged);
			ruleMessagePanel.populateFields(template);
			lastSelectedUsage = template.getUsageType();
			isInDB = true;
		}
		setEnabled(false);
		setHasChangesStatus(false);

		// Enable the CRUD button iff the user has edit template privilege on this template's usageType
		JButton buttonArray[] = { editButton, deleteButton, cloneButton, versionButton, saveButton, newButton };
		if (template != null && template.getUsageType() != null) {//making sure the user has selected a template and a usage type
			if (ClientUtil.checkEditTemplatePermission(template)) {
				ClientUtil.updateVisibileAndEnableOfButtons(buttonArray, true, (template != null && template.getID() != -1));
			}
			else {
				ClientUtil.updateVisibileAndEnableOfButtons(buttonArray, true, false);
			}
		}
		else {
			ClientUtil.updateVisibileAndEnableOfButtons(buttonArray, true, (template != null && template.getID() != -1));
		}
		saveButton.setEnabled(false);
	}

	public synchronized void templateSelected(GridTemplate newTemplate) throws CanceledException {
		if (newTemplate == template) {
			return;
		}

		ClientUtil.getParent().setCursor(UIFactory.getWaitCursor());
		// prompt for unsaved changes, if appropriate
		try {
			if (template != null && hasChanges) {
				Boolean result = ClientUtil.getInstance().showSaveDiscardCancelDialog();
				if (result == null) {
					throw CanceledException.getInstance();
				}
				if (result.booleanValue()) {
					saveTemplate_internal();
				}
				else {
					discardChanges();
				}
			}
			setTemplate(newTemplate, true);
		}
		catch (CanceledException ex) {
			throw ex;
		}
		catch (ValidationException ex) {
			((ValidationException) ex).showAsWarning();
			throw CanceledException.getInstance();
		}
		catch (InputValidationException ex) {
			ClientUtil.getInstance().showWarning("msg.error.failure.save", new Object[] { "template", ex.getMessage() });
			throw CanceledException.getInstance();
		}
		catch (Exception ex) {
			ClientUtil.handleRuntimeException(ex);
		}
		finally {
			showDetailPanel();
			ClientUtil.getParent().setCursor(UIFactory.getDefaultCursor());
		}
	}

	public synchronized void usageSelected(TemplateUsageType usageType) throws CanceledException {
		ClientUtil.getParent().setCursor(UIFactory.getWaitCursor());
		try {
			if (template != null && hasChanges) {
				Boolean result = ClientUtil.getInstance().showSaveDiscardCancelDialog();
				if (result == null) {
					throw CanceledException.getInstance();
				}
				if (result.booleanValue()) {
					saveTemplate_internal();
				}
				else {
					discardChanges();
				}
			}

			setTemplate(null, true);
			lastSelectedUsage = usageType;
			if (ClientUtil.checkEditTemplatePermission(usageType)) {
				newButton.setEnabled(usageType != null);
			}
			else {
				newButton.setEnabled(false);
			}
		}
		catch (CanceledException ex) {
			throw ex;
		}
		catch (InputValidationException ex) {
			ClientUtil.getInstance().showWarning("msg.error.failure.save", new Object[] { "template", ex.getMessage() });
			throw CanceledException.getInstance();
		}
		catch (Exception ex) {
			ClientUtil.handleRuntimeException(ex);
		}
		finally {
			hideDetailPanel();
			ClientUtil.getParent().setCursor(UIFactory.getDefaultCursor());
		}
	}


	public synchronized void usageGroupSelected(GuidelineTab GuidelineTab) throws CanceledException {
		if (GuidelineTab.getUsageType() != null && !GuidelineTab.getUsageType().isEmpty()) {
			usageSelected(TemplateUsageType.valueOf(GuidelineTab.getUsageType().get(0).getName()));
		}
	}

	public synchronized void selectionCleared() {
		try {
			if (template != null && hasChanges) {
				Boolean result = ClientUtil.getInstance().showConfirmationWithCancel("msg.question.unsaved.changes.template", new Object[] { template.getName() });
				if (result == null) {
					throw CanceledException.getInstance();
				}
				if (result.booleanValue()) {
					saveTemplate_internal();
				}
				else {
					discardChanges();
				}
			}

			setTemplate(null, true);
		}
		catch (CanceledException ex) {
			// noop
		}
		catch (InputValidationException ex) {
			ClientUtil.getInstance().showWarning("msg.error.failure.save", new Object[] { "template", ex.getMessage() });
		}
		catch (Exception ex) {
			ClientUtil.handleRuntimeException(ex);
		}
	}

	public final void addDetailChangeListener(DetailChangeListener dcl) {
		synchronized (changeListenerList) {
			if (!changeListenerList.contains(dcl)) {
				changeListenerList.add(dcl);
			}
		}
	}

	public final void removeDetailChangeListener(DetailChangeListener dcl) {
		synchronized (changeListenerList) {
			if (changeListenerList.contains(dcl)) {
				changeListenerList.remove(dcl);
			}
		}
	}

	protected final void fireDetailChanged() {
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

	private synchronized void setHasChangesStatus(boolean hasChanges) {
		this.hasChanges = hasChanges;
		saveButton.setEnabled(hasChanges);
	}

	public synchronized boolean hasUnsavedChanges() {
		return hasChanges;
	}

	public void saveChanges() throws CanceledException, ServerException {
		try {
			saveTemplate_internal();
		}
		catch (ValidationException ex) {
			((ValidationException) ex).showAsWarning();
			throw CanceledException.getInstance();
		}
		catch (InputValidationException ex) {
			ClientUtil.getInstance().showWarning("msg.error.failure.save", new Object[] { "template", ex.getMessage() });
			throw CanceledException.getInstance();
		}
		catch (Exception ex) {
			ClientUtil.handleRuntimeException(ex);
		}
	}

	private synchronized void saveTemplate_internal() throws InputValidationException, ValidationException, ServerException {
		if (template == null) return;

		TemplateUsageType oldUsage = template.getUsageType();
		Map<String, String> columnNameToPreviousTypeMap = getColumnNameToTypeMap();
		String oldName = template.getName();
		String oldVersion = template.getVersion();

		descriptionPanel.updateFromFields(template);
		columnPanel.updateFromFields();
		ruleMessagePanel.updateFromFields();

		// validate name is unique
		boolean nameChanged = !oldName.equals(template.getName());
		if (nameChanged) {
			if (!ClientUtil.getCommunicator().checkNameForUniqueness(PeDataType.TEMPLATE, template.getName())) {
				template.setName(oldName);
				ClientUtil.getInstance().showWarning("msg.warning.not.unique", new Object[] { "The specified template name" });
				descriptionPanel.selectNameField();
				return;
			}
		}
		// validate version is unique
		if (!oldVersion.equals(template.getVersion())) {
			List<GridTemplate> templateList = ClientUtil.getCommunicator().search(new TemplateByNameVersionFilter(template.getName(), template.getVersion()));
			if (templateList != null && !templateList.isEmpty()) {
				template.setVersion(oldVersion);
				ClientUtil.getInstance().showWarning("msg.warning.not.unique", new Object[] { "The specified template version" });
				return;
			}
		}

		// validate rule if necessary
		int oldID = template.getID();
		if (oldID > 0) {
			try {
				validateRules();
			}
			catch (InputValidationException e) {
				setHasChangesStatus(true);
				throw e; // With rule problems the user can fix and re-save the template without losing work...
			}

			boolean save = validateGridData(columnNameToPreviousTypeMap); //...but with grid problems give them the option to save the template before fixing grid problems
			if (!save) {
				setHasChangesStatus(true);
				return;
			}
		}

		int newID = ClientUtil.getCommunicator().save(template, false);
		setHasChangesStatus(false);
		if (oldID < 0) {
			template.setID(newID);
			templateSelectionPanel.addTemplate(template);
		}
		else {
			if (oldUsage != template.getUsageType()) {
				templateSelectionPanel.moveTemplateNode(oldUsage, template);
			}
			else if (nameChanged) {
				templateSelectionPanel.updateTemplateName(template);
			}
			ClientUtil.getPreferenceManager().clearGridColumnWidths(template.getID());
		}

		setEnabled(true);
		saveButton.setEnabled(false);//because they just saved something
	}

	private Map<String, String> getColumnNameToTypeMap() {
		Map<String, String> result = new HashMap<String, String>();
		for (Iterator<GridTemplateColumn> columnIter = template.getColumns().iterator(); columnIter.hasNext();) {
			GridTemplateColumn column = columnIter.next();
			result.put(column.getName(), column.getColumnDataSpecDigest().getType());
		}
		return result;
	}

	private void validateRules() throws InputValidationException {
		String errMsg = TemplateValidator.isValid(template.getRuleDefinition(), template, DomainModel.getInstance());

		if (!ClientUtil.isEmpty(errMsg)) {
			throw new InputValidationException(errMsg);
		}
	}

	/** 
	 * @return true if save operation should proceed (with or without grid validation issues), 
	 *         or false if the save operation should be cancelled. 
	 */
	private synchronized boolean validateGridData(Map<String, String> columnNameToPreviousTypeMap) {
		try {
			Set<GuidelineContext[]> allContextsInUse = getAllContextsInUse();

			if (allContextsInUse.isEmpty()) {
				return true; // no grids, so template can safely be saved
			}

			LinkedList<GuidelineReportData> errorReportDataList = new LinkedList<GuidelineReportData>();

			for (Iterator<GuidelineContext[]> guidelineListIter = allContextsInUse.iterator(); guidelineListIter.hasNext();) {
				GuidelineContext[] context = guidelineListIter.next();

				List<ProductGrid> gridData = ClientUtil.getCommunicator().fetchGridData(template.getID(), context).getResultList();

				for (Iterator<ProductGrid> iter = gridData.iterator(); iter.hasNext();) {
					ProductGrid grid = iter.next();

					for (int columnIndex = 0; columnIndex < template.getNumColumns(); columnIndex++) {
						AbstractTemplateColumn column = template.getColumn(columnIndex + 1);
						boolean blankAllowed = column.getColumnDataSpecDigest().isBlankAllowed() && !(column.getColumnDataSpecDigest().getType().equals(ColumnDataSpecDigest.TYPE_BOOLEAN)); // boolean can always be blank?
						boolean multiAllowed = column.getColumnDataSpecDigest().isMultiSelectAllowed();

						for (int rowIndex = 0; rowIndex < grid.getNumRows(); rowIndex++) {
							Object value = grid.getCellValue(rowIndex + 1, column.getName());

							if (!blankAllowed && isBlank(value)) {
								addError(errorReportDataList, context, grid, rowIndex, columnIndex, ClientUtil.getInstance().getMessage("msg.warning.invalid.blank"));
							}

							if (!multiAllowed) {
								if (value instanceof EnumValues) {
									EnumValues<?> enumValues = (EnumValues<?>) value;
									if (enumValues.isSelectionExclusion()) {
										addError(errorReportDataList, context, grid, rowIndex, columnIndex, ClientUtil.getInstance().getMessage("msg.warning.invalid.select.excl"));
									}
									else if (enumValues.size() > 1) {
										addError(errorReportDataList, context, grid, rowIndex, columnIndex, ClientUtil.getInstance().getMessage("msg.warning.invalid.multiple"));
									}
								}
								else if (value instanceof CategoryOrEntityValues) {
									CategoryOrEntityValues entValues = (CategoryOrEntityValues) value;
									if (entValues.isSelectionExclusion()) {
										addError(errorReportDataList, context, grid, rowIndex, columnIndex, ClientUtil.getInstance().getMessage("msg.warning.invalid.select.excl"));
									}
									else if (entValues.size() > 1) {
										addError(errorReportDataList, context, grid, rowIndex, columnIndex, ClientUtil.getInstance().getMessage("msg.warning.invalid.multiple"));
									}
								}
							}

							String oldColumnType = columnNameToPreviousTypeMap.get(column.getName());
							String newColumnType = column.getColumnDataSpecDigest().getType();
							if (!newColumnType.equals(oldColumnType) && !isBlank(value)) {
								if (incompatibleColumnTypeValues(newColumnType, oldColumnType)) {
									addError(errorReportDataList, context, grid, rowIndex, columnIndex, ClientUtil.getInstance().getMessage("msg.warning.value.incompatible.with.colType"));
								}
							}
						}
					}
				}
			}
			if (!errorReportDataList.isEmpty()) {
				return showErrorReport(errorReportDataList);
			}
		}
		catch (Exception ex) {
			ClientUtil.handleRuntimeException(ex);
			return false;
		}

		return true;
	}

	/*
	New Column Type     Invalid Data When Old Column Type Was
	---------------     -------------------------------------
	Boolean             <All>
	Currency            <All except Float, Integer>
	Currency Range      <All except Float Range, Integer Range>
	Date                <All>
	Date Range          <All>
	Dynamic String      <None>
	Entity List         <All>
	Enum List           <All>
	Float               <All except Currency, Integer>
	Float Range         <All except Currency Range, Integer Range>
	Integer             <All>
	Integer Range       <All>
	Time Range          <All>
	String              <None>
	Symbol              <All except Float, Integer, Currency>
	 */
	private static final Map<String, Set<String>> NEW_COLUMN_TYPE_TO_COMPATIBLE_OLD_COLUMN_TYPES_MAP;
	static {
		NEW_COLUMN_TYPE_TO_COMPATIBLE_OLD_COLUMN_TYPES_MAP = new HashMap<String, Set<String>>(15, 1.0f);

		// Types for which no old value is compatible, regardless of old column type
		NEW_COLUMN_TYPE_TO_COMPATIBLE_OLD_COLUMN_TYPES_MAP.put(ColumnDataSpecDigest.TYPE_BOOLEAN, new HashSet<String>());
		NEW_COLUMN_TYPE_TO_COMPATIBLE_OLD_COLUMN_TYPES_MAP.put(ColumnDataSpecDigest.TYPE_DATE, new HashSet<String>());
		NEW_COLUMN_TYPE_TO_COMPATIBLE_OLD_COLUMN_TYPES_MAP.put(ColumnDataSpecDigest.TYPE_DATE_RANGE, new HashSet<String>());
		NEW_COLUMN_TYPE_TO_COMPATIBLE_OLD_COLUMN_TYPES_MAP.put(ColumnDataSpecDigest.TYPE_ENTITY, new HashSet<String>());
		NEW_COLUMN_TYPE_TO_COMPATIBLE_OLD_COLUMN_TYPES_MAP.put(ColumnDataSpecDigest.TYPE_ENUM_LIST, new HashSet<String>());
		NEW_COLUMN_TYPE_TO_COMPATIBLE_OLD_COLUMN_TYPES_MAP.put(ColumnDataSpecDigest.TYPE_INTEGER, new HashSet<String>());
		NEW_COLUMN_TYPE_TO_COMPATIBLE_OLD_COLUMN_TYPES_MAP.put(ColumnDataSpecDigest.TYPE_INTEGER_RANGE, new HashSet<String>());
		NEW_COLUMN_TYPE_TO_COMPATIBLE_OLD_COLUMN_TYPES_MAP.put(ColumnDataSpecDigest.TYPE_TIME_RANGE, new HashSet<String>());

		// Types for which all old values are compatible, regardless of old column type
		String[] allTypes = new String[] {
				ColumnDataSpecDigest.TYPE_BOOLEAN,
				ColumnDataSpecDigest.TYPE_CURRENCY,
				ColumnDataSpecDigest.TYPE_CURRENCY_RANGE,
				ColumnDataSpecDigest.TYPE_DATE,
				ColumnDataSpecDigest.TYPE_DATE_RANGE,
				ColumnDataSpecDigest.TYPE_DYNAMIC_STRING,
				ColumnDataSpecDigest.TYPE_ENTITY,
				ColumnDataSpecDigest.TYPE_ENUM_LIST,
				ColumnDataSpecDigest.TYPE_FLOAT,
				ColumnDataSpecDigest.TYPE_FLOAT_RANGE,
				ColumnDataSpecDigest.TYPE_INTEGER,
				ColumnDataSpecDigest.TYPE_INTEGER_RANGE,
				ColumnDataSpecDigest.TYPE_TIME_RANGE,
				ColumnDataSpecDigest.TYPE_STRING,
				ColumnDataSpecDigest.TYPE_SYMBOL };
		NEW_COLUMN_TYPE_TO_COMPATIBLE_OLD_COLUMN_TYPES_MAP.put(ColumnDataSpecDigest.TYPE_DYNAMIC_STRING, new HashSet<String>(Arrays.asList(allTypes)));
		NEW_COLUMN_TYPE_TO_COMPATIBLE_OLD_COLUMN_TYPES_MAP.put(ColumnDataSpecDigest.TYPE_STRING, new HashSet<String>(Arrays.asList(allTypes)));

		// Types for which there are specific old type to new type compatibilities
		NEW_COLUMN_TYPE_TO_COMPATIBLE_OLD_COLUMN_TYPES_MAP.put(
				ColumnDataSpecDigest.TYPE_CURRENCY,
				new HashSet<String>(Arrays.asList(new String[] { ColumnDataSpecDigest.TYPE_FLOAT, ColumnDataSpecDigest.TYPE_INTEGER })));
		NEW_COLUMN_TYPE_TO_COMPATIBLE_OLD_COLUMN_TYPES_MAP.put(
				ColumnDataSpecDigest.TYPE_CURRENCY_RANGE,
				new HashSet<String>(Arrays.asList(new String[] { ColumnDataSpecDigest.TYPE_FLOAT_RANGE, ColumnDataSpecDigest.TYPE_INTEGER_RANGE })));
		NEW_COLUMN_TYPE_TO_COMPATIBLE_OLD_COLUMN_TYPES_MAP.put(
				ColumnDataSpecDigest.TYPE_FLOAT,
				new HashSet<String>(Arrays.asList(new String[] { ColumnDataSpecDigest.TYPE_CURRENCY, ColumnDataSpecDigest.TYPE_INTEGER })));
		NEW_COLUMN_TYPE_TO_COMPATIBLE_OLD_COLUMN_TYPES_MAP.put(
				ColumnDataSpecDigest.TYPE_FLOAT_RANGE,
				new HashSet<String>(Arrays.asList(new String[] { ColumnDataSpecDigest.TYPE_CURRENCY_RANGE, ColumnDataSpecDigest.TYPE_INTEGER_RANGE })));
		NEW_COLUMN_TYPE_TO_COMPATIBLE_OLD_COLUMN_TYPES_MAP.put(
				ColumnDataSpecDigest.TYPE_SYMBOL,
				new HashSet<String>(Arrays.asList(new String[] { ColumnDataSpecDigest.TYPE_FLOAT, ColumnDataSpecDigest.TYPE_INTEGER, ColumnDataSpecDigest.TYPE_CURRENCY })));
	}

	private boolean incompatibleColumnTypeValues(String newColumnType, String oldColumnType) {
		return !NEW_COLUMN_TYPE_TO_COMPATIBLE_OLD_COLUMN_TYPES_MAP.get(newColumnType).contains(oldColumnType);
	}

	private boolean isBlank(Object value) {
		return value == null || (!(value instanceof Boolean) && value.toString().length() == 0); // assumes no class other than String will return blank from toString()
	}

	private Set<GuidelineContext[]> getAllContextsInUse() throws ServerException {
		GuidelineReportFilter filter = new GuidelineReportFilter();
		filter.setIncludeParameters(false);
		filter.addGuidelineTemplateID(new Integer(template.getID()));

		List<AbstractIDObject> allGuidelines = ClientUtil.getCommunicator().search(filter);

		if (allGuidelines == null) {
			return new HashSet<GuidelineContext[]>();
		}

		Set<GuidelineContext[]> foundContexts = new HashSet<GuidelineContext[]>();

		for (Iterator<AbstractIDObject> guidelineIter = allGuidelines.iterator(); guidelineIter.hasNext();) {
			GuidelineContext[] context = ((GuidelineReportData) guidelineIter.next()).getContext();

			if (!alreadyFound(context, foundContexts)) {
				foundContexts.add(context);
			}
		}
		return foundContexts;
	}

	private boolean alreadyFound(GuidelineContext[] context, Set<GuidelineContext[]> foundContexts) {
		if (foundContexts.contains(context)) {
			return true; // ==
		}
		for (Iterator<GuidelineContext[]> processedContextsIter = foundContexts.iterator(); processedContextsIter.hasNext();) {
			GuidelineContext[] processedContext = processedContextsIter.next();
			if (GuidelineContext.isIdentical(processedContext, context)) {
				return true; // .equals()
			}
		}
		foundContexts.add(context);
		return false;
	}

	private void addError(LinkedList<GuidelineReportData> errorReportDataList, GuidelineContext[] context, AbstractGuidelineGrid grid, int rowIndex, int columnIndex, String errorDesc) {
		AbstractTemplateCore<GridTemplateColumn> template = grid.getTemplate();

		if (isNewGuideline(grid, context, errorReportDataList)) {
			// This is the first error for this Guideline.
			GuidelineReportData reportData = new GuidelineReportData(
					template.getID(),
					grid.getID(),
					template.getName(),
					template.getUsageType(),
					template.getVersion(),
					context,
					grid.getCreationDate(),
					grid.getEffectiveDate(),
					grid.getExpirationDate(),
					false);

			errorReportDataList.add(reportData);

			reportData.getCellSubset().add(new GridCellCoordinates(rowIndex, columnIndex, errorDesc));

		}
		else {
			// This Guideline already has at least one error (in some cell, maybe not this one).
			GuidelineReportData reportData = errorReportDataList.getLast();
			GridCellCoordinates existingCoords = reportData.getCellSubset().get(rowIndex, columnIndex);

			if (existingCoords == null) {
				// first error for this cell
				reportData.getCellSubset().add(new GridCellCoordinates(rowIndex, columnIndex, errorDesc));
			}
			else {
				// already an error of a different type in this cell
				existingCoords.setPayload(existingCoords.getPayload().toString() + ", " + errorDesc);
			}
		}
	}

	private boolean isNewGuideline(AbstractGuidelineGrid currentGrid, GuidelineContext[] currentContext, LinkedList<GuidelineReportData> errorReportDataList) {
		if (errorReportDataList.isEmpty()) {
			return true;
		}

		GuidelineReportData lastReportData = errorReportDataList.getLast();
		return lastReportData.getTemplateID() != currentGrid.getTemplateID() || !UtilBase.nullSafeEquals(lastReportData.getActivationDate(), currentGrid.getEffectiveDate())
				|| !GuidelineContext.isIdentical(lastReportData.getContext(), currentContext);
	}

	private boolean showErrorReport(LinkedList<GuidelineReportData> errorReportDataList) throws Exception {
		return TemplateValidationErrorReportDialog.showErrorReport(generateValidationErrorReport(errorReportDataList), errorReportDataList.size());
	}

	private String generateValidationErrorReport(LinkedList<GuidelineReportData> errorReportDataList) throws Exception {
		byte[] zippedBytes = ClientUtil.getCommunicator().generatePolicySummaryReport(initValidationErrorReportSpec(), errorReportDataList);

		StringWriter stringBuffer = new StringWriter();
		PrintWriter writer = new PrintWriter(new BufferedWriter(stringBuffer), true);
		BufferedReader reader = new BufferedReader(new InputStreamReader(new GZIPInputStream(new ByteArrayInputStream(zippedBytes))));
		for (String line = reader.readLine(); line != null; line = reader.readLine()) {
			writer.println(line);
		}
		writer.flush();
		writer.close();

		return stringBuffer.toString();
	}

	private static GuidelineReportSpec initValidationErrorReportSpec() {
		GuidelineReportSpec spec = new GuidelineReportSpec();
		spec.setCreatedDateOn(true);
		spec.setStatusOn(true);
		spec.setStatusChangeDateOn(true);
		spec.setCommentsOn(true);
		spec.setGridOn(true);
		return spec;
	}

	private synchronized void hideDetailPanel() {
		templateTab.setVisible(false);
		templateTab.setEnabled(false);
	}

	private synchronized void showDetailPanel() {
		templateTab.setEnabled(true);
		templateTab.setVisible(true);
	}

}