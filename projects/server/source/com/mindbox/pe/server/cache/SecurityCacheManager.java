package com.mindbox.pe.server.cache;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

import com.mindbox.pe.common.UtilBase;
import com.mindbox.pe.common.config.UserDisplayNameAttribute;
import com.mindbox.pe.model.Constants;
import com.mindbox.pe.model.GridTemplate;
import com.mindbox.pe.model.admin.Privilege;
import com.mindbox.pe.model.admin.Role;
import com.mindbox.pe.model.admin.UserData;
import com.mindbox.pe.model.admin.UserPassword;
import com.mindbox.pe.server.ServerException;
import com.mindbox.pe.server.config.ConfigurationManager;
import com.mindbox.pe.server.model.GridTemplateSecurityInfo;
import com.mindbox.pe.server.model.User;
import com.mindbox.pe.server.spi.ServiceProviderFactory;
import com.mindbox.pe.server.spi.db.UserSecurityDataHolder;

public class SecurityCacheManager extends AbstractCacheManager implements UserSecurityDataHolder {

	private static SecurityCacheManager instance = null;

	public static synchronized SecurityCacheManager getInstance() {
		if (instance == null) instance = new SecurityCacheManager();
		return instance;
	}

	private Hashtable<Integer, GridTemplateSecurityInfo> mTemplateSecurityHash;
	private Hashtable<String, User> userMap;
	private Hashtable<Integer, Role> roleMapCache;
	private Hashtable<Integer, Privilege> mPrivilegeHash;

	private final boolean isUserCacheEnabled() {
		return ServiceProviderFactory.getUserManagementProvider().cacheUserObjects();
	}

	/**
	 * Gets a list of roles of the specified user.
	 * @param userID
	 * @return a list of {@link Role} objects
	 * @throws ServerException on error
	 * @throws NullPointerException if user cache is enabled and if the specified user id is not found
	 */
	public List<Role> getRoles(String userID) throws ServerException {
		if (isUserCacheEnabled()) {
			return getUser(userID).getRoles();
		}
		else {
			try {
				return ServiceProviderFactory.getUserManagementProvider().getRoles(userID);
			}
			catch (Exception e) {
				logger.error("Failed to retrieve roles of " + userID, e);
				throw new ServerException("Error while getting roles of user " + userID + ": " + e.getMessage());
			}
		}
	}

	public Iterator<Role> getRoles() {
		return roleMapCache.values().iterator();
	}

	public int countRoles() {
		return roleMapCache.values().size();
	}

	public boolean allowEdit(int templateID, String userName) throws ServerException {
		return allowEdit_checkTemplateRole(templateID, userName) && allowEdit_checkUsagePrivilege(templateID, userName);
	}

	private boolean allowEdit_checkUsagePrivilege(int templateID, String user) throws ServerException {
		GridTemplate template = GuidelineTemplateManager.getInstance().getTemplate(templateID);
		if (template == null) return false;
		String priv = UtilBase.getRequiredPermission(template.getUsageType(), false);
		return authorize(user, priv);
	}

	private boolean allowEdit_checkTemplateRole(int templateID, String user) throws ServerException {
		boolean flag = false;
		GridTemplateSecurityInfo gridtemplatesecurityinfo = getSecurityInfo(templateID);
		if (gridtemplatesecurityinfo != null) {
			List<Role> list = getRoles(user);
			for (Iterator<Role> iterator = list.iterator(); iterator.hasNext();) {
				Role role = iterator.next();
				if (gridtemplatesecurityinfo.allowEdit(role.getName())) {
					flag = true;
					break;
				}
			}

		}
		else {
			flag = true;
		}
		return flag;
	}

	@Override
	public Iterator<User> getUsers() {
		return userMap.values().iterator();
	}

	public Role getRole(int i) {
		return roleMapCache.get(new Integer(i));
	}

	/**
	 * Gets the role with the specified name.
	 * 
	 * @param roleName
	 *            the role name
	 * @return role object with the specified name, if found; <code>null</code>,
	 *         otherwise
	 * @since PowerEditor 4.5.0
	 */
	public Role getRole(String roleName) {
		for (Iterator<Role> iter = roleMapCache.values().iterator(); iter.hasNext();) {
			Role element = iter.next();
			if (element.getName().equals(roleName)) return element;
		}
		return null;
	}

	/**
	 * Tests if this contains the specified user id.
	 * 
	 * @param id
	 *            user id
	 * @return <code>true</code> if <code>id</code> is valid;
	 *         <code>false</code>, otherwise
	 * @since PowerEditor 4.5.0
	 */
	public boolean hasUser(String id) {
		return (id != null && userMap.containsKey(id));
	}

	public User findUserByID(int id) {
		for (Iterator<User> iter = userMap.values().iterator(); iter.hasNext();) {
			User element = iter.next();
			if (element.getID() == id) {
				return element;
			}
		}
		return null;
	}

	public User getUser(String s) {
		if (isUserCacheEnabled()) {
			if (s != null) {
				return userMap.get(s);
			}
			else {
				return null;
			}
		}
		else {
			return new User(s, s, Constants.ACTIVE_STATUS, false, 0, null);
		}
	}

	/**
	 * 
	 * @param userName
	 * @return
	 * @since 2012-04-08
	 */
	public String getDisplayName(String userId) {
		User user = getUser(userId);

		String displayName = null;
		if (user != null) {
			UserDisplayNameAttribute userDisplayNameAttribute = ConfigurationManager.getInstance().getUIConfiguration().getUserDisplayNameAttribute();
			if (userDisplayNameAttribute == null || userDisplayNameAttribute == UserDisplayNameAttribute.ID) {
				displayName = user.getUserID();
			}
			else {
				displayName = user.getName();
			}
		}
		return displayName;
	}

	public String getMatchingUserName(String userName) {
		for (String userId : userMap.keySet()) {
			if (userId.equalsIgnoreCase(userName)) {
				return userId;
			}
		}
		return userName;
	}

	private List<Privilege> getPrivileges(int roleID) {
		Role role = getRole(roleID);
		List<Privilege> list = role.getPrivileges();
		return list;
	}

	public Privilege getPrivilege(int i) {
		return mPrivilegeHash.get(new Integer(i));
	}

	public Privilege findPrivilegeByName(String name) {
		for (Iterator<Privilege> privIter = getPrivileges(); privIter.hasNext();) {
			Privilege priv = privIter.next();
			if (priv.getName().equals(name)) {
				return priv;
			}
		}
		return null;
	}

	/**
	 * Given the privilege name, this returns true if that permission exists
	 * for the role the current user has logged in as
	 * @param userID
	 * @param privilegeStr
	 * @return true is privilege exists, false otherwise
	 */
	public boolean checkPermissionByPrivilegeName(String userID, String privilegeStr) throws ServerException {
		List<Role> list = getRoles(userID);
		for (Iterator<Role> iterator = list.iterator(); iterator.hasNext();) {
			Role role = iterator.next();
			List<Privilege> list1 = getPrivileges(role.getID());
			for (Iterator<Privilege> iterator1 = list1.iterator(); iterator1.hasNext();) {
				Privilege privilege = iterator1.next();
				if (privilege.getName().equals(privilegeStr)) return true;
			}
		}
		return false;
	}

	public Iterator<Privilege> getPrivileges() {
		return mPrivilegeHash.values().iterator();
	}

	public void addPrivilege(int id, String name, String displayName, int privType) {
		Privilege privilege = new Privilege(id, name, displayName, privType);
		mPrivilegeHash.put(new Integer(id), privilege);
	}

	public boolean insertCache(UserData userdata) {
		String s = userdata.getUserID();

		userMap.put(s, User.valueOf(userdata));
		return true;
	}

	public boolean insertCache(Role role) {
		int id = role.getID();
		roleMapCache.put(new Integer(id), role);
		return true;
	}

	public void addPrivilegeToRole(int privID, int roleID) {
		Privilege privilege = getPrivilege(privID);
		if (privilege != null) getRole(roleID).addPrivilege(privilege);
	}

	public void addRole(int id, String name) {
		Role role = new Role(id, name, new ArrayList<Privilege>());
		roleMapCache.put(new Integer(id), role);
	}

	public boolean removeFromCache(String userID) {
		boolean flag = userMap.remove(userID) != null;
		return flag;
	}

	public boolean removeFromCache(int roleID) {
		Role roleToRemove = roleMapCache.get(new Integer(roleID));
		for (Enumeration<User> enumeration = userMap.elements(); enumeration.hasMoreElements();) {
			User user = enumeration.nextElement();
			user.removeRole(roleToRemove);
		}

		return roleMapCache.remove(new Integer(roleID)) != null;
	}

	public void startLoading() {
		userMap.clear();
		roleMapCache.clear();
		mPrivilegeHash.clear();
	}

	private SecurityCacheManager() {
		mTemplateSecurityHash = new Hashtable<Integer, GridTemplateSecurityInfo>();
		userMap = new Hashtable<String, User>();
		roleMapCache = new Hashtable<Integer, Role>();
		mPrivilegeHash = new Hashtable<Integer, Privilege>();
	}

	public String toString() {
		String s = "";
		s += "SecurityController with " + mTemplateSecurityHash.size() + " SecurityInfo objects!";
		s += mTemplateSecurityHash.toString();
		s += " Users: " + userMap.toString();
		return s;
	}

	public boolean allowView(int templateID, String userID) throws ServerException {
		boolean flag = false;
		GridTemplateSecurityInfo gridtemplatesecurityinfo = getSecurityInfo(templateID);
		if (gridtemplatesecurityinfo != null) {
			List<Role> list = getRoles(userID);
			for (Iterator<Role> iterator = list.iterator(); iterator.hasNext();) {
				Role role = iterator.next();
				if (gridtemplatesecurityinfo.allowView(role.getName())) {
					flag = true;
					break;
				}
			}
		}
		else {
			flag = true;
		}
		return flag;
	}

	public void finishLoading() {
	}

	private GridTemplateSecurityInfo getSecurityInfo(int i) {
		return mTemplateSecurityHash.get(new Integer(i));
	}

	public boolean hasRole(String s, String s1) throws ServerException {
		List<Role> list = getRoles(s);
		for (Iterator<Role> iterator = list.iterator(); iterator.hasNext();) {
			Role role = iterator.next();
			if (role.getName().equals(s1)) return true;
		}

		return false;
	}

	public boolean authorize(String userID, String privilegeStr) throws ServerException {
		// Check if the user exists only if users are cached
		if (isUserCacheEnabled()) {
			User user = getUser(userID);
			if (user == null) return false;
		}
		// as of 4.5.0 do not check authenticated status on authorization
		return privilegeStr == null || privilegeStr.length() == 0 || checkPermissionByPrivilegeName(userID, privilegeStr);
	}

	public boolean updateCache(User user, UserData userdata) {
		if (user == null) throw new NullPointerException("user cannot be null");
		if (userdata == null) throw new NullPointerException("userdata cannot be null");

		if (!UtilBase.isSame(user.getRoles(), userdata.getRoles())) {
			user.setRoles(userdata.getRoles());
		}
		user.setName(userdata.getName());
		user.setStatus(userdata.getStatus());
		user.setPasswordChangeRequired(userdata.getPasswordChangeRequired());
		user.setPasswordHistory(userdata.getPasswordHistory());
		user.setFailedLoginCounter(userdata.getFailedLoginCounter());
		return true;
	}

	public boolean addUser(String userID, String name, String status, boolean passwordChangeRequired, int failedLoginAttempts,
			List<UserPassword> passwordHistory) {
		User user = new User(userID, name, status, passwordChangeRequired, failedLoginAttempts, passwordHistory);

		userMap.put(userID, user);
		logger.info("User " + userID + " added");
		return true;
	}

	public boolean updateCache(Role role, Role role1) {
		if (!UtilBase.isSame(role.getPrivileges(), role1.getPrivileges())) role.setPrivileges(role1.getPrivileges());
		role.setName(role1.getName());
		return true;
	}

	public void addGridTemplateSecurityInfo(int i, List<String> list, List<String> list1) {
		GridTemplateSecurityInfo gridtemplatesecurityinfo = new GridTemplateSecurityInfo(i, list, list1);
		mTemplateSecurityHash.put(new Integer(i), gridtemplatesecurityinfo);
	}

	public void addUserToRole(String s, int roleID) {
		getUser(s).add(getRole(roleID));
	}
}