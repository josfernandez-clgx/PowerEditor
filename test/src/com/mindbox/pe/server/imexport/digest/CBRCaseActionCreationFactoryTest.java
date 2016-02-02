package com.mindbox.pe.server.imexport.digest;

import junit.framework.Test;
import junit.framework.TestSuite;

import com.mindbox.pe.ObjectMother;
import com.mindbox.pe.model.CBRCaseAction;

public class CBRCaseActionCreationFactoryTest extends AbstractCBRCachedObjectCreationFactoryTest {

	public static Test suite() {
		TestSuite suite = new TestSuite("CBRCaseActionCreationFactoryTest Tests");
		suite.addTestSuite(CBRCaseActionCreationFactoryTest.class);
		return suite;
	}

	public static void main(String[] args) {
		junit.textui.TestRunner.run(suite());
	}

	private CBRCaseActionCreationFactory factory;

	public CBRCaseActionCreationFactoryTest(String name) {
		super(name);
	}

	public void testCreateObjectWithCachedObject() throws Exception {
		CBRCaseAction caseAction = ObjectMother.createCBRCaseAction();
		factory.cbrManager.addCBRCaseAction(caseAction);
		setUpMockForId(caseAction.getId());
		replayAllMocks();
		
		assertSame(caseAction, factory.createObject(attributesMock));
	}

	public void testCreateObjectWithNoCachedObject() throws Exception {
		int id = setUpMockForNewId();
		replayAllMocks();

		CBRCaseAction caseAction = (CBRCaseAction) factory.createObject(attributesMock);
		assertEquals(-1, caseAction.getId());
		assertEquals(String.valueOf(id), caseAction.getSymbol());
		assertEquals("", caseAction.getName());
		assertEquals(null, caseAction.getDescription());
	}

	@Override
	public void setUp() throws Exception {
		super.setUp();
		this.factory = new CBRCaseActionCreationFactory();
	}
}
