package com.mindbox.pe.client;

import java.util.List;
import java.util.Map;

import javax.swing.DefaultComboBoxModel;
import javax.swing.ListModel;

import junit.framework.Test;
import junit.framework.TestSuite;

import com.mindbox.pe.AbstractTestWithGenericEntityType;
import com.mindbox.pe.ObjectMother;
import com.mindbox.pe.common.ReflectionUtil;
import com.mindbox.pe.model.Constants;
import com.mindbox.pe.model.GenericCategory;
import com.mindbox.pe.model.GenericEntity;
import com.mindbox.pe.model.GenericEntityType;
import com.mindbox.pe.model.ParameterTemplate;
import com.mindbox.pe.model.assckey.DefaultMutableTimedAssociationKey;
import com.mindbox.pe.model.assckey.MutableTimedAssociationKey;

public class EntityModelCacheFactoryTest extends AbstractTestWithGenericEntityType {

	public static void main(String[] args) {
		junit.textui.TestRunner.run(suite());
	}

	public static Test suite() {
		TestSuite suite = new TestSuite("EntityModelCacheFactoryTest Tests");
		suite.addTestSuite(EntityModelCacheFactoryTest.class);
		return suite;
	}

	private GenericEntity entity;

	public EntityModelCacheFactoryTest(String name) {
		super(name);
	}

	public void testAddGenericCategoryHappyCase() throws Exception {
		GenericCategory category = ObjectMother.createGenericCategory(entityType);
		EntityModelCacheFactory.getInstance().addGenericCategory(category);
		assertNotNull(EntityModelCacheFactory.getInstance().getGenericCategory(entityType, category.getID()));
		assertEquals(category.getName(), EntityModelCacheFactory.getInstance().getGenericCategoryName(entityType, category.getID(), null));
	}

	public void testAddForGenericEntityHappyCase() throws Exception {
		EntityModelCacheFactory.getInstance().add(entity);
		assertNotNull(EntityModelCacheFactory.getInstance().getGenericEntity(entityType, entity.getID()));
		assertEquals(entity.getName(), EntityModelCacheFactory.getInstance().getGenericEntityName(entityType, entity.getID(), null));
	}

	public void testGetGenericCategoryNameWithNullTypeThrowsNullPointerException() throws Exception {
		try {
			EntityModelCacheFactory.getInstance().getGenericCategoryName(null, 0, null);
			fail("Expected NullPointerException not thrown");
		}
		catch (NullPointerException ex) {
			// expected
		}
	}

	public void testGetGenericEntityNameWithNullTypeThrowsNullPointerException() throws Exception {
		try {
			EntityModelCacheFactory.getInstance().getGenericEntityName(null, 0, null);
			fail("Expected NullPointerException not thrown");
		}
		catch (NullPointerException ex) {
			// expected
		}
	}

	public void testFindGenericEntityWithNullEntityTypeThrowsNullPointerException() throws Exception {
		assertThrowsNullPointerException(EntityModelCacheFactory.getInstance(), "findGenericEntity", new Class[] { GenericEntityType.class,
				String.class }, new Object[] { null, "" });
	}

	public void testFindGenericEntityWithNullNameReturnsNull() throws Exception {
		assertNull(EntityModelCacheFactory.getInstance().findGenericEntity(entityType, null));
	}

	public void testFindGenericEntityWithEmptyNameReturnsNull() throws Exception {
		assertNull(EntityModelCacheFactory.getInstance().findGenericEntity(entityType, ""));
	}

	public void testFindGenericEntityWithNotFoundNameReturnsNull() throws Exception {
		EntityModelCacheFactory.getInstance().add(entity);
		assertNull(EntityModelCacheFactory.getInstance().findGenericEntity(entityType, entity.getName() + "!"));
	}

	public void testFindGenericEntityHappyCase() throws Exception {
		EntityModelCacheFactory.getInstance().add(entity);
		EntityModelCacheFactory.getInstance().add(ObjectMother.createGenericEntity(entityType));
		assertEquals(entity.getId(), EntityModelCacheFactory.getInstance().findGenericEntity(entityType, entity.getName()).getId());
	}

	public void testFindGenericCategoryWithNullEntityTypeThrowsNullPointerException() throws Exception {
		assertThrowsNullPointerException(EntityModelCacheFactory.getInstance(), "findGenericCategoryByName", new Class[] { GenericEntityType.class,
				String.class }, new Object[] { null, "" });
	}

	public void testFindGenericCategoryWithNullNameReturnsNull() throws Exception {
		assertNull(EntityModelCacheFactory.getInstance().findGenericCategoryByName(entityType, null));
	}

	public void testFindGenericCategoryWithEmptyNameReturnsNull() throws Exception {
		assertNull(EntityModelCacheFactory.getInstance().findGenericCategoryByName(entityType, ""));
	}

	public void testFindGenericCategoryWithNotFoundNameReturnsNull() throws Exception {
		GenericCategory category = ObjectMother.createGenericCategory(entityType);
		EntityModelCacheFactory.getInstance().addGenericCategory(category);
		assertNull(EntityModelCacheFactory.getInstance().findGenericCategoryByName(entityType, category.getName() + "!"));
	}

	public void testFindGenericCategoryHappyCase() throws Exception {
		GenericCategory category = ObjectMother.createGenericCategory(entityType);
		EntityModelCacheFactory.getInstance().addGenericCategory(category);
		EntityModelCacheFactory.getInstance().addGenericCategory(ObjectMother.createGenericCategory(entityType));
		assertEquals(category.getId(), EntityModelCacheFactory.getInstance().findGenericCategoryByName(entityType, category.getName())[0].getId());
	}
    
    public void testfindFullyQualifiedGenericCategoryByPathHappyCase() throws Exception {
        GenericCategory rootCategory = ObjectMother.createGenericCategory(entityType);
        rootCategory.setName("root");
        EntityModelCacheFactory.getInstance().addGenericCategory(rootCategory);
        GenericCategory childCategory = ObjectMother.createGenericCategory(entityType);
        childCategory.setName("child");
        EntityModelCacheFactory.getInstance().addGenericCategory(childCategory);
        MutableTimedAssociationKey childToParentKey = new DefaultMutableTimedAssociationKey(rootCategory.getId(),
                null, null);
        MutableTimedAssociationKey parentToChildKey = new DefaultMutableTimedAssociationKey(childCategory.getId(),
                null, null);
        childCategory.addParentKey(childToParentKey);
        rootCategory.addChildAssociation(parentToChildKey);
        assertEquals("root" + Constants.CATEGORY_PATH_DELIMITER + "child", EntityModelCacheFactory.getInstance().getMostRecentFullyQualifiedCategoryName(childCategory));
        assertEquals("root", EntityModelCacheFactory.getInstance().getMostRecentFullyQualifiedCategoryName(rootCategory));        
    }

	protected void setUp() throws Exception {
		super.setUp();
		entity = ObjectMother.createGenericEntity(entityType);
	}

	protected void tearDown() throws Exception {
		EntityModelCacheFactory.getInstance().remove(entity);
		entity = null;
		super.tearDown();
	}
    
    public void testGenericEntityListModelMap() throws Exception {
        ListModel model = EntityModelCacheFactory.getInstance().getGenericEntityListModel(entityType, false);
        EntityModelCacheFactory.getInstance().add(entity);
        Map<Integer,Integer> map = EntityModelCacheFactory.getInstance().getGenericEntityListModelMap(entityType);
        assertNotNull(map);
        assertTrue(model.getSize() == map.size());
        assertTrue(map.containsKey(new Integer(entity.getID())));
        
        GenericEntity firstEntity = (GenericEntity)model.getElementAt(0);
        assertTrue(map.containsKey(new Integer(firstEntity.getID())));
        Integer rowNumber = map.get(new Integer(firstEntity.getID())); 
        assertTrue(rowNumber.intValue() == 0);
    }

    public void testGetCategoryToEntityAssociationsByCategoryThrowsNullPointer() throws Exception {
        GenericCategory category = ObjectMother.createGenericCategory(entityType); 
        //List list = EntityModelCacheFactory.getInstance().getCategoryToEntityAssociationsByCategory(category));
        // TODO Gaughan Mock the client-server communication rather than catching a NPE
        assertThrowsNullPointerException(EntityModelCacheFactory.getInstance(), "getCategoryToEntityAssociationsByCategory", 
                new Class[] { GenericCategory.class}, new Object[] { category });
                
    }
    
    public void testGenericCategoryIsDescendentAtAnyTime() throws Exception {
        GenericCategory parentCategory = ObjectMother.createGenericCategory(entityType);
        EntityModelCacheFactory.getInstance().addGenericCategory(parentCategory);
        GenericCategory childCategory = ObjectMother.createGenericCategory(entityType);
        EntityModelCacheFactory.getInstance().addGenericCategory(childCategory);
        MutableTimedAssociationKey childToParentKey = new DefaultMutableTimedAssociationKey(parentCategory.getId(),
                null, null);
        MutableTimedAssociationKey parentToChildKey = new DefaultMutableTimedAssociationKey(childCategory.getId(),
                null, null);
        childCategory.addParentKey(childToParentKey);
        parentCategory.addChildAssociation(parentToChildKey);
        
        assertTrue(EntityModelCacheFactory.getInstance().isDescendentAtAnyTime(childCategory.getId(), 
                new int[] { parentCategory.getId() }, parentCategory.getType()));
        assertFalse(EntityModelCacheFactory.getInstance().isDescendentAtAnyTime(parentCategory.getId(), 
                new int[] { childCategory.getId() }, parentCategory.getType()));
        
    }
    
    public void testGetAllParameterTemplates() throws Exception {
        List<ParameterTemplate> templates = EntityModelCacheFactory.getInstance().getAllParameterTemplates();
        assertNotNull(templates);
        assertTrue(templates.size() == 0);
        DefaultComboBoxModel model = (DefaultComboBoxModel)ReflectionUtil.getPrivate(EntityModelCacheFactory.getInstance(), "paramTemplateModel");
        ParameterTemplate template1 = ObjectMother.createParameterTemplate();
        ParameterTemplate template2 = ObjectMother.createParameterTemplate();
        
        model.addElement(template1);
        model.addElement(template2);
        templates = EntityModelCacheFactory.getInstance().getAllParameterTemplates();
        assertNotNull(templates);
        assertEquals(templates.size(), 2);
        assertTrue(templates.contains(template1));
        assertTrue(templates.contains(template2));
    }

}
