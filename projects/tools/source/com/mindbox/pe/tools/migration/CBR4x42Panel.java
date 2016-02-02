/*
 * Created on 2004. 12. 20.
 *
 */
package com.mindbox.pe.tools.migration;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.JScrollPane;

import com.mindbox.pe.tools.db.DBConnInfo;
import com.mindbox.pe.tools.db.DBConnectionFactory;
import com.mindbox.pe.tools.util.SwingUtil;


/**
 * Panel for Guideline migration between 4.1.x and 4.2. 
 * @author Geneho Kim
 * @since PowerEditor 4.2.0
 */
class CBR4x42Panel extends AbstractMigrationDetailPanel {
	
	private final ActivationDateMappingTable table;
	private final ActivationDateMappingTableModel tableModel;

	public CBR4x42Panel() {
		tableModel = new ActivationDateMappingTableModel();
		table = new ActivationDateMappingTable(tableModel);
		table.setEnabled(false);
	}
	
	protected void initPanel() {
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
		SwingUtil.addComponent(panel, bag, c, new JLabel("CBR Date Mapping:"));

		c.gridwidth = GridBagConstraints.REMAINDER;
		c.weightx = 1.0;
		c.weighty = 1.0;
		SwingUtil.addComponent(panel, bag, c, new JScrollPane(table));
	}

	protected void preprocess(DBConnInfo connInfo) {
		// load guideline dates
		try {
			List<GuidelineDateMap> mapList = MigrationWorker.getInstance().preprocessCBRDates(DBConnectionFactory.getInstance().getConnection(connInfo));
			tableModel.setDataList(mapList);

			setEnabled(!mapList.isEmpty());
		}
		catch (Exception ex) {
			ex.printStackTrace();
			SwingUtil.showWarning("Failed to retrieve CBR data from " + connInfo + ": " + ex.getMessage());
		}
	}

	protected void migrate(DBConnInfo connInfo) {
		try {
			setEnabled(false);

			List<GuidelineDateMap> mapList = tableModel.getDataList();
			
			int[] totals = MigrationWorker.getInstance().migrateCBRDates(DBConnectionFactory.getInstance().getConnection(connInfo), mapList);
			
			SwingUtil.showWarning("Successfully migrated CBR dates: created " + totals[0] + " date synonyms; migrated " + totals[1] + " case bases and " + totals[2] + " cases.");
		}
		catch (Exception ex) {
			ex.printStackTrace();
			SwingUtil.showWarning("Failed to retrieve CBR data from " + connInfo + ": " + ex.getMessage());
		}
	}

	private void setEnabled(boolean enabled) {
		table.setEnabled(enabled);
	}
}
