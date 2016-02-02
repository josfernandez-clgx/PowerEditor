package com.mindbox.pe.model.admin;

import java.util.Iterator;

import junit.framework.Test;
import junit.framework.TestSuite;

import com.mindbox.pe.AbstractTestBase;

/**
 * Unit tests for {@link UserData}.
 * @author Geneho Kim
 * @since 5.1.0
 */
public class UserDataTest extends AbstractTestBase {

	public static void main(String[] args) {
		junit.textui.TestRunner.run(suite());
	}

	public static Test suite() {
		TestSuite suite = new TestSuite("UserDataTest Tests");
		suite.addTestSuite(UserDataTest.class);
		return suite;
	}

	private UserData userData = null;

	public UserDataTest(String name) {
		super(name);
	}

    public void testGetCurrentPassword() throws Exception {
        userData.setCurrentPassword("first", 1);
        userData.setCurrentPassword("second", 1);
        assertTrue(userData.getCurrentPassword().equals("second"));
        
    }
	public void testSetPasswordWithNoHistory() throws Exception {
        userData.setCurrentPassword("first", 0);
        assertNotNull(userData.getPasswordHistory());
        assertEquals(userData.getPasswordHistory().size(), 1);
        
        userData.setCurrentPassword("second", 0);
        assertEquals(userData.getPasswordHistory().size(), 1);
        
        UserPassword password = (UserPassword)userData.getPasswordHistory().get(0);
        assertTrue(password.getPassword().equals("second"));
	}

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
    
	protected void setUp() throws Exception {
		super.setUp();
		userData = new UserData("test", "test", null, null, false, 0, null);
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}
}
