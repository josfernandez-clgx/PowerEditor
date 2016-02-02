package com.mindbox.pe.common.config;

import java.util.Comparator;
import java.util.List;
import java.util.Set;

import junit.framework.Test;
import junit.framework.TestSuite;

import com.mindbox.pe.AbstractTestBase;
import com.mindbox.pe.common.digest.DigestedObjectHolder;

public class AbstractDigestedObjectHolderTest extends AbstractTestBase {

	public static Test suite() {
		TestSuite suite = new TestSuite("AbstractDigestedObjectHolderTest Tests");
		suite.addTestSuite(AbstractDigestedObjectHolderTest.class);
		return suite;
	}

	private AbstractDigestedObjectHolder objectHolder;

	public AbstractDigestedObjectHolderTest(String name) {
		super(name);
	}

	protected void setUp() throws Exception {
		super.setUp();
		objectHolder = new DigestedObjectHolder();

		objectHolder.addObject("one");
		objectHolder.addObject("two");
		objectHolder.addObject("three");
		objectHolder.addObject(new Integer(100));
	}

	protected void tearDown() throws Exception {
		super.tearDown();
		objectHolder = null;
	}

	// Note: No longer valid as getObjects() return typed list.
//	public void testGetObjectsWithInvalidClassReturnsUnmodifiableList() throws Exception {
//		List<Float> list = objectHolder.getObjects(Float.class);
//		try {
//			list.add("something");
//			fail("List is modifiable; UnsupportedOperationException should have been thrown");
//		}
//		catch (UnsupportedOperationException ex) {
//			// ignore
//		}
//	}

	public void testGetObjectsWithValidClassReturnsUnmodifiableList() throws Exception {
		List<String> list = objectHolder.getObjects(String.class);
		try {
			list.add("something");
			fail("List is modifiable; UnsupportedOperationException should have been thrown");
		}
		catch (UnsupportedOperationException ex) {
			// ignore
		}
	}

	public void testGetObjectsWithInvalidClassReturnEmptyList() throws Exception {
		List<Long> list = objectHolder.getObjects(Long.class);
		assertNotNull(list);
		assertEquals(0, list.size());
	}

	public void testGetObjectsWithValidClassReturnsCorrectList() throws Exception {
		List<String> list = objectHolder.getObjects(String.class);
		assertEquals(3, list.size());
		assertTrue(list.contains("one"));
		assertTrue(list.contains("two"));
		assertTrue(list.contains("three"));
	}

	public void testGetObjectsWithComparatorWithValidClassReturnsSortedList() throws Exception {
		List<String> list = objectHolder.getObjects(String.class, new Comparator<String>(){
			public int compare(String o1, String o2) {
				if (o1 == o2) return 0;
				return o1.compareTo(o2);
			}
		});
		assertEquals(3, list.size());
		assertEquals("one", list.get(0));
		assertEquals("three", list.get(1));
		assertEquals("two", list.get(2));
	}

	public void testGetClassKeySetReturnsCorrectSet() throws Exception {
		Set<Class<?>> set = objectHolder.getClassKeySet();
		assertEquals(2, set.size());
		assertTrue(set.contains(String.class));
		assertTrue(set.contains(Integer.class));
	}

	public void testGetClassKeySetReturnsUnmodifiableSet() throws Exception {
		Set<Class<?>> set = objectHolder.getClassKeySet();
		try {
			set.add(String.class);
			fail("Set is modifiable; UnsupportedOperationException should have been thrown");
		}
		catch (UnsupportedOperationException ex) {
			// ignore
		}
	}
}
