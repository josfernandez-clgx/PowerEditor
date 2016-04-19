/*
 * Created on 2004. 4. 15.
 *  
 */
package com.mindbox.pe.server.db.updaters;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Date;
import java.util.Map;

import com.mindbox.pe.model.Constants;
import com.mindbox.pe.model.DateSynonym;
import com.mindbox.pe.model.assckey.MutableTimedAssociationKey;
import com.mindbox.pe.server.db.DateSynonymReferenceUpdater;
import com.mindbox.pe.server.spi.db.GenericEntityDataUpdater;

/**
 * @author Geneho Kim
 * @author MindBox
 * @since PowerEditor 3.0.0
 */
public class GenericEntityUpdater extends DateSynonymReferenceUpdater implements GenericEntityDataUpdater {

	private static final String Q_REPLACE_ALL_COMPATIBILITY_EFFECTIVE_DATE_SYNONYM = "update mb_entity_compatibility set effective_synonym_id=? where effective_synonym_id in ("
			+ IN_CLAUSE_LIST_HOLDER + ")";

	private static final String Q_REPLACE_ALL_COMPATIBILITY_EXPIRATION_DATE_SYNONYM = "update mb_entity_compatibility set expiration_synonym_id=? where expiration_synonym_id in ("
			+ IN_CLAUSE_LIST_HOLDER + ")";

	private static final String Q_DELETE_ENTITY = "delete from MB_ENTITY where entity_id=? and entity_type=?";

	private static final String Q_DELETE_ENTITY_PROPERTY = "delete from MB_ENTITY_PROPERTY where entity_id=? and entity_type=?";

	private static final String Q_INSERT_ENTITY = "insert into MB_ENTITY (entity_id,entity_type,entity_name,parent_id) values (?,?,?,?)";

	private static final String Q_INSERT_ENTITY_PROPERTY = "insert into MB_ENTITY_PROPERTY (entity_id,entity_type,property_name,string_value) VALUES (?,?,?,?)";

	private static final String Q_UPDATE_ENTITY = "update MB_ENTITY set entity_name=?,parent_id=? where entity_id=? and entity_type=?";

	private static final String Q_INSERT_ENTITY_COMPATIBILITY = "insert into MB_ENTITY_COMPATIBILITY (entity1_id,entity1_type,entity2_id,entity2_type,effective_synonym_id,expiration_synonym_id)"
			+ " values (?,?,?,?,?,?)";

	private static final String Q_UPDATE_ENTITY_COMPATIBILITY = "update MB_ENTITY_COMPATIBILITY set effective_synonym_id=?,expiration_synonym_id=?"
			+ " where entity1_id=? and entity1_type=? and entity2_id=? and entity2_type=?";

	private static final String Q_DELETE_ENTITY_COMPATIBILITY = "delete from MB_ENTITY_COMPATIBILITY" + " where entity1_id=? and entity1_type=? and entity2_id=? and entity2_type=?";

	private static final String Q_DELETE_ENTITY_ALL_COMPATIBILITY_1 = "delete from MB_ENTITY_COMPATIBILITY where entity1_id=? and entity1_type=?";

	private static final String Q_DELETE_ENTITY_ALL_COMPATIBILITY_2 = "delete from MB_ENTITY_COMPATIBILITY where entity2_id=? and entity2_type=?";

	private static final String Q_INSERT_CATEGORY = "insert into MB_ENTITY_CATEGORY (category_id,category_name,category_type) values (?,?,?)";

	private static final String Q_UPDATE_CATEGORY = "update MB_ENTITY_CATEGORY set category_name=? where category_id=? and category_type=?";

	private static final String Q_DELETE_CATEGORY = "delete from MB_ENTITY_CATEGORY where category_id=? and category_type=?";

	private static final String Q_DELETE_ALL_ENTITY_TO_CATEGORY = "delete from MB_ENTITY_CATEGORY_LINK where category_id=? and category_type=?";

	private static final String Q_DELETE_ALL_CATEGORY_TO_ENTITY = "delete from MB_ENTITY_CATEGORY_LINK where entity_id=? and entity_type=?";

	private static final String Q_INSERT_ENTITY_TO_CATEGORY = "insert into MB_ENTITY_CATEGORY_LINK (entity_id,entity_type,category_id,category_type,effective_synonym_id,expiration_synonym_id)"
			+ " values (?,?,?,?,?,?)";

	private static final String Q_INSERT_CATEGORY_PARENT = "insert into MB_ENTITY_CATEGORY_PARENT (category_type,category_id,parent_id,effective_synonym_id,expiration_synonym_id) values (?,?,?,?,?)";

	private static final String Q_DELETE_CATEGORY_PARENTS = "delete from MB_ENTITY_CATEGORY_PARENT where category_type=? and category_id=?";

	public GenericEntityUpdater() {
	}

	public GenericEntityUpdater(Connection conn) {
		super(conn);
	}

	public void addCategory(int id, String name, int categoryType, MutableTimedAssociationKey[] parentAssocations) throws SQLException {
		logger.debug(">>> addCategory: " + id + "," + name + "," + categoryType);
		Connection conn = getConnection();
		PreparedStatement ps = null;
		try {
			conn.setAutoCommit(false);

			ps = conn.prepareStatement(Q_INSERT_CATEGORY);
			ps.setInt(1, id);
			ps.setString(2, name);
			ps.setInt(3, categoryType);

			int count = ps.executeUpdate();
			if (count < 1) {
				throw new SQLException("Insert failed: no rows inserted");
			}

			addCategoryParentRelationships(conn, id, categoryType, parentAssocations);
			conn.commit();

			logger.debug("<<< addCategory: " + count);
		}
		catch (Exception ex) {
			conn.rollback();
			logger.error("addCategory - Error: rolled back", ex);
			if (ex instanceof SQLException) {
				throw (SQLException) ex;
			}
			else {
				throw new SQLException(ex.getMessage());
			}
		}
		finally {
			if (ps != null) ps.close();
			releaseConnection();
		}
	}

	public void updateCategoryName(int id, int categoryType, String newName) throws SQLException {
		logger.debug(">>> updateCategoryName: " + id + "," + categoryType + "," + newName);
		Connection conn = getConnection();
		PreparedStatement ps = null;
		try {
			conn.setAutoCommit(false);

			ps = conn.prepareStatement(Q_UPDATE_CATEGORY);
			ps.setString(1, newName);
			ps.setInt(2, id);
			ps.setInt(3, categoryType);

			int count = ps.executeUpdate();
			if (count < 1) {
				throw new SQLException("updateCategoryName failed: no rows updated");
			}
			conn.commit();

			logger.debug("<<< updateCategoryName: " + count);
		}
		catch (Exception ex) {
			conn.rollback();
			logger.error("updateCategoryName - Error: rolled back", ex);
			if (ex instanceof SQLException) {
				throw (SQLException) ex;
			}
			else {
				throw new SQLException(ex.getMessage());
			}
		}
		finally {
			if (ps != null) ps.close();
			releaseConnection();
		}
	}

	public void updateCategory(int id, String name, int categoryType, MutableTimedAssociationKey[] parentAssocations) throws SQLException {
		logger.debug(">>> updateCategory: " + id + "," + name + "," + categoryType);
		Connection conn = getConnection();
		PreparedStatement ps = null;
		try {
			conn.setAutoCommit(false);

			ps = conn.prepareStatement(Q_UPDATE_CATEGORY);
			ps.setString(1, name);
			ps.setInt(2, id);
			ps.setInt(3, categoryType);

			int count = ps.executeUpdate();
			if (count < 1) {
				throw new SQLException("Update failed: no rows updated");
			}

			deleteCategoryParentRelationships(conn, id, categoryType);
			addCategoryParentRelationships(conn, id, categoryType, parentAssocations);

			conn.commit();

			logger.debug("<<< updateCategory: " + count);
		}
		catch (Exception ex) {
			conn.rollback();
			logger.error("updateCategory - Error: rolled back", ex);
			if (ex instanceof SQLException) {
				throw (SQLException) ex;
			}
			else {
				throw new SQLException(ex.getMessage());
			}
		}
		finally {
			if (ps != null) ps.close();
			releaseConnection();
		}
	}

	/**
	 * @param conn
	 * @param categoryID
	 * @param categoryType
	 * @param parentAssocations
	 * @throws SQLException
	 */
	private void addCategoryParentRelationships(Connection conn, int categoryID, int categoryType, MutableTimedAssociationKey[] parentAssocations) throws SQLException {
		logger.debug(">>> addCategoryParentRelationships: " + categoryID + "," + categoryType + "," + parentAssocations);
		PreparedStatement ps = null;
		try {
			ps = conn.prepareStatement(Q_INSERT_CATEGORY_PARENT);
			for (int i = 0; i < parentAssocations.length; i++) {
				ps.setInt(1, categoryType);
				ps.setInt(2, categoryID);
				ps.setInt(3, parentAssocations[i].getAssociableID());
				ps.setInt(4, (parentAssocations[i].getEffectiveDate() == null ? -1 : parentAssocations[i].getEffectiveDate().getId()));
				ps.setInt(5, (parentAssocations[i].getExpirationDate() == null ? -1 : parentAssocations[i].getExpirationDate().getId()));
				int count = ps.executeUpdate();
				if (count < 1) throw new SQLException("No row added");
			}
			logger.debug("<<< addCategoryParentRelationships");
		}
		catch (SQLException ex) {
			throw ex;
		}
		catch (Exception ex) {
			logger.error("addCategoryParentRelationships", ex);
			throw new SQLException(ex.getMessage());
		}
		finally {
			if (ps != null) ps.close();
		}
	}

	public void deleteCategoryParentRelationships(Connection conn, int categoryID, int categoryType) throws SQLException {
		logger.debug(">>> deleteCategoryParentRelationships: " + categoryID + "," + categoryType);
		PreparedStatement ps = null;
		try {
			ps = conn.prepareStatement(Q_DELETE_CATEGORY_PARENTS);
			ps.setInt(1, categoryType);
			ps.setInt(2, categoryID);
			ps.executeUpdate();

			logger.debug("<<< deleteCategoryParentRelationships");
		}
		catch (Exception ex) {
			logger.error("deleteCategoryParentRelationships - Error: rolled back", ex);
			if (ex instanceof SQLException) {
				throw (SQLException) ex;
			}
			else {
				throw new SQLException(ex.getMessage());
			}
		}
		finally {
			if (ps != null) ps.close();
		}
	}

	public void deleteCategory(int id, int type) throws SQLException {
		logger.debug(">>> deleteCategory: " + id + " of " + type);
		Connection conn = getConnection();
		PreparedStatement ps = null;
		try {
			conn.setAutoCommit(false);

			ps = conn.prepareStatement(Q_DELETE_ALL_ENTITY_TO_CATEGORY);
			ps.setInt(1, id);
			ps.setInt(2, type);
			int count = ps.executeUpdate();
			logger.debug("deleteCategory: deleted " + count + " entity-to-category rows");
			ps.close();
			ps = null;

			deleteCategoryParentRelationships(conn, id, type);

			ps = conn.prepareStatement(Q_DELETE_CATEGORY);
			ps.setInt(1, id);
			ps.setInt(2, type);
			count = ps.executeUpdate();
			logger.debug("deleteCategory: deleted " + count + " category rows");

			conn.commit();

			logger.debug("<<< deleteCategory");
		}
		catch (Exception ex) {
			conn.rollback();
			logger.error("deleteCategory - Error: rolled back", ex);
			if (ex instanceof SQLException) {
				throw (SQLException) ex;
			}
			else {
				throw new SQLException(ex.getMessage());
			}
		}
		finally {
			if (ps != null) ps.close();
			releaseConnection();
		}
	}

	public void deleteGenericEntity(int id, int type) throws SQLException {
		logger.debug(">>> deleteGenericEntity: " + id + " of " + type);
		Connection conn = getConnection();
		PreparedStatement ps = null;
		try {
			conn.setAutoCommit(false);

			ps = conn.prepareStatement(Q_DELETE_ENTITY_PROPERTY);
			ps.setInt(1, id);
			ps.setInt(2, type);
			int count = ps.executeUpdate();
			ps.close();
			ps = null;
			logger.debug("deleteGenericEntity: deleted " + count + " property rows");

			ps = conn.prepareStatement(Q_DELETE_ENTITY);
			ps.setInt(1, id);
			ps.setInt(2, type);
			count = ps.executeUpdate();
			ps.close();
			ps = null;
			logger.debug("deleteGenericEntity: deleted " + count + " entity rows");

			// TBD delete entity-category relationships
			ps = conn.prepareStatement(Q_DELETE_ALL_CATEGORY_TO_ENTITY);
			ps.setInt(1, id);
			ps.setInt(2, type);
			count = ps.executeUpdate();
			logger.debug("deleteGenericEntity: deleted " + count + " entity to category rows");

			conn.commit();

			logger.debug("<<< deleteGenericEntity");
		}
		catch (Exception ex) {
			conn.rollback();
			logger.error("deleteGenericEntity - Error: rolled back", ex);
			if (ex instanceof SQLException) {
				throw (SQLException) ex;
			}
			else {
				throw new SQLException(ex.getMessage());
			}
		}
		finally {
			if (ps != null) ps.close();
			releaseConnection();
		}
	}

	private String toStorageString(Object value) {
		logger.info(">>> toStorageString: " + value);
		if (value == null) {
			return "";
		}
		if (value instanceof Date) {
			return Constants.THREADLOCAL_FORMAT_DATE_TIME_SEC.get().format((Date) value);
		}
		else {
			return value.toString();
		}
	}

	public void insertGenericEntity(int id, int type, String name, int parentID, Map<String, Object> propertyMap, int categoryType, MutableTimedAssociationKey[] categoryAssociations)
			throws SQLException {
		logger.debug(">>> insertGenericEntity: " + id + " of " + type);
		Connection conn = getConnection();
		PreparedStatement ps = null;
		try {
			conn.setAutoCommit(false);

			ps = conn.prepareStatement(Q_INSERT_ENTITY);
			ps.setInt(1, id);
			ps.setInt(2, type);
			ps.setString(3, name);
			ps.setInt(4, parentID);
			int count = ps.executeUpdate();
			ps.close();
			ps = null;

			logger.debug("insertGenericEntity: inserted " + count + " entity rows");

			ps = conn.prepareStatement(Q_INSERT_ENTITY_PROPERTY);

			for (Map.Entry<String, Object> element : propertyMap.entrySet()) {
				ps.setInt(1, id);
				ps.setInt(2, type);
				ps.setString(3, element.getKey());
				ps.setString(4, toStorageString(element.getValue()));

				count = ps.executeUpdate();
				if (count < 1) {
					throw new SQLException("no generic entity rows inserted");
				}

				logger.debug("insertGenericEntity: inserted " + count + " entity rows");
			}
			ps.close();
			ps = null;

			insertEntityToCategoryLinks(conn, id, type, categoryType, categoryAssociations);

			conn.commit();

			logger.debug("<<< insertGenericEntity");
		}
		catch (Exception ex) {
			conn.rollback();
			logger.error("insertGenericEntity - Error: rolled back", ex);
			if (ex instanceof SQLException) {
				throw (SQLException) ex;
			}
			else {
				throw new SQLException(ex.getMessage());
			}
		}
		finally {
			if (ps != null) ps.close();
			releaseConnection();
		}
	}

	private void insertEntityToCategoryLinks(Connection conn, int id, int type, int categoryType, MutableTimedAssociationKey[] categoryAssociations) throws SQLException {
		if (categoryAssociations != null && categoryAssociations.length > 0 && categoryType != -1) {
			PreparedStatement ps = null;
			try {
				ps = conn.prepareStatement(Q_INSERT_ENTITY_TO_CATEGORY);
				for (int i = 0; i < categoryAssociations.length; i++) {
					logger.debug("insertEntityToCategoryLinks: processing category " + categoryAssociations[i]);
					ps.setInt(1, id);
					ps.setInt(2, type);
					ps.setInt(3, categoryAssociations[i].getAssociableID());
					ps.setInt(4, categoryType);
					ps.setInt(5, (categoryAssociations[i].getEffectiveDate() == null ? -1 : categoryAssociations[i].getEffectiveDate().getID()));
					ps.setInt(6, (categoryAssociations[i].getExpirationDate() == null ? -1 : categoryAssociations[i].getExpirationDate().getID()));
					int count = ps.executeUpdate();
					if (count < 1) {
						throw new SQLException("no entity-to-category rows inserted");
					}
				}
			}
			finally {
				if (ps != null) ps.close();
			}
		}
	}

	public void updateGenericEntity(int id, int type, String name, int parentID, Map<String, Object> propertyMap, int categoryType, MutableTimedAssociationKey[] categoryAssociations)
			throws SQLException {
		logger.debug(">>> updateGenericEntity: " + id + " of " + type);
		Connection conn = getConnection();
		PreparedStatement ps = null;
		PreparedStatement insertPS = null;
		try {
			conn.setAutoCommit(false);

			ps = conn.prepareStatement(Q_UPDATE_ENTITY);
			ps.setString(1, name);
			ps.setInt(2, parentID);
			ps.setInt(3, id);
			ps.setInt(4, type);
			int count = ps.executeUpdate();
			ps.close();
			ps = null;

			logger.debug("updateGenericEntity: updated " + count + " entity rows");

			// delete all entity properties 
			ps = conn.prepareStatement(Q_DELETE_ENTITY_PROPERTY);
			ps.setInt(1, id);
			ps.setInt(2, type);
			count = ps.executeUpdate();
			ps.close();
			ps = null;
			logger.debug("updateGenericEntity: deleted " + count + " entity propety value row ");

			count = 0;
			insertPS = conn.prepareStatement(Q_INSERT_ENTITY_PROPERTY);
			for (Map.Entry<String, Object> element : propertyMap.entrySet()) {
				insertPS.setInt(1, id);
				insertPS.setInt(2, type);
				insertPS.setString(3, element.getKey());
				insertPS.setString(4, toStorageString(element.getValue()));
				count = count + insertPS.executeUpdate();
			}
			logger.debug("updateGenericEntity: inserted " + count + " entity rows");
			insertPS.close();
			insertPS = null;

			// check category parameters
			// remove all relationships, first
			ps = conn.prepareStatement(Q_DELETE_ALL_CATEGORY_TO_ENTITY);
			ps.setInt(1, id);
			ps.setInt(2, type);
			count = ps.executeUpdate();
			logger.debug("updateGenericEntity: deleted " + count + " entity to category rows");
			ps.close();
			ps = null;

			// insert entity-to-category relationships
			insertEntityToCategoryLinks(conn, id, type, categoryType, categoryAssociations);

			conn.commit();

			logger.debug("<<< updateGenericEntity");
		}
		catch (Exception ex) {
			conn.rollback();
			logger.error("updateGenericEntity - Error: rolled back", ex);
			if (ex instanceof SQLException) {
				throw (SQLException) ex;
			}
			else {
				throw new SQLException(ex.getMessage());
			}
		}
		finally {
			if (ps != null) ps.close();
			if (insertPS != null) insertPS.close();
			releaseConnection();
		}
	}

	public void insertEntityCompatibility(int entityType1, int entityID1, int entityType2, int entityID2, DateSynonym effectiveDate, DateSynonym expirationDate) throws SQLException {
		logger.debug(">>> insertEntityCompatibility: " + entityType1 + "," + entityID1 + "," + entityType2 + "," + entityID2 + "," + effectiveDate + "," + expirationDate);
		Connection conn = getConnection();
		PreparedStatement ps = null;
		try {
			conn.setAutoCommit(false);

			ps = conn.prepareStatement(Q_INSERT_ENTITY_COMPATIBILITY);
			ps.setInt(1, entityID1);
			ps.setInt(2, entityType1);
			ps.setInt(3, entityID2);
			ps.setInt(4, entityType2);

			ps.setInt(5, (effectiveDate == null ? -1 : effectiveDate.getID()));
			ps.setInt(6, (expirationDate == null ? -1 : expirationDate.getID()));
			int count = ps.executeUpdate();

			conn.commit();

			logger.debug("<<< insertEntityCompatibility: " + count);
		}
		catch (Exception ex) {
			conn.rollback();
			logger.error("insertEntityCompatibility - Error: rolled back", ex);
			if (ex instanceof SQLException) {
				throw (SQLException) ex;
			}
			else {
				throw new SQLException(ex.getMessage());
			}
		}
		finally {
			if (ps != null) ps.close();
			releaseConnection();
		}
	}

	public void updateEntityCompatibility(int entityType1, int entityID1, int entityType2, int entityID2, DateSynonym effectiveDate, DateSynonym expirationDate) throws SQLException {
		logger.debug(">>> updateEntityCompatibility: " + entityType1 + "," + entityID1 + "," + entityType2 + "," + entityID2 + "," + effectiveDate + "," + expirationDate);
		Connection conn = getConnection();
		PreparedStatement ps = null;
		try {
			conn.setAutoCommit(false);

			ps = conn.prepareStatement(Q_UPDATE_ENTITY_COMPATIBILITY);

			ps.setInt(1, (effectiveDate == null ? -1 : effectiveDate.getID()));
			ps.setInt(2, (expirationDate == null ? -1 : expirationDate.getID()));
			ps.setInt(3, entityID1);
			ps.setInt(4, entityType1);
			ps.setInt(5, entityID2);
			ps.setInt(6, entityType2);

			int count = ps.executeUpdate();

			conn.commit();

			logger.debug("<<< updateEntityCompatibility: " + count);
		}
		catch (Exception ex) {
			conn.rollback();
			logger.error("updateEntityCompatibility - Error: rolled back", ex);
			if (ex instanceof SQLException) {
				throw (SQLException) ex;
			}
			else {
				throw new SQLException(ex.getMessage());
			}
		}
		finally {
			if (ps != null) ps.close();
			releaseConnection();
		}
	}

	public void deleteEntityCompatibility(int entityType1, int entityID1, int entityType2, int entityID2) throws SQLException {
		logger.debug(">>> deleteEntityCompatibility: " + entityType1 + "," + entityID1 + "," + entityType2 + "," + entityID2);
		Connection conn = getConnection();
		PreparedStatement ps = null;
		try {
			conn.setAutoCommit(false);

			ps = conn.prepareStatement(Q_DELETE_ENTITY_COMPATIBILITY);
			ps.setInt(1, entityID1);
			ps.setInt(2, entityType1);
			ps.setInt(3, entityID2);
			ps.setInt(4, entityType2);

			int count = ps.executeUpdate();

			conn.commit();

			logger.debug("<<< deleteEntityCompatibility: " + count);
		}
		catch (Exception ex) {
			conn.rollback();
			logger.error("deleteEntityCompatibility - Error: rolled back", ex);
			if (ex instanceof SQLException) {
				throw (SQLException) ex;
			}
			else {
				throw new SQLException(ex.getMessage());
			}
		}
		finally {
			if (ps != null) ps.close();
			releaseConnection();
		}
	}

	public void deleteAllEntityCompatibility(int entityType1, int entityID1) throws SQLException {
		logger.debug(">>> deleteAllEntityCompatibility: " + entityType1 + "," + entityID1);
		Connection conn = getConnection();
		PreparedStatement ps = null;
		try {
			conn.setAutoCommit(false);

			ps = conn.prepareStatement(Q_DELETE_ENTITY_ALL_COMPATIBILITY_1);
			ps.setInt(1, entityID1);
			ps.setInt(2, entityType1);
			int count = ps.executeUpdate();
			logger.debug("... deleteAllEntityCompatibility: removed compatibility-1 = " + count);
			ps.close();
			ps = null;

			ps = conn.prepareStatement(Q_DELETE_ENTITY_ALL_COMPATIBILITY_2);
			ps.setInt(1, entityID1);
			ps.setInt(2, entityType1);
			count = ps.executeUpdate();
			logger.debug("... deleteAllEntityCompatibility: removed compatibility-2 = " + count);

			conn.commit();

			logger.debug("<<< deleteEntityCompatibility: " + count);
		}
		catch (Exception ex) {
			conn.rollback();
			logger.error("deleteEntityCompatibility - Error: rolled back", ex);
			if (ex instanceof SQLException) {
				throw (SQLException) ex;
			}
			else {
				throw new SQLException(ex.getMessage());
			}
		}
		finally {
			if (ps != null) ps.close();
			releaseConnection();
		}
	}

	public void replaceDateSynonymReferences(DateSynonym[] toBeReplaced, DateSynonym replacement) throws SQLException {
		replaceDateSynonymReferencesInIntersectionTable(toBeReplaced, replacement, Q_REPLACE_ALL_COMPATIBILITY_EFFECTIVE_DATE_SYNONYM, Q_REPLACE_ALL_COMPATIBILITY_EXPIRATION_DATE_SYNONYM);
	}

}