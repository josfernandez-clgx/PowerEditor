package com.mindbox.pe.tools.migration;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.mindbox.pe.common.UtilBase;
import com.mindbox.pe.common.config.UIConfiguration;
import com.mindbox.pe.tools.db.DBUtil;


/**
 * @author Geneho Kim
 * @since PowerEditor 
 */
public class MigrationWorker {

	private static final String Q_LOAD_TEMPLATE = "select g.template_id,g.name,g.usage,a.label_name,a.activation_date,a.expiration_date"
			+ " from MB_TEMPLATE g, MB_TEMPLATE_LABEL l, MB_ACTIVATION_LABEL a" + " where g.template_id=l.template_id and l.label_id=a.label_id "
			+ " and g.template_id not in (select template_id from MB_TEMPLATE_VERSION) order by g.template_id";

	private static final String Q_SET_TEMPLATE_VERSION = "update MB_TEMPLATE_VERSION set version=? where template_id=?";

	private static final String Q_INSERT_TEMPLATE_VERSION = "insert into MB_TEMPLATE_VERSION (template_id,version) values (?,?)";

	private static final String Q_SET_TEMPLATE_PARENT = "update MB_TEMPLATE set parent_id=? where template_id=?";

	private static final String Q_LOAD_GUIDELINE_DATES = "select a.label_id,a.activation_date,a.expiration_date from MB_ACTIVATION_LABEL a, MB_GRID_LABEL g"
			+ " where a.label_id=g.label_id and g.grid_id not in (select grid_id from MB_GRID_DATE_SYNONYM)";

	private static final String Q_INSERT_DATE_SYNONYM = "insert into MB_DATE_SYNONYM (synonym_id,synonym_name,synonym_desc,synonym_date,is_named) values (?,?,?,?,?)";

	private static final String Q_GET_NEXT_ID = "select next_id from MB_ID_GENERATOR where id_type='SEQUENTIAL'";

	private static final String Q_SET_NEXT_ID = "update MB_ID_GENERATOR set next_id=? where id_type='SEQUENTIAL'";

	private static final String Q_INSERT_GRID_DATE_SYNONYM = "insert into MB_GRID_DATE_SYNONYM (grid_id,effective_synonym_id,expiration_synonym_id) values (?,?,?)";

	private static final String Q_LOAD_GRID_DATES_FROM_LABEL = "select g.grid_id,a.activation_date,a.expiration_date from MB_ACTIVATION_LABEL a, MB_GRID_LABEL g"
			+ " where a.label_id=g.label_id and g.grid_id not in (select grid_id from MB_GRID_DATE_SYNONYM) order by g.grid_id";

	private static final String Q_LOAD_PARAMETER_DATES = "select parameter_id,activation_date,expiration_date from MB_PARAMETER"
			+ " where parameter_id not in (select parameter_id from MB_PARAMETER_DATE_SYNONYM)";

	private static final String Q_INSERT_PARAMETER_DATE_SYNONYM = "insert into MB_PARAMETER_DATE_SYNONYM (parameter_id,effective_synonym_id,expiration_synonym_id) values (?,?,?)";

	private static final String Q_LOAD_CBR_CASE_BASE_DATES = "select c.case_base_id,a.activation_date,a.expiration_date from MB_ACTIVATION_LABEL a, MB_CBR_CASE_BASE_LABEL_MAPPING c"
			+ " where a.label_id=c.label_id and c.case_base_id not in (select case_base_id from MB_CBR_CASE_BASE_DATE_SYNONYM)";

	private static final String Q_INSERT_CASE_BASE_DATE_SYNONYM = "insert into MB_CBR_CASE_BASE_DATE_SYNONYM (case_base_id,effective_synonym_id,expiration_synonym_id) values (?,?,?)";

	private static final String Q_LOAD_CBR_CASE_DATES = "select c.case_id,a.activation_date,a.expiration_date from MB_ACTIVATION_LABEL a, MB_CBR_CASE_LABEL_MAPPING c"
			+ " where a.label_id=c.label_id and c.case_id not in (select case_id from MB_CBR_CASE_DATE_SYNONYM)";

	private static final String Q_INSERT_CASE_DATE_SYNONYM = "insert into MB_CBR_CASE_DATE_SYNONYM (case_id,effective_synonym_id,expiration_synonym_id) values (?,?,?)";

	private static final String Q_LOAD_GUIDELINE_ACTION_PARAMS = "select function_id,param_id,param_name,deploy_type from MB_GUIDELINE_FUNCTION_PARAMETER";
			//+ " where action_id not in (select function_id from MB_GUIDELINE_FUNCTION_PARAMETER) order by action_id";

	private static final String Q_INSERT_GUIDELINE_FUNCTION_PARAM = "insert into MB_GUIDELINE_ACTION_PARAMETER (action_id,param_id,param_name,deploy_type) values (?,?,?,?)";
	private static final String Q_UPDATE_GUIDELINE_FUNCTION_PARAM = "update MB_GUIDELINE_ACTION_PARAMETER set param_id=?,param_name=?,deploy_type=? where action_id=?";

	private static String toLabelString(String name, Date effDate, Date expDate) {
		StringBuffer buff = new StringBuffer();
		buff.append(name);
		buff.append(" (");
		if (effDate != null) buff.append(UIConfiguration.FORMAT_DATE_TIME_SEC.format(effDate));
		buff.append(" - ");
		if (expDate != null) buff.append(UIConfiguration.FORMAT_DATE_TIME_SEC.format(expDate));
		buff.append(")");
		return buff.toString();
	}


	private static MigrationWorker instance = null;

	/**
	 * Gets the one and only instance of this class.
	 * @return the only instance
	 */
	public static MigrationWorker getInstance() {
		if (instance == null) {
			instance = new MigrationWorker();
		}
		return instance;
	}


	private static class DateMap {

		int gridID = -1;
		Date actDate = null;
		Date expDate = null;

		public String toString() {
			return "GridDateMap[" + gridID + "," + actDate + "," + expDate + "]";
		}
	}

	private MigrationWorker() {
	}

	private DateMap[] loadDatesToMigrate(Connection conn, String query, String entityName) throws SQLException {
		PreparedStatement ps = null;
		ResultSet rs = null;
		List<DateMap> list = new ArrayList<DateMap>();

		try {
			ps = conn.prepareStatement(query);
			rs = ps.executeQuery();
			while (rs.next()) {
				DateMap map = new DateMap();
				map.gridID = rs.getInt(1);
				map.actDate = DBUtil.getDateValue(rs, 2);
				map.expDate = DBUtil.getDateValue(rs, 3);

				list.add(map);
			}

			return list.toArray(new DateMap[0]);
		}
		catch (SQLException ex) {
			ex.printStackTrace();
			throw ex;
		}
		catch (Exception ex) {
			ex.printStackTrace();
			throw new SQLException("Failed to retrieve " + entityName + " dates to migrate: " + ex.getMessage());
		}
		finally {
			if (rs != null) rs.close();
			if (ps != null) ps.close();
		}
	}

	private DateMap[] loadCaseBaseToUpdate(Connection conn) throws SQLException {
		return loadDatesToMigrate(conn, Q_LOAD_CBR_CASE_BASE_DATES, "case base");
	}

	private DateMap[] loadCaseToUpdate(Connection conn) throws SQLException {
		return loadDatesToMigrate(conn, Q_LOAD_CBR_CASE_DATES, "case");
	}

	private DateMap[] loadGridToUpdate(Connection conn) throws SQLException {
		return loadDatesToMigrate(conn, Q_LOAD_GRID_DATES_FROM_LABEL, "guideline grid");
	}

	private DateMap[] loadParameterToUpdate(Connection conn) throws SQLException {
		return loadDatesToMigrate(conn, Q_LOAD_PARAMETER_DATES, "parameter");
	}

	/**
	 * 
	 * @param conn
	 * @param mapList
	 * @return  count array; [0] = number of date synonyms created; [1] = number of case base updated; [2] = number of case updated
	 * @throws SQLException
	 */
	public synchronized int[] migrateCBRDates(Connection conn, List<GuidelineDateMap> mapList) throws SQLException {
		int[] counts1 = migrateDates(conn, mapList, loadCaseBaseToUpdate(conn), Q_INSERT_CASE_BASE_DATE_SYNONYM, "case base");
		int[] counts2 = migrateDates(conn, mapList, loadCaseToUpdate(conn), Q_INSERT_CASE_DATE_SYNONYM, "case");
		return new int[] { counts1[0] + counts2[0], counts1[1], counts2[1]};
	}

	public synchronized List<GuidelineDateMap> preprocessCBRDates(Connection conn) throws SQLException {
		// load parameter dates from DB
		List<GuidelineDateMap> list = new ArrayList<GuidelineDateMap>();
		List<Date> dateList = loadDatesToProcess(conn, Q_LOAD_CBR_CASE_BASE_DATES);
		dateList.addAll(loadDatesToProcess(conn, Q_LOAD_CBR_CASE_DATES));

		// set parent id's based on template namd and label
		for (int i = 0; i < dateList.size(); i++) {
			Date date = (Date) dateList.get(i);
			list.add(new GuidelineDateMap(date));
		}
		return list;
	}

	private int[] migrateDates(Connection conn, List<GuidelineDateMap> mapList, DateMap[] dateMaps, String insertDSMapQuery, String entityName) throws SQLException {
		System.out.println(">>> migrateDates: " + entityName + ",map.size=" + mapList.size() + ",q=" + insertDSMapQuery);
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			conn.setAutoCommit(false);

			int total1 = 0;
			int total2 = 0;

			Map<Date, Integer> dsMap = new HashMap<Date, Integer>();

			// 1 get the next id
			int nextID = 0;
			ps = conn.prepareStatement(Q_GET_NEXT_ID);
			rs = ps.executeQuery();
			if (rs.next()) {
				nextID = rs.getInt(1);
			}
			else {
				throw new SQLException("Failed to retrieve next sequential id");
			}
			rs.close();
			rs = null;
			ps.close();
			ps = null;

			System.out.println("... nextID=" + nextID);

			// 2 create date synonyms from the map list
			ps = conn.prepareStatement(Q_INSERT_DATE_SYNONYM);

			int count = 0;
			for (Iterator<GuidelineDateMap> iter = mapList.iterator(); iter.hasNext();) {
				GuidelineDateMap element = iter.next();
				System.out.println("... processing " + element);
				ps.setInt(1, nextID);
				ps.setString(2, element.getName());
				ps.setString(3, element.getDescription());
				DBUtil.setDateValue(ps, 4, element.getDate());
				ps.setBoolean(5, true);

				count = ps.executeUpdate();
				if (count < 1) throw new SQLException("No date synonym row was inserted");

				dsMap.put(element.getDate(), new Integer(nextID));
				++nextID;
				total1 += count;
			}
			ps.close();
			ps = null;

			// 3 insert grid dates
			ps = conn.prepareStatement(insertDSMapQuery);
			for (int i = 0; i < dateMaps.length; i++) {
				System.out.println("... migrating " + dateMaps[i]);
				ps.setInt(1, dateMaps[i].gridID);
				ps.setInt(2, (dateMaps[i].actDate == null ? -1 : (dsMap.containsKey(dateMaps[i].actDate)
						? dsMap.get(dateMaps[i].actDate).intValue()
						: -1)));
				ps.setInt(3, (dateMaps[i].expDate == null ? -1 : (dsMap.containsKey(dateMaps[i].expDate)
						? dsMap.get(dateMaps[i].expDate).intValue()
						: -1)));

				count = ps.executeUpdate();
				if (count < 1) throw new SQLException("No grid " + entityName + " row was inserted");

				total2 += count;
			}
			ps.close();
			ps = null;

			System.out.println("... setting seq. next id to = " + nextID);

			// 4 set the next id
			ps = conn.prepareStatement(Q_SET_NEXT_ID);
			ps.setInt(1, nextID);
			count = ps.executeUpdate();
			if (count < 1) throw new SQLException("Update next sequetial id failed (no row updated)");

			System.out.println("... committing...");
			conn.commit();

			System.out.println("<<< migrateDates: " + total1 + ", " + total2);
			return new int[] { total1, total2};
		}
		catch (SQLException ex) {
			conn.rollback();
			ex.printStackTrace();
			throw ex;
		}
		catch (Exception ex) {
			conn.rollback();
			ex.printStackTrace();
			throw new SQLException("Failed to migration " + entityName + " dates: " + ex.getMessage());
		}
		finally {
			if (rs != null) rs.close();
			if (ps != null) ps.close();
		}
	}

	/**
	 * 
	 * @param conn
	 * @param mapList
	 * @return count array; [0] = number of date synonyms created; [1] = number of grid updated
	 * @throws SQLException
	 */
	public synchronized int[] migrateGuidelineDates(Connection conn, List<GuidelineDateMap> mapList) throws SQLException {
		return migrateDates(conn, mapList, loadGridToUpdate(conn), Q_INSERT_GRID_DATE_SYNONYM, "guideline grid");
	}

	/**
	 * 
	 * @param conn
	 * @param mapList
	 * @return count array; [0] = number of date synonyms created; [1] = number of grid updated
	 * @throws SQLException
	 */
	public synchronized int[] migrateParameterDates(Connection conn, List<GuidelineDateMap> mapList) throws SQLException {
		return migrateDates(conn, mapList, loadParameterToUpdate(conn), Q_INSERT_PARAMETER_DATE_SYNONYM, "parameter grid");
	}

	public synchronized List<GuidelineDateMap> preprocessParameterDates(Connection conn) throws SQLException {
		// load parameter dates from DB
		List<GuidelineDateMap> list = new ArrayList<GuidelineDateMap>();
		List<Date> dateList = loadDatesToProcess(conn, Q_LOAD_PARAMETER_DATES);

		// set parent id's based on template namd and label
		for (int i = 0; i < dateList.size(); i++) {
			Date date = dateList.get(i);
			list.add(new GuidelineDateMap(date));
		}
		return list;
	}

	/**
	 * 
	 * @param conn
	 * @param query
	 * @return list of java.util.Date objects
	 * @throws SQLException
	 */
	private List<Date> loadDatesToProcess(Connection conn, String query) throws SQLException {
		List<Date> list = new ArrayList<Date>();

		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = conn.prepareStatement(query);
			rs = ps.executeQuery();

			while (rs.next()) {
				rs.getInt(1);
				Date actDate = DBUtil.getDateValue(rs, 2);
				Date expDate = DBUtil.getDateValue(rs, 3);
				if (actDate != null && !list.contains(actDate)) {
					list.add(actDate);
				}
				if (expDate != null && !list.contains(expDate)) {
					list.add(expDate);
				}
			}
			rs.close();
			rs = null;

			return list;
		}
		finally {
			if (rs != null) rs.close();
			if (ps != null) ps.close();
		}
	}

	public synchronized List<GuidelineDateMap> preprocessGuidelineDates(Connection conn) throws SQLException {
		// load guideline dates from DB
		List<GuidelineDateMap> list = new ArrayList<GuidelineDateMap>();
		List<Date> dateList = loadDatesToProcess(conn, Q_LOAD_GUIDELINE_DATES);

		// set parent id's based on template namd and label
		for (int i = 0; i < dateList.size(); i++) {
			Date date = dateList.get(i);
			list.add(new GuidelineDateMap(date));
		}
		return list;
	}

	/**
	 * Migrate template versions and returns the number of rows updated.
	 * @param conn
	 * @param mapList
	 * @return the number of templates updated
	 * @throws SQLException on DB error
	 */
	public synchronized int migrateTemplateVersions(Connection conn, List<TemplateVersionMap> mapList) throws SQLException {
		System.out.println(">>> migrateTemplateVersions: " + conn + ", list.siz=" + mapList.size());
		PreparedStatement psVersionUpdate = null;
		PreparedStatement psVersionInsert = null;
		PreparedStatement psParentUpdate = null;
		try {
			conn.setAutoCommit(false);

			psVersionUpdate = conn.prepareStatement(Q_SET_TEMPLATE_VERSION);
			psVersionInsert = conn.prepareStatement(Q_INSERT_TEMPLATE_VERSION);
			psParentUpdate = conn.prepareStatement(Q_SET_TEMPLATE_PARENT);

			int total = 0;
			int count = 0;
			for (Iterator<TemplateVersionMap> iter = mapList.iterator(); iter.hasNext();) {
				TemplateVersionMap element = iter.next();
				System.out.println("... processing: " + element);

				psVersionUpdate.setString(1, element.getVersion());
				psVersionUpdate.setInt(2, element.getID());
				count = psVersionUpdate.executeUpdate();
				if (count < 1) {
					// attempt to insert
					psVersionInsert.setInt(1, element.getID());
					psVersionInsert.setString(2, element.getVersion());
					count = psVersionInsert.executeUpdate();

					if (count < 1) throw new SQLException("No row was updated");
				}

				psParentUpdate.setInt(1, element.getParentID());
				psParentUpdate.setInt(2, element.getID());
				count = psParentUpdate.executeUpdate();
				if (count < 1) throw new SQLException("No row was updated");

				total += count;
			}

			System.out.println("... committing...");
			conn.commit();

			System.out.println("<<< migrateTemplateVersions: " + total);
			return total;
		}
		catch (SQLException ex) {
			conn.rollback();
			ex.printStackTrace();
			throw ex;
		}
		catch (Exception ex) {
			conn.rollback();
			ex.printStackTrace();
			throw new SQLException("Failed to migration templates: " + ex.getMessage());
		}
		finally {
			if (psVersionUpdate != null) psVersionUpdate.close();
			if (psParentUpdate != null) psParentUpdate.close();
		}
	}

	public synchronized List<GuidelineActionParameterRow> preprocessGuidelineActionParameters(Connection conn) throws SQLException {
		List<GuidelineActionParameterRow> list = new ArrayList<GuidelineActionParameterRow>();
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = conn.prepareStatement(Q_LOAD_GUIDELINE_ACTION_PARAMS);
			rs = ps.executeQuery();

			while (rs.next()) {
				int actionID = rs.getInt(1);
				int paramID = rs.getInt(2);
				String name = UtilBase.trim(rs.getString(3));
				String deployType = UtilBase.trim(rs.getString(4));
				list.add(new GuidelineActionParameterRow(actionID, paramID, name, deployType));
			}
			rs.close();
			rs = null;

			return list;
		}
		finally {
			if (rs != null) rs.close();
			if (ps != null) ps.close();
		}
	}

	public synchronized void migrateGuidelineActionParameters(Connection conn, List<GuidelineActionParameterRow> paramList) throws SQLException {
		System.out.println(">>> migrateGuidelineActionParameters: " + paramList.size());
		PreparedStatement ps = null;
		PreparedStatement psUpdate = null;
		try {
			int count = 0;
			conn.setAutoCommit(false);

			ps = conn.prepareStatement(Q_INSERT_GUIDELINE_FUNCTION_PARAM);
			psUpdate = conn.prepareStatement(Q_UPDATE_GUIDELINE_FUNCTION_PARAM);

			for (Iterator<GuidelineActionParameterRow> iter = paramList.iterator(); iter.hasNext();) {
				GuidelineActionParameterRow element = iter.next();
				System.out.println("... processing: " + element);

				ps.setInt(1, element.getActionID());
				ps.setInt(2, element.getParamID());
				ps.setString(3, element.getName());
				ps.setString(4, element.getDeployType());

				count = ps.executeUpdate();
				if (count < 1) {
					// attempt to update
					ps.setInt(1, element.getParamID());
					ps.setString(2, element.getName());
					ps.setString(3, element.getDeployType());
					ps.setInt(4, element.getActionID());
					count = psUpdate.executeUpdate();

					if (count < 1) throw new SQLException("No row was updated");
				}
			}

			System.out.println("... committing...");
			conn.commit();

			System.out.println("<<< migrateGuidelineActionParameters");
		}
		catch (SQLException ex) {
			conn.rollback();
			ex.printStackTrace();
			throw ex;
		}
		catch (Exception ex) {
			conn.rollback();
			ex.printStackTrace();
			throw new SQLException("Failed to migration guideline action parameters: " + ex.getMessage());
		}
		finally {
			if (ps != null) ps.close();
		}
	}

	public synchronized List<TemplateVersionMap> preprocessTemplates(Connection conn) throws SQLException {
		// load template mapping data from DB
		List<TemplateVersionMap> list = loadTemplatesToProcess(conn);

		// set parent id's based on template namd and label
		for (int i = 0; i < list.size(); i++) {
			TemplateVersionMap map = list.get(i);
			try {
				map.setParentID(guessParentID(list, map));
			}
			catch (ParseException e) {
				e.printStackTrace();
			}
		}
		return list;
	}

	private int guessParentID(List<TemplateVersionMap> list, TemplateVersionMap source) throws ParseException {
		int parentID = -1;
		for (int i = 0; i < list.size(); i++) {
			TemplateVersionMap map = list.get(i);
			if (map.getID() != source.getID() && map.getUsage().equals(source.getUsage()) && map.getName().equals(source.getName())) {
				// compare id to determine which one is the parent (get the largest if more than one is found)
				if (map.getID() < source.getID()) {
					parentID = map.getID();
				}
			}
		}
		return parentID;
	}

	private List<TemplateVersionMap> loadTemplatesToProcess(Connection conn) throws SQLException {
		List<TemplateVersionMap> list = new ArrayList<TemplateVersionMap>();

		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = conn.prepareStatement(Q_LOAD_TEMPLATE);
			rs = ps.executeQuery();

			while (rs.next()) {
				int id = rs.getInt(1);
				String name = UtilBase.trim(rs.getString(2));
				String usage = UtilBase.trim(rs.getString(3));
				String labelName = UtilBase.trim(rs.getString(4));
				Date actDate = DBUtil.getDateValue(rs, 5);
				Date expDate = DBUtil.getDateValue(rs, 6);
				list.add(new TemplateVersionMap(id, name, toLabelString(labelName, actDate, expDate), usage));
			}
			rs.close();
			rs = null;

			return list;
		}
		finally {
			if (rs != null) rs.close();
			if (ps != null) ps.close();
		}
	}
}