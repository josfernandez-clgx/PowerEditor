package com.mindbox.pe.server.db.loaders;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import com.mindbox.pe.common.UtilBase;
import com.mindbox.pe.common.config.EntityTypeDefinition;
import com.mindbox.pe.model.ColumnAttributeItemDigest;
import com.mindbox.pe.model.ColumnDataSpecDigest;
import com.mindbox.pe.model.ColumnMessageFragmentDigest;
import com.mindbox.pe.model.Constants;
import com.mindbox.pe.model.GenericEntity;
import com.mindbox.pe.model.GenericEntityType;
import com.mindbox.pe.model.GridTemplate;
import com.mindbox.pe.model.GridTemplateColumn;
import com.mindbox.pe.model.Persistent;
import com.mindbox.pe.model.TemplateMessageDigest;
import com.mindbox.pe.model.TemplateUsageType;
import com.mindbox.pe.model.ColumnDataSpecDigest.EnumSourceType;
import com.mindbox.pe.model.exceptions.SapphireException;
import com.mindbox.pe.model.rule.RuleDefinition;
import com.mindbox.pe.server.RuleDefinitionUtil;
import com.mindbox.pe.server.Util;
import com.mindbox.pe.server.cache.EntityManager;
import com.mindbox.pe.server.cache.GuidelineTemplateManager;
import com.mindbox.pe.server.config.ConfigurationManager;
import com.mindbox.pe.server.config.KnowledgeBaseFilterConfig;
import com.mindbox.pe.server.db.DBConnectionManager;
import com.mindbox.pe.server.db.DBUtil;
import com.mindbox.pe.server.spi.GuidelineRuleInfo;
import com.mindbox.pe.server.spi.ServiceProviderFactory;
import com.mindbox.server.parser.jtb.rule.ParseException;

/**
 * Loads guideline actions from DB.
 * 
 * @author Geneho
 * @since PowerEditor 4.0.0
 */
public class GuidelineTemplateLoader extends AbstractLoader {

	private static final String Q_LOAD_TEMPLATES = "select template_id,name,usage_type,status,max_row,parent_id,description,comments,comp_cols,consist_cols,fit_screen"
			+ " from MB_TEMPLATE order by template_id";

	private static final String Q_LOAD_TEMPLATE_VERSIONS = "select template_id,version from MB_TEMPLATE_VERSION";

	private static final String Q_LOAD_COLUMNS = "select column_no,column_name,description,attribute_map,"
			+ " title,font,color,width,data_type," + " multi_select,allow_blank,sort_enum,show_lhs_attr,min_value,max_value,precision_val"
			+ " from MB_TEMPLATE_COLUMN where template_id=? order by column_no";

	private static final String Q_LOAD_COLUMN_ENUMS = "select enum_value from MB_TEMPLATE_COLUMN_ENUM where template_id=? and column_no=?";

	private static final String Q_LOAD_COLUMN_ATTR_ITEMS = "select name,display_value from MB_TEMPLATE_COLUMN_ATTR_ITEM where template_id=? and column_no=?";

	private static final String Q_LOAD_COLUMN_MESSAGE_FRAGMENTS = "select message_text,type,cell_selection,enum_delim,enum_final_delim,enum_prefix,range_style"
			+ " from MB_TEMPLATE_MESSAGE_FRAGMENT where template_id=? and column_no=?";

	private static final String Q_LOAD_COLUMN_PROPS = "select property_name,property_value from MB_TEMPLATE_COLUMN_PROP where template_id=? and column_no=?";

	private static final String Q_LOAD_TEMPLATE_MESSAGES = "select template_id,column_no,entity_id,cond_delim,cond_final_delim,message_text"
			+ " from MB_TEMPLATE_MESSAGE order by template_id";

	public static final String COLUMN_PROP_ENTITY_TYPE = "entityType";
	public static final String COLUMN_PROP_ALLOW_CATEGORY = "allowCategory";
	public static final String COLUMN_PROP_ALLOW_ENTITY = "allowEntity";
	public static final String COLUMN_PROP_ENUM_SOURCE_TYPE = "enumSourceType";
	public static final String COLUMN_PROP_ENUM_SOURCE_NAME = "enumSourceName";
	public static final String COLUMN_PROP_ENUM_SELECTOR_COLUMN = "enumSelectorColumn";

	private static GuidelineTemplateLoader instance = null;

	public static GuidelineTemplateLoader getInstance() {
		if (instance == null) {
			instance = new GuidelineTemplateLoader();
		}
		return instance;
	}

	private GuidelineTemplateLoader() {
	}

	public void load(KnowledgeBaseFilterConfig knowledgeBaseFilterConfig) throws ParseException, SapphireException, SQLException,
			SAXException, IOException, ParserConfigurationException {
		// NOTE: filter not used as date filter is not applicable to guideline actions
		
		GuidelineTemplateManager.getInstance().startLoading();
		try {
			loadTemplates();
			loadTemplateVersions();
			loadTemplateColumns();
			loadTemplateMessages();
			loadTemplateDeployRules();
		}
		finally {
			GuidelineTemplateManager.getInstance().finishLoading();
		}
	}

	private void loadTemplates() throws SQLException {
		logger.info("=== TEMPLATE: TEMPLATES ===");
		Connection conn = DBConnectionManager.getInstance().getConnection();
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = conn.prepareStatement(Q_LOAD_TEMPLATES);
			rs = ps.executeQuery();

			while (rs.next()) {
				int id = rs.getInt(1);
				String name = UtilBase.trim(rs.getString(2));
				String usage = UtilBase.trim(rs.getString(3));
				String status = UtilBase.trim(rs.getString(4));
				int maxRow = rs.getInt(5);
				int parentID = rs.getInt(6);
				String desc = UtilBase.trim(rs.getString(7));
				String comment = UtilBase.trim(rs.getString(8));
				String completeColsStr = UtilBase.trim(rs.getString(9));
				String consistencyColsStr = UtilBase.trim(rs.getString(10));
				boolean fitToScreen = rs.getBoolean(11);

				logger.info("Template: " + id + ",name=" + name + ",parent=" + parentID + ",desc=" + desc + ",status=" + status
						+ ",fitScreen=" + fitToScreen);

				GridTemplate template = new GridTemplate(id, name, TemplateUsageType.valueOf(usage));
				template.setMaxNumOfRows(maxRow);
				template.setDescription(desc);
				template.setParentTemplateID(parentID);
				template.setFitToScreen(fitToScreen);
				template.setCompletenessColumns(Util.fromIDListString(completeColsStr));
				template.setConsistencyColumns(Util.fromIDListString(consistencyColsStr));
				template.setStatus(status);
				template.setComment(comment);

				GuidelineTemplateManager.getInstance().addTemplate(template);
			}
		}
		finally {
			DBUtil.closeLocallyManagedResources(rs, ps);
			DBConnectionManager.getInstance().freeConnection(conn);
		}
	}

	private void loadTemplateVersions() throws SQLException {
		logger.info("=== TEMPLATE: Template Versions ===");
		Connection conn = DBConnectionManager.getInstance().getConnection();
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = conn.prepareStatement(Q_LOAD_TEMPLATE_VERSIONS);
			rs = ps.executeQuery();

			while (rs.next()) {
				int templateID = rs.getInt(1);
				String version = Util.trim(rs.getString(2));

				GridTemplate template = GuidelineTemplateManager.getInstance().getTemplate(templateID);
				if (template == null) {
					logger.warn("Template-Version: ignored - template not found: t=" + templateID + ",v=" + version);
				}
				else {
					if (Util.isEmpty(version)) {
						template.setVersion(GridTemplate.DEFAULT_VERSION);
						logger.warn("Template-Version: version empty: set to " + GridTemplate.DEFAULT_VERSION + "; " + templateID + ",v="
								+ version);
					}
					else {
						template.setVersion(version);
						logger.info("Template-Version: template=" + templateID + ",v=" + version);
					}
				}
			}
		}
		finally {
			DBUtil.closeLocallyManagedResources(rs, ps);
			DBConnectionManager.getInstance().freeConnection(conn);
		}
	}

	private void loadTemplateColumns() throws SQLException {
		logger.info("=== TEMPLATE: Template Columns ===");
		Connection conn = DBConnectionManager.getInstance().getConnection();
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = conn.prepareStatement(Q_LOAD_COLUMNS);

			for (Iterator<GridTemplate> iter = GuidelineTemplateManager.getInstance().getAllTemplates().iterator(); iter.hasNext();) {
				GridTemplate template = iter.next();
				logger.debug("Loading columns for template " + template.getID());

				ps.setInt(1, template.getID());
				rs = ps.executeQuery();

				while (rs.next()) {
					int columnNo = rs.getInt(1);
					String name = UtilBase.trim(rs.getString(2));
					String desc = UtilBase.trim(rs.getString(3));
					String attrMap = UtilBase.trim(rs.getString(4));
					String title = UtilBase.trim(rs.getString(5));
					String font = UtilBase.trim(rs.getString(6));
					String color = UtilBase.trim(rs.getString(7));
					int width = rs.getInt(8);
					String dataType = UtilBase.trim(rs.getString(9));
					boolean multiSelect = rs.getBoolean(10);
					boolean allowBlank = rs.getBoolean(11);
					boolean sortEnum = rs.getBoolean(12);
					boolean showLHSAttr = rs.getBoolean(13);
					String minValue = UtilBase.trim(rs.getString(14));
					String maxValue = UtilBase.trim(rs.getString(15));
					String precisionStr = rs.getString(16);

					if (columnNo <= 0) {
						logger.warn("Template_Column: ignored - invalid column no: col=" + columnNo + ",title=" + title + ",attrMap="
								+ attrMap + ",dataType=" + dataType + "," + ",allowBlank?=" + allowBlank);
					}
					else {
						GridTemplateColumn column = new GridTemplateColumn(columnNo, name, desc, width, template.getUsageType());
						column.setColor(color);
						column.setTitle(title);
						column.setFont(font);

						// use digest object for data spec creation
						// b/c it's known to work
						ColumnDataSpecDigest digest = new ColumnDataSpecDigest();

						loadColumnEnumValues(template.getID(), columnNo, digest);
						loadColumnAttributeItems(template.getID(), columnNo, digest);

						digest.setAllowBlank((allowBlank ? Constants.VALUE_YES : Constants.VALUE_NO));
						if (maxValue != null && !maxValue.equalsIgnoreCase("null")) digest.setMaxValue(maxValue);
						if (minValue != null && !minValue.equalsIgnoreCase("null")) digest.setMinValue(minValue);
						digest.setMultipleSelect((multiSelect ? Constants.VALUE_YES : Constants.VALUE_NO));
						// TT 1879 do not set precision if it's null
						if (precisionStr != null) {
							try {
								digest.setPrecision(Integer.parseInt(precisionStr));
							}
							catch (NumberFormatException ex) {
								// ignore
							}
						}
						digest.setShowLhsAttributes((showLHSAttr ? Constants.VALUE_YES : Constants.VALUE_NO));
						digest.setSortEnumValue((sortEnum ? Constants.VALUE_YES : Constants.VALUE_NO));
						digest.setType(dataType);
						
						column.setDataSpecDigest(digest);

						loadColumnMessageFragments(template.getID(), columnNo, column);

						loadColumnProperties(template.getID(), columnNo, column);

						digest.setAttributeMap(attrMap);
						digest.resetColumnEnumSourceTypeIfNecessary();

						template.addGridTemplateColumn(column);
						logger.info("Template_Column: added - col=" + columnNo + ",title=" + title + ",attrMap=" + attrMap + ",dataType="
								+ dataType + "," + ",allowBlank?=" + allowBlank);
					}
				}
			}
		}
		finally {
			DBUtil.closeLocallyManagedResources(rs, ps);
			DBConnectionManager.getInstance().freeConnection(conn);
		}
	}

	private void loadColumnEnumValues(int templateID, int columnNo, ColumnDataSpecDigest digest) {
		Connection connection = null;
		ResultSet rs = null;
		PreparedStatement ps = null;
		try {
			connection = DBConnectionManager.getInstance().getConnection();
			ps = connection.prepareStatement(Q_LOAD_COLUMN_ENUMS);
			ps.setInt(1, templateID);
			ps.setInt(2, columnNo);
			rs = ps.executeQuery();

			while (rs.next()) {
				String enumValue = UtilBase.trim(rs.getString(1));
				digest.addColumnEnumValue(enumValue);
			}
			rs.close();
			rs = null;
		}
		catch (Exception ex) {
			logger.error("Failed to get enum values for template,col " + templateID + "," + columnNo, ex);
		}
		finally {
			DBUtil.closeLocallyManagedResources(rs, ps);
			DBConnectionManager.getInstance().freeConnection(connection);
		}
	}

	private void loadColumnAttributeItems(int templateID, int columnNo, ColumnDataSpecDigest digest) {
		Connection connection = null;
		ResultSet rs = null;
		PreparedStatement ps = null;
		try {
			connection = DBConnectionManager.getInstance().getConnection();
			ps = connection.prepareStatement(Q_LOAD_COLUMN_ATTR_ITEMS);
			ps.setInt(1, templateID);
			ps.setInt(2, columnNo);
			rs = ps.executeQuery();

			while (rs.next()) {
				String name = UtilBase.trim(rs.getString(1));
				String dispValue = UtilBase.trim(rs.getString(2));
				digest.addAttributeItem(new ColumnAttributeItemDigest(name, dispValue));
			}
			rs.close();
			rs = null;
		}
		catch (Exception ex) {
			logger.error("Failed to get attribute items for template,col " + templateID + "," + columnNo, ex);
		}
		finally {
			DBUtil.closeLocallyManagedResources(rs, ps);
			DBConnectionManager.getInstance().freeConnection(connection);
		}
	}

	private void loadColumnMessageFragments(int templateID, int columnNo, GridTemplateColumn column) {
		Connection connection = null;
		ResultSet rs = null;
		PreparedStatement ps = null;
		try {
			connection = DBConnectionManager.getInstance().getConnection();
			ps = connection.prepareStatement(Q_LOAD_COLUMN_MESSAGE_FRAGMENTS);
			ps.setInt(1, templateID);
			ps.setInt(2, columnNo);
			rs = ps.executeQuery();

			while (rs.next()) {
				String messageText = UtilBase.trim(rs.getString(1));
				String type = UtilBase.trim(rs.getString(2));
				String cellSelection = UtilBase.trim(rs.getString(3));
				String enumDelimiter = UtilBase.trim(rs.getString(4));
				String enumFinalDelimiter = UtilBase.trim(rs.getString(5));
				String enumPrefix = UtilBase.trim(rs.getString(6));
				String rangeStyle = UtilBase.trim(rs.getString(7));

				messageText = DBUtil.decodeSpacePlaceHolder(messageText);
				enumDelimiter = DBUtil.decodeSpacePlaceHolder(enumDelimiter);
				enumPrefix = DBUtil.decodeSpacePlaceHolder(enumPrefix);
				messageText = DBUtil.decodeSpacePlaceHolder(messageText);

				ColumnMessageFragmentDigest digest = new ColumnMessageFragmentDigest();
				digest.setCellSelection(cellSelection);
				digest.setEnumDelimiter(enumDelimiter);
				digest.setEnumFinalDelimiter(enumFinalDelimiter);
				digest.setEnumPrefix(enumPrefix);
				digest.setRangeStyle(rangeStyle);
				digest.setText(messageText);
				digest.setType(type);

				column.addColumnMessageFragment(digest);
				logger.info("Column Message Fragment: template=" + templateID + ",col=" + columnNo + ",type=" + type);
			}
			rs.close();
			rs = null;
		}

		catch (Exception ex) {
			logger.error("Failed to get column message fragments for template,col " + templateID + "," + columnNo, ex);
		}
		finally {
			DBUtil.closeLocallyManagedResources(rs, ps);
			DBConnectionManager.getInstance().freeConnection(connection);
		}
	}

	private void loadColumnProperties(int templateID, int columnNo, GridTemplateColumn column) {
		Connection connection = null;
		ResultSet rs = null;
		PreparedStatement ps = null;
		try {
			connection = DBConnectionManager.getInstance().getConnection();
			ps = connection.prepareStatement(Q_LOAD_COLUMN_PROPS);
			ps.setInt(1, templateID);
			ps.setInt(2, columnNo);
			rs = ps.executeQuery();

			while (rs.next()) {
				String key = UtilBase.trim(rs.getString(1));
				String value = UtilBase.trim(rs.getString(2));
				if (key.equals(COLUMN_PROP_ENTITY_TYPE)) {
					column.getColumnDataSpecDigest().setEntityType(value);
				}
				else if (key.equals(COLUMN_PROP_ALLOW_CATEGORY)) {
					column.getColumnDataSpecDigest().setAllowCategory(value);
				}
				else if (key.equals(COLUMN_PROP_ALLOW_ENTITY)) {
					column.getColumnDataSpecDigest().setAllowEntity(value);
				}
				else if (key.equals(COLUMN_PROP_ENUM_SOURCE_TYPE)) {
					column.getColumnDataSpecDigest().setEnumSourceType(EnumSourceType.valueOf(value));
				}
				else if (key.equals(COLUMN_PROP_ENUM_SOURCE_NAME)) {
					column.getColumnDataSpecDigest().setEnumSourceName(value);
				}
				else if (key.equals(COLUMN_PROP_ENUM_SELECTOR_COLUMN)) {
					column.getColumnDataSpecDigest().setEnumSelectorColumnName(value);
				}
				else {
					logger.warn("Invalid column property key: " + key + " = " + value);
				}
			}
			rs.close();
			rs = null;
		}
		catch (Exception ex) {
			logger.error("Failed to get column properties for template,col " + templateID + "," + columnNo, ex);
		}
		finally {
			DBUtil.closeLocallyManagedResources(rs, ps);
			DBConnectionManager.getInstance().freeConnection(connection);
		}
	}

	private void loadTemplateMessages() throws SQLException {
		EntityTypeDefinition etDef = ConfigurationManager.getInstance().getEntityConfiguration().getEntityTypeForMessageContext();
		GenericEntityType entityTypeForMessage = (etDef == null ? null : GenericEntityType.forID(etDef.getTypeID()));
		logger.info("=== TEMPLATE: Template Messages ===");
		Connection conn = DBConnectionManager.getInstance().getConnection();
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = conn.prepareStatement(Q_LOAD_TEMPLATE_MESSAGES);
			rs = ps.executeQuery();

			while (rs.next()) {
				int templateID = rs.getInt(1);
				int columnNo = rs.getInt(2);
				int entityID = rs.getInt(3);
				String condDelimiter = UtilBase.trim(rs.getString(4));
				String condFinalDelimiter = UtilBase.trim(rs.getString(5));
				String messageText = UtilBase.trim(rs.getString(6));
				condDelimiter = DBUtil.decodeSpacePlaceHolder(condDelimiter);
				condFinalDelimiter = DBUtil.decodeSpacePlaceHolder(condFinalDelimiter);

				GridTemplate template = GuidelineTemplateManager.getInstance().getTemplate(templateID);
				if (template == null) {
					logger.warn("Template-Message: ignored - template not found: temp=" + templateID + ",col=" + columnNo + ",entityID="
							+ entityID);
				}
				else if (columnNo > template.getNumColumns()) {
					logger.warn("Template-Message: ignored - invalid column no: temp=" + templateID + ",col=" + columnNo + ",entityID="
							+ entityID);
				}
				else {
					TemplateMessageDigest messageDigest = new TemplateMessageDigest();
					if (entityTypeForMessage != null) {
						GenericEntity entity = EntityManager.getInstance().getEntity(entityTypeForMessage, entityID);
						messageDigest.setEntityID((entity == null ? Persistent.UNASSIGNED_ID : entityID));
					}
					messageDigest.setConditionalDelimiter(condDelimiter);
					messageDigest.setConditionalFinalDelimiter(condFinalDelimiter);
					messageDigest.setText(messageText);

					if (columnNo < 1) {
						template.addMessageDigest(messageDigest);
					}
					else {
						GridTemplateColumn column = (GridTemplateColumn) template.getColumn(columnNo);
						column.addMessageDigest(messageDigest);
					}
					logger.info("Template-Message: added - temp=" + templateID + ",col=" + columnNo + ",entityID=" + entityID);
				}
			}
		}
		finally {
			DBUtil.closeLocallyManagedResources(rs, ps);
			DBConnectionManager.getInstance().freeConnection(conn);
		}
	}

	private void loadTemplateDeployRules() throws SQLException, SAXException, IOException, ParserConfigurationException {
		logger.info("=== TEMPLATE: Template Deploy Rules ===");

		List<GuidelineRuleInfo> ruleInfoList = ServiceProviderFactory.getGuidelineRuleProvider().fetchAllGuidelineRules();
		for (GuidelineRuleInfo ruleInfo : ruleInfoList) {
			String ruleDefStr = null;
			try {
				ruleDefStr = ruleInfo.getRuleString();

				GridTemplate template = GuidelineTemplateManager.getInstance().getTemplate(ruleInfo.getTemplateID());
				if (template == null) {
					logger.warn("Template-Rule: ignored - template not found: temp=" + ruleInfo.getTemplateID() + ",col="
							+ ruleInfo.getColumnNo());
				}
				else if (ruleInfo.getColumnNo() > template.getNumColumns()) {
					logger.warn("Template-Rule: ignored - invalid column no: temp=" + ruleInfo.getTemplateID() + ",col="
							+ ruleInfo.getColumnNo());
				}
				else {
					RuleDefinition ruleDef = RuleDefinitionUtil.parseToRuleDefinition(ruleDefStr);
					if (ruleInfo.getColumnNo() < 1) {
						template.setRuleDefinition(ruleDef);
					}
					else {
						GridTemplateColumn column = (GridTemplateColumn) template.getColumn(ruleInfo.getColumnNo());
						column.setRuleDefinition(ruleDef);
					}
					logger.info("Template-Rule: added - temp=" + ruleInfo.getTemplateID() + ",col=" + ruleInfo.getColumnNo() + ",strSize="
							+ (ruleDefStr == null ? 0 : ruleDefStr.length()));
				}
			}
			catch (SAXException ex) {
				logger.error("Failed to load template (XML Error): template=" + ruleInfo.getTemplateID() + ", column="
						+ ruleInfo.getColumnNo(), ex);
				logger.error("Rule String size = " + ruleDefStr.length());
				logger.error("Rule String: " + ruleDefStr);
				logger.error("Loading continued...");
			}
		}
	}
}