/*
 * Created on Jul 2, 2004
 *
 */
package com.mindbox.pe.server.report;

import java.io.StringReader;
import java.text.MessageFormat;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;

import com.mindbox.pe.common.config.MessageConfiguration;
import com.mindbox.pe.model.GridTemplate;
import com.mindbox.pe.model.GridTemplateColumn;
import com.mindbox.pe.model.GridValueContainable;
import com.mindbox.pe.model.exceptions.InvalidDataException;
import com.mindbox.pe.model.table.DynamicStringValue;
import com.mindbox.pe.server.Util;
import com.mindbox.pe.server.config.ConfigurationManager;
import com.mindbox.pe.server.generator.aemodel.AbstractAeValue;
import com.mindbox.pe.server.generator.processor.MessageProcessor;
import com.mindbox.pe.server.parser.jtb.message.MessageParser;
import com.mindbox.pe.server.parser.jtb.message.ParseException;
import com.mindbox.pe.server.parser.jtb.message.syntaxtree.CellValueLiteral;
import com.mindbox.pe.server.parser.jtb.message.syntaxtree.ColumnLiteral;
import com.mindbox.pe.server.parser.jtb.message.syntaxtree.ColumnMessagesLiteral;
import com.mindbox.pe.server.parser.jtb.message.syntaxtree.ColumnNumberList;
import com.mindbox.pe.server.parser.jtb.message.syntaxtree.FreeText;
import com.mindbox.pe.server.parser.jtb.message.syntaxtree.Message;
import com.mindbox.pe.server.parser.jtb.message.syntaxtree.NodeSequence;
import com.mindbox.pe.server.parser.jtb.message.syntaxtree.NodeToken;
import com.mindbox.pe.server.parser.jtb.message.syntaxtree.Reference;
import com.mindbox.pe.server.parser.jtb.message.syntaxtree.RuleNameLiteral;
import com.mindbox.pe.server.parser.jtb.message.syntaxtree.SingleFreeChar;
import com.mindbox.pe.server.parser.jtb.message.visitor.ObjectDepthFirst;


/**
 * Message Processor.
 * @author Geneho
 * @since PowerEditor 3.3.0
 */
class MessageReportGenerator extends ObjectDepthFirst {

	private static String generateMessageText(DynamicStringValue dsValue, MessageReportGenerator source, GridValueContainable grid)
			throws ParseException {
		if (dsValue == null || dsValue.toString() == null || dsValue.toString().length() == 0) { return ""; }

		MessageParser messageParser = new MessageParser(new StringReader((dsValue.toString())));
		Message messageObj = messageParser.Message();

		MessageReportGenerator messageProcessor = new MessageReportGenerator(source.replaceColumnReference);

		return messageProcessor.process(messageObj, grid, source.template, source.row, source.column);
	}


	private static final String RULE_NAME = "RULE-NAME";


	private final Logger logger = Logger.getLogger(getClass());
	private final boolean replaceColumnReference;
	private StringBuffer buff = null;
	private int cellValueOverride = -1;
	private int row = 1;
	private int column = -1;
	private GridTemplate template = null;
	private boolean useFormattingInLine = false;

	/**
	 * 
	 * @param replaceColumnReference
	 * @since PowerEditor 4.1.1
	 */
	public MessageReportGenerator(boolean replaceColumnReference) {
		this.replaceColumnReference = replaceColumnReference;
		this.buff = new StringBuffer();
	}

	public void setUseFormattingInLine(boolean useFormattingInLine) {
		this.useFormattingInLine = useFormattingInLine;
	}

	private void reset(GridTemplate template, int row, int column) {
		buff = new StringBuffer();
		//argList.clear();
		//emptyPatternList.clear();
		this.template = template;
		this.row = row;
		this.column = column;
		cellValueOverride = -1;
	}

	/**
	 * Builds the message output for the specified message object and the grid for guideline reports.
	 * @param messageObj the message object
	 * @param grid grid
	 * @param template the template
	 * @param row the row
	 * @param column the column; pass <code>-1</code> if this is for template message
	 * @return the string message output
	 */
	public synchronized String process(Message messageObj, GridValueContainable grid, GridTemplate template, int row, int column) {
		if (messageObj == null) return "";

		this.template = template;
		this.row = row;
		this.column = column;

		String messageStr = process_internal(messageObj, grid);
		return messageStr;
	}

	/**
	 * This does not generate the finalized form (that is, without the sprint function call).
	 */
	private synchronized String process_internal(Message messageObj, GridValueContainable grid) {
		messageObj.accept(this, grid);

		return buff.toString();
	}

	private MessageConfiguration getMessageConfiguration() {
		MessageConfiguration config = null;
		if (column > 0) {
			if (ConfigurationManager.getInstance().getRuleGenerationConfiguration(this.template.getColumn(column).getUsageType()) != null) {
				config = ConfigurationManager.getInstance().getRuleGenerationConfiguration(this.template.getColumn(column).getUsageType()).getMessageConfig();
			}
		}
		return (config == null
				? ConfigurationManager.getInstance().getRuleGenerationConfiguration(this.template.getUsageType()).getMessageConfig()
				: config);
	}

	private String getConditionalDelimiter() {
		return ConfigurationManager.getInstance().getRuleGenerationConfiguration(template.getUsageType()).getMessageConfig().getConditionalDelimiter();
	}

	private String getConditionalFinalDelimiter() {
		return ConfigurationManager.getInstance().getRuleGenerationConfiguration(template.getUsageType()).getMessageConfig().getConditionalFinalDelimiter();
	}

	private void append(String str) {
		if (str != null) {
			buff.append(str);
		}
	}

	private void appendAsSpan(Object value, String className) {
		if (useFormattingInLine) {
			String valueStr = (value == null ? "" : value.toString());
			MessageFormat messageFormat = (MessageFormat) RuleDefinitionReportWriter.inLineFormattingMap.get(className);
			if (messageFormat == null) {
				append(valueStr);
			}
			else {
				append(messageFormat.format(new Object[] { valueStr}));
			}
		}
		else {
			append("<span class='");
			append(className);
			append("'>");
			append((value == null ? "" : value.toString()));
			append("</span>");
		}
	}

	private void appendReference(String op) {
		appendAsSpan(op, "refinmsg");
	}

	private boolean hasValue(Object value, GridValueContainable grid) {
		if (value instanceof AbstractAeValue) {
			return hasValue((AbstractAeValue) value, grid);
		}
		else if (value instanceof String) {
			return (value != null && ((String) value).length() > 0);
		}
		else {
			return value != null;
		}
	}

	public Object visit(CellValueLiteral n, Object argu) {
		if (!(argu instanceof GridValueContainable)) return null;

		if (replaceColumnReference) {
			try {
				int colToUse = (cellValueOverride == -1 ? column : cellValueOverride);
				MessageProcessor.writeVal(
						template.getColumn(colToUse),
						((GridValueContainable) argu).getCellValueObject(row, colToUse, null),
						buff,
						getMessageConfiguration(),
						"");
			}
			catch (InvalidDataException ex) {
				logger.error("Invalid date encountered", ex);
				throw new RuntimeReportException("Invalid date encountered: " + ex.getMessage());
			}
		}
		else {
			append("Column '");
			append(template.getColumn((cellValueOverride == -1 ? column : cellValueOverride)).getTitle());
			append("'");
		}

		return null;
	}

	public Object visit(ColumnLiteral n, Object argu) {
		if (!(argu instanceof GridValueContainable)) return null;

		int column = Integer.parseInt(n.f1.tokenImage);
		if (template.getColumn(column) == null) {
			logger.warn("Invalid column reference " + column + "; column doesn't exist for template " + template.getId());
			appendAsSpan("Invalid Column Reference: " + column, "error");
			return null;
		}
		if (replaceColumnReference) {
			try {
				Object value = ((GridValueContainable) argu).getCellValueObject(this.row, column, null);
				if (value instanceof DynamicStringValue) {
					buff.append(generateMessageText((DynamicStringValue) value, this, (GridValueContainable) argu));
				}
				else {
					MessageProcessor.writeVal(template.getColumn(column), value, buff, getMessageConfiguration(), "");
				}
			}
			catch (InvalidDataException ex) {
				logger.error("Invalid data encountered", ex);
				throw new RuntimeReportException("Invalid data encountered: " + ex.getMessage());
			}
			catch (ParseException e) {
				logger.warn("Parse Error for " + column + " of template " + template.getID(), e);
			}
		}
		else {
			appendReference("&lt;Column '");
			appendReference(template.getColumn(column).getTitle());
			appendReference("'&gt;");
		}

		return null;
	}

	public Object visit(RuleNameLiteral n, Object argu) {
		if (!(argu instanceof GridValueContainable)) return null;

		buff.append(RULE_NAME);
		return null;
	}

	@SuppressWarnings("unchecked")
	public Object visit(ColumnMessagesLiteral n, Object argu) {
		if (!(argu instanceof GridValueContainable)) return null;

		GridValueContainable grid = (GridValueContainable) argu;

		List<Integer> intList = (List<Integer>) n.f2.accept(this, grid);
		int intArray[] = Util.toIntArray(intList);

		if (intArray.length == 0) return null;

		MessageReportGenerator columnMessageProcessor = new MessageReportGenerator(this.replaceColumnReference);

		// for each column, put in its message and the delimeter
		for (int i = 0; i < intArray.length; i++) {
			if (i > 0 && i < intArray.length - 1) {
				String condDelim = getConditionalDelimiter();
				buff.append((condDelim == null ? MessageConfiguration.DEFAULT_CONDITIONAL_DELIMITER : condDelim));
			}
			else if (i == intArray.length - 1 && i != 0) {
				String condFinalDelim = getConditionalFinalDelimiter();
				buff.append((condFinalDelim == null ? MessageConfiguration.DEFAULT_CONDITIONAL_FINAL_DELIMITER : condFinalDelim));
			}

			int colNum = intArray[i];

			GridTemplateColumn gridColumn = (GridTemplateColumn) template.getColumn(colNum);
			Object cellValue;
			try {
				cellValue = grid.getCellValueObject(row, colNum, null);
				Message parsedMessage = gridColumn.getParsedMessage(cellValue);
				if (parsedMessage == null) {
					if (replaceColumnReference) {
						logger.warn("    visit(ColumnMessageLiteral): no column message found - adding column value: "
								+ grid.getCellValueObject(row, colNum, null));
						MessageProcessor.writeVal(gridColumn, grid.getCellValueObject(row, colNum, null), buff, getMessageConfiguration(), "");
					}
					else {
						appendReference("&lt;Column '");
						appendReference(gridColumn.getTitle());
						appendReference("'&gt;");
					}
				}
				else {
					columnMessageProcessor.reset(this.template, this.row, this.column);
					columnMessageProcessor.cellValueOverride = colNum;
					String columnMessageStr = columnMessageProcessor.process_internal(parsedMessage, grid);
					buff.append(columnMessageStr);
				}
			}
			catch (InvalidDataException ex) {
				logger.error("Invalid date encountered", ex);
				throw new RuntimeReportException("Invalid date encountered: " + ex.getMessage());
			}
		}
		return null;
	}

	public Object visit(ColumnNumberList n, Object argu) {
		if (!(argu instanceof GridValueContainable)) return null;
		GridValueContainable grid = (GridValueContainable) argu;

		List<Integer> list = new LinkedList<Integer>();
		Integer columnNo = Integer.valueOf(n.f0.tokenImage);
		try {
			if (hasValue(grid.getCellValueObject(this.row, columnNo.intValue(), null), grid)) {
				list.add(columnNo);
			}
		}
		catch (InvalidDataException ex) {
			logger.error("Invalid date encountered", ex);
			throw new RuntimeReportException("Invalid date encountered: " + ex.getMessage());
		}
		if (n.f1.present()) {
			for (int i = 0; i < n.f1.size(); i++) {
				NodeSequence seq = (NodeSequence) n.f1.elementAt(i);
				if (seq.elementAt(1) != null) {
					columnNo = Integer.valueOf(seq.elementAt(1).toString());
					try {
						if (hasValue(grid.getCellValueObject(this.row, columnNo.intValue(), null), grid)) {
							list.add(columnNo);
						}
					}
					catch (InvalidDataException ex) {
						logger.error("Invalid date encountered", ex);
						throw new RuntimeReportException("Invalid date encountered: " + ex.getMessage());
					}
				}
			}
		}
		return list;
	}

	public Object visit(Reference n, Object argu) {
		if (!(argu instanceof GridValueContainable)) return null;

		List<String> nameList = new LinkedList<String>();

		// '.' no longer used as token delimiter. Split image on '.'
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
			Collections.reverse(nameList);
			appendReference("&lt;");
			for (Iterator<String> iter = nameList.iterator(); iter.hasNext();) {
				String element = iter.next();
				appendReference(element);
				if (iter.hasNext()) {
					appendReference(" of ");
				}
			}
			appendReference("&gt;");
		}

		return null;
	}

	public Object visit(FreeText n, Object argu) {
		buff.append(n.f0.tokenImage);
		return null;
	}

	public Object visit(SingleFreeChar n, Object argu) {
		buff.append(((NodeToken) n.f0.choice).tokenImage);
		return null;
	}

}