package com.mindbox.pe.server.imexport.provider;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.beanutils.MethodUtils;
import org.apache.log4j.Logger;

import com.mindbox.pe.model.DateSynonym;
import com.mindbox.pe.model.admin.Privilege;
import com.mindbox.pe.model.admin.Role;
import com.mindbox.pe.model.grid.AbstractGrid;
import com.mindbox.pe.model.grid.ParameterGrid;
import com.mindbox.pe.model.grid.ProductGrid;
import com.mindbox.pe.model.template.GridTemplate;
import com.mindbox.pe.model.template.ParameterTemplate;
import com.mindbox.pe.server.Util;
import com.mindbox.pe.server.cache.DateSynonymManager;
import com.mindbox.pe.server.cache.GridManager;
import com.mindbox.pe.server.cache.GuidelineTemplateManager;
import com.mindbox.pe.server.cache.ParameterManager;
import com.mindbox.pe.server.cache.ParameterTemplateManager;
import com.mindbox.pe.server.cache.SecurityCacheManager;
import com.mindbox.pe.server.imexport.ExportException;
import com.mindbox.pe.server.model.User;

/**
 * @author Geneho Kim
 * @author MindBox
 * @since PowerEditor 2.5.0
 */
abstract class DataProviderBase implements ExportDataProvider {

	private static Object getSingletonInstance(String className) throws Exception {
		Class<?> classObj = Class.forName(className);
		Method method = classObj.getDeclaredMethod("getInstance", (Class[]) null);
		return method.invoke(null, (Object[]) null);
	}

	static Object invokeSingletonMethod(String className, String methodName) throws Exception {
		return MethodUtils.invokeExactMethod(getSingletonInstance(className), methodName, null);
	}

	static Object invokeSingletonMethod(String className, String methodName, Object arg) throws Exception {
		return MethodUtils.invokeMethod(getSingletonInstance(className), methodName, arg);
	}

	static Object invokeSingletonMethod(String className, String methodName, Object[] args) throws Exception {
		return MethodUtils.invokeMethod(getSingletonInstance(className), methodName, args);
	}

	protected final void reportError(String msg, Throwable t) {
		Logger.getLogger(getClass()).error(msg, t);
	}

	// implementation methods ///////////////////////////////////////////////

	public List<ProductGrid> getGuidelineGrids(int templateID) throws ExportException {
		return GridManager.getInstance().getAllGridsForTemplate(templateID);
	}

	public Privilege[] getAllPrivileges() throws ExportException {
		List<Privilege> list = new ArrayList<Privilege>();
		for (Iterator<Privilege> iter = SecurityCacheManager.getInstance().getPrivileges(); iter.hasNext();) {
			list.add(iter.next());
		}
		return list.toArray(new Privilege[0]);
	}

	public Role[] getAllRoles() throws ExportException {
		List<Role> list = new ArrayList<Role>();
		for (Iterator<Role> iter = SecurityCacheManager.getInstance().getRoles(); iter.hasNext();) {
			list.add(iter.next());
		}
		return list.toArray(new Role[0]);
	}

	public User[] getAllUsers() throws ExportException {
		List<User> list = new ArrayList<User>();
		for (Iterator<User> iter = SecurityCacheManager.getInstance().getUserIterator(); iter.hasNext();) {
			list.add(iter.next());
		}
		return list.toArray(new User[0]);
	}

	public List<GridTemplate> getAllGuidelineTemplates() throws ExportException {
		return GuidelineTemplateManager.getInstance().getAllTemplates();
	}

	public String getCellValue(AbstractGrid<?> grid, int row, int col, String defaultValue) throws ExportException {
		try {
			Object obj = grid.getCellValueObject(row, col, null);
			return (obj == null ? defaultValue : Util.convertCellValueToString(obj));
		}
		catch (Exception ex) {
			throw new ExportException(ex.getMessage());
		}
	}

	public List<ParameterTemplate> getAllParameterTemplates() throws ExportException {
		return ParameterTemplateManager.getInstance().getTemplates();
	}

	public List<ParameterGrid> getParameterGrids(int templateID) throws ExportException {
		return ParameterManager.getInstance().getGrids(templateID);
	}

	public DateSynonym getDateSynonym(int id) throws ExportException {
		return DateSynonymManager.getInstance().getDateSynonym(id);
	}
}
