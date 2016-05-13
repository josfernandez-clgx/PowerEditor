package com.mindbox.pe.client.applet.parameters;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import javax.swing.ToolTipManager;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import com.mindbox.pe.client.ClientUtil;
import com.mindbox.pe.client.EntityModelCacheFactory;
import com.mindbox.pe.client.applet.PowerEditorSwingTheme;
import com.mindbox.pe.client.applet.UIFactory;
import com.mindbox.pe.client.applet.guidelines.search.GuidelineListPanel;
import com.mindbox.pe.client.applet.validate.Validator;
import com.mindbox.pe.client.common.AbstractThreadedActionAdapter;
import com.mindbox.pe.client.common.DateSelectorComboField;
import com.mindbox.pe.client.common.PanelBase;
import com.mindbox.pe.client.common.TypeEnumValueComboBox;
import com.mindbox.pe.client.common.context.GuidelineContextPanel;
import com.mindbox.pe.client.common.dialog.ParameterContextDialog;
import com.mindbox.pe.client.common.event.EntityDeleteEvent;
import com.mindbox.pe.client.common.event.EntityDeleteListener;
import com.mindbox.pe.client.common.grid.ExcelAdapter;
import com.mindbox.pe.client.common.rowheader.RowHeaderTable;
import com.mindbox.pe.client.common.tab.PowerEditorTabPanel;
import com.mindbox.pe.common.PrivilegeConstants;
import com.mindbox.pe.common.UtilBase;
import com.mindbox.pe.communication.ServerException;
import com.mindbox.pe.model.DateSynonym;
import com.mindbox.pe.model.GenericEntity;
import com.mindbox.pe.model.PeDataType;
import com.mindbox.pe.model.exceptions.CanceledException;
import com.mindbox.pe.model.grid.ParameterGrid;
import com.mindbox.pe.model.template.ParameterTemplate;

/**
 * Parameter detail panel.
 * 
 * @author Geneho Kim
 * @author MindBox
 * @since PowerEditor 2.1.0
 */
public class ParameterDetailPanel extends PanelBase implements EntityDeleteListener, PowerEditorTabPanel {
	private final class BackL extends AbstractThreadedActionAdapter {

		private void discardChanges() {
			setDirty(false);
			gridTableModel.setDirty(false);
			clearFields();
			clearGrid();
			ParameterGrid grid = getSelectedGrid();
			if (grid != null) {
				actField.setValue(grid.getEffectiveDate());
				expField.setValue(grid.getExpirationDate());
			}
			setEnabledSelectionAwareButtons(contextTable.getSelectedRow() >= 0);
		}

		public void performAction(ActionEvent e) {
			// if there are unsaved changes, prompt for saving first
			if (hasUnsavedChanges()) {
				Boolean result = ClientUtil.getInstance().showConfirmationWithCancel("msg.question.grid.save");
				if (result == null) return; // cancel

				boolean save = result.booleanValue();
				if (save) {
					try {
						if (!saveChanges_internal()) {
							return;
						}
					}
					catch (ServerException e1) {
						ClientUtil.handleRuntimeException(e1);
						return;
					}
				}
				else { // discard
					discardChanges();
				}
			}

			if ((guidelineListPanel == null) && (pmTab != null)) { // if it came from Param
				// Template Selection Panel
				try {
					unlockSelectedGrid();
				}
				catch (Exception ex) {
					ClientUtil.getLogger().error("Failed to unlock the selected grid: " + getSelectedGrid(), ex);
				}
			}
			else { // if it came from Guideline List Panel.
				ParameterGrid grid = guidelineListPanel.getTable().getSelectedDataObject().getParameterGrid();
				try {
					unlockGrid(grid);
				}
				catch (Exception ex) {
					ClientUtil.getLogger().error("Failed to unlock the selected grid: " + grid, ex);
				}
			}
			showGridSelectionPanel();
			setEnabled(true);
			setEnabledSelectionAwareButtons(contextTable.getSelectedRow() >= 0);
		}
	}

	private final class CloneL extends AbstractThreadedActionAdapter {

		public void performAction(ActionEvent e) {
			ParameterGrid originalGrid = getSelectedGrid();
			originalGrid.setTemplate(template);
			Object[] values = ParameterContextDialog.cloneParameterGrid(originalGrid);
			ParameterGrid grid = (ParameterGrid) values[0];
			if (grid != null) {
				try {
					if (((Boolean) values[1]).booleanValue() && (grid.getEffectiveDate() == null || grid.getEffectiveDate().after(originalGrid.getEffectiveDate()))) {
						if (originalGrid.getExpirationDate() == null) {
							Calendar cal = Calendar.getInstance();
							cal.setTime(grid.getSunrise());
							cal.add(Calendar.DATE, -1);
							originalGrid.setExpirationDate(DateSynonym.createUnnamedInstance(cal.getTime()));

							ClientUtil.getCommunicator().save(originalGrid, false);
						}
					}

					grid.setID(-1);
					int id = ClientUtil.getCommunicator().save(grid, false);
					grid.setID(id);

					contextTableModel.addParameterGrid(grid);
					clearGrid();
					selectGrid(grid);
				}
				catch (Exception ex) {
					ClientUtil.handleRuntimeException(ex);
				}
			}
		}
	}

	private class ContextTableSelectionL implements ListSelectionListener {

		public void valueChanged(ListSelectionEvent arg0) {
			setEnabledSelectionAwareButtons(contextTable.getSelectedRow() >= 0);
		}
	}

	private final class DeleteL extends AbstractThreadedActionAdapter {

		public void actionPerformed(ActionEvent arg0) {
			if (ClientUtil.getInstance().showConfirmation("msg.question.delete.parameter")) {
				super.actionPerformed(arg0);
			}
		}

		public void performAction(ActionEvent e) {
			try {
				lockSelectedGrid();

				ParameterGrid grid = getSelectedGrid();
				ClientUtil.getCommunicator().delete(grid.getID(), PeDataType.PARAMETER_GRID);

				unlockSelectedGrid();

				contextTableModel.removeParameterGrid(grid);

				clearGrid();
			}
			catch (ServerException ex) {
				ClientUtil.handleRuntimeException(ex);
			}
		}
	}

	private final class EditL extends AbstractThreadedActionAdapter {

		public void performAction(ActionEvent e) {
			showGrid(true);
		}
	}

	/**
	 * 
	 * @author nill
	 * @since PowerEditor 4.2.
	 * 
	 */
	private final class EditParamContextL extends AbstractThreadedActionAdapter {

		public void performAction(ActionEvent e) {
			ParameterGrid originalGrid = getSelectedGrid();
			ParameterContextDialog.editParameterGrid(originalGrid);

			try {
				ClientUtil.getCommunicator().save(originalGrid, false);
				contextTableModel.fireTableRowsUpdated(contextTable.getSelectedRow(), contextTable.getSelectedRow());
				setEnabledSelectionAwareButtons(contextTable.getSelectedRow() >= 0);
			}
			catch (Exception ex) {
				ClientUtil.handleRuntimeException(ex);
			}
		}
	}

	private class GridAdapter extends AbstractThreadedActionAdapter {

		public void performAction(ActionEvent actionevent) {
			Object obj = actionevent.getSource();
			if (obj == appendRowButton) {
				gridTable.addRow(-1);
			}
			else if (obj == addRowButton) {
				gridTable.addRow(gridTable.getSelectedRow());
			}
			else if (obj == deleteRowButton) {
				gridTable.removeRow();
				gridTableModel.setDirty(true);
			}
			else if (obj == cutButton) {
				excelAdapter.cut();
				gridTableModel.setDirty(true);
			}
			else if (obj == copyButton) {
				excelAdapter.copy();
			}
			else if (obj == pasteButton) {
				excelAdapter.paste();
				gridTableModel.setDirty(true);
			}
			else {
				ClientUtil.getLogger().warn("Unknown Action Event= " + actionevent);
			}
			updateButtonStates();
		}
	}

	private final class NewL extends AbstractThreadedActionAdapter {

		public void performAction(ActionEvent e) {
			ParameterGrid context = ParameterContextDialog.newParameterGrid();
			if (context != null) {
				context.setTemplateID(template.getID());
				context.setTemplate(template);
				try {
					int id = ClientUtil.getCommunicator().save(context, false);
					context.setID(id);

					contextTableModel.addParameterGrid(context);
					clearGrid();
					selectGrid(context);
				}
				catch (ServerException ex) {
					ClientUtil.handleRuntimeException(ex);
				}
			}
			setEnabledSelectionAwareButtons(contextTable.getSelectedRow() >= 0);
		}
	}

	private final class SaveL extends AbstractThreadedActionAdapter {

		public void performAction(ActionEvent e) {
			try {
				saveChanges_internal();
			}
			catch (ServerException ex) {
				ClientUtil.handleRuntimeException(ex);
			}
		}
	}

	private final class ShowDateNameL extends AbstractThreadedActionAdapter {

		public void performAction(ActionEvent e) {
			contextTable.refresh(dateNameCheckbox.isSelected());
		}
	}

	private class StatusComboL implements ActionListener {

		public void actionPerformed(ActionEvent event) {
			String newStatus = statusField.getSelectedEnumValueValue();
			if (!ClientUtil.checkPermissionByPrivilegeName(PrivilegeConstants.PRIV_EDIT_PRODUCTION_DATA) && ClientUtil.isHighestStatus(newStatus)) {
				if (isGridActive()) {
					ClientUtil.getInstance().showErrorDialog("msg.error.statuschange.activate", new Object[] { ClientUtil.getHighestStatusDisplayLabel() });
					setSelectedStatus(getSelectedGrid().getStatus());
				}
				else if (!ClientUtil.getInstance().showConfirmation("msg.confirm.statuschange.parameter.hightest", new Object[] { ClientUtil.getHighestStatusDisplayLabel() })) {
					setSelectedStatus(getSelectedGrid().getStatus());
				}
			}
		}
	}

	private final class ViewL extends AbstractThreadedActionAdapter {

		public void performAction(ActionEvent e) {
			showGrid(false);
		}
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = -3951228734910107454L;

	private static final String LAYOUT_KEY_SELECTION = "GUIDELINESELECTION";
	private static final String LAYOUT_KEY_MANAGEMENT = "GRIDMANAGEMENT";

	private final JButton newButton;
	private final JButton editButton;
	private final JButton saveButton;
	private final JButton viewButton;
	private final JButton deleteButton;
	private final JButton editParamContextButton;
	private final JButton cloneButton;

	private final JButton backButton;
	private JButton appendRowButton;
	private JButton addRowButton;
	private JButton deleteRowButton;
	private JButton cutButton;
	private JButton copyButton;
	private JButton pasteButton;
	private final JLabel templateNameLabel;
	private final JTextArea templateDescTextArea;
	private final ParameterContextTable contextTable;
	private final ParameterContextTableModel contextTableModel;
	private final ParameterGridTable gridTable;
	private final ParameterGridTableModel gridTableModel;
	private ParameterTemplate template = null;
	private JPanel centerPanel = null;
	private final ExcelAdapter excelAdapter;
	private final CardLayout cardLayout;
	private final GuidelineContextPanel contextPanel;
	private final DateSelectorComboField actField, expField;
	private final TypeEnumValueComboBox statusField;
	private final JPanel topPanel;
	private ParameterTemplateSelectionPanel selectionPanel = null;
	private final ParameterManagerTab pmTab;
	private GuidelineListPanel guidelineListPanel;
	private JPanel gridEditPanel;
	private boolean isDirty;
	private final JCheckBox dateNameCheckbox;
	private final StatusComboL statusComboL = new StatusComboL();

	private final boolean readOnly;

	public ParameterDetailPanel(GuidelineListPanel guidelineListPanel, boolean readOnly) {
		this((ParameterManagerTab) null, readOnly);
		this.guidelineListPanel = guidelineListPanel;
	}

	protected ParameterDetailPanel(ParameterManagerTab pmTab, boolean readOnly) {
		super();
		this.readOnly = readOnly;
		topPanel = UIFactory.createJPanel(new GridLayout(1, 2, 0, 0));
		this.contextPanel = new GuidelineContextPanel("button.edit.context", false, false);
		this.pmTab = pmTab;
		this.guidelineListPanel = null;

		contextTableModel = new ParameterContextTableModel();
		contextTable = new ParameterContextTable(contextTableModel);

		dateNameCheckbox = UIFactory.createCheckBox("checkbox.show.date.name");
		dateNameCheckbox.setSelected(true);
		dateNameCheckbox.addActionListener(new ShowDateNameL());

		gridTableModel = new ParameterGridTableModel(this);
		gridTable = new ParameterGridTable(gridTableModel);
		initGridTable();
		excelAdapter = new ExcelAdapter(gridTable);

		templateNameLabel = new JLabel();
		templateNameLabel.setFont(PowerEditorSwingTheme.boldFont);
		templateDescTextArea = new JTextArea();
		templateDescTextArea.setEditable(false);
		templateDescTextArea.setRows(4);
		templateDescTextArea.setLineWrap(true);
		templateDescTextArea.setWrapStyleWord(true);
		templateDescTextArea.setText("");

		newButton = UIFactory.createButton(ClientUtil.getInstance().getLabel("button.new"), "image.btn.small.new", new NewL(), null);
		deleteButton = UIFactory.createButton(ClientUtil.getInstance().getLabel("button.delete"), "image.btn.small.delete", new DeleteL(), null);
		// This is a copy button as of PE 4.2.0.
		cloneButton = UIFactory.createButton(ClientUtil.getInstance().getLabel("button.copy"), "image.btn.small.copy", new CloneL(), null);

		editButton = UIFactory.createButton(ClientUtil.getInstance().getLabel("button.edit"), "image.btn.small.edit", new EditL(), null);
		viewButton = UIFactory.createButton(ClientUtil.getInstance().getLabel("button.view"), "image.btn.small.view", new ViewL(), null);
		editParamContextButton = UIFactory.createButton(ClientUtil.getInstance().getLabel("button.edit.param.context"), "image.btn.small.edit", new EditParamContextL(), null);

		backButton = UIFactory.createButton(ClientUtil.getInstance().getLabel("button.back"), "image.btn.small.back", new BackL(), null);
		saveButton = UIFactory.createButton(ClientUtil.getInstance().getLabel("button.save"), "image.btn.small.save", new SaveL(), null);

		appendRowButton = UIFactory.createButton("", "image.btn.grid.row.append", null, "button.tooltip.row.append", false);
		addRowButton = UIFactory.createButton("", "image.btn.grid.row.insert", null, "button.tooltip.row.add", false);
		deleteRowButton = UIFactory.createButton("", "image.btn.grid.row.delete", null, "button.tooltip.row.delete", false);
		cutButton = UIFactory.createButton("", "image.btn.grid.row.cut", null, "button.tooltip.row.cut", false);
		copyButton = UIFactory.createButton("", "image.btn.grid.row.copy", null, "button.tooltip.row.copy", false);
		pasteButton = UIFactory.createButton("", "image.btn.grid.row.paste", null, "button.tooltip.row.paste", false);

		actField = new DateSelectorComboField();
		expField = new DateSelectorComboField();

		statusField = UIFactory.createStatusComboBox(false);

		cardLayout = new CardLayout(0, 0);

		isDirty = false;
		saveButton.setEnabled(false);

		initPanel();
		clearFields();

		contextTable.getSelectionModel().addListSelectionListener(new ContextTableSelectionL());
		if (!readOnly) {
			contextTable.addMouseListener(new MouseAdapter() {

				public void mouseClicked(MouseEvent e) {
					if (e.getClickCount() == 2 && editButton.isEnabled()) {
						showGrid(true);
					}
				}
			});
		}

		EntityModelCacheFactory.getInstance().addEntityDeleteListener(this);
		statusField.addActionListener(statusComboL);
	}

	private void clearFields() {
		templateNameLabel.setText("");
		templateDescTextArea.setText("");
		setEnabled(false);
	}

	private void clearGrid() {
		gridTable.populate(null);
		contextPanel.clearContext();
		actField.setValue(null);
		expField.setValue(null);
		statusField.setSelectedIndex(0);
		isDirty = false;
	}

	public void discardChanges() {
		clearFields();
		gridTableModel.setDirty(false);
		setDirty(false);
		unlockIfLocked();
		showGridSelectionPanel();
		setEnabled(true);
		setEnabledSelectionAwareButtons(contextTable.getSelectedRow() >= 0);
	}

	public void entityDeleted(EntityDeleteEvent e) {
		if (selectionPanel != null && selectionPanel.isVisible()) {
			refreshContexts();
		}
		if (gridEditPanel.isVisible()) {
			contextPanel.removeContext(new GenericEntity[] { e.getEntity() });
			ParameterGrid grid = getGridInEdit();
			if (grid != null) {
				grid.removeGenericEntityID(e.getEntity().getType(), e.getEntity().getID());
			}
		}
	}

	// This is called by GuidelineListPanel.
	public ParameterContextTable getContextTable() {
		return contextTable;
	}

	private ParameterGrid getGridInEdit() {
		if (guidelineListPanel == null) { // if it came from parameter search
			return getSelectedGrid();
		}
		else {
			// if it came from policy (ie guideline) search.
			return guidelineListPanel.getTable().getSelectedDataObject().getParameterGrid();
		}
	}

	private ParameterGrid getSelectedGrid() {
		if (contextTable.getSelectedRow() < 0) return null;
		return (ParameterGrid) contextTable.getModel().getValueAt(contextTable.getSelectedRow(), -1);
	}

	private boolean hasProductionRestrictions() {
		return !ClientUtil.checkPermissionByPrivilegeName(PrivilegeConstants.PRIV_EDIT_PRODUCTION_DATA) && ClientUtil.isHighestStatus(statusField.getSelectedEnumValueValue());
	}

	public boolean hasUnsavedChanges() {
		return this.isDirty || gridTableModel.isDirty(); // || hasFieldsChanged();
	}

	private void initGridTable() {
		gridTable.setRowHeight(24);
		gridTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {

			public void valueChanged(ListSelectionEvent listselectionevent) {
				if (!listselectionevent.getValueIsAdjusting()) updateButtonStates();
			}

		});
		ToolTipManager.sharedInstance().unregisterComponent(gridTable);
		ToolTipManager.sharedInstance().unregisterComponent(gridTable.getTableHeader());
	}

	private void initPanel() {
		JPanel topTemplatePanel = new JPanel(new BorderLayout(2, 2));
		topTemplatePanel.add(templateNameLabel, BorderLayout.NORTH);
		topTemplatePanel.add(new JScrollPane(templateDescTextArea), BorderLayout.CENTER);
		topTemplatePanel.setBorder(UIFactory.createTitledBorder(ClientUtil.getInstance().getLabel("label.title.parameter.template.selected")));

		topPanel.add(topTemplatePanel);
		contextPanel.getJPanel().setBorder(UIFactory.createTitledBorder(ClientUtil.getInstance().getLabel("label.title.context.selected")));
		contextPanel.getJPanel().setVisible(false);

		JPanel buttonPanel = UIFactory.createJPanel(new FlowLayout(FlowLayout.LEFT));
		if (!readOnly) {
			buttonPanel.add(newButton);
			buttonPanel.add(cloneButton);
			buttonPanel.add(deleteButton);
			buttonPanel.add(new JSeparator());
			buttonPanel.add(editButton);
		}
		buttonPanel.add(viewButton);
		if (!readOnly) {
			buttonPanel.add(editParamContextButton);
		}

		JPanel parameterPanel = UIFactory.createJPanel(new BorderLayout(0, 0));
		parameterPanel.setBorder(UIFactory.createTitledBorder(ClientUtil.getInstance().getLabel("label.title.parameters")));

		parameterPanel.add(dateNameCheckbox, BorderLayout.NORTH);
		parameterPanel.add(new JScrollPane(contextTable), BorderLayout.CENTER);

		JPanel selectionPanel = UIFactory.createJPanel(new BorderLayout(0, 0));
		selectionPanel.add(buttonPanel, BorderLayout.NORTH);
		selectionPanel.add(parameterPanel, BorderLayout.CENTER);

		// create grid edit panel ///////////////////////////////////
		GridBagLayout bag = new GridBagLayout();

		JPanel gbPanel = UIFactory.createJPanel(new FlowLayout(FlowLayout.LEFT));
		gbPanel.add(backButton);
		gbPanel.add(saveButton);

		JPanel actPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
		actPanel.add(actField);
		actPanel.add(new JLabel(" to "));
		actPanel.add(expField);

		GridAdapter gridAdapter = new GridAdapter();
		JPanel gridButtonPanel = UIFactory.createJPanel();
		gridButtonPanel.setLayout(new BoxLayout(gridButtonPanel, 1));
		JButton ajbutton[] = { appendRowButton, addRowButton, deleteRowButton, cutButton, copyButton, pasteButton };
		for (int i = 0; i < ajbutton.length; i++) {
			if (ajbutton[i] != null) {
				gridButtonPanel.add(ajbutton[i]);
				ajbutton[i].addActionListener(gridAdapter);
			}
		}

		JPanel gridTablePanel = UIFactory.createJPanel(new BorderLayout(2, 2));
		JScrollPane gridScrollPane = new JScrollPane(gridTable);
		// TT 2132 Parameter - Missing row number column and can't copy/cut and paste grid row
		new RowHeaderTable(gridTable, gridScrollPane);
		gridTablePanel.add(gridScrollPane, BorderLayout.CENTER);
		gridTablePanel.add(gridButtonPanel, BorderLayout.EAST);

		gridEditPanel = UIFactory.createJPanel();
		gridEditPanel.setLayout(bag);
		gridEditPanel.setBorder(UIFactory.createTitledBorder(ClientUtil.getInstance().getLabel("label.title.parameters")));

		GridBagConstraints c = new GridBagConstraints();
		c.weighty = 0.0;
		c.gridheight = 1;
		c.anchor = GridBagConstraints.NORTH;
		c.fill = GridBagConstraints.BOTH;
		c.insets = new Insets(4, 4, 4, 4);

		c.gridwidth = GridBagConstraints.REMAINDER;
		c.weightx = 1.0;
		UIFactory.addComponent(gridEditPanel, bag, c, gbPanel);

		c.gridwidth = 1;
		c.weightx = 0.0;
		UIFactory.addComponent(gridEditPanel, bag, c, UIFactory.createFormLabel("label.activation"));

		c.gridwidth = GridBagConstraints.REMAINDER;
		c.weightx = 1.0;
		UIFactory.addComponent(gridEditPanel, bag, c, actPanel);

		c.gridwidth = 1;
		c.weightx = 0.0;
		JLabel label = UIFactory.createFormLabel("label.parameters");
		label.setVerticalAlignment(SwingConstants.TOP);
		UIFactory.addComponent(gridEditPanel, bag, c, label);

		c.gridheight = 1;
		c.weighty = 1.0;
		c.gridwidth = GridBagConstraints.REMAINDER;
		c.weightx = 1.0;
		UIFactory.addComponent(gridEditPanel, bag, c, gridTablePanel);

		c.weighty = 0.0;
		c.gridwidth = 1;
		c.weightx = 0.0;
		UIFactory.addComponent(gridEditPanel, bag, c, UIFactory.createFormLabel("label.status"));

		c.gridwidth = GridBagConstraints.REMAINDER;
		c.weightx = 1.0;
		UIFactory.addComponent(gridEditPanel, bag, c, statusField);

		centerPanel = UIFactory.createJPanel(cardLayout);
		centerPanel.add(selectionPanel, LAYOUT_KEY_SELECTION);
		centerPanel.add(gridEditPanel, LAYOUT_KEY_MANAGEMENT);

		setLayout(new BorderLayout(0, 0));
		add(topPanel, BorderLayout.NORTH);
		add(centerPanel, BorderLayout.CENTER);
	}

	private boolean isGridActive() {
		return actField.getValue() == null || actField.getValue().getDate() == null || actField.getValue().getDate().before(new Date());
	}

	private boolean isGridExpired() {
		return expField.getValue() != null && expField.getValue().getDate() != null && expField.getValue().getDate().before(new Date());
	}

	public void lockGrid(ParameterGrid grid) throws ServerException {
		ClientUtil.getCommunicator().lock(grid.getTemplateID(), PeDataType.PARAMETER_TEMPLATE);
	}

	private void lockSelectedGrid() throws ServerException {
		ClientUtil.getCommunicator().lock(getSelectedGrid().getTemplateID(), PeDataType.PARAMETER_TEMPLATE);
	}

	private void refreshContexts() {
		contextTableModel.clearDataList();
		if (template != null) {
			try {
				List<ParameterGrid> paramGridList = ClientUtil.getCommunicator().fetchParameters(template.getID());
				contextTableModel.setDataList(paramGridList);
			}
			catch (ServerException ex) {
				ClientUtil.handleRuntimeException(ex);
			}
		}
	}

	private boolean rowHasProductionRestrictions() {
		ParameterGrid grid = getSelectedGrid();
		return grid != null && !ClientUtil.checkPermissionByPrivilegeName(PrivilegeConstants.PRIV_EDIT_PRODUCTION_DATA) && ClientUtil.isHighestStatus(grid.getStatus());
	}

	public void saveChanges() throws CanceledException, ServerException {
		saveChanges_internal();
	}

	@SuppressWarnings("unchecked")
	private boolean saveChanges_internal() throws ServerException {
		ParameterGrid parameterGrid = getGridInEdit();
		if (parameterGrid == null) {
			return true;
		}

		if (hasProductionRestrictions() && !UtilBase.isSame(actField.getValue(), parameterGrid.getEffectiveDate()) && (actField.getValue() == null || actField.getValue().getDate().before(new Date()))) {
			ClientUtil.getInstance().showErrorDialog("msg.error.statuschange.activate", new Object[] { ClientUtil.getStatusDisplayLabel(statusField.getSelectedEnumValueValue()) });
			return false;
		}

		String errMessageKey = Validator.validateActivationDateRange(actField.getValue(), expField.getValue(), statusField.getSelectedEnumValueValue());
		if (errMessageKey != null) {
			ClientUtil.getInstance().showErrorDialog(errMessageKey, new Object[] { ClientUtil.getStatusDisplayLabel(statusField.getSelectedEnumValueValue()) });
			return false;
		}

		parameterGrid.setDataList(gridTableModel.getDataVector());
		updateGridFromFields();

		boolean saved = saveGrid();
		if (saved) {
			setDirty(false);
			gridTableModel.setDirty(false);
		}
		setGridEditable(true);
		return saved;
	}

	private boolean saveGrid() {
		if (guidelineListPanel == null) { // if it came from parameter search.
			try {
				ClientUtil.getCommunicator().save(getSelectedGrid(), false);
				return true;
			}
			catch (Exception exception) {
				ClientUtil.handleRuntimeException(exception);
				return false;
			}
		}
		else { // if it came from policy (ie guideline) search.
			try {
				ParameterGrid grid = guidelineListPanel.getTable().getSelectedDataObject().getParameterGrid();
				ClientUtil.getCommunicator().save(grid, false);
				return true;
			}
			catch (Exception exception) {
				ClientUtil.handleRuntimeException(exception);
				return false;
			}
		}
	}

	private void selectGrid(ParameterGrid grid) {
		for (int i = 0; i < contextTableModel.getRowCount(); i++) {
			ParameterGrid rowData = (ParameterGrid) contextTableModel.getValueAt(i, -1);
			if (rowData.getID() == grid.getID()) {
				contextTable.getSelectionModel().setSelectionInterval(i, i);
				return;
			}
		}


	}

	public void setDirty(boolean flag) {
		isDirty = flag;
	}

	public void setEnabled(boolean enabled) {
		newButton.setEnabled(enabled);
		setEnabledSelectionAwareButtons(false);
		setEnabledGridAwareButtons(false);
	}

	/**
	 * Sets the enabling/disabling of buttons on the detail page
	 * @param enabled
	 */
	private void setEnabledGridAwareButtons(boolean enabled) {
		saveButton.setEnabled(enabled);
		appendRowButton.setEnabled(enabled && !hasProductionRestrictions());
		addRowButton.setEnabled(enabled && !hasProductionRestrictions());
		deleteRowButton.setEnabled(enabled && gridTable.getSelectedRowCount() > 0 && !hasProductionRestrictions());
		copyButton.setEnabled(gridTable.getSelectedRowCount() > 0);
		cutButton.setEnabled(enabled && gridTable.getSelectedRowCount() > 0 && !hasProductionRestrictions());
		pasteButton.setEnabled(enabled && gridTable.getSelectedRowCount() > 0 && !hasProductionRestrictions());
		excelAdapter.setCutAndPasteEabled(enabled);
	}

	/**
	 * Sets the enabling/disabling or row-specific buttons
	 * @param enabled
	 */
	private void setEnabledSelectionAwareButtons(boolean enabled) {
		editButton.setEnabled(enabled);
		viewButton.setEnabled(enabled);
		deleteButton.setEnabled(enabled && !rowHasProductionRestrictions());
		cloneButton.setEnabled(enabled);
		editParamContextButton.setEnabled(enabled && !rowHasProductionRestrictions());
	}

	public void setGrid(ParameterGrid grid) {
		if (grid == null) {
			clearGrid();
		}
		else {
			gridTable.populate(grid.getDataObjects());
			contextPanel.setContextElemens(grid.extractGuidelineContext());
			actField.setValue(grid.getEffectiveDate());
			expField.setValue(grid.getExpirationDate());
			setSelectedStatus(grid.getStatus());
		}
	}

	public void setGridEditable(boolean editable) {
		setEnabledGridAwareButtons(editable);
		gridTable.setEnabled(editable && !hasProductionRestrictions());
		actField.setEnabled(editable && !hasProductionRestrictions());
		expField.setEnabled(editable && !(hasProductionRestrictions() && isGridExpired()));
		statusField.setEnabled(editable && !(hasProductionRestrictions() && isGridActive()));
		contextPanel.setEditContextEnabled(false);
	}

	private final void setSelectedStatus(String status) {
		statusField.removeActionListener(statusComboL);
		statusField.selectTypeEnumValue(status);
		statusField.addActionListener(statusComboL);
	}

	public void setSelectionPanel(ParameterTemplateSelectionPanel selectionPanel) {
		this.selectionPanel = selectionPanel;
	}

	public void setTemplate(ParameterTemplate template) throws ServerException {
		unlockIfLocked();

		this.template = template;
		if (template == null) {
			clearFields();
			gridTable.setTemplate(null);
			gridTableModel.setTemplate(null);
		}
		else {
			clearFields();
			templateNameLabel.setText(template.getName());
			templateDescTextArea.setText(template.getDescription());
			gridTable.setTemplate(template);
			gridTableModel.setTemplate(template);

			setEnabled(true);
		}
		gridTable.populate(null);

		refreshContexts();
	}

	private void showGrid(boolean enableEdit) {
		try {
			if (enableEdit) {
				lockSelectedGrid();
			}
			setGrid(getSelectedGrid());
			setGridEditable(enableEdit);
			showGridManagementPanel();
		}
		catch (ServerException ex) {
			ClientUtil.handleRuntimeException(ex);
		}
	}

	public void showGridManagementPanel() {
		if (selectionPanel != null) selectionPanel.setEnabled(false);
		if (pmTab != null) {
			pmTab.setDividerLocation(0);
		}
		contextPanel.getJPanel().setVisible(true);
		topPanel.add(contextPanel.getJPanel());
		((GridLayout) topPanel.getLayout()).setColumns(2);
		cardLayout.show(centerPanel, LAYOUT_KEY_MANAGEMENT);
	}

	public void showGridSelectionPanel() {
		if (selectionPanel != null) selectionPanel.setEnabled(true);
		if ((pmTab != null) && (guidelineListPanel == null)) {
			contextPanel.getJPanel().setVisible(false);
			topPanel.remove(contextPanel.getJPanel());
			((GridLayout) topPanel.getLayout()).setColumns(1);
			cardLayout.show(centerPanel, LAYOUT_KEY_SELECTION);
			pmTab.setDividerLocation(pmTab.getLastDividerLocation());
		}
		else if (guidelineListPanel != null) { // must have come from guideline part.
			guidelineListPanel.showGridSelectionPanel();
		}
	}

	private void unlockGrid(ParameterGrid grid) throws ServerException {
		ClientUtil.getCommunicator().unlock(grid.getTemplateID(), PeDataType.PARAMETER_TEMPLATE);
	}

	private void unlockIfLocked() {
		if (this.template != null && saveButton.isEnabled()) {
			try {
				ClientUtil.getCommunicator().unlock(template.getID(), PeDataType.PARAMETER_TEMPLATE);
			}
			catch (Exception ex) {
				ClientUtil.handleRuntimeException(ex);
			}
		}
	}

	private void unlockSelectedGrid() throws ServerException {
		ClientUtil.getCommunicator().unlock(getSelectedGrid().getTemplateID(), PeDataType.PARAMETER_TEMPLATE);
	}

	private void updateButtonStates() {
		boolean flag = gridTable.getSelectedRowCount() > 0;
		deleteRowButton.setEnabled(flag);
		copyButton.setEnabled(flag);
		cutButton.setEnabled(flag);
		pasteButton.setEnabled(flag);
		editParamContextButton.setEnabled(flag);
	}

	private void updateGridFromFields() {
		ParameterGrid grid = getGridInEdit();
		if (grid != null) {
			grid.setEffectiveDate(actField.getValue());
			grid.setExpirationDate(expField.getValue());
			grid.setStatus(statusField.getSelectedEnumValueValue());
		}
	}


}