package com.mindbox.pe.server.db.updaters;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.mindbox.pe.common.UtilBase;
import com.mindbox.pe.model.ColumnAttributeItemDigest;
import com.mindbox.pe.model.ColumnDataSpecDigest;
import com.mindbox.pe.model.ColumnMessageFragmentDigest;
import com.mindbox.pe.model.GridTemplate;
import com.mindbox.pe.model.GridTemplateColumn;
import com.mindbox.pe.model.TemplateMessageDigest;
import com.mindbox.pe.model.ColumnDataSpecDigest.EnumSourceType;
import com.mindbox.pe.model.rule.RuleDefinition;
import com.mindbox.pe.server.RuleDefinitionUtil;
import com.mindbox.pe.server.Util;
import com.mindbox.pe.server.db.loaders.GuidelineTemplateLoader;
import com.mindbox.pe.server.spi.ServiceProviderFactory;

/**
 * Guideline Template updater.
 * None of the methods in this performs connection management functions.
 * @author kim
 * @author MindBox
 * @since PowerEditor 4.0.0
 */
public class GuidelineTemplateUpdater extends AbstractUpdater {

	private static final String Q_INSERT_TEMPLATE_COLUMN_PROP = "insert into MB_TEMPLATE_COLUMN_PROP"
			+ " (template_id,column_no,property_name,property_value) values (?,?,?,?)";

	private static final String Q_DELETE_TEMPLATE_COLUMN_PROP = "delete from MB_TEMPLATE_COLUMN_PROP"
			+ " where template_id=? and column_no=?";

	private static final String Q_INSERT_TEMPLATE = "insert into MB_TEMPLATE"
			+ " (template_id,name,usage_type,status,max_row,parent_id,comp_cols,consist_cols,fit_screen,description,comments)"
			+ " values (?,?,?,?,?,?,?,?,?,?,?)";

	private static final String Q_UPDATE_TEMPLATE = "update MB_TEMPLATE"
			+ " set name=?,usage_type=?,status=?,max_row=?,parent_id=?,comp_cols=?,consist_cols=?,fit_screen=?,description=?,comments=?"
			+ " where template_id=?";

	private static final String Q_DELETE_TEMPLATE = "delete from MB_TEMPLATE where template_id=?";

	private static final String Q_INSERT_TEMPLATE_COLUMN = "insert into MB_TEMPLATE_COLUMN"
			+ " (template_id,column_no,column_name,description,attribute_map,title,font,color,width,data_type,"
			+ " multi_select,allow_blank,sort_enum,show_lhs_attr,min_value,max_value,precision_val)"
			+ " values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

	private static final String Q_UPDATE_TEMPLATE_COLUMN = "update MB_TEMPLATE_COLUMN"
			+ " set column_name=?,description=?,attribute_map=?,title=?,font=?,color=?,width=?,data_type=?,"
			+ " multi_select=?,allow_blank=?,sort_enum=?,show_lhs_attr=?,min_value=?,max_value=?,precision_val=?"
			+ " where template_id=? and column_no=?";

	private static final String Q_DELETE_TEMPLATE_ALL_COLUMNS = "delete from MB_TEMPLATE_COLUMN where template_id=?";

	private static final String Q_DELETE_TEMPLATE_COLUMN = "delete from MB_TEMPLATE_COLUMN where template_id=? and column_no=?";

	private static final String Q_INSERT_TEMPLATE_COLUMN_ENUM = "insert into MB_TEMPLATE_COLUMN_ENUM"
			+ " (template_id,column_no,enum_value) values (?,?,?)";

	private static final String Q_DELETE_TEMPLATE_COLUMN_ENUMS = "delete from MB_TEMPLATE_COLUMN_ENUM where template_id=? and column_no=?";

	private static final String Q_DELETE_TEMPLATE_ALL_COLUMN_ENUMS = "delete from MB_TEMPLATE_COLUMN_ENUM where template_id=?";

	private static final String Q_INSERT_TEMPLATE_COLUMN_ATTR_ITEM = "insert into MB_TEMPLATE_COLUMN_ATTR_ITEM"
			+ " (template_id,column_no,name,display_value) values (?,?,?,?)";

	private static final String Q_DELETE_TEMPLATE_COLUMN_ATTR_ITEMS = "delete from MB_TEMPLATE_COLUMN_ATTR_ITEM where template_id=? and column_no=?";

	private static final String Q_DELETE_TEMPLATE_ALL_COLUMN_ATTR_ITEMS = "delete from MB_TEMPLATE_COLUMN_ATTR_ITEM where template_id=?";

	private static final String Q_INSERT_TEMPLATE_COLUMN_MESSAGE_FRAGMENT = "insert into MB_TEMPLATE_MESSAGE_FRAGMENT"
			+ " (template_id,column_no,message_text,type,cell_selection,enum_delim,enum_final_delim,enum_prefix,range_style)"
			+ " values (?,?,?,?,?,?,?,?,?)";

	private static final String Q_DELETE_TEMPLATE_COLUMN_MESSAGE_FRAGMENTS = "delete from MB_TEMPLATE_MESSAGE_FRAGMENT where template_id=? and column_no=?";

	private static final String Q_DELETE_TEMPLATE_ALL_COLUMN_MESSAGE_FRAGMENTS = "delete from MB_TEMPLATE_MESSAGE_FRAGMENT where template_id=?";

	private static final String Q_INSERT_TEMPLATE_MESSAGE = "insert into MB_TEMPLATE_MESSAGE"
			+ " (template_id,column_no,entity_id,cond_delim,cond_final_delim,message_text)" + " values (?,?,?,?,?,?)";

	private static final String Q_DELETE_TEMPLATE_MESSAGES = "delete from MB_TEMPLATE_MESSAGE where template_id=? and column_no=?";

	private static final String Q_DELETE_TEMPLATE_ALL_MESSAGES = "delete from MB_TEMPLATE_MESSAGE where template_id=?";

	private static final String Q_DELETE_TEMPLATE_ALL_COLUMN_MESSAGES = "delete from MB_TEMPLATE_MESSAGE where template_id=? and column_no <> -1";

	private static final String Q_DELETE_TEMPLATE_COLUMN_MESSAGE = "delete from MB_TEMPLATE_MESSAGE where template_id=? and column_no=?";

	private static final String Q_DELETE_TEMPLATE_ALL_DEPLOY_RULES = "delete from MB_TEMPLATE_DEPLOY_RULE where template_id=?";

	private static final String Q_DELETE_TEMPLATE_ALL_COLUMN_DEPLOY_RULES = "delete from MB_TEMPLATE_DEPLOY_RULE where template_id=? and column_no <> -1";

	private static final String Q_DELETE_TEMPLATE_COLUMN_DEPLOY_RULE = "delete from MB_TEMPLATE_DEPLOY_RULE where template_id=? and column_no=?";

	private static final String Q_INSERT_TEMPLATE_VERSION = "insert into MB_TEMPLATE_VERSION" + " (template_id,version) values (?,?)";

	private static final String Q_UPDATE_TEMPLATE_VERSION = "update MB_TEMPLATE_VERSION set version=? where template_id=?";

	private static final String Q_DELETE_TEMPLATE_VERSION = "delete from MB_TEMPLATE_VERSION where template_id=?";

	/**
	 * 
	 */
	public GuidelineTemplateUpdater() {
	}

	public void insertTemplate(Connection conn, GridTemplate template) throws SQLException {
		logger.debug(">>> insertTemplate: " + conn + "," + template);
		PreparedStatement ps = null;
		try {
			// insert template details
			ps = conn.prepareStatement(Q_INSERT_TEMPLATE);
			ps.setInt(1, template.getID());
			ps.setString(2, template.getName());
			ps.setString(3, template.getUsageType().toString());
			ps.setString(4, template.getStatus());
			ps.setInt(5, template.getMaxNumOfRows());
			ps.setInt(6, template.getParentTemplateID());
			ps.setString(7, Util.toString(template.getCompletenessColumns()));
			ps.setString(8, Util.toString(template.getConsistencyColumns()));
			ps.setBoolean(9, template.fitToScreen());
			ps.setString(10, (template.getDescription() == null ? " " : template.getDescription()));
			ps.setString(11, (template.getComment() == null ? " " : template.getComment()));

			int count = ps.executeUpdate();
			if (count < 1) {
				throw new SQLException("Failed to insert a row into the template table");
			}
			ps.close();
			ps = null;

			logger.debug("    insertTemplate: inserting template version");
			ps = conn.prepareStatement(Q_INSERT_TEMPLATE_VERSION);
			ps.setInt(1, template.getID());
			ps.setString(2, template.getVersion());
			count = ps.executeUpdate();
			if (count < 1) {
				throw new SQLException("Failed to insert the template-version row");
			}
			ps.close();
			ps = null;

			logger.debug("    insertTemplate: inserting template rule");
			insertTemplateDeploymentRule(conn, template.getID(), -1, template.getRuleDefinition());

			if (template.hasMessages()) {
				setTemplateMessages(conn, template.getID(), -1, template.getAllMessageDigest(), false);
			}

			// insert column details
			logger.debug("    insertTemplate: template details inserted: inserting columns...");

			for (Iterator<GridTemplateColumn> iter = template.getColumns().iterator(); iter.hasNext();) {
				GridTemplateColumn element = iter.next();
				insertColumn(conn, template.getID(), element);
			}
			logger.debug("<<< insertTemplate");
		}
		catch (SQLException ex) {
			throw ex;
		}
		catch (Exception ex) {
			logger.error("Error while inserting " + template, ex);
			throw new SQLException(ex.getMessage());
		}
		finally {
			if (ps != null) ps.close();
		}
	}

	private void insertTemplateDeploymentRule(Connection conn, int templateID, int columnNo, RuleDefinition ruleDef) throws SQLException {
		String ruleStr = RuleDefinitionUtil.toString(ruleDef);
		ServiceProviderFactory.getGuidelineRuleProvider().insertGuidelineRule(conn, templateID, columnNo, ruleStr);
	}

	private void updateTemplateDeploymentRule(Connection conn, int templateID, int columnNo, RuleDefinition ruleDef) throws SQLException {
		logger.debug(">>> updateTemplateDeploymentRule: " + templateID + "," + columnNo + " - " + conn);
		logger.debug("  rule = " + (ruleDef == null ? "" : ruleDef.toDebugString()));
		String ruleStr = RuleDefinitionUtil.toString(ruleDef);
		logger.debug(">>> updateTemplateDeploymentRule: rule=" + ruleStr);

		ServiceProviderFactory.getGuidelineRuleProvider().updateGuidelineRule(conn, templateID, columnNo, ruleStr);
	}

	private void setTemplateMessages(Connection conn, int templateID, int columnNo, List<TemplateMessageDigest> messageDigestList,
			boolean deleteFirst) throws SQLException {
		logger.debug(">>> setTemplateMessageMap: " + templateID + "," + columnNo + "," + messageDigestList.size() + "," + deleteFirst);
		PreparedStatement ps = null;
		try {
			int count = 0;
			if (deleteFirst) {
				ps = conn.prepareStatement(Q_DELETE_TEMPLATE_MESSAGES);
				ps.setInt(1, templateID);
				ps.setInt(2, columnNo);
				count = ps.executeUpdate();
				logger.debug("    setTemplateMessageMap: removed " + count + " message rows");
				ps.close();
				ps = null;
			}
			if (!messageDigestList.isEmpty()) {
				ps = conn.prepareStatement(Q_INSERT_TEMPLATE_MESSAGE);

				for (Iterator<TemplateMessageDigest> iter = messageDigestList.iterator(); iter.hasNext();) {
					TemplateMessageDigest element = iter.next();
					String conditionalDelimiter = element.getConditionalDelimiter();
					String conditionalFinalDelimiter = element.getConditionalFinalDelimiter();
					if (conditionalDelimiter != null && conditionalDelimiter.indexOf(" ") > -1) {
						conditionalDelimiter = conditionalDelimiter.replaceAll(" ", ColumnMessageFragmentDigest.SPACE_PLACE_HOLDER);
					}
					if (conditionalFinalDelimiter != null && conditionalFinalDelimiter.indexOf(" ") > -1) {
						conditionalFinalDelimiter = conditionalFinalDelimiter.replaceAll(
								" ",
								ColumnMessageFragmentDigest.SPACE_PLACE_HOLDER);
					}
					ps.setInt(1, templateID);
					ps.setInt(2, columnNo);
					ps.setInt(3, element.getEntityID());
					ps.setString(4, conditionalDelimiter);
					ps.setString(5, conditionalFinalDelimiter);
					ps.setString(6, element.getText());
					count = ps.executeUpdate();
					if (count < 1) {
						throw new SQLException("Failed to insert template message for " + templateID + "," + columnNo + " - entityID:"
								+ element.getEntityID());
					}
				}
			}
			logger.debug("<<< setTemplateMessageMap");
		}
		finally {
			if (ps != null) ps.close();
		}
	}

	public void updateTemplate(Connection conn, GridTemplate template) throws SQLException {
		logger.debug(">>> updateTemplate: " + conn + "," + template);
		PreparedStatement ps = null;
		try {
			// insert template details
			ps = conn.prepareStatement(Q_UPDATE_TEMPLATE);
			ps.setString(1, template.getName());
			ps.setString(2, template.getUsageType().toString());
			ps.setString(3, template.getStatus());
			ps.setInt(4, template.getMaxNumOfRows());
			ps.setInt(5, template.getParentTemplateID());
			ps.setString(6, Util.toString(template.getCompletenessColumns()));
			ps.setString(7, Util.toString(template.getConsistencyColumns()));
			ps.setBoolean(8, template.fitToScreen());
			ps.setString(9, (template.getDescription() == null ? " " : template.getDescription()));
			ps.setString(10, (template.getComment() == null ? " " : template.getComment()));
			ps.setInt(11, template.getID());

			int count = ps.executeUpdate();
			if (count < 1) {
				throw new SQLException("Failed to update a row into the template table");
			}
			ps.close();
			ps = null;

			logger.debug(" updateTemplate: updating version " + template.getVersion());
			ps = conn.prepareStatement(Q_UPDATE_TEMPLATE_VERSION);
			ps.setString(1, template.getVersion());
			ps.setInt(2, template.getID());
			count = ps.executeUpdate();
			ps.close();
			ps = null;
			if (count < 1) {
				// try to insert
				ps = conn.prepareStatement(Q_INSERT_TEMPLATE_VERSION);
				ps.setInt(1, template.getID());
				ps.setString(2, template.getVersion());
				count = ps.executeUpdate();
				if (count < 1) {
					throw new SQLException("Failed to update the template-version row");
				}
				ps.close();
				ps = null;
			}

			logger.debug(" updateTemplate: updating rules...");
			updateTemplateDeploymentRule(conn, template.getID(), -1, template.getRuleDefinition());

			logger.debug(" updateTemplate: updating messages...");
			setTemplateMessages(conn, template.getID(), -1, template.getAllMessageDigest(), true);

			// insert column details
			logger.debug("    updateTemplate: template details updated: inserting columns...");

			// delete columns first
			deleteAllColumns(conn, template.getID(), true);

			for (Iterator<GridTemplateColumn> iter = template.getColumns().iterator(); iter.hasNext();) {
				GridTemplateColumn element = iter.next();
				insertColumn(conn, template.getID(), element);
			}
			logger.debug("<<< updateTemplate");
		}
		catch (SQLException ex) {
			throw ex;
		}
		catch (Exception ex) {
			logger.error("Error while updating " + template, ex);
			throw new SQLException(ex.getMessage());
		}
		finally {
			if (ps != null) ps.close();
		}
	}

	public void deleteTemplate(Connection conn, int templateID) throws SQLException {
		logger.debug(">>> deleteTemplate: " + conn + "," + templateID);
		PreparedStatement ps = null;
		try {
			int count = 0;

			deleteAllColumns(conn, templateID, false);

			ps = conn.prepareStatement(Q_DELETE_TEMPLATE_ALL_MESSAGES);
			ps.setInt(1, templateID);
			count = ps.executeUpdate();
			ps.close();
			ps = null;
			logger.debug("    deleteTemplate: removed " + count + " messages");

			ps = conn.prepareStatement(Q_DELETE_TEMPLATE_ALL_DEPLOY_RULES);
			ps.setInt(1, templateID);
			count = ps.executeUpdate();
			ps.close();
			ps = null;
			logger.debug("    deleteTemplate: removed " + count + " deploy rules");

			ps = conn.prepareStatement(Q_DELETE_TEMPLATE_VERSION);
			ps.setInt(1, templateID);
			count = ps.executeUpdate();
			ps.close();
			ps = null;
			logger.debug("    deleteTemplate: removed " + count + " versions");

			ps = conn.prepareStatement(Q_DELETE_TEMPLATE);
			ps.setInt(1, templateID);
			count = ps.executeUpdate();
			logger.debug("    deleteTemplate: removed " + count + " template");

			logger.debug("<<< deleteTemplate");
		}
		catch (SQLException ex) {
			throw ex;
		}
		catch (Exception ex) {
			logger.error("Error while deleting " + templateID, ex);
			throw new SQLException(ex.getMessage());
		}
		finally {
			if (ps != null) ps.close();
		}
	}

	private void deleteAllColumns(Connection conn, int templateID, boolean deleteRulesAndMessages) throws SQLException {
		logger.debug(">>> deleteAllColumns: " + conn + "," + templateID);
		PreparedStatement ps = null;
		try {
			int count = 0;
			ps = conn.prepareStatement(Q_DELETE_TEMPLATE_ALL_COLUMN_ATTR_ITEMS);
			ps.setInt(1, templateID);
			count = ps.executeUpdate();
			ps.close();
			ps = null;
			logger.debug("    deleteAllColumns: removed " + count + " attribute items");

			ps = conn.prepareStatement(Q_DELETE_TEMPLATE_ALL_COLUMN_ENUMS);
			ps.setInt(1, templateID);
			count = ps.executeUpdate();
			ps.close();
			ps = null;
			logger.debug("    deleteAllColumns: removed " + count + " enum values");

			ps = conn.prepareStatement(Q_DELETE_TEMPLATE_ALL_COLUMN_MESSAGE_FRAGMENTS);
			ps.setInt(1, templateID);
			count = ps.executeUpdate();
			ps.close();
			ps = null;
			logger.debug("    deleteAllColumns: removed " + count + " message fragments");

			if (deleteRulesAndMessages) {
				ps = conn.prepareStatement(Q_DELETE_TEMPLATE_ALL_COLUMN_MESSAGES);
				ps.setInt(1, templateID);
				count = ps.executeUpdate();
				ps.close();
				ps = null;
				logger.debug("    deleteAllColumns: removed " + count + " messages");

				ps = conn.prepareStatement(Q_DELETE_TEMPLATE_ALL_COLUMN_DEPLOY_RULES);
				ps.setInt(1, templateID);
				count = ps.executeUpdate();
				ps.close();
				ps = null;
				logger.debug("    deleteAllColumns: removed " + count + " deploy rules");
			}

			ps = conn.prepareStatement(Q_DELETE_TEMPLATE_ALL_COLUMNS);
			ps.setInt(1, templateID);
			count = ps.executeUpdate();
			logger.debug("    deleteAllColumns: removed " + count + " columns");

			logger.debug("<<< deleteAllColumns");
		}
		finally {
			if (ps != null) ps.close();
		}
	}

	public void insertColumn(Connection conn, int templateID, GridTemplateColumn column) throws SQLException {
		logger.debug(">>> insertColumn: " + conn + "," + templateID + "," + column);
		PreparedStatement ps = null;
		try {
			ps = conn.prepareStatement(Q_INSERT_TEMPLATE_COLUMN);
			ps.setInt(1, templateID);
			ps.setInt(2, column.getColumnNumber());
			ps.setString(3, column.getName());
			ps.setString(4, column.getDescription());
			ps.setString(5, column.getMappedAttribute());
			ps.setString(6, column.getTitle());
			ps.setString(7, column.getFont());
			ps.setString(8, column.getColor());
			ps.setInt(9, column.getColumnWidth());
			ps.setString(10, column.getColumnDataSpecDigest().getType());
			ps.setBoolean(11, column.getColumnDataSpecDigest().isMultiSelectAllowed());
			ps.setBoolean(12, column.getColumnDataSpecDigest().isBlankAllowed());
			ps.setBoolean(13, column.getColumnDataSpecDigest().isEnumValueNeedSorted());
			ps.setBoolean(14, column.getColumnDataSpecDigest().isLHSAttributeVisible());
			ps.setString(15, column.getColumnDataSpecDigest().getMinValue());
			ps.setString(16, column.getColumnDataSpecDigest().getMaxValue());
			ps.setInt(17, column.getColumnDataSpecDigest().getPrecision());

			int count = ps.executeUpdate();
			if (count < 1) {
				throw new SQLException("Failed to insert column row: template=" + templateID + ",column=" + column.getColumnNumber());
			}
			ps.close();
			ps = null;

			setColumnAttributeItems(conn, templateID, column, false);
			setColumnEnumValues(conn, templateID, column, false);
			setColumnMessageFragments(conn, templateID, column, false);

			insertTemplateDeploymentRule(conn, templateID, column.getColumnNumber(), column.getRuleDefinition());

			if (column.hasMessageDigest()) {
				setTemplateMessages(conn, templateID, column.getColumnNumber(), column.getAllMessageDigest(), false);
			}

			setColumnProperties(conn, templateID, column.getColumnNumber(), getColumnPropertyMap(column));

			logger.debug("<<< insertColumn");
		}
		catch (SQLException ex) {
			throw ex;
		}
		catch (Exception ex) {
			logger.error("Error while inserting " + column, ex);
			throw new SQLException(ex.getMessage());
		}
		finally {
			if (ps != null) ps.close();
		}

	}

	private void setColumnAttributeItems(Connection conn, int templateID, GridTemplateColumn column, boolean deleteFirst)
			throws SQLException {
		logger.debug(">>> setColumnAttributeItems: " + templateID + ", " + column + "," + deleteFirst);
		PreparedStatement ps = null;
		try {
			int count = 0;
			if (deleteFirst) {
				ps = conn.prepareStatement(Q_DELETE_TEMPLATE_COLUMN_ATTR_ITEMS);
				ps.setInt(1, templateID);
				ps.setInt(2, column.getColumnNumber());
				count = ps.executeUpdate();
				ps.close();
				ps = null;
				logger.debug("    setColumnAttributeItems: removed " + count + " attribute items");
			}

			if (column.getColumnDataSpecDigest().hasAttributeItem()) {
				ps = conn.prepareStatement(Q_INSERT_TEMPLATE_COLUMN_ATTR_ITEM);

				for (Iterator<ColumnAttributeItemDigest> iter = column.getColumnDataSpecDigest().getAllAttributeItems().iterator(); iter.hasNext();) {
					ColumnAttributeItemDigest digest = iter.next();
					ps.setInt(1, templateID);
					ps.setInt(2, column.getColumnNumber());
					ps.setString(3, digest.getName());
					ps.setString(4, digest.getDisplayValue());
					count = ps.executeUpdate();
					if (count < 1) {
						throw new SQLException("Failed to insert column attr item for " + templateID + "," + column.getColumnNumber()
								+ " - " + digest.getName());
					}
				}
			}
			logger.debug("<<< setColumnAttributeItems");
		}
		finally {
			if (ps != null) ps.close();
		}
	}

	private void setColumnEnumValues(Connection conn, int templateID, GridTemplateColumn column, boolean deleteFirst) throws SQLException {
		logger.debug(">>> setColumnEnumValues: " + templateID + ", " + column + "," + deleteFirst);
		PreparedStatement ps = null;
		try {
			int count = 0;
			if (deleteFirst) {
				ps = conn.prepareStatement(Q_DELETE_TEMPLATE_COLUMN_ENUMS);
				ps.setInt(1, templateID);
				ps.setInt(2, column.getColumnNumber());
				count = ps.executeUpdate();
				ps.close();
				ps = null;
				logger.debug("    setColumnEnumValues: removed " + count + " enum values");
			}

			if (column.getColumnDataSpecDigest().hasEnumValue()) {
				ps = conn.prepareStatement(Q_INSERT_TEMPLATE_COLUMN_ENUM);

				for (Iterator<String> iter = column.getColumnDataSpecDigest().getAllColumnEnumValues().iterator(); iter.hasNext();) {
					String value = iter.next();
					ps.setInt(1, templateID);
					ps.setInt(2, column.getColumnNumber());
					ps.setString(3, value);
					count = ps.executeUpdate();
					if (count < 1) {
						throw new SQLException("Failed to insert column enum value for " + templateID + "," + column.getColumnNumber()
								+ " - " + value);
					}
				}
			}
			logger.debug("<<< setColumnEnumValues");
		}
		finally {
			if (ps != null) ps.close();
		}
	}

	private void setColumnMessageFragments(Connection conn, int templateID, GridTemplateColumn column, boolean deleteFirst)
			throws SQLException {
		logger.debug(">>> setColumnMessageFragments: " + templateID + ", " + column + "," + deleteFirst);
		PreparedStatement ps = null;
		try {
			int count = 0;
			if (deleteFirst) {
				ps = conn.prepareStatement(Q_DELETE_TEMPLATE_COLUMN_MESSAGE_FRAGMENTS);
				ps.setInt(1, templateID);
				ps.setInt(2, column.getColumnNumber());
				count = ps.executeUpdate();
				ps.close();
				ps = null;
				logger.debug("    setColumnMessageFragments: removed " + count + " message fragments");
			}

			if (column.hasMessageFragmentDigest()) {
				ps = conn.prepareStatement(Q_INSERT_TEMPLATE_COLUMN_MESSAGE_FRAGMENT);
				for (ColumnMessageFragmentDigest digest : column.getAllMessageFragmentDigests()) {
					String enumDelimiter = digest.getEnumDelimiter();
					if (enumDelimiter != null && enumDelimiter.indexOf(" ") > -1) {
						enumDelimiter = enumDelimiter.replaceAll(" ", ColumnMessageFragmentDigest.SPACE_PLACE_HOLDER);
					}
					String enumFinalDelimiter = digest.getEnumFinalDelimiter();
					if (enumFinalDelimiter != null && enumFinalDelimiter.indexOf(" ") > -1) {
						enumFinalDelimiter = enumFinalDelimiter.replaceAll(" ", ColumnMessageFragmentDigest.SPACE_PLACE_HOLDER);
					}
					String enumPrefix = digest.getEnumPrefix();
					if (enumPrefix != null && enumPrefix.indexOf(" ") > -1) {
						enumPrefix = enumPrefix.replaceAll(" ", ColumnMessageFragmentDigest.SPACE_PLACE_HOLDER);
					}
					ps.setInt(1, templateID);
					ps.setInt(2, column.getColumnNumber());
					ps.setString(3, digest.getText());
					ps.setString(4, digest.getType());
					ps.setString(5, digest.getCellSelection());
					ps.setString(6, enumDelimiter);
					ps.setString(7, enumFinalDelimiter);
					ps.setString(8, enumPrefix);
					ps.setString(9, digest.getRangeStyle());
					count = ps.executeUpdate();
					if (count < 1) {
						throw new SQLException("Failed to insert column message fragment for " + templateID + ","
								+ column.getColumnNumber() + " - " + digest);
					}
				}
			}
			logger.debug("<<< setColumnMessageFragments");
		}
		finally {
			if (ps != null) ps.close();
		}
	}

	public void updateColumn(Connection conn, int templateID, GridTemplateColumn column) throws SQLException {
		logger.debug(">>> updateColumn: " + conn + "," + templateID + "," + column);
		PreparedStatement ps = null;
		try {
			ps = conn.prepareStatement(Q_UPDATE_TEMPLATE_COLUMN);
			ps.setInt(1, column.getColumnNumber());
			ps.setString(2, column.getName());
			ps.setString(3, column.getDescription());
			ps.setString(4, column.getMappedAttribute());
			ps.setString(5, column.getTitle());
			ps.setString(6, column.getFont());
			ps.setString(7, column.getColor());
			ps.setInt(8, column.getColumnWidth());
			ps.setString(9, column.getColumnDataSpecDigest().getType());
			ps.setBoolean(10, column.getColumnDataSpecDigest().isMultiSelectAllowed());
			ps.setBoolean(11, column.getColumnDataSpecDigest().isBlankAllowed());
			ps.setBoolean(12, column.getColumnDataSpecDigest().isEnumValueNeedSorted());
			ps.setBoolean(13, column.getColumnDataSpecDigest().isLHSAttributeVisible());
			ps.setString(14, column.getColumnDataSpecDigest().getMinValue());
			ps.setString(15, column.getColumnDataSpecDigest().getMaxValue());
			ps.setInt(16, column.getColumnDataSpecDigest().getPrecision());
			ps.setInt(17, templateID);

			int count = ps.executeUpdate();
			if (count < 1) {
				throw new SQLException("Failed to update column row: template=" + templateID + ",column=" + column.getColumnNumber());
			}
			ps.close();
			ps = null;

			setColumnAttributeItems(conn, templateID, column, true);
			setColumnEnumValues(conn, templateID, column, true);
			setColumnMessageFragments(conn, templateID, column, true);

			updateTemplateDeploymentRule(conn, templateID, column.getColumnNumber(), column.getRuleDefinition());

			if (column.hasMessageDigest()) {
				setTemplateMessages(conn, templateID, column.getColumnNumber(), column.getAllMessageDigest(), true);
			}

			setColumnProperties(conn, templateID, column.getColumnNumber(), getColumnPropertyMap(column));

			logger.debug("<<< updateColumn");
		}
		catch (SQLException ex) {
			throw ex;
		}
		catch (Exception ex) {
			logger.error("Error while updating " + column, ex);
			throw new SQLException(ex.getMessage());
		}
		finally {
			if (ps != null) ps.close();
		}
	}

	private Map<String, String> getColumnPropertyMap(GridTemplateColumn column) {
		Map<String, String> map = new HashMap<String, String>();
		if (column.getColumnDataSpecDigest().getType().equals(ColumnDataSpecDigest.TYPE_ENTITY)) {
			map.put("entityType", column.getColumnDataSpecDigest().getEntityType());
			map.put("allowCategory", column.getColumnDataSpecDigest().getAllowCategory());
			map.put("allowEntity", column.getColumnDataSpecDigest().getAllowEntity());
		}
		else if (column.getColumnDataSpecDigest().getType().equals(ColumnDataSpecDigest.TYPE_ENUM_LIST)) {
			map.put(GuidelineTemplateLoader.COLUMN_PROP_ENUM_SOURCE_TYPE, column.getColumnDataSpecDigest().getEnumSourceType().toString());
			if (column.getColumnDataSpecDigest().getEnumSourceType() == EnumSourceType.EXTERNAL) {
				map.put(GuidelineTemplateLoader.COLUMN_PROP_ENUM_SOURCE_NAME, column.getColumnDataSpecDigest().getEnumSourceName());
			}
			if (!UtilBase.isEmpty(column.getColumnDataSpecDigest().getEnumSelectorColumnName())) {
				map.put(
						GuidelineTemplateLoader.COLUMN_PROP_ENUM_SELECTOR_COLUMN,
						column.getColumnDataSpecDigest().getEnumSelectorColumnName());
			}
		}
		return map;
	}

	private void setColumnProperties(Connection conn, int templateID, int columnID, Map<String, String> columnPropMap) throws SQLException {
		logger.debug(">>> setColumnProperties: " + conn + "," + templateID + "," + columnID);
		PreparedStatement ps = null;
		try {
			ps = conn.prepareStatement(Q_DELETE_TEMPLATE_COLUMN_PROP);
			ps.setInt(1, templateID);
			ps.setInt(2, columnID);

			int count = ps.executeUpdate();
			ps.close();
			ps = null;
			logger.debug("    setColumnProperties: removed " + count + " column props");

			if (columnPropMap != null && !columnPropMap.isEmpty()) {
				ps = conn.prepareStatement(Q_INSERT_TEMPLATE_COLUMN_PROP);
				ps.setInt(1, templateID);
				ps.setInt(2, columnID);
				for (Iterator<String> iter = columnPropMap.keySet().iterator(); iter.hasNext();) {
					String key = iter.next();
					String value = columnPropMap.get(key);
					if (UtilBase.isEmpty(value)) {
						logger.warn("   setColumnProperties: skipping " + key + "; value is empty!");
					}
					else {
						logger.debug("    setColumnProperties: inserting column prop " + key + " = " + value);
						ps.setString(3, key);
						ps.setString(4, value);
						count = ps.executeUpdate();
						if (count < 1) throw new SQLException("No row inserted for " + key + "=" + value);
					}
				}
			}
			logger.debug("<<< updateColumn");
		}
		catch (SQLException ex) {
			throw ex;
		}
		catch (Exception ex) {
			logger.error("Error while updating props for " + templateID + "," + columnID, ex);
			throw new SQLException(ex.getMessage());
		}
		finally {
			if (ps != null) ps.close();
		}
	}

	public void deleteColumn(Connection conn, int templateID, int columnNo) throws SQLException {
		logger.debug(">>> deleteColumn: " + conn + "," + templateID + "," + columnNo);
		PreparedStatement ps = null;
		try {
			int count = 0;
			ps = conn.prepareStatement(Q_DELETE_TEMPLATE_COLUMN_ATTR_ITEMS);
			ps.setInt(1, templateID);
			ps.setInt(2, columnNo);
			count = ps.executeUpdate();
			ps.close();
			ps = null;
			logger.debug("    deleteColumn: removed " + count + " attribute items");

			ps = conn.prepareStatement(Q_DELETE_TEMPLATE_COLUMN_ENUMS);
			ps.setInt(1, templateID);
			ps.setInt(2, columnNo);
			count = ps.executeUpdate();
			ps.close();
			ps = null;
			logger.debug("    deleteColumn: removed " + count + " enum values");

			ps = conn.prepareStatement(Q_DELETE_TEMPLATE_COLUMN_MESSAGE_FRAGMENTS);
			ps.setInt(1, templateID);
			ps.setInt(2, columnNo);
			count = ps.executeUpdate();
			ps.close();
			ps = null;
			logger.debug("    deleteColumn: removed " + count + " message fragments");

			ps = conn.prepareStatement(Q_DELETE_TEMPLATE_COLUMN_MESSAGE);
			ps.setInt(1, templateID);
			ps.setInt(2, columnNo);
			count = ps.executeUpdate();
			ps.close();
			ps = null;
			logger.debug("    deleteColumn: removed " + count + " message");

			ps = conn.prepareStatement(Q_DELETE_TEMPLATE_COLUMN_DEPLOY_RULE);
			ps.setInt(1, templateID);
			ps.setInt(2, columnNo);
			count = ps.executeUpdate();
			ps.close();
			ps = null;
			logger.debug("    deleteColumn: removed " + count + " deploy rule");

			ps = conn.prepareStatement(Q_DELETE_TEMPLATE_COLUMN_PROP);
			ps.setInt(1, templateID);
			ps.setInt(2, columnNo);
			count = ps.executeUpdate();
			ps.close();
			ps = null;
			logger.debug("    deleteColumn: removed " + count + " column props");

			ps = conn.prepareStatement(Q_DELETE_TEMPLATE_COLUMN);
			ps.setInt(1, templateID);
			ps.setInt(2, columnNo);
			count = ps.executeUpdate();
			logger.debug("    deleteColumn: removed " + count + " columns");

			logger.debug("<<< deleteColumn");
		}
		finally {
			if (ps != null) ps.close();
		}
	}

	public void updateRuleDefinition(Connection conn, int templateID, int columnNo, RuleDefinition ruleDef) throws SQLException {
		try {
			updateTemplateDeploymentRule(conn, templateID, columnNo, ruleDef);
		}
		catch (SQLException ex) {
			throw ex;
		}
		catch (Exception ex) {
			logger.error("Error while updating rule for " + templateID + "," + columnNo, ex);
			throw new SQLException(ex.getMessage());
		}
	}

}
