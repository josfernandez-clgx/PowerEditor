package com.mindbox.pe.model.admin;

import static com.mindbox.pe.common.CommonTestObjectMother.createPrivilege;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.LinkedList;
import java.util.List;

import org.junit.Test;

import com.mindbox.pe.unittest.AbstractTestBase;

/**
 * Unit tests for Role
 * 
 * @author MindBox
 * @since 5.1.0
 */
public class RoleTest extends AbstractTestBase {

	@Test
	public void testCopyConstructor() throws Exception {
		List<Privilege> sourcePriv = new LinkedList<Privilege>();
		Privilege p = createPrivilege();
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

}
