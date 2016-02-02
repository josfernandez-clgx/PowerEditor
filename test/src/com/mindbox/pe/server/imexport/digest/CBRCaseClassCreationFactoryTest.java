package com.mindbox.pe.server.imexport.digest;

import junit.framework.Test;
import junit.framework.TestSuite;

import com.mindbox.pe.ObjectMother;
import com.mindbox.pe.model.CBRCaseClass;

public class CBRCaseClassCreationFactoryTest extends AbstractCBRCachedObjectCreationFactoryTest {

	public static Test suite() {
		TestSuite suite = new TestSuite("CBRCaseClassCreationFactoryTest Tests");
		suite.addTestSuite(CBRCaseClassCreationFactoryTest.class);
		return suite;
	}

	public static void main(String[] args) {
		junit.textui.TestRunner.run(suite());
	}

	private CBRCaseClassCreationFactory factory;

	public CBRCaseClassCreationFactoryTest(String name) {
		super(name);
	}

	public void testCreateObjectWithCachedObject() throws Exception {
		CBRCaseClass caseClass = ObjectMother.createCBRCaseClass();
		factory.cbrManager.addCBRCaseClass(caseClass);
		setUpMockForId(caseClass.getId());
		replayAllMocks();
		
		assertSame(caseClass, factory.createObject(attributesMock));
	}

	public void testCreateObjectWithNoCachedObject() throws Exception {
		int id = setUpMockForNewId();
		replayAllMocks();

		CBRCaseClass caseClass = (CBRCaseClass) factory.createObject(attributesMock);
		assertEquals(-1, caseClass.getId());
		assertEquals(String.valueOf(id), caseClass.getSymbol());
		assertEquals("", caseClass.getName());
		assertEquals(null, caseClass.getDescription());
	}

	@Override
	public void setUp() throws Exception {
		super.setUp();
		this.factory = new CBRCaseClassCreationFactory();
	}
	
}
