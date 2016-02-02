package com.mindbox.pe.client.common.tree;

import junit.framework.Test;
import junit.framework.TestSuite;

import com.mindbox.pe.AbstractTestWithGenericEntityType;
import com.mindbox.pe.ObjectMother;
import com.mindbox.pe.model.comparator.GenericCategoryComparator;

public class GenericCategoryNodeTest extends AbstractTestWithGenericEntityType {

	public static Test suite() {
		TestSuite suite = new TestSuite("GenericCategoryNodeTest Tests");
		suite.addTestSuite(GenericCategoryNodeTest.class);
		return suite;
	}

	public static void main(String[] args) {
		junit.textui.TestRunner.run(suite());
	}

	public GenericCategoryNodeTest(String name) {
		super(name);
	}

	public void testAddSortsChildren() throws Exception {
		GenericCategoryNode categoryNode = new GenericCategoryNode(
				ObjectMother.createGenericCategory(entityType),
				GenericCategoryComparator.getSortByNameInstance());
		GenericCategoryNode child1 = new GenericCategoryNode(ObjectMother.createGenericCategory(entityType));
		GenericCategoryNode child2 = new GenericCategoryNode(ObjectMother.createGenericCategory(entityType));
		child1.getGenericCategory().setName("first");
		child2.getGenericCategory().setName("last");
		categoryNode.add(child2);
		categoryNode.add(child1);

		assertEquals(2, categoryNode.getChildCount());
		assertEquals("first", ((GenericCategoryNode) categoryNode.getChildAt(0)).getGenericCategory().getName());
		assertEquals("last", ((GenericCategoryNode) categoryNode.getChildAt(1)).getGenericCategory().getName());
	}

	public void testAddAddsCategoryNodeBeforeEntityNodes() throws Exception {
		GenericCategoryNode categoryNode = new GenericCategoryNode(
				ObjectMother.createGenericCategory(entityType),
				GenericCategoryComparator.getSortByNameInstance());
		GenericEntityNode entityChild = new GenericEntityNode(ObjectMother.createGenericEntity(entityType));
		GenericCategoryNode categoryChild = new GenericCategoryNode(ObjectMother.createGenericCategory(entityType));
		categoryNode.add(entityChild);
		categoryNode.add(categoryChild);

		assertEquals(2, categoryNode.getChildCount());
		assertTrue(categoryNode.getChildAt(0) instanceof GenericCategoryNode);
		assertTrue(categoryNode.getChildAt(1) instanceof GenericEntityNode);
	}
}
