package com.mindbox.pe.model.admin;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Iterator;

import org.junit.Before;
import org.junit.Test;

import com.mindbox.pe.unittest.AbstractTestBase;

/**
 * Unit tests for {@link UserData}.
 * 
 * @author Geneho Kim
 * @since 5.1.0
 */
public class UserDataTest extends AbstractTestBase {

	private UserData userData = null;

	@Before
	public void setUp() throws Exception {
		userData = new UserData("test", "test", null, null, false, 0, null, null);
	}

	@Test
	public void testGetCurrentPassword() throws Exception {
		userData.setCurrentPassword("first", 1);
		userData.setCurrentPassword("second", 1);
		assertTrue(userData.getCurrentPassword().equals("second"));
	}

	@Test
	public void testSetPasswordWithHistory() throws Exception {
		userData.setCurrentPassword("first", 3);
		assertNotNull(userData.getPasswordHistory());
		assertEquals(userData.getPasswordHistory().size(), 1);

		userData.setCurrentPassword("second", 3);
		assertEquals(userData.getPasswordHistory().size(), 2);

		userData.setCurrentPassword("third", 3);
		assertEquals(userData.getPasswordHistory().size(), 3);

		userData.setCurrentPassword("fourth", 3);
		assertEquals(userData.getPasswordHistory().size(), 4);

		userData.setCurrentPassword("fifth", 3);
		assertEquals(userData.getPasswordHistory().size(), 4);

		userData.setCurrentPassword("sixth", 3);
		assertEquals(userData.getPasswordHistory().size(), 4);

		for (Iterator<UserPassword> i = userData.getPasswordHistory().iterator(); i.hasNext();) {
			UserPassword up = i.next();
			assertFalse(up.equals("first") || up.equals("second"));
		}
	}

	@Test
	public void testSetPasswordWithNoHistory() throws Exception {
		userData.setCurrentPassword("first", 0);
		assertNotNull(userData.getPasswordHistory());
		assertEquals(userData.getPasswordHistory().size(), 1);

		userData.setCurrentPassword("second", 0);
		assertEquals(userData.getPasswordHistory().size(), 1);

		UserPassword password = (UserPassword) userData.getPasswordHistory().get(0);
		assertTrue(password.getPassword().equals("second"));
	}

}
