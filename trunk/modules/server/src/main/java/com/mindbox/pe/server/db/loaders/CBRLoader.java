package com.mindbox.pe.server.db.loaders;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import com.mindbox.pe.common.UtilBase;
import com.mindbox.pe.model.cbr.CBRAttribute;
import com.mindbox.pe.model.cbr.CBRAttributeType;
import com.mindbox.pe.model.cbr.CBRAttributeValue;
import com.mindbox.pe.model.cbr.CBRCase;
import com.mindbox.pe.model.cbr.CBRCaseAction;
import com.mindbox.pe.model.cbr.CBRCaseBase;
import com.mindbox.pe.model.cbr.CBRCaseClass;
import com.mindbox.pe.model.cbr.CBREnumeratedValue;
import com.mindbox.pe.model.cbr.CBRScoringFunction;
import com.mindbox.pe.model.cbr.CBRValueRange;
import com.mindbox.pe.server.cache.CBRManager;
import com.mindbox.pe.server.cache.DateSynonymManager;
import com.mindbox.pe.server.db.DBConnectionManager;
import com.mindbox.pe.xsd.config.KnowledgeBaseFilter;

public class CBRLoader extends AbstractLoader {

	private static final String Q_LOAD_CBR_CASE_BASES = "select case_base_id, display_name, case_class_id, index_file, scoring_function_id, naming_attribute, match_threshold,"
			+ " maximum_matches, notes from MB_CBR_CASE_BASE ORDER BY case_base_id";
	private static final String Q_LOAD_CBR_CASE_BASE_DATE_SYNONYM_MAPPING = "select case_base_id, effective_synonym_id,expiration_synonym_id from MB_CBR_CASE_BASE_DATE_SYN where case_base_id = ?";
	private static final String Q_LOAD_CBR_ATTRIBUTES = "select attribute_id, case_base_id, display_name, attribute_type_id, match_contribution, mismatch_penalty,"
			+ " absence_penalty, lowest_value, highest_value, match_interval, value_range_id, notes from MB_CBR_ATTRIBUTE ORDER BY attribute_id";
	private static final String Q_LOAD_CBR_VALUE_RANGES = "select value_range_id, display_name, symbol, description, enumerated_values_allowed, anything_allowed, numeric_allowed, float_allowed, negative_allowed from MB_CBR_VALUE_RANGE ORDER BY value_range_id";
	private static final String Q_LOAD_CBR_CASE_CLASSES = "select case_class_id, display_name, symbol from MB_CBR_CASE_CLASS ORDER BY case_class_id";
	private static final String Q_LOAD_CBR_CASE_ACTIONS = "select case_action_id, display_name, symbol from MB_CBR_CASE_ACTION ORDER BY case_action_id";
	private static final String Q_LOAD_CBR_SCORING_FUNCTIONS = "select scoring_function_id, display_name, symbol from MB_CBR_SCORING_FUNCTION ORDER BY scoring_function_id";
	private static final String Q_LOAD_CBR_ATTRIBUTE_TYPES = "select attribute_type_id, display_name, symbol,  default_match_contribution, default_mismatch_penalty, "
			+ "default_absence_penalty, default_value_range_id, ask_for_match_interval, ask_for_numeric_range " + "from MB_CBR_ATTRIBUTE_TYPE ORDER BY attribute_type_id";
	private static final String Q_LOAD_CBR_ENUMERATED_VALUES = "select value_string from MB_CBR_ENUMERATED_VALUE where attribute_id = ?";
	private static final String Q_LOAD_CBR_CASES = "select case_id, case_base_id, display_name, notes from MB_CBR_CASE ORDER BY case_id";
	private static final String Q_LOAD_CBR_ATTRIBUTE_VALUES = "select attribute_value_id, case_id, attribute_id, display_name, match_contribution, mismatch_penalty, "
			+ "notes from MB_CBR_ATTRIBUTE_VALUE where case_id = ? order by attribute_value_id";
	private static final String Q_LOAD_CBR_CASE_ACTIONS_MAPPING = "select case_id, action_id, action_order from MB_CBR_CASE_ACTIONS_MAPPING where case_id = ? order by action_order";
	private static final String Q_LOAD_CBR_CASE_DATE_SYNONYM_MAPPING = "select effective_synonym_id,expiration_synonym_id from MB_CBR_CASE_DATE_SYNONYM where case_id = ?";

	public CBRLoader() {
	}

	public void load(final KnowledgeBaseFilter knowledgeBaseFilterConfig) throws SQLException {
		// TODO GKim: Implement filtering

		CBRManager.getInstance().startDbLoading();
		try {
			dbLoadCBRScoringFunctions();
			dbLoadCBRCaseClasses();
			dbLoadCBRCaseActions();
			dbLoadCBRValueRanges();
			dbLoadCBRAttributeTypes();
			dbLoadCBRCaseBases();
			dbLoadCBRAttributes();
			dbLoadCBRCases();
		}
		finally {
			CBRManager.getInstance().finishLoading();
		}
	}

	private void dbLoadCBRValueRanges() throws SQLException {
		DBConnectionManager dbconnectionmanager = DBConnectionManager.getInstance();
		Connection connection = dbconnectionmanager.getConnection();
		CBRManager cbrmanager = CBRManager.getInstance();
		assert (cbrmanager != null) : " CBRManager instance is null";

		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = connection.prepareStatement(Q_LOAD_CBR_VALUE_RANGES);
			rs = ps.executeQuery();
			logger.info("===== Loading ValueRanges ======");
			int valueRangeID;
			String displayName;
			String symbol;
			String description;
			int enumeratedValuesAllowed;
			int anythingAllowed;
			int numericAllowed;
			int floatAllowed;
			int negativeAllowed;
			while (rs.next()) {
				valueRangeID = rs.getInt(1);
				displayName = UtilBase.trim(rs.getString(2));
				symbol = UtilBase.trim(rs.getString(3));
				description = UtilBase.trim(rs.getString(4));
				enumeratedValuesAllowed = rs.getInt(5);
				anythingAllowed = rs.getInt(6);
				numericAllowed = rs.getInt(7);
				floatAllowed = rs.getInt(8);
				negativeAllowed = rs.getInt(9);
				CBRValueRange cbrValueRange = new CBRValueRange(
						valueRangeID,
						symbol,
						displayName,
						description,
						enumeratedValuesAllowed != 0,
						anythingAllowed != 0,
						numericAllowed != 0,
						floatAllowed != 0,
						negativeAllowed != 0);


				logger.info("ValueRange: " + displayName);
				cbrmanager.addCBRValueRange(cbrValueRange);

			}
		}
		finally {
			if (rs != null) rs.close();
			if (ps != null) ps.close();
			dbconnectionmanager.freeConnection(connection);
		}
	}

	private void dbLoadCBRCaseClasses() throws SQLException {
		DBConnectionManager dbconnectionmanager = DBConnectionManager.getInstance();
		Connection connection = dbconnectionmanager.getConnection();
		CBRManager cbrmanager = CBRManager.getInstance();
		assert (cbrmanager != null) : " CBRManager instance is null";

		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = connection.prepareStatement(Q_LOAD_CBR_CASE_CLASSES);
			rs = ps.executeQuery();
			logger.info("===== Loading CaseClasses ======");
			int caseClassID;
			String displayName;
			String symbol;
			while (rs.next()) {
				caseClassID = rs.getInt(1);
				displayName = UtilBase.trim(rs.getString(2));
				symbol = UtilBase.trim(rs.getString(3));
				CBRCaseClass cbrCaseClass = new CBRCaseClass(caseClassID, symbol, displayName);


				logger.info("CaseClass" + displayName);
				cbrmanager.addCBRCaseClass(cbrCaseClass);

			}
		}
		finally {
			if (rs != null) rs.close();
			if (ps != null) ps.close();
			dbconnectionmanager.freeConnection(connection);
		}
	}

	private void dbLoadCBRCaseActions() throws SQLException {
		DBConnectionManager dbconnectionmanager = DBConnectionManager.getInstance();
		Connection connection = dbconnectionmanager.getConnection();
		CBRManager cbrmanager = CBRManager.getInstance();
		assert (cbrmanager != null) : " CBRManager instance is null";

		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = connection.prepareStatement(Q_LOAD_CBR_CASE_ACTIONS);
			rs = ps.executeQuery();
			logger.info("===== Loading CaseActions ======");
			int caseActionID;
			String displayName;
			String symbol;
			while (rs.next()) {
				caseActionID = rs.getInt(1);
				displayName = UtilBase.trim(rs.getString(2));
				symbol = UtilBase.trim(rs.getString(3));
				CBRCaseAction cbrCaseAction = new CBRCaseAction(caseActionID, symbol, displayName);


				logger.info("CaseAction" + displayName);
				cbrmanager.addCBRCaseAction(cbrCaseAction);

			}
		}
		finally {
			if (rs != null) rs.close();
			if (ps != null) ps.close();
			dbconnectionmanager.freeConnection(connection);
		}
	}

	private void dbLoadCBRScoringFunctions() throws SQLException {
		DBConnectionManager dbconnectionmanager = DBConnectionManager.getInstance();
		Connection connection = dbconnectionmanager.getConnection();
		CBRManager cbrmanager = CBRManager.getInstance();
		assert (cbrmanager != null) : " CBRManager instance is null";

		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = connection.prepareStatement(Q_LOAD_CBR_SCORING_FUNCTIONS);
			rs = ps.executeQuery();
			logger.info("===== Loading CaseActions ======");
			int scoringFunctionID;
			String displayName;
			String symbol;
			while (rs.next()) {
				scoringFunctionID = rs.getInt(1);
				displayName = UtilBase.trim(rs.getString(2));
				symbol = UtilBase.trim(rs.getString(3));
				CBRScoringFunction cbrScoringFunction = new CBRScoringFunction(scoringFunctionID, symbol, displayName);


				logger.info("ScoringFunction" + displayName);
				cbrmanager.addCBRScoringFunction(cbrScoringFunction);

			}
		}
		finally {
			if (rs != null) rs.close();
			if (ps != null) ps.close();
			dbconnectionmanager.freeConnection(connection);
		}
	}

	private void dbLoadCBRAttributeTypes() throws SQLException {
		DBConnectionManager dbconnectionmanager = DBConnectionManager.getInstance();
		Connection connection = dbconnectionmanager.getConnection();
		CBRManager cbrmanager = CBRManager.getInstance();
		assert (cbrmanager != null) : " CBRManager instance is null";

		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = connection.prepareStatement(Q_LOAD_CBR_ATTRIBUTE_TYPES);
			rs = ps.executeQuery();
			logger.info("===== Loading AttributeTypes ======");
			int id;
			String displayName;
			String symbol;
			int dmc, dmp, dap, dvr, afmi, afnr;
			while (rs.next()) {
				id = rs.getInt(1);
				displayName = UtilBase.trim(rs.getString(2));
				symbol = UtilBase.trim(rs.getString(3));
				dmc = rs.getInt(4);
				dmp = rs.getInt(5);
				dap = rs.getInt(6);
				dvr = rs.getInt(7);
				afmi = rs.getInt(8);
				afnr = rs.getInt(9);

				CBRAttributeType cbrAttributeType = new CBRAttributeType(id, symbol, displayName);
				cbrAttributeType.setDefaultMatchContribution(dmc);
				cbrAttributeType.setDefaultMismatchPenalty(dmp);
				cbrAttributeType.setDefaultAbsencePenalty(dap);
				cbrAttributeType.setDefaultValueRange(dvr > 0 ? cbrmanager.getCBRValueRange(dvr).getName() : null);
				cbrAttributeType.setAskForMatchInterval(new Boolean(afmi != 0));
				cbrAttributeType.setAskForNumericRange(new Boolean(afnr != 0));

				logger.info("AttributeType" + displayName);
				cbrmanager.addCBRAttributeType(cbrAttributeType);

			}
		}
		finally {
			if (rs != null) rs.close();
			if (ps != null) ps.close();
			dbconnectionmanager.freeConnection(connection);
		}
	}

	private void dbLoadCBRAttributes() throws SQLException {
		DBConnectionManager dbconnectionmanager = DBConnectionManager.getInstance();
		Connection connection = dbconnectionmanager.getConnection();
		CBRManager cbrmanager = CBRManager.getInstance();
		assert (cbrmanager != null) : " CBRManager instance is null";

		PreparedStatement ps = null;
		ResultSet rs = null;
		PreparedStatement ps2 = null;
		ResultSet rs2 = null;
		try {
			ps = connection.prepareStatement(Q_LOAD_CBR_ATTRIBUTES);
			rs = ps.executeQuery();
			ps2 = connection.prepareStatement(Q_LOAD_CBR_ENUMERATED_VALUES);
			logger.info("===== Loading Attributes ======");
			int attributeID;
			int caseBaseID;
			String displayName;
			int attributeTypeID;
			int matchContribution;
			int mismatchPenalty;
			int absencePenalty;
			double lowestValue;
			double highestValue;
			double matchInterval;
			int valueRangeID;
			String notes;
			while (rs.next()) {
				attributeID = rs.getInt(1);
				caseBaseID = rs.getInt(2);
				displayName = UtilBase.trim(rs.getString(3));
				attributeTypeID = rs.getInt(4);
				matchContribution = rs.getInt(5);
				mismatchPenalty = rs.getInt(6);
				absencePenalty = rs.getInt(7);
				lowestValue = rs.getDouble(8);
				highestValue = rs.getDouble(9);
				matchInterval = rs.getDouble(10);
				valueRangeID = rs.getInt(11);
				notes = UtilBase.trim(rs.getString(12));

				CBRAttribute cbrAttribute = new CBRAttribute(attributeID, displayName, notes);
				cbrAttribute.setAttributeType(attributeTypeID > 0 ? cbrmanager.getCBRAttributeType(attributeTypeID) : null);
				cbrAttribute.setValueRange(valueRangeID > 0 ? cbrmanager.getCBRValueRange(valueRangeID) : null);
				cbrAttribute.setCaseBase(cbrmanager.getCBRCaseBase(caseBaseID));
				cbrAttribute.setMatchContribution(matchContribution);
				cbrAttribute.setMismatchPenalty(mismatchPenalty);
				cbrAttribute.setAbsencePenalty(absencePenalty);
				cbrAttribute.setLowestValue(lowestValue);
				cbrAttribute.setHighestValue(highestValue);
				cbrAttribute.setMatchInterval(matchInterval);

				ps2.setInt(1, attributeID);
				rs2 = ps2.executeQuery();
				List<CBREnumeratedValue> ev = cbrAttribute.getEnumeratedValues();
				while (rs2.next())
					ev.add(new CBREnumeratedValue(UtilBase.trim(rs2.getString(1))));

				logger.info("attribute: " + displayName);
				cbrmanager.addCBRAttribute(cbrAttribute);

			}
		}
		finally {
			if (rs != null) rs.close();
			if (ps != null) ps.close();
			if (rs2 != null) rs2.close();
			if (ps2 != null) ps2.close();
			dbconnectionmanager.freeConnection(connection);
		}
	}

	/**
	 * Loads CBR Case Bases
	 * @throws SQLException
	 */
	private void dbLoadCBRCaseBases() throws SQLException {
		DBConnectionManager dbconnectionmanager = DBConnectionManager.getInstance();
		Connection connection = dbconnectionmanager.getConnection();
		CBRManager cbrmanager = CBRManager.getInstance();
		assert (cbrmanager != null) : " CBRManager instance is null";

		PreparedStatement ps = null;
		ResultSet rs = null;
		PreparedStatement ps2 = null;
		ResultSet rs2 = null;
		try {
			ps = connection.prepareStatement(Q_LOAD_CBR_CASE_BASES);
			ps2 = connection.prepareStatement(Q_LOAD_CBR_CASE_BASE_DATE_SYNONYM_MAPPING);
			rs = ps.executeQuery();
			logger.info("===== Loading CBR Case Bases ======");
			int caseBaseID;
			String displayName;
			int caseClassId;
			String indexFile;
			int scoringFunctionId;
			String namingAttribute;
			int matchThreshold;
			int maximumMatches;
			String notes;
			while (rs.next()) {
				caseBaseID = rs.getInt(1);
				displayName = UtilBase.trim(rs.getString(2));
				caseClassId = rs.getInt(3);
				indexFile = rs.getString(4);
				scoringFunctionId = rs.getInt(5);
				namingAttribute = UtilBase.trim(rs.getString(6));
				matchThreshold = rs.getInt(7);
				maximumMatches = rs.getInt(8);
				notes = UtilBase.trim(rs.getString(9));

				CBRCaseBase cbrCaseBase = new CBRCaseBase(caseBaseID, displayName, notes);
				cbrCaseBase.setCaseClass(caseClassId > 0 ? cbrmanager.getCBRCaseClass(caseClassId) : null);
				cbrCaseBase.setIndexFile(indexFile);
				cbrCaseBase.setScoringFunction(scoringFunctionId > 0 ? cbrmanager.getCBRScoringFunction(scoringFunctionId) : null);
				cbrCaseBase.setNamingAttribute(namingAttribute);
				cbrCaseBase.setMatchThreshold(matchThreshold);
				cbrCaseBase.setMaximumMatches(maximumMatches);
				ps2.setInt(1, caseBaseID);
				rs2 = ps2.executeQuery();
				if (rs2.next()) {
					int effID = rs2.getInt(2);
					int expID = rs2.getInt(3);
					cbrCaseBase.setEffectiveDate(DateSynonymManager.getInstance().getDateSynonym(effID));
					cbrCaseBase.setExpirationDate(DateSynonymManager.getInstance().getDateSynonym(expID));
				}

				logger.info("case base: " + displayName);
				cbrmanager.addCBRCaseBase(cbrCaseBase);
			}
		}
		finally {
			if (rs != null) rs.close();
			if (ps != null) ps.close();
			if (rs2 != null) rs2.close();
			if (ps2 != null) ps2.close();
			dbconnectionmanager.freeConnection(connection);
		}
	}

	/**
	 * Loads CBR Cases from db.
	 * @throws SQLException
	 */
	private void dbLoadCBRCases() throws SQLException {
		DBConnectionManager dbconnectionmanager = DBConnectionManager.getInstance();
		Connection connection = dbconnectionmanager.getConnection();
		CBRManager cbrmanager = CBRManager.getInstance();
		assert (cbrmanager != null) : " CBRManager instance is null";

		PreparedStatement ps = null;
		ResultSet rs = null;
		PreparedStatement ps2 = null;
		ResultSet rs2 = null;
		PreparedStatement ps3 = null;
		ResultSet rs3 = null;
		PreparedStatement ps4 = null;
		ResultSet rs4 = null;
		try {
			ps = connection.prepareStatement(Q_LOAD_CBR_CASES);
			ps2 = connection.prepareStatement(Q_LOAD_CBR_ATTRIBUTE_VALUES);
			ps3 = connection.prepareStatement(Q_LOAD_CBR_CASE_ACTIONS_MAPPING);
			ps4 = connection.prepareStatement(Q_LOAD_CBR_CASE_DATE_SYNONYM_MAPPING);
			rs = ps.executeQuery();
			logger.info("===== Loading CBR Cases ======");
			int caseID;
			int caseBaseID;
			String displayName;
			String notes;
			while (rs.next()) {
				caseID = rs.getInt(1);
				caseBaseID = rs.getInt(2);
				displayName = UtilBase.trim(rs.getString(3));
				notes = UtilBase.trim(rs.getString(4));

				CBRCase cbrCase = new CBRCase(caseID, displayName, notes);
				cbrCase.setCaseBase(cbrmanager.getCBRCaseBase(caseBaseID));

				ps2.setInt(1, caseID);
				rs2 = ps2.executeQuery();
				List<CBRAttributeValue> attrValues = cbrCase.getAttributeValues();
				while (rs2.next()) {
					CBRAttributeValue newVal = new CBRAttributeValue(rs2.getInt(1), UtilBase.trim(rs2.getString(4)), UtilBase.trim(rs2.getString(7)));
					newVal.setAttribute(cbrmanager.getCBRAttribute(rs2.getInt(3)));
					newVal.setMatchContribution(rs2.getInt(5));
					newVal.setMismatchPenalty(rs2.getInt(6));
					attrValues.add(newVal);
				}

				ps3.setInt(1, caseID);
				rs3 = ps3.executeQuery();
				List<CBRCaseAction> actions = cbrCase.getCaseActions();
				while (rs3.next()) {
					actions.add(CBRManager.getInstance().getCBRCaseAction(rs3.getInt(2)));
				}
				logger.debug("Added " + actions.size() + " case actions to " + caseID);

				ps4.setInt(1, caseID);
				rs4 = ps4.executeQuery();
				if (rs4.next()) {
					int effID = rs4.getInt(1);
					int expID = rs4.getInt(2);
					cbrCase.setEffectiveDate(DateSynonymManager.getInstance().getDateSynonym(effID));
					cbrCase.setExpirationDate(DateSynonymManager.getInstance().getDateSynonym(expID));
				}
				else {
					cbrCase.setEffectiveDate(null);
					cbrCase.setExpirationDate(null);
				}

				logger.info("case: " + displayName);
				cbrmanager.addCBRCase(cbrCase);

			}
		}
		finally {
			if (rs != null) rs.close();
			if (ps != null) ps.close();
			if (rs2 != null) rs2.close();
			if (ps2 != null) ps2.close();
			if (rs3 != null) rs3.close();
			if (ps3 != null) ps3.close();
			if (rs4 != null) rs4.close();
			if (ps4 != null) ps4.close();
			dbconnectionmanager.freeConnection(connection);
		}
	}
}