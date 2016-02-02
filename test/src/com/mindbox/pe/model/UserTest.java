package com.mindbox.pe.model;

import junit.framework.Test;
import junit.framework.TestSuite;

import com.mindbox.pe.AbstractTestWithTestConfig;
import com.mindbox.pe.model.admin.UserPassword;
import com.mindbox.pe.server.model.User;

/**
 * Unit tests for {@link User}.
 * @author Geneho Kim
 * @since 5.1.0
 */
public class UserTest extends AbstractTestWithTestConfig {

	public static void main(String[] args) {
		junit.textui.TestRunner.run(suite());
	}

	public static Test suite() {
		TestSuite suite = new TestSuite("UserTest Tests");
		suite.addTestSuite(UserTest.class);
		return suite;
	}

	private User user = null;

	public UserTest(String name) {
		super(name);
	}

    public void testGetCurrentPassword() throws Exception {
        user.setPassword("first");
        user.setPassword("second");
        assertTrue(user.getCurrentPassword().equals("second"));
        
    }

    /**
     * Assumes test config with history of 3
     * @throws Exception
     */
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
    
	protected void setUp() throws Exception {
		super.setUp();
        config.resetConfiguration();
        config.initServer("test/config/PowerEditorConfiguration.xml");
		user = new User("test", "test", null, false, 0, null);
	}

	protected void tearDown() throws Exception {
		super.tearDown();
        config.resetConfiguration();        
	}
}
