/*
 * Created on 2004. 10. 6.
 *
 */
package com.mindbox.pe.model.cbr;

import org.junit.Test;

import com.mindbox.pe.model.cbr.CBRAttributeValue;
import com.mindbox.pe.unittest.AbstractTestBase;

/**
 * @author Inna Nill
 * @since PowerEditor 4.1.0
 */
public class CBRAttributeValueTest extends AbstractTestBase {

	@Test
	public void testCBRAttributeValue() throws Exception {
		logBegin("testCBRAttributeValue");

		StringBuffer buff = new StringBuffer();
		CBRAttributeValue attrValue = new CBRAttributeValue();
		buff.append("Brand new attribute value class: " + attrValue);

		logger.info(buff.toString());
		logEnd("testCBRAttributeValue");
	}
}
