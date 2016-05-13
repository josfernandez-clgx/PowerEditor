package com.mindbox.pe.server.db.updaters;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import com.mindbox.pe.model.DateSynonym;
import com.mindbox.pe.server.db.DateSynonymReferenceUpdater;
import com.mindbox.pe.server.model.GenericCategoryIdentity;
import com.mindbox.pe.server.model.GenericEntityIdentity;

/**
 * @author Geneho Kim
 * @author MindBox
 * @since PowerEditor 2.2.0
 */
public class ParameterUpdater extends DateSynonymReferenceUpdater {

	private static final String Q_INSERT_GRID_DATE_SYNONYM = "insert into MB_PARAMETER_DATE_SYNONYM (parameter_id,effective_synonym_id,expiration_synonym_id) values (?,?,?)";

	private static final String Q_UPDATE_GRID_DATE_SYNONYM = "update MB_PARAMETER_DATE_SYNONYM set effective_synonym_id=?,expiration_synonym_id=? where parameter_id=?";

	private static final String Q_DELETE_GRID_DATE_SYNONYM = "delete from MB_PARAMETER_DATE_SYNONYM where parameter_id=?";
	
	private static final String Q_REPLACE_ALL_GRID_EFFECTIVE_DATE_SYNONYM = "update MB_PARAMETER_DATE_SYNONYM set effective_synonym_id=? where effective_synonym_id in (" + IN_CLAUSE_LIST_HOLDER + ")";

	private static final String Q_REPLACE_ALL_GRID_EXPIRATION_DATE_SYNONYM = "update MB_PARAMETER_DATE_SYNONYM set expiration_synonym_id=? where expiration_synonym_id in (" + IN_CLAUSE_LIST_HOLDER + ")";

	private static final String Q_INSERT_GRID = "INSERT INTO MB_PARAMETER (parameter_id,template_id,cell_values,num_rows,status) VALUES (?,?,?,?,?)";

	private static final String Q_UPDATE_GRID = "UPDATE MB_PARAMETER SET template_id=?,cell_values=?,num_rows=?,status=? WHERE parameter_id=?";

	private static final String Q_DELETE_GRID = "DELETE FROM MB_PARAMETER WHERE parameter_id=?";

	private static final String Q_INSERT_ENTITY_CONTEXT = "INSERT INTO MB_ENTITY_PARAMETER_CONTEXT (parameter_id,entity_id,entity_type) values (?,?,?)";

	private static final String Q_INSERT_CATEGORY_CONTEXT = "INSERT INTO MB_ENTITY_PARAMETER_CONTEXT (parameter_id,category_id,category_type) values (?,?,?)";

	private static final String Q_DELETE_ENTITY_CONTEXT = "DELETE FROM MB_ENTITY_PARAMETER_CONTEXT WHERE parameter_id=?";

	private static final String Q_DELETE_GRID_CONTEXT_GENERIC_CATEGORY = 
		"delete from MB_ENTITY_PARAMETER_CONTEXT where category_type=? and category_id=?";
	
	private static final String Q_DELETE_GRID_CONTEXT_GENERIC_ENTITY = 
		"delete from MB_ENTITY_PARAMETER_CONTEXT where entity_type=? and entity_id=?";
	

	/**
	 *  
	 */
	public ParameterUpdater() {
		super();
	}
	
	public ParameterUpdater(Connection conn) {
		super(conn);
	}
	
	/**
	 * Deletes the specified generic category from all grid context.
	 * Note: this does not perform connection management functions.
	 * @param categoryType category type
	 * @param categoryID category id
	 * @throws SQLException on error
	 * @since 4.4.1
	 */
	public void deleteCategoryFromContext(int categoryType, int categoryID) throws SQLException {
		logger.debug(">>> deleteCategoryFromContext: type=" + categoryType + ", id=" + categoryID);
		Connection conn = getConnection();
		PreparedStatement ps = null;
		try {
			int count = 0;
			ps = conn.prepareStatement(Q_DELETE_GRID_CONTEXT_GENERIC_CATEGORY);
			ps.setInt(1, categoryType);
			ps.setInt(2, categoryID);
			count = ps.executeUpdate();
			logger.debug("    deleteCategoryFromContext: no of context rows deleted = " + count);

			logger.debug("<<< deleteCategoryFromContext");
		}
		finally {
			if (ps != null) ps.close();
		}
	}

	/**
	 * Deletes the specified generic category from all grid context.
	 * Note: this does not perform connection management functions.
	 * @param entityType category type
	 * @param entityID category id
	 * @throws SQLException on error
	 * @since 5.1.0
	 */
	public void deleteEntityFromContext(int entityType, int entityID) throws SQLException {
		logger.debug(">>> deleteEntityFromContext: type=" + entityType + ", id=" + entityID);
		Connection conn = getConnection();
		PreparedStatement ps = null;
		try {
			int count = 0;
			ps = conn.prepareStatement(Q_DELETE_GRID_CONTEXT_GENERIC_ENTITY);
			ps.setInt(1, entityType);
			ps.setInt(2, entityID);
			count = ps.executeUpdate();
			logger.debug("    deleteEntityFromContext: no of context rows deleted = " + count);

			logger.debug("<<< deleteEntityFromContext");
		}
		finally {
			if (ps != null) ps.close();
		}
	}

	/**
	 * Inserts the specified parameter grid. Performs no connection management actions. 
	 * Commit the connection after calling this.
	 * @param gridID
	 * @param templateID
	 * @param cellValues
	 * @param numRows
	 * @param effDate
	 * @param expDate
	 * @param status
	 * @param catID
	 * @param prodID
	 * @param channelID
	 * @param investorID
	 * @param geIdentity
	 * @param genericCategoryID
	 * @param genericCategoryType
	 * @throws SQLException on DB error
	 */
	public void insertGrid(int gridID, int templateID, String cellValues, int numRows, DateSynonym effDate, DateSynonym expDate,
			String status, GenericEntityIdentity[] entityIdentities, GenericCategoryIdentity[] categoryIdentities) throws SQLException {
		Connection conn = getConnection();
		PreparedStatement ps = null;
		try {
			conn.setAutoCommit(false);

			ps = conn.prepareStatement(Q_INSERT_GRID);
			ps.setInt(1, gridID);
			ps.setInt(2, templateID);
			ps.setString(3, cellValues);
			ps.setInt(4, numRows);
			ps.setString(5, status);

			int count = ps.executeUpdate();
			if (count < 1) { throw new SQLException("Parameter Grid Insert Failed (count=" + count + ")"); }
			ps.close();
			ps = null;

			ps = conn.prepareStatement(Q_INSERT_GRID_DATE_SYNONYM);
			ps.setInt(1, gridID);
			ps.setInt(2, (effDate == null ? -1 : effDate.getID()));
			ps.setInt(3, (expDate == null ? -1 : expDate.getID()));
			count = ps.executeUpdate();
			if (count < 1) { throw new SQLException("Parameter activation date synonym insert Failed (count=" + count + ")"); }
			ps.close();
			ps = null;

			if (entityIdentities != null) {
				ps = conn.prepareStatement(Q_INSERT_ENTITY_CONTEXT);
				for (int i = 0; i < entityIdentities.length; i++) {
					ps.setInt(1, gridID);
					ps.setInt(2, entityIdentities[i].getEntityID());
					ps.setInt(3, entityIdentities[i].getEntityType());
					count = ps.executeUpdate();
					if (count < 1) { throw new SQLException("Parameter Entity Context Insert Failed (count=" + count + ")"); }
				}
				ps.close();
				ps = null;
			}
			if (categoryIdentities != null) {
				ps = conn.prepareStatement(Q_INSERT_CATEGORY_CONTEXT);
				for (int i = 0; i < categoryIdentities.length; i++) {
					ps.setInt(1, gridID);
					ps.setInt(2, categoryIdentities[i].getCategoryID());
					ps.setInt(3, categoryIdentities[i].getCategoryType());
					count = ps.executeUpdate();
					if (count < 1) { throw new SQLException("Parameter Category Context Insert Failed (count=" + count + ")"); }
				}
				ps.close();
				ps = null;
			}

			conn.commit();
		}
		catch (Exception ex) {
			conn.rollback();
			logger.error("Failed to insert new parameter: " + gridID + "," + templateID + "," + cellValues + "," + numRows + "," + effDate
					+ "," + expDate + "," + status, ex);
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

	public void removeGrid(int gridID) throws SQLException {
		Connection conn = getConnection();
		PreparedStatement ps = null;
		try {
			conn.setAutoCommit(false);

			ps = conn.prepareStatement(Q_DELETE_ENTITY_CONTEXT);
			ps.setInt(1, gridID);
			int count = ps.executeUpdate();
			logger.debug("deleted " + count + " entity context");
			ps.close();
			ps = null;

			ps = conn.prepareStatement(Q_DELETE_GRID_DATE_SYNONYM);
			ps.setInt(1, gridID);
			count = ps.executeUpdate();
			logger.debug("deleted " + count + " date synonyms");
			ps.close();
			ps = null;

			ps = conn.prepareStatement(Q_DELETE_GRID);
			ps.setInt(1, gridID);
			ps.executeUpdate();

			conn.commit();
		}
		catch (Exception ex) {
			conn.rollback();
			logger.error("Failed to delete parameter: " + gridID, ex);
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

	public void updateGrid(int gridID, int templateID, String cellValues, int numRows, DateSynonym effDate, DateSynonym expDate,
			String status, GenericEntityIdentity[] entityIdentities, GenericCategoryIdentity[] categoryIdentities) throws SQLException {
		Connection conn = getConnection();
		PreparedStatement ps = null;
		try {
			conn.setAutoCommit(false);

			ps = conn.prepareStatement(Q_UPDATE_GRID);
			ps.setInt(1, templateID);
			ps.setString(2, cellValues);
			ps.setInt(3, numRows);
			ps.setString(4, status);
			ps.setInt(5, gridID);

			int count = ps.executeUpdate();
			if (count < 1) { throw new SQLException("Parameter Grid Update Failed (count=" + count + ")"); }
			ps.close();
			ps = null;

			ps = conn.prepareStatement(Q_UPDATE_GRID_DATE_SYNONYM);
			ps.setInt(1, (effDate == null ? -1 : effDate.getID()));
			ps.setInt(2, (expDate == null ? -1 : expDate.getID()));
			ps.setInt(3, gridID);
			count = ps.executeUpdate();
			if (count < 1) { throw new SQLException("Parameter date synonym update Failed (count=" + count + ")"); }
			ps.close();
			ps = null;

			// remove old context
			ps = conn.prepareStatement(Q_DELETE_ENTITY_CONTEXT);
			ps.setInt(1, gridID);
			ps.executeUpdate();
			ps.close();
			ps = null;

			if (entityIdentities != null) {
				ps = conn.prepareStatement(Q_INSERT_ENTITY_CONTEXT);
				for (int i = 0; i < entityIdentities.length; i++) {
					ps.setInt(1, gridID);
					ps.setInt(2, entityIdentities[i].getEntityID());
					ps.setInt(3, entityIdentities[i].getEntityType());
					count = ps.executeUpdate();
					if (count < 1) { throw new SQLException("Parameter Entity Context Insert Failed (count=" + count + ")"); }
				}
				ps.close();
				ps = null;
			}
			if (categoryIdentities != null) {
				ps = conn.prepareStatement(Q_INSERT_CATEGORY_CONTEXT);
				for (int i = 0; i < categoryIdentities.length; i++) {
					ps.setInt(1, gridID);
					ps.setInt(2, categoryIdentities[i].getCategoryID());
					ps.setInt(3, categoryIdentities[i].getCategoryType());
					count = ps.executeUpdate();
					if (count < 1) { throw new SQLException("Parameter Category Context Insert Failed (count=" + count + ")"); }
				}
				ps.close();
				ps = null;
			}

			conn.commit();
		}
		catch (Exception ex) {
			conn.rollback();
			logger.error("Failed to update parameter: " + gridID + "," + templateID + "," + cellValues + "," + numRows + "," + effDate
					+ "," + expDate + "," + status, ex);
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
	 * Update the context row of the specified grid.
	 * 
	 * @param gridID
	 * @param entityIdentities entity identities; can be <code>null</code>
	 * @param categoryIdentities category identities; can be <code>null</code>
	 * @throws SQLException
	 *             on db error
	 */
	public void updateGridContext(int gridID, GenericEntityIdentity[] entityIdentities, GenericCategoryIdentity[] categoryIdentities) throws SQLException {
		logger.debug(">>> updateGridContext: " + gridID + "," + entityIdentities + "," + categoryIdentities);
		Connection conn = getConnection();
		PreparedStatement ps = null;
		try {
			ps = conn.prepareStatement(Q_DELETE_ENTITY_CONTEXT);
			ps.setInt(1, gridID);
			int count = ps.executeUpdate();
			logger.debug(count + " entity context rows deleted");
			ps.close();
			ps = null;

			if (entityIdentities != null && entityIdentities.length > 0) {
				ps = conn.prepareStatement(Q_INSERT_ENTITY_CONTEXT);
				for (int i = 0; i < entityIdentities.length; i++) {
					ps.setInt(1, gridID);
					ps.setInt(2, entityIdentities[i].getEntityID());
					ps.setInt(3, entityIdentities[i].getEntityType());
					count = ps.executeUpdate();
					if (count < 1) {
						throw new SQLException("Grid Entity Context Insert Failed (count=" + count + ")");
					}
				}
				ps.close();
				ps = null;
			}
			if (categoryIdentities != null && categoryIdentities.length > 0) {
				ps = conn.prepareStatement(Q_INSERT_CATEGORY_CONTEXT);
				for (int i = 0; i < categoryIdentities.length; i++) {
					ps.setInt(1, gridID);
					ps.setInt(2, categoryIdentities[i].getCategoryID());
					ps.setInt(3, categoryIdentities[i].getCategoryType());
					count = ps.executeUpdate();
					if (count < 1) {
						throw new SQLException("Grid Category Context Insert Failed (count=" + count + ")");
					}
				}
				ps.close();
				ps = null;
			}
		}
		catch (SQLException exp) {
			if (exp.getMessage().equalsIgnoreCase("No Data Found") == false) {
				throw exp;
			}
			else {
				logger.info("No Data Found");
			}
		}
		finally {
			if (ps != null)
				ps.close();
		}
	}

	public void replaceDateSynonymReferences(DateSynonym[] toBeReplaced, DateSynonym replacement) throws SQLException {
		replaceDateSynonymReferencesInIntersectionTable(toBeReplaced, replacement, Q_REPLACE_ALL_GRID_EFFECTIVE_DATE_SYNONYM, 
				Q_REPLACE_ALL_GRID_EXPIRATION_DATE_SYNONYM);
	}
}