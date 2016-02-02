/*
 * Created on 2004. 12. 08.
 *
 */
package com.mindbox.pe.server.db.loaders;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.Date;

import com.mindbox.pe.common.UtilBase;
import com.mindbox.pe.server.cache.DateSynonymManager;
import com.mindbox.pe.server.config.KnowledgeBaseFilterConfig;
import com.mindbox.pe.server.db.DBConnectionManager;
import com.mindbox.pe.server.db.DBUtil;


/**
 * Loads date synonyms into cache from DB.
 * @author Geneho
 * @since PowerEditor 4.2.0
 */
public class DateSynonymLoader extends AbstractLoader {

	private static final String Q_LOAD_DATE_SYNONYMS = "select synonym_id,synonym_name,synonym_desc,synonym_date,is_named from MB_DATE_SYNONYM";


	private static DateSynonymLoader instance = null;

	public static DateSynonymLoader getInstance() {
		if (instance == null) {
			instance = new DateSynonymLoader();
		}
		return instance;
	}

	private DateSynonymLoader() {
	}

	@Override
	public void load(KnowledgeBaseFilterConfig knowledgeBaseFilterConfig) throws SQLException, ParseException {
		// Load all date synonyms, ignoring date filter
		DateSynonymManager.getInstance().startLoading();
		try {
			loadDateSynonyms(knowledgeBaseFilterConfig);
		}
		finally {
			DateSynonymManager.getInstance().finishLoading();
		}

	}

	private void loadDateSynonyms(KnowledgeBaseFilterConfig knowledgeBaseFilterConfig) throws SQLException, ParseException {
		logger.info("=== DATE SYNONYMS ===");

		DBConnectionManager dbconnectionmanager = DBConnectionManager.getInstance();
		Connection conn = dbconnectionmanager.getConnection();
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = conn.prepareStatement(Q_LOAD_DATE_SYNONYMS);
			rs = ps.executeQuery();

			while (rs.next()) {
				int id = rs.getInt(1);
				String name = UtilBase.trim(rs.getString(2));
				String desc = UtilBase.trim(rs.getString(3));
				Date date = DBUtil.getDateValue(rs, 4);
				boolean isNamed = rs.getBoolean(5);

				DateSynonymManager.getInstance().insert(id, name, desc, date, isNamed);
				logger.info("Date Synonym: " + id + ",name=" + name + ",desc=" + desc + ",date=" + date + ",isNamed=" + isNamed);

				// Makr as not-in-use if the date is filtered out
				if (knowledgeBaseFilterConfig.getDateFilterConfig() != null) {
					if (date != null && !knowledgeBaseFilterConfig.getDateFilterConfig().isInRange(date)) {
						DateSynonymManager.getInstance().getDateSynonym(id).setNotInUse(true);
					}
				}
			}
			rs.close();
			rs = null;
			logger.info("=== " + DateSynonymManager.getInstance().getAllDateSynonyms().size() + " date synonyms loaded!!!");
		}
		finally {
			DBUtil.closeLocallyManagedResources(rs, ps);
			dbconnectionmanager.freeConnection(conn);
		}
	}


}