package com.mindbox.pe.client.common;


import static com.mindbox.pe.client.ClientTestObjectMother.createGenericEntity;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.mindbox.pe.client.AbstractTestWithGenericEntityType;
import com.mindbox.pe.client.EntityModelCacheFactory;
import com.mindbox.pe.model.GenericEntity;
import com.mindbox.pe.model.GenericEntityType;


public class GenericEntityCheckListTest extends AbstractTestWithGenericEntityType {

	/**
	* Test to make sure the drop-down is not editable and that the toString() method contains
	* the name
	*
	* @throws Exception
	*/
	@Test
	public void testSelectAll() throws Exception {
		EntityModelCacheFactory.getInstance().add(createGenericEntity(entityType));
		GenericEntityCheckList checkList = new GenericEntityCheckList(GenericEntityType.forName("product"));
		checkList.selectAll();
		GenericEntity[] entities = checkList.getSelectedGenericEntities();
		assertNotNull(entities);
		assertTrue(checkList.getModel().getSize() == entities.length);
	}

	@Before
	public void setUp() throws Exception {
		super.setUp();

	}

	@After
	public void tearDown() throws Exception {
		super.tearDown();
	}
}
