package com.mindbox.pe.common;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ReflectionUtil {

	/**
	 * Calls the constructor of <code>className</code> with the specified param classes with the specified parameters.
	 * This allows getting an instance of non-public member classes.
	 * 
	 * @param className className
	 * @param paramClasses paramClasses
	 * @param params params
	 * @return the instance of <code>className</code>
	 * @throws RuntimeException if no default constructor exists, it is inaccessible or if the constructor itself throws
	 *             any exception.
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static Object createInstance(String className, Class<?>[] paramClasses, Object[] params) {
		try {
			Class c = Class.forName(className);
			Constructor constructor = c.getDeclaredConstructor(paramClasses);
			constructor.setAccessible(true);
			return constructor.newInstance(params);
		}
		catch (RuntimeException e) {
			throw e;
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Invoke a method, even if its access modifier is 'private'.
	 * 
	 * @param o objet
	 * @param methodName method name
	 * @param parameterTypes parameter types
	 * @param args arguments
	 * @return result
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
	 * @param clazz class
	 * @param methodName method name
	 * @param parameterTypes parameter types
	 * @param args arguments
	 * @return result
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

	/**
	 * Get all the static instances of a class that are declared as members of the same class.
	 * 
	 * @param clazz class
	 * @return enum instances
	 * @throws SecurityException if the system SecurityManager denies access.
	 */
	public static Object[] getEnumInstances(Class<?> clazz) {
		return getStaticInstances(clazz, clazz);
	}

	/**
	 * Searches the given class and all its superclasses for the named field.
	 * 
	 * @param clazz class
	 * @param fieldName field name
	 * @return field object
	 */
	public static Field getField(Class<?> clazz, String fieldName) {
		Class<?> curClazz = clazz;
		try {
			do {
				Field[] fields = curClazz.getDeclaredFields();
				for (int i = 0; i < fields.length; i++) {
					Field declaredField = fields[i];
					if (declaredField.getName().equals(fieldName)) {
						return declaredField;
					}
				}
				curClazz = curClazz.getSuperclass();
			} while (curClazz != null);
			throw new NoSuchFieldException(fieldName + " not in " + clazz.getName());
		}
		catch (RuntimeException re) {
			throw re;
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Searches the given class and all its superclasses for the named method.
	 * 
	 * @param clazz class
	 * @param methodName method name
	 * @param parameterTypes parameter types
	 * @return method
	 */
	public static Method getMethod(Class<?> clazz, String methodName, Class<?>[] parameterTypes) {
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
			} while (curClazz != null);
			throw new NoSuchMethodException(methodName + " not in " + clazz.getName());
		}
		catch (RuntimeException re) {
			throw re;
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Searches the given class and all its superclasses for the named methods.
	 * 
	 * @param clazz class
	 * @param methodNames method name
	 * @param paramTypes parameter types
	 * @return methods
	 */
	public static Method[] getMethods(Class<?> clazz, String[] methodNames, Class<?>[][] paramTypes) {
		Method[] methods = new Method[methodNames.length];
		for (int i = 0; i < methodNames.length; i++) {
			methods[i] = getMethod(clazz, methodNames[i], paramTypes[i]);
		}
		return methods;
	}

	/**
	 * Access the value held in a field, even if its access modifier is 'private'.
	 * 
	 * @param o object
	 * @param fieldName field name
	 * @return result
	 * @throws SecurityException if the system SecurityManager denies access.
	 */
	public static Object getPrivate(Object o, String fieldName) {
		try {
			Field field = getField(o.getClass(), fieldName);
			field.setAccessible(true);
			return field.get(o);
		}
		catch (RuntimeException re) {
			throw re;
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@SuppressWarnings("unchecked")
	public static <T> T getPrivate(Object o, String fieldName, Class<T> c) {
		try {
			Field field = getField(o.getClass(), fieldName);
			field.setAccessible(true);
			Object obj = field.get(o);
			if (c.isInstance(obj)) {
				return (T) field.get(o);
			}
			else {
				throw new IllegalArgumentException(fieldName + " of " + o.getClass().getName() + " is not an instance of " + c.getClass().getName());
			}
		}
		catch (RuntimeException re) {
			throw re;
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Searches the given class and all its superclasses for the named static field.
	 * 
	 * @param clazz class
	 * @param fieldName field name
	 * @return field
	 */
	public static Field getStaticField(Class<?> clazz, String fieldName) {
		Class<?> curClazz = clazz;
		try {
			do {
				Field[] fields = curClazz.getDeclaredFields();
				for (int i = 0; i < fields.length; i++) {
					Field declaredField = fields[i];
					if (Modifier.isStatic(declaredField.getModifiers()) && declaredField.getName().equals(fieldName)) {
						return declaredField;
					}
				}
				curClazz = curClazz.getSuperclass();
			} while (curClazz != null);
			throw new NoSuchFieldException(fieldName + " not in " + clazz.getName());
		}
		catch (RuntimeException re) {
			throw re;
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Get all the static instances of a class that are declared as members some other class.
	 * @param inClass in class
	 * @param ofClass of class
	 * @return object array
	 * @throws SecurityException if the system SecurityManager denies access.
	 */
	public static Object[] getStaticInstances(Class<?> inClass, Class<?> ofClass) {
		try {
			List<Object> instances = new ArrayList<Object>();
			Field[] fields = inClass.getDeclaredFields();
			for (int i = 0; i < fields.length; i++) {
				Field field = fields[i];
				field.setAccessible(true);
				if (Modifier.isStatic(field.getModifiers())) {
					Object fieldVal = field.get(null);
					if (ofClass.isAssignableFrom(fieldVal.getClass())) {
						instances.add(fieldVal);
					}
				}
			}
			return instances.toArray();
		}
		catch (RuntimeException re) {
			throw re;
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Access the value held in a static field, even if its access modifier is 'private'.
	 * 
	 * @param clazz class
	 * @param fieldName field name
	 * @return result
	 * @throws SecurityException if the system SecurityManager denies access.
	 */
	public static Object getStaticPrivate(Class<?> clazz, String fieldName) {
		try {
			Field field = getStaticField(clazz, fieldName);
			field.setAccessible(true);
			return field.get(null);
		}
		catch (RuntimeException re) {
			throw re;
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Calls the default constructor of <code>clazz</code>.
	 * 
	 * @param clazz class
	 * @return new instance
	 * @throws RuntimeException if no default constructor exists, it is inaccessible or if the constructor itself throws
	 *             any exception.
	 */
	public static Object newInstance(Class<?> clazz) {
		try {
			return clazz.newInstance();
		}
		catch (RuntimeException e) {
			throw e;
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Modify the value of a static field, even if its access modifier is 'private'.
	 * 
	 * @param clazz class
	 * @param fieldName field name
	 * @param val value
	 * @throws SecurityException if the system SecurityManager denies access.
	 */
	public static void setPrivate(Class<?> clazz, String fieldName, Object val) {
		try {
			Field field = getStaticField(clazz, fieldName);
			field.setAccessible(true);
			field.set(null, val);
		}
		catch (RuntimeException re) {
			throw re;
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Modify the value of a field, even if its access modifier is 'private'.
	 * @param o object
	 * @param fieldName field name
	 * @param val value
	 * @throws SecurityException if the system SecurityManager denies access.
	 */
	public static void setPrivate(Object o, String fieldName, Object val) {
		try {
			Field field = getField(o.getClass(), fieldName);
			field.setAccessible(true);
			field.set(o, val);
		}
		catch (RuntimeException re) {
			throw re;
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private ReflectionUtil() {
	}
}
