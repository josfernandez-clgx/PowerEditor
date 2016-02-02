package com.mindbox.pe.client.common.selection;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;

import com.mindbox.pe.client.ClientUtil;
import com.mindbox.pe.client.EntityModelCacheFactory;
import com.mindbox.pe.client.applet.UIFactory;
import com.mindbox.pe.client.common.AbstractThreadedActionAdapter;
import com.mindbox.pe.client.common.PanelBase;
import com.mindbox.pe.client.common.filter.panel.GuidelineFilterPanel;
import com.mindbox.pe.client.common.table.TemplateIDNameTable;
import com.mindbox.pe.client.common.tree.UsageTypeTemplateTreeWithCheckBox;
import com.mindbox.pe.common.config.GuidelineTabConfig;
import com.mindbox.pe.model.GridTemplate;
import com.mindbox.pe.model.TemplateUsageType;

/**
 * Guideline type or template selection panel.
 * @see GuidelineFilterPanel
 */
public class GuidelineTypeTemplateCheckBoxSelectionPanel extends PanelBase {

	private final class TableViewCheckBoxL implements ActionListener {

		public void actionPerformed(ActionEvent e) {
			if (((JRadioButton) e.getSource()).isSelected()) {
				displayTemplateAsTable = true;
				refreshDisplay();
			}
		}
	}

	private final class TreeViewCheckBoxL implements ActionListener {

		public void actionPerformed(ActionEvent e) {
			if (((JRadioButton) e.getSource()).isSelected()) {
				displayTemplateAsTable = false;
				refreshDisplay();
			}
		}
	}

	private final JPanel viewPanel;
	private final JButton clearButton;
	private final CardLayout viewCard;
	private final UsageTypeTemplateTreeWithCheckBox usageTemplateTree;
	private TemplateIDNameTable templateTable = null;
	private boolean displayTemplateAsTable = false;

	/**
	 * @param allowTableView
	 * @param searchView
	 */
	public GuidelineTypeTemplateCheckBoxSelectionPanel(boolean allowTableView) {
		this(null, allowTableView);
	}

	/**
	 * @param topButtons
	 * @param allowTableView
	 * @param searchView
	 */
	public GuidelineTypeTemplateCheckBoxSelectionPanel(JButton[] topButtons, boolean allowTableView) {
		templateTable = new TemplateIDNameTable(EntityModelCacheFactory.getInstance().getTemplateIDNameTableModel());
		templateTable.setRowSelectionAllowed(true);
		templateTable.setColumnSelectionAllowed(false);
		templateTable.getSelectionModel().setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

		usageTemplateTree = new UsageTypeTemplateTreeWithCheckBox(false);

		viewCard = new CardLayout();
		viewPanel = new JPanel(viewCard);

		clearButton = UIFactory.createJButton("button.clear.selection", null, new AbstractThreadedActionAdapter() {
			@Override
			public void performAction(ActionEvent event) throws Exception {
				templateTable.clearSelection();
			}
		}, null);

		if (allowTableView) {
			JPanel buttonPanel = UIFactory.createFlowLayoutPanelLeftAlignment(1, 1);
			buttonPanel.add(clearButton);
			JPanel tableViewPanel = UIFactory.createBorderLayoutPanel(0, 0);
			tableViewPanel.add(buttonPanel, BorderLayout.NORTH);
			tableViewPanel.add(new JScrollPane(templateTable), BorderLayout.CENTER);
			tableViewPanel.setBorder(BorderFactory.createLoweredBevelBorder());
			viewPanel.add(tableViewPanel, "TABLE");
		}
		JPanel panel = usageTemplateTree.getJComponent();
		viewPanel.add(panel, "TREE");


		initPanel(topButtons, allowTableView);

		refreshDisplay();
	}

	public void clearSelection() {
		refreshDisplay();
	}

	public List<GridTemplate> getSelectedTemplates() {
		if (!displayTemplateAsTable) {
			return usageTemplateTree.getSelectedTemplates();
		}
		else {
			return templateTable.getSelectedTemplates();
		}
	}

	public List<Integer> getSelectedTemplateIDs() {
		if (!displayTemplateAsTable) {
			return usageTemplateTree.getSelectedTemplateIDs();
		}
		else {
			return templateTable.getSelectedTemplateIDs();
		}
	}

	public List<TemplateUsageType> getSelectedUsageTypes() {
		if (!displayTemplateAsTable) return usageTemplateTree.getSelectedUsageTypes();
		return new ArrayList<TemplateUsageType>();
	}

	public List<GuidelineTabConfig> getSelectedUsageGroups() {
		if (!displayTemplateAsTable) return usageTemplateTree.getSelectedUsageTypeGroups();
		return new ArrayList<GuidelineTabConfig>();
	}

	private void initPanel(JButton[] topButtons, boolean allowTableView) {
		JPanel tnPanel = null;
		if (allowTableView) {
			JRadioButton asTreeRadio = new JRadioButton(ClientUtil.getInstance().getLabel("radio.as.tree"), false);
			asTreeRadio.addActionListener(new TreeViewCheckBoxL());
			asTreeRadio.setFocusable(false);
			JRadioButton asTableRadio = new JRadioButton(ClientUtil.getInstance().getLabel("radio.as.table"), false);
			asTableRadio.addActionListener(new TableViewCheckBoxL());
			asTableRadio.setFocusable(false);

			ButtonGroup group = new ButtonGroup();
			group.add(asTreeRadio);
			group.add(asTableRadio);

			tnPanel = UIFactory.createJPanel(new FlowLayout(FlowLayout.LEFT, 2, 2));
			tnPanel.add(asTreeRadio);
			tnPanel.add(asTableRadio);

			asTreeRadio.setSelected(true);
			asTableRadio.setSelected(false);
		}

		JPanel topPanel = UIFactory.createBorderLayoutPanel(0, 0);

		if (topButtons != null && topButtons.length > 0) {
			JPanel btnPanel = UIFactory.createJPanel(new FlowLayout(FlowLayout.RIGHT, 2, 2));
			for (int i = 0; i < topButtons.length; i++) {
				btnPanel.add(topButtons[i]);
			}
			topPanel.add(btnPanel, BorderLayout.EAST);
			if (tnPanel != null) topPanel.add(tnPanel, BorderLayout.WEST);
		}
		else {
			if (tnPanel != null) topPanel.add(tnPanel, BorderLayout.CENTER);
		}

		setLayout(new BorderLayout(1, 1));
		add(topPanel, BorderLayout.NORTH);
		add(viewPanel, BorderLayout.CENTER);
	}

	private void refreshDisplay() {
		if (displayTemplateAsTable) {
			templateTable.clearSelection();
			viewCard.show(viewPanel, "TABLE");
		}
		else {
			usageTemplateTree.clearSelection();
			viewCard.show(viewPanel, "TREE");
		}
	}
}
