package com.mindbox.pe.client.common.event;

import com.mindbox.pe.model.GenericEntity;

public class EntityDeleteEvent {

	private GenericEntity entity;

	public EntityDeleteEvent(GenericEntity entity) {
		if (entity == null) throw new IllegalArgumentException("entity cannot be null");
		this.entity = entity;
	}

	public GenericEntity getEntity() {
		return entity;
	}

}
