package com.mindbox.pe.server.config;

import static com.mindbox.pe.common.PrivilegeConstants.DEPLOY_PRIV_NAME_PREFIX;
import static com.mindbox.pe.common.PrivilegeConstants.EDIT_PRIV_DISPLAY_NAME_PREFIX;
import static com.mindbox.pe.common.PrivilegeConstants.EDIT_PRIV_NAME_PREFIX;
import static com.mindbox.pe.common.PrivilegeConstants.ENTITY_TYPE_PRIV;
import static com.mindbox.pe.common.PrivilegeConstants.HARD_CODED_PRIV;
import static com.mindbox.pe.common.PrivilegeConstants.USAGE_TYPE_PRIV;
import static com.mindbox.pe.common.PrivilegeConstants.VIEW_AND_EDIT_ENTITY_PRIV_DISPLAY_NAME_SUFFIX;
import static com.mindbox.pe.common.PrivilegeConstants.VIEW_PRIV_DISPLAY_NAME_PREFIX;
import static com.mindbox.pe.common.PrivilegeConstants.VIEW_PRIV_NAME_PREFIX;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.mindbox.pe.common.UtilBase;
import com.mindbox.pe.common.config.EntityTypeDefinition;
import com.mindbox.pe.model.TemplateUsageType;
import com.mindbox.pe.model.TypeEnumValue;
import com.mindbox.pe.model.admin.Privilege;
import com.mindbox.pe.server.db.DBConnectionManager;
import com.mindbox.pe.server.db.updaters.UserSecurityUpdater;

/**
 * This class is called during initialization and has methods which are used to 
 * update Privileges in the database from the config file (PowerEditorConfiguration.xml)
 * These privileges are constructed from EntityType and UsageType.
 * 
 * @author Geneho Kim
 * @author Vineet Khosla
 * @author MDA MindBox, Inc
 * @since PowerEditor 5.0.0
 */
//TODO Vineet...write test class for this class
public class PrivilegeConfig implements Serializable {

	/** 
	 * @param privilegeName
	 * @param privilegeDisplayName
	 */
	private static void updatePrivilegesInMap(String privilegeName, String privilegeDisplayName, Map<Privilege, ChangeState> privilegeMap) {
		boolean privilegeNameFound = false;
		int privilegeId = 0;
		for (Map.Entry<Privilege, ChangeState> entry : privilegeMap.entrySet()) {
			Privilege key = entry.getKey();
			// if we have to insert these privileges, then we wont have conflicting id's
			// when we are done with this loop, we will have max id in hand to manipulate
			if (key.getID() >= privilegeId) {
				privilegeId = key.getID() + 1;
			}

			if (key.getName().equals(privilegeName)) {
				privilegeNameFound = true;
				// then check if display name needs to be updated
				if (!key.getDisplayString().equals(privilegeDisplayName)) {
					key.setDisplayString(privilegeDisplayName);
					entry.setValue(ChangeState.UPDATE);
				}
				else {
					entry.setValue(ChangeState.NOCHANGE);
				}
			}
		}// dont break for loop since we need greatest ID

		if (!privilegeNameFound) {
			Privilege privilegeViewEntity = new Privilege(privilegeId, privilegeName, privilegeDisplayName, USAGE_TYPE_PRIV);
			privilegeMap.put(privilegeViewEntity, ChangeState.INSERT);
		}
	}

	enum ChangeState {
		NOCHANGE, INSERT, UPDATE, DELETE;
	}

	private static final long serialVersionUID = 200404109002L;


	private final Logger logger = Logger.getLogger(getClass());


	public void updatePrivilegesForInitialization(EntityTypeDefinition[] typeDefs, List<TemplateUsageType> usageTypes,
			List<TypeEnumValue> statusList) throws SQLException {
		Map<Privilege, ChangeState> privilegeMap = new HashMap<Privilege, ChangeState>();

		List<Privilege> privileges = loadAllPrivilegesFromDB();
		for (Privilege privilege : privileges) {
			privilegeMap.put(privilege, ChangeState.DELETE);
		}

		updateEntityTypePrivileges(typeDefs, privilegeMap);
		updateUsageTypePrivileges(usageTypes, privilegeMap);
		updateDeployStatusPrivileges(statusList, privilegeMap);

		updateAllPrivilegesToDB(privilegeMap);
	}

	/**
	 * @author Vineet Khosla
	 * @since PowerEditor 5.0.0
	 * @throws SQLException
	 */
	private List<Privilege> loadAllPrivilegesFromDB() throws SQLException {
		DBConnectionManager dbconnectionmanager = DBConnectionManager.getInstance();
		Connection connection = dbconnectionmanager.getConnection();
		try {
			UserSecurityUpdater updater = new UserSecurityUpdater();
			return updater.loadPrivileges(connection);
		}
		finally {
			dbconnectionmanager.freeConnection(connection);
		}
	}

	private void updateDeployStatusPrivileges(List<TypeEnumValue> statusList, Map<Privilege, ChangeState> privilegeMap) {
		for (TypeEnumValue typeEnumValue : statusList) {
			String privilegeName = DEPLOY_PRIV_NAME_PREFIX + typeEnumValue.getName();
			String privilegeDisplayName = String.format("Deploy Policies in %s status", typeEnumValue.getDisplayLabel());

			updatePrivilegesInMap(privilegeName, privilegeDisplayName, privilegeMap);
		}
	}

	/*
	 * LOOP through EntitType picked up from config file.
	 * 	Construct that EntityType's View privilege and Edit Privilege	 
	 * 	  LOOP through Privileges loaded from DB
	 * 		IF ViewPrivlege is found
	 * 		   check if DisplayName of privilege has changed
	 * 			IF changed then update DisplayName and mark it for update in DB
	 * 			ELSE mark it as unchanged
	 * 
	 *		IF EditPrivlege is found
	 * 		   check if DisplayName of privilege has changed
	 * 			IF changed then update DisplayName and mark it for update in DB
	 * 			ELSE mark it as unchanged
	 *    END LOOP	 
	 *    IF EntityType's View privilege and/or Edit Privilege were not found in DB
	 *    Insert them into the Privilege HashMap and mark them for insert into DB 	    
	 * END LOOP
	 * @param typeDefs
	 */
	/**
	 * This method updates all existing EntityType privileges and adds new prvileges to privilege HashMap
	 * which are constructed from EntityType from the config file
	 * @param typeDefs
	 */
	private void updateEntityTypePrivileges(EntityTypeDefinition[] typeDefs, Map<Privilege, ChangeState> privilegeMap) {

		for (int i = 0; i < typeDefs.length; i++) {
			String entityTypeName = typeDefs[i].getName();
			String entityTypeDisplayName = typeDefs[i].getDisplayName();

			// construct View privilege
			String viewEntityTypePrivilegeName = VIEW_PRIV_NAME_PREFIX + entityTypeName;
			String viewEntityTypePrivilegeDisplayName = VIEW_PRIV_DISPLAY_NAME_PREFIX + entityTypeDisplayName
					+ VIEW_AND_EDIT_ENTITY_PRIV_DISPLAY_NAME_SUFFIX;
			boolean viewEntityTypePrivilege_NameFound = false;

			// construct Edit privilege
			String editEntityTypePrivilegeName = EDIT_PRIV_NAME_PREFIX + entityTypeName;
			String editEntityTypePrivilegeDisplayName = EDIT_PRIV_DISPLAY_NAME_PREFIX + entityTypeDisplayName
					+ VIEW_AND_EDIT_ENTITY_PRIV_DISPLAY_NAME_SUFFIX;
			boolean editEntityTypePrivilegeNameFound = false;


			//TODO Vineet...move this logic to updatePrivilegesInMap(String, String)
			int privilegeId = 0;
			for (Map.Entry<Privilege, ChangeState> entry : privilegeMap.entrySet()) {
				Privilege key = entry.getKey();
				//Integer value = (Integer)entry.getValue();

				// if we have to insert these privileges, then we wont have conflicting id's
				// when we are done with this loop, we will have max id in hand to manipulate
				if (key.getID() >= privilegeId) {
					privilegeId = key.getID() + 1;
				}

				if (key.getName().equals(viewEntityTypePrivilegeName)) {
					viewEntityTypePrivilege_NameFound = true;
					// then check if display name needs to be updated
					if (key.getDisplayString().equals(viewEntityTypePrivilegeDisplayName) == false) {
						key.setDisplayString(viewEntityTypePrivilegeDisplayName);
						entry.setValue(ChangeState.UPDATE);
					}
					else {
						entry.setValue(ChangeState.NOCHANGE);
					}
				}

				if (key.getName().equals(editEntityTypePrivilegeName)) {
					editEntityTypePrivilegeNameFound = true;
					// then check if display name needs to be updated
					if (key.getDisplayString().equals(editEntityTypePrivilegeDisplayName) == false) {
						key.setDisplayString(editEntityTypePrivilegeDisplayName);
						entry.setValue(ChangeState.UPDATE);
					}
					else {
						entry.setValue(ChangeState.NOCHANGE);
					}
				}
			}

			if (viewEntityTypePrivilege_NameFound == false && editEntityTypePrivilegeNameFound != false) {
				Privilege privilegeViewEntity = new Privilege(
						privilegeId,
						viewEntityTypePrivilegeName,
						viewEntityTypePrivilegeDisplayName,
						ENTITY_TYPE_PRIV);
				privilegeMap.put(privilegeViewEntity, ChangeState.INSERT);
			}
			if (editEntityTypePrivilegeNameFound == false && viewEntityTypePrivilege_NameFound != false) {
				Privilege privilegeEditEntity = new Privilege(
						privilegeId,
						editEntityTypePrivilegeName,
						editEntityTypePrivilegeDisplayName,
						ENTITY_TYPE_PRIV);
				privilegeMap.put(privilegeEditEntity, ChangeState.INSERT);
			}
			if (viewEntityTypePrivilege_NameFound == false && editEntityTypePrivilegeNameFound == false) {
				Privilege privilegeViewEntity = new Privilege(
						privilegeId,
						viewEntityTypePrivilegeName,
						viewEntityTypePrivilegeDisplayName,
						ENTITY_TYPE_PRIV);
				Privilege privilegeEditEntity = new Privilege(
						privilegeId + 1,
						editEntityTypePrivilegeName,
						editEntityTypePrivilegeDisplayName,
						ENTITY_TYPE_PRIV);
				privilegeMap.put(privilegeViewEntity, ChangeState.INSERT);
				privilegeMap.put(privilegeEditEntity, ChangeState.INSERT);
			}
		}
	}


	/**
	 * This method updates all existing UsageType privileges and adds new prvileges to privilege HashMap
	 *  which are constructed from UsageType from the config file
	* @param usageTypes
	*/
	private void updateUsageTypePrivileges(List<TemplateUsageType> usageTypes, Map<Privilege, ChangeState> privilegeMap) {
		HashMap<String, String> usageTypeMap = new HashMap<String, String>();

		for (int i = 0; i < usageTypes.size(); i++) {
			TemplateUsageType usageType = usageTypes.get(i);
			String privilege = usageType.getPrivilege();
			if (!usageTypeMap.containsKey(privilege)) {
				usageTypeMap.put(privilege, "a");

				//		 construct View Guideline privilege
				String viewGuidelinePrivilege_Name = UtilBase.constructViewGuidelinePrivilege_Name(privilege);
				String viewGuidelinePrivilege_DisplayName = UtilBase.constructViewGuidelinePrivilege_DisplayName(privilege);
				updatePrivilegesInMap(viewGuidelinePrivilege_Name, viewGuidelinePrivilege_DisplayName, privilegeMap);

				// construct Edit Guideline privilege
				String editGuidelinePrivilege_Name = UtilBase.constructEditGuidelinePrivilege_Name(privilege);
				String editGuidelinePrivilege_DisplayName = UtilBase.constructEditGuidelinePrivilege_DisplayName(privilege);
				updatePrivilegesInMap(editGuidelinePrivilege_Name, editGuidelinePrivilege_DisplayName, privilegeMap);

				// construct View Templates privilege
				String viewTemplatesPrivilege_Name = UtilBase.constructViewTemplatesPrivilege_Name(privilege);
				String viewTemplatesPrivilege_DisplayName = UtilBase.constructViewTemplatesPrivilege_DisplayName(privilege);
				updatePrivilegesInMap(viewTemplatesPrivilege_Name, viewTemplatesPrivilege_DisplayName, privilegeMap);

				// construct Edit Templates privilege
				String editTemplatesPrivilege_Name = UtilBase.constructEditTemplatesPrivilege_Name(privilege);
				String editTemplatesPrivilege_DisplayName = UtilBase.constructEditTemplatesPrivilege_DisplayName(privilege);
				updatePrivilegesInMap(editTemplatesPrivilege_Name, editTemplatesPrivilege_DisplayName, privilegeMap);
			}
		}
	}


	/**
	 * Iterates through the privilegeMap and inserts, updates or
	 * deletes a privilege to the DB 
	 * Also inserts, updates or delete role-privilege relationships
	 */
	private void updateAllPrivilegesToDB(Map<Privilege, ChangeState> privilegeMap) throws SQLException {
		DBConnectionManager dbconnectionmanager = DBConnectionManager.getInstance();
		UserSecurityUpdater updater = new UserSecurityUpdater();
		Connection connection = dbconnectionmanager.getConnection();
		try {
			connection.setAutoCommit(false);
			for (Map.Entry<Privilege, ChangeState> entry : privilegeMap.entrySet()) {
				Privilege key = entry.getKey();
				ChangeState value = entry.getValue();

				if (key.getPrivilegeType() != HARD_CODED_PRIV) {
					switch (value) {
					case INSERT: {
						updater.insertPrivilege(connection, key); //add privilege
						updater.loadRoles(connection); // load all roles
						updater.insertPrivilegeRoles(connection, key); //add privilege-role relationship				 		
						break;
					}
					case UPDATE: {
						updater.updatePrivilege(connection, key);
						break;
					}
					case DELETE: {
						updater.deletePrivilege(connection, key);
						break;
					}
					case NOCHANGE: {
						break;
					}
					default: {
						logger.debug("UNRESOLVABLE priv =" + key.toStringComplete());
					}
					}
				}
			}
			connection.commit();
		}
		catch (SQLException e) {
			if (connection != null) connection.rollback();
			throw e;
		}
		catch (Exception e) {
			if (connection != null) connection.rollback();
			throw new RuntimeException(e);
		}
		finally {
			dbconnectionmanager.freeConnection(connection);
		}

	}


}
