package com.mindbox.pe.unittest;

import java.util.Calendar;
import java.util.Date;

/**
 * ObjectMother.
 */
public class TestObjectMother {
	//	 Note: zero is not a valid id for some data elements, such templates;
	//         So, be sure to set nextUniqueId = 1;
	private static int nextUniqueId = 1;
	private static Calendar nextUniqueDateCalendar = Calendar.getInstance();

	public static int createInt() {
		return createInteger().intValue();
	}

	public static Integer createInteger() {
		return new Integer(getNextUniqueId());
	}

	public static String createString() {
		return "str" + createInt();
	}

	public static Date getNextUniqueDate() {
		Date result = nextUniqueDateCalendar.getTime();
		nextUniqueDateCalendar.add(Calendar.DATE, 1);
		return result;
	}

	public static int getNextUniqueId() {
		return nextUniqueId++;
	}

	private TestObjectMother() {
	}

}
