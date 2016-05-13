package com.mindbox.pe.server.imexport;

import static com.mindbox.pe.unittest.TestObjectMother.createInt;
import static org.junit.Assert.assertEquals;

import java.util.LinkedList;
import java.util.List;

import org.junit.Test;

import com.mindbox.pe.common.ReflectionUtil;
import com.mindbox.pe.server.AbstractTestWithGenericEntityType;
import com.mindbox.pe.xsd.data.EntityDataElement.Category;
import com.mindbox.pe.xsd.data.EntityDataElement.Category.Parent;

public class EntityDataImporterTest extends AbstractTestWithGenericEntityType {

	private void testIsParentYetToBeProcessed(Category category, List<Category> categoryList, int startIndex, boolean expected) throws Exception {
		Boolean result = (Boolean) ReflectionUtil.executeStaticPrivate(
				EntityDataImporter.class,
				"isParentYetToBeProcessed",
				new Class[] { Category.class, List.class, int.class, List.class },
				new Object[] { category, categoryList, new Integer(startIndex), null });
		assertEquals(expected, result.booleanValue());
	}

	@Test
	public void testIsParentYetToBeProcessedHappyCase() throws Exception {
		final List<Category> list = new LinkedList<Category>();
		Category category = new Category();
		category.setId(100);
		category.setType(entityType.getName());
		Parent parent = new Parent();
		parent.setParentID(createInt());
		category.getParent().add(parent);
		list.add(category);

		Category category2 = new Category();
		category2.setId(parent.getParentID());
		category2.setType(entityType.getName());
		list.add(category2);

		testIsParentYetToBeProcessed(category, list, 1, true);
	}

}
