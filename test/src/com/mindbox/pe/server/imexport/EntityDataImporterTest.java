package com.mindbox.pe.server.imexport;

import java.util.LinkedList;
import java.util.List;

import junit.framework.Test;
import junit.framework.TestSuite;

import com.mindbox.pe.AbstractTestWithGenericEntityType;
import com.mindbox.pe.ObjectMother;
import com.mindbox.pe.common.ReflectionUtil;
import com.mindbox.pe.server.imexport.digest.CategoryDigest;
import com.mindbox.pe.server.imexport.digest.Parent;

public class EntityDataImporterTest extends AbstractTestWithGenericEntityType {

	public static Test suite() {
		TestSuite suite = new TestSuite("EntityDataImporterTest Tests");
		suite.addTestSuite(EntityDataImporterTest.class);
		return suite;
	}

	public static void main(String[] args) {
		junit.textui.TestRunner.run(suite());
	}

	public EntityDataImporterTest(String name) {
		super(name);
	}

	public void testIsParentYetToBeProcessedHappyCase() throws Exception {
		List<CategoryDigest> list = new LinkedList<CategoryDigest>();
		CategoryDigest categoryDigest = new CategoryDigest();
		categoryDigest.setId(100);
		categoryDigest.setType(entityType.getName());
		Parent parent = new Parent();
		parent.setId(ObjectMother.createInt());
		categoryDigest.addObject(parent);
		list.add(categoryDigest);

		// add some category digests
		for (int i = 0; i < 10; i++) {
			CategoryDigest categoryDigestTemp = new CategoryDigest();
			categoryDigestTemp.setId(ObjectMother.createInt());
			categoryDigestTemp.setType("channel");
		}

		CategoryDigest categoryDigest2 = new CategoryDigest();
		categoryDigest2.setId(parent.getId());
		categoryDigest2.setType(entityType.getName());
		list.add(categoryDigest2);

		testIsParentYetToBeProcessed(categoryDigest, list, 1, true);
	}

	private void testIsParentYetToBeProcessed(CategoryDigest categoryDigest, List<CategoryDigest> categoryDigestList, int startIndex,
			boolean expected) throws Exception {
		Boolean result = (Boolean) ReflectionUtil.executeStaticPrivate(EntityDataImporter.class, "isParentYetToBeProcessed", new Class[] {
				CategoryDigest.class,
				List.class,
				int.class,
				List.class }, new Object[] { categoryDigest, categoryDigestList, new Integer(startIndex), null });
		assertEquals(expected, result.booleanValue());
	}

}
