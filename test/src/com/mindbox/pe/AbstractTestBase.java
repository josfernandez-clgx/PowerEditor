package com.mindbox.pe;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

import junit.framework.TestCase;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang.SerializationUtils;
import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;

import com.mindbox.pe.common.ReflectionUtil;
import com.mindbox.pe.common.UtilBase;
import com.mindbox.pe.model.ColumnMessageFragmentDigest;
import com.mindbox.pe.model.GenericCategory;
import com.mindbox.pe.model.GenericEntity;
import com.mindbox.pe.model.assckey.GenericEntityCompatibilityData;
import com.mindbox.pe.model.rule.Condition;
import com.mindbox.pe.model.rule.RuleElement;
import com.mindbox.pe.model.table.CategoryOrEntityValue;
import com.mindbox.pe.model.table.CategoryOrEntityValues;
import com.mindbox.pe.server.model.GenericEntityIdentity;

/**
 * Base test case for all PowerEdtior tests. Check the available protected fields and methods.
 * 
 * @author Gene Kim
 * @author MindBox, Inc
 * @since
 */
public abstract class AbstractTestBase extends TestCase {

	public static final String EOL = System.getProperty("line.separator");
	
	public static Date getDate(int year, int month, int day) {
		return getDate(year, month, day, 0, 0, 0);
	}

	public static Date getDate(int year, int month, int day, int hour, int minute, int sec) {
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.YEAR, year);
		cal.set(Calendar.MONTH, month - 1);
		cal.set(Calendar.DAY_OF_MONTH, day);
		cal.set(Calendar.HOUR_OF_DAY, hour);
		cal.set(Calendar.MINUTE, minute);
		cal.set(Calendar.SECOND, sec);
		cal.set(Calendar.MILLISECOND, 0);
		return cal.getTime();
	}
	
	public static void copy(File sourceFile, File targetFile) throws Exception {
		BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(targetFile));
		BufferedInputStream in = new BufferedInputStream(new FileInputStream(sourceFile));
		int sourceSize = in.available();
		for (int c = in.read(); c != -1; c = in.read()) {
			out.write(c);
		}
		out.flush();
		out.close();
		in.close();
		in = null;

		assertTrue(targetFile.exists());
		in = new BufferedInputStream(new FileInputStream(targetFile));
		int targetSize = in.available();
		in.close();
		in = null;
		assertEquals(sourceSize, targetSize);
	}
	
	/**
	 * Makes sure the specified directory is empty.
	 * @param dir
	 */
	public static void clearDir(File dir) {
		if (dir.isDirectory()) {
			File[] children = dir.listFiles();
			for (int i = 0; i < children.length; i++) {
				if (children[i].isDirectory()) {
					clearDir(children[i]);
				}
				children[i].delete();
			}
		}
	}

	public static void deleteDir(File dir, String failureMessage) {
		if (!deleteDir(dir)) {
			fail(failureMessage);
		}
	}

	public static boolean deleteDir(File dir) {
		if (dir.isDirectory()) {
			String[] children = dir.list();
			for (int i = 0; i < children.length; i++) {
				boolean success = deleteDir(new File(dir, children[i]));
				if (!success) {
					return false;
				}
			}
		}

		// The directory is now empty so delete it
		return dir.delete();
	}
	
	protected static void turnOnDigesterLogger(Level level) {
		Logger.getRootLogger().addAppender(new ConsoleAppender(new PatternLayout("%d{yyyy-MM-dd HH:mm:ss} %c{2} %-5p: %m%n")));
		Logger.getRootLogger().setLevel(Level.WARN);
		Logger.getLogger("org.apache.commons.digester.Digester").setLevel((level == null ? Level.ALL : level));
	}

	protected static void assertThrowsNullPointerExceptionWithNullArgs(Class<?> type, String methodName, Class<?>[] argTypes) throws Exception {
		Object[] args = new Object[argTypes.length];
		for (int i = 0; i < args.length; i++) {
			args[i] = null;
		}
		assertThrowsNullPointerException(type, methodName, argTypes, args);
	}

	protected static void assertThrowsNullPointerException(Class<?> type, String methodName, Class<?>[] argTypes, Object[] args) throws Exception {
		assertThrowsException(type, methodName, argTypes, args, NullPointerException.class);
	}

	protected static void assertThrowsException(Class<?> type, String methodName, Class<?>[] argTypes, Object[] args, Class<?> exceptionType) throws Exception {
		try {
			ReflectionUtil.executeStaticPrivate(type, methodName, argTypes, args);
			fail("Expected exception of type " + exceptionType + " not thrown");
		}
		catch (RuntimeException ex) {
			if (ex.getCause() instanceof InvocationTargetException) {
				if (exceptionType.isAssignableFrom(((InvocationTargetException) ex.getCause()).getCause().getClass())) {
					return;
				}
			}
			throw ex;
		}
	}

	protected static void assertThrowsNullPointerExceptionWithNullArgs(Object obj, String methodName, Class<?>[] argTypes) throws Exception {
		Object[] args = new Object[argTypes.length];
		for (int i = 0; i < args.length; i++) {
			args[i] = null;
		}
		assertThrowsNullPointerException(obj, methodName, argTypes, args);
	}

	protected static void assertThrowsNullPointerException(Object obj, String methodName, Class<?>[] argTypes, Object[] args) throws Exception {
		assertThrowsException(obj, methodName, argTypes, args, NullPointerException.class);
	}

	protected static void assertThrowsException(Object obj, String methodName, Class<?>[] argTypes, Object[] args, Class<?> exceptionType) throws Exception {
		try {
			ReflectionUtil.executePrivate(obj, methodName, argTypes, args);
			fail("Expected exception of type " + exceptionType + " not thrown");
		}
		catch (RuntimeException ex) {
			if (ex.getCause() instanceof InvocationTargetException) {
				if (exceptionType.isAssignableFrom(((InvocationTargetException) ex.getCause()).getCause().getClass())) {
					return;
				}
			}
			throw ex;
		}
	}

	/**
	 * 
	 * @param array
	 * @param obj
	 * @throws NullPointerException
	 *             if <code>array</code> or <code>obj</code> is <code>null</code>
	 */
	protected static void assertContains(Object[] array, Object obj) {
		assertContains("", array, obj);
	}

	/**
	 * 
	 * @param message
	 * @param array
	 * @param obj
	 * @throws NullPointerException
	 *             if <code>array</code> or <code>obj</code> is <code>null</code>
	 */
	protected static void assertContains(String message, Object[] array, Object obj) {
		if (obj == null) throw new NullPointerException("obj is null");
		if (array == null) throw new NullPointerException("array is null");
		if (array.length > 0) {
			for (int i = 0; i < array.length; i++) {
				if (obj.equals(array[i])) return;
			}
			fail(message + ": " + array + " does not contain " + obj);
		}
		else {
			fail(message + ": " + array + " does not contain " + obj + "; it's empty");
		}
	}

	protected static void assertContains(GenericEntity entity, CategoryOrEntityValues values) {
		assertContains("", entity, values);
	}

	protected static void assertContains(String message, GenericEntity entity, CategoryOrEntityValues values) {
		if (values == null) fail(message + ": CategoryOrEntityValues is null");
		for (int i = 0; i < values.size(); i++) {
			CategoryOrEntityValue value = (CategoryOrEntityValue) values.get(i);
			if (value.isForEntity() && value.getId() == entity.getId()) return;
		}
		fail(message + ": " + entity + " not found in " + values);
	}

	protected static void assertContains(GenericCategory category, CategoryOrEntityValues values) {
		assertContains("", category, values);
	}

	protected static void assertContains(String message, GenericCategory category, CategoryOrEntityValues values) {
		if (values == null) fail(message + ": CategoryOrEntityValues is null");
		for (int i = 0; i < values.size(); i++) {
			CategoryOrEntityValue value = (CategoryOrEntityValue) values.get(i);
			if (!value.isForEntity() && value.getId() == category.getId()) return;
		}
		fail(message + ": " + category + " not found in " + values);
	}

	protected static void assertEquals(GenericEntityIdentity id1, GenericEntityIdentity id2) {
		assertEquals("", id1, id2);
	}

	protected static void assertEquals(String message, GenericEntityIdentity id1, GenericEntityIdentity id2) {
		assertEquals(message + "; id mismatch", id1.getEntityID(), id2.getEntityID());
		assertEquals(message + "; type mismatch", id1.getEntityType(), id2.getEntityType());
	}

	protected static void assertEquals(GenericEntityCompatibilityData cd1, GenericEntityCompatibilityData cd2) {
		assertEquals("", cd1, cd2);
	}

	protected static void assertEquals(String message, GenericEntityCompatibilityData cd1, GenericEntityCompatibilityData cd2) {
		assertEquals(message + "; id mismatch", cd1.getID(), cd2.getID());
		assertEquals(message + "; source id mismatch", cd1.getSourceID(), cd2.getSourceID());
		assertEquals(message + "; source type mismatch", cd1.getSourceType(), cd2.getSourceType());
		assertEquals(message + "; associable id mismatch", cd1.getAssociableID(), cd2.getAssociableID());
		assertEquals(message + "; effective date mismatch", cd1.getEffectiveDate(), cd2.getEffectiveDate());
		assertEquals(message + "; expiration date mismatch", cd1.getExpirationDate(), cd2.getExpirationDate());
		assertEquals(message + "; generic entity type mismatch", cd1.getGenericEntityType(), cd2.getGenericEntityType());
	}

	protected static void assertEquals(ColumnMessageFragmentDigest digest1, ColumnMessageFragmentDigest digest2) {
		assertEquals("", digest1, digest2);
	}

	protected static void assertEquals(String message, ColumnMessageFragmentDigest digest1, ColumnMessageFragmentDigest digest2) {
		assertEquals(message + "; type mismatch", digest1.getType(), digest2.getType());
		assertEquals(message + "; text mismatch", digest1.getText(), digest2.getText());
		assertEquals(message + "; cell selection mismatch", digest1.getCellSelection(), digest2.getCellSelection());
		assertEquals(message + "; enum delimiter mismatch", digest1.getEnumDelimiter(), digest2.getEnumDelimiter());
		assertEquals(message + "; enum final delimiter mismatch", digest1.getEnumFinalDelimiter(), digest2.getEnumFinalDelimiter());
		assertEquals(message + "; enum prefix mismatch", digest1.getEnumPrefix(), digest2.getEnumPrefix());
		assertEquals(message + "; range style mismatch", digest1.getRangeStyle(), digest2.getRangeStyle());
	}

	protected static void assertEquals(Condition condition1, Condition condition2) {
		assertEquals("", condition1, condition2);
	}

	protected static void assertEquals(String message, Condition condition1, Condition condition2) {
		assertEquals(message + "; object name mismatch", condition1.getObjectName(), condition2.getObjectName());
		assertEquals(message + "; operator mismatch", condition1.getOp(), condition2.getOp());
		assertEquals(message + "; reference mismatch", condition1.getReference(), condition2.getReference());
		assertEquals(message + "; value mismatch", condition1.getValue().toString(), condition2.getValue().toString());
		assertCommentEquals(message, condition1, condition2);
	}

	protected static void assertCommentEquals(RuleElement e1, RuleElement e2) {
		assertCommentEquals("", e1, e2);
	}

	protected static void assertCommentEquals(String message, RuleElement e1, RuleElement e2) {
		assertTrue(message + "; comments do not match for " +e1 + "," + e2, equalsNullOrEmpty(e1.getComment(), e2.getComment()));
	}

	protected static void assertPropertyEquals(Object bean, String property, Object expectedValue) throws Exception {
		assertEquals(expectedValue, PropertyUtils.getProperty(bean, property));
	}

	protected static void asssertPropertyEquals(Object bean1, Object bean2, String property) throws Exception {
		assertEquals(property + " mismatch", PropertyUtils.getProperty(bean1, property), PropertyUtils.getProperty(bean2, property));
	}

	protected static void assertEquals(GenericEntity entity1, GenericEntity entity2) throws Exception {
		assertEquals("id mismatch", entity1.getID(), entity2.getID());
		assertEquals("type mismatch", entity1.getType(), entity2.getType());
		assertEquals("parent id mismatch", entity1.getParentID(), entity2.getParentID());
		String[] propNames = entity1.getProperties();
		for (int i = 0; i < propNames.length; i++) {
			assertEquals(propNames[i] + " property mismatch", entity1.getProperty(propNames[i]), entity2.getProperty(propNames[i]));
		}
		assertEquals("property length mismatch", propNames.length, entity2.getProperties().length);
	}

	protected static void assertNotEquals(Object o1, Object o2) throws Exception {
		assertNotEquals(o1.toString() + " equals " + o2, o1, o2);
	}

	protected static void assertNotEquals(String msg, Object o1, Object o2) throws Exception {
		assertFalse(msg, (o1 == null && o2 == null) || (o1.equals(o2)));
	}

	protected static void assertNotEquals(int i1, int i2) throws Exception {
		assertNotEquals(String.valueOf(i1) + " equals " + i2, i1, i2);
	}

	protected static void assertNotEquals(String msg, int i1, int i2) throws Exception {
		assertFalse(msg, i1 == i2);
	}

	public static boolean equalsNullOrEmpty(String s1, String s2) {
		return ((s1 == null || s1.length() == 0) && (s2 == null || s2.length() == 0)) || s1.equals(s2);
	}

	protected static void assertEquals(String message, Object[] sa1, Object[] sa2) {
		assertEquals(message, Arrays.asList(sa1), Arrays.asList(sa2));
	}

	protected static void assertEquals(Object[] sa1, Object[] sa2) {
		assertEquals(Arrays.asList(sa1), Arrays.asList(sa2));
	}

	public static void assertMemberOf(int i, int[] ia) {
		assertMemberOf("", i, ia);
	}

	public static void assertMemberOf(String message, int i, int[] ia) {
		assertTrue(message, UtilBase.isMember(i, ia));
	}

	public static void assertNotMemberOf(int i, int[] ia) {
		assertNotMemberOf("", i, ia);

	}

	public static void assertNotMemberOf(String message, int i, int[] ia) {
		assertFalse(message, UtilBase.isMember(i, ia));
	}

	protected static void assertEquals(int[] ia1, int[] ia2) {
		assertEquals("", ia1, ia2);
	}

	protected static void assertEquals(String message, int[] ia1, int[] ia2) {
		assertTrue(message + ": expected " + UtilBase.toString(ia1) + " but got " + UtilBase.toString(ia2), UtilBase.equals(ia1, ia2));
	}

	protected static void assertEquals(Date d1, Date d2) {
		assertTrue("Date mismatch: " + d1 + "," + d2, equalsIgnoreTime(d1, d2));
	}

	public static void assertCause(Class<?> expectedCauseType, Exception wrappingException) {
		if (expectedCauseType == null) {
			assertNull(wrappingException.getCause());
		}
		else {
			assertEquals(expectedCauseType.getName(), wrappingException.getCause() == null ? null : wrappingException.getCause().getClass().getName());
		}
	}

	/** for testing readResolve() implementations */
	protected static void assertSerializeConstant(Serializable s) {
		assertTrue(s == SerializationUtils.deserialize(SerializationUtils.serialize(s)));
	}

	public static boolean equalsIgnoreTime(Date d1, Date d2) {
		if (d1 == d2) {
			return true;
		}
		else if (d1 != null && d2 != null) {
			Calendar c1 = Calendar.getInstance();
			c1.setTime(d1);
			Calendar c2 = Calendar.getInstance();
			c2.setTime(d2);
			return c1.get(Calendar.YEAR) == c2.get(Calendar.YEAR) && c1.get(Calendar.MONTH) == c2.get(Calendar.MONTH)
					&& c1.get(Calendar.DAY_OF_MONTH) == c2.get(Calendar.DAY_OF_MONTH);
		}
		else {
			return false;
		}
	}

	protected final Logger logger;

	protected AbstractTestBase(String name) {
		super(name);
		this.logger = Logger.getLogger(getClass());
	}

	protected final void log(Object msg) {
		logger.info(msg);
	}

	protected final void log(Object msg, Throwable t) {
		logger.error(msg, t);
	}

	protected final void logBegin() {
		logger.info("=== BEGINING OF " + getName() + " ===");
	}

	protected final void logEnd() {
		logger.info("=== END OF " + getName() + " ===");
	}
	
	protected void tearDown() throws Exception {
		super.tearDown();
	}
}
