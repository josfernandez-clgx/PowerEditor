package com.mindbox.pe.client.common.tree;

import java.util.Map;

import javax.swing.tree.TreeSelectionModel;

import junit.framework.Test;
import junit.framework.TestSuite;

import com.mindbox.pe.ObjectMother;
import com.mindbox.pe.client.AbstractClientTestBase;
import com.mindbox.pe.client.EntityModelCacheFactory;
import com.mindbox.pe.common.ReflectionUtil;
import com.mindbox.pe.model.GenericCategory;
import com.mindbox.pe.model.GenericEntity;
import com.mindbox.pe.model.GenericEntityType;
import com.mindbox.pe.model.assckey.DefaultMutableTimedAssociationKey;

public class AbstractGenericCategorySelectionTreeTest extends AbstractClientTestBase {

	public static void main(String[] args) {
		junit.textui.TestRunner.run(suite());
	}

	public static Test suite() {
		TestSuite suite = new TestSuite("AbstractGenericCategorySelectionTreeTest Tests");
		suite.addTestSuite(AbstractGenericCategorySelectionTreeTest.class);
		return suite;
	}

	private class TestImpl extends AbstractGenericCategorySelectionTree {

		protected TestImpl(GenericEntityType entityType, boolean allowEntity) {
			super(entityType, allowEntity, TreeSelectionModel.SINGLE_TREE_SELECTION, true, true, false, false);
		}
	}

	private GenericEntity entity1;
	private GenericCategory category1, category2;
	private TestImpl categorySelectionTree;

	public AbstractGenericCategorySelectionTreeTest(String name) {
		super(name);
	}

	public void testConstructorWithNullEntityTypeThrowsNullPointerException() throws Exception {
		try {
			new TestImpl(null, true);
			fail("Expected NullPointerException not thrown");
		}
		catch (NullPointerException e) {
			// expected
		}
	}

// Kim, 2007-08-30: no longer valid, per fix to performance issues with grids with entity columns
//	public void testConstructorHasNoNodeExpanded() throws Exception {
//		Enumeration<TreePath> enumeration = categorySelectionTree.getTree().getExpandedDescendants(categorySelectionTree.getTree().getPathForRow(0));
//		assertTrue(enumeration == null || !enumeration.hasMoreElements());
//	}

	public void testGetSelectedCategoriesWorksWithAllowEntity() throws Exception {
		GenericCategory[] categories = categorySelectionTree.getSelectedCategories();
		assertNull(categories);

		categorySelectionTree.selectGenericEntity(entity1.getID());
		categories = categorySelectionTree.getSelectedCategories();
		assertNull(categories);
	}

	public void testGetSelectedCategoryWorksWithAllowEntity() throws Exception {
		GenericCategory category = categorySelectionTree.getSelectedGenericCategory();
		assertNull(category);

		categorySelectionTree.selectGenericEntity(entity1.getID());
		category = categorySelectionTree.getSelectedGenericCategory();
		assertNull(category);
	}

	public void testSelectGenericCategoryWorksWithAllowEntity() throws Exception {
		categorySelectionTree.selectGenericCategory(category2.getID());

		GenericCategory category = categorySelectionTree.getSelectedGenericCategory();
		assertEquals(category2.getID(), category.getID());
	}

    public void testSetEnabled() throws Exception {
        categorySelectionTree.setEnabled(false);
        assertFalse(categorySelectionTree.tree.isEditable());
        assertTrue(categorySelectionTree.tree.isEnabled());
        categorySelectionTree.setEnabled(true);
        assertTrue(categorySelectionTree.tree.isEditable());
        assertTrue(categorySelectionTree.tree.isEnabled());
    }

	protected void setUp() throws Exception {
		super.setUp();

		category1 = ObjectMother.createGenericCategory(GenericEntityType.forName("product"));
		category2 = ObjectMother.createGenericCategory(GenericEntityType.forName("product"));
		category1.addChildAssociation(new DefaultMutableTimedAssociationKey(category2.getID()));
		category2.addParentKey(new DefaultMutableTimedAssociationKey(category1.getID()));
		EntityModelCacheFactory.getInstance().addGenericCategory(category1);
		EntityModelCacheFactory.getInstance().addGenericCategory(category2);

		entity1 = ObjectMother.createGenericEntity(GenericEntityType.forName("product"));
		entity1.addCategoryAssociation(new DefaultMutableTimedAssociationKey(category2.getID()));
		EntityModelCacheFactory.getInstance().add(entity1);
		categorySelectionTree = new TestImpl(GenericEntityType.forName("product"), true);
	}

	protected void tearDown() throws Exception {
		// Tear downs for AbstractGenericCategorySelectionTreeTest
		ReflectionUtil.getPrivate(EntityModelCacheFactory.getInstance(), "cachedGenericCategoryMap", Map.class).clear();
		EntityModelCacheFactory.getInstance().remove(entity1);
		super.tearDown();
	}
}
