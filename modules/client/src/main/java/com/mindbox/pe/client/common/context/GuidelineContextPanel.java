package com.mindbox.pe.client.common.context;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import com.mindbox.pe.client.ClientUtil;
import com.mindbox.pe.client.applet.UIFactory;
import com.mindbox.pe.client.common.CategoryOrEntityCellRenderer;
import com.mindbox.pe.client.common.dialog.GuidelineContextDialog;
import com.mindbox.pe.common.GuidelineContextProvider;
import com.mindbox.pe.common.config.ConfigUtil;
import com.mindbox.pe.model.GenericCategory;
import com.mindbox.pe.model.GenericContextElement;
import com.mindbox.pe.model.GenericEntity;
import com.mindbox.pe.model.GenericEntityType;
import com.mindbox.pe.model.GuidelineContext;
import com.mindbox.pe.xsd.config.CategoryType;
import com.mindbox.pe.xsd.config.EntityType;

/**
 * Panel with guideline context information. As of PowerEditor 4.2.0, this allows modifying
 * guideline context. To use a non-GUI implementation of
 * {@link com.mindbox.pe.common.GuidelineContextHolder}, use concrete implementations of
 * {@link com.mindbox.pe.common.AbstractGuidelineContextHolder}.
 * This panel is also used to selecting entites for searching purposes - the entities may
 * not nessessarily be part of a context.
 * 
 * @author Gene Kim
 * @author MindBox
 * @since PowerEditor 2.0
 */
public final class GuidelineContextPanel extends DefaultGuidelineContextHolderForClient implements GuidelineContextHolderForClient, GuidelineContextProvider {

	private class EditContextL implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			editContextButton.setEnabled(false);
			try {
				if (hasProductionRestrictions) {
					ClientUtil.getInstance().showWarning("msg.warning.context.production.activation", new Object[] { ClientUtil.getHighestStatusDisplayLabel() });
				}
				else {
					GuidelineContext[] modifiedContexts = GuidelineContextDialog.editContext(getGuidelineContexts());
					if (modifiedContexts != null) {
						setContextElemens(modifiedContexts);
					}
				}
			}
			finally {
				editContextButton.setEnabled(true);
			}
		}
	}

	private final Map<GenericEntityType, JList<GenericContextElement>> genericEntityJListMap;
	private final Map<GenericEntityType, JLabel> genericEntityCatLabelMap;
	private final Map<GenericEntityType, String[]> genericEntityLabelMap;
	private boolean allowEdit, hasProductionRestrictions;
	private final boolean showSearchOptions;
	private final JButton editContextButton;
	private final JPanel panel;
	private JCheckBox includeEmptyContextsCheckbox;
	private JCheckBox includeParentsCheckbox;
	private JCheckBox includeChildrenCheckbox;
	private JCheckBox searchInColumnCheckbox;

	/**
	 * Equivalent to <code>GuidelineContextPanel(editButtonLabelKey, allowEdit, false, false)</code>.
	 * @param editButtonLabelKey editButtonLabelKey
	 * @param allowEdit allowEdit
	 */
	public GuidelineContextPanel(String editButtonLabelKey, boolean allowEdit) {
		this(editButtonLabelKey, allowEdit, false, false);
	}

	public GuidelineContextPanel(String editButtonLabelKey, boolean allowEdit, boolean verticalOrientation) {
		this(editButtonLabelKey, allowEdit, verticalOrientation, false);
	}

	public GuidelineContextPanel(String editButtonLabelKey, boolean allowEdit, boolean verticalOrientation, boolean showSearchOptions) {
		this.allowEdit = allowEdit;
		this.showSearchOptions = showSearchOptions;

		genericEntityJListMap = new HashMap<GenericEntityType, JList<GenericContextElement>>();
		genericEntityCatLabelMap = new HashMap<GenericEntityType, JLabel>();
		genericEntityLabelMap = new HashMap<GenericEntityType, String[]>();

		editContextButton = (allowEdit ? UIFactory.createJButton(editButtonLabelKey, null, new EditContextL(), null) : null);

		panel = UIFactory.createBorderLayoutPanel(0, 0);
		initPanel(verticalOrientation);
	}

	@Override
	public synchronized void addContext(GenericCategory[] categories) {
		if (categories.length > 0) {
			super.addContext(categories);
			GenericEntityType type = GenericEntityType.forCategoryType(categories[0].getType());
			genericEntityCatLabelMap.get(type).setText(genericEntityLabelMap.get(type)[1]);
		}
	}

	@Override
	public void addContext(GenericEntity[] entities) {
		if (entities.length > 0) {
			super.addContext(entities);
			genericEntityCatLabelMap.get(entities[0].getType()).setText(genericEntityLabelMap.get(entities[0].getType())[0]);
		}

	}

	@Override
	public synchronized void clearContext() {
		super.clearContext();
		for (Map.Entry<GenericEntityType, JLabel> entry : genericEntityCatLabelMap.entrySet()) {
			entry.getValue().setText(getGenericCategoryOrEntityLabel(entry.getKey()));
		}
	}

	public void clearSearchOptions() {
		includeChildrenCheckbox.setSelected(false);
		includeParentsCheckbox.setSelected(false);
		searchInColumnCheckbox.setSelected(false);
		includeEmptyContextsCheckbox.setSelected(false);
	}

	private String getGenericCategoryOrEntityLabel(GenericEntityType type) {
		String[] strs = genericEntityLabelMap.get(type);
		if (strs != null) {
			return strs[0] + '/' + strs[1];
		}
		else {
			return "Category/" + type;
		}
	}

	private JList<GenericContextElement> getGenericEntityList(final GenericEntityType type) {
		if (genericEntityJListMap.containsKey(type)) {
			return genericEntityJListMap.get(type);
		}
		else {
			JList<GenericContextElement> list = UIFactory.createList(getGenenricEntityListModel(type));
			list.setCellRenderer(new CategoryOrEntityCellRenderer());
			list.addMouseListener(new MouseAdapter() {

				@Override
				public void mouseClicked(MouseEvent e) {
					if (allowEdit && e.getClickCount() == 2) {
						if (hasProductionRestrictions) {
							ClientUtil.getInstance().showWarning("msg.warning.context.production.activation", new Object[] { ClientUtil.getHighestStatusDisplayLabel() });
						}
						else {
							removeSelectedGenericEntities(type);
							removeSelectedGenericCategories(type);
						}
					}
				}
			});
			genericEntityJListMap.put(type, list);
			return list;
		}
	}

	public JPanel getJPanel() {
		return panel;
	}

	synchronized GenericCategory[] getSelectedGenericCategories() {
		List<GenericCategory> result = new ArrayList<GenericCategory>();
		for (Iterator<JList<GenericContextElement>> iter = genericEntityJListMap.values().iterator(); iter.hasNext();) {
			JList<GenericContextElement> list = iter.next();
			for (GenericContextElement element : list.getSelectedValuesList()) {
				if (element instanceof GenericCategory) {
					result.add(GenericCategory.class.cast(element));
				}
			}
		}
		return result.toArray(new GenericCategory[0]);
	}

	synchronized GenericCategory[] getSelectedGenericCategories(int categoryType) {
		GenericEntityType type = ClientUtil.getEntityConfigHelper().findEntityTypeForCategoryType(categoryType);
		DefaultListModel<GenericContextElement> model = getGenenricEntityListModel(type);
		if (model.isEmpty() || !hasGenericCategoryContext(type)) {
			return new GenericCategory[0];
		}
		else {
			List<GenericContextElement> elements = getGenericEntityList(type).getSelectedValuesList();
			GenericCategory[] categories = new GenericCategory[elements.size()];
			for (int i = 0; i < categories.length; i++) {
				categories[i] = (GenericCategory) elements.get(i);
			}
			return categories;
		}
	}

	synchronized GenericEntity[] getSelectedGenericEntities() {
		List<GenericEntity> list = new ArrayList<GenericEntity>();
		for (Iterator<JList<GenericContextElement>> iter = genericEntityJListMap.values().iterator(); iter.hasNext();) {
			JList<GenericContextElement> jlist = iter.next();
			for (GenericContextElement element : jlist.getSelectedValuesList()) {
				if (element instanceof GenericEntity) {
					list.add(GenericEntity.class.cast(element));
				}
			}
		}
		return list.toArray(new GenericEntity[0]);
	}

	synchronized GenericEntity[] getSelectedGenericEntities(GenericEntityType type) {
		List<GenericEntity> list = new ArrayList<GenericEntity>();
		JList<GenericContextElement> jlist = genericEntityJListMap.get(type);
		for (GenericContextElement element : jlist.getSelectedValuesList()) {
			if (element instanceof GenericEntity) {
				list.add(GenericEntity.class.cast(element));
			}
		}
		return list.toArray(new GenericEntity[0]);
	}

	public boolean includeChildrenCategories() {
		return includeChildrenCheckbox.isSelected();
	}

	public boolean includeEmptyContexts() {
		return includeEmptyContextsCheckbox.isSelected();
	}

	public boolean includeParentCategories() {
		return includeParentsCheckbox.isSelected();
	}

	private void initPanel(boolean verticalOrientation) {
		Dimension listPrefSize = new Dimension(40, 48);
		JScrollPane scrollPane = null;

		List<JPanel> contextPanelList = new ArrayList<JPanel>();

		// add generic entities
		for (final EntityType entityType : ClientUtil.getEntityConfigHelper().getEntityTypeDefinitions()) {
			if (ConfigUtil.isUseInContext(entityType)) {
				GenericEntityType type = GenericEntityType.forID(entityType.getTypeID().intValue());
				CategoryType catDef = ClientUtil.getEntityConfigHelper().getCategoryDefinition(type);
				genericEntityLabelMap.put(
						type,
						new String[] { ClientUtil.getInstance().getLabel(type), (catDef == null ? "Category" : ClientUtil.getInstance().getLabel(catDef)) });

				genericEntityCatLabelMap.put(type, new JLabel(getGenericCategoryOrEntityLabel(type)));

				JPanel contextPanel = UIFactory.createBorderLayoutPanel(2, 2);
				contextPanel.add(genericEntityCatLabelMap.get(type), BorderLayout.NORTH);
				scrollPane = new JScrollPane(getGenericEntityList(type));
				scrollPane.setPreferredSize(listPrefSize);
				contextPanel.add(scrollPane, BorderLayout.CENTER);

				contextPanelList.add(contextPanel);
			}
		}

		JPanel contextPanel = (verticalOrientation
				? UIFactory.createJPanel(new GridLayout(contextPanelList.size(), 1, 2, 2))
				: UIFactory.createJPanel(new GridLayout(1, contextPanelList.size(), 2, 2)));

		for (int i = 0; i < contextPanelList.size(); i++) {
			contextPanel.add(contextPanelList.get(i));
		}

		if (allowEdit) {
			JPanel bp = null;
			if (showSearchOptions) {
				includeEmptyContextsCheckbox = UIFactory.createCheckBox("checkbox.include.emptycontexts");
				includeParentsCheckbox = UIFactory.createCheckBox("checkbox.include.parent");
				includeChildrenCheckbox = UIFactory.createCheckBox("checkbox.include.children");
				searchInColumnCheckbox = UIFactory.createCheckBox("checkbox.include.column");

				GridBagLayout bag = new GridBagLayout();
				bp = UIFactory.createJPanel(bag);
				GridBagConstraints c = new GridBagConstraints();
				c.insets.bottom = 0;
				c.insets.left = 0;
				c.insets.right = 0;
				c.insets.top = 0;
				c.anchor = GridBagConstraints.NORTHWEST;
				c.gridwidth = 1;
				c.weightx = 1.0;
				c.gridx = 0;
				c.gridy = 0;
				UIFactory.addComponent(bp, bag, c, editContextButton);
				c.fill = GridBagConstraints.BOTH;
				c.gridwidth = 2;
				c.gridx = 0;
				c.gridy = 1;

				UIFactory.addComponent(bp, bag, c, includeEmptyContextsCheckbox);
				c.gridx = 0;
				c.gridy = 2;

				UIFactory.addComponent(bp, bag, c, includeParentsCheckbox);
				c.gridx = 0;
				c.gridy = 3;
				UIFactory.addComponent(bp, bag, c, includeChildrenCheckbox);

				c.gridx = 0;
				c.gridy = 4;
				UIFactory.addComponent(bp, bag, c, searchInColumnCheckbox);

			}
			else {
				bp = UIFactory.createFlowLayoutPanelLeftAlignment(2, 2);
				bp.add(editContextButton);
			}
			panel.add(bp, BorderLayout.NORTH);
		}
		if (verticalOrientation) {
			int preferredHeight = (contextPanel.getPreferredSize().height + 6 > 269 ? 269 : contextPanel.getPreferredSize().height + 6);
			contextPanel.setMinimumSize(new Dimension(contextPanel.getMinimumSize().width, 28));
			JScrollPane contextScrollPane = new JScrollPane(contextPanel);
			contextScrollPane.setPreferredSize(new Dimension(contextPanel.getPreferredSize().width, preferredHeight));
			panel.add(contextScrollPane, BorderLayout.CENTER);
		}
		else {
			panel.add(contextPanel, BorderLayout.CENTER);
		}
	}

	@Override
	public void removeContext(GenericCategory[] categories) {
		super.removeContext(categories);
		if (categories != null && categories.length > 0) {
			GenericEntityType type = ClientUtil.getEntityConfigHelper().findEntityTypeForCategoryType(categories[0].getType());
			if (type == null) {
				throw new IllegalArgumentException("Cannot add categories: invalid type " + categories[0].getID());
			}
			DefaultListModel<GenericContextElement> model = getGenenricEntityListModel(type);
			if (model.isEmpty()) {
				genericEntityCatLabelMap.get(type).setText(getGenericCategoryOrEntityLabel(type));
			}
			fireContextChangeEvent();
		}
	}

	@Override
	public void removeContext(GenericEntity[] entities) {
		super.removeContext(entities);
		if (entities != null && entities.length > 0) {
			DefaultListModel<GenericContextElement> model = getGenenricEntityListModel(entities[0].getType());
			if (model.isEmpty()) {
				genericEntityCatLabelMap.get(entities[0].getType()).setText(getGenericCategoryOrEntityLabel(entities[0].getType()));
			}
		}
	}

	private void removeSelectedGenericCategories(GenericEntityType type) {
		CategoryType catTypeDef = ClientUtil.getEntityConfigHelper().getCategoryDefinition(type);
		if (catTypeDef != null) {
			GenericCategory[] categories = getSelectedGenericCategories(catTypeDef.getTypeID().intValue());
			if (categories != null && categories.length > 0) {
				removeContext(categories);
			}
		}
	}

	private void removeSelectedGenericEntities(GenericEntityType type) {
		GenericEntity[] entities = getSelectedGenericEntities(type);
		if (entities != null && entities.length > 0) {
			removeContext(entities);
		}
	}

	public boolean searchInColumnCheckbox() {
		return searchInColumnCheckbox.isSelected();
	}

	public final void setEditContextEnabled(boolean enabled) {
		allowEdit = enabled;
		if (editContextButton != null) editContextButton.setEnabled(enabled);
	}

	public void setHasProductionRestrictions(boolean hasProductionRestrictions) {
		this.hasProductionRestrictions = hasProductionRestrictions;
	}

}