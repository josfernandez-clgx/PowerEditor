package com.mindbox.pe.model.admin;

import java.util.LinkedList;
import java.util.List;

import junit.framework.Test;
import junit.framework.TestSuite;

import com.mindbox.pe.AbstractTestBase;
import com.mindbox.pe.ObjectMother;

/**
 * Unit tests for Role 
 * @author MindBox
 * @since 5.1.0
 */
public class RoleTest extends AbstractTestBase {

	public static void main(String[] args) {
		junit.textui.TestRunner.run(suite());
	}

	public static Test suite() {
		TestSuite suite = new TestSuite("RoleTest Tests");
		suite.addTestSuite(RoleTest.class);
		return suite;
	}


	public RoleTest(String name) {
		super(name);
	}

    public void testCopyConstructor() throws Exception {
        List<Privilege> sourcePriv = new LinkedList<Privilege>();
        Privilege p =  ObjectMother.createPrivilege();
        Role sourceRole = new Role(-1, "source", sourcePriv);
        sourceRole.addPrivilege(p);
        
        Role clonedRole = new Role(sourceRole);

        assertTrue(sourceRole.getPrivileges().contains(p));        
        assertTrue(clonedRole.getPrivileges().contains(p));
        
        Privilege newPriv = new Privilege(-1, "test", "test", 999); 
        sourceRole.addPrivilege(newPriv);
        assertTrue(sourceRole.getPrivileges().contains(newPriv));        
        assertFalse(clonedRole.getPrivileges().contains(newPriv));

        Privilege newClonePriv = new Privilege(-2, "test2", "test2", 12345);
        clonedRole.addPrivilege(newClonePriv);
        assertFalse(sourceRole.getPrivileges().contains(newClonePriv));        
        assertTrue(clonedRole.getPrivileges().contains(newClonePriv));
    }
    
	protected void setUp() throws Exception {
		super.setUp();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}
}
