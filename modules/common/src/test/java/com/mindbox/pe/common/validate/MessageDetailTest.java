package com.mindbox.pe.common.validate;

import static org.junit.Assert.assertTrue;

import java.io.Serializable;

import org.junit.Test;

import com.mindbox.pe.unittest.AbstractTestBase;


public class MessageDetailTest extends AbstractTestBase {

	@Test
	public void testMessageDetailIsSerializable() throws Exception {
		assertTrue(Serializable.class.isAssignableFrom(MessageDetail.class));
	}
}
