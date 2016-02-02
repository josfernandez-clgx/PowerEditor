package com.mindbox.pe.server.db.loaders;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.mindbox.pe.common.UtilBase;
import com.mindbox.pe.model.GenericEntityType;
import com.mindbox.pe.server.cache.DateSynonymManager;
import com.mindbox.pe.server.cache.EntityManager;
import com.mindbox.pe.server.cache.ParameterManager;
import com.mindbox.pe.server.config.KnowledgeBaseFilterConfig;
import com.mindbox.pe.server.db.DBConnectionManager;
import com.mindbox.pe.server.db.DBUtil;

/**
 * @author Geneho Kim
 * @author MindBox
 * @since PowerEditor 2.2.0
 */
public class ParameterLoader extends AbstractLoader {

	private static final String Q_SELECT_GRID = "SELECT g.parameter_id,g.template_id,g.cell_values,g.num_rows,g.status,"
			+ "d.effective_synonym_id, d.expiration_synonym_id FROM MB_PARAMETER g, MB_PARAMETER_DATE_SYNONYM d where g.parameter_id = d.parameter_id ORDER BY g.parameter_id";

	private static final String Q_SELECT_ENTITY_CONTEXT = "SELECT parameter_id,entity_id,entity_type,category_id,category_type FROM MB_ENTITY_PARAMETER_CONTEXT ORDER BY parameter_id";

	/**
	 * 
	 */
	public ParameterLoader() {
		super();
	}

	public void load(KnowledgeBaseFilterConfig knowledgeBaseFilterConfig) throws SQLException {
		logger.info("=== Parameter Data ===");
		ParameterManager.getInstance().startLoading();
		try {
			loadGrid(knowledgeBaseFilterConfig);
			loadEntityContext();
		}
		finally {
			ParameterManager.getInstance().finishLoading();
		}
	}

	private void loadGrid(KnowledgeBaseFilterConfig knowledgeBaseFilterConfig) throws SQLException {
		logger.info("=== Parameter Grid ===");
		DBConnectionManager dbconnectionmanager = DBConnectionManager.getInstance();
		Connection conn = dbconnectionmanager.getConnection();
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = conn.prepareStatement(Q_SELECT_GRID);
			rs = ps.executeQuery();

			while (rs.next()) {
				int gridID = rs.getInt(1);
				int templateID = rs.getInt(2);
				String cellValues = UtilBase.trim(rs.getString(3));
				int rowCount = rs.getInt(4);
				String status = UtilBase.trim(rs.getString(5));
				int effID = rs.getInt(6);
				int expID = rs.getInt(7);

				if (dateSysnonymsPassFilter(effID, expID, knowledgeBaseFilterConfig.getDateFilterConfig())) {

					ParameterManager.getInstance().addParameterGrid(
							gridID,
							templateID,
							cellValues,
							rowCount,
							DateSynonymManager.getInstance().getDateSynonym(effID),
							DateSynonymManager.getInstance().getDateSynonym(expID),
							status);

					logger.info("ParameterGrid: " + gridID + ",template=" + templateID + ",cellValues=" + cellValues + ",rowCount="
							+ rowCount + ",sunrise=" + effID + ",sunset=" + expID + ",status=" + status);
				}
				else {
					logger.info(String.format(
							"*SKIPPED* Parmeter Grid filtered out by KBDateFilter: gridID=%d,template=%d,cellValues=%s,rowCount=%d,effID=%d,expID=%d,status=%s",
							gridID,
							templateID,
							cellValues,
							rowCount,
							effID,
							expID,
							status));
				}
			}
		}
		finally {
			DBUtil.closeLocallyManagedResources(rs, ps);
			dbconnectionmanager.freeConnection(conn);
		}
	}

	/**
	 * @throws SQLException
	 * @since 3.0.0
	 */
	private void loadEntityContext() throws SQLException {
		logger.info("=== Parameter Entity Context ===");
		DBConnectionManager dbconnectionmanager = DBConnectionManager.getInstance();
		Connection conn = dbconnectionmanager.getConnection();
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = conn.prepareStatement(Q_SELECT_ENTITY_CONTEXT);
			rs = ps.executeQuery();

			while (rs.next()) {
				int gridID = rs.getInt(1);
				int entityID = rs.getInt(2);
				int entityType = rs.getInt(3);
				int categoryID = rs.getInt(4);
				int categoryType = rs.getInt(5);

				if (entityID > 0) {
					if (EntityManager.getInstance().getEntity(GenericEntityType.forID(entityType), entityID) == null) {
						logger.warn("ParameterEntityContext ignored - generic entity not found (" + gridID + "," + entityID + ','
								+ entityType);
					}
					else {
						boolean added = ParameterManager.getInstance().addGridContext(gridID, GenericEntityType.forID(entityType), entityID);
						if (added) {
							logger.info("ParameterEntityContext: " + gridID + ",type=" + entityType + ",id=" + entityID);
						}
						else {
							logger.warn("Ignoring grid context: grid=" + gridID + ",type=" + entityType + ",entityID=" + entityID
									+ "; no grid " + gridID + " exist");
						}
					}
				}
				else if (categoryID > 0) {
					if (EntityManager.getInstance().getGenericCategory(categoryType, categoryID) == null) {
						logger.warn("ParameterEntityContext ignored - generic category not found (" + gridID + "," + categoryType + ','
								+ categoryID);
					}
					else {
						boolean added = ParameterManager.getInstance().addGridContext(gridID, categoryType, categoryID);
						if (added) {
							logger.info("ParameterEntityContext: " + gridID + ",type=" + categoryType + ",id=" + categoryID);
						}
						else {
							logger.warn("Ignoring grid context: grid=" + gridID + ",categoryType=" + categoryType + ",categoryID="
									+ categoryID + "; no grid " + gridID + " exist");
						}
					}
				}
			}
		}
		finally {
			DBUtil.closeLocallyManagedResources(rs, ps);
			dbconnectionmanager.freeConnection(conn);
		}
	}

}