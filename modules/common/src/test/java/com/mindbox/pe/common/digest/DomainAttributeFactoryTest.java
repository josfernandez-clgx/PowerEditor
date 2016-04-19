package com.mindbox.pe.common.digest;

import static com.mindbox.pe.unittest.UnitTestHelper.assertNotEquals;
import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;
import org.xml.sax.Attributes;

import com.mindbox.pe.model.DeployType;
import com.mindbox.pe.model.domain.FloatDomainAttribute;
import com.mindbox.pe.unittest.AbstractTestBase;

public class DomainAttributeFactoryTest extends AbstractTestBase {

	private static final class TestAttributes implements Attributes {
		String deployTypeVal;

		public int getIndex(String qName) {
			throw new UnsupportedOperationException();
		}

		public int getIndex(String uri, String localName) {
			throw new UnsupportedOperationException();
		}

		public int getLength() {
			throw new UnsupportedOperationException();
		}

		public String getLocalName(int index) {
			throw new UnsupportedOperationException();
		}

		public String getQName(int index) {
			throw new UnsupportedOperationException();
		}

		public String getType(int index) {
			throw new UnsupportedOperationException();
		}

		public String getType(String qName) {
			throw new UnsupportedOperationException();
		}

		public String getType(String uri, String localName) {
			throw new UnsupportedOperationException();
		}

		public String getURI(int index) {
			throw new UnsupportedOperationException();
		}

		public String getValue(int index) {
			throw new UnsupportedOperationException();
		}

		public String getValue(String qName) {
			throw new UnsupportedOperationException();
		}

		public String getValue(String uri, String localName) {
			if ("".equals(uri) && "DeployType".equals(localName)) {
				return deployTypeVal;
			}
			throw new IllegalArgumentException("Expected getValue(\"\", \"DeployType\"), found getValue(\"" + uri + "\", \"" + localName + "\")");
		}
	}

	private DomainAttributeFactory factory;
	private TestAttributes attrs;

	@Before
	public void setUp() throws Exception {
		factory = new DomainAttributeFactory();
		attrs = new TestAttributes();
	}

	@Test
	public void testAllDeployTypes() throws Exception {
		for (int i = 0; i < DeployType.VALID_VALUES.length; i++) {
			DeployType deployType = DeployType.VALID_VALUES[i];
			attrs.deployTypeVal = deployType.getName();
			if (deployType == DeployType.FLOAT || deployType == DeployType.CURRENCY || deployType == DeployType.PERCENT) {
				assertEquals(FloatDomainAttribute.class, factory.createObject(attrs).getClass());
			}
			else {
				assertNotEquals("Failed at deployType=" + deployType, FloatDomainAttribute.class, factory.createObject(attrs).getClass());
			}
		}
	}
}
