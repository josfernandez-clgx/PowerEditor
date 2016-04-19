package com.mindbox.pe.common.config;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Comparator;
import java.util.List;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import com.mindbox.pe.common.digest.DigestedObjectHolder;
import com.mindbox.pe.unittest.AbstractTestBase;

public class AbstractDigestedObjectHolderTest extends AbstractTestBase {

	private AbstractDigestedObjectHolder objectHolder;

	@Before
	public void setUp() throws Exception {
		objectHolder = new DigestedObjectHolder();

		objectHolder.addObject("one");
		objectHolder.addObject("two");
		objectHolder.addObject("three");
		objectHolder.addObject(new Integer(100));
	}

	// Note: No longer valid as getObjects() return typed list.
	//	@Test public void testGetObjectsWithInvalidClassReturnsUnmodifiableList() throws Exception {
	//		List<Float> list = objectHolder.getObjects(Float.class);
	//		try {
	//			list.add("something");
	//			fail("List is modifiable; UnsupportedOperationException should have been thrown");
	//		}
	//		catch (UnsupportedOperationException ex) {
	//			// ignore
	//		}
	//	}

	@Test
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

	@Test
	public void testGetObjectsWithInvalidClassReturnEmptyList() throws Exception {
		List<Long> list = objectHolder.getObjects(Long.class);
		assertNotNull(list);
		assertEquals(0, list.size());
	}

	@Test
	public void testGetObjectsWithValidClassReturnsCorrectList() throws Exception {
		List<String> list = objectHolder.getObjects(String.class);
		assertEquals(3, list.size());
		assertTrue(list.contains("one"));
		assertTrue(list.contains("two"));
		assertTrue(list.contains("three"));
	}

	@Test
	public void testGetObjectsWithComparatorWithValidClassReturnsSortedList() throws Exception {
		List<String> list = objectHolder.getObjects(String.class, new Comparator<String>() {
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

	@Test
	public void testGetClassKeySetReturnsCorrectSet() throws Exception {
		Set<Class<?>> set = objectHolder.getClassKeySet();
		assertEquals(2, set.size());
		assertTrue(set.contains(String.class));
		assertTrue(set.contains(Integer.class));
	}

	@Test
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
