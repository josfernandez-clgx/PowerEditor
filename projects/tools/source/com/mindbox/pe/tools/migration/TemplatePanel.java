package com.mindbox.pe.tools.migration;

import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Iterator;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import com.mindbox.pe.model.GridTemplate;
import com.mindbox.pe.tools.db.DBConnInfo;
import com.mindbox.pe.tools.db.DBConnectionFactory;
import com.mindbox.pe.tools.util.SwingUtil;


/**
 * @author Geneho Kim
 * @since PowerEditor 
 */
class TemplatePanel extends AbstractMigrationDetailPanel {

	private class SetToDefaultL implements ActionListener {

		public void actionPerformed(ActionEvent e) {
			for (Iterator<TemplateVersionMap> iter = tableModel.getDataList().iterator(); iter.hasNext();) {
				TemplateVersionMap map = iter.next();
				map.setVersion((String) defVersionCombo.getSelectedItem());
			}
			tableModel.refreshData();
		}
	}


	private final JComboBox defVersionCombo;
	private final TemplateVersionMappingTable versionMapTable;
	private final TemplateVersionMappingTableModel tableModel;

	public TemplatePanel() {
		defVersionCombo = new JComboBox();
		defVersionCombo.setEditable(true);
		defVersionCombo.addItem(GridTemplate.DEFAULT_VERSION);
		tableModel = new TemplateVersionMappingTableModel();
		versionMapTable = new TemplateVersionMappingTable(tableModel);
		setEnabled(false);
	}

	private void setEnabled(boolean enabled) {
		versionMapTable.setEnabled(enabled);
	}

	protected void preprocess(DBConnInfo connInfo) {
		// load template that does not have versions
		try {
			List<TemplateVersionMap> mapList = MigrationWorker.getInstance().preprocessTemplates(DBConnectionFactory.getInstance().getConnection(connInfo));
			versionMapTable.setDataList(mapList);

			setEnabled(!mapList.isEmpty());
		}
		catch (Exception ex) {
			ex.printStackTrace();
			SwingUtil.showWarning("Failed to retrieve template data from " + connInfo + ": " + ex.getMessage());
		}
	}

	protected void migrate(DBConnInfo connInfo) {
		try {
			setEnabled(false);

			List<TemplateVersionMap> mapList = tableModel.getDataList();
			if (mapList.isEmpty()) {
				SwingUtil.showInfo("No template versions to process. No update was made.");
			}
			else {
				int total = MigrationWorker.getInstance().migrateTemplateVersions(DBConnectionFactory.getInstance().getConnection(connInfo), mapList);

				SwingUtil.showInfo("Successfully migrated template verions: updated " + total + " templates.");
			}
		}
		catch (Exception ex) {
			ex.printStackTrace();
			SwingUtil.showWarning("Failed to retrieve template data from " + connInfo + ": " + ex.getMessage());
		}
	}

	protected void initPanel() {
		// layout components 
		GridBagLayout bag = new GridBagLayout();
		panel.setLayout(bag);

		GridBagConstraints c = new GridBagConstraints();
		c.weighty = 0.0;
		c.gridheight = 1;
		c.anchor = GridBagConstraints.NORTHWEST;
		c.fill = GridBagConstraints.BOTH;
		c.insets = new Insets(4, 4, 4, 4);

		c.gridwidth = GridBagConstraints.REMAINDER;
		c.weightx = 1.0;
		SwingUtil.addComponent(panel, bag, c, new JLabel("Specify version and parent ID for each template"));
		
		JPanel bp = new JPanel(new FlowLayout(FlowLayout.LEFT, 2, 2));
		JButton button = new JButton("Set All to Default Version");
		button.addActionListener(new SetToDefaultL());
		bp.add(button);

		c.gridwidth = GridBagConstraints.REMAINDER;
		c.weightx = 1.0;
		SwingUtil.addComponent(panel, bag, c, bp);

		c.gridwidth = 1;
		c.weightx = 0.0;
		SwingUtil.addComponent(panel, bag, c, new JLabel("Default Version:"));

		c.gridwidth = GridBagConstraints.REMAINDER;
		c.weightx = 1.0;
		SwingUtil.addComponent(panel, bag, c, defVersionCombo);

		c.gridwidth = GridBagConstraints.REMAINDER;
		c.weightx = 1.0;
		SwingUtil.addComponent(panel, bag, c, new JLabel("Template Version Mapping:"));

		c.gridwidth = GridBagConstraints.REMAINDER;
		c.weightx = 1.0;
		c.weighty = 1.0;
		SwingUtil.addComponent(panel, bag, c, new JScrollPane(versionMapTable));
	}
}