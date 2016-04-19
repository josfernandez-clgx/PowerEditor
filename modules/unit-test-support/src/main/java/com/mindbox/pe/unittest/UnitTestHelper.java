package com.mindbox.pe.unittest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;

public class UnitTestHelper {

	public static final String EOL = System.getProperty("line.separator");

	public static void assertArrayEqualsIgnoresOrder(int[] ia1, int[] ia2) {
		assertArrayEqualsIgnoresOrder("", ia1, ia2);
	}

	public static void assertArrayEqualsIgnoresOrder(Object[] array1, Object[] array2) {
		assertArrayEqualsIgnoresOrder("", array1, array2);
	}

	public static void assertArrayEqualsIgnoresOrder(String message, int[] array1, int[] array2) {
		if (array1 == array2) return;
		String finalMessage = String.format("%s: expected %s but got %s", message, array1, array2);
		Arrays.sort(array1);
		Arrays.sort(array2);
		assertTrue(finalMessage, Arrays.equals(array1, array2));
	}

	public static void assertArrayEqualsIgnoresOrder(String message, Object[] array1, Object[] array2) {
		Arrays.sort(array1);
		Arrays.sort(array2);
		assertTrue(message, Arrays.equals(array1, array2));
	}

	public static void assertCause(Class<?> expectedCauseType, Exception wrappingException) {
		if (expectedCauseType == null) {
			assertNull(wrappingException.getCause());
		}
		else {
			assertEquals(expectedCauseType, wrappingException.getCause() == null ? null : wrappingException.getCause().getClass());
		}
	}

	/**
	 * 
	 * @param array
	 * @param obj
	 * @throws NullPointerException if <code>array</code> or <code>obj</code> is <code>null</code>
	 */
	public static void assertContains(Object[] array, Object obj) {
		assertContains("", array, obj);
	}

	/**
	 * 
	 * @param message
	 * @param array
	 * @param obj
	 * @throws NullPointerException if <code>array</code> or <code>obj</code> is <code>null</code>
	 */
	public static void assertContains(String message, Object[] array, Object obj) {
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

	public static void assertDateEquals(Date d1, Date d2) {
		assertTrue("Date mismatch: " + d1 + "," + d2, equalsIgnoreTime(d1, d2));
	}

	public static void assertMemberOf(int i, int[] ia) {
		assertMemberOf("", i, ia);
	}

	public static void assertMemberOf(String message, int i, int[] ia) {
		assertTrue(message, Arrays.asList(ia).contains(i));
	}

	public static void assertNotEquals(int i1, int i2) throws Exception {
		assertNotEquals(String.valueOf(i1) + " equals " + i2, i1, i2);
	}

	public static void assertNotEquals(Object o1, Object o2) throws Exception {
		assertNotEquals(o1.toString() + " equals " + o2, o1, o2);
	}

	public static void assertNotEquals(String msg, int i1, int i2) throws Exception {
		assertFalse(msg, i1 == i2);
	}

	public static void assertNotEquals(String msg, Object o1, Object o2) throws Exception {
		assertFalse(msg, (o1 == null && o2 == null) || (o1.equals(o2)));
	}

	public static void assertNotMemberOf(int i, int[] ia) {
		assertNotMemberOf("", i, ia);

	}

	public static void assertNotMemberOf(String message, int i, int[] ia) {
		assertFalse(message, Arrays.asList(ia).contains(i));
	}

	public static void assertThrowsException(Class<?> type, String methodName, Class<?>[] argTypes, Object[] args, Class<?> exceptionType)
			throws Exception {
		try {
			executeStaticPrivate(type, methodName, argTypes, args);
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

	public static void assertThrowsException(Object obj, String methodName, Class<?>[] argTypes, Object[] args, Class<?> exceptionType)
			throws Exception {
		try {
			executePrivate(obj, methodName, argTypes, args);
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

	public static void assertThrowsNullPointerException(Class<?> type, String methodName, Class<?>[] argTypes, Object[] args) throws Exception {
		assertThrowsException(type, methodName, argTypes, args, NullPointerException.class);
	}

	public static void assertThrowsNullPointerException(Object obj, String methodName, Class<?>[] argTypes, Object[] args) throws Exception {
		assertThrowsException(obj, methodName, argTypes, args, NullPointerException.class);
	}

	public static void assertThrowsNullPointerExceptionWithNullArgs(Class<?> type, String methodName, Class<?>[] argTypes) throws Exception {
		Object[] args = new Object[argTypes.length];
		for (int i = 0; i < args.length; i++) {
			args[i] = null;
		}
		assertThrowsNullPointerException(type, methodName, argTypes, args);
	}

	public static void assertThrowsNullPointerExceptionWithNullArgs(Object obj, String methodName, Class<?>[] argTypes) throws Exception {
		Object[] args = new Object[argTypes.length];
		for (int i = 0; i < args.length; i++) {
			args[i] = null;
		}
		assertThrowsNullPointerException(obj, methodName, argTypes, args);
	}

	/**
	 * Makes sure the specified directory is empty.
	 * 
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
		assertTrue(sourceSize == targetSize);
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

	public static void deleteDir(File dir, String failureMessage) {
		if (!deleteDir(dir)) {
			fail(failureMessage);
		}
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

	public static boolean equalsNullOrEmpty(String s1, String s2) {
		return ((s1 == null || s1.length() == 0) && (s2 == null || s2.length() == 0)) || s1.equals(s2);
	}

	/**
	 * Invoke a method, even if its access modifier is 'private'.
	 * 
	 * @throws SecurityException if the system SecurityManager denies access.
	 */
	public static Object executePrivate(Object o, String methodName, Class<?>[] parameterTypes, Object[] args) {
		Method method = getMethod(o.getClass(), methodName, parameterTypes);
		try {
			method.setAccessible(true);
			return method.invoke(o, args);
		}
		catch (RuntimeException re) {
			throw re;
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Invoke a method, even if its access modifier is 'private'.
	 * 
	 * @throws SecurityException if the system SecurityManager denies access.
	 * @throws IllegalArgumentException if the specified method is not static
	 */
	public static Object executeStaticPrivate(Class<?> clazz, String methodName, Class<?>[] parameterTypes, Object[] args) {
		Method method = getMethod(clazz, methodName, parameterTypes);
		if (Modifier.isStatic(method.getModifiers())) {
			try {
				method.setAccessible(true);
				return method.invoke(null, args);
			}
			catch (RuntimeException re) {
				throw re;
			}
			catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
		else {
			throw new IllegalArgumentException("The specified method " + methodName + " is not static");
		}
	}

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

	private static Method getMethod(Class<?> clazz, String methodName, Class<?>[] parameterTypes) {
		Class<?> curClazz = clazz;
		Class<?>[] parmTypes = parameterTypes == null ? new Class<?>[] {} : parameterTypes;
		try {
			do {
				Method[] methods = curClazz.getDeclaredMethods();
				for (int i = 0; i < methods.length; i++) {
					Method declaredMethod = methods[i];
					if (declaredMethod.getName().equals(methodName) && Arrays.equals(declaredMethod.getParameterTypes(), parmTypes)) {
						return declaredMethod;
					}
				}
				curClazz = curClazz.getSuperclass();
			}
			while (curClazz != null);
			throw new NoSuchMethodException(methodName + " not in " + clazz.getName());
		}
		catch (RuntimeException re) {
			throw re;
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	//	/** for testing readResolve() implementations */
	//	public static void assertSerializeConstant(Serializable s) {
	//		assertTrue(s == SerializationUtils.deserialize(SerializationUtils.serialize(s)));
	//	}

	public static void turnOnDigesterLogger(Level level) {
		Logger.getRootLogger().addAppender(new ConsoleAppender(new PatternLayout("%d{yyyy-MM-dd HH:mm:ss} %c{2} %-5p: %m%n")));
		Logger.getRootLogger().setLevel(Level.WARN);
		Logger.getLogger("org.apache.commons.digester.Digester").setLevel((level == null ? Level.ALL : level));
	}

	private UnitTestHelper() {
	}
}
