/*
 * Created on 2005. 3. 10.
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
 * @author Geneho Kim
 * @since PowerEditor 
 */
public class GuidelineActionParameterPanel extends AbstractMigrationDetailPanel {

	private final GuidelineActionParameterTableModel tableModel;
	private final GuidelineActionParameterTable table;

	public GuidelineActionParameterPanel() {
		tableModel = new GuidelineActionParameterTableModel();
		table = new GuidelineActionParameterTable(tableModel);
	}

	private void setEnabled(boolean enabled) {
		table.setEnabled(enabled);
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
		SwingUtil.addComponent(panel, bag, c, new JLabel("Guideline Action Parameters to Migrate"));

		c.gridwidth = GridBagConstraints.REMAINDER;
		c.weightx = 1.0;
		c.weighty = 1.0;
		SwingUtil.addComponent(panel, bag, c, new JScrollPane(table));
	}

	public void preprocess(DBConnInfo connInfo) {
		try {
			List<GuidelineActionParameterRow> paramList = MigrationWorker.getInstance().preprocessGuidelineActionParameters(
					DBConnectionFactory.getInstance().getConnection(connInfo));
			table.setDataList(paramList);

			setEnabled(!paramList.isEmpty());
		}
		catch (Exception ex) {
			ex.printStackTrace();
			SwingUtil.showWarning("Failed to retrieve guideline action parameters from " + connInfo + ": " + ex.getMessage());
		}
	}

	public void migrate(DBConnInfo connInfo) {
		try {
			setEnabled(false);

			List<GuidelineActionParameterRow> paramList = tableModel.getDataList();
			if (paramList.isEmpty()) {
				SwingUtil.showInfo("No guideline action parameters to process. No update was made.");
			}
			else {
				MigrationWorker.getInstance().migrateGuidelineActionParameters(DBConnectionFactory.getInstance().getConnection(connInfo), paramList);

				SwingUtil.showInfo("Successfully migrated guideline action parameters: updated " + paramList.size() + " parameters.");
			}
		}
		catch (Exception ex) {
			ex.printStackTrace();
			SwingUtil.showWarning("Failed to retrieve template data from " + connInfo + ": " + ex.getMessage());
		}
	}

}