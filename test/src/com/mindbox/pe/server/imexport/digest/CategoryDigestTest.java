package com.mindbox.pe.server.imexport.digest;

import junit.framework.Test;
import junit.framework.TestSuite;

import com.mindbox.pe.AbstractTestBase;

public class CategoryDigestTest extends AbstractTestBase {

	public static void main(String[] args) {
		junit.textui.TestRunner.run(suite());
	}

	public static Test suite() {
		TestSuite suite = new TestSuite("CategoryDigestTest Tests");
		suite.addTestSuite(CategoryDigestTest.class);
		return suite;
	}

	private CategoryDigest categoryDigest;
	
	public CategoryDigestTest(String name) {
		super(name);
	}

	public void testConstructorInitializesParentIDToNegativeOne() throws Exception {
		assertEquals(-1, categoryDigest.getParentID());
	}

	public void testConstructorInitializesParentsToEmptyList() throws Exception {
		assertTrue(categoryDigest.getParents().isEmpty());
	}

	public void testGetParetnsDoNotReturnNull() throws Exception {
		assertNotNull(categoryDigest.getParents());
		assertEquals(0, categoryDigest.getParents().size());
	}
	
	public void testIsRootPositiveCaseForEmptyParentList() throws Exception {
		assertTrue(categoryDigest.isRoot());
	}

	public void testIsRootPositiveCaseForOneParentOfNegativeParentID() throws Exception {
		Parent parent = new Parent();
		parent.setId(-1);
		categoryDigest.addObject(parent);
		assertTrue(new CategoryDigest().isRoot());
	}

	public void testIsRootNegativeCaseForNonNegativeParentID() throws Exception {
		categoryDigest.setParentID(1);
		assertFalse(categoryDigest.isRoot());
	}

	public void testIsRootNegativeCaseForOneParentOfNonNegativeParentID() throws Exception {
		Parent parent = new Parent();
		parent.setId(0);
		categoryDigest.addObject(parent);
		assertFalse(categoryDigest.isRoot());
		parent.setId(1);
		assertFalse(categoryDigest.isRoot());
	}

	public void testIsRootNegativeCaseForMultipleParents() throws Exception {
		Parent parent = new Parent();
		parent.setId(0);
		categoryDigest.addObject(parent);
		categoryDigest.addObject(new Parent());
		assertFalse(categoryDigest.isRoot());
	}

	protected void setUp() throws Exception {
		super.setUp();
		categoryDigest = new CategoryDigest();
	}

	protected void tearDown() throws Exception {
		// Tear downs for CategoryDigestTest
		super.tearDown();
	}
}
