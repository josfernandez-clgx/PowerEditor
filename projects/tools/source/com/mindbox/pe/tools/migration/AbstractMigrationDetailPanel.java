/*
 * Created on 2004. 12. 20.
 *
 */
package com.mindbox.pe.tools.migration;

import javax.swing.JPanel;

import com.mindbox.pe.tools.db.DBConnInfo;


/**
 * @author Geneho Kim
 * @since PowerEditor 
 */
public abstract class AbstractMigrationDetailPanel {
	
	protected JPanel panel = null;
	
	public final JPanel getPanel() {
		if (panel == null) {
			panel = new JPanel();
			initPanel();
		}
		return panel;
	}
	
	protected abstract void initPanel();
	
	protected abstract void preprocess(DBConnInfo connInfo);
	
	protected abstract void migrate(DBConnInfo connInfo);
	
}
