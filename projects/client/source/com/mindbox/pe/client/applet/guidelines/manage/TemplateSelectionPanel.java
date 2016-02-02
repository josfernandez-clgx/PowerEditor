package com.mindbox.pe.client.applet.guidelines.manage;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.SwingConstants;

import com.mindbox.pe.client.ClientUtil;
import com.mindbox.pe.client.applet.PowerEditorSwingTheme;
import com.mindbox.pe.client.applet.UIFactory;
import com.mindbox.pe.client.common.AbstractThreadedActionAdapter;
import com.mindbox.pe.client.common.ButtonPanel;
import com.mindbox.pe.common.GuidelineContextProvider;
import com.mindbox.pe.communication.GridDataResponse;
import com.mindbox.pe.communication.ServerException;
import com.mindbox.pe.model.GridSummary;
import com.mindbox.pe.model.GuidelineContext;
import com.mindbox.pe.model.TemplateUsageType;

/**
 * 
 * @author Gene Kim
 * @author MindBox, Inc
 * @since PowerEditor 1.10.0
 */
public final class TemplateSelectionPanel extends JPanel implements GuidelineContextProvider {

	protected static final void addComponent(JPanel panel, GridBagLayout bag, GridBagConstraints c, Component component) {
		bag.setConstraints(component, c);
		panel.add(component);
	}

	private final class MouseL extends MouseAdapter {

		public void mouseClicked(MouseEvent e) {
			if (e.getClickCount() == 2) {
				if (!readOnly && editButton.isEnabled()) {
					editTemplate();
				}
				else if (!editButton.isEnabled()) {// simple view on double click
					GridSummary summary = guidelinesTable.getSelectedGridSummary();
					if (summary != null) {
						populateGrids(summary.getTemplateID(), false, summary.isSubContext());
					}
				}
			}
		}
	}

	private final class NewL extends AbstractThreadedActionAdapter {

		public void performAction(ActionEvent e) {
			createTemplate();
		}
	}

	private final class EditL extends AbstractThreadedActionAdapter {

		public void performAction(ActionEvent e) {
			editTemplate();
		}
	}

	private final class ViewL extends AbstractThreadedActionAdapter {

		public void performAction(ActionEvent e) {
			GridSummary summary = guidelinesTable.getSelectedGridSummary();
			if (summary != null) {
				populateGrids(summary.getTemplateID(), false, summary.isSubContext());
			}
		}
	}

	private final class SetFullContextL extends AbstractThreadedActionAdapter {

		public void performAction(ActionEvent e) {
			if (guidelinesTable.getSelectedTemplateID() > 0) {
				try {
					GuidelineContext[] fullContext = ClientUtil.getCommunicator().fetchFullContext(
							guidelinesTable.getSelectedTemplateID(),
							contexts);
					if (fullContext == null) {
						fullContext = new GuidelineContext[0];
					}
					if (gridEditor != null) {
						gridEditor.resetContext(fullContext);
					}
					TemplateSelectionPanel.this.contexts = fullContext;
					reloadTemplates();
				}
				catch (Exception ex) {
					ClientUtil.handleRuntimeException(ex);
				}
			}
		}
	}

	private final JButton editButton;
	private final JButton viewButton;
	private final JButton newButton;
	private final JButton setFullContextButton;
	private final GuidelinesTable guidelinesTable;
	private GuidelineContext[] contexts = null;
	private GuidelineGridEditor gridEditor = null;
	private JLabel contextLabel = null;
	private TemplateUsageType currentUsage = null;
	private boolean isClear = true;
	private final boolean readOnly;

	public TemplateSelectionPanel(boolean readOnly) {
		this.readOnly = readOnly;
		this.editButton = UIFactory.createButton(
				ClientUtil.getInstance().getLabel("button.edit"),
				"image.btn.small.edit",
				new EditL(),
				null);
		this.viewButton = UIFactory.createButton(
				ClientUtil.getInstance().getLabel("button.view"),
				"image.btn.small.view",
				new ViewL(),
				null);
		this.newButton = UIFactory.createButton(ClientUtil.getInstance().getLabel("button.new"), "image.btn.small.new", new NewL(), null);
		this.setFullContextButton = UIFactory.createButton(
				ClientUtil.getInstance().getLabel("button.reset.full.context"),
				null,
				new SetFullContextL(),
				null);
		this.guidelinesTable = new GuidelinesTable(new GuidelinesTableModel(), editButton, viewButton, setFullContextButton);
		UIFactory.setLookAndFeel(this);
		setBorder(UIFactory.createTitledBorder(ClientUtil.getInstance().getLabel("label.title.guideline.template")));
		initPanel();

		editButton.setEnabled(false);
		viewButton.setEnabled(false);
		setFullContextButton.setEnabled(false);

		newButton.setEnabled(false);
		newButton.setVisible(false);

		this.guidelinesTable.addMouseListener(new MouseL());
	}

	private void editTemplate() {
		GridSummary summary = guidelinesTable.getSelectedGridSummary();
		if (summary != null) {
			populateGrids(summary.getTemplateID(), true, summary.isSubContext());
		}
	}

	private void createTemplate() {
		// TBD implement template+guideline creation
	}

	private void initPanel() {
		contextLabel = new JLabel("");
		contextLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		contextLabel.setFont(PowerEditorSwingTheme.windowtitlefont);

		GridBagLayout bag = new GridBagLayout();
		this.setLayout(bag);

		GridBagConstraints c = new GridBagConstraints();
		c.anchor = GridBagConstraints.NORTHWEST;
		c.insets = new Insets(4, 2, 2, 2);
		c.fill = GridBagConstraints.BOTH;
		c.weighty = 0.0;
		c.gridheight = 1;

		c.gridwidth = 1;
		c.weightx = 0.0;
		ButtonPanel buttonPanel = new ButtonPanel((readOnly ? new JButton[] { viewButton, setFullContextButton } : new JButton[] {
				newButton,
				viewButton,
				editButton,
				setFullContextButton }), FlowLayout.LEFT);
		addComponent(this, bag, c, buttonPanel);

		c.gridwidth = GridBagConstraints.REMAINDER;
		c.weightx = 1.0;
		addComponent(this, bag, c, contextLabel);

		c.insets = new Insets(1, 1, 1, 1);
		addComponent(this, bag, c, new JSeparator());

		// add template table
		JScrollPane templatePane = new JScrollPane(guidelinesTable);
		UIFactory.setLookAndFeel(templatePane);
		guidelinesTable.setPreferredScrollableViewportSize(new Dimension(200, 180));

		c.gridwidth = GridBagConstraints.REMAINDER;
		c.weightx = 1.0;
		c.gridheight = GridBagConstraints.REMAINDER;
		c.weighty = 1.0;
		addComponent(this, bag, c, templatePane);
	}

	public synchronized void clearTemplates() {
		if (!isClear) {
			this.guidelinesTable.clearGuidelines();
			contextLabel.setText("");
			editButton.setEnabled(false);
			viewButton.setEnabled(false);
			newButton.setEnabled(false);
			currentUsage = null;
			isClear = true;
		}
	}

	synchronized void reloadTemplates() {
		try {
			List<GridSummary> summaryList = ClientUtil.getCommunicator().fetchGridSummaries(this.currentUsage, this.contexts);
			setTemplates(currentUsage, contexts, summaryList);
		}
		catch (ServerException ex) {
			ClientUtil.getLogger().error("failed to get template summaries for " + contexts, ex);
			ClientUtil.getInstance().showErrorDialog(
					"msg.error.generic.service",
					new Object[] { ClientUtil.getInstance().getErrorMessage(ex) });
		}
	}

	public synchronized void setTemplates(TemplateUsageType usageType, GuidelineContext[] contexts, List<GridSummary> templateList) {
		if (usageType != null) {
			currentUsage = usageType;
			this.contextLabel.setText("<html><body>" + ClientUtil.getInstance().getLabel("label.guideline.type") + ": <b>" + usageType
					+ "</b></body></html>");
		}
		else {
			this.contextLabel.setText(null);
		}
		this.contexts = contexts;
		this.guidelinesTable.setGuidelines(templateList);
		editButton.setEnabled(false);
		viewButton.setEnabled(false);
		newButton.setEnabled(currentUsage != null);

		isClear = false;
	}

	// 2.4.7 call change of button from here
	private synchronized void populateGrids(int templateID, boolean isForEdit, boolean isSubcontext) {
		// use context and templateID to load grids
		ClientUtil.getParent().setCursor(UIFactory.getWaitCursor());
		try {
			if (isForEdit) {
				ClientUtil.getCommunicator().lockGrid(templateID, contexts);
			}
			// set isEdit flag
			gridEditor.setViewOnly(!isForEdit);

			GridDataResponse response = ClientUtil.getCommunicator().fetchGridData(templateID, contexts);

			gridEditor.populate(contexts, response.getResultList(), response.getTemplate(), isSubcontext, null);

			// show grid management panel
			gridEditor.showGridManagementPanel();
		}
		catch (ServerException ex) {
			ClientUtil.getInstance().showErrorDialog("msg.error.generic.service", ex.getMessage());
		}
		finally {
			ClientUtil.getParent().setCursor(UIFactory.getDefaultCursor());
		}
	}

	void setGridEditor(GuidelineGridEditor gridEditor) {
		this.gridEditor = gridEditor;
	}

	public GuidelineContext[] getGuidelineContexts() {
		return this.contexts;
	}

	public GuidelinesTable getGuidelinesTable() {
		return this.guidelinesTable;
	}
}