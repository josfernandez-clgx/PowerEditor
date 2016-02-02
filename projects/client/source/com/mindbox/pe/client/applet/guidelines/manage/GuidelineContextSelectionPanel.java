/*
 * Created on Oct 9, 2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package com.mindbox.pe.client.applet.guidelines.manage;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;

import com.mindbox.pe.client.ClientUtil;
import com.mindbox.pe.client.EntityModelCacheFactory;
import com.mindbox.pe.client.applet.UIFactory;
import com.mindbox.pe.client.common.IDNameObjectCellRenderer;
import com.mindbox.pe.client.common.PanelBase;
import com.mindbox.pe.client.common.tree.GenericCategorySelectionTree;
import com.mindbox.pe.common.GuidelineContextHolder;
import com.mindbox.pe.common.config.CategoryTypeDefinition;
import com.mindbox.pe.common.config.EntityTypeDefinition;
import com.mindbox.pe.model.GenericCategory;
import com.mindbox.pe.model.GenericEntity;
import com.mindbox.pe.model.GenericEntityType;

/**
 * Guideline context selection panel.
 * 
 * @author Gene Kim
 * @author MindBox
 * @since PowerEditor 4.2.0
 */
public final class GuidelineContextSelectionPanel extends PanelBase {

	private final GuidelineContextHolder contextHolder;

	private final JTabbedPane tabPane;

	private final Map<GenericEntityType, JList> genericEntityJListMap;

	private final Map<GenericEntityType, GenericCategorySelectionTree> genericCategoryTreeMap;

	public GuidelineContextSelectionPanel(GuidelineContextHolder contextHolder) {
		this.contextHolder = contextHolder;

		this.tabPane = new JTabbedPane(JTabbedPane.TOP);

		genericEntityJListMap = new HashMap<GenericEntityType, JList>();
		genericCategoryTreeMap = new HashMap<GenericEntityType, GenericCategorySelectionTree>();

		initPanel();
	}

	private JList getGenericEntityList(GenericEntityType type) {
		if (genericEntityJListMap.containsKey(type)) {
			return genericEntityJListMap.get(type);
		}
		else {
			final GenericEntityType genericEntityType = type;
			JList list = UIFactory.createList(EntityModelCacheFactory.getInstance().getGenericEntityListModel(genericEntityType, false));
			list.setCellRenderer(new IDNameObjectCellRenderer("image.node.entity"));
			list.addMouseListener(new MouseAdapter() {

				public void mouseClicked(MouseEvent e) {
					if (e.getClickCount() == 2) {
						addSelectedGenericEntities(genericEntityType);
					}
				}
			});
			genericEntityJListMap.put(genericEntityType, list);
			return list;
		}
	}

	private GenericCategorySelectionTree getGenericCategoryTree(GenericEntityType type) {
		if (genericCategoryTreeMap.containsKey(type)) {
			return genericCategoryTreeMap.get(type);
		}
		else {
			CategoryTypeDefinition catDef = ClientUtil.getEntityConfiguration().getCategoryDefinition(type);
			final GenericEntityType genericEntityType = type;
			GenericCategorySelectionTree tree = new GenericCategorySelectionTree(catDef.getTypeID(), false, true, true);
			tree.addMouseListener(new MouseAdapter() {

				public void mouseClicked(MouseEvent e) {
					if (e.getClickCount() == 2) {
						addSelectedGenericCategories(genericEntityType);
					}
				}
			});
			genericCategoryTreeMap.put(genericEntityType, tree);
			return tree;
		}
	}

	private void addSelectedGenericEntities(GenericEntityType type) {
		GenericEntity[] entities = getSelectedGenericEntities(type);
		if (entities != null && entities.length > 0) {
			try {
				contextHolder.addContext(entities);
			}
			catch (IllegalArgumentException ex) {
				ClientUtil.getInstance().showWarning("msg.warning.invalid.context.generic.entity");
			}
		}
	}

	private void addSelectedGenericCategories(GenericEntityType type) {
		GenericCategory[] entities = getSelectedGenericCategories(type);
		if (entities != null && entities.length > 0) {
			try {
				contextHolder.addContext(entities);
			}
			catch (IllegalArgumentException ex) {
				ClientUtil.getInstance().showWarning("msg.warning.invalid.context.generic.category");
			}
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

	synchronized GenericCategory[] getSelectedGenericCategories(GenericEntityType type) {
		GenericCategorySelectionTree tree = genericCategoryTreeMap.get(type);
		return (tree == null ? new GenericCategory[0] : tree.getSelectedCategories());
	}

	private void initPanel() {

		// add generic entities
		EntityTypeDefinition[] entityTypes = ClientUtil.getEntityConfiguration().getEntityTypeDefinitions();
		for (int i = 0; i < entityTypes.length; i++) {
			if (entityTypes[i].useInContext()) {
				final GenericEntityType type = GenericEntityType.forID(entityTypes[i].getTypeID());

				JButton addButton = UIFactory.createButton(
						ClientUtil.getInstance().getLabel("button.add.context"),
						"image.btn.small.forward",
						new ActionListener() {

							public void actionPerformed(ActionEvent e) {
								GenericEntity[] entities = getSelectedGenericEntities(type);
								if (entities == null || entities.length == 0) {
									ClientUtil.getInstance().showWarning("msg.warning.select.generic");
								}
								else {
									try {
										contextHolder.addContext(entities);
									}
									catch (IllegalArgumentException ex) {
										ClientUtil.getInstance().showWarning("msg.warning.invalid.context.generic.entity");
									}
								}
							}
						},
						null);

				JPanel contextPanel = UIFactory.createBorderLayoutPanel(2, 2);
				contextPanel.add(addButton, BorderLayout.NORTH);
				contextPanel.add(new JScrollPane(getGenericEntityList(type)), BorderLayout.CENTER);

				tabPane.add(ClientUtil.getInstance().getLabel(type), contextPanel);

				// add category selection, if configured
				CategoryTypeDefinition categoryDef = ClientUtil.getEntityConfiguration().getCategoryDefinition(type);
				if (categoryDef != null) {
					tabPane.add(
							ClientUtil.getInstance().getLabel(ClientUtil.getEntityConfiguration().getCategoryDefinition(type)),
							createCategoryContextPanel(type));
				}
			}
		}

		tabPane.setFocusable(false);

		setLayout(new BorderLayout(0, 0));
		add(tabPane, BorderLayout.CENTER);

		setBorder(UIFactory.createTitledBorder(ClientUtil.getInstance().getLabel("label.title.guideline.context.selection")));
	}

	private JPanel createCategoryContextPanel(final GenericEntityType type) {
		JButton addButton = UIFactory.createButton(
				ClientUtil.getInstance().getLabel("button.add.context"),
				"image.btn.small.forward",
				new ActionListener() {

					public void actionPerformed(ActionEvent e) {
						GenericCategory[] catgories = getSelectedGenericCategories(type);
						if (catgories == null || catgories.length == 0) {
							ClientUtil.getInstance().showWarning("msg.warning.select.generic");
						}
						else {
							try {
								contextHolder.addContext(catgories);
							}
							catch (IllegalArgumentException ex) {
								ClientUtil.getInstance().showWarning("msg.warning.invalid.context.generic.category");
							}

						}
					}
				},
				null);
		JPanel categoryContextPanel = UIFactory.createBorderLayoutPanel(2, 2);
		categoryContextPanel.add(addButton, BorderLayout.NORTH);
		categoryContextPanel.add(getGenericCategoryTree(type).getJComponent(), BorderLayout.CENTER);
		return categoryContextPanel;
	}
}