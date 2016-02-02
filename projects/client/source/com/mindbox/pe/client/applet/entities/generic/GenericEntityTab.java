/*
 * Created on 2004. 5. 5.
 *  
 */
package com.mindbox.pe.client.applet.entities.generic;

import com.mindbox.pe.client.ClientUtil;
import com.mindbox.pe.client.applet.PowerEditorSwingTheme;
import com.mindbox.pe.client.applet.UIFactory;
import com.mindbox.pe.client.applet.entities.generic.category.GenericCategoryManagementTab;
import com.mindbox.pe.client.common.tab.PowerEditorTab;
import com.mindbox.pe.common.config.CategoryTypeDefinition;
import com.mindbox.pe.model.GenericEntityType;


/**
 * Generic Entity Tab.
 * @author Geneho
 * @since PowerEditor 3.1.0
 */
public class GenericEntityTab extends PowerEditorTab {

	public static GenericEntityTab createInstance(GenericEntityType type, boolean canClone, boolean hasEditEntityPrivilege) {
		GenericEntityTab instance = new GenericEntityTab(type, canClone, hasEditEntityPrivilege);
		return instance;
	}


	private GenericEntityTab(GenericEntityType type, boolean canClone, boolean hasEditEntityPrivilege) {
		UIFactory.setLookAndFeel(this);
		setFont(PowerEditorSwingTheme.boldFont);

		CategoryTypeDefinition categoryDef = ClientUtil.getEntityConfiguration().getCategoryDefinition(type);

		// TT 2021
		GenericEntityManagementPanel genericEntityManagementPanel = GenericEntityManagementPanel.createInstance(type, (categoryDef == null
				? -1
				: categoryDef.getTypeID()), canClone, !hasEditEntityPrivilege);

		// add entity management tab
		addTab(
				ClientUtil.getInstance().getLabel(type),
				ClientUtil.getInstance().makeImageIcon("image.blank"),
				genericEntityManagementPanel,
				null);

		// add category management tab, if necessary
		if (categoryDef != null) {
			addTab(
					ClientUtil.getInstance().getLabel(categoryDef),
					ClientUtil.getInstance().makeImageIcon("image.blank"),
					new GenericCategoryManagementTab(categoryDef, hasEditEntityPrivilege, genericEntityManagementPanel),
					null);
		}

	}
}