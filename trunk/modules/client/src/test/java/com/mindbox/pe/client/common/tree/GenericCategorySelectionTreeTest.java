package com.mindbox.pe.client.common.tree;

import static com.mindbox.pe.client.ClientTestObjectMother.createGenericCategory;
import static org.junit.Assert.assertEquals;

import javax.swing.tree.TreeSelectionModel;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.mindbox.pe.client.AbstractClientTestBase;
import com.mindbox.pe.client.EntityModelCacheFactory;
import com.mindbox.pe.model.GenericCategory;

public class GenericCategorySelectionTreeTest extends AbstractClientTestBase {

	private GenericCategory category;

	/**
	 * @throws Exception
	 */
	@Test
	public void testTreeSelectionSetToDISCONTIGUOUS_TREE_SELECTION() throws Exception {
		GenericCategorySelectionTree tree = new GenericCategorySelectionTree(entityType1.getCategoryType(), false, true, true);
		assertEquals(tree.getTreeSelectionModel().getSelectionMode(), TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION);
	}

	@Before
	public void setUp() throws Exception {
		super.setUp();
		category = createGenericCategory(entityType1);
		EntityModelCacheFactory.getInstance().addGenericCategory(category);
	}

	@After
	public void tearDown() throws Exception {
		super.tearDown();
		EntityModelCacheFactory.getInstance().removeGenericCategory(category);
	}
}
