package com.mindbox.pe.server.spi.db;

import java.util.Iterator;
import java.util.List;

import com.mindbox.pe.model.admin.UserPassword;
import com.mindbox.pe.server.model.User;

/**
 * 
 * @author Gene Kim
 * @author MindBox, Inc
 * @since PE 2.0.0
 */
public interface UserSecurityDataHolder {

	void addPrivilege(int privilegeID, String name, String displayLabel, int privType);
	
	void addPrivilegeToRole(int privID, int roleID);
	
	void addRole(int id, String name);
	
	boolean addUser(String userID, String name, String status, boolean passwordChangeRequired, 
					int failedLoginAttempts, List<UserPassword> passwordHistory);
	
	void addUserToRole(String userID, int roleID);
	
	Iterator<User> getUserIterator();
}
