package com.mindbox.pe.server.db;

import com.mindbox.pe.ObjectMother;
import com.mindbox.pe.common.ReflectionUtil;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * This requires a valid DB connection to work.
 * DO NOT run this as a part of automated unit testing.
 * @author kim
 *
 */
public class DBIdGeneratorTest extends DBTestBase {

	public static Test suite() {
		TestSuite suite = new TestSuite("DBIdGeneratorTest Tests");
		suite.addTestSuite(DBIdGeneratorTest.class);
		return suite;
	}

	public static void main(String[] args) {
		junit.textui.TestRunner.run(suite());
	}

	public DBIdGeneratorTest(String name) {
		super(name);
	}

	public void testFetchOneAndRestartGenerateCorrectID() throws Exception {
		// 
		int nextID = DBIdGenerator.getInstance().nextRuleID();
		System.out.println("nextID = " + nextID);

		// simulate server restart
		ReflectionUtil.setPrivate(DBIdGenerator.class, "instance", null);

		int newNextID = DBIdGenerator.getInstance().nextRuleID();
		System.out.println("newNextID = " + newNextID);
		assertTrue(newNextID > nextID);

		// simulate server restart
		ReflectionUtil.setPrivate(DBIdGenerator.class, "instance", null);

		int newNextID2 = DBIdGenerator.getInstance().nextRuleID();
		System.out.println("newNextID2 = " + newNextID2);
		assertTrue(newNextID2 > newNextID);
	}

	public void testSetNextIDHappyCase() throws Exception {
		DBIdGenerator.getInstance();

		int nextID = ObjectMother.createInt() + 100;
		DBIdGenerator.getInstance().setNextID(DBIdGenerator.RULE_ID, nextID, 10);

		assertEquals(nextID, DBIdGenerator.getInstance().nextRuleID());
		assertEquals(nextID + 1, DBIdGenerator.getInstance().nextRuleID());
		// simulate server restart
		ReflectionUtil.setPrivate(DBIdGenerator.class, "instance", null);

		int newNextID = DBIdGenerator.getInstance().nextRuleID();
		System.out.println("newNextID = " + newNextID);
		assertTrue(newNextID > nextID + 1);
	}
}
