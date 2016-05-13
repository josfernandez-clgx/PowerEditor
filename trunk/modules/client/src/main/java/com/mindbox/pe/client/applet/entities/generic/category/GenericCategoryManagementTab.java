/*
 * Created on 2004. 2. 24.
 *
 */
package com.mindbox.pe.client.applet.entities.generic.category;

import javax.swing.JTabbedPane;

import com.mindbox.pe.client.ClientUtil;
import com.mindbox.pe.client.applet.PowerEditorSwingTheme;
import com.mindbox.pe.client.applet.entities.generic.GenericEntityManagementPanel;
import com.mindbox.pe.xsd.config.CategoryType;

/**
 * @author Geneho Kim
 * @author MindBox
 * @since PowerEditor 2.4.0
 */
public class GenericCategoryManagementTab extends JTabbedPane {
	/**
	 * 
	 */
	private static final long serialVersionUID = -3951228734910107454L;

	/**
	 * 
	 */
	// TT 2021 added parameter function genericEntityManagementPanel
	public GenericCategoryManagementTab(CategoryType categoryDef, boolean hasEditEntityPrivilege, GenericEntityManagementPanel genericEntityManagementPanel) {
		super();
		setFocusable(false);
		setFont(PowerEditorSwingTheme.smallTabFont);
		addTab(ClientUtil.getInstance().getLabel("tab.navigate"), new ManageGenericCategoryPanel(categoryDef.getTypeID().intValue(), hasEditEntityPrivilege, genericEntityManagementPanel));
	}


}
