package com.mindbox.pe.client.applet.guidelines.manage;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListCellRenderer;
import javax.swing.SwingConstants;
import javax.swing.Timer;
import javax.swing.border.Border;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

import org.apache.log4j.Logger;

import com.mindbox.pe.client.ClientUtil;
import com.mindbox.pe.client.applet.PowerEditorSwingTheme;
import com.mindbox.pe.client.applet.UIFactory;
import com.mindbox.pe.client.applet.template.rule.CellValueChangeListener;
import com.mindbox.pe.client.applet.validate.Validator;
import com.mindbox.pe.client.common.AbstractThreadedActionAdapter;
import com.mindbox.pe.client.common.RefreshableComboBoxModel;
import com.mindbox.pe.client.common.TypeEnumValueComboBox;
import com.mindbox.pe.client.common.context.GuidelineContextPanel;
import com.mindbox.pe.client.common.event.ContextChangeEvent;
import com.mindbox.pe.client.common.event.ContextChangeListener;
import com.mindbox.pe.client.common.grid.GridCardsPanel;
import com.mindbox.pe.client.common.grid.GridTablePanel;
import com.mindbox.pe.client.common.grid.GridValidator;
import com.mindbox.pe.client.common.grid.IGridDataCard;
import com.mindbox.pe.client.common.tab.PowerEditorTabPanel;
import com.mindbox.pe.common.GuidelineContextHolder;
import com.mindbox.pe.common.PrivilegeConstants;
import com.mindbox.pe.common.UtilBase;
import com.mindbox.pe.communication.GridDataResponse;
import com.mindbox.pe.communication.ServerException;
import com.mindbox.pe.model.Constants;
import com.mindbox.pe.model.DateSynonym;
import com.mindbox.pe.model.GuidelineContext;
import com.mindbox.pe.model.comparator.ActivationsComparator;
import com.mindbox.pe.model.exceptions.CanceledException;
import com.mindbox.pe.model.exceptions.InvalidDataException;
import com.mindbox.pe.model.grid.AbstractGuidelineGrid;
import com.mindbox.pe.model.grid.ProductGrid;
import com.mindbox.pe.model.template.GridTemplate;

/**
 * Abstract Grid panel.
 * Note: this only works with instances of {@link ProductGrid}.
 * @since PowerEditor 1.0
 */
public abstract class AbstractGridPanel extends JPanel implements PowerEditorTabPanel, CellValueChangeListener {

	private final class ActivationRenderer extends JLabel implements ListCellRenderer<Object> {
		/**
		 * 
		 */
		private static final long serialVersionUID = -3951228734910107454L;

		private final Border redBorder;

		private final Border emptyBorder;

		public ActivationRenderer() {
			redBorder = BorderFactory.createLineBorder(Color.red, 2);
			emptyBorder = BorderFactory.createEmptyBorder(2, 2, 2, 2);
		}

		@Override
		public Component getListCellRendererComponent(JList<? extends Object> jlist, Object obj, int i, boolean flag, boolean flag1) {
			if (obj == null) {
				setText(ClientUtil.getInstance().getLabel("label.none"));
				return this;
			}
			ProductGrid abstractgrid = (ProductGrid) obj;
			setText(getActivationString(abstractgrid));
			if (flag) {
				setBorder(redBorder);
			}
			else {
				setBorder(emptyBorder);
			}
			return this;
		}
	}

	private class ComboL implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent event) {
			selectActivation();
		}
	}

	private class ContextChangeL implements ContextChangeListener {

		@Override
		public void contextChanged(ContextChangeEvent e) {
			setDirty(true);
			contextMayHaveChanged = true;
		}

	}

	private class GridAdapter extends AbstractThreadedActionAdapter {

		@Override
		public void performAction(ActionEvent actionevent) {
			try {
				Object obj = actionevent.getSource();
				if (obj == backButton) {
					if (continueAfterConditionalSave()) {
						displayGuidelinesSelectionPanel();
					}
				}
				else if (obj == saveGridButton) {
					saveGridButton.setEnabled(false);
					save();
				}
				else if (obj == validateAllMenuItem) {
					validateGridData();
				}
				else if (obj == validateCurrentMenuItem) {
					validateCurrentGrid(false);
				}
				else if (obj == addActivationBtn) {
					if (continueAfterConditionalSave()) {
						addActivation();
					}
				}
				else if (obj == cloneActivationBtn) {
					if (continueAfterConditionalSave()) {
						cloneActivation();
					}
				}
				else if (obj == removeActivationBtn) {
					removeActivation();
				}
				else if (obj == editActivationBtn) {
					editActivation();
				}
				else {
					ClientUtil.printWarning("Unknown Action Event= " + actionevent);
				}
			}
			catch (Exception ex) {
				ClientUtil.handleRuntimeException(ex);
			}
		}
	}

	private class GridSelectionL implements ListSelectionListener {

		@Override
		public void valueChanged(ListSelectionEvent arg0) {
			try {
				ruleViewPanel.showRow(gridTablePanel.getSelectedRow());
			}
			catch (InvalidDataException e) {
				e.printStackTrace();
			}
		}
	}

	private class GridTableModelL implements TableModelListener {

		@Override
		public void tableChanged(TableModelEvent event) {
			if (event == null || event.getFirstRow() == TableModelEvent.HEADER_ROW) return;
			switch (event.getType()) {
			case TableModelEvent.UPDATE: {
				int row = event.getFirstRow();
				if (row >= 0) {
					try {
						ruleViewPanel.refresh(row + 1);
					}
					catch (InvalidDataException e) {
						e.printStackTrace();
					}
				}
				break;
			}
			case TableModelEvent.INSERT: {
				try {
					ruleViewPanel.rowsAdded();
				}
				catch (InvalidDataException e) {
					e.printStackTrace();
				}
				break;
			}
			case TableModelEvent.DELETE: {
				int numRowsDeleted = event.getLastRow() - event.getFirstRow() + 1;
				try {
					ruleViewPanel.rowsDeleted(numRowsDeleted);
				}
				catch (InvalidDataException e) {
					e.printStackTrace();
				}
				break;
			}
			}
		}

	}

	private final class ShowDateNameL extends AbstractThreadedActionAdapter {

		@Override
		public void performAction(ActionEvent e) {
			((RefreshableComboBoxModel<ProductGrid>) activationsCombo.getModel()).refresh();
		}
	}

	private class StatusComboL implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent event) {
			boolean dirty = isDirty;
			String newStatus = statusField.getSelectedEnumValueValue();
			if (ClientUtil.hasProductionRestrictions(newStatus)) {
				if (isGridActive()) {
					ClientUtil.getInstance().showErrorDialog("msg.error.statuschange.activate", new Object[] { ClientUtil.getHighestStatusDisplayLabel() });
					setSelectedStatus(currentGrid.getStatus());
					setDirty(dirty);
				}
				else if (!ClientUtil.getInstance().showConfirmation("msg.confirm.statuschange.hightest", new Object[] { ClientUtil.getHighestStatusDisplayLabel() })) {
					setSelectedStatus(currentGrid.getStatus());
					setDirty(dirty);
				}
			}
		}
	}

	private static final long serialVersionUID = -3951228734910107454L;
	private static final ActivationsComparator ACTIVATION_COMPARATOR = ActivationsComparator.getInstance();
	public static final SimpleDateFormat ACTIVATION_DATE_FORMAT = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss a z");

	private static final void addComponent(JPanel panel, GridBagLayout bag, GridBagConstraints c, Component component) {
		bag.setConstraints(component, c);
		panel.add(component);
	}

	private static ProductGrid createGrid(GridTemplate template, GuidelineContext[] context, DateSynonym effDate, DateSynonym expDate) {
		ProductGrid gridData = new ProductGrid(-1, template, effDate, expDate);
		setContext(gridData, context);
		return gridData;
	}

	private static void setContext(ProductGrid productGrid, GuidelineContext[] context) {
		productGrid.clearAllContext();
		if (context != null) {
			for (int i = 0; i < context.length; i++) {
				if (context[i].getGenericEntityType() != null) {
					productGrid.setGenericEntityIDs(context[i].getGenericEntityType(), context[i].getIDs());
				}
				else if (context[i].hasCategoryContext()) {
					productGrid.setGenericCategoryIDs(ClientUtil.getEntityConfigHelper().findEntityTypeForCategoryType(context[i].getGenericCategoryType()), context[i].getIDs());
				}
			}
		}
	}

	protected int[] productIDs;
	protected GridTemplate template;
	protected final List<ProductGrid> gridList;
	protected final List<ProductGrid> removedGrids;
	protected GuidelineGridEditor gridEditor;
	protected AbstractGuidelineGrid currentGrid;
	protected AbstractGuidelineGrid lastSelectedGrid;
	protected JComboBox<ProductGrid> activationsCombo;
	protected JLabel activationLabelLabel;
	protected JPanel activationPanel;
	protected final GuidelineContextPanel contextHolder;
	protected final Logger logger = Logger.getLogger(getClass());
	private final GridSelectionL gridSelectionListener;
	private final GridTableModelL gridTableModelListener;
	private boolean contextMayHaveChanged = false;
	private JLabel templateNameLabel;
	private JTextArea templateDescLabel;
	private JMenuItem validateAllMenuItem;
	private JMenuItem validateCurrentMenuItem;
	private JPanel buttonPanel;
	private GridCardsPanel gridCardPanel;
	private GridTablePanel gridTablePanel;
	private JButton saveGridButton;
	private JButton backButton;
	private JButton editActivationBtn;
	private JButton addActivationBtn;
	private JButton cloneActivationBtn;
	private JButton removeActivationBtn;
	private TypeEnumValueComboBox statusField;
	private JTextArea commentsField;
	private JTextField lastStatusChangeField;
	private GridAdapter buttonListener;
	private JPanel topPanel = null;
	private boolean autoSaveOn;
	private Timer updateStateTimer;
	private boolean viewOnly;
	private boolean isDirty;
	private final ComboL activationsComboL = new ComboL();
	private final StatusComboL statusComboL = new StatusComboL();
	private final JSplitPane guidelineSplitPane;
	private JButton ruleViewButton, editTemplateButton;
	private RuleViewPanel ruleViewPanel;
	private GuidelineContext[] currentContext;
	private final JCheckBox dateNameCheckbox;
	private JButton ruleIDToggleButton;
	private final boolean readOnly;

	public AbstractGridPanel(boolean readOnly) {
		this.readOnly = readOnly;
		this.guidelineSplitPane = UIFactory.createSplitPane(JSplitPane.VERTICAL_SPLIT);
		ruleViewPanel = new RuleViewPanel(this);
		ruleViewPanel.setBorder(UIFactory.createTitledBorder("Rule"));
		ruleViewPanel.setVisible(false);

		templateNameLabel = new JLabel();
		templateDescLabel = new JTextArea();
		saveGridButton = null;
		backButton = null;
		buttonPanel = null;
		gridCardPanel = null;
		gridTablePanel = null;
		activationPanel = null;
		editActivationBtn = null;
		addActivationBtn = null;
		cloneActivationBtn = null;
		removeActivationBtn = null;
		activationsCombo = UIFactory.createComboBox();
		activationsCombo.setModel(new RefreshableComboBoxModel<ProductGrid>());
		statusField = UIFactory.createStatusComboBox(false);
		commentsField = new JTextArea();
		lastStatusChangeField = new JTextField(10);
		buttonListener = new GridAdapter();
		productIDs = null;
		template = null;
		gridList = new ArrayList<ProductGrid>();
		removedGrids = new ArrayList<ProductGrid>();
		lastSelectedGrid = null;
		autoSaveOn = true;
		viewOnly = false;
		isDirty = false;
		contextHolder = new GuidelineContextPanel("button.edit.context", true);

		dateNameCheckbox = UIFactory.createCheckBox("checkbox.show.date.name");
		dateNameCheckbox.setSelected(true);
		dateNameCheckbox.addActionListener(new ShowDateNameL());

		initComponents();
		addComponents();

		setEnabled(false);
		initTimer();

		gridSelectionListener = new GridSelectionL();
		gridTablePanel.addGridRowSelectionListener(gridSelectionListener);
		gridTableModelListener = new GridTableModelL();
		gridTablePanel.addGridTableModelListener(gridTableModelListener);

		contextHolder.addContextChangeListener(new ContextChangeL());
	}

	private void addActivation() throws ServerException {
		ProductGrid abstractgrid = createGrid(template, currentContext, null, null);
		initializeNewActivationDates(abstractgrid);

		if ((new ActivationEditorDialog(this)).edit(abstractgrid, ActivationEditorDialog.Operation.ADD)) {
			gridList.add(abstractgrid);
			Collections.sort(gridList, ACTIVATION_COMPARATOR);
			setDirty(true);
			populate(gridList, gridList.indexOf(abstractgrid));
			setActivation(abstractgrid);
			selectActivation();
			gridTablePanel.addRow(0);
		}
	}

	private void addComponents() {
		UIFactory.setLookAndFeel(this);

		JPanel detailPanel = UIFactory.createJPanel(new BorderLayout(2, 2));
		detailPanel.add(templateNameLabel, BorderLayout.NORTH);
		detailPanel.add(new JScrollPane(templateDescLabel), BorderLayout.CENTER);
		detailPanel.setBorder(UIFactory.createTitledBorder("Selected Guideline Template"));

		JPanel contextHolderPanel = contextHolder.getJPanel();
		contextHolderPanel.setBorder(UIFactory.createTitledBorder("Selected Guideline Context"));
		contextHolderPanel.setPreferredSize(new Dimension(560, 170));
		JPanel panel = UIFactory.createJPanel(new BorderLayout(1, 1));
		panel.add(detailPanel, BorderLayout.CENTER);
		panel.add(contextHolderPanel, BorderLayout.EAST);

		topPanel = UIFactory.createJPanel(new BorderLayout(1, 1));
		topPanel.add(panel, BorderLayout.CENTER);

		JPanel northPanel = UIFactory.createJPanel(new BorderLayout(0, 0));
		northPanel.add(topPanel, BorderLayout.CENTER);
		northPanel.add(buttonPanel, BorderLayout.SOUTH);

		GridBagConstraints c = new GridBagConstraints();
		c.anchor = GridBagConstraints.NORTH;
		c.insets = new Insets(2, 2, 2, 2);
		c.fill = GridBagConstraints.BOTH;
		c.weighty = 0.0;
		c.gridheight = 1;

		c.gridwidth = GridBagConstraints.REMAINDER;
		c.weightx = 1.0;

		GridBagLayout bag = new GridBagLayout();

		JPanel bottomPanel = UIFactory.createJPanel(bag);

		c.gridwidth = 1;
		c.weightx = 0.0;
		addComponent(bottomPanel, bag, c, UIFactory.createFormLabel("label.activations"));

		JPanel actPanel = UIFactory.createJPanel(new BorderLayout(0, 0));
		actPanel.add(activationPanel, BorderLayout.CENTER);

		c.gridwidth = GridBagConstraints.REMAINDER;
		c.weightx = 1.0;
		addComponent(bottomPanel, bag, c, actPanel);

		c.gridwidth = 1;
		c.weightx = 0.0;
		JLabel label = UIFactory.createFormLabel("label.guideline");
		label.setVerticalAlignment(SwingConstants.TOP);
		addComponent(bottomPanel, bag, c, label);

		guidelineSplitPane.setTopComponent(ruleViewPanel);
		guidelineSplitPane.setBottomComponent(gridCardPanel);

		c.gridwidth = GridBagConstraints.REMAINDER;
		c.weightx = 1.0;
		c.weighty = 0.5;
		addComponent(bottomPanel, bag, c, guidelineSplitPane);

		c.weighty = 0.0;
		c.gridwidth = 1;
		c.weightx = 0.0;
		addComponent(bottomPanel, bag, c, UIFactory.createFormLabel("label.status"));

		c.gridwidth = 1;
		c.weightx = 0.25;
		addComponent(bottomPanel, bag, c, statusField);

		c.gridwidth = 1;
		c.weightx = 0.0;
		addComponent(bottomPanel, bag, c, UIFactory.createFormLabel("label.date.lastStatus"));

		c.gridwidth = GridBagConstraints.REMAINDER;
		c.weightx = 1.0;
		addComponent(bottomPanel, bag, c, lastStatusChangeField);

		c.gridwidth = 1;
		c.weightx = 0.0;
		label = UIFactory.createFormLabel("label.comments");
		label.setVerticalAlignment(SwingConstants.TOP);
		addComponent(bottomPanel, bag, c, label);

		c.gridwidth = GridBagConstraints.REMAINDER;
		c.weightx = 1.0;
		addComponent(bottomPanel, bag, c, new JScrollPane(commentsField));

		JSplitPane splitPane = UIFactory.createSplitPane(JSplitPane.VERTICAL_SPLIT);
		splitPane.setTopComponent(new JScrollPane(northPanel));
		splitPane.setBottomComponent(bottomPanel);

		setLayout(new BorderLayout(0, 0));
		add(northPanel, BorderLayout.NORTH);
		add(bottomPanel, BorderLayout.CENTER);
	}

	/**
	 * Adjust the activation dates on other grids based on the source grid.
	 * 
	 * @param source
	 * @return
	 */
	boolean adjustActivationDates(ProductGrid source) {
		boolean flag = false;
		DateSynonym effDate = source.getEffectiveDate();
		DateSynonym expDate = source.getExpirationDate();

		if (gridList != null && !gridList.isEmpty()) {
			for (int i = 0; i < gridList.size(); i++) {
				ProductGrid gridToCheck = gridList.get(i);
				if (ClientUtil.hasProductionRestrictions(gridToCheck.getStatus()) && (effDate == null || effDate.getDate().before(new Date()))) {
					break;
				}
				// target starts before new one and expires after new one starts
				if (gridToCheck.getEffectiveDate() == null || gridToCheck.getEffectiveDate().before(effDate)) {
					if (gridToCheck.getExpirationDate() == null || gridToCheck.getExpirationDate().after(effDate)) {
						gridToCheck.setExpirationDate(effDate);
						flag = true;
					}
				} // target starts after new one
				else if (expDate != null && expDate.getDate() != null) {
					if (gridToCheck.getEffectiveDate().before(expDate)) {
						gridToCheck.setEffectiveDate(expDate);
						flag = true;
					}
					if (gridToCheck.getExpirationDate() != null && gridToCheck.getExpirationDate().before(expDate)) {
						gridToCheck.setExpirationDate(expDate);
						flag = true;
					}
				}
			}
		}
		return flag;
	}

	private boolean cancelEditGrids() {
		try {
			ClientUtil.getCommunicator().unlockGrid(template.getID(), currentContext);
			return true;
		}
		catch (Exception exception) {
			ClientUtil.handleRuntimeException(exception);
			return false;
		}
	}

	@Override
	public void cellValueChanged(int column, Object newValue) {
		gridTablePanel.removeGridRowSelectionListener(gridSelectionListener);
		gridTablePanel.removeGridTableModelListener(gridTableModelListener);
		try {
			gridTablePanel.updateCellValue(ruleViewPanel.getSelectedRow(), column, newValue);
		}
		finally {
			gridTablePanel.addGridRowSelectionListener(gridSelectionListener);
			gridTablePanel.addGridTableModelListener(gridTableModelListener);
		}
	}

	private boolean checkAllowRemoveActivation() {
		boolean flag = true;
		ProductGrid abstractgrid = (ProductGrid) activationsCombo.getSelectedItem();
		if (abstractgrid == null)
			flag = false;
		else if (abstractgrid.isNew() || !ClientUtil.hasProductionRestrictions(abstractgrid.getStatus()))
			flag = true;
		else if (abstractgrid.getSunrise() != null && abstractgrid.getSunrise().after(new Date()))
			flag = true;
		else
			flag = false;
		return flag;
	}

	private void clearForm() {
		gridCardPanel.getSelectedCard().populate(null, true);
		commentsField.setText("");
		lastStatusChangeField.setText("");
		statusField.removeActionListener(statusComboL);
		statusField.setSelectedIndex(0);
		statusField.addActionListener(statusComboL);
		currentGrid = null;
		try {
			ruleViewPanel.setGrid(template, null);
		}
		catch (InvalidDataException e) {
			e.printStackTrace();
		}
		setEnabled(false);
	}

	private void cloneActivation() throws ServerException {
		ProductGrid sourceGrid = (ProductGrid) activationsCombo.getSelectedItem();
		ProductGrid newGrid = createGrid(sourceGrid, template, null, null);

		if ((new ActivationEditorDialog(this)).edit(newGrid, ActivationEditorDialog.Operation.COPY)) {
			gridList.add(newGrid);
			Collections.sort(gridList, ACTIVATION_COMPARATOR);
			setDirty(true);
			populate(gridList, gridList.indexOf(newGrid));
			setActivation(newGrid);
			selectActivation();
			//setSelectedStatus(newGrid.getStatus());
		}
	}

	/** @return true if requested action should continue. */
	private boolean continueAfterConditionalSave() {
		if (isDirty()) {
			Boolean userChoice = ClientUtil.getInstance().showSaveDiscardCancelDialog();

			if (userChoice == null) {
				return false; // user cancelled
			}

			try {
				if (userChoice.booleanValue()) {
					save();
				}
				else {
					discardChanges();
				}
			}
			catch (Exception ex) {
				ClientUtil.handleRuntimeException(ex);
			}
			finally {
				ClientUtil.getParent().setCursor(UIFactory.getDefaultCursor());
			}
		}
		return true;
	}

	private ProductGrid createGrid(AbstractGuidelineGrid abstractgrid, GridTemplate template, DateSynonym effDate, DateSynonym expDate) {
		ProductGrid gridData = ProductGrid.copyOf((ProductGrid) abstractgrid, template, effDate, expDate);
		return gridData;
	}

	@Override
	public void discardChanges() {
		clearForm();
		setDirty(false);
		setViewOnly(true);
		cancelEditGrids();
		removedGrids.clear();
	}

	private void displayGuidelinesSelectionPanel() {
		if (!viewOnly) {
			cancelEditGrids();
		}
		gridEditor.showGridSelectionPanel();
	}

	private synchronized void editActivation() throws ServerException {
		ProductGrid abstractgrid = (ProductGrid) activationsCombo.getSelectedItem();
		if ((new ActivationEditorDialog(this)).edit(abstractgrid, ActivationEditorDialog.Operation.EDIT)) {
			Collections.sort(gridList, ACTIVATION_COMPARATOR);
			activationsCombo.removeActionListener(activationsComboL);
			activationsCombo.removeAllItems();
			try {
				for (int i = 0; i < gridList.size(); i++) {
					activationsCombo.addItem(gridList.get(i));
				}
				activationsCombo.setSelectedItem(abstractgrid);
			}
			finally {
				activationsCombo.addActionListener(activationsComboL);
			}
			setDirty(true);
		}
	}

	private int findIndexOf(List<ProductGrid> gridList, AbstractGuidelineGrid gridToSelect) {
		if (gridToSelect == null || gridList.isEmpty()) return 0;
		if (gridList.contains(gridToSelect)) {
			return gridList.indexOf(gridToSelect);
		}
		else {
			for (int i = 0; i < gridList.size(); i++) {
				if (gridToSelect.getID() == gridList.get(i).getID()) {
					return i;
				}
			}
			return 0;
		}
	}

	private String getActivationString(ProductGrid abstractgrid) {
		StringBuilder buff = new StringBuilder();
		buff.append(" ");
		synchronized (dateNameCheckbox) {
			if (abstractgrid.getSunrise() != null) {
				buff.append(
						(dateNameCheckbox.isSelected()
								? abstractgrid.getEffectiveDate().getName()
								: Constants.THREADLOCAL_FORMAT_DATE_TIME_SEC.get().format(abstractgrid.getEffectiveDate().getDate())));
			}
			buff.append(" - ");
			if (abstractgrid.getSunset() != null) {
				buff.append(
						(dateNameCheckbox.isSelected()
								? abstractgrid.getExpirationDate().getName()
								: Constants.THREADLOCAL_FORMAT_DATE_TIME_SEC.get().format(abstractgrid.getExpirationDate().getDate())));
			}
		}
		return buff.toString();
	}

	public final GuidelineContextHolder getContextHolder() {
		return contextHolder;
	}

	private AbstractGuidelineGrid getSelectedGrid() {
		return currentGrid;
	}

	public final String getSelectedStatus() {
		return statusField.getSelectedEnumValueValue();
	}

	public GridTemplate getTemplate() {
		return template;
	}

	private boolean hasAnyGridProductionRestrictions() {
		if (ClientUtil.checkPermissionByPrivilegeName(PrivilegeConstants.PRIV_EDIT_PRODUCTION_DATA)) {
			return false;
		}
		boolean productionStatus = false;
		if (gridList != null && !gridList.isEmpty()) {
			for (int i = 0; i < gridList.size(); i++) {
				ProductGrid grid = gridList.get(i);
				if (ClientUtil.isHighestStatus(grid.getStatus())) {
					productionStatus = true;
					break;
				}
			}
		}
		return productionStatus;
	}

	private boolean hasProductionRestrictions() {
		return currentGrid != null && ClientUtil.hasProductionRestrictions(currentGrid.getStatus());
	}

	@Override
	public boolean hasUnsavedChanges() {
		return !viewOnly && isDirty;
	}

	private void hideRuleViewPanel() {
		ruleViewButton.setText(ClientUtil.getInstance().getLabel("button.show.rule"));
		ruleViewPanel.setVisible(false);
	}

	private void initActivationsPanel() {
		addActivationBtn = new JButton(ClientUtil.getInstance().getLabel("button.add"), ClientUtil.getInstance().makeImageIcon("image.btn.small.new"));
		// This is a copy button as of PowerEditor 4.2.0.
		cloneActivationBtn = new JButton(ClientUtil.getInstance().getLabel("button.copy"), ClientUtil.getInstance().makeImageIcon("image.btn.small.copy"));
		removeActivationBtn = new JButton(ClientUtil.getInstance().getLabel("button.remove"), ClientUtil.getInstance().makeImageIcon("image.btn.small.delete"));
		editActivationBtn = new JButton(ClientUtil.getInstance().getLabel("button.edit.date"), ClientUtil.getInstance().makeImageIcon("image.btn.small.edit"));
		activationPanel = UIFactory.createJPanel(new FlowLayout(0, 1, 1));
		activationPanel.add(activationsCombo);

		activationPanel.add(dateNameCheckbox);

		JButton ajbutton[] = { editActivationBtn, addActivationBtn, cloneActivationBtn, removeActivationBtn };
		for (int i = 0; i < ajbutton.length; i++)
			if (ajbutton[i] != null) {
				activationPanel.add(ajbutton[i]);
				ajbutton[i].addActionListener(buttonListener);
			}

		activationsCombo.setRenderer(new ActivationRenderer());
		activationsCombo.setPreferredSize(new Dimension(380, 26));
		activationsCombo.addActionListener(activationsComboL);

		statusField.addActionListener(statusComboL);
		editActivationBtn.setEnabled(false);
	}

	private void initButtonPanel() {
		saveGridButton = UIFactory.createButton(ClientUtil.getInstance().getLabel("button.save"), "image.btn.small.save", buttonListener, null);
		backButton = UIFactory.createButton(ClientUtil.getInstance().getLabel("button.back"), "image.btn.small.back", buttonListener, null);

		validateAllMenuItem = new JMenuItem(ClientUtil.getInstance().getLabel("button.validate.all"));
		validateCurrentMenuItem = new JMenuItem(ClientUtil.getInstance().getLabel("button.validate.this"));

		final JPopupMenu popup = new JPopupMenu();
		popup.add(validateAllMenuItem);
		popup.add(validateCurrentMenuItem);

		JButton validateButton = UIFactory.createButton(ClientUtil.getInstance().getLabel("button.validate"), "image.btn.small.validate", null, "button.tooltip.validate");
		validateButton.addMouseListener(new MouseAdapter() {

			@Override
			public void mousePressed(MouseEvent e) {
				popup.show(e.getComponent(), e.getX(), e.getY());
			}
		});

		JPanel jpanel = UIFactory.createJPanel(new FlowLayout(FlowLayout.CENTER, 10, 3));
		jpanel.setBackground(PowerEditorSwingTheme.primary3);

		if (backButton != null) jpanel.add(backButton);
		if (saveGridButton != null) jpanel.add(saveGridButton);

		jpanel.add(validateButton);

		final JButton toggleButton = UIFactory.createButton(ClientUtil.getInstance().getLabel("button.hide.context"), null, null, null);
		toggleButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				topPanel.setVisible(!topPanel.isVisible());
				toggleButton.setText((topPanel.isVisible() ? ClientUtil.getInstance().getLabel("button.hide.context") : ClientUtil.getInstance().getLabel("button.show.context")));
			}
		});
		jpanel.add(new JLabel("  "));
		jpanel.add(toggleButton);

		ruleViewButton = UIFactory.createButton(ClientUtil.getInstance().getLabel("button.show.rule"), null, null, null);
		ruleViewButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (ruleViewPanel.isVisible()) {
					hideRuleViewPanel();
				}
				else {
					showRuleViewPanel();
				}
			}
		});
		jpanel.add(ruleViewButton);

		final String showLabel = ClientUtil.getInstance().getLabel("button.show.rule.id");
		ruleIDToggleButton = UIFactory.createButton(showLabel, null, null, null);
		ruleIDToggleButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				toggleRuleIDColumns();
				ruleIDToggleButton.setText(ruleIDToggleButton.getText().equals(showLabel) ? ClientUtil.getInstance().getLabel("button.hide.rule.id") : showLabel);
			}
		});
		jpanel.add(new JLabel("  "));
		jpanel.add(ruleIDToggleButton);

		//to make edit template button or view template button		
		if (ClientUtil.checkViewOrEditAnyTemplatePermission()) {
			if (template != null) {//check for permissions only if template is not null
				String editTemplatesPrivilegeName = UtilBase.constructEditTemplatesPrivilege_Name(template.getUsageType().getPrivilege());
				String viewTemplatesPrivilegeName = UtilBase.constructViewTemplatesPrivilege_Name(template.getUsageType().getPrivilege());
				if (!readOnly && ClientUtil.checkPermissionByPrivilegeName(editTemplatesPrivilegeName)) {
					editTemplateButton = UIFactory.createButton(ClientUtil.getInstance().getLabel("button.edit.template"), null, null, null);
				}
				else if (ClientUtil.checkPermissionByPrivilegeName(viewTemplatesPrivilegeName)) {
					editTemplateButton = UIFactory.createButton(ClientUtil.getInstance().getLabel("button.view.template"), null, null, null);
				}
			}
			else {//if template is null, then just make button with view label
				editTemplateButton = UIFactory.createButton(ClientUtil.getInstance().getLabel("button.view.template"), null, null, null);
			}

			editTemplateButton.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					if (template != null) {
						try {
							ClientUtil.showTemplateEditPanel(template);
						}
						catch (CanceledException e1) {
							e1.printStackTrace();
						}
					}
				}
			});
			jpanel.add(editTemplateButton);
		}

		buttonPanel = jpanel;
		UIFactory.setLookAndFeel(buttonPanel);

		validateAllMenuItem.addActionListener(buttonListener);
		validateCurrentMenuItem.addActionListener(buttonListener);
	}

	private void initComponents() {
		ClientUtil.getInstance();
		templateNameLabel.setFont(PowerEditorSwingTheme.windowtitlefont);
		templateDescLabel.setEditable(false);
		templateDescLabel.setRows(3);
		templateDescLabel.setLineWrap(true);
		templateDescLabel.setWrapStyleWord(true);
		templateDescLabel.setVisible(true);

		initActivationsPanel();
		initButtonPanel();
		initGridTablePanel();
		commentsField.setLineWrap(false);
		commentsField.setWrapStyleWord(false);
		commentsField.setRows(4);
		commentsField.setPreferredSize(new Dimension(100, 28));
		lastStatusChangeField.setEditable(false);

		setEditable(false);
	}

	private void initGridTablePanel() {
		gridTablePanel = new GridTablePanel();
		gridCardPanel = new GridCardsPanel(gridTablePanel);
	}

	private void initializeNewActivationDates(ProductGrid source) {
		DateSynonym date = null;
		DateSynonym effDate = date;
		DateSynonym expDate = null;
		if (gridList != null && !gridList.isEmpty()) {
			for (int i = 0; i < gridList.size(); i++) {
				ProductGrid abstractgrid1 = gridList.get(i);
				if (i == 0 && abstractgrid1.getEffectiveDate() != null && abstractgrid1.getEffectiveDate().after(date)) {
					effDate = date;
					expDate = abstractgrid1.getExpirationDate();
				}
				else {
					effDate = abstractgrid1.getEffectiveDate();
					if (i + 1 < gridList.size()) {
						ProductGrid abstractgrid2 = gridList.get(i + 1);
						expDate = abstractgrid2.getExpirationDate();
					}
					else {
						expDate = null;
					}
				}
				if (effDate == null || effDate.before(date)) effDate = date;
				if (expDate == null || expDate.getDate() == null
						|| (effDate != null && effDate.getDate() != null && expDate.getDate().getTime() - effDate.getDate().getTime() > 0x5265c00L))
					break;
			}

		}

		source.setEffectiveDate(effDate);
		if (expDate != null)
			source.setExpirationDate(expDate);
		else
			source.setExpirationDate(expDate);
	}

	private void initTimer() {
		updateStateTimer = new Timer(500, new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent actionevent) {
				if (!isDirty() && gridCardPanel.getSelectedCard().isDirty()) {
					setDirty(true);
				}
			}

		});
	}

	public boolean isDirty() {
		if (isDirty) return isDirty;
		AbstractGuidelineGrid abstractgrid = getSelectedGrid();
		if (abstractgrid == null) return false;
		String s = commentsField.getText();
		if (!s.equals(abstractgrid.getComments())) {
			setDirty(true);
		}
		String s1 = statusField.getSelectedEnumValueValue();
		if (!s1.equals(abstractgrid.getStatus())) {
			setDirty(true);
		}
		return isDirty;
	}

	private boolean isGridActive() {
		return currentGrid != null && (currentGrid.getEffectiveDate() == null || currentGrid.getEffectiveDate().getDate().before(new Date()));
	}

	private boolean isGridExpired() {
		return currentGrid != null && currentGrid.getExpirationDate() != null && currentGrid.getExpirationDate().getDate().before(new Date());
	}

	private boolean isRuleIDButtonShowButton() {
		return ruleIDToggleButton.getText().equals(ClientUtil.getInstance().getLabel("button.show.rule.id"));
	}

	private boolean isSame(ProductGrid serverGrid, ProductGrid selectedGrid) {
		if (UtilBase.isSame(serverGrid.getEffectiveDate(), selectedGrid.getEffectiveDate()) && UtilBase.isSame(serverGrid.getExpirationDate(), selectedGrid.getExpirationDate())
				&& UtilBase.isSame(serverGrid.getCreationDate(), selectedGrid.getCreationDate()) && serverGrid.getStatus().equals(selectedGrid.getStatus())
				&& serverGrid.getNumRows() == selectedGrid.getNumRows()) {
			return true;
		}
		else {
			return false;
		}
	}

	public boolean isViewOnly() {
		return viewOnly;
	}

	public void populate(GuidelineContext[] contexts, List<ProductGrid> list, GridTemplate gridTemplate, boolean isSubcontext, AbstractGuidelineGrid gridToSelect) {
		setTemplate(gridTemplate);

		contextHolder.setContextElemens(contexts);
		contextHolder.setEditContextEnabled(!viewOnly && !isSubcontext);
		contextHolder.setHasProductionRestrictions(hasAnyGridProductionRestrictions());
		currentGrid = null;
		gridList.clear();
		gridList.addAll(list);
		this.currentContext = contexts;

		Collections.sort(gridList, ACTIVATION_COMPARATOR);
		lastSelectedGrid = null;

		populate(gridList, findIndexOf(gridList, gridToSelect));
		selectActivation();
		setDirty(false);
		contextMayHaveChanged = false;
		if (editTemplateButton != null) {
			if (!readOnly && ClientUtil.checkEditTemplatePermission(template)) {
				editTemplateButton.setText(ClientUtil.getInstance().getLabel("button.edit.template"));
			}
			else {
				editTemplateButton.setText(ClientUtil.getInstance().getLabel("button.view.template"));
			}
		}
		if (saveGridButton.isEnabled() && isViewOnly()) {
			saveGridButton.setEnabled(false);
		}
	}

	private void populate(List<ProductGrid> list, int indexToSelect) {
		if (list != null && list.size() == 0) {
			clearForm();
			activationsCombo.removeActionListener(activationsComboL);
			activationsCombo.removeAllItems();
			activationsCombo.addActionListener(activationsComboL);
		}
		else if (list == null || indexToSelect < 0 || indexToSelect >= list.size()) {
			clearForm();
		}
		else {
			activationsCombo.removeActionListener(activationsComboL);
			activationsCombo.removeAllItems();
			for (int j = 0; j < list.size(); j++) {
				activationsCombo.addItem(list.get(j));
			}
			activationsCombo.setSelectedIndex(indexToSelect);
			activationsCombo.addActionListener(activationsComboL);
		}
	}

	private void removeActivation() {
		ProductGrid gridToRemove = (ProductGrid) activationsCombo.getSelectedItem();
		if (!checkAllowRemoveActivation()) {
			return;
		}
		if (ClientUtil.getUserInterfaceConfig().getUIPolicies() != null
				&& UtilBase.asBoolean(ClientUtil.getUserInterfaceConfig().getUIPolicies().isEnforceSequentialActivationDates(), false)) {
			String msgKey = Validator.validateGapsInDatesForRemoval(
					UtilBase.asBoolean(ClientUtil.getUserInterfaceConfig().getUIPolicies().isAllowGapsInActivationDates(), false),
					gridToRemove,
					gridList);
			if (msgKey != null) {
				ClientUtil.getInstance().showWarning(msgKey);
				return;
			}
		}

		if (ClientUtil.getInstance().showConfirmation("RemoveActivationMsg")) {
			gridList.remove(gridToRemove);
			removedGrids.add(gridToRemove);
			setDirty(true);
			populate(gridList, 0);
			selectActivation();
		}
	}

	/**
	 * @since PowerEditor 4.2.0 This is needed in order to repopulate enum fields with enum strings
	 *        (updateFromGui makes them into numerical values).
	 */
	protected final void repopulateGui() {
		if (currentGrid != null) {
			gridCardPanel.getSelectedCard().populate(currentGrid, isRuleIDButtonShowButton());
		}
	}

	private void save() {
		try {
			saveChanges_internal();
		}
		catch (Exception exception) {
			ClientUtil.handleRuntimeException(exception);
		}
	}

	@Override
	public void saveChanges() throws CanceledException, ServerException {
		saveChanges_internal();
	}

	private synchronized void saveChanges_internal() throws ServerException {
		updateFromGui();

		// temporarily store column width
		storeColumnWidthsIfPossible();

		if (validateGridDataForBlanks()) {
			saveGridsOnServer();
			updateNewGridIDs();
			removedGrids.clear();
			if (contextMayHaveChanged) {
				GuidelineContext[] newContexts = contextHolder.getGuidelineContexts();
				if (!GuidelineContext.isIdentical(currentContext, newContexts)) {
					saveGridContextChanges(newContexts);
					this.currentContext = newContexts;
				}
				contextMayHaveChanged = false;
			}
			setEnabledState();
			setDirty(false);
		}
		else {
			setDirty(true);
		}
	}

	private void saveGridContextChanges(GuidelineContext[] newContexts) throws ServerException {
		ClientUtil.getCommunicator().updateGridContext(template.getID(), gridList, newContexts);
	}

	private void saveGridsOnServer() throws ServerException {
		ClientUtil.getCommunicator().saveGridData(template.getID(), gridList, removedGrids);
	}

	private void selectActivation() {
		logger.info("Activation Combo selected. Last = " + lastSelectedGrid);
		AbstractGuidelineGrid selectedGrid = (AbstractGuidelineGrid) activationsCombo.getSelectedItem();
		int newIndex = activationsCombo.getSelectedIndex();
		logger.info("Activation Combo: new index = " + newIndex);
		if (autoSaveOn && lastSelectedGrid != null && !lastSelectedGrid.equals(selectedGrid)) updateFromGui();
		lastSelectedGrid = selectedGrid;
		setActivation(selectedGrid);
		setEnabledState();
	}

	protected final void setActivation(AbstractGuidelineGrid grid) {
		if (gridList == null || gridList.size() < 1 || grid == null) {
			clearForm();
			return;
		}
		else {
			boolean hideRuleIDColumns = currentGrid == null || isRuleIDButtonShowButton();
			currentGrid = grid;
			gridCardPanel.getSelectedCard().populate(grid, hideRuleIDColumns);
			commentsField.setText(grid.getComments());
			lastStatusChangeField.setText(UtilBase.format(grid.getStatusChangeDate()));
			setSelectedStatus(grid.getStatus());
			try {
				ruleViewPanel.setGrid(template, gridTablePanel.getGridTableModel());
			}
			catch (InvalidDataException e) {
				e.printStackTrace();
			}
		}
	}

	protected final void setCurrentContext(GuidelineContext[] contexts) {
		this.currentContext = contexts;
	}

	public void setDirty(boolean flag) {
		if (isViewOnly()) {
			isDirty = false;
			return;
		}

		isDirty = flag;
		saveGridButton.setEnabled(flag);
		if (flag)
			stopTimer();
		else
			startTimer();
	}

	private void setEditable(boolean flag) {
		contextHolder.setHasProductionRestrictions(hasAnyGridProductionRestrictions());
		gridCardPanel.getSelectedCard().setViewOnly(currentGrid != null && !flag || hasProductionRestrictions());
		gridCardPanel.getSelectedCard().setEnabled(currentGrid != null && flag && !hasProductionRestrictions());
		boolean b = isGridExpired() && hasProductionRestrictions();
		editActivationBtn.setEnabled(currentGrid != null && flag && !(b));
		addActivationBtn.setEnabled(flag);
		cloneActivationBtn.setEnabled(currentGrid != null && flag);
		removeActivationBtn.setEnabled(currentGrid != null && flag && !hasProductionRestrictions());
		commentsField.setEnabled(currentGrid != null && flag && !hasProductionRestrictions());
		statusField.setEnabled(currentGrid != null && flag && !(hasProductionRestrictions() && isGridActive()));
		ruleViewPanel.setEnabled(currentGrid != null && flag);
	}

	@Override
	public void setEnabled(boolean flag) {
		boolean enabled = flag && !isViewOnly() && !hasProductionRestrictions();

		gridCardPanel.getSelectedCard().setEnabled(enabled);

		if (editActivationBtn != null) {
			boolean b = isGridExpired() && hasProductionRestrictions();
			editActivationBtn.setEnabled(flag && !(b));
		}
		if (cloneActivationBtn != null) {
			cloneActivationBtn.setEnabled(flag && !isViewOnly());
		}
		if (removeActivationBtn != null) {
			removeActivationBtn.setEnabled(enabled && checkAllowRemoveActivation());
		}
		super.setEnabled(enabled);
	}

	private void setEnabledState() {
		setEditable(!viewOnly);
	}

	public void setGridEditor(GuidelineGridEditor gridEditor) {
		this.gridEditor = gridEditor;
	}

	private final void setSelectedStatus(String status) {
		statusField.removeActionListener(statusComboL);
		statusField.selectTypeEnumValue(status);
		statusField.addActionListener(statusComboL);
	}

	private void setTemplate(GridTemplate gridtemplate) {
		template = gridtemplate;
		gridCardPanel.setTemplate(template);
		if (template != null) {
			templateNameLabel.setText(template.getName() + " (Type: " + template.getUsageType() + ")");
			templateDescLabel.setText(template.getDescription());
			templateDescLabel.setCaretPosition(0);
			if (template.getMaxNumOfRows() < 2) {
				showRuleViewPanel();
				ruleViewButton.setEnabled(false);
			}
			else {
				ruleViewButton.setEnabled(true);
			}
			ruleIDToggleButton.setVisible(template.hasRuleIDColumn());
		}
		invalidate();
	}

	public void setViewOnly(boolean flag) {
		viewOnly = flag;
		setEditable(!flag);
	}

	private void showRuleViewPanel() {
		ruleViewButton.setText(ClientUtil.getInstance().getLabel("button.hide.rule"));
		ruleViewPanel.setVisible(true);
		if (guidelineSplitPane.getDividerLocation() < 7) {
			guidelineSplitPane.setDividerLocation(440);
		}
	}

	private void showSuccessMsg() {
		ClientUtil.getInstance().showInformation("ValidationSuccessMsg");
	}

	private void startTimer() {
		updateStateTimer.start();
	}

	private void stopTimer() {
		updateStateTimer.stop();
	}

	private void storeColumnWidthsIfPossible() {
		try {
			int[] columnWidths = gridTablePanel.getColumnWidths();
			AbstractGuidelineGrid selectedGrid = (AbstractGuidelineGrid) activationsCombo.getSelectedItem();
			if (selectedGrid != null) {
				ClientUtil.getPreferenceManager().storeGridColumnWidths(selectedGrid.getTemplateID(), columnWidths);
			}
		}
		catch (Exception ex) {
			// ignore
			ex.printStackTrace();
		}
	}

	private void toggleRuleIDColumns() {
		gridCardPanel.getSelectedCard().toggleRuleIDColumns();
	}

	private final boolean updateFromGui() {
		if (activationsCombo.getItemCount() > 0 && currentGrid == null) {
			ClientUtil.getInstance().showWarning("msg.warning.select.activation");
			return false;
		}
		else if (activationsCombo.getItemCount() == 0 && currentGrid == null) {
			return true;
		}

		boolean flag = false;
		try {
			if (currentGrid != null) {

				IGridDataCard currentGridTablePanel = gridCardPanel.getSelectedCard();
				if (currentGridTablePanel.isDirty()) {
					currentGridTablePanel.cancelEdits();
					currentGrid.setDataList(currentGridTablePanel.getDataVector());
					currentGridTablePanel.getGridTableModel().setDirty(false);
					flag = true;
				}

				String s = commentsField.getText();
				if (!s.equals(currentGrid.getComments())) {
					currentGrid.setComments(s);
					flag = true;
				}

				String s1 = statusField.getSelectedEnumValueValue();
				if (!s1.equals(currentGrid.getStatus())) {
					currentGrid.setStatus(s1);
					flag = true;
				}
			}

			if (flag) setDirty(true);
		}
		catch (Exception exception) {
			ClientUtil.handleRuntimeException(exception);
		}

		return flag;
	}

	/**
	 * TT 1674: If a new grid has been saved, this updates the grid ID on the client from -1 to 
	 * the grid ID assigned by the server. If this is not done the grid ID will remain
	 * -1, causing numerous problems (i.e. when the context is saved a NPE is thrown). 
	 */
	private void updateNewGridIDs() {
		ProductGrid selectedGrid = (ProductGrid) activationsCombo.getSelectedItem();
		if (selectedGrid != null && selectedGrid.getID() == -1) {
			//need to go to the server and get the new grid ID            
			try {
				GridDataResponse response = ClientUtil.getCommunicator().fetchGridData(currentGrid.getTemplateID(), currentContext);
				if (response.getResultList() != null && response.getResultList().size() > 0) {
					for (Iterator<ProductGrid> i = response.getResultList().iterator(); i.hasNext();) {
						ProductGrid serverGrid = i.next();
						if (isSame(serverGrid, selectedGrid)) {
							selectedGrid.setID(serverGrid.getID());
						}
					}
				}
			}
			catch (ServerException e) {
				e.printStackTrace();
			}
		}

	}

	private boolean validateCurrentGrid(boolean flag) {
		updateFromGui();
		boolean flag1 = GridValidator.validate(currentGrid, !flag);
		if (flag1 && !flag) showSuccessMsg();
		return flag1;
	}

	private boolean validateForSave() {
		boolean flag = GridValidator.validate(gridList, true);
		flag = GridValidator.validateForBlanks(gridList);
		return flag;
	}

	private boolean validateGridData() {
		boolean flag = true;
		updateFromGui();
		if (!validateForSave()) flag = false;
		if (flag) showSuccessMsg();
		return flag;
	}

	private boolean validateGridDataForBlanks() {
		boolean flag = GridValidator.validateForBlanks(currentGrid);
		return flag;
	}


}
