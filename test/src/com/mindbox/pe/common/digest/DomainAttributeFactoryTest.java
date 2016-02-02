package com.mindbox.pe.common.digest;

import junit.framework.TestSuite;

import org.xml.sax.Attributes;

import com.mindbox.pe.AbstractTestBase;
import com.mindbox.pe.model.DeployType;
import com.mindbox.pe.model.FloatDomainAttribute;

public class DomainAttributeFactoryTest extends AbstractTestBase {
	private DomainAttributeFactory factory;
	private TestAttributes attrs;
	
	public static TestSuite suite() {
		TestSuite suite = new TestSuite(DomainAttributeFactoryTest.class.getName());
		suite.addTestSuite(DomainAttributeFactoryTest.class);
		return suite;
	}
	
	protected void setUp() throws Exception {
		super.setUp();
		factory = new DomainAttributeFactory();
		attrs = new TestAttributes();
	}
	
	public DomainAttributeFactoryTest(String name) {
		super(name);
	}
	
	public void testAllDeployTypes() throws Exception {
		for (int i = 0; i < DeployType.VALID_VALUES.length; i++) {
			DeployType deployType = DeployType.VALID_VALUES[i];
			attrs.deployTypeVal = deployType.getName();
			if (deployType == DeployType.FLOAT || deployType == DeployType.CURRENCY || deployType == DeployType.PERCENT) {
				assertEquals(FloatDomainAttribute.class, factory.createObject(attrs).getClass());
			} else {
				assertNotEquals("Failed at deployType=" + deployType, FloatDomainAttribute.class, factory.createObject(attrs).getClass());
			}
		}
	}
	
	private static final class TestAttributes implements Attributes {
		String deployTypeVal;
		public String getValue(String uri, String localName) {
			if ("".equals(uri) && "DeployType".equals(localName)) {
				return deployTypeVal;
			}
			throw new IllegalArgumentException("Expected getValue(\"\", \"DeployType\"), found getValue(\"" + uri + "\", \"" + localName + "\")");
		}

		public int getLength() { throw new UnsupportedOperationException(); }
		public String getURI(int index) { throw new UnsupportedOperationException(); }
		public String getLocalName(int index) { throw new UnsupportedOperationException(); }
		public String getQName(int index) { throw new UnsupportedOperationException(); }
		public String getType(int index) { throw new UnsupportedOperationException(); }
		public String getValue(int index) { throw new UnsupportedOperationException(); }
		public int getIndex(String uri, String localName) { throw new UnsupportedOperationException(); }
		public int getIndex(String qName) { throw new UnsupportedOperationException(); }
		public String getType(String uri, String localName) { throw new UnsupportedOperationException(); }
		public String getType(String qName) { throw new UnsupportedOperationException(); }
		public String getValue(String qName) { throw new UnsupportedOperationException(); }
	}
}
