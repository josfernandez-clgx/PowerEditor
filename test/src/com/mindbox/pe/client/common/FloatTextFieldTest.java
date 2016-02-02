package com.mindbox.pe.client.common;

import java.util.HashMap;
import java.util.Map;

import junit.extensions.abbot.ComponentTestFixture;
import junit.extensions.abbot.TestHelper;
import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * A test of FloatTextField.
 * 
 * It is also an example of how to use Abbot's JUnit extension to test a GUI component.
 * 
 * Notice that the call to "showFrame" in setUp is commented out. Without this line commented out,
 * the test framework will throw an exception in a "headless" environment, such as our build
 * machine. When running within Eclipse, or other GUI environment, remove the comment to see the
 * robot actually create and manipulate the text field.
 */

public class FloatTextFieldTest extends ComponentTestFixture {
	private static final Double ZERO = Double.valueOf("0");
	private static final Double NEG_ZERO = Double.valueOf("-0");
	private static final Double TWO = Double.valueOf("2");

	private static Map<String, Double> inputStringToExpectedDoubleMap;
	static {
		inputStringToExpectedDoubleMap = new HashMap<String, Double>();

		inputStringToExpectedDoubleMap.put(null, null);
		inputStringToExpectedDoubleMap.put("", null);
		inputStringToExpectedDoubleMap.put("-", null);
		inputStringToExpectedDoubleMap.put("0", ZERO);
		inputStringToExpectedDoubleMap.put("-0", NEG_ZERO);
		inputStringToExpectedDoubleMap.put("0-", ZERO);
		inputStringToExpectedDoubleMap.put(".0", ZERO);
		inputStringToExpectedDoubleMap.put("-.0", NEG_ZERO);
		inputStringToExpectedDoubleMap.put("0.0.0", ZERO);
		inputStringToExpectedDoubleMap.put("-0.0.0", NEG_ZERO);
		inputStringToExpectedDoubleMap.put("a2e", TWO); // alpha chars are ignored, no matter their
		// position
		inputStringToExpectedDoubleMap.put("2%.0", TWO);
		inputStringToExpectedDoubleMap.put("2x", TWO);
		inputStringToExpectedDoubleMap.put("2a", TWO);
		inputStringToExpectedDoubleMap.put("e2", TWO);
		inputStringToExpectedDoubleMap.put("o2.f0a", TWO);
		inputStringToExpectedDoubleMap.put("$2", TWO);
		inputStringToExpectedDoubleMap.put("a2", TWO);
		inputStringToExpectedDoubleMap.put(",2,,,.0,", TWO);
		inputStringToExpectedDoubleMap.put("1.5E7", new Double("15000000.0"));
		inputStringToExpectedDoubleMap.put(
				"-12345678901234567890123456789012345678901234567890.1234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890",
				new Double(-1.2345678901234567E49));
	}

	private static String[] invalidInputStr = new String[] { "-a", "a", "$", "o" };

	private FloatTextField field;

	public static Test suite() {
		TestSuite suite = new TestSuite(FloatTextFieldTest.class.getName());
		suite.addTestSuite(FloatTextFieldTest.class);
		return suite;
	}

	public FloatTextFieldTest(String name) {
		super(name);
	}

	public static void main(String[] args) {
		TestHelper.runTests(args, FloatTextFieldTest.class);
	}

	protected void setUp() {
		field = new FloatTextField(0, false);
		// showFrame(field);
	}

	public void testHasValueWithNonValueReturnsFalse() throws Exception {
		field.setText(null);
		assertFalse(field.hasValue());
		field.setText(" ");
		assertFalse(field.hasValue());
	}

	public void testHasValueWithValueReturnsTrue() throws Exception {
		field.setText("23.555");
		assertTrue(field.hasValue());
		field.setValue(0.0f);
		assertTrue(field.hasValue());
	}

	public void testInitialValueValid() throws Exception {
		for (Map.Entry<String,Double> entry : inputStringToExpectedDoubleMap.entrySet()) {
			initFieldEmpty();
			setText((String) entry.getKey());
			test((Double) entry.getValue());
		}
	}

	public void testInitialValueInvalid() throws Exception {
		for (int i = 0; i < invalidInputStr.length; i++) {
			initFieldNotEmpty();
			setText(invalidInputStr[i]);
			test(null);
		}
	}

	public void testTypedValueValid() throws Exception {
		for (Map.Entry<String,Double> entry : inputStringToExpectedDoubleMap.entrySet()) {
			initFieldEmpty();
			typeChars((String) entry.getKey());
			test((Double) entry.getValue());
		}
	}

	public void testTypedValueInvalid() throws Exception {
		for (int i = 0; i < invalidInputStr.length; i++) {
			initFieldEmpty();
			typeChars(invalidInputStr[i]);
			test(null);
		}
	}

	private void initFieldEmpty() throws Exception {
		setText(null);
	}

	private void initFieldNotEmpty() throws Exception {
		field.setValue(1.0);
	}

	private void setText(String s) throws Exception {
		field.setText(s);
	}

	private void typeChars(String s) throws Exception {
		if (s != null) {
			char[] chars = s.toCharArray();
			for (int i = 0; i < chars.length; i++) {
				// System.out.println("typing: " + chars[i]);
				field.getDocument().insertString(field.getText() == null ? 0 : field.getText().length(), String.valueOf(chars[i]), null);
				// System.out.println("result: " + field.getText());
			}
		}
	}

	private void test(Double expectedValue) throws Exception {
		if (expectedValue == null) {
			// System.out.println("Testing (expect null). Input->Result: " + input + "->" +
			// field.getDoubleValue());
			assertNull(field.getDoubleValue()); // invalid entries clear field
		}
		else {
			// System.out.println("Testing (expect not null). Input->Result: " + input + "->" +
			// field.getDoubleValue());
			assertEquals(expectedValue, field.getDoubleValue());
		}
	}
}
