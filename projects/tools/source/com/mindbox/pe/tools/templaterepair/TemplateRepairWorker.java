/*
 * Created on 2005. 5. 10.
 *
 */
package com.mindbox.pe.tools.templaterepair;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import com.mindbox.pe.model.TemplateUsageType;
import com.mindbox.pe.model.rule.CompoundLHSElement;
import com.mindbox.pe.model.rule.Condition;
import com.mindbox.pe.model.rule.ExistExpression;
import com.mindbox.pe.model.rule.FunctionParameter;
import com.mindbox.pe.model.rule.Reference;
import com.mindbox.pe.model.rule.RuleAction;
import com.mindbox.pe.model.rule.RuleDefinition;
import com.mindbox.pe.model.rule.RuleElement;
import com.mindbox.pe.model.rule.Value;
import com.mindbox.pe.server.RuleDefinitionUtil;
import com.mindbox.pe.server.cache.GuidelineFunctionManager;
import com.mindbox.pe.server.db.loaders.GuidelineActionLoader;
import com.mindbox.pe.tools.InvalidSpecException;
import com.mindbox.pe.tools.db.DBConnInfo;
import com.mindbox.pe.tools.db.DBConnectionFactory;
import com.mindbox.pe.tools.db.DBUtil;
import com.mindbox.server.parser.jtb.rule.ParseException;


/**
 * @author Geneho Kim
 * @since PowerEditor 
 */
public class TemplateRepairWorker {

	private static final String Q_GET_TEMPLATE_COLUMN_RULES = "select T.template_id,T.name,T.usage_type,V.version,R.column_no,R.rule_def from MB_TEMPLATE T,MB_TEMPLATE_VERSION V,MB_TEMPLATE_DEPLOY_RULE R where"
			+ " R.rule_def <> '' and T.template_id=V.template_id and T.template_id=R.template_id order by T.template_id,R.column_no";

	private static final String Q_UPDATE_TEMPLATE_RULE = "update MB_TEMPLATE_DEPLOY_RULE set rule_def=? where template_id=? and column_no=?";

	private static TemplateRepairWorker instance = null;

	private static boolean hasReference(String source, String className, String attributeName) {
		if (source == null || source.trim().length() == 0) return false;

		int ci = source.indexOf("<Class>" + className + "</Class>");
		int ai = source.indexOf("<Attribute>" + attributeName + "</Attribute>");
		if (ci > 0 && ai > 0 && ai > ci + 15 + className.length()) {
			String tmp = source.substring(ci + 15 + className.length(), ai);
			return tmp.trim().length() < 1 || tmp.indexOf("<Class>") < 1;
		}
		else {
			return false;
		}
	}

	private static class ReferenceReplacer {

		private final String oldClass;
		private final String oldAttribute;
		private final String newClass;
		private final String newAttribute;

		ReferenceReplacer(String oldClass, String oldAttribute, String newClass, String newAttribute) {
			this.oldClass = oldClass;
			this.oldAttribute = oldAttribute;
			this.newClass = newClass;
			this.newAttribute = newAttribute;
			System.out.println("ReferenceReplacer<init>: " + oldClass + '.' + oldAttribute + "->" + newClass + '.' + newAttribute);
		}

		public String replaceReference(String source) throws SAXException, IOException, ParserConfigurationException {
			RuleDefinition ruleDefinition = RuleDefinitionUtil.parseToRuleDefinition(source);

			System.out.println("... replaceReference: parsed to " + ruleDefinition);

			replaceReference(ruleDefinition);

			return RuleDefinitionUtil.toString(ruleDefinition);
		}

		private void replaceReference(RuleDefinition ruleDefinition) {
			replaceReference(ruleDefinition.getRootElement());
			replaceReference(ruleDefinition.getRuleAction());
		}

		private void replaceReference(RuleAction action) {
			for (int i = 0; i < action.size(); ++i) {
				replaceReference((FunctionParameter) action.get(i));
			}
		}

		private void replaceReference(FunctionParameter param) {
			if (param instanceof Reference) {
				replaceReference((Reference) param);
			}
		}
		
		private void replaceReference(Reference reference) {
			System.out.println(">>> TemplateRepairWorker.replaceReference: reference = " + reference);
			if (reference.getClassName().equals(this.oldClass) && reference.getAttributeName().equals(this.oldAttribute)) {
				reference.setClassName(newClass);
				reference.setAttributeName(newAttribute);
				System.out.println("... TemplateRepairWorker.replaceReference: after update = " + reference);
			}
		}

		private void replaceReference(CompoundLHSElement compoundElement) {
			for (int i = 0; i < compoundElement.size(); i++) {
				RuleElement element = compoundElement.get(i);
				if (element instanceof CompoundLHSElement) {
					replaceReference((CompoundLHSElement) element);
				}
				else if (element instanceof Condition) {
					replaceReference((Condition) element);
				}
				else if (element instanceof ExistExpression) {
					replaceReference((ExistExpression) element);
				}
				else if (element instanceof Reference) {
					replaceReference((Reference) element);
				}
			}
		}

		private void replaceReference(Condition condition) {
			replaceReference(condition.getReference());
			replaceReference(condition.getValue());
		}

		private void replaceReference(ExistExpression exist) {
			replaceReference(exist.getCompoundLHSElement());
		}

		private void replaceReference(Value value) {
			if (value instanceof Reference) {
				replaceReference((Reference) value);
			}
		}
	}

	/**
	 * Gets the one and only instance of this class.
	 * @return the only instance
	 */
	public static TemplateRepairWorker getInstance() {
		if (instance == null) {
			instance = new TemplateRepairWorker();
		}
		return instance;
	}

	private final Logger logger;
	private boolean cacheLoaded = false;

	private TemplateRepairWorker() {
		logger = Logger.getLogger(getClass().getName());
	}

	private synchronized void initServerCache(Connection conn) throws SQLException, ParseException {
		if (!cacheLoaded) {
			// initialize usage types
			loadUsageTypesForActions(conn);

			GuidelineFunctionManager.getInstance();
			GuidelineActionLoader.getInstance().load(conn);
			cacheLoaded = true;
			System.out.println("Guideline actions loaded");
		}
	}

	private void loadUsageTypesForActions(Connection conn) throws SQLException {
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = conn.prepareStatement("select distinct usage_type from MB_GUIDELINE_ACTION_USAGE");
			rs = ps.executeQuery();
			while (rs.next()) {
				String usageStr = DBUtil.getStringValue(rs, 1);
				TemplateUsageType.createInstance(usageStr, usageStr, usageStr);
			}
		}
		finally {
			if (rs != null) rs.close();
			if (ps != null) ps.close();
		}
	}

	public synchronized TemplateColumnRuleInfo[] retrieveTemplateColumRulesToUpdate(String className, String attributeName, DBConnInfo connInfo)
			throws SQLException, InvalidSpecException {
		logger.fine(">>> retrieveTemplateColumnRulesToUpdate: " + className + '.' + attributeName + "," + connInfo);

		List<TemplateColumnRuleInfo> list = new ArrayList<TemplateColumnRuleInfo>();

		Connection conn = null;
		try {
			conn = DBConnectionFactory.getInstance().getConnection(connInfo);

			initServerCache(conn);
		}
		catch (SQLException ex) {
			throw ex;
		}
		catch (Exception ex) {
			ex.printStackTrace();
			throw new SQLException("Error while initializing server cache: " + ex.getMessage());
		}

		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = conn.prepareStatement(Q_GET_TEMPLATE_COLUMN_RULES);
			rs = ps.executeQuery();
			while (rs.next()) {
				int templateID = rs.getInt(1);
				String name = DBUtil.getStringValue(rs, 2);
				String usage = DBUtil.getStringValue(rs, 3);
				String version = DBUtil.getStringValue(rs, 4);
				int columnNo = rs.getInt(5);
				String ruleDef = DBUtil.getStringValue(rs, 6);

				System.out.println("... retrieveTemplateColumnRulesToUpdate: processing " + templateID + "," + columnNo);
				if (hasReference(ruleDef, className, attributeName)) {
					list.add(new TemplateColumnRuleInfo(templateID, name, version, usage, columnNo, ruleDef));
				}
			}
			rs.close();
			rs = null;
			System.out.println("<<< retrieveTemplateColumnRulesToUpdate: " + list.size());
		}
		catch (SQLException ex) {
			throw ex;
		}
		catch (Exception ex) {
			ex.printStackTrace();
			throw new SQLException("Error while retrieving template/column rules: " + ex.getLocalizedMessage());
		}
		finally {
			if (rs != null) rs.close();
			if (ps != null) ps.close();
		}
		return (TemplateColumnRuleInfo[]) list.toArray(new TemplateColumnRuleInfo[0]);
	}

	public synchronized int updateTemplateColumnRules(String oldClassName, String oldAttributeName, String newClassName, String newAttributeName,
			TemplateColumnRuleInfo[] templateIdentifiers, DBConnInfo connInfo) throws SQLException, InvalidSpecException {
		System.out.println(">>> retrieveTemplateColumnRulesToUpdate: " + oldClassName + '.' + oldAttributeName + "," + newClassName + '.'
				+ newAttributeName + ',' + connInfo);

		ReferenceReplacer refReplacer = new ReferenceReplacer(oldClassName, oldAttributeName, newClassName, newAttributeName);

		Connection conn = null;
		PreparedStatement ps = null;
		try {
			conn = DBConnectionFactory.getInstance().getConnection(connInfo);
			conn.setAutoCommit(false);

			ps = conn.prepareStatement(Q_UPDATE_TEMPLATE_RULE);

			int count = 0;
			for (int i = 0; i < templateIdentifiers.length; i++) {
				// replace references in template rules
				String newRuleStr = refReplacer.replaceReference(templateIdentifiers[i].getRuleDefinition());
				if (!newRuleStr.equals(templateIdentifiers[i].getRuleDefinition())) {
					System.out.println("... retrieveTemplateColumnRulesToUpdate: updating " + templateIdentifiers[i]);
					ps.setString(1, newRuleStr);
					ps.setInt(2, templateIdentifiers[i].getID());
					ps.setInt(3, templateIdentifiers[i].getColumnID());
					if (ps.executeUpdate() < 1) { throw new SQLException("No row updated for " + templateIdentifiers[i]); }
					++count;
				}
			}
			conn.commit();
			return count;
		}
		catch (SQLException ex) {
			conn.rollback();
			throw ex;
		}
		catch (Exception ex) {
			conn.rollback();
			ex.printStackTrace();
			throw new SQLException("Error while retrieving template/column rules: " + ex.getLocalizedMessage());
		}
		finally {
			if (ps != null) ps.close();
		}
	}
}