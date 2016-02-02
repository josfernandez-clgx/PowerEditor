package com.mindbox.pe.client.applet.guidelines.search;

import java.awt.CardLayout;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.zip.GZIPInputStream;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSplitPane;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.apache.log4j.Logger;

import com.mindbox.pe.client.ClientUtil;
import com.mindbox.pe.client.EntityModelCacheFactory;
import com.mindbox.pe.client.applet.UIFactory;
import com.mindbox.pe.client.applet.guidelines.manage.ActivationEditorDialog;
import com.mindbox.pe.client.applet.guidelines.manage.GuidelineGridEditor;
import com.mindbox.pe.client.applet.guidelines.manage.TemplateSelectionPanel;
import com.mindbox.pe.client.applet.parameters.ParameterDetailPanel;
import com.mindbox.pe.client.applet.report.GuidelineReportSpecDialog;
import com.mindbox.pe.client.common.AbstractThreadedActionAdapter;
import com.mindbox.pe.client.common.PanelBase;
import com.mindbox.pe.client.common.TypeEnumValueComboBox;
import com.mindbox.pe.client.common.event.EntityDeleteEvent;
import com.mindbox.pe.client.common.event.EntityDeleteListener;
import com.mindbox.pe.client.common.tab.PowerEditorTabPanel;
import com.mindbox.pe.common.PrivilegeConstants;
import com.mindbox.pe.common.UtilBase;
import com.mindbox.pe.communication.GridDataResponse;
import com.mindbox.pe.communication.ServerException;
import com.mindbox.pe.model.AbstractGuidelineGrid;
import com.mindbox.pe.model.DateSynonym;
import com.mindbox.pe.model.GenericEntity;
import com.mindbox.pe.model.GridTemplate;
import com.mindbox.pe.model.GuidelineContext;
import com.mindbox.pe.model.GuidelineReportData;
import com.mindbox.pe.model.ParameterGrid;
import com.mindbox.pe.model.ParameterTemplate;
import com.mindbox.pe.model.ProductGrid;
import com.mindbox.pe.model.exceptions.CanceledException;
import com.mindbox.pe.model.report.GuidelineReportSpec;

/**
 * Panel that displays guideline search result.
 * @author Geneho Kim
 * @author MindBox
 */
public final class GuidelineListPanel extends PanelBase implements GuidelineGridEditor, EntityDeleteListener, PowerEditorTabPanel {

	private static final String LAYOUT_KEY_SELECTION = "GUIDELINESELECTION";
	private static final String LAYOUT_KEY_MANAGEMENT = "GRIDMANAGEMENT";
	private static final String LAYOUT_KEY_PARAMMANAGEMENT = "PARAMETERMANAGEMENT";

	private final class ShowDateNameL extends AbstractThreadedActionAdapter {

		public void performAction(ActionEvent e) {
			table.refresh(dateNameCheckbox.isSelected());
		}
	}

	/**
	 * Bulk status update.
	 * @author Inna Nill
	 * @author MindBox LLC
	 * 
	 * @since PowerEditor 4.2.0
	 */
	private final class ChangeStatusL extends AbstractThreadedActionAdapter {

		public void performAction(ActionEvent e) {
			TypeEnumValueComboBox combo = UIFactory.createStatusComboBox(false);
			int option = -1;
			do {
				option = JOptionPane.showConfirmDialog(
						ClientUtil.getApplet(),
						combo,
						"Select Status",
						JOptionPane.OK_CANCEL_OPTION,
						JOptionPane.PLAIN_MESSAGE);
			} while (option == JOptionPane.OK_OPTION && invalidProductionChange(combo.getSelectedEnumValueValue()));
			if (option == JOptionPane.OK_OPTION) {
				if (!ClientUtil.checkPermissionByPrivilegeName(PrivilegeConstants.PRIV_EDIT_PRODUCTION_DATA)
						&& ClientUtil.isHighestStatus(combo.getSelectedEnumValueValue())
						&& !ClientUtil.getInstance().showConfirmation(
								"msg.confirm.statuschange.hightest",
								new Object[] { ClientUtil.getHighestStatusDisplayLabel() })) {
					return;
				}
				try {
					List<GuidelineReportData> allData = getAllSelectedRowsDataList();
					ClientUtil.getCommunicator().bulkSaveGridData(allData, combo.getSelectedEnumValueValue(), null, null);
					// refresh list
					if (searchPanel != null) searchPanel.redoSearch();
				}
				catch (ServerException se) {
					ClientUtil.handleRuntimeException(se);
				}
			}

		}
	}

	/**
	 * Bulk activation date update.
	 * @author Inna Nill
	 * @author MindBox LLC
	 * 
	 * @since PowerEditor 4.2.0
	 */
	private final class ChangeActivationDateL extends AbstractThreadedActionAdapter {

		public void performAction(ActionEvent e) {
			try {
				editActivation();
			}
			catch (Exception ex) {
				ClientUtil.handleRuntimeException(ex);
			}
		}
	}

	private final class PrintL extends AbstractThreadedActionAdapter {

		public void performAction(ActionEvent e) {
			try {
				List<GuidelineReportData> selectedData = getAllSelectedRowsData();
				if (selectedData == null) return;

				GuidelineReportSpec reportSpec = GuidelineReportSpecDialog.newGuidelineReportSpec(JOptionPane.getFrameForComponent(ClientUtil.getApplet()));

				if (reportSpec != null) {
					// save report file
					byte[] ba = ClientUtil.getCommunicator().generatePolicySummaryReport(reportSpec, selectedData);

					File file = new File(reportSpec.getLocalFilename());
					PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(file, false)), true);
					BufferedReader reader = new BufferedReader(new InputStreamReader(new GZIPInputStream(new ByteArrayInputStream(ba))));
					for (String line = reader.readLine(); line != null; line = reader.readLine()) {
						writer.println(line);
					}
					writer.flush();
					writer.close();
					reader.close();

					// download style sheet
					String targetFile = file.getParent() + File.separatorChar + "pe_report_style.css";
					String sourceFile = "pe_report_style.css";
					try {
						ClientUtil.getInstance().downloadResourceFileFromServer(sourceFile, targetFile);
					}
					catch (IOException ex) {
						ex.printStackTrace();
					}

					String fileUrl = "file://" + file.getAbsolutePath();
					ClientUtil.getLogger().info("fileURL = " + fileUrl);
					ClientUtil.executeAsScript(fileUrl);
				}
			}
			catch (Exception ex) {
				ClientUtil.handleRuntimeException(ex);
			}
		}
	}

	private final class ViewL implements ActionListener {

		public void actionPerformed(ActionEvent e) {
			showGridPanel(true);
		}
	}

	private final class EditL implements ActionListener {

		public void actionPerformed(ActionEvent e) {
			showGridPanel(false); // false = not view only.
		}
	}

	private final class MouseL extends MouseAdapter {

		public void mouseClicked(MouseEvent e) {
			if (e.getClickCount() == 2) {
				//even when edit is disabled, double clicking should take us to view screen
				showGridPanel(readOnly || !editButton.isEnabled());
			}
		}
	}

	private class TableSelectionL implements ListSelectionListener {

		public void valueChanged(ListSelectionEvent arg0) {
			int[] rows = table.getSelectedRows();
			boolean atLeastOneSelection = (rows != null && rows.length > 0);
			boolean moreThanOneSelection = (rows != null && rows.length > 1);
			boolean productionRestriction = false;
			boolean oneSelectedNotEditable = false;
			if (atLeastOneSelection) {
				for (Iterator<GuidelineReportData> i = getAllSelectedRowsDataList().iterator(); i.hasNext();) {
					GuidelineReportData data = i.next();
					if (!data.isEditable()) {
						oneSelectedNotEditable = true;
					}
					if (!ClientUtil.checkPermissionByPrivilegeName(PrivilegeConstants.PRIV_EDIT_PRODUCTION_DATA)
							&& ClientUtil.isHighestStatus(data.getStatus())
							&& (data.getActivationDate() == null || data.getActivationDate().getDate().before(new Date()))) {
						productionRestriction = true;
						break;
					}
				}
			}

			printButton.setEnabled(atLeastOneSelection);
			changeDatesButton.setEnabled(atLeastOneSelection && !productionRestriction && !oneSelectedNotEditable);
			changeStatusButton.setEnabled(atLeastOneSelection && !productionRestriction && !oneSelectedNotEditable);
			removeSelectedButton.setEnabled(atLeastOneSelection);

			GuidelineReportData data = getSelectedRowData();
			if (data != null && !moreThanOneSelection) {
				editButton.setEnabled(data.isEditable());
				viewButton.setEnabled(true);
				changeDatesButton.setEnabled(data.isEditable());
				changeStatusButton.setEnabled(data.isEditable());
			}
			else {
				editButton.setEnabled(false);
				viewButton.setEnabled(false);
			}
		}
	}

	private final JButton viewButton, editButton;
	private final JButton printButton, changeStatusButton, changeDatesButton;
	private final JButton selectAllButton, clearSelectionButton;
	private final JButton removeSelectedButton;
	private final TemplateReportTable table;
	private final GuidelineEditPanel gridEditPanel;
	private final ParameterDetailPanel parameterPanel;
	private final Logger logger = Logger.getLogger(getClass());
	private final JLabel contextLabel;
	private GuidelineSearchPanel searchPanel = null;
	private final JPanel resultPanel;
	private final SearchGuidelinesTab sgTab;
	private final JCheckBox dateNameCheckbox;
	private boolean readOnly;

	public GuidelineListPanel(SearchGuidelinesTab sgTab, boolean readOnly) {
		super();
		this.readOnly = readOnly;
		this.sgTab = sgTab;
		table = new TemplateReportTable(new TemplateReportTableModel());

		this.viewButton = UIFactory.createButton(
				ClientUtil.getInstance().getLabel("button.view"),
				null,
				new ViewL(),
				"button.tooltip.view.guideline");
		viewButton.setEnabled(false);
		this.editButton = UIFactory.createButton(
				ClientUtil.getInstance().getLabel("button.edit"),
				null,
				new EditL(),
				"button.tooltip.edit.guideline");
		editButton.setEnabled(false);

		this.printButton = UIFactory.createButton(
				ClientUtil.getInstance().getLabel("button.report"),
				null,
				new PrintL(),
				"button.tooltip.report");
		printButton.setEnabled(false);
		this.changeStatusButton = UIFactory.createButton(
				ClientUtil.getInstance().getLabel("button.change.status"),
				null,
				new ChangeStatusL(),
				"button.tooltip.change.status");
		changeStatusButton.setEnabled(false);
		this.changeDatesButton = UIFactory.createButton(
				ClientUtil.getInstance().getLabel("button.change.activation.date"),
				null,
				new ChangeActivationDateL(),
				"button.tooltip.change.activation.date");
		changeDatesButton.setEnabled(false);

		dateNameCheckbox = UIFactory.createCheckBox("checkbox.show.date.name");
		dateNameCheckbox.setSelected(true);
		dateNameCheckbox.addActionListener(new ShowDateNameL());

		this.gridEditPanel = new GuidelineEditPanel(readOnly);
		this.gridEditPanel.setGridEditor(this);

		this.contextLabel = new JLabel("");
		this.contextLabel.setVerticalAlignment(SwingConstants.TOP);

		selectAllButton = UIFactory.createButton(ClientUtil.getInstance().getLabel("button.select.all"), null, new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				table.selectAll();
			}
		}, null);

		clearSelectionButton = UIFactory.createButton(ClientUtil.getInstance().getLabel("button.select.none"), null, new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				table.clearSelection();
			}
		}, null);

		removeSelectedButton = UIFactory.createButton(
				ClientUtil.getInstance().getLabel("button.remove.selection"),
				null,
				new ActionListener() {

					public void actionPerformed(ActionEvent e) {
						removeSelectedButton.setEnabled(false);
						removeSelectionFromList();
					}
				},
				null);
		removeSelectedButton.setEnabled(false);

		JPanel btnPanel = UIFactory.createJPanel(new FlowLayout(FlowLayout.LEFT, 2, 2));
		btnPanel.add(viewButton);
		if (!readOnly) {
			btnPanel.add(editButton);
			btnPanel.add(new JSeparator());
			btnPanel.add(changeDatesButton);
			btnPanel.add(changeStatusButton);
		}
		btnPanel.add(new JSeparator());
		btnPanel.add(printButton);

		JPanel btnPanel2 = UIFactory.createJPanel(new FlowLayout(FlowLayout.LEFT, 2, 2));
		btnPanel2.add(selectAllButton);
		btnPanel2.add(clearSelectionButton);
		btnPanel2.add(removeSelectedButton);

		setBorder(UIFactory.createTitledBorder(ClientUtil.getInstance().getLabel("label.title.guideline.search.result")));

		GridBagLayout bag = new GridBagLayout();
		resultPanel = UIFactory.createJPanel(bag);
		GridBagConstraints c = new GridBagConstraints();
		c.anchor = GridBagConstraints.NORTHWEST;
		c.fill = GridBagConstraints.BOTH;
		c.weighty = 0.0;
		c.gridheight = 1;

		c.weightx = 1.0;
		c.gridwidth = GridBagConstraints.REMAINDER;

		addComponent(resultPanel, bag, c, btnPanel);
		addComponent(resultPanel, bag, c, btnPanel2);
		addComponent(resultPanel, bag, c, dateNameCheckbox);
		c.weighty = 1.0;
		c.gridheight = GridBagConstraints.REMAINDER;
		addComponent(resultPanel, bag, c, new JScrollPane(table));

		JSplitPane resultSplitPane = UIFactory.createSplitPane(JSplitPane.VERTICAL_SPLIT, contextLabel, resultPanel);
		resultSplitPane.setDividerLocation(14);

		// Parameter Detail Panel
		this.parameterPanel = new ParameterDetailPanel(this, readOnly);

		setLayout(new CardLayout(0, 0));
		add(resultSplitPane, LAYOUT_KEY_SELECTION);
		add(gridEditPanel, LAYOUT_KEY_MANAGEMENT);
		add(parameterPanel, LAYOUT_KEY_PARAMMANAGEMENT);

		table.addMouseListener(new MouseL());
		table.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		table.getSelectionModel().addListSelectionListener(new TableSelectionL());
		EntityModelCacheFactory.getInstance().addEntityDeleteListener(this);
	}

	private boolean invalidProductionChange(String status) {
		if (ClientUtil.isHighestStatus(status)) {
			List<GuidelineReportData> allData = getAllSelectedRowsDataList();
			for (Iterator<GuidelineReportData> i = allData.iterator(); i.hasNext();) {
				GuidelineReportData data = i.next();
				if (!data.getStatus().equals(status)
						&& (data.getActivationDate() == null || data.getActivationDate().getDate().before(new Date()))) {
					ClientUtil.getInstance().showErrorDialog(
							"msg.error.statuschange.activate",
							new Object[] { ClientUtil.getHighestStatusDisplayLabel() });
					return true;
				}
			}
		}
		return false;
	}

	public final void resetContext(GuidelineContext[] newContext) {
		ClientUtil.getLogger().info("NOT SUPPORTED HERE");
	}

	public boolean hasUnsavedChanges() {
		return gridEditPanel.hasUnsavedChanges();
	}

	private GuidelineReportData getSelectedRowData() {
		if (table.getSelectedRow() >= 0) {
			return table.getSelectedDataObject();
		}
		else {
			return null;
		}
	}

	public TemplateReportTable getTable() {
		return table;
	}

	private void removeSelectionFromList() {
		List<GuidelineReportData> selection = getAllSelectedRowsData();
		table.getSelectionTableModel().removeDataList(selection);
	}

	/**
	 * Return all the data from selected rows, for bulk updates.
	 * @since PowerEditor 4.2.0
	 * 
	 * @return GuidelineReportData[]
	 */
	private List<GuidelineReportData> getAllSelectedRowsData() {
		return table.getSelectedDataObjects();
	}

	/**
	 * Return a list of selected data.
	 * @since PowerEditor 4.2.0
	 * 
	 * @return A List of selected data, for bulk updates.
	 */
	private List<GuidelineReportData> getAllSelectedRowsDataList() {
		int[] rows = table.getSelectedRows();
		List<GuidelineReportData> allData = new ArrayList<GuidelineReportData>();
		for (int idx = 0; idx < rows.length; idx++) {
			GuidelineReportData data = table.getSelectedData(rows[idx]);
			allData.add(data);
		}
		return allData;
	}

	public void saveChanges() throws CanceledException, ServerException {
		gridEditPanel.saveChanges();
	}

	public void discardChanges() {
		gridEditPanel.discardChanges();
	}

	void setSearchPanel(GuidelineSearchPanel searchPanel) {
		this.searchPanel = searchPanel;
	}

	void setResult(String context, List<?> dataList) {
		// previous selection
		GuidelineReportData prevSelection = null;
		if (table.getSelectedRow() >= 0) {
			prevSelection = table.getSelectedDataObject();
		}

		contextLabel.setText(context);
		List<GuidelineReportData> filteredList = new ArrayList<GuidelineReportData>();
		if (dataList != null && !dataList.isEmpty()) {
			for (Iterator<?> iter = dataList.iterator(); iter.hasNext();) {
				Object obj = iter.next();
				if (obj instanceof GuidelineReportData) {
					GuidelineReportData element = (GuidelineReportData) obj;
					if (element.getDataType().equals("GuidelineGrid")) {
						// need to ensure we have either view or edit guideline privilege
						if (ClientUtil.checkViewOrEditGuidelinePermissionOnUsageType(element.getUsageType())) {
							filteredList.add(element);
						}
					}
					else { // Must be a parameter grid element.
						filteredList.add(element);
					}
				}
				else {
					ClientUtil.getLogger().warn("NOT GuidelineReportData! " + obj.getClass());
				}
			}
		}
		table.setDataList(filteredList);

		if (prevSelection != null) {
			// select it, if found in the new list
			table.selectIfFound(prevSelection);
		}
	}

	private AbstractGuidelineGrid findActivation(List<ProductGrid> gridList, DateSynonym actDate, DateSynonym expDate) {
		for (Iterator<ProductGrid> iter = gridList.iterator(); iter.hasNext();) {
			AbstractGuidelineGrid element = iter.next();
			if (UtilBase.isSame(actDate, element.getEffectiveDate()) && UtilBase.isSame(expDate, element.getExpirationDate())) {
				return element;
			}
		}
		return null;
	}

	/**
	 * Used for bulk date updates.
	 * @since PowerEditor 4.2.0
	 * 
	 * @throws ServerException
	 */
	synchronized void editActivation() throws ServerException {
		List<GuidelineReportData> allSelectedData = getAllSelectedRowsDataList();
		if ((new ActivationEditorDialog(this)).edit(allSelectedData)) {
			try {
				this.setResult(contextLabel.getText(), ClientUtil.getCommunicator().search(searchPanel.getFilter()));
			}
			catch (Exception ex) {
				ClientUtil.handleRuntimeException(ex);
			}
		}
	}

	private void showGridPanel(boolean viewOnly) {
		int selectedRow = table.getSelectedRow();
		if (selectedRow < 0) {
			ClientUtil.getInstance().showWarning("msg.warning.select.guideline");
			return;
		}

		ClientUtil.getParent().setCursor(UIFactory.getWaitCursor());

		GuidelineReportData data = table.getSelectedDataObject();
		try {
			if ((data.getContext() != null) && (data.getDataType().equals("GuidelineGrid"))) { // If it's a guideline grid.
				if (!viewOnly) {
					ClientUtil.getCommunicator().lockGrid(data.getID(), data.getContext());
				}

				// gridEditPanel is an instance of class that displays guideline grid.
				gridEditPanel.setViewOnly(viewOnly);

				GridDataResponse response = ClientUtil.getCommunicator().fetchGridData(data.getID(), data.getContext());

				populate(data.getContext(), response.getResultList(), response.getTemplate(), false, null);

				// set activation
				gridEditPanel.showActivation(data.getContext(), findActivation(
						response.getResultList(),
						data.getActivationDate(),
						data.getExpirationDate()));

				if (!viewOnly) {
					gridEditPanel.setEnabled(true);
				}

				// show grid edit panel
				showGridManagementPanel();
			}
			else { // It must be a parameter.
				ParameterGrid parameterGridData = data.getParameterGrid();
				// This code and for loop are here to make sure that the grid data is updated before going to the
				// grid management screen. If this is not here then bulk updates to status and date may not be
				// updated on that screen (until another search is done). Also, updates from the Parameters screen
				// would not appear here...
				List<ParameterGrid> serverParamGrids = ClientUtil.getCommunicator().fetchParameters(data.getParameterGrid().getTemplateID());
				ParameterGrid temp = null;
				for (Iterator<ParameterGrid> iter = serverParamGrids.iterator(); iter.hasNext();) {
					temp = iter.next();
					if (temp.getID() == data.getParameterGrid().getID()) {
						data.setParameterGrid(temp);
						parameterGridData = temp;
						break;
					}
				}

				try {
					if (!viewOnly) {
						parameterPanel.setEnabled(true);
						parameterPanel.lockGrid(parameterGridData);
					}
					parameterPanel.setTemplate((ParameterTemplate) parameterGridData.getTemplate());
					parameterPanel.setGrid(parameterGridData);
					parameterPanel.setGridEditable(!viewOnly);
				}
				catch (Exception ex) {
					logger.error("Failed to check rule existence for columns in " + parameterGridData.getTemplate(), ex);
					ClientUtil.handleRuntimeException(ex);
				}

				showParameterManagementPanel();
			}
		}
		catch (Exception ex) {
			ClientUtil.handleRuntimeException(ex);
		}
		finally {
			ClientUtil.getParent().setCursor(UIFactory.getDefaultCursor());
		}
	}

	/**
	 * Do not call this method on instances of this class.
	 */
	public TemplateSelectionPanel getTemplatePanel() {
		throw new IllegalStateException("NOT IMPLEMENTED");
	}

	public void populate(GuidelineContext[] contexts, List<ProductGrid> list, GridTemplate gridTemplate, boolean isSubcontext,
			AbstractGuidelineGrid currentGrid) {
		gridEditPanel.populate(contexts, list, gridTemplate, isSubcontext, currentGrid);
	}

	public void setViewOnly(boolean isForEdit) {
		gridEditPanel.setViewOnly(!isForEdit);
	}

	// Shows the grid management card.
	public synchronized void showGridManagementPanel() {
		searchPanel.setEnabled(false);
		sgTab.setDividerLocation(0);
		((CardLayout) getLayout()).show(GuidelineListPanel.this, LAYOUT_KEY_MANAGEMENT);
	}

	// Shows the parameter grid card.
	public synchronized void showParameterManagementPanel() {
		searchPanel.setEnabled(false);
		sgTab.setDividerLocation(0);
		((CardLayout) getLayout()).show(GuidelineListPanel.this, LAYOUT_KEY_PARAMMANAGEMENT); // unique key.
		parameterPanel.showGridManagementPanel();
	}

	public synchronized void showGridSelectionPanel() {
		searchPanel.setEnabled(true);
		sgTab.setDividerLocation(sgTab.getLastDividerLocation());

		CardLayout cl = (CardLayout) getLayout();
		// redo search to refresh the search result screen
		if (searchPanel != null) searchPanel.redoSearch();
		cl.show(this, LAYOUT_KEY_SELECTION);

	}

	public synchronized void save(GridDataResponse response) {
		try {
			ClientUtil.getCommunicator().save(response.getTemplate(), true);
		}
		catch (Exception ex) {
			ClientUtil.handleRuntimeException(ex);
		}
	}

	public void entityDeleted(EntityDeleteEvent e) {
		if (resultPanel.isVisible()) {
			if (searchPanel != null) searchPanel.redoSearch();
		}
		if (gridEditPanel.isVisible()) {
			gridEditPanel.getContextHolder().removeContext(new GenericEntity[] { e.getEntity() });
		}
		if (parameterPanel.isVisible()) {
			parameterPanel.entityDeleted(e);
		}
	}
}