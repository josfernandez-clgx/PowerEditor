package com.mindbox.pe.server.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.mindbox.pe.model.admin.UserPassword;
import com.mindbox.pe.server.AbstractTestWithTestConfig;

/**
 * Unit tests for {@link User}.
 * 
 * @author Geneho Kim
 * @since 5.1.0
 */
public class UserTest extends AbstractTestWithTestConfig {

	private User user = null;

	@Test
	public void testGetCurrentPassword() throws Exception {
		user.setPassword("first");
		user.setPassword("second");
		assertTrue(user.getCurrentPassword().equals("second"));

	}

	/**
	 * Assumes test config with history of 3
	 * 
	 * @throws Exception
	 */
	@Test
	public void testSetPasswordWithHistory() throws Exception {
		user.setPassword("first");
		assertNotNull(user.getPasswordHistory());
		assertEquals(user.getPasswordHistory().size(), 1);

		user.setPassword("second");
		assertEquals(user.getPasswordHistory().size(), 2);

		user.setPassword("third");
		assertEquals(user.getPasswordHistory().size(), 3);

		user.setPassword("fourth");
		assertEquals(user.getPasswordHistory().size(), 4);

		user.setPassword("fifth");
		assertEquals(user.getPasswordHistory().size(), 4);

		user.setPassword("sixth");
		assertEquals(user.getPasswordHistory().size(), 4);

		for (UserPassword up : user.getPasswordHistory()) {
			assertFalse(up.equals("first") || up.equals("second"));
		}

	}

	public void setUp() throws Exception {
		super.setUp();
		config.resetConfiguration();
		config.initServer("src/test/config/PowerEditorConfiguration.xml");
		user = new User("test", "test", null, false, 0, null, null);
	}
}
