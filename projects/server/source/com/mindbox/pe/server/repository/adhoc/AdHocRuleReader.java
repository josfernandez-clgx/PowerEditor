package com.mindbox.pe.server.repository.adhoc;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.mindbox.pe.common.PowerEditorXMLParser;
import com.mindbox.pe.model.rule.FunctionParameter;
import com.mindbox.pe.model.rule.ActionTypeDefinition;
import com.mindbox.pe.model.rule.CompoundLHSElement;
import com.mindbox.pe.model.rule.Condition;
import com.mindbox.pe.model.rule.Reference;
import com.mindbox.pe.model.rule.RuleAction;
import com.mindbox.pe.model.rule.RuleDefinition;
import com.mindbox.pe.model.rule.RuleElementFactory;
import com.mindbox.pe.model.rule.Value;
import com.mindbox.pe.server.cache.GuidelineFunctionManager;

/**
 * Reader for Ad-Hoc Rule repository file.
 * Behavior of this class after it has been closed is not defined.
 * @author Geneho Kim
 * @author MindBox
 * @since PowerEditor 2.3.0
 */
final class AdHocRuleReader {

	private final Logger logger = Logger.getLogger(getClass());
	private RandomAccessFile raf = null;

	/**
	 * 
	 */
	public AdHocRuleReader(File file) throws FileNotFoundException {
		raf = new RandomAccessFile(file, "rw");
	}

	public synchronized RuleDefinition[] readRules() throws SAXException, IOException, ParserConfigurationException {
		logger.debug(">>> readRules");

		// lock the file before reading it
		FileChannel fileChannel = raf.getChannel();

		assert (fileChannel.size() < Integer.MAX_VALUE) : "PowerEditor does not support files of size greater than " + Integer.MAX_VALUE;

		FileLock fileLock = fileChannel.lock();

		logger.debug("readRules: file locked: " + fileLock);

		try {
			List<RuleDefinition> ruleList = new ArrayList<RuleDefinition>();

			Document document = PowerEditorXMLParser.getInstance().loadXML(Channels.newReader(fileChannel, "UTF-8"));
			Element rootElement = document.getDocumentElement();

			logger.debug("readRules: XML parsed. processing elements...");

			NodeList nodeList = rootElement.getElementsByTagName("Rule");
			for (int i = 0; i < nodeList.getLength(); i++) {
				RuleDefinition rule = readToRule((Element) nodeList.item(i));
				logger.debug("readRule: adding " + rule);
				ruleList.add(rule);
			}

			logger.debug("<<< readRules: # of rules = " + ruleList.size());
			return ruleList.toArray(new RuleDefinition[0]);
		}
		finally {
			try {
				fileLock.release();
				fileChannel.close();
			}
			catch (Exception e) {
				logger.warn("Failed to close channel: " + fileChannel, e);
			}
		}
	}

	private RuleDefinition readToRule(Element ruleElement) {
		logger.debug(">>> readToRule: " + ruleElement);
		int id = Integer.parseInt(ruleElement.getAttribute("id"));
		String name = ruleElement.getAttribute("name");

		String desc = PowerEditorXMLParser.getValueOfFirstChild(ruleElement, "Description");

		CompoundLHSElement rootCondition = null;
		Element lhsElement = PowerEditorXMLParser.getFirstChild(ruleElement, "LHS");
		if (lhsElement != null) {
			rootCondition = readToRootCondition(PowerEditorXMLParser.getFirstChild(lhsElement, "AND"));
		}
		else {
			rootCondition = RuleElementFactory.getInstance().createAndCompoundCondition();
		}

		RuleAction action = readToRuleAction(PowerEditorXMLParser.getFirstChild(ruleElement, "Action"));

		RuleDefinition rule = new RuleDefinition(id, name, desc, rootCondition, action);

		// read messages
		NodeList messageNodes = ruleElement.getElementsByTagName("Message");
		for (int i = 0; i < messageNodes.getLength(); i++) {
			Element messageElement = (Element) messageNodes.item(i);
			rule.addMessage(messageElement.getAttribute("channel"), PowerEditorXMLParser.getValue(messageElement));
		}
		return rule;
	}

	private CompoundLHSElement readToRootCondition(Element condElement) {
		CompoundLHSElement rootCondition = RuleElementFactory.getInstance().createAndCompoundCondition();

		if (condElement != null) {
			addLHSElements(rootCondition, condElement);
		}
		return rootCondition;
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
			}
		}
	}

	private CompoundLHSElement asAnd(Element element) {
		CompoundLHSElement compoundElement = RuleElementFactory.getInstance().createAndCompoundCondition();
		compoundElement.setComment(PowerEditorXMLParser.getValueOfFirstChild(element, "Comment"));

		addLHSElements(compoundElement, element);
		return compoundElement;
	}

	private CompoundLHSElement asOr(Element element) {
		CompoundLHSElement compoundElement = RuleElementFactory.getInstance().createOrCompoundCondition();
		compoundElement.setComment(PowerEditorXMLParser.getValueOfFirstChild(element, "Comment"));

		addLHSElements(compoundElement, element);
		return compoundElement;
	}

	private CompoundLHSElement asNot(Element element) {
		CompoundLHSElement compoundElement = RuleElementFactory.getInstance().createNotCompoundCondition();
		compoundElement.setComment(PowerEditorXMLParser.getValueOfFirstChild(element, "Comment"));

		addLHSElements(compoundElement, element);
		return compoundElement;
	}

	private Condition asCondition(Element element) {
		Condition condition = RuleElementFactory.getInstance().createCondition();
		condition.setComment(PowerEditorXMLParser.getValueOfFirstChild(element, "Comment"));
		condition.setOp(Condition.Aux.toOpInt(PowerEditorXMLParser.getValueOfFirstChild(element, "Operator")));
		condition.setReference(asReference(PowerEditorXMLParser.getFirstChild(element, "Reference")));
		condition.setValue(asValue(PowerEditorXMLParser.getFirstChild(element, "Value")));

		return condition;
	}

	private Value asValue(Element element) {
		return RuleElementFactory.getInstance().createValue(PowerEditorXMLParser.getValue(element));
	}

	private Reference asReference(Element element) {
		Reference ref = RuleElementFactory.getInstance().createReference(
				PowerEditorXMLParser.getValueOfFirstChild(element, "Class"),
				PowerEditorXMLParser.getValueOfFirstChild(element, "Attribute"));
		return ref;
	}

	private RuleAction readToRuleAction(Element actionElement) {
		logger.debug(">>> readToRuleAction: " + actionElement);
		RuleAction action = RuleElementFactory.getInstance().createRuleAction();

		String typeStr = PowerEditorXMLParser.getValueOfFirstChild(actionElement, "Type");
		if (typeStr != null && typeStr.length() > 0) {
			int typeID = Integer.parseInt(typeStr);

			ActionTypeDefinition actionTypeDef = GuidelineFunctionManager.getInstance().getActionTypeDefinition(typeID);

			if (actionTypeDef == null) {
				logger.warn("Action type of " + typeID + " not found");
				logger.warn("Parameters will not be loaded");
			}
			else {
				action.setActionType(actionTypeDef);

				Element paramListElement = PowerEditorXMLParser.getFirstChild(actionElement, "ParameterList");
				if (paramListElement != null) {
					NodeList paramList = paramListElement.getElementsByTagName("Parameter");
					logger.debug("... readToRuleAction: processing " + paramList.getLength() + " parameters...");

					for (int i = 0; i < paramList.getLength(); i++) {
						Element paramElement = (Element) paramList.item(i);
						int paramIndex = Integer.parseInt(paramElement.getAttribute("index"));
						logger.debug("... readToRuleAction: index = " + paramIndex);
						String paramName = (actionTypeDef.getParameterDefinitionAt(paramIndex) == null ? "" : actionTypeDef.getParameterDefinitionAt(
								paramIndex).getName());
						FunctionParameter param = RuleElementFactory.getInstance().createFunctionParameter(
								paramIndex,
								paramName,
								paramElement.getAttribute("value"));

						action.add(param);
						logger.debug("... readToRuleAction: added: " + param);
					}
				}
			}
		}

		String comment = PowerEditorXMLParser.getValueOfFirstChild(actionElement, "Comment");
		action.setComment(comment);
		logger.debug("<<< readToRuleAction");
		return action;
	}

	public synchronized void close() throws IOException {
		try {
			if (raf != null) {
				raf.close();
			}
		}
		finally {
			raf = null;
		}
	}

	public void finalize() {
		try {
			close();
		}
		catch (Exception e) {
		}
	}
}
