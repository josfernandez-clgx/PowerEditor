package com.mindbox.pe.client.applet.entities.generic;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;

import com.mindbox.pe.client.applet.UIFactory;
import com.mindbox.pe.client.common.event.ValueChangeEvent;
import com.mindbox.pe.client.common.event.ValueChangeListener;
import com.mindbox.pe.client.common.tree.GenericCategoryTreeWithCheckBox;
import com.mindbox.pe.model.GenericEntity;
import com.mindbox.pe.model.GenericEntityType;
import com.mindbox.pe.model.assckey.MutableTimedAssociationKey;
import com.mindbox.pe.model.assckey.TimedAssociationKey;


/**
 * Panel for managing the associations between generic entities and categories.
 * @author MindBox
 * @since PowerEditor 5.1.0 
 */
class GenericEntityToCategoryPanel extends JPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = -3951228734910107454L;

	private GenericEntity entity = null;
	private final GenericCategoryTreeWithCheckBox categoriesTreeField;
	private final GenericEntityToCategoryListPanel categoryAssociationsPanel;

	public GenericEntityToCategoryPanel(GenericEntityType entityType) {
		categoriesTreeField = new GenericCategoryTreeWithCheckBox(entityType, false, true);
		categoryAssociationsPanel = new GenericEntityToCategoryListPanel(entityType);
		categoryAssociationsPanel.addValueChangeListener(new ValueChangeListener() {
			public void valueChanged(ValueChangeEvent e) {
				List<Integer> categoryIDList = new ArrayList<Integer>();
				Date asOfDate = categoriesTreeField.getSelectedDate();
				if (categoryAssociationsPanel.getCategoryAssociations() != null) {
					for (Iterator<MutableTimedAssociationKey> i = categoryAssociationsPanel.getCategoryAssociations().iterator(); i.hasNext();) {
						TimedAssociationKey key = i.next();
						if (asOfDate == null
								|| ((key.getEffectiveDate() == null || !key.getEffectiveDate().getDate().after(asOfDate)) && (key.getExpirationDate() == null || !key.getExpirationDate().getDate().before(
										asOfDate)))) {
							categoryIDList.add(new Integer(key.getAssociableID()));
						}
					}
				}
				categoriesTreeField.setSelectedCategoriesAndEntities(categoryIDList, null);
			}
		});
		initDialog();
	}

	private void setCategorySelection(List<Integer> list) {
		categoriesTreeField.setSelectedCategoriesAndEntities(list, null);
	}

	private void initDialog() {
		GridBagLayout bag = new GridBagLayout();
		this.setLayout(bag);

		GridBagConstraints c = new GridBagConstraints();
		c.anchor = GridBagConstraints.NORTH;
		c.fill = GridBagConstraints.BOTH;
		c.gridwidth = 1;

		c.weighty = 1;
		c.weightx = 0.4;

		c.gridx = 0;
		UIFactory.addComponent(this, bag, c, categoriesTreeField.getJComponent());

		c.weightx = 0.6;
		c.gridx = 1;
		c.gridwidth = GridBagConstraints.REMAINDER;
		UIFactory.addComponent(this, bag, c, categoryAssociationsPanel);

		categoriesTreeField.setEnabled(false);
		categoriesTreeField.addTreeModelListener(new TreeModelListener() {
			public void treeNodesChanged(TreeModelEvent e) {
			}

			public void treeNodesInserted(TreeModelEvent e) {
			}

			public void treeNodesRemoved(TreeModelEvent e) {
			}

			public void treeStructureChanged(TreeModelEvent e) {
				if (entity != null) {
					setCategorySelection(entity.getCategoryIDList(categoriesTreeField.getSelectedDate()));
				}
			}
		});
	}

	public void setEntity(GenericEntity entity) {
		this.entity = entity;

		if (entity != null) {
			categoriesTreeField.setSelectedCategoriesAndEntities(entity.getCategoryIDList(categoriesTreeField.getSelectedDate()), null);
			categoryAssociationsPanel.setEntity(entity);
		}
		else {
			categoriesTreeField.clearAll();
			categoryAssociationsPanel.setEntity(null);
		}
	}

	public void clearFields() {
		setEntity(null);
	}

	public final void addValueChangeListener(ValueChangeListener cl) {
		categoryAssociationsPanel.addValueChangeListener(cl);
	}

	public final void removeValueChangeListener(ValueChangeListener cl) {
		categoryAssociationsPanel.removeValueChangeListener(cl);
	}

	public List<MutableTimedAssociationKey> getCategoryAssociations() {
		return categoryAssociationsPanel.getCategoryAssociations();
	}

	public void setEnabledFields(boolean enabled) {
		categoryAssociationsPanel.setEnabledFields(enabled);
	}
}
