package com.mindbox.pe.common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.DefaultListModel;

import com.mindbox.pe.common.config.EntityConfigHelper;
import com.mindbox.pe.model.GenericCategory;
import com.mindbox.pe.model.GenericContextElement;
import com.mindbox.pe.model.GenericEntity;
import com.mindbox.pe.model.GenericEntityType;
import com.mindbox.pe.model.GuidelineContext;

/**
 * Abstract implementation of {@link com.mindbox.pe.common.GuidelineContextHolder}. This uses
 * various model objects in javax.swing package to maintain context elements. This does not provide
 * any GUI elements for manaing guideline context; use
 * {@link com.mindbox.pe.client.common.context.GuidelineContextPanel}, instead.
 * <p>
 * This is abstract; use concrete implementation for client or server.
 * 
 * @author Gene Kim
 * @author MindBox
 * @since PowerEditor 4.2.0
 */
public abstract class AbstractGuidelineContextHolder implements GuidelineContextHolder, GuidelineContextProvider {

	private final Map<GenericEntityType, DefaultListModel<GenericContextElement>> genericEntityListModelMap;

	protected AbstractGuidelineContextHolder() {
		genericEntityListModelMap = new HashMap<GenericEntityType, DefaultListModel<GenericContextElement>>();
	}

	@Override
	public synchronized void addContext(final GenericCategory[] categories) {
		if (categories != null && categories.length > 0 && categories[0] != null) {
			GenericEntityType type = getEntityConfiguration().findEntityTypeForCategoryType(categories[0].getType());
			if (type == null) {
				throw new IllegalArgumentException("Cannot add categories: invalid type " + categories[0].getID());
			}
			DefaultListModel<GenericContextElement> model = getGenenricEntityListModel(type);

			if (model.isEmpty() || hasGenericCategoryContext(type)) {
				for (int i = 0; i < categories.length; i++) {
					if (!model.contains(categories[i])) {
						model.addElement(categories[i]);
					}
				}
			}
			else {
				throw new IllegalArgumentException("Cannot add category as generic entity context exists");
			}
		}
	}

	@Override
	public void addContext(final GenericEntity[] entities) {
		if (entities.length > 0) {
			DefaultListModel<GenericContextElement> model = getGenenricEntityListModel(entities[0].getType());
			if (model.isEmpty() || !hasGenericCategoryContext(entities[0].getType())) {
				for (int i = 0; i < entities.length; i++) {
					if (!model.contains(entities[i])) {
						model.addElement(entities[i]);
					}
				}
			}
			else {
				throw new IllegalArgumentException("Cannot add entities as category context exists");
			}
		}
	}

	@Override
	public synchronized void clearContext() {
		// clear generic entity lists
		for (DefaultListModel<GenericContextElement> element : genericEntityListModelMap.values()) {
			element.clear();
		}
	}

	protected abstract EntityConfigHelper getEntityConfiguration();

	protected final DefaultListModel<GenericContextElement> getGenenricEntityListModel(final GenericEntityType type) {
		if (genericEntityListModelMap.containsKey(type)) {
			return genericEntityListModelMap.get(type);
		}
		else {
			DefaultListModel<GenericContextElement> model = new DefaultListModel<GenericContextElement>();
			genericEntityListModelMap.put(type, model);
			return model;
		}
	}

	protected abstract GenericCategory getGenericCategory(int genericCategoryType, int categoryID);

	protected abstract GenericEntity getGenericEntity(GenericEntityType type, int id);

	@Override
	public synchronized final GuidelineContext[] getGuidelineContexts() {
		List<GuidelineContext> list = new ArrayList<GuidelineContext>();
		GuidelineContext context = null;
		// handle generic entity context
		for (Map.Entry<GenericEntityType, DefaultListModel<GenericContextElement>> element : genericEntityListModelMap.entrySet()) {
			GenericEntityType type = element.getKey();
			DefaultListModel<GenericContextElement> model = element.getValue();
			if (!model.isEmpty()) {
				int[] ids = new int[model.size()];
				if (hasGenericCategoryContext(type)) {
					context = new GuidelineContext(((GenericCategory) model.get(0)).getType());
					for (int i = 0; i < ids.length; i++) {
						ids[i] = ((GenericCategory) model.get(i)).getID();
					}
				}
				else {
					context = new GuidelineContext(type);
					for (int i = 0; i < ids.length; i++) {
						ids[i] = ((GenericEntity) model.get(i)).getID();
					}
				}
				context.setIDs(ids);
				list.add(context);
			}
		}
		return list.toArray(new GuidelineContext[0]);
	}

	protected final boolean hasGenericCategoryContext(final GenericEntityType type) {
		DefaultListModel<GenericContextElement> model = getGenenricEntityListModel(type);
		return model.get(0) instanceof GenericCategory;
	}

	@Override
	public void removeContext(final GenericCategory[] categories) {
		if (categories != null && categories.length > 0) {
			final GenericEntityType type = GenericEntityType.forCategoryType(categories[0].getType());
			if (type == null) {
				throw new IllegalArgumentException("Cannot add categories: invalid type " + categories[0].getID());
			}
			DefaultListModel<GenericContextElement> model = getGenenricEntityListModel(type);

			for (int i = 0; i < categories.length; i++) {
				if (model.contains(categories[i])) {
					model.removeElement(categories[i]);
				}
			}
		}
	}

	@Override
	public void removeContext(final GenericEntity[] entities) {
		for (int i = 0; i < entities.length; i++) {
			DefaultListModel<GenericContextElement> model = getGenenricEntityListModel(entities[i].getType());
			if (model.contains(entities[i])) {
				model.removeElement(entities[i]);
			}
		}
	}

	@Override
	public synchronized final void setContextElemens(final GuidelineContext[] contexts) {
		clearContext();
		for (int i = 0; i < contexts.length; i++) {
			int[] ids = contexts[i].getIDs();
			// process EntityType until all entities are generic
			// handle generic entities
			if (contexts[i].getGenericEntityType() != null) {
				GenericEntity[] entities = new GenericEntity[ids.length];
				for (int j = 0; j < entities.length; j++) {
					entities[j] = getGenericEntity(contexts[i].getGenericEntityType(), ids[j]);
				}
				addContext(entities);
			}
			else if (contexts[i].getGenericCategoryType() > 0) {
				GenericCategory[] categories = new GenericCategory[ids.length];
				for (int j = 0; j < categories.length; j++) {
					categories[j] = getGenericCategory(contexts[i].getGenericCategoryType(), ids[j]);
				}
				addContext(categories);
			}
		}
	}
}