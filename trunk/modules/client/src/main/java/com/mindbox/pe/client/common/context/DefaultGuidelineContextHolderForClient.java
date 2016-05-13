package com.mindbox.pe.client.common.context;

import java.util.ArrayList;
import java.util.List;

import com.mindbox.pe.client.ClientUtil;
import com.mindbox.pe.client.EntityModelCacheFactory;
import com.mindbox.pe.client.common.event.ContextChangeEvent;
import com.mindbox.pe.client.common.event.ContextChangeListener;
import com.mindbox.pe.common.AbstractGuidelineContextHolder;
import com.mindbox.pe.common.config.EntityConfigHelper;
import com.mindbox.pe.model.GenericCategory;
import com.mindbox.pe.model.GenericEntity;
import com.mindbox.pe.model.GenericEntityType;


/**
 * Concrete implementation of {@link com.mindbox.pe.common.GuidelineContextHolder} for PE Client side use.
 * @author Geneho Kim
 * @since PowerEditor 4.2.0
 */
public class DefaultGuidelineContextHolderForClient extends AbstractGuidelineContextHolder implements GuidelineContextHolderForClient {

	private final List<ContextChangeListener> ccListenerList;

	public DefaultGuidelineContextHolderForClient() {
		ccListenerList = new ArrayList<ContextChangeListener>();
	}

	public void addContext(GenericEntity[] entities) {
		super.addContext(entities);
		if (entities != null && entities.length > 0) {
			fireContextChangeEvent();
		}
	}

	protected EntityConfigHelper getEntityConfiguration() {
		return ClientUtil.getEntityConfigHelper();
	}

	public synchronized void addContext(GenericCategory[] categories) {
		super.addContext(categories);
		if (categories.length > 0) {
			if (categories != null && categories.length > 0) {
				fireContextChangeEvent();
			}
		}
	}

	public synchronized void clearContext() {
		super.clearContext();
		fireContextChangeEvent();
	}

	public void removeContext(GenericEntity[] entities) {
		super.removeContext(entities);
		if (entities != null && entities.length > 0) {
			fireContextChangeEvent();
		}
	}

	public void removeContext(GenericCategory[] categories) {
		super.removeContext(categories);
		if (categories != null && categories.length > 0) {
			fireContextChangeEvent();
		}
	}

	public synchronized final void addContextChangeListener(ContextChangeListener l) {
		if (!ccListenerList.contains(l)) {
			ccListenerList.add(l);
		}
	}

	public synchronized final void removeContextChangeListener(ContextChangeListener l) {
		if (ccListenerList.contains(l)) {
			ccListenerList.remove(l);
		}
	}

	protected synchronized void fireContextChangeEvent() {
		for (int i = 0; i < ccListenerList.size(); i++) {
			ContextChangeListener listener = ccListenerList.get(i);
			if (listener != null) {
				listener.contextChanged(new ContextChangeEvent());
			}
		}
	}

	protected GenericCategory getGenericCategory(int genericCategoryType, int categoryID) {
		return EntityModelCacheFactory.getInstance().getGenericCategory(genericCategoryType, categoryID);
	}

	protected GenericEntity getGenericEntity(GenericEntityType type, int id) {
		return EntityModelCacheFactory.getInstance().getGenericEntity(type, id);
	}
}