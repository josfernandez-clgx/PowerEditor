package com.mindbox.pe.client;

import static org.junit.Assert.fail;

import com.mindbox.pe.model.GenericCategory;
import com.mindbox.pe.model.GenericEntity;
import com.mindbox.pe.model.table.CategoryOrEntityValue;
import com.mindbox.pe.model.table.CategoryOrEntityValues;

public class ClientTestAssert {

	public static void assertContains(GenericEntity entity, CategoryOrEntityValues values) {
		assertContains("", entity, values);
	}

	public static void assertContains(String message, GenericEntity entity, CategoryOrEntityValues values) {
		if (values == null) fail(message + ": CategoryOrEntityValues is null");
		for (int i = 0; i < values.size(); i++) {
			CategoryOrEntityValue value = (CategoryOrEntityValue) values.get(i);
			if (value.isForEntity() && value.getId() == entity.getId()) return;
		}
		fail(message + ": " + entity + " not found in " + values);
	}

	public static void assertContains(GenericCategory category, CategoryOrEntityValues values) {
		assertContains("", category, values);
	}

	public static void assertContains(String message, GenericCategory category, CategoryOrEntityValues values) {
		if (values == null) fail(message + ": CategoryOrEntityValues is null");
		for (int i = 0; i < values.size(); i++) {
			CategoryOrEntityValue value = (CategoryOrEntityValue) values.get(i);
			if (!value.isForEntity() && value.getId() == category.getId()) return;
		}
		fail(message + ": " + category + " not found in " + values);
	}

	private ClientTestAssert() {
	}

}
