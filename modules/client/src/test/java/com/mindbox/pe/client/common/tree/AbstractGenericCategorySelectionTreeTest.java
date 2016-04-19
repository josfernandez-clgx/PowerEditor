package com.mindbox.pe.client.common.tree;

import static com.mindbox.pe.client.ClientTestObjectMother.createGenericCategory;
import static com.mindbox.pe.client.ClientTestObjectMother.createGenericEntity;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Map;

import javax.swing.tree.TreeSelectionModel;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.mindbox.pe.client.AbstractClientTestBase;
import com.mindbox.pe.client.EntityModelCacheFactory;
import com.mindbox.pe.common.ReflectionUtil;
import com.mindbox.pe.model.GenericCategory;
import com.mindbox.pe.model.GenericEntity;
import com.mindbox.pe.model.GenericEntityType;
import com.mindbox.pe.model.assckey.DefaultMutableTimedAssociationKey;

public class AbstractGenericCategorySelectionTreeTest extends AbstractClientTestBase {

	private class TestImpl extends AbstractGenericCategorySelectionTree {

		protected TestImpl(GenericEntityType entityType, boolean allowEntity) {
			super(entityType, allowEntity, TreeSelectionModel.SINGLE_TREE_SELECTION, true, true, false, false);
		}
	}

	private GenericEntity entity1;
	private GenericCategory category1, category2;
	private TestImpl categorySelectionTree;

	@Test
	public void testConstructorWithNullEntityTypeThrowsNullPointerException() throws Exception {
		try {
			new TestImpl(null, true);
			fail("Expected NullPointerException not thrown");
		}
		catch (NullPointerException e) {
			// expected
		}
	}

	@Test
	public void testGetSelectedCategoriesWorksWithAllowEntity() throws Exception {
		GenericCategory[] categories = categorySelectionTree.getSelectedCategories();
		assertNull(categories);

		categorySelectionTree.selectGenericEntity(entity1.getID());
		categories = categorySelectionTree.getSelectedCategories();
		assertNull(categories);
	}

	@Test
	public void testGetSelectedCategoryWorksWithAllowEntity() throws Exception {
		GenericCategory category = categorySelectionTree.getSelectedGenericCategory();
		assertNull(category);

		categorySelectionTree.selectGenericEntity(entity1.getID());
		category = categorySelectionTree.getSelectedGenericCategory();
		assertNull(category);
	}

	@Test
	public void testSelectGenericCategoryWorksWithAllowEntity() throws Exception {
		categorySelectionTree.selectGenericCategory(category2.getID());

		GenericCategory category = categorySelectionTree.getSelectedGenericCategory();
		assertEquals(category2.getID(), category.getID());
	}

	@Test
	public void testSetEnabled() throws Exception {
		categorySelectionTree.setEnabled(false);
		assertFalse(categorySelectionTree.tree.isEditable());
		assertTrue(categorySelectionTree.tree.isEnabled());
		categorySelectionTree.setEnabled(true);
		assertTrue(categorySelectionTree.tree.isEditable());
		assertTrue(categorySelectionTree.tree.isEnabled());
	}

	@Before
	public void setUp() throws Exception {
		super.setUp();

		category1 = createGenericCategory(entityType1);
		category2 = createGenericCategory(entityType1);
		category1.addChildAssociation(new DefaultMutableTimedAssociationKey(category2.getID()));
		category2.addParentKey(new DefaultMutableTimedAssociationKey(category1.getID()));
		EntityModelCacheFactory.getInstance().addGenericCategory(category1);
		EntityModelCacheFactory.getInstance().addGenericCategory(category2);

		entity1 = createGenericEntity(entityType1);
		entity1.addCategoryAssociation(new DefaultMutableTimedAssociationKey(category2.getID()));
		EntityModelCacheFactory.getInstance().add(entity1);
		categorySelectionTree = new TestImpl(entityType1, true);
	}

	@After
	public void tearDown() throws Exception {
		// Tear downs for AbstractGenericCategorySelectionTreeTest
		ReflectionUtil.getPrivate(EntityModelCacheFactory.getInstance(), "cachedGenericCategoryMap", Map.class).clear();
		EntityModelCacheFactory.getInstance().remove(entity1);
		super.tearDown();
	}
}
