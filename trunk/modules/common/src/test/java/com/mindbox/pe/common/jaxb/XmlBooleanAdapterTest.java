package com.mindbox.pe.common.jaxb;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import com.mindbox.pe.common.jaxb.XmlBooleanAdapter;

public class XmlBooleanAdapterTest {

	@Test
	public void unmarshal_PositiveCaseYes() throws Exception {
		assertEquals(Boolean.TRUE, xmlBooleanAdapter.unmarshal("YES"));
		assertEquals(Boolean.TRUE, xmlBooleanAdapter.unmarshal("Yes"));
		assertEquals(Boolean.TRUE, xmlBooleanAdapter.unmarshal("yes"));
	}

	@Test
	public void unmarshal_PositiveCaseTrue() throws Exception {
		assertEquals(Boolean.TRUE, xmlBooleanAdapter.unmarshal("TRUE"));
		assertEquals(Boolean.TRUE, xmlBooleanAdapter.unmarshal("True"));
		assertEquals(Boolean.TRUE, xmlBooleanAdapter.unmarshal("true"));
	}

	@Before
	public void setUp() throws Exception {
		xmlBooleanAdapter = new XmlBooleanAdapter();
	}

	private XmlBooleanAdapter xmlBooleanAdapter;
}
