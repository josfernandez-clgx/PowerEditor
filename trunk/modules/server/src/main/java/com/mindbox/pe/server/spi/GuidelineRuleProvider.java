/*
 * Created on 2005. 7. 1.
 *
 */
package com.mindbox.pe.server.spi;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;


/**
 * @author Geneho Kim
 * @since PowerEditor 4.3.3
 */
public interface GuidelineRuleProvider {

	/**
	 * Retrieves all guideline rules as a list of {@link GuidelineRuleInfo}.
	 * @return list of all guideline rules
	 * @throws SQLException on DB error
	 */
	List<GuidelineRuleInfo> fetchAllGuidelineRules() throws SQLException;
	
	/**
	 * Inserts the guideline rule for the specified template and column in a new DB transaction.
	 * @param templateID
	 * @param columnNo
	 * @param deploymentRule
	 * @throws SQLException on DB error
	 */
	void insertGuidelineRule(int templateID, int columnNo, String deploymentRule) throws SQLException;
	
	/**
	 * Inserts the guideline rule for the specified template and column using the specified DB connection.
	 * This is just like {@link #insertGuidelineRule(int, int, String)} except that this does not make any
	 * connection management calls, such as commit or close.
	 * @param templateID
	 * @param columnNo
	 * @param deploymentRule
	 * @throws SQLException
	 */
	void insertGuidelineRule(Connection conn, int templateID, int columnNo, String deploymentRule) throws SQLException;

	/**
	 * Updates the guideline rule for the specified template and column in a new DB transaction.
	 * @param templateID
	 * @param columnNo
	 * @param deploymentRule
	 * @throws SQLException
	 */
	void updateGuidelineRule(int templateID, int columnNo, String deploymentRule) throws SQLException;
	
	/**
	 * Updates the guideline rule for the specified template and column using the specified DB connection.
	 * This is just like {@link #updateGuidelineRule(int, int, String)} except that this does not make any
	 * connection management calls, such as commit or close.
	 * @param templateID
	 * @param columnNo
	 * @param deploymentRule
	 * @throws SQLException
	 */
	void updateGuidelineRule(Connection conn, int templateID, int columnNo, String deploymentRule) throws SQLException;
}
