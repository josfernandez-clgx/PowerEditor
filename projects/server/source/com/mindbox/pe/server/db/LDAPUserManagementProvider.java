package com.mindbox.pe.server.db;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.SearchResult;

import org.apache.log4j.Logger;

import com.mindbox.pe.model.admin.Role;
import com.mindbox.pe.server.cache.SecurityCacheManager;
import com.mindbox.pe.server.config.ConfigurationManager;
import com.mindbox.pe.server.config.LDAPConnectionConfig;
import com.mindbox.pe.server.db.loaders.UserSecurityLoader;
import com.mindbox.pe.server.db.updaters.UserSecurityUpdater;
import com.mindbox.pe.server.ldap.LdapUtil;
import com.mindbox.pe.server.spi.db.AbstractNotCachedUserDataProvider;
import com.mindbox.pe.server.spi.db.AbstractUnmodifiableUserDataUpdater;
import com.mindbox.pe.server.spi.db.UserSecurityDataHolder;


/**
 * A concrete implementation of {@link com.mindbox.pe.server.spi.UserManagementProvider} that uses LDAP.
 * @author Geneho Kim
 * @since PowerEditor 4.5.0
 */
public class LDAPUserManagementProvider extends DefaultUserManagementProvider {

	private static Logger loaderLogger = null;

	private static Logger getLoaderLog() {
		if (loaderLogger == null) {
			loaderLogger = Logger.getLogger("com.mindbox.pe.server.db.loaders.LDAPLoader");
		}
		return loaderLogger;
	}

	private static class LDAPUserLoader extends AbstractNotCachedUserDataProvider {

		private final UserSecurityLoader userSecurityLoader = new UserSecurityLoader();

		public List<Role> getRoles(String userID) throws SQLException {
			try {
				SearchResult sr = LdapUtil.findUserObject(userID);
				logger.debug(">>> getRoles: sr = " + sr);
				if (sr != null) {
					Attributes attrs = sr.getAttributes();
					Attribute attr = attrs.get(ldapConnectionConfig.getUserRolesAttribute());
					List<Role> list = new ArrayList<Role>();
					if (attr == null) {
						getLoaderLog().warn("No roles found for user " + userID + ": role attribute not found");
					}
					else {
						for (NamingEnumeration<?> ne = attr.getAll(); ne.hasMore();) {
							Object obj = ne.next();
							if (obj != null) {
								String roleName = (obj instanceof String ? (String) obj : obj.toString());
								Role role = SecurityCacheManager.getInstance().getRole(roleName);
								if (role == null) {
									getLoaderLog().warn("Skipping role: invalid role name for " + "user=" + userID + "; role=" + roleName);
								}
								else {
									getLoaderLog().info("Found role " + roleName + " for " + userID);
									list.add(role);
								}
							}
						}
					}
					return Collections.unmodifiableList(list);
				}
				else {
					throw new SQLException("No user with id " + userID + " found");
				}
			}
			catch (NamingException ex) {
				logger.error("Failed to find user " + userID, ex);
				throw new SQLException("Failed to find user " + userID, ex.getMessage());
			}
		}

		public void loadAllPrivileges(UserSecurityDataHolder dataHolder) throws SQLException {
			userSecurityLoader.loadAllPrivileges(dataHolder);
		}

		public void loadAllPrivilegesToRoles(UserSecurityDataHolder dataHolder) throws SQLException {
			userSecurityLoader.loadAllPrivilegesToRoles(dataHolder);
		}

		public void loadAllRoles(UserSecurityDataHolder dataHolder) throws SQLException {
			userSecurityLoader.loadAllRoles(dataHolder);
		}
	}

	private static class LDAPUserUpdater extends AbstractUnmodifiableUserDataUpdater {

		private final UserSecurityUpdater userSecurityUpdater = new UserSecurityUpdater();

		public void deleteRole(int roleID) throws SQLException {
			userSecurityUpdater.deleteRole(roleID);
		}

		public void insertRole(int roleID, String name, int[] privilegeIDs) throws SQLException {
			userSecurityUpdater.insertRole(roleID, name, privilegeIDs);
		}

		public void updateRole(int roleID, String name, int[] privilegeIDs) throws SQLException {
			userSecurityUpdater.updateRole(roleID, name, privilegeIDs);
		}

		public void insertRoleWithUnknownPrivileges(int roleID, String name, int[] privilegeIDs, List<String> unknownPrivsForRole)
				throws SQLException {
			userSecurityUpdater.insertRoleWithUnknownPrivileges(roleID, name, privilegeIDs, unknownPrivsForRole);
		}

		public void updateRoleWithUnknownPrivileges(int roleID, String name, int[] privilegeIDs, List<String> unknownPrivsForRole)
				throws SQLException {
			userSecurityUpdater.updateRoleWithUnknownPrivileges(roleID, name, privilegeIDs, unknownPrivsForRole);
		}

		@Override
		public void enableUser(String userID, String actingUserID) throws SQLException {
			throw new UnsupportedOperationException();
		}

	}

	private static LDAPConnectionConfig ldapConnectionConfig = ConfigurationManager.getInstance().getLDAPConnectionConfig();
	private static final Logger logger = Logger.getLogger(LDAPUserManagementProvider.class);

	public LDAPUserManagementProvider() {
		super(new LDAPUserLoader(), new LDAPUserUpdater());
	}

}
