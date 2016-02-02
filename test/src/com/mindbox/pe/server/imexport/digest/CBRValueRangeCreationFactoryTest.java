package com.mindbox.pe.server.imexport.digest;

import junit.framework.Test;
import junit.framework.TestSuite;

import com.mindbox.pe.ObjectMother;
import com.mindbox.pe.model.CBRValueRange;

public class CBRValueRangeCreationFactoryTest extends AbstractCBRCachedObjectCreationFactoryTest {

	public static Test suite() {
		TestSuite suite = new TestSuite("CBRValueRangeCreationFactoryTest Tests");
		suite.addTestSuite(CBRValueRangeCreationFactoryTest.class);
		return suite;
	}

	public static void main(String[] args) {
		junit.textui.TestRunner.run(suite());
	}

	private CBRValueRangeCreationFactory factory;

	public CBRValueRangeCreationFactoryTest(String name) {
		super(name);
	}

	public void testCreateObjectWithCachedObject() throws Exception {
		CBRValueRange valueRange = ObjectMother.createCBRValueRange();
		factory.cbrManager.addCBRValueRange(valueRange);
		setUpMockForId(valueRange.getId());
		replayAllMocks();
		
		assertSame(valueRange, factory.createObject(attributesMock));
	}

	public void testCreateObjectWithNoCachedObject() throws Exception {
		int id = setUpMockForNewId();
		replayAllMocks();

		CBRValueRange valueRange = (CBRValueRange) factory.createObject(attributesMock);
		assertEquals(-1, valueRange.getId());
		assertEquals(String.valueOf(id), valueRange.getSymbol());
		assertEquals("", valueRange.getName());
		assertEquals("", valueRange.getDescription());
	}

	@Override
	public void setUp() throws Exception {
		super.setUp();
		this.factory = new CBRValueRangeCreationFactory();
	}
	
}
