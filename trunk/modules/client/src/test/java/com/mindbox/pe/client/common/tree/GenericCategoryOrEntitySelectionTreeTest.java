package com.mindbox.pe.client.common.tree;

import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.mindbox.pe.client.AbstractClientTestBase;
import com.mindbox.pe.client.EntityModelCacheFactory;
import com.mindbox.pe.model.GenericCategory;
import com.mindbox.pe.model.GenericEntityType;

public class GenericCategoryOrEntitySelectionTreeTest extends AbstractClientTestBase {


	private static class TreeImpl extends GenericCategorySelectionTree {

		public TreeImpl(GenericEntityType entityType, boolean showEntities) {
			super(entityType.getCategoryType(), showEntities, true, true);
		}

	}

	@Before
	public void setUp() throws Exception {
		super.setUp();
	}

	@After
	public void tearDown() throws Exception {
		super.tearDown();
	}

	@Test
	public void testAll() throws Exception {
		GenericCategory genericCategory = new GenericCategory(10, entityType1.getName(), entityType1.getCategoryType());
		EntityModelCacheFactory.getInstance().addGenericCategory(genericCategory);
		GenericEntityType type = entityType1;
		GenericCategorySelectionTree tree = new TreeImpl(type, true);
		assertTrue(tree.tree.isRootVisible());

	}

}
