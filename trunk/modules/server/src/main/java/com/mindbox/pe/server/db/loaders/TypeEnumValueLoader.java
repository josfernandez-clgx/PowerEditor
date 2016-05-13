/*
 * Created on 2005. 5. 23.
 *
 */
package com.mindbox.pe.server.db.loaders;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.mindbox.pe.common.UtilBase;
import com.mindbox.pe.model.TypeEnumValue;
import com.mindbox.pe.server.cache.TypeEnumValueManager;
import com.mindbox.pe.server.db.DBConnectionManager;
import com.mindbox.pe.xsd.config.KnowledgeBaseFilter;


/**
 * Type enumeration value loader.
 * @author Geneho Kim
 * @since PowerEditor 4.3.1
 */
public class TypeEnumValueLoader extends AbstractLoader {

	private static final String Q_LOAD = "select enum_type,enum_id,enum_value,enum_disp_label from MB_TYPE_ENUM order by enum_type,enum_id";

	private static TypeEnumValueLoader instance = null;

	/**
	 * Gets the one and only instance of this class.
	 * @return the only instance
	 */
	public static TypeEnumValueLoader getInstance() {
		if (instance == null) {
			instance = new TypeEnumValueLoader();
		}
		return instance;
	}

	private TypeEnumValueLoader() {
	}

	public void load(final KnowledgeBaseFilter knowledgeBaseFilterConfig) throws SQLException {
		// NOTE: filter not used as date filter is not applicable to guideline actions

		TypeEnumValueManager.getInstance().startLoading();

		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			conn = DBConnectionManager.getInstance().getConnection();

			ps = conn.prepareStatement(Q_LOAD);
			rs = ps.executeQuery();
			while (rs.next()) {
				String type = UtilBase.trim(rs.getString(1));
				int id = rs.getInt(2);
				String value = UtilBase.trim(rs.getString(3));
				String dispLabel = UtilBase.trim(rs.getString(4));

				TypeEnumValueManager.getInstance().insert(type, new TypeEnumValue(id, value, dispLabel));
				logger.info("TypeEnumValue: type=" + type + " - " + id + ',' + value + ',' + dispLabel);
			}

			TypeEnumValueManager.getInstance().finishLoading();
		}
		finally {
			if (rs != null) rs.close();
			if (ps != null) ps.close();
			DBConnectionManager.getInstance().freeConnection(conn);

		}
	}

}