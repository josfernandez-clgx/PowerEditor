package com.mindbox.pe.client;

import static com.mindbox.pe.client.ClientTestObjectMother.createGenericCategory;
import static com.mindbox.pe.client.ClientTestObjectMother.createGenericEntity;
import static com.mindbox.pe.client.ClientTestObjectMother.createParameterTemplate;
import static com.mindbox.pe.unittest.UnitTestHelper.assertThrowsNullPointerException;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.List;
import java.util.Map;

import javax.swing.DefaultComboBoxModel;
import javax.swing.ListModel;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.mindbox.pe.common.ReflectionUtil;
import com.mindbox.pe.model.Constants;
import com.mindbox.pe.model.GenericCategory;
import com.mindbox.pe.model.GenericEntity;
import com.mindbox.pe.model.GenericEntityType;
import com.mindbox.pe.model.assckey.DefaultMutableTimedAssociationKey;
import com.mindbox.pe.model.assckey.MutableTimedAssociationKey;
import com.mindbox.pe.model.template.ParameterTemplate;

public class EntityModelCacheFactoryTest extends AbstractTestWithGenericEntityType {

	private GenericEntity entity;

	@Override
	@Before
	public void setUp() throws Exception {
		super.setUp();
		entity = createGenericEntity(entityType);
	}

	@Override
	@After
	public void tearDown() throws Exception {
		EntityModelCacheFactory.getInstance().remove(entity);
		entity = null;
	}

	@Test
	public void testAddForGenericEntityHappyCase() throws Exception {
		EntityModelCacheFactory.getInstance().add(entity);
		assertNotNull(EntityModelCacheFactory.getInstance().getGenericEntity(entityType, entity.getID()));
		assertEquals(entity.getName(), EntityModelCacheFactory.getInstance().getGenericEntityName(entityType, entity.getID(), null));
	}

	@Test
	public void testAddGenericCategoryHappyCase() throws Exception {
		GenericCategory category = createGenericCategory(entityType);
		EntityModelCacheFactory.getInstance().addGenericCategory(category);
		assertNotNull(EntityModelCacheFactory.getInstance().getGenericCategory(entityType, category.getID()));
		assertEquals(category.getName(), EntityModelCacheFactory.getInstance().getGenericCategoryName(entityType, category.getID(), null));
	}

	@Test
	public void testfindFullyQualifiedGenericCategoryByPathHappyCase() throws Exception {
		GenericCategory rootCategory = createGenericCategory(entityType);
		rootCategory.setName("root");
		EntityModelCacheFactory.getInstance().addGenericCategory(rootCategory);
		GenericCategory childCategory = createGenericCategory(entityType);
		childCategory.setName("child");
		EntityModelCacheFactory.getInstance().addGenericCategory(childCategory);
		MutableTimedAssociationKey childToParentKey = new DefaultMutableTimedAssociationKey(rootCategory.getId(), null, null);
		MutableTimedAssociationKey parentToChildKey = new DefaultMutableTimedAssociationKey(childCategory.getId(), null, null);
		childCategory.addParentKey(childToParentKey);
		rootCategory.addChildAssociation(parentToChildKey);
		assertEquals("root" + Constants.CATEGORY_PATH_DELIMITER + "child", EntityModelCacheFactory.getInstance().getMostRecentFullyQualifiedCategoryName(childCategory));
		assertEquals("root", EntityModelCacheFactory.getInstance().getMostRecentFullyQualifiedCategoryName(rootCategory));
	}

	@Test
	public void testFindGenericCategoryHappyCase() throws Exception {
		GenericCategory category = createGenericCategory(entityType);
		EntityModelCacheFactory.getInstance().addGenericCategory(category);
		EntityModelCacheFactory.getInstance().addGenericCategory(createGenericCategory(entityType));
		assertEquals(category.getId(), EntityModelCacheFactory.getInstance().findGenericCategoryByName(entityType, category.getName())[0].getId());
	}

	@Test
	public void testFindGenericCategoryWithEmptyNameReturnsNull() throws Exception {
		assertNull(EntityModelCacheFactory.getInstance().findGenericCategoryByName(entityType, ""));
	}

	@Test
	public void testFindGenericCategoryWithNotFoundNameReturnsNull() throws Exception {
		GenericCategory category = createGenericCategory(entityType);
		EntityModelCacheFactory.getInstance().addGenericCategory(category);
		assertNull(EntityModelCacheFactory.getInstance().findGenericCategoryByName(entityType, category.getName() + "!"));
	}

	@Test
	public void testFindGenericCategoryWithNullEntityTypeThrowsNullPointerException() throws Exception {
		assertThrowsNullPointerException(
				EntityModelCacheFactory.getInstance(),
				"findGenericCategoryByName",
				new Class[] { GenericEntityType.class, String.class },
				new Object[] { null, "" });
	}

	@Test
	public void testFindGenericCategoryWithNullNameReturnsNull() throws Exception {
		assertNull(EntityModelCacheFactory.getInstance().findGenericCategoryByName(entityType, null));
	}

	@Test
	public void testFindGenericEntityHappyCase() throws Exception {
		EntityModelCacheFactory.getInstance().add(entity);
		EntityModelCacheFactory.getInstance().add(createGenericEntity(entityType));
		assertEquals(entity.getId(), EntityModelCacheFactory.getInstance().findGenericEntity(entityType, entity.getName()).getId());
	}

	@Test
	public void testFindGenericEntityWithEmptyNameReturnsNull() throws Exception {
		assertNull(EntityModelCacheFactory.getInstance().findGenericEntity(entityType, ""));
	}

	@Test
	public void testFindGenericEntityWithNotFoundNameReturnsNull() throws Exception {
		EntityModelCacheFactory.getInstance().add(entity);
		assertNull(EntityModelCacheFactory.getInstance().findGenericEntity(entityType, entity.getName() + "!"));
	}

	@Test
	public void testFindGenericEntityWithNullEntityTypeThrowsNullPointerException() throws Exception {
		assertThrowsNullPointerException(
				EntityModelCacheFactory.getInstance(),
				"findGenericEntity",
				new Class[] { GenericEntityType.class, String.class },
				new Object[] { null, "" });
	}

	@Test
	public void testFindGenericEntityWithNullNameReturnsNull() throws Exception {
		assertNull(EntityModelCacheFactory.getInstance().findGenericEntity(entityType, null));
	}

	@Test
	public void testGenericCategoryIsDescendentAtAnyTime() throws Exception {
		GenericCategory parentCategory = createGenericCategory(entityType);
		EntityModelCacheFactory.getInstance().addGenericCategory(parentCategory);
		GenericCategory childCategory = createGenericCategory(entityType);
		EntityModelCacheFactory.getInstance().addGenericCategory(childCategory);
		MutableTimedAssociationKey childToParentKey = new DefaultMutableTimedAssociationKey(parentCategory.getId(), null, null);
		MutableTimedAssociationKey parentToChildKey = new DefaultMutableTimedAssociationKey(childCategory.getId(), null, null);
		childCategory.addParentKey(childToParentKey);
		parentCategory.addChildAssociation(parentToChildKey);

		assertTrue(EntityModelCacheFactory.getInstance().isDescendentAtAnyTime(childCategory.getId(), new int[] { parentCategory.getId() }, parentCategory.getType()));
		assertFalse(EntityModelCacheFactory.getInstance().isDescendentAtAnyTime(parentCategory.getId(), new int[] { childCategory.getId() }, parentCategory.getType()));
	}

	@Test
	public void testGenericEntityListModelMap() throws Exception {
		ListModel<GenericEntity> model = EntityModelCacheFactory.getInstance().getGenericEntityListModel(entityType, false);
		EntityModelCacheFactory.getInstance().add(entity);
		Map<Integer, Integer> map = EntityModelCacheFactory.getInstance().getGenericEntityListModelMap(entityType);
		assertNotNull(map);
		assertTrue(model.getSize() == map.size());
		assertTrue(map.containsKey(new Integer(entity.getID())));

		GenericEntity firstEntity = model.getElementAt(0);
		assertTrue(map.containsKey(new Integer(firstEntity.getID())));
		Integer rowNumber = map.get(new Integer(firstEntity.getID()));
		assertTrue(rowNumber.intValue() == 0);
	}

	@Test
	public void testGetAllParameterTemplates() throws Exception {
		List<ParameterTemplate> templates = EntityModelCacheFactory.getInstance().getAllParameterTemplates();
		assertNotNull(templates);
		assertTrue(templates.size() == 0);
		@SuppressWarnings("unchecked")
		DefaultComboBoxModel<ParameterTemplate> model = (DefaultComboBoxModel<ParameterTemplate>) ReflectionUtil.getPrivate(
				EntityModelCacheFactory.getInstance(),
				"paramTemplateModel");
		ParameterTemplate template1 = createParameterTemplate();
		ParameterTemplate template2 = createParameterTemplate();

		model.addElement(template1);
		model.addElement(template2);
		templates = EntityModelCacheFactory.getInstance().getAllParameterTemplates();
		assertNotNull(templates);
		assertEquals(templates.size(), 2);
		assertTrue(templates.contains(template1));
		assertTrue(templates.contains(template2));
	}

	@Test
	public void testGetCategoryToEntityAssociationsByCategoryThrowsNullPointer() throws Exception {
		GenericCategory category = createGenericCategory(entityType);
		assertThrowsNullPointerException(
				EntityModelCacheFactory.getInstance(),
				"getCategoryToEntityAssociationsByCategory",
				new Class[] { GenericCategory.class },
				new Object[] { category });
	}

	@Test
	public void testGetGenericCategoryNameWithNullTypeThrowsNullPointerException() throws Exception {
		try {
			EntityModelCacheFactory.getInstance().getGenericCategoryName(null, 0, null);
			fail("Expected NullPointerException not thrown");
		}
		catch (NullPointerException ex) {
			// expected
		}
	}

	@Test
	public void testGetGenericEntityNameWithNullTypeThrowsNullPointerException() throws Exception {
		try {
			EntityModelCacheFactory.getInstance().getGenericEntityName(null, 0, null);
			fail("Expected NullPointerException not thrown");
		}
		catch (NullPointerException ex) {
			// expected
		}
	}

}
