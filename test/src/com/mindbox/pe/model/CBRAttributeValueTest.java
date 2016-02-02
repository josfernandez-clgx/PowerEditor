/*
 * Created on 2004. 10. 6.
 *
 */
package com.mindbox.pe.model;

import junit.framework.TestSuite;

import com.mindbox.pe.model.CBRAttributeValue;
import com.mindbox.pe.AbstractTestBase;


/**
 * @author Inna Nill
 * @since PowerEditor 4.1.0
 */
public class CBRAttributeValueTest extends AbstractTestBase {

	public static void main(String[] args) {
		junit.textui.TestRunner.run(suite());
	}

	public static TestSuite suite() {
		TestSuite suite = new TestSuite(CBRAttributeValueTest.class);
		suite.setName("CBR Attribute Value Test");
		return suite;
	}

	/**
	 * @param name
	 */
	public CBRAttributeValueTest(String name) {
		super(name);
	}

	public void testCBRAttributeValue() throws Exception {
		logBegin();

		StringBuffer buff = new StringBuffer();
		CBRAttributeValue attrValue = new CBRAttributeValue();
		buff.append("Brand new attribute value class: " + attrValue);
		/*
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
		*/

		logger.info(buff.toString());
		logEnd();
	}
}
