package com.mindbox.pe.model.filter;

import java.lang.reflect.InvocationTargetException;

import com.mindbox.pe.common.AbstractTestWithGenericEntityType;
import com.mindbox.pe.common.ReflectionUtil;
import com.mindbox.pe.model.GenericEntity;

public abstract class AbstractEntitySearchFilterTestBase extends AbstractTestWithGenericEntityType {

	protected static final boolean invokeIsAcceptable(GenericEntityBasicSearchFilter filter, GenericEntity entity) throws Exception {
		try {
			Boolean result = (Boolean) ReflectionUtil.executePrivate(
					filter,
					"isAcceptable",
					new Class[] { GenericEntity.class },
					new Object[] { entity });
			return (result == null ? false : result.booleanValue());
		}
		catch (RuntimeException ex) {
			throw (ex.getCause() instanceof InvocationTargetException ? (Exception) ((InvocationTargetException) ex.getCause()).getCause() : ex);
		}
		catch (Exception ex) {
			throw (ex instanceof InvocationTargetException ? (Exception) ((InvocationTargetException) ex).getCause() : ex);
		}
	}

}
