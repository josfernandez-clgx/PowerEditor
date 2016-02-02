package com.mindbox.pe.server.imexport.digest;

import junit.framework.Test;
import junit.framework.TestSuite;

import com.mindbox.pe.ObjectMother;
import com.mindbox.pe.model.CBRScoringFunction;

public class CBRScoringFunctionCreationFactoryTest extends AbstractCBRCachedObjectCreationFactoryTest {

	public static Test suite() {
		TestSuite suite = new TestSuite("CBRScoringFunctionCreationFactoryTest Tests");
		suite.addTestSuite(CBRScoringFunctionCreationFactoryTest.class);
		return suite;
	}

	public static void main(String[] args) {
		junit.textui.TestRunner.run(suite());
	}

	private CBRScoringFunctionCreationFactory factory;

	public CBRScoringFunctionCreationFactoryTest(String name) {
		super(name);
	}

	public void testCreateObjectWithCachedObject() throws Exception {
		CBRScoringFunction function = ObjectMother.createCBRScoringFunction();
		factory.cbrManager.addCBRScoringFunction(function);
		setUpMockForId(function.getId());
		replayAllMocks();
		
		assertSame(function, factory.createObject(attributesMock));
	}

	public void testCreateObjectWithNoCachedObject() throws Exception {
		int id = setUpMockForNewId();
		replayAllMocks();

		CBRScoringFunction function = (CBRScoringFunction) factory.createObject(attributesMock);
		assertEquals(-1, function.getId());
		assertEquals(String.valueOf(id), function.getSymbol());
		assertEquals("", function.getName());
		assertEquals(null, function.getDescription());
	}

	@Override
	public void setUp() throws Exception {
		super.setUp();
		this.factory = new CBRScoringFunctionCreationFactory();
	}
	
}
