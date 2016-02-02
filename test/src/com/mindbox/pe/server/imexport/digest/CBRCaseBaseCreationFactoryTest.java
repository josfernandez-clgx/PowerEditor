package com.mindbox.pe.server.imexport.digest;

import junit.framework.Test;
import junit.framework.TestSuite;

import com.mindbox.pe.ObjectMother;
import com.mindbox.pe.model.CBRCaseBase;

public class CBRCaseBaseCreationFactoryTest extends AbstractCBRCachedObjectCreationFactoryTest {

	public static Test suite() {
		TestSuite suite = new TestSuite("CBRCaseBaseCreationFactoryTest Tests");
		suite.addTestSuite(CBRCaseBaseCreationFactoryTest.class);
		return suite;
	}

	public static void main(String[] args) {
		junit.textui.TestRunner.run(suite());
	}

	private CBRCaseBaseCreationFactory factory;

	public CBRCaseBaseCreationFactoryTest(String name) {
		super(name);
	}

	public void testCreateObjectWithCachedObject() throws Exception {
		CBRCaseBase caseBase = ObjectMother.createCBRCaseBase();
		factory.cbrManager.addCBRCaseBase(caseBase);
		setUpMockForId(caseBase.getId());
		replayAllMocks();
		
		assertSame(caseBase, factory.createObject(attributesMock));
	}

	public void testCreateObjectWithNoCachedObject() throws Exception {
		int id = setUpMockForNewId();
		replayAllMocks();

		CBRCaseBase caseBase = (CBRCaseBase) factory.createObject(attributesMock);
		assertEquals(-1, caseBase.getId());
		assertEquals(String.valueOf(id), caseBase.getName());
		assertEquals("", caseBase.getDescription());
	}

	@Override
	public void setUp() throws Exception {
		super.setUp();
		this.factory = new CBRCaseBaseCreationFactory();
	}
	
}
