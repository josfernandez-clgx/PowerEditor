package com.mindbox.pe.client;

import java.util.Collections;
import java.util.List;

import org.apache.log4j.Logger;

import com.mindbox.pe.communication.NonSessionSearchRequest;
import com.mindbox.pe.model.GenericCategory;
import com.mindbox.pe.model.GenericEntity;
import com.mindbox.pe.model.GenericEntityType;
import com.mindbox.pe.model.PeDataType;
import com.mindbox.pe.model.Persistent;
import com.mindbox.pe.model.admin.Privilege;
import com.mindbox.pe.model.admin.Role;
import com.mindbox.pe.model.admin.UserData;
import com.mindbox.pe.model.filter.AllGenericCategorySearchFilter;
import com.mindbox.pe.model.filter.AllGenericEntitySearchFilter;
import com.mindbox.pe.model.filter.AllSearchFilter;

/**
 * Caches entities retrieved from the server.
 * 
 * @since PowerEditor 1.0
 */
final class ClientCache {

	private List<Role> roleList;
	private List<Privilege> privilegeList;
	private final Logger logger;

	/**
	 * Constructs a new client cache instance.
	 *  
	 */
	ClientCache() {
		this.logger = Logger.getLogger("MPEClient.Cache");
		clear_internal();
	}

	/**
	 * @author vineet khosla
	 * @since PowerEditor 5.0
	 * @param roleToAdd
	 */
	public void addToRoleList(Role roleToAdd) {
		if (!roleList.contains(roleToAdd)) roleList.add(roleToAdd);
	}

	private void clear_internal() {
		roleList = null;
		privilegeList = null;
	}

	/**
	 * Clears this cache.
	 *  
	 */
	void clearCache() {
		clear_internal();
	}

	List<Role> getAllRoles() {
		return Collections.unmodifiableList(roleList);
	}

	List<GenericEntity> lookup(GenericEntityType entityType) {
		return lookupServer(entityType);
	}

	/**
	 * Gets all generic categories of the specified type.
	 * @param categoryType the category type
	 * @return list of all generic categories of type <code>categoryType</code>
	 * @since PowerEditor 3.1.0
	 */
	List<GenericCategory> lookupCategory(int categoryType) {
		return lookupServer(categoryType);
	}

	List<Privilege> lookupPrivileges(boolean reloadRequest) {
		if (reloadRequest) privilegeList = null;
		if (privilegeList == null) {
			privilegeList = lookupServer(PeDataType.PRIVILEGE);
		}
		return privilegeList;
	}

	List<Role> lookupRoles(boolean reloadRequest) {
		if (reloadRequest) roleList = null;
		if (roleList == null) {
			roleList = lookupServer(PeDataType.ROLE);
		}
		return roleList;
	}

	private List<GenericEntity> lookupServer(GenericEntityType entityType) {
		List<GenericEntity> list = null;
		try {
			NonSessionSearchRequest<GenericEntity> request = new NonSessionSearchRequest<GenericEntity>(new AllGenericEntitySearchFilter(entityType));
			list = request.sendRequest(ClientUtil.getInstance().getTimeOutController()).getResultList();
		}
		catch (Exception ex) {
			logger.error("Lookup Error", ex);
			if (ClientUtil.getParent() != null) {
				ClientUtil.getParent().handleRuntimeException(ex);
			}
		}
		return list;
	}

	/**
	 * Gets all generic categories of the specified type.
	 * @param categoryType the category type
	 * @param framebase
	 * @return list of all generic categories of type <code>categoryType</code>
	 * @since PowerEditor 3.1.0
	 */
	private List<GenericCategory> lookupServer(int categoryType) {
		List<GenericCategory> list = null;
		try {
			NonSessionSearchRequest<GenericCategory> request = new NonSessionSearchRequest<GenericCategory>(new AllGenericCategorySearchFilter(categoryType, false));
			list = request.sendRequest(ClientUtil.getInstance().getTimeOutController()).getResultList();
		}
		catch (Exception ex) {
			logger.error("Lookup Error", ex);
			if (ClientUtil.getParent() != null) {
				ClientUtil.getParent().handleRuntimeException(ex);
			}
		}
		return list;
	}

	private <T extends Persistent> List<T> lookupServer(PeDataType entityType) {
		List<T> list = null;
		try {
			NonSessionSearchRequest<T> request = new NonSessionSearchRequest<T>(new AllSearchFilter<T>(entityType));
			list = request.sendRequest(ClientUtil.getInstance().getTimeOutController()).getResultList();
		}
		catch (Exception ex) {
			logger.error("Lookup Error", ex);
			if (ClientUtil.getParent() != null) {
				ClientUtil.getParent().handleRuntimeException(ex);
			}
		}
		return list;
	}

	List<UserData> lookupUserData() {
		return lookupServer(PeDataType.USER_DATA);
	}

	/**
	 * @author vineet khosla
	 * @since PowerEditor 5.0
	 * @param roleToDelete
	 */
	public void removeFromRoleList(Role roleToDelete) {
		if (roleList.contains(roleToDelete)) roleList.remove(roleToDelete);
	}

}