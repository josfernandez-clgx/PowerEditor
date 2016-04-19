/*
 * Created on 2004. 4. 15.
 *  
 */
package com.mindbox.pe.server.db.loaders;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import com.mindbox.pe.common.UtilBase;
import com.mindbox.pe.model.DateSynonym;
import com.mindbox.pe.model.GenericEntityType;
import com.mindbox.pe.server.cache.DateSynonymManager;
import com.mindbox.pe.server.config.ConfigurationManager;
import com.mindbox.pe.server.db.DBConnectionManager;
import com.mindbox.pe.server.db.DBUtil;
import com.mindbox.pe.server.spi.db.EntityDataHolder;
import com.mindbox.pe.server.spi.db.GenericEntityDataProvider;
import com.mindbox.pe.xsd.config.KnowledgeBaseFilter;

/**
 * Generic entity loader. This extends {@link AbstractLoader}to load channels and
 * investors, until they get converted to generic entities.
 * 
 * @author Geneho Kim
 * @author MindBox
 * @since PowerEditor 3.0.0
 */
public class GenericEntityLoader extends AbstractLoader implements GenericEntityDataProvider {

	private static final String Q_SELECT_ENTITY = "select entity_id,entity_type,entity_name,parent_id from MB_ENTITY order by entity_id";

	private static final String Q_SELECT_ENTITY_PROPERTY = "select property_name,string_value,blob_value from MB_ENTITY_PROPERTY where entity_id=? and entity_type=?";

	private static final String Q_SELECT_ENTITY_COMPATIBILITY = "select entity1_id,entity1_type,entity2_id,entity2_type,effective_synonym_id, expiration_synonym_id from MB_ENTITY_COMPATIBILITY order by entity1_type";

	private static final String Q_SELECT_CATEGORY = "select category_id,category_name,category_type from MB_ENTITY_CATEGORY";

	private static final String Q_SELECT_CATEGORY_PARENT = "select category_id,category_type,parent_id,effective_synonym_id,expiration_synonym_id from MB_ENTITY_CATEGORY_PARENT";

	private static final String Q_SELECT_ENTITY_TO_CATEEGORY = "select entity_id,entity_type,category_id,category_type,effective_synonym_id,expiration_synonym_id from MB_ENTITY_CATEGORY_LINK";


	public void loadGenericEntities(EntityDataHolder entityDataHolder) throws SQLException {
		logger.info("===== Generic Entities =======");

		DBConnectionManager dbconnectionmanager = DBConnectionManager.getInstance();
		Connection conn = dbconnectionmanager.getConnection();
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = conn.prepareStatement(Q_SELECT_ENTITY);
			rs = ps.executeQuery();
			while (rs.next()) {
				int id = rs.getInt(1);
				int type = rs.getInt(2);
				String name = UtilBase.trim(rs.getString(3));
				int parentID = rs.getInt(4);
				if (GenericEntityType.hasTypeFor(type)) {
					entityDataHolder.addGenericEntity(id, type, name, parentID, getPropertyMap(id, type));
					logger.info("Generic entity: id=" + id + ",type=" + type + ",name=" + name + ",parent=" + parentID);
				}
			}
		}
		finally {
			if (rs != null) rs.close();
			if (ps != null) ps.close();
			dbconnectionmanager.freeConnection(conn);
		}
	}

	private Map<String, Object> getPropertyMap(int entityID, int entityType) throws SQLException {
		logger.debug(">>> getPropertyMap: " + entityID + " of " + entityType);

		DBConnectionManager dbconnectionmanager = DBConnectionManager.getInstance();
		Connection conn = dbconnectionmanager.getConnection();
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = conn.prepareStatement(Q_SELECT_ENTITY_PROPERTY);
			ps.setInt(1, entityID);
			ps.setInt(2, entityType);

			Map<String, Object> map = new HashMap<String, Object>();

			rs = ps.executeQuery();
			while (rs.next()) {
				String name = UtilBase.trim(rs.getString(1));
				String value = UtilBase.trim(rs.getString(2));
				if (value == null || value.length() == 0) {
					value = DBUtil.extractBlobValue(rs, 3);
				}
				map.put(name, value);
				logger.debug("getPropertyMap: retrieved " + name + "=" + value);
			}
			logger.debug("<<< getPropertyMap: map.size = " + map.size());
			return map;
		}
		finally {
			if (rs != null) rs.close();
			if (ps != null) ps.close();
			dbconnectionmanager.freeConnection(conn);
		}
	}

	public void loadEntityCompaitilityMatrix(EntityDataHolder entityDataHolder) throws SQLException {
		logger.info("===== Generic Entity Compatibility =======");

		DBConnectionManager dbconnectionmanager = DBConnectionManager.getInstance();
		Connection conn = dbconnectionmanager.getConnection();
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = conn.prepareStatement(Q_SELECT_ENTITY_COMPATIBILITY);
			rs = ps.executeQuery();
			while (rs.next()) {
				int id1 = rs.getInt(1);
				int type1 = rs.getInt(2);
				int id2 = rs.getInt(3);
				int type2 = rs.getInt(4);
				int effID = rs.getInt(5);
				int expID = rs.getInt(6);

				// check date filter, if present
				if (dateSysnonymsPassFilter(effID, expID, ConfigurationManager.getInstance().getDateFilterConfigHelper())) {

					if (GenericEntityType.hasTypeFor(type1) && GenericEntityType.hasTypeFor(type2)) {

						DateSynonym effectiveDateSynonym = DateSynonymManager.getInstance().getDateSynonym(effID);
						DateSynonym expirationDateSynonym = DateSynonymManager.getInstance().getDateSynonym(expID);

						entityDataHolder.addEntityCompatibility(type1, id1, type2, id2, effectiveDateSynonym, expirationDateSynonym);
						logger.info(String.format("Loaded Compatibility: id1=%s,type1=%s,id2=%d,type2=%s,effDateID=%d,expDateID=%d", id1, type1, id2, type2, effID, expID));
					}
					else {
						logger.info(String.format(
								"*SKIPPED* Compatibility has invalid entity type1 or type2: id1=%s,type1=%s,id2=%d,type2=%s,effDateID=%d,expDateID=%d",
								id1,
								type1,
								id2,
								type2,
								effID,
								expID));
					}
				}
				else {
					logger.info(String.format("*SKIPPED* Compatibility Filtered out by KBDateFilter: id1=%s,type1=%s,id2=%d,type2=%s,effDateID=%d,expDateID=%d", id1, type1, id2, type2, effID, expID));
				}
			}
		}
		finally {
			if (rs != null) rs.close();
			if (ps != null) ps.close();
			dbconnectionmanager.freeConnection(conn);
		}
	}

	public void loadCategories(EntityDataHolder entityDataHolder) throws SQLException {
		logger.info("===== Generic Categories =======");

		DBConnectionManager dbconnectionmanager = DBConnectionManager.getInstance();
		Connection conn = dbconnectionmanager.getConnection();
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = conn.prepareStatement(Q_SELECT_CATEGORY);
			rs = ps.executeQuery();
			while (rs.next()) {
				int id = rs.getInt(1);
				String name = UtilBase.trim(rs.getString(2));
				int type = rs.getInt(3);

				entityDataHolder.addGenericEntityCategory(type, id, name);
				logger.info("Generic category: id=" + id + ",type=" + type + ",name=" + name);
			}
		}
		finally {
			if (rs != null) rs.close();
			if (ps != null) ps.close();
			dbconnectionmanager.freeConnection(conn);
		}
	}

	public void loadCategoryParents(EntityDataHolder entityDataHolder) throws SQLException {
		logger.info("===== Generic Category Parents =======");

		DBConnectionManager dbconnectionmanager = DBConnectionManager.getInstance();
		Connection conn = dbconnectionmanager.getConnection();
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = conn.prepareStatement(Q_SELECT_CATEGORY_PARENT);
			rs = ps.executeQuery();
			while (rs.next()) {
				int catID = rs.getInt(1);
				int type = rs.getInt(2);
				int parentID = rs.getInt(3);
				int effID = rs.getInt(4);
				int expID = rs.getInt(5);

				if (dateSysnonymsPassFilter(effID, expID, ConfigurationManager.getInstance().getDateFilterConfigHelper())) {
					try {
						entityDataHolder.addParentAssociation(type, catID, parentID, effID, expID);
						logger.info("Generic category parent: cat=" + catID + ",type=" + type + ",parent=" + parentID + ", effID=" + effID + ",expID=" + expID);
					}
					catch (Exception ex) {
						logger.warn("Ignore invalid association: cat=" + catID + ",type=" + type + ",parent=" + parentID + ", effID=" + effID + ",expID=" + expID, ex);
					}
				}
				else {
					logger.info(String.format("*SKIPPED* Generic Category Assoc. Filtered out by KBDateFilter: cat=%d,type=%s,parent=%d,effID=%d,expID=%d", catID, type, parentID, effID, expID));
				}
			}
		}
		finally {
			if (rs != null) rs.close();
			if (ps != null) ps.close();
			dbconnectionmanager.freeConnection(conn);
		}
	}

	public void loadEntityToCategories(EntityDataHolder entityDataHolder) throws SQLException {
		logger.info("===== Generic Entity-->Categories =======");

		DBConnectionManager dbconnectionmanager = DBConnectionManager.getInstance();
		Connection conn = dbconnectionmanager.getConnection();
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = conn.prepareStatement(Q_SELECT_ENTITY_TO_CATEEGORY);
			rs = ps.executeQuery();
			while (rs.next()) {
				int entityID = rs.getInt(1);
				int entityType = rs.getInt(2);
				int catID = rs.getInt(3);
				int catType = rs.getInt(4);
				int effID = rs.getInt(5);
				int expID = rs.getInt(6);

				if (dateSysnonymsPassFilter(effID, expID, ConfigurationManager.getInstance().getDateFilterConfigHelper())) {
					entityDataHolder.addGenericEntityToCategory(catID, catType, entityID, entityType, effID, expID);
					logger.info("Generic entity-category: eid=" + entityID + ",etype=" + entityType + ",cid=" + catID + ",ctype=" + catType + ",eff=" + effID + ",exp=" + expID);
				}
				else {
					logger.info(String.format(
							"*SKIPPED* Generic entity-category Filtered out by KBDateFilter: eid=%d,etype=%s,catId=%d,catType=%s,effID=%d,expID=%d",
							entityID,
							entityType,
							catID,
							catType,
							effID,
							expID));
				}
			}
		}
		finally {
			if (rs != null) rs.close();
			if (ps != null) ps.close();
			dbconnectionmanager.freeConnection(conn);
		}
	}

	public void load(KnowledgeBaseFilter knowledgeBaseFilterConfig) throws Exception {
		// NOT USED
	}

}