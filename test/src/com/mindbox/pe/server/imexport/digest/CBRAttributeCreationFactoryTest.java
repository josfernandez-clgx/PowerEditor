package com.mindbox.pe.server.imexport.digest;

import junit.framework.Test;
import junit.framework.TestSuite;

import com.mindbox.pe.ObjectMother;
import com.mindbox.pe.model.CBRAttribute;

public class CBRAttributeCreationFactoryTest extends AbstractCBRCachedObjectCreationFactoryTest {

	public static Test suite() {
		TestSuite suite = new TestSuite("CBRAttributeCreationFactoryTest Tests");
		suite.addTestSuite(CBRAttributeCreationFactoryTest.class);
		return suite;
	}

	public static void main(String[] args) {
		junit.textui.TestRunner.run(suite());
	}

	private CBRAttributeCreationFactory factory;

	public CBRAttributeCreationFactoryTest(String name) {
		super(name);
	}

	public void testCreateObjectWithCachedObject() throws Exception {
		CBRAttribute attribute = ObjectMother.createCBRAttribute();
		factory.cbrManager.addCBRAttribute(attribute);
		setUpMockForId(attribute.getId());
		replayAllMocks();
		
		CBRAttribute attributeType2 = (CBRAttribute) factory.createObject(attributesMock);
		assertSame(attribute, attributeType2);
	}

	public void testCreateObjectWithNoCachedObject() throws Exception {
		int id = setUpMockForNewId();
		replayAllMocks();

		CBRAttribute attribute = (CBRAttribute) factory.createObject(attributesMock);
		assertEquals(-1, attribute.getId());
		assertEquals(String.valueOf(id), attribute.getName());
		assertEquals("", attribute.getDescription());
	}

	@Override
	public void setUp() throws Exception {
		super.setUp();
		this.factory = new CBRAttributeCreationFactory();
	}
	
	@Override
	protected void tearDown() throws Exception {
		factory.cbrManager.startLoading();
		super.tearDown();
	}
}
