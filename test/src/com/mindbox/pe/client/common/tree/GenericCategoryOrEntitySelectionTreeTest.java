package com.mindbox.pe.client.common.tree;

import junit.framework.Test;
import junit.framework.TestSuite;

import com.mindbox.pe.client.AbstractClientTestBase;
import com.mindbox.pe.client.EntityModelCacheFactory;
import com.mindbox.pe.model.GenericCategory;
import com.mindbox.pe.model.GenericEntityType;

public class GenericCategoryOrEntitySelectionTreeTest extends AbstractClientTestBase {

	public static void main(String[] args) {
		junit.textui.TestRunner.run(suite());
	}

	public static Test suite() {
		TestSuite suite = new TestSuite("GenericCategoryOrEntitySelectionTreeTest Tests");
		suite.addTestSuite(GenericCategoryOrEntitySelectionTreeTest.class);
		return suite;
	}

	private static class TreeImpl extends GenericCategorySelectionTree {

		public TreeImpl(GenericEntityType entityType, boolean showEntities) {
			super(entityType.getCategoryType(), showEntities, true, true);
		}

	}

	public GenericCategoryOrEntitySelectionTreeTest(String name) {
		super(name);
	}

	protected void setUp() throws Exception {
		super.setUp();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testAll() throws Exception {
		GenericCategory genericCategory = new GenericCategory(10, "product", 10);
		EntityModelCacheFactory.getInstance().addGenericCategory(genericCategory);
		GenericEntityType type = GenericEntityType.forName("product");
		GenericCategorySelectionTree tree = new TreeImpl(type, true);
		assertTrue(tree.tree.isRootVisible());

	}

}
