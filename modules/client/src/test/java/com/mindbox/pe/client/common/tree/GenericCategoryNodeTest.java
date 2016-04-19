package com.mindbox.pe.client.common.tree;

import static com.mindbox.pe.client.ClientTestObjectMother.createGenericCategory;
import static com.mindbox.pe.client.ClientTestObjectMother.createGenericEntity;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.mindbox.pe.client.AbstractTestWithGenericEntityType;
import com.mindbox.pe.model.comparator.GenericCategoryComparator;

public class GenericCategoryNodeTest extends AbstractTestWithGenericEntityType {

	@Test
	public void testAddSortsChildren() throws Exception {
		GenericCategoryNode categoryNode = new GenericCategoryNode(createGenericCategory(entityType), GenericCategoryComparator.getSortByNameInstance());
		GenericCategoryNode child1 = new GenericCategoryNode(createGenericCategory(entityType));
		GenericCategoryNode child2 = new GenericCategoryNode(createGenericCategory(entityType));
		child1.getGenericCategory().setName("first");
		child2.getGenericCategory().setName("last");
		categoryNode.add(child2);
		categoryNode.add(child1);

		assertEquals(2, categoryNode.getChildCount());
		assertEquals("first", ((GenericCategoryNode) categoryNode.getChildAt(0)).getGenericCategory().getName());
		assertEquals("last", ((GenericCategoryNode) categoryNode.getChildAt(1)).getGenericCategory().getName());
	}

	@Test
	public void testAddAddsCategoryNodeBeforeEntityNodes() throws Exception {
		GenericCategoryNode categoryNode = new GenericCategoryNode(createGenericCategory(entityType), GenericCategoryComparator.getSortByNameInstance());
		GenericEntityNode entityChild = new GenericEntityNode(createGenericEntity(entityType));
		GenericCategoryNode categoryChild = new GenericCategoryNode(createGenericCategory(entityType));
		categoryNode.add(entityChild);
		categoryNode.add(categoryChild);

		assertEquals(2, categoryNode.getChildCount());
		assertTrue(categoryNode.getChildAt(0) instanceof GenericCategoryNode);
		assertTrue(categoryNode.getChildAt(1) instanceof GenericEntityNode);
	}
}
