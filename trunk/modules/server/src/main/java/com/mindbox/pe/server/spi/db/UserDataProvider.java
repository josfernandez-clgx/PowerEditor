package com.mindbox.pe.server.spi.db;

import java.sql.SQLException;
import java.text.ParseException;
import java.util.List;

import com.mindbox.pe.model.admin.Role;


/**
 * Provides read-only view of user data.
 * An implementation of this do not need provide a meaningful implementation of 
 * all declared methods. Refer to documentation of each method for more information.
 * If the documentation of a method doesn't provide details, a meaningful implementation
 * must be provided for that method at all times.
 * @author Gene Kim
 * @author MindBox, Inc
 * @since PE 2.0.0
 */
public interface UserDataProvider {

	/**
	 * Tests if user objects are to be cached.
	 * If this returns <code>false</code>, {@link #getRoles(String)} must be implemented and
	 * return valid results.
	 * @return <code>true</code> if user objects are to be cached; <code>false</code>, otherwise
	 */
	boolean cacheUserObjects();
	
	/**
	 * Gets a list of roles of the specified user.
	 * 
	 * This is not used if {@link #cacheUserObjects()} returns <code>true</code>.
	 * So, only provide a meaningful implementation of this if and only if
	 * {@link #cacheUserObjects()} returns <code>false</code>.
	 * @param userID
	 * @return a list of {@link Role} objects
	 * @throws UnsupportedOperationException if this is not supported
	 */
	List<Role> getRoles(String userID) throws SQLException;
	
	/**
	 * Loads all users from the data source into the specified data holder.
	 * 
	 * This is not used if {@link #cacheUserObjects()} returns <code>false</code>.
	 * So, only provide a meaningful implementation of this if and only if
	 * {@link #cacheUserObjects()} returns <code>true</code>.
	 * @param dataHolder
	 * @throws SQLException on data source error
	 * @throws ParseException on parse error
	 * @throws UnsupportedOperationException if this is not supported
	 */
	void loadAllUsers(UserSecurityDataHolder dataHolder) throws SQLException, ParseException;

	/**
	 * Loads all user-to-role associations from the data source into the specified data holder.
	 * 
	 * This is not used if {@link #cacheUserObjects()} returns <code>false</code>.
	 * So, only provide a meaningful implementation of this if and only if
	 * {@link #cacheUserObjects()} returns <code>true</code>.
	 * @param dataHolder
	 * @throws SQLException
	 * @throws UnsupportedOperationException if this is not supported
	 */
	void loadAllUsersToRoles(UserSecurityDataHolder dataHolder) throws SQLException;
	
	void loadAllPrivileges(UserSecurityDataHolder dataHolder) throws SQLException;
	
	void loadAllRoles(UserSecurityDataHolder dataHolder) throws SQLException;
	
	void loadAllPrivilegesToRoles(UserSecurityDataHolder dataHolder) throws SQLException;
	
}
