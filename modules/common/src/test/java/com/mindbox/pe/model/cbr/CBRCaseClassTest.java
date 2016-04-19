/*
 * Created on 2004. 10. 6.
 *
 */
package com.mindbox.pe.model.cbr;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.mindbox.pe.model.cbr.CBRCaseClass;
import com.mindbox.pe.unittest.AbstractTestBase;

/**
 * @author Inna Nill
 * @since PowerEditor 4.1.0
 */
public class CBRCaseClassTest extends AbstractTestBase {

	@Test
	public void testCBRCaseClass() throws Exception {
		logBegin("testCBRCaseClass");

		StringBuffer buff = new StringBuffer();
		CBRCaseClass cbrCaseClass1 = new CBRCaseClass();
		cbrCaseClass1.setSymbol("S1");
		cbrCaseClass1.setName("Display case 1");
		assertTrue(cbrCaseClass1.getSymbol().equals("S1"));
		assertTrue(cbrCaseClass1.getName().equals("Display case 1"));
		buff.append("Default new: Case 1: " + cbrCaseClass1.toString());
		CBRCaseClass cbrCaseClass2 = new CBRCaseClass(2, "S2", "Display case 2");
		assertTrue(cbrCaseClass2.getSymbol().equals("S2"));
		assertTrue(cbrCaseClass2.getName().equals("Display case 2"));
		buff.append("\nNew with params: Case 2: " + cbrCaseClass2.toString());
		cbrCaseClass2.copyFrom(cbrCaseClass1);
		assertTrue(cbrCaseClass2.getSymbol().equals("S1"));
		assertTrue(cbrCaseClass2.getName().equals("Display case 1"));
		buff.append("\nAfter copy: Case 2 should now be the same as Case 1: " + cbrCaseClass2.toString());

		logger.info(buff.toString());
		logEnd("testCBRCaseClass");
	}
}
