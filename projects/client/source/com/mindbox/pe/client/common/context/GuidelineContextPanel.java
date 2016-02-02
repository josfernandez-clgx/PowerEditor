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
import com.mindbox.pe.common.config.CategoryTypeDefinition;
import com.mindbox.pe.common.config.EntityTypeDefinition;
import com.mindbox.pe.model.GenericCategory;
import com.mindbox.pe.model.GenericEntity;
import com.mindbox.pe.model.GenericEntityType;
import com.mindbox.pe.model.GuidelineContext;

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
public final class GuidelineContextPanel extends DefaultGuidelineContextHolderForClient implements GuidelineContextHolderForClient,
		GuidelineContextProvider {

	private class EditContextL implements ActionListener {

	    public void actionPerformed(ActionEvent e) {
	        editContextButton.setEnabled(false);
	        try {
	            if (hasProductionRestrictions) {
                    ClientUtil.getInstance().showWarning("msg.warning.context.production.activation", 
                            new Object[] { ClientUtil.getHighestStatusDisplayLabel() });
	            } else {
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

	private final Map<GenericEntityType, JList> genericEntityJListMap;
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
	 * Equivalent to <code>GuidelineContextPanel(allowEdit, false)</code>.
	 * 
	 */
	public GuidelineContextPanel(String editButtonLabelKey, boolean allowEdit) {
		this(editButtonLabelKey, allowEdit, false, false);
	}

    /**
     * 
     */
    public GuidelineContextPanel(String editButtonLabelKey, boolean allowEdit, boolean verticalOrientation) {
        this(editButtonLabelKey, allowEdit, verticalOrientation, false);
    }
    
	public GuidelineContextPanel(String editButtonLabelKey, boolean allowEdit, 
            boolean verticalOrientation, boolean showSearchOptions) {
		this.allowEdit = allowEdit;
        this.showSearchOptions = showSearchOptions;

		genericEntityJListMap = new HashMap<GenericEntityType, JList>();
		genericEntityCatLabelMap = new HashMap<GenericEntityType, JLabel>();
		genericEntityLabelMap = new HashMap<GenericEntityType, String[]>();

		editContextButton = (allowEdit ? UIFactory.createJButton(editButtonLabelKey, null, new EditContextL(), null) : null);

		panel = UIFactory.createBorderLayoutPanel(0, 0);
		initPanel(verticalOrientation);
	}

	public JPanel getJPanel() {
		return panel;
	}

	public final void setEditContextEnabled(boolean enabled) {
        allowEdit = enabled;
		if (editContextButton != null)
			editContextButton.setEnabled(enabled);
	}

	private void initPanel(boolean verticalOrientation) {
		Dimension listPrefSize = new Dimension(40, 48);
		JScrollPane scrollPane = null;

		List<JPanel> contextPanelList = new ArrayList<JPanel>();

		// add generic entities
		EntityTypeDefinition[] entityTypes = ClientUtil.getEntityConfiguration().getEntityTypeDefinitions();
		for (int i = 0; i < entityTypes.length; i++) {
			if (entityTypes[i].useInContext()) {
				GenericEntityType type = GenericEntityType.forID(entityTypes[i].getTypeID());
				CategoryTypeDefinition catDef = ClientUtil.getEntityConfiguration().getCategoryDefinition(type);
				genericEntityLabelMap.put(type, new String[] { ClientUtil.getInstance().getLabel(type),
						(catDef == null ? "Category" : ClientUtil.getInstance().getLabel(catDef)) });

				genericEntityCatLabelMap.put(type, new JLabel(getGenericCategoryOrEntityLabel(type)));

				JPanel contextPanel = UIFactory.createBorderLayoutPanel(2, 2);
				contextPanel.add(genericEntityCatLabelMap.get(type), BorderLayout.NORTH);
				scrollPane = new JScrollPane(getGenericEntityList(type));
				scrollPane.setPreferredSize(listPrefSize);
				contextPanel.add(scrollPane, BorderLayout.CENTER);

				contextPanelList.add(contextPanel);
			}
		}

		JPanel contextPanel = (verticalOrientation ? UIFactory.createJPanel(new GridLayout(contextPanelList.size(), 1, 2, 2))
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
                
            } else {
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

	public void addContext(GenericEntity[] entities) {
		if (entities.length > 0) {
			super.addContext(entities);
			genericEntityCatLabelMap.get(entities[0].getType()).setText(genericEntityLabelMap.get(entities[0].getType())[0]);
		}
	}

	public synchronized void addContext(GenericCategory[] categories) {
		if (categories.length > 0) {
			super.addContext(categories);
			GenericEntityType type = GenericEntityType.forCategoryType(categories[0].getType());
			genericEntityCatLabelMap.get(type).setText(genericEntityLabelMap.get(type)[1]);
		}
	}

	private JList getGenericEntityList(final GenericEntityType type) {
		if (genericEntityJListMap.containsKey(type)) {
			return genericEntityJListMap.get(type);
		}
		else {
			JList list = UIFactory.createList(getGenenricEntityListModel(type));
			list.setCellRenderer(new CategoryOrEntityCellRenderer());
			list.addMouseListener(new MouseAdapter() {

				public void mouseClicked(MouseEvent e) {
					if (allowEdit && e.getClickCount() == 2) {
                        if (hasProductionRestrictions) {
                            ClientUtil.getInstance().showWarning("msg.warning.context.production.activation", 
                                    new Object[] { ClientUtil.getHighestStatusDisplayLabel() });
                        } else {
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

	private void removeSelectedGenericEntities(GenericEntityType type) {
		GenericEntity[] entities = getSelectedGenericEntities(type);
		if (entities != null && entities.length > 0) {
			removeContext(entities);
		}
	}

	private void removeSelectedGenericCategories(GenericEntityType type) {
		CategoryTypeDefinition catTypeDef = ClientUtil.getEntityConfiguration().getCategoryDefinition(type);
		if (catTypeDef != null) {
			GenericCategory[] categories = getSelectedGenericCategories(catTypeDef.getTypeID());
			if (categories != null && categories.length > 0) {
				removeContext(categories);
			}
		}
	}

	public void removeContext(GenericEntity[] entities) {
		super.removeContext(entities);
		if (entities != null && entities.length > 0) {
			DefaultListModel model = getGenenricEntityListModel(entities[0].getType());
			if (model.isEmpty()) {
				genericEntityCatLabelMap.get(entities[0].getType()).setText(getGenericCategoryOrEntityLabel(entities[0].getType()));
			}
		}
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

	public void removeContext(GenericCategory[] categories) {
		super.removeContext(categories);
		if (categories != null && categories.length > 0) {
			GenericEntityType type = ClientUtil.getEntityConfiguration().findEntityTypeForCategoryType(categories[0].getType());
			if (type == null) {
				throw new IllegalArgumentException("Cannot add categories: invalid type " + categories[0].getID());
			}
			DefaultListModel model = getGenenricEntityListModel(type);
			if (model.isEmpty()) {
				genericEntityCatLabelMap.get(type).setText(getGenericCategoryOrEntityLabel(type));
			}
			fireContextChangeEvent();
		}
	}

	synchronized GenericEntity[] getSelectedGenericEntities(GenericEntityType type) {
		List<Object> list = new ArrayList<Object>();
		JList element = genericEntityJListMap.get(type);
		Object[] objs = element.getSelectedValues();
		for (int i = 0; i < objs.length; i++) {
			if (objs[i] instanceof GenericEntity) {
				list.add(objs[i]);
			}
		}
		return list.toArray(new GenericEntity[0]);
	}

	synchronized GenericEntity[] getSelectedGenericEntities() {
		List<Object> list = new ArrayList<Object>();
		for (Iterator<JList> iter = genericEntityJListMap.values().iterator(); iter.hasNext();) {
			JList element = iter.next();
			Object[] objs = element.getSelectedValues();
			for (int i = 0; i < objs.length; i++) {
				if (objs[i] instanceof GenericEntity) {
					list.add(objs[i]);
				}
			}
		}
		return list.toArray(new GenericEntity[0]);
	}

	synchronized GenericCategory[] getSelectedGenericCategories(int categoryType) {
		GenericEntityType type = ClientUtil.getEntityConfiguration().findEntityTypeForCategoryType(categoryType);
		DefaultListModel model = getGenenricEntityListModel(type);
		if (model.isEmpty() || !hasGenericCategoryContext(type)) {
			return new GenericCategory[0];
		}
		else {
			Object[] objs = getGenericEntityList(type).getSelectedValues();
			GenericCategory[] categories = new GenericCategory[objs.length];
			for (int i = 0; i < categories.length; i++) {
				categories[i] = (GenericCategory) objs[i];
			}
			return categories;
		}
	}

	synchronized GenericCategory[] getSelectedGenericCategories() {
		List<Object> result = new ArrayList<Object>();
		for (Iterator<JList> iter = genericEntityJListMap.values().iterator(); iter.hasNext();) {
			JList list = iter.next();
			Object[] objs = list.getSelectedValues();
			for (int i = 0; i < objs.length; i++) {
				if (objs[i] instanceof GenericCategory) {
					result.add(objs[i]);
				}
			}
		}
		return result.toArray(new GenericCategory[0]);
	}

	public synchronized void clearContext() {
		super.clearContext();
		for (Map.Entry<GenericEntityType, JLabel> entry : genericEntityCatLabelMap.entrySet()) {
			entry.getValue().setText(getGenericCategoryOrEntityLabel(entry.getKey()));
		}
	}

    public boolean includeEmptyContexts() {
        return includeEmptyContextsCheckbox.isSelected();
    }

    public boolean includeParentCategories() {
        return includeParentsCheckbox.isSelected();
    }

    public boolean searchInColumnCheckbox() {
        return searchInColumnCheckbox.isSelected();
    }

    public boolean includeChildrenCategories() {
        return includeChildrenCheckbox.isSelected();
    }
    
    public void clearSearchOptions() {
        includeChildrenCheckbox.setSelected(false);
        includeParentsCheckbox.setSelected(false);
        searchInColumnCheckbox.setSelected(false);
        includeEmptyContextsCheckbox.setSelected(false);
    }

    public void setHasProductionRestrictions(boolean hasProductionRestrictions) {
        this.hasProductionRestrictions = hasProductionRestrictions;
    }
    
}