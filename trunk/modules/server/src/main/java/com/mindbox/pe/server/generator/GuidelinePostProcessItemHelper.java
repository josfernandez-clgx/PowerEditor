/*
 * Created on 2005. 2. 16.
 *
 */
package com.mindbox.pe.server.generator;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.mindbox.pe.common.UtilBase;
import com.mindbox.pe.model.TemplateUsageType;
import com.mindbox.pe.model.domain.DomainAttribute;
import com.mindbox.pe.model.domain.DomainClass;
import com.mindbox.pe.model.domain.DomainTranslation;
import com.mindbox.pe.model.rule.Reference;
import com.mindbox.pe.model.rule.RuleElementFactory;
import com.mindbox.pe.model.table.DynamicStringValue;
import com.mindbox.pe.model.template.AbstractTemplateColumn;
import com.mindbox.pe.model.template.ColumnAttributeItemDigest;
import com.mindbox.pe.model.template.GridTemplate;
import com.mindbox.pe.model.template.GridTemplateColumn;
import com.mindbox.pe.model.template.TemplateMessageDigest;
import com.mindbox.pe.server.Util;
import com.mindbox.pe.server.cache.DomainManager;
import com.mindbox.pe.server.config.ConfigurationManager;
import com.mindbox.pe.server.generator.processor.MessageProcessor;
import com.mindbox.pe.server.generator.rule.ColumnReferencePatternValueSlot;
import com.mindbox.pe.server.generator.rule.FunctionArgument;
import com.mindbox.pe.server.generator.rule.FunctionCallPattern;
import com.mindbox.pe.server.generator.rule.GuidelineRule;
import com.mindbox.pe.server.generator.rule.StaticFunctionArgument;
import com.mindbox.pe.server.generator.rule.ValueSlot;
import com.mindbox.pe.server.parser.jtb.message.syntaxtree.CellValueLiteral;
import com.mindbox.pe.server.parser.jtb.message.syntaxtree.ColumnLiteral;
import com.mindbox.pe.server.parser.jtb.message.syntaxtree.ColumnMessagesLiteral;
import com.mindbox.pe.server.parser.jtb.message.syntaxtree.ColumnNumberList;
import com.mindbox.pe.server.parser.jtb.message.syntaxtree.Message;
import com.mindbox.pe.server.parser.jtb.message.syntaxtree.NodeSequence;
import com.mindbox.pe.server.parser.jtb.message.visitor.ObjectDepthFirst;

/**
 * Manages post processing items for guideline rule generation.
 * This is thread-safe.
 * @see com.mindbox.pe.server.generator.GuidelineRuleGenerator
 * @author Geneho Kim
 * @since PowerEditor 4.2.0
 */
final class GuidelinePostProcessItemHelper extends AbstractGuidelineHelper {

	private static final Pattern COLUMN_REF_PATTERN = Pattern.compile("%column ([0-9]+)%");

	private static final Reference createReference(String className, String attributeName) {
		return RuleElementFactory.getInstance().createReference(className, attributeName);
	}

	/**
	 * Message visitor for finding post process items.
	 * @author Geneho Kim
	 * @since PowerEditor
	 */
	private class MessageVisitor extends ObjectDepthFirst {

		private int cellValueOverride = -1;

		void extractPostProcessItems(Message messageObj, GuidelineGenerateParams ruleParams) {
			messageObj.accept(this, ruleParams);
		}

		public Object visit(CellValueLiteral n, Object argu) {
			if (!(argu instanceof GuidelineGenerateParams)) return null;
			// [gkim] it's possible that dynamic strings can be inserted into a message, but not passed into RHS. Handle it!!! 
			Object cellValue = (cellValueOverride == -1 ? ((AbstractGenerateParms) argu).getCellValue() : ((AbstractGenerateParms) argu).getColumnValue(cellValueOverride));

			if (cellValue instanceof DynamicStringValue) {
				logger.debug("MessageVisitor.visit(CellValueLiteral): processing references in dynamic string value: " + cellValue);
				addToPostProcessingItem_aux(((AbstractGenerateParms) argu).getColumnNum(), (DynamicStringValue) cellValue, (GuidelineGenerateParams) argu);
			}
			return null;
		}

		public Object visit(ColumnLiteral n, Object argu) {
			if (!(argu instanceof GuidelineGenerateParams)) return null;

			int column = Integer.parseInt(n.f1.tokenImage);
			// [gkim] it's possible that dynamic strings can be inserted into a message, but not passed into RHS. Handle it!!! 
			Object cellValue = ((AbstractGenerateParms) argu).getColumnValue(column);
			if (cellValue instanceof DynamicStringValue) {
				logger.debug("MessageVisitor.visit(ColumnLiteral): processing references in dynamic string value: " + cellValue);
				addToPostProcessingItem_aux(column, (DynamicStringValue) cellValue, (GuidelineGenerateParams) argu);
			}
			return null;
		}

		@SuppressWarnings("unchecked")
		public Object visit(ColumnMessagesLiteral n, Object argu) {
			if (!(argu instanceof GuidelineGenerateParams)) return null;

			GuidelineGenerateParams generateParams = (GuidelineGenerateParams) argu;
			logger.debug(">>> MessageVisitor.visit(ColumnMessageLiteral) for " + argu);

			// get the list of column numbers for this literal
			List<Integer> intList = (List<Integer>) n.f2.accept(this, generateParams);
			int intArray[] = Util.toIntArray(intList);

			if (intArray.length == 0) return null;

			MessageVisitor columnMessageProcessor = GuidelinePostProcessItemHelper.this.new MessageVisitor();

			// for each column, put in its message and the delimeter
			GridTemplate template = generateParams.getTemplate();

			for (int i = 0; i < intArray.length; i++) {
				int colNum = intArray[i];

				logger.debug("    MessageVisitor.visit(ColumnMessageLiteral): column no=" + colNum);

				GridTemplateColumn gridColumn = (GridTemplateColumn) template.getColumn(colNum);
				Object cellValue = generateParams.getColumnValue(colNum);
				logger.debug("    MessageVisitor.visit(ColumnMessageLiteral): cell value=" + cellValue);

				Message parsedMessage = gridColumn.getParsedMessage(cellValue);
				if (parsedMessage != null) {
					// process for references
					columnMessageProcessor.cellValueOverride = -1;
					columnMessageProcessor.cellValueOverride = colNum;
					columnMessageProcessor.extractPostProcessItems(parsedMessage, generateParams);
				}
			}
			logger.debug("<<< MessageVisitor.visit(ColumnMessageLiteral)");
			return null;
		}

		public Object visit(ColumnNumberList n, Object argu) {
			if (!(argu instanceof GuidelineGenerateParams)) return null;
			AbstractGenerateParms generateParams = (AbstractGenerateParms) argu;

			List<Integer> list = new LinkedList<Integer>();
			Integer columnNo = Integer.valueOf(n.f0.tokenImage);
			if (RuleGeneratorHelper.hasValue(generateParams.getColumnValue(columnNo.intValue()), generateParams)) {
				list.add(columnNo);
			}
			if (n.f1.present()) {
				for (int i = 0; i < n.f1.size(); i++) {
					NodeSequence seq = (NodeSequence) n.f1.elementAt(i);
					if (seq.elementAt(1) != null) {
						columnNo = Integer.valueOf(seq.elementAt(1).toString());
						if (RuleGeneratorHelper.hasValue(generateParams.getColumnValue(columnNo.intValue()), generateParams)) {
							list.add(columnNo);
						}
					}
				}
			}
			return list;
		}

		public Object visit(com.mindbox.pe.server.parser.jtb.message.syntaxtree.Reference n, Object argu) {
			if (!(argu instanceof AbstractGenerateParms)) return null;
			logger.debug("MessageVisitor.visit(reference): adding " + n);

			List<String> nameList = new LinkedList<String>();

			String imageStr = n.f1.tokenImage;
			if (imageStr != null && imageStr.length() > 0) {
				String[] args = imageStr.split("\\.");
				for (int i = 0; i < args.length; i++) {
					if (args[i] != null && args[i].length() > 0) {
						nameList.add(args[i]);
					}
				}
			}
			if (!nameList.isEmpty()) {
				String className = (String) nameList.get(0);
				if (nameList.size() == 1) {
					addToPostProcessingItem_aux(className);
				}
				else {
					for (int i = 1; i < nameList.size(); i++) {
						String attrName = (String) nameList.get(i);
						addToPostProcessingItem_aux(className, attrName);
						className = attrName;
					}
				}
			}
			return null;
		}
	}

	private final List<Reference> postProcessingItemList;

	GuidelinePostProcessItemHelper(BufferedGenerator bufferedGenerator) {
		super(bufferedGenerator);
		postProcessingItemList = new ArrayList<Reference>();
	}

	synchronized void clear() {
		postProcessingItemList.clear();
	}

	synchronized void writePatterns(GuidelineRule guidelineRule, TemplateUsageType usageType, boolean clearWhenDone) throws RuleGenerationException {
		if (!postProcessingItemList.isEmpty()) {
			logger.debug(">>>> writePatterns: size = " + postProcessingItemList.size());
			for (Iterator<Reference> iter = postProcessingItemList.iterator(); iter.hasNext();) {
				Reference item = iter.next();
				logger.debug("    writePatterns: checking " + item);

				// 1189: don't write the empty pattern, if there is an object pattern with the same attribute reference in the rule
				if (!guidelineRule.hasPatternForReference((Reference) item)) {
					writeEmptyAttributePattern((Reference) item, usageType);
					if (iter.hasNext()) {
						bufferedGenerator.nextLine();
					}
				}
				else {
					logger.info("    writePatterns: " + item + " skipped; the rule has a condition for reference");
				}
			}
		}
		if (clearWhenDone) {
			postProcessingItemList.clear();
		}
	}

	private String getClassAttributeVarName(Reference reference) throws RuleGenerationException {
		return AeMapper.getClassAttributeVarName(reference.getClassName(), reference.getAttributeName());
	}

	private void writeEmptyAttributePattern(Reference reference, TemplateUsageType usageType) throws RuleGenerationException {
		logger.debug(">>> writeEmptyAttributePattern: " + reference);

		String attribVarName = getClassAttributeVarName(reference);
		String className = reference.getClassName();

		bufferedGenerator.nextLine();

		String mappedClassName = AeMapper.mapClassName(className).toUpperCase();
		bufferedGenerator.openParan();
		bufferedGenerator.print("object ");
		bufferedGenerator.print(AeMapper.makeAEVariable(className));
		bufferedGenerator.nextLineIndent();
		bufferedGenerator.openParan();
		bufferedGenerator.print("instance-of ");
		bufferedGenerator.print(mappedClassName);
		bufferedGenerator.closeParan();
		bufferedGenerator.nextLine();

		// if prefix matches, insert lineage static pattern
		writeLineagePatternIfMatch(mappedClassName, usageType);

		if (reference.getAttributeName() != null && reference.getAttributeName().length() > 0) {
			String attribName = AeMapper.mapAttributeName(className, reference.getAttributeName()).toUpperCase();

			bufferedGenerator.openParan();
			bufferedGenerator.print(attribName);

			// append '$' only if configured to do so
			if (ConfigurationManager.getInstance().getRuleGenerationConfigHelper(usageType).getEmptyPatternConfig() != null
					&& UtilBase.asBoolean(ConfigurationManager.getInstance().getRuleGenerationConfigHelper(usageType).getEmptyPatternConfig().isAsSequence(), false)) {
				bufferedGenerator.print(" $");
			}
			else {
				bufferedGenerator.print(" ");
			}
			bufferedGenerator.print(attribVarName);
			bufferedGenerator.closeParan();
		}
		bufferedGenerator.outdent();
		bufferedGenerator.closeParan();
		bufferedGenerator.nextLine();
		logger.debug("<<< writeEmptyAttributePattern");
	}

	synchronized void processForAttributeAndClassReferencesInMessages(GuidelineGenerateParams ruleParams, int messageContextEntityID) throws RuleGenerationException {
		logger.debug(">>> processForAttributeAndClassReferencesInMessages: " + ruleParams);
		try {
			Message messageObj = RuleGeneratorHelper.parseMessage(ruleParams.findTemplateMessageDigest(messageContextEntityID));
			if (messageObj != null) {
				new MessageVisitor().extractPostProcessItems(messageObj, ruleParams);
			}
		}
		catch (Exception ex) {
			logger.error("Failed to complete finding references in messages for " + ruleParams, ex);
			throw new RuleGenerationException(ex.getMessage());
		}
	}

	synchronized void processForAttributeAndClassReferencesInRHS(FunctionCallPattern functionCallPattern, GuidelineGenerateParams ruleParams) throws RuleGenerationException {
		logger.debug(">>> processForAttributeAndClassReferencesInRHS: " + functionCallPattern + "," + ruleParams);
		if (functionCallPattern.isEmpty()) return;

		for (int i = 0; i < functionCallPattern.argSize(); i++) {
			FunctionArgument argument = functionCallPattern.getArgAt(i);
			logger.debug("... processForAttributeAndClassReferencesInRHS: processing=" + argument);
			if (argument instanceof FunctionCallPattern) {
				processForAttributeAndClassReferencesInRHS((FunctionCallPattern) argument, ruleParams);
			}
			else if (argument instanceof StaticFunctionArgument) {
				String strValue = ((StaticFunctionArgument) argument).getValue();
				if (strValue != null) {
					if (strValue.startsWith("\"|") && strValue.endsWith("|\"")) {
						// handle class or class.attr reference
						int index = strValue.indexOf(".");
						if (index == -1) {
							// get class reference
							String className = strValue.substring(2, strValue.length() - 2).toUpperCase();
							logger.debug("... processForAttributeAndClassReferencesInRHS: class ref found = " + className);

							addToPostProcessingItem_aux(className);
						}
						else {
							// get attribute reference
							String className = strValue.substring(2, index);
							String attrName = strValue.substring(index + 1, strValue.length() - 2).toUpperCase();

							logger.debug("... processForAttributeAndClassReferencesInRHS: attr ref found = " + className + '.' + attrName);
							addToPostProcessingItem_aux(className, attrName);
						}
					}
				}
			}
		}
	}

	synchronized void processForDynamicStringInMessage(GuidelineGenerateParams ruleParams, int messageContextEntityID) throws RuleGenerationException {
		// check for each column reference in the message
		TemplateMessageDigest messageDigest = ruleParams.findTemplateMessageDigest(messageContextEntityID);
		if (messageDigest != null && messageDigest.getText() != null) {
			if (logger.isDebugEnabled()) logger.debug("... processForDynamicStringInMessage: messageText = " + messageDigest.getText());
			Matcher matcher = COLUMN_REF_PATTERN.matcher(messageDigest.getText());
			while (matcher.find()) {
				try {
					if (logger.isDebugEnabled()) logger.debug("... processForDynamicStringInMessage: found = " + matcher.group());
					int columnNo = Integer.parseInt(matcher.group(1).trim());
					processForDynamicStringInRHS_aux(columnNo, ruleParams);
				}
				catch (NumberFormatException ex) {
					// ignore -- this should not happen
					logger.warn("Invalid column number: " + matcher.group(1));
				}
			}
		}
	}

	synchronized void processForDynamicStringInRHS(FunctionCallPattern functionCallPattern, GuidelineGenerateParams ruleParams) throws RuleGenerationException {
		logger.debug(">>> processForDynamicStringInRHS: paramList=" + functionCallPattern + ", ruleParams=" + ruleParams);
		if (functionCallPattern.isEmpty()) return;

		for (int i = 0; i < functionCallPattern.argSize(); i++) {
			FunctionArgument argument = functionCallPattern.getArgAt(i);
			logger.debug("... processForDynamicStringInRHS: processing=" + argument);
			if (argument instanceof FunctionCallPattern) {
				processForDynamicStringInRHS((FunctionCallPattern) argument, ruleParams);
			}
			else if (argument instanceof ValueSlot) {
				if (((ValueSlot) argument).getType() == ValueSlot.Type.COLUMN_REFERENCE) {
					processForDynamicStringInRHS_aux(((ColumnReferencePatternValueSlot) argument).getColumnNo(), ruleParams);
				}
				else if (((ValueSlot) argument).getType() == ValueSlot.Type.CELL_VALUE) {
					processForDynamicStringInRHS_aux(ruleParams.getColumnNum(), ruleParams);
				}
			}
			else if (argument instanceof StaticFunctionArgument) {
				processForDynamicStringInRHS_aux(((StaticFunctionArgument) argument).getValue(), ruleParams);
			}
		}

	}

	private void processForDynamicStringInRHS_aux(String strValue, GuidelineGenerateParams ruleParams) throws RuleGenerationException {
		logger.debug(">>> processForDynamicStringInRHS_aux: strValue=" + strValue + ", ruleParams=" + ruleParams);
		if (strValue != null && strValue.startsWith("\"%column ")) {
			try {
				int col = Integer.parseInt(strValue.substring(9, strValue.length() - 2));
				processForDynamicStringInRHS_aux(col, ruleParams);
			}
			catch (NumberFormatException ex) {
				throw new RuleGenerationException("Invalid action definition: in %column N%, N must be an integer.");
			}
		}
		else if (strValue != null && strValue.equals("\"%cellValue%\"")) {
			processForDynamicStringInRHS_aux(ruleParams.getColumnNum(), ruleParams);
		}
	}

	private void processForDynamicStringInRHS_aux(int column, GuidelineGenerateParams ruleParams) throws RuleGenerationException {
		Object colValue = ruleParams.getColumnValue(column);
		logger.debug("... processForDynamicStringInRHS_aux: colValue=" + colValue + ", ruleParams=" + ruleParams);
		if (colValue != null && colValue instanceof DynamicStringValue) {
			addToPostProcessingItem(column, (DynamicStringValue) colValue, ruleParams);
		}
	}

	/**
	 * Process for post processing item in the specified dynamic string value.
	 * This not only replaces column/cell references and domain class/attribute references in text of <code>dsValue</code>, 
	 * but also sets argument list of <code>dsValue</code>.
	 * It calls two private methods, one which processes column reference while the other processes class/attribute references.
	 * @param column
	 * @param dsValue
	 * @param ruleParams
	 * @throws RuleGenerationException
	 */
	private void addToPostProcessingItem(int column, DynamicStringValue dsValue, GuidelineGenerateParams ruleParams) throws RuleGenerationException {
		// Note: dynamic string value can only occur in RHS
		logger.debug(">>> addToPostProcessingItem: column=" + column + ", DynamicStringValue=" + dsValue + ", ruleParams=" + ruleParams);

		String tempStr = new String();
		tempStr = addToPostProcessingItem_Columns(column, dsValue, ruleParams);
		List<String> list = addToPostProcessingItem_ClassAttribute(column, dsValue, ruleParams, tempStr);
		dsValue.setDeployValues(list.toArray(new String[0]));
	}

	/**This only replaces column/cell references in text of <code>dsValue</code>.
	 * @param column
	 * @param dsValue
	 * @param ruleParams
	 * @param strAfterColumns
	 * @return String
	 * @throws RuleGenerationException
	 */
	private String addToPostProcessingItem_Columns(int column, DynamicStringValue dsValue, GuidelineGenerateParams ruleParams) throws RuleGenerationException {
		logger.debug(">>> addToPostProcessingItem_Columns: column=" + column + ", DynamicStringValue=" + dsValue + ", ruleParams=" + ruleParams);

		String text = dsValue.toString();
		Pattern pattern = Pattern.compile("%column ([0-9]+)%");
		Matcher matcher = pattern.matcher(text);

		StringBuilder buff = new StringBuilder();

		int prevIndex = 0;
		while (matcher.find()) {
			// add text upto the match
			buff.append(text.substring(prevIndex, matcher.start()));
			prevIndex = matcher.end();

			int col = Integer.parseInt(matcher.group(1));
			Object columnValue = ruleParams.getColumnValue(col);
			logger.debug("... addToPostProcessingItem: columnValue=" + columnValue + " (" + (columnValue == null ? "" : columnValue.getClass().getName()) + ")");
			if (columnValue == null) {
				buff.append(RuleGeneratorHelper.AE_NIL);
			}
			else {
				MessageProcessor.writeVal(
						ruleParams.getTemplate().getColumn(col),
						columnValue,
						buff,
						ConfigurationManager.getInstance().getRuleGenerationConfigHelper(ruleParams.getUsage()).getMessageConfig(),
						columnValue.toString());
			}
		}

		if (prevIndex == 0) {
			buff.append(text);
		}
		else if (prevIndex > 0 && prevIndex < text.length() - 1) {
			buff.append(text.substring(prevIndex));
		}

		String tempStr = buff.toString();
		logger.debug("...TempStr after columns=" + tempStr);
		return tempStr;
	}

	/** This only replaces domain class/attribute references in text of <code>dsValue</code>.
	 * @param column
	 * @param dsValue
	 * @param ruleParams
	 * @param strAfterColumns
	 * @return List
	 * @throws RuleGenerationException
	 */
	private List<String> addToPostProcessingItem_ClassAttribute(int column, DynamicStringValue dsValue, GuidelineGenerateParams ruleParams, String strAfterColumns) throws RuleGenerationException {
		logger.debug(">>> addToPostProcessingItem_ClassAttribute: column=" + column + ", DynamicStringValue=" + dsValue + ", ruleParams=" + ruleParams + ", strAfterColumns=" + strAfterColumns);

		StringBuilder buff = new StringBuilder();
		List<String> list = new ArrayList<String>();

		Pattern pattern = Pattern.compile("\\$([^\\$,\"%]+)\\$");
		Matcher matcher = pattern.matcher(strAfterColumns);

		int prevIndex = 0;
		boolean prevMatch = false;
		while (matcher.find(prevIndex)) {
			buff.append(strAfterColumns.substring(prevIndex, matcher.start()));

			String refName = matcher.group(1);
			logger.debug(".... looking for refName=" + refName);

			int index = refName.indexOf(".");// means we did not find $object.attribute$
			if (index < 0) {
				boolean found = false;
				// find in attribute item, first
				AbstractTemplateColumn templateColumn = (ruleParams.getTemplate() == null ? null : ruleParams.getTemplate().getColumn(column));
				if (templateColumn != null && templateColumn.getColumnDataSpecDigest() != null) {
					for (Iterator<ColumnAttributeItemDigest> iter = templateColumn.getColumnDataSpecDigest().getAllAttributeItems().iterator(); iter.hasNext() && !found;) {
						ColumnAttributeItemDigest element = iter.next();
						if (element.getDisplayValue() != null && element.getDisplayValue().equals(refName)) {
							found = true;
							prevMatch = true;
							String[] strs = element.getName().split("\\.");
							if (strs.length >= 2) {
								addToPostProcessingItem_aux(strs[0], strs[1], list, buff);
							}
							else {
								buff.append("*INVALID-AttributeItem-Name-" + element.getName() + "-FOR-" + refName);
							}
						}
					}
				}
				if (!found) {
					DomainTranslation dt = DomainManager.getInstance().findDomainTranslationByContextlessLabel(refName);
					if (dt != null) {
						list.add(addToPostProcessingItem(dt));
						buff.append("%a");
						prevMatch = true;
					}
					else {
						DomainAttribute da = DomainManager.getInstance().findDomainAttributeByContextlessLabel(refName);
						if (da != null) {
							DomainClass dc = DomainManager.getInstance().findDomainClassForAttribute(da);
							addAsReference(dc.getName(), da.getName());

							// convert to variable
							list.add(AeMapper.makeAEVariable(da.getName()));
							buff.append("%a");
							prevMatch = true;
						}
						else {
							// check if it's a class name
							DomainClass dc = DomainManager.getInstance().getDomainClass(refName);
							if (dc == null) {
								buff.append("$" + refName);
							}
							else {
								addToPostProcessingItem_aux(refName, null, list, buff);
								prevMatch = true;
							}
						}
					}
				}
			}
			else {//there is no '.' in the refName
				String cn = refName.substring(0, index);
				String an = refName.substring(index + 1);
				prevMatch = addToPostProcessingItem_aux(cn, an, list, buff);
			}

			// decrement index by 1 only if we did not make a match in the previous matcher.find(int)
			if (prevMatch) {
				prevIndex = matcher.end();
				prevMatch = false;
			}
			else {
				prevIndex = matcher.end() - 1;
			}
		}

		if (prevIndex == 0) {
			buff.append(strAfterColumns);
		}
		else if (prevIndex > 0 && prevIndex < strAfterColumns.length() - 1) {
			buff.append(strAfterColumns.substring(prevIndex));
		}

		list.add(0, buff.toString());
		return list;
	}

	/**
	 * Finds domain class/attribute references in the specified dynamic string value and adds them as a post processing item.
	 * Unlike {@link #addToPostProcessingItem(int, DynamicStringValue, GuidelineGenerateParams)}, 
	 * this does not modify the specified dynamic value at all.
	 * @param dsValue
	 * @param ruleParams
	 */
	private void addToPostProcessingItem_aux(int column, DynamicStringValue dsValue, GuidelineGenerateParams ruleParams) {
		logger.debug(">>> addToPostProcessingItem_aux: column=" + column + ", DynamicStringValue=" + dsValue + ", ruleParams=" + ruleParams);
		Pattern pattern = Pattern.compile("\\$([^\\$,\"%]+)\\$");
		Matcher matcher = pattern.matcher(dsValue.toString());

		while (matcher.find()) {
			String refName = matcher.group(1);
			logger.debug("addToProcessProcessingItem_aux(col,dsValue): found= " + refName);

			int index = refName.indexOf(".");
			if (index < 0) {
				boolean found = false;
				// find in attribute item, first
				AbstractTemplateColumn templateColumn = (ruleParams.getTemplate() == null ? null : ruleParams.getTemplate().getColumn(column));
				if (templateColumn != null && templateColumn.getColumnDataSpecDigest() != null) {
					for (Iterator<ColumnAttributeItemDigest> iter = templateColumn.getColumnDataSpecDigest().getAllAttributeItems().iterator(); iter.hasNext() && !found;) {
						ColumnAttributeItemDigest element = iter.next();
						if (element.getDisplayValue() != null && element.getDisplayValue().equals(refName)) {
							found = true;

							String[] strs = element.getName().split("\\.");
							if (strs.length >= 2) {
								addToPostProcessingItem_aux(strs[0], strs[1]);
							}
							else {
								logger.warn("*INVALID-AttributeItem-Name-" + element.getName() + "-FOR-" + refName);
							}
						}
					}
				}
				if (!found) {
					DomainTranslation dt = DomainManager.getInstance().findDomainTranslationByContextlessLabel(refName);
					if (dt != null) {
						try {
							addToPostProcessingItem(dt);
						}
						catch (Exception ex) {
							logger.warn("*WARNING: Failed to add domain translation " + dt + " as a post processing item; this may cause rule loading error.");
						}
					}
					else {
						DomainAttribute da = DomainManager.getInstance().findDomainAttributeByContextlessLabel(refName);
						if (da != null) {
							DomainClass dc = DomainManager.getInstance().findDomainClassForAttribute(da);
							addToPostProcessingItem_aux(dc.getName(), da.getName());
						}
						else {
							// check if it's a class name
							DomainClass dc = DomainManager.getInstance().getDomainClass(refName);
							if (dc == null) {
								logger.warn("*ERROR-NOT-FOUND-" + refName);
							}
							else {
								addToPostProcessingItem_aux(refName);
							}
						}
					}
				}
			}
			else {
				String cn = refName.substring(0, index);
				String an = refName.substring(index + 1);
				addToPostProcessingItem_aux(cn, an);
			}
		}
	}

	private void addToPostProcessingItem_aux(String className, String subName) {
		DomainClass dc = DomainManager.getInstance().getDomainClass(className);
		DomainAttribute da = DomainManager.getInstance().getDomainAttribute(className, subName);
		if (da != null) {
			addAsReference(dc.getName(), da.getName());
		}
	}

	private void addToPostProcessingItem_aux(String className) {
		DomainClass dc = DomainManager.getInstance().getDomainClass(className);
		if (dc != null) {
			addAsReference(dc.getName(), null);
		}
	}

	/**
	 * @param className
	 * @param subName
	 * @param varList
	 * @param buff
	 * @return boolean value indicating wether we matched with {@link com.mindbox.pe.model.domain.DomainClass },
	 *  {@link com.mindbox.pe.model.domain.DomainTranslation} or {@link com.mindbox.pe.model.domain.DomainAttribute}
	 * @throws RuleGenerationException
	 */
	private boolean addToPostProcessingItem_aux(String className, String subName, List<String> varList, StringBuilder buff) throws RuleGenerationException {
		logger.debug(">>> addToPostProcessingItem_aux(), className=" + className + ", subName=" + subName);
		boolean match = false;
		if (subName == null) {
			DomainClass dc = DomainManager.getInstance().getDomainClass(className);
			if (dc != null) {
				addAsReference(className, null);
				if (varList != null) varList.add(AeMapper.makeAEVariable(className));
				if (buff != null) buff.append("%a");
				match = true;
			}
			else {
				buff.append("*ERROR-NOT-FOUND-" + className);
				match = false;
			}
			return match;
		}

		DomainTranslation dt = DomainManager.getInstance().findDomainTranslationByContextlessLabel(className, subName);
		if (dt != null) {
			logger.debug("DomainTranslation found for className=" + className + ", subName=" + subName);
			String var = addToPostProcessingItem(dt);
			if (varList != null) varList.add(var);
			if (buff != null) buff.append("%a");
			match = true;
		}
		else {
			DomainAttribute da = DomainManager.getInstance().findDomainAttributeByContextlessLabel(className, subName);
			if (da != null) {
				logger.debug("DomainAttribute found");
				addAsReference(className, subName);

				// convert to variable
				if (varList != null) varList.add(AeMapper.makeAEVariable(subName));
				if (buff != null) buff.append("%a");
				match = true;
			}
			else {
				logger.debug("DomainAttribute not found");
				if (buff != null) {
					buff.append("$" + className + "." + subName);// for $9.8888
				}
				match = false;
			}
		}

		return match;
	}

	/**
	 *
	 * @param dt
	 * @param ruleParams
	 * @return the attribute variable name
	 * @throws RuleGenerationException
	 */
	private String addToPostProcessingItem(DomainTranslation dt) throws RuleGenerationException {
		String linkPath = dt.getLinkPath();

		String[] strs = linkPath.split("\\.");
		if (strs.length >= 2) {
			String parentClass = strs[0];
			for (int i = 1; i < strs.length - 1; i++) {
				addAsReference(parentClass, strs[i]);

				parentClass = strs[i];
			}
			addAsReference(parentClass, strs[strs.length - 1]);
			return AeMapper.getClassAttributeVarName(parentClass, strs[strs.length - 1]);
		}
		else {
			return "";
		}
	}

	private void addAsReference(String className, String attributeName) {
		Reference ref = createReference(className, attributeName);
		if (!postProcessingItemList.contains(ref)) {
			postProcessingItemList.add(ref);
		}
	}

}