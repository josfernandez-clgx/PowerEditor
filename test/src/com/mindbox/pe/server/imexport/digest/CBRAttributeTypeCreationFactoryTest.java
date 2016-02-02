package com.mindbox.pe.server.imexport.digest;

import junit.framework.Test;
import junit.framework.TestSuite;

import com.mindbox.pe.ObjectMother;
import com.mindbox.pe.model.CBRAttributeType;

public class CBRAttributeTypeCreationFactoryTest extends AbstractCBRCachedObjectCreationFactoryTest {

	public static Test suite() {
		TestSuite suite = new TestSuite("CBRAttributeTypeCreationFactoryTest Tests");
		suite.addTestSuite(CBRAttributeTypeCreationFactoryTest.class);
		return suite;
	}

	public static void main(String[] args) {
		junit.textui.TestRunner.run(suite());
	}

	private CBRAttributeTypeCreationFactory factory;

	public CBRAttributeTypeCreationFactoryTest(String name) {
		super(name);
	}

	public void testCreateObjectWithCachedObject() throws Exception {
		CBRAttributeType attributeType = ObjectMother.createCBRAttributeType();
		factory.cbrManager.addCBRAttributeType(attributeType);
		setUpMockForId(attributeType.getId());
		replayAllMocks();
		
		CBRAttributeType attributeType2 = (CBRAttributeType) factory.createObject(attributesMock);
		assertSame(attributeType, attributeType2);
	}

	public void testCreateObjectWithNoCachedObject() throws Exception {
		int id = setUpMockForNewId();
		replayAllMocks();

		CBRAttributeType attributeType = (CBRAttributeType) factory.createObject(attributesMock);
		assertEquals(-1, attributeType.getId());
		assertEquals(String.valueOf(id), attributeType.getSymbol());
		assertEquals("", attributeType.getName());
		assertEquals(null, attributeType.getDescription());
	}

	@Override
	public void setUp() throws Exception {
		super.setUp();
		this.factory = new CBRAttributeTypeCreationFactory();
	}
	
	@Override
	protected void tearDown() throws Exception {
		factory.cbrManager.startLoading();
		super.tearDown();
	}
}
