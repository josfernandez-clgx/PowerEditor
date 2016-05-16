/*
 * Created on 2004. 2. 10.
 *
 */
package com.mindbox.pe.server;

import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.mindbox.pe.common.TemplateUtil;
import com.mindbox.pe.common.XmlUtil;
import com.mindbox.pe.model.rule.ActionTypeDefinition;
import com.mindbox.pe.model.rule.ColumnReference;
import com.mindbox.pe.model.rule.CompoundLHSElement;
import com.mindbox.pe.model.rule.Condition;
import com.mindbox.pe.model.rule.ExistExpression;
import com.mindbox.pe.model.rule.FunctionParameter;
import com.mindbox.pe.model.rule.Reference;
import com.mindbox.pe.model.rule.RuleAction;
import com.mindbox.pe.model.rule.RuleDefinition;
import com.mindbox.pe.model.rule.RuleElement;
import com.mindbox.pe.model.rule.RuleElementFactory;
import com.mindbox.pe.model.rule.TestCondition;
import com.mindbox.pe.model.rule.TestTypeDefinition;
import com.mindbox.pe.model.rule.Value;
import com.mindbox.pe.model.template.GridTemplate;
import com.mindbox.pe.model.template.GridTemplateColumn;
import com.mindbox.pe.server.cache.DomainManager;
import com.mindbox.pe.server.cache.GuidelineFunctionManager;

/**
 * RuleDefinition to String converter.
 * @author Geneho Kim
 * @author MindBox
 * @since PowerEditor 4.0
 */
public final class RuleDefinitionUtil {

	private static class Producer {

		private final Logger logger = Logger.getLogger(getClass());

		private void addColumn(GridTemplate template, Condition condition, List<String> valueList) {
			int columnNo = template.getNumColumns() + 1;
			GridTemplateColumn column = TemplateUtil.generateColumnsFromAttribute(
					DomainManager.getInstance(),
					template.getUsageType(),
					condition.getReference().getClassName() + "." + condition.getReference().getAttributeName(),
					condition.getOp(),
					columnNo);

			template.addGridTemplateColumn(column);
			// add value
			logger.debug("... addColumn: adding to valueList: " + condition.getValue().toString());
			valueList.add(condition.getValue().toString());

			condition.setValue(RuleElementFactory.getInstance().createValue(RuleElementFactory.getInstance().createColumnReference(columnNo)));

			logger.debug("... addColumn: set value of condition to " + condition.getValue());
		}

		private void addColumns(GridTemplate template, CompoundLHSElement compoundElement, List<String> valueList) {
			for (int i = 0; i < compoundElement.size(); i++) {
				RuleElement element = compoundElement.get(i);
				if (element instanceof Condition) {
					addColumn(template, (Condition) element, valueList);
				}
				else if (element instanceof CompoundLHSElement) {
					addColumns(template, (CompoundLHSElement) element, valueList);
				}
			}
		}

		private void addColumns(GridTemplate template, RuleAction ruleAction, List<String> valueList) {
			logger.debug(">>> addColumns for " + ruleAction);
			List<FunctionParameter> newParamList = new LinkedList<FunctionParameter>();
			for (int i = 0; i < ruleAction.size(); i++) {
				int columnNo = template.getNumColumns() + i + 1;
				logger.debug("... addColumns: columNo = " + columnNo);

				FunctionParameter oldParam = (FunctionParameter) ruleAction.get(i);
				FunctionParameter newParam = RuleElementFactory.getInstance().createFunctionParameter(
						oldParam.index(),
						oldParam.toDisplayName(),
						RuleElementFactory.getInstance().createColumnReference(columnNo));
				newParamList.add(newParam);

				logger.debug("... addColumns: oldParam = " + oldParam);
				logger.debug("... addColumns: newParam = " + newParam);
				logger.debug("... addColumns: adding to valueList: " + oldParam.valueString());

				valueList.add(oldParam.valueString());
			}
			ruleAction.removeAll();
			for (Iterator<FunctionParameter> iter = newParamList.iterator(); iter.hasNext();) {
				ruleAction.add(iter.next());
			}

			logger.debug("... addColumns: Action after processing: " + ruleAction);
			for (int i = 0; i < ruleAction.size(); i++) {
				logger.debug("... addColumns: action param " + i + " = " + ruleAction.get(i));
			}

			TemplateUtil.generateAndAddColumns(template, ruleAction.getActionType());
			logger.debug("<<< addColumns");
		}

		void generateColumns(GridTemplate template, RuleDefinition ruleDef, List<String> valueList) {
			logger.debug(">>> generateColumns: " + template + "," + ruleDef + "," + valueList);
			addColumns(template, ruleDef.getRootElement(), valueList);
			RuleAction tempRuleAction = ruleDef.getRuleAction();
			addColumns(template, tempRuleAction, valueList);

			ruleDef.updateAction(tempRuleAction);

			logger.debug("<<< generateColumns");
		}
	}

	/**
	 * Equivalent to <code>parseToRuleDefinition(ruleDefStr, null)</code>.
	 * @param ruleDefStr ruleDefStr
	 * @return rule definition
	 * @throws SAXException on error
	 * @throws IOException on error
	 * @throws ParserConfigurationException on error
	 */
	public static RuleDefinition parseToRuleDefinition(String ruleDefStr) throws SAXException, IOException, ParserConfigurationException {
		return parseToRuleDefinition(ruleDefStr, null);
	}

	/**
	 * 
	 * @param ruleDefStr ruleDefStr
	 * @param actionIDMap <code>null</code> or a valid map of "action:"&lt;integer&gt; to Integer for merge import
	 * @return rule definition
	 * @throws SAXException on error
	 * @throws IOException on error
	 * @throws ParserConfigurationException on error
	 */
	public static RuleDefinition parseToRuleDefinition(String ruleDefStr, Map<String, Integer> actionIDMap) throws SAXException, IOException, ParserConfigurationException {
		if (ruleDefStr == null || ruleDefStr.trim().length() == 0) return null;
		return new RuleDefinitionUtil(actionIDMap).convertToRuleDefinition(ruleDefStr.trim());
	}

	public static void produceTemplateColumns(GridTemplate template, RuleDefinition ruleDef, List<String> valueList) {
		new Producer().generateColumns(template, ruleDef, valueList);
	}

	public static String toString(RuleDefinition ruleDefinition) {
		if (ruleDefinition == null) return "";
		return RuleDefinitionStringWriter.writeAsString(ruleDefinition);
	}

	private final Logger logger = Logger.getLogger(getClass());
	private final Map<String, Integer> actionIDMap = new HashMap<String, Integer>();

	private RuleDefinitionUtil(Map<String, Integer> actionIDMap) {
		if (actionIDMap != null) {
			this.actionIDMap.putAll(actionIDMap);
		}
	}

	private void addLHSElements(CompoundLHSElement lshCompoundElement, Element compoundElement) {
		NodeList nodeList = compoundElement.getChildNodes();
		for (int i = 0; i < nodeList.getLength(); i++) {
			if (nodeList.item(i) instanceof Element) {
				Element lhsElement = (Element) nodeList.item(i);
				if (lhsElement.getTagName().equals("AND")) {
					lshCompoundElement.add(asAnd(lhsElement));
				}
				else if (lhsElement.getTagName().equals("OR")) {
					lshCompoundElement.add(asOr(lhsElement));
				}
				else if (lhsElement.getTagName().equals("NOT")) {
					lshCompoundElement.add(asNot(lhsElement));
				}
				else if (lhsElement.getTagName().equals("Condition")) {
					lshCompoundElement.add(asCondition(lhsElement));
				}
				else if (lhsElement.getTagName().equals("Exist")) {
					lshCompoundElement.add(asExistExpression(lhsElement));
				}
				else if (lhsElement.getTagName().equals("TestCondition")) {
					lshCompoundElement.add(asTestCondition(lhsElement));
				}
			}
		}
	}

	private CompoundLHSElement asAnd(Element element) {
		CompoundLHSElement compoundElement = RuleElementFactory.getInstance().createAndCompoundCondition();
		compoundElement.setComment(XmlUtil.getValueOfFirstChild(element, "Comment"));

		addLHSElements(compoundElement, element);
		return compoundElement;
	}

	private ColumnReference asColumnReference(Element element) {
		String columnStr = element.getAttribute("columnNo");
		try {
			return RuleElementFactory.getInstance().createColumnReference(Integer.parseInt(columnStr));
		}
		catch (Exception ex) {
			logger.warn("Failed to create column reference from " + element, ex);
			throw new IllegalArgumentException("Invalid column no: " + columnStr + " at " + element);
		}
	}

	private Condition asCondition(Element element) {
		Condition condition = RuleElementFactory.getInstance().createCondition();
		condition.setComment(XmlUtil.getValueOfFirstChild(element, "Comment"));
		condition.setOp(Condition.Aux.toOpInt(XmlUtil.getValueOfFirstChild(element, "Operator")));
		condition.setReference(asReference(XmlUtil.getFirstChild(element, "Reference")));
		condition.setValue(asValue(XmlUtil.getFirstChild(element, "Value")));
		condition.setObjectName(XmlUtil.getValueOfFirstChild(element, "ObjectName", null));
		return condition;
	}

	private ExistExpression asExistExpression(Element element) {
		String value = element.getAttribute("class");
		ExistExpression expression = RuleElementFactory.getInstance().createExistExpression(value);
		value = element.getAttribute("objectName");
		if (!Util.isEmpty(value)) {
			expression.setObjectName(value);
		}
		value = element.getAttribute("excludedObjectName");
		if (!Util.isEmpty(value)) {
			expression.setExcludedObjectName(value);
		}

		addLHSElements(expression.getCompoundLHSElement(), element);

		expression.setComment(XmlUtil.getValueOfFirstChild(element, "Comment"));

		return expression;
	}

	private Value asMathExpressionValue(Element element) {
		Element childElement = XmlUtil.getFirstChild(element, "ColumnRef");
		ColumnReference colRef = asColumnReference(childElement);
		String operator = XmlUtil.getValueOfFirstChild(element, "Operator");
		childElement = XmlUtil.getFirstChild(element, "Reference");
		Reference attrRef = asReference(childElement);

		return RuleElementFactory.getInstance().createValue(colRef, operator, attrRef);
	}

	private CompoundLHSElement asNot(Element element) {
		CompoundLHSElement compoundElement = RuleElementFactory.getInstance().createNotCompoundCondition();
		compoundElement.setComment(XmlUtil.getValueOfFirstChild(element, "Comment"));

		addLHSElements(compoundElement, element);
		return compoundElement;
	}

	private CompoundLHSElement asOr(Element element) {
		CompoundLHSElement compoundElement = RuleElementFactory.getInstance().createOrCompoundCondition();
		compoundElement.setComment(XmlUtil.getValueOfFirstChild(element, "Comment"));

		addLHSElements(compoundElement, element);
		return compoundElement;
	}

	private Reference asReference(Element element) {
		Reference ref = RuleElementFactory.getInstance().createReference(XmlUtil.getValueOfFirstChild(element, "Class"), XmlUtil.getValueOfFirstChild(element, "Attribute"));
		return ref;
	}

	private TestCondition asTestCondition(Element actionElement) {
		logger.debug(">>> in asTestCondition()");
		TestCondition test = RuleElementFactory.getInstance().createTestCondition();

		String typeStr = XmlUtil.getValueOfFirstChild(actionElement, "Type");
		if (typeStr != null && typeStr.length() > 0) {
			int typeID = findMappedTestConditionID(Integer.parseInt(typeStr));

			TestTypeDefinition testTypeDef = GuidelineFunctionManager.getInstance().getTestTypeDefinition(typeID);

			if (testTypeDef == null) {
				logger.warn("TestCondition type of " + typeID + " not found");
				logger.warn("Parameters will not be loaded");
			}
			else {
				test.setTestType(testTypeDef);

				Element paramListElement = XmlUtil.getFirstChild(actionElement, "ParameterList");
				if (paramListElement != null) {
					NodeList paramList = paramListElement.getElementsByTagName("Parameter");
					for (int i = 0; i < paramList.getLength(); i++) {
						Element paramElement = (Element) paramList.item(i);
						int paramIndex = Integer.parseInt(paramElement.getAttribute("index"));
						String paramName = (testTypeDef.getParameterDefinitionAt(paramIndex) == null ? "" : testTypeDef.getParameterDefinitionAt(paramIndex).getName());

						FunctionParameter param = RuleElementFactory.getInstance().createFunctionParameterFromXMLStr(paramIndex, paramName, paramElement.getAttribute("value"));
						logger.debug(">> Created Parameter: " + paramName + " " + param.valueString() + " " + param.getClass().getName());
						test.add(param);
					}
				}
			}
		}

		String comment = XmlUtil.getValueOfFirstChild(actionElement, "Comment");
		test.setComment(comment);

		logger.debug("asTestCondition() => " + test);
		return test;
	}

	private Value asValue(Element element) {
		Element childElement = XmlUtil.getFirstChild(element, "MathExpression");
		if (childElement != null) {
			return asMathExpressionValue(childElement);
		}
		childElement = XmlUtil.getFirstChild(element, "Reference");
		if (childElement != null) {
			return RuleElementFactory.getInstance().createValue(asReference(childElement));
		}
		childElement = XmlUtil.getFirstChild(element, "ColumnRef");
		if (childElement != null) {
			return RuleElementFactory.getInstance().createValue(asColumnReference(childElement));
		}
		return RuleElementFactory.getInstance().createValue(XmlUtil.getValue(element));
	}

	private RuleDefinition convertToRuleDefinition(String ruleDefStr) throws SAXException, IOException, ParserConfigurationException {
		final Document document = XmlUtil.parseAsDomDocument(new StringReader(ruleDefStr), true);
		Element rootElement = document.getDocumentElement();
		return readToRule(rootElement);
	}

	private int findMappedActionID(int actionID) {
		if (actionIDMap == null || actionIDMap.isEmpty()) return actionID;
		Integer mappedInt = actionIDMap.get("action:" + actionID);
		return (mappedInt == null ? actionID : mappedInt.intValue());
	}

	private int findMappedTestConditionID(int actionID) {
		if (actionIDMap == null || actionIDMap.isEmpty()) return actionID;
		Integer mappedInt = actionIDMap.get("test:" + actionID);
		return (mappedInt == null ? actionID : mappedInt.intValue());
	}

	private CompoundLHSElement readToRootCondition(Element condElement) {
		CompoundLHSElement rootCondition = RuleElementFactory.getInstance().createAndCompoundCondition();

		if (condElement != null) {
			addLHSElements(rootCondition, condElement);
		}
		return rootCondition;
	}

	private RuleDefinition readToRule(Element ruleElement) {
		int id = Integer.parseInt(ruleElement.getAttribute("id"));
		String name = ruleElement.getAttribute("name");

		String desc = XmlUtil.getValueOfFirstChild(ruleElement, "Description");

		CompoundLHSElement rootCondition = null;
		Element lhsElement = XmlUtil.getFirstChild(ruleElement, "LHS");
		if (lhsElement != null) {
			rootCondition = readToRootCondition(XmlUtil.getFirstChild(lhsElement, "AND"));
		}
		else {
			rootCondition = RuleElementFactory.getInstance().createAndCompoundCondition();
		}

		RuleAction action = readToRuleAction(XmlUtil.getFirstChild(ruleElement, "Action"));

		RuleDefinition rule = new RuleDefinition(id, name, desc, rootCondition, action);
		return rule;
	}

	private RuleAction readToRuleAction(Element actionElement) {
		RuleAction action = RuleElementFactory.getInstance().createRuleAction();

		String typeStr = XmlUtil.getValueOfFirstChild(actionElement, "Type");
		if (typeStr != null && typeStr.length() > 0) {
			int typeID = findMappedActionID(Integer.parseInt(typeStr));
			ActionTypeDefinition actionTypeDef = GuidelineFunctionManager.getInstance().getActionTypeDefinition(typeID);

			if (actionTypeDef == null) {
				logger.warn("Action type of " + typeID + " not found");
				logger.warn("Parameters will not be loaded");
			}
			else {
				action.setActionType(actionTypeDef);

				Element paramListElement = XmlUtil.getFirstChild(actionElement, "ParameterList");
				if (paramListElement != null) {
					NodeList paramList = paramListElement.getElementsByTagName("Parameter");
					FunctionParameter[] fps = new FunctionParameter[paramList.getLength()];
					for (int i = 0; i < paramList.getLength(); i++) {
						Element paramElement = (Element) paramList.item(i);
						int paramIndex = Integer.parseInt(paramElement.getAttribute("index"));
						String paramName = (actionTypeDef.getParameterDefinitionAt(paramIndex) == null ? "" : actionTypeDef.getParameterDefinitionAt(paramIndex).getName());

						FunctionParameter param = RuleElementFactory.getInstance().createFunctionParameterFromXMLStr(paramIndex, paramName, paramElement.getAttribute("value"));
						fps[paramIndex - 1] = param;
					}
					// make sure they are added in proper order
					for (int i = 0; i < paramList.getLength(); i++) {
						action.add(fps[i]);
					}
				}
			}
		}

		String comment = XmlUtil.getValueOfFirstChild(actionElement, "Comment");
		action.setComment(comment);
		return action;
	}
}