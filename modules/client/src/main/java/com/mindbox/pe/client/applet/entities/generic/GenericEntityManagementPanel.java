package com.mindbox.pe.client.applet.entities.generic;

import java.awt.event.ActionEvent;
import java.util.List;

import javax.swing.JPanel;

import com.mindbox.pe.client.ClientUtil;
import com.mindbox.pe.client.applet.entities.EntityManagementButtonPanel;
import com.mindbox.pe.client.common.AbstractThreadedActionAdapter;
import com.mindbox.pe.client.common.ThreeTierPanel;
import com.mindbox.pe.client.common.filter.AbstractFilterPanel;
import com.mindbox.pe.client.common.filter.IFilterSubpanel;
import com.mindbox.pe.client.common.filter.NavigationPanel;
import com.mindbox.pe.client.common.selection.AbstractSelectionPanel;
import com.mindbox.pe.model.GenericEntity;
import com.mindbox.pe.model.GenericEntityType;

/**
 * @author Geneho Kim
 * @author MindBox
 * @since PowerEditor 3.0.0
 */
public class GenericEntityManagementPanel extends ThreeTierPanel<GenericEntity, EntityManagementButtonPanel<GenericEntity>> {
	/**
	 * 
	 */
	private static final long serialVersionUID = -3951228734910107454L;

	// TT 2021
	private final GenericEntityFilterPanel genericEntityFilterPanel;

	public static GenericEntityManagementPanel createInstance(GenericEntityType type, int categoryType, boolean canClone, boolean readOnly) {
		GenericEntityDetailPanel detailPanel = new GenericEntityDetailPanel(type);
		GenericEntitySelectionPanel selectionPanel = GenericEntitySelectionPanel.createInstance(type, canClone, detailPanel, readOnly);
		GenericEntityFilterPanel filterPanel = new GenericEntityFilterPanel(type, selectionPanel);
		detailPanel.setSelectionPanel(selectionPanel);

		GenericEntityManagementPanel genericEntityPanel = new GenericEntityManagementPanel(type, categoryType, filterPanel, selectionPanel, detailPanel);
		return genericEntityPanel;
	}

	private class FetchEntitiesListener extends AbstractThreadedActionAdapter {
		public void performAction(ActionEvent actionevent) throws Exception {
			List<GenericEntity> list = subpanel.doFilter();
			selectionPanel.populate(list);
		}

		private IFilterSubpanel<GenericEntity> subpanel;

		FetchEntitiesListener(IFilterSubpanel<GenericEntity> ifiltersubpanel) {
			subpanel = ifiltersubpanel;
		}
	}

	/**
	 * @param entityType
	 * @param filterPanel
	 * @param selectionPanel
	 * @param workPanel
	 */
	private GenericEntityManagementPanel(GenericEntityType entityType, int categoryType, AbstractFilterPanel<GenericEntity, EntityManagementButtonPanel<GenericEntity>> filterPanel,
			AbstractSelectionPanel<GenericEntity, EntityManagementButtonPanel<GenericEntity>> selectionPanel, JPanel workPanel) {
		super(entityType, filterPanel, selectionPanel, workPanel);
		this.genericEntityFilterPanel = (GenericEntityFilterPanel) filterPanel;
		boolean needNavigationPanel = (categoryType > 0);
		if (needNavigationPanel) {
			NavigationPanel navigationPanel = new NavigationPanel((categoryType > 0), false, entityType, categoryType);
			navigationPanel.build();
			navigationPanel.addActionListener(new FetchEntitiesListener(navigationPanel));
			super.tabPane.addTab(
					ClientUtil.getInstance().getLabel(AbstractFilterPanel.NAVIGATE_FILTER_LBL),
					null,
					navigationPanel,
					ClientUtil.getInstance().getLabel(AbstractFilterPanel.NAVIGATE_FILTER_TOOLTIP));
		}
	}

	public GenericEntityFilterPanel getGenericEntityFilterPanel() {
		return genericEntityFilterPanel;
	}

}
