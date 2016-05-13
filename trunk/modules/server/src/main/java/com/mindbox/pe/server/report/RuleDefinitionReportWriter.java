/*
 * Created on 2004. 12. 13.
 */
package com.mindbox.pe.server.report;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import com.mindbox.pe.model.DeployType;
import com.mindbox.pe.model.domain.DomainAttribute;
import com.mindbox.pe.model.domain.DomainClass;
import com.mindbox.pe.model.exceptions.InvalidDataException;
import com.mindbox.pe.model.grid.GridValueContainable;
import com.mindbox.pe.model.rule.ColumnReference;
import com.mindbox.pe.model.rule.CompoundLHSElement;
import com.mindbox.pe.model.rule.Condition;
import com.mindbox.pe.model.rule.ExistExpression;
import com.mindbox.pe.model.rule.FunctionParameter;
import com.mindbox.pe.model.rule.MathExpressionValue;
import com.mindbox.pe.model.rule.Reference;
import com.mindbox.pe.model.rule.RuleAction;
import com.mindbox.pe.model.rule.RuleDefinition;
import com.mindbox.pe.model.rule.RuleElement;
import com.mindbox.pe.model.rule.TestCondition;
import com.mindbox.pe.model.rule.Value;
import com.mindbox.pe.model.table.DateRange;
import com.mindbox.pe.model.table.DynamicStringValue;
import com.mindbox.pe.model.table.EnumValues;
import com.mindbox.pe.model.table.IRange;
import com.mindbox.pe.model.template.GridTemplate;
import com.mindbox.pe.server.cache.DomainManager;
import com.mindbox.pe.server.generator.RuleGeneratorHelper;

/**
 * Rule report writer.
 * @author kim
 * @author MindBox
 * @since PowerEditor 4.1.1
 */
public class RuleDefinitionReportWriter {

	static final String ANY_VALUE = "<font color='blue'>ANY-VALUE</font>";

	static final String IS_EMPTY = "<font color='blue'>IS EMPTY</font>";

	static final String IS_NOT_EMPTY = "<font color='blue'>IS NOT EMPTY</font>";

	static final String NEW_LINE = "<br>";

	static final Map<String, MessageFormat> inLineFormattingMap = new HashMap<String, MessageFormat>();
	static {
		inLineFormattingMap.put("action", new MessageFormat("<font color=\"blue\"><b>{0}</b></font>"));
		inLineFormattingMap.put("bracket", new MessageFormat("<font color=\"red\">{0}</font>"));
		inLineFormattingMap.put("keyword", new MessageFormat("<font color=\"black\"><b>{0}</b></font>"));
		inLineFormattingMap.put("operator", new MessageFormat("<font color=\"red\">{0}</font>"));
		inLineFormattingMap.put("parameter", new MessageFormat("<font color=\"blue\">{0}</font>"));
		inLineFormattingMap.put("reference", new MessageFormat("<font color=\"green\"><b>{0}</b></font>"));
		inLineFormattingMap.put("refinmsg", new MessageFormat("<font color=\"green\"><b>{0}</b></font>"));
		inLineFormattingMap.put("value", new MessageFormat("<font color=\"blue\">{0}</font>"));
		inLineFormattingMap.put("any", new MessageFormat("<font color=\"red\"><b>{0}</b></font>"));
	}

	public static String htmlify(String str) {
		return str.replaceAll("\\n", "<br>").replaceAll("&", "&amp;").replaceAll("<", "&lt;").replaceAll(">", "&gt;").replaceAll(
				"\"",
				"&quot;");
	}

	public static String generateReport(RuleDefinition ruleDef, GridTemplate template, boolean useFormattingInLine) throws ReportException {
		RuleDefinitionReportWriter writer = new RuleDefinitionReportWriter(ruleDef, template, null, -1, useFormattingInLine);
		return writer.toString();
	}

	public static String generateReport(RuleDefinition ruleDef, GridTemplate template, GridValueContainable grid, int row,
			boolean useFormattingInLine) throws ReportException {
		RuleDefinitionReportWriter writer = new RuleDefinitionReportWriter(ruleDef, template, grid, row, useFormattingInLine);
		return writer.toString();
	}

	private final StringBuilder buffer = new StringBuilder();
	private final Logger logger = Logger.getLogger(getClass());
	private int indentCount = 0;
	private final GridTemplate template;
	private final GridValueContainable grid;
	private final boolean replaceColumnRef;
	private final int row;
	private final boolean useFormattingInLine;

	private RuleDefinitionReportWriter(RuleDefinition ruleDef, GridTemplate template, GridValueContainable grid, int row,
			boolean useFormattingInLine) throws ReportException {
		this.template = template;
		this.grid = grid;
		this.row = row;
		this.replaceColumnRef = (grid != null);
		this.useFormattingInLine = useFormattingInLine;
		write(ruleDef);
	}

	private void indent() {
		for (int i = 0; i < indentCount; i++) {
			buffer.append("&nbsp;&nbsp;&nbsp;&nbsp;");
		}
	}

	private void println(String str) {
		indent();
		buffer.append(str);
		if (str != null) {
			buffer.append(NEW_LINE);
		}
	}

	private void print(String str) {
		indent();
		if (str != null) {
			buffer.append(str);
		}
	}

	private void append(String str) {
		if (str != null) {
			buffer.append(str);
		}
	}

	private void appendln(String str) {
		if (str != null) {
			buffer.append(str);
		}
		buffer.append(NEW_LINE);
	}

	private void appendAction(String op) {
		appendAsSpan(op, "action");
	}

	private void appendBracket(String op) {
		appendAsSpan(op, "bracket");
	}

	private void appendKeyword(String op) {
		appendAsSpan(op, "keyword");
	}

	private void appendOperator(String op) {
		appendAsSpan(op, "operator");
	}

	private void appendParameter(String op) {
		appendAsSpan(op, "parameter");
	}

	private void appendReference(String op) {
		appendAsSpan(op, "reference");
	}

	private void appendAsSpan(Object value, String className) {
		if (useFormattingInLine) {
			append(inLineFormattingMap.get(className).format(new Object[] { (value == null ? "" : value.toString()) }));
		}
		else {
			append("<span class='");
			append(className);
			append("'>");
			append((value == null ? "" : value.toString()));
			append("</span>");
		}
	}

	private void write(RuleDefinition rule) throws ReportException {
		println("IF");
		++indentCount;

		write(rule.getRootElement());

		--indentCount;

		appendln("");

		println("THEN");
		++indentCount;

		write(rule.getRuleAction());

		--indentCount;
	}

	private void write(CompoundLHSElement element) throws ReportException {
		String tagName = null;
		print("");
		switch (element.getType()) {
		case CompoundLHSElement.TYPE_AND:
			appendBracket("(");
			tagName = "AND";
			break;
		case CompoundLHSElement.TYPE_OR:
			appendBracket("(");
			tagName = "OR";
			break;
		case CompoundLHSElement.TYPE_NOT:
			appendOperator("NOT");
			append(" ");
			appendBracket("(");
			append(NEW_LINE);
			tagName = "AND";
			break;
		default:
			logger.warn("Invalid compound element type in  " + element);
		}
		append(" ");

		writeElements(element, tagName);

		append(" ");
		appendBracket(")");
		//appendln("");
	}

	private void writeElements(CompoundLHSElement parent, String op) throws ReportException {
		//logger.debug(">>> writeElements(CompoundLHSElement): " + parent + ", size=" + parent.size());
		for (int i = 0; i < parent.size(); ++i) {
			RuleElement element = parent.get(i);
			if (i > 0) {
				append(" &nbsp;");
				appendKeyword(op);
				appendln("");
				if (!(element instanceof CompoundLHSElement)) {
					print("&nbsp;&nbsp;");
				}
			}
			if (element instanceof CompoundLHSElement) {
				++indentCount;
				write((CompoundLHSElement) element);
				--indentCount;
			}
			else if (element instanceof Condition) {
				write((Condition) element);
			}
			else if (element instanceof ExistExpression) {
				write((ExistExpression) element);
			}
			else if (element instanceof TestCondition) {
				write((TestCondition) element);
			}
			else {
				logger.warn("*** Ignored unknown LHS element: " + element + " at " + i + " in " + parent);
			}
		}
	}

	private void write(ExistExpression existExpression) throws ReportException {
		appendKeyword("EXIST ");
		append(existExpression.getClassName());
		append(" ");
		appendKeyword("WITH");
		append(" ");

		if (existExpression.getCompoundLHSElement().size() < 2) {
			writeElements(existExpression.getCompoundLHSElement(), "&nbsp;");
		}
		else {
			appendBracket("(");

			++indentCount;

			append(NEW_LINE);

			indent();
			append("&nbsp;&nbsp;");

			writeElements(existExpression.getCompoundLHSElement(), "AND");

			appendBracket(")");
			--indentCount;
		}
	}

	private void write(Condition condition) throws ReportException {
		//logger.debug(">>> write(Condition): " + condition);
		if (replaceColumnRef && (condition.getOp() == Condition.OP_BETWEEN || condition.getOp() == Condition.OP_NOT_BETWEEN)) {
			try {
				writeBetweenCondition(condition);
			}
			catch (ReportException ex) {
				throw ex;
			}
			catch (Exception ex) {
				logger.error("Failed to write between condition", ex);
				throw new ReportException(ex.toString());
			}
		}
		else {
			write(condition.getReference());

			append("&nbsp;");

			switch (condition.getOp()) {

			case Condition.OP_ANY_VALUE:
				appendOperator("IS");
				append("&nbsp;");
				append(ANY_VALUE);
				break;
			case Condition.OP_IS_EMPTY:
				append(IS_EMPTY);
				break;
			case Condition.OP_IS_NOT_EMPTY:
				append(IS_NOT_EMPTY);
				break;
			default:
				Value value = condition.getValue();
				boolean flag = false;
				try {
					flag = isEmptyValue(value);
				}
				catch (InvalidDataException ex) {
					logger.error(ex);
				}
				// if value is empty
				if (flag) {
					//logger.debug("    write(Condition): condition is empty");
					if (condition.getOp() == Condition.OP_NOT_IN || condition.getOp() == Condition.OP_NOT_BETWEEN) {
						appendOperator("IS NOT");
					}
					else {
						appendOperator("IS");
					}
				}
				else {
					if (replaceColumnRef) {
						boolean reverseNot = false;
						try {
							reverseNot = isExcludedEnumValue(value);
						}
						catch (Exception ex) {
							logger.error("Failed to determine if value is an excluded enum value", ex);
							throw new ReportException(ex.toString());
						}
						//logger.debug("    write(Condition): reverseNot = " + reverseNot);
						switch (condition.getOp()) {
						case Condition.OP_BETWEEN:
						case Condition.OP_IN:
						case Condition.OP_EQUAL:
							appendOperator((reverseNot ? "IS NOT" : "IS"));
							break;
						case Condition.OP_NOT_EQUAL:
						case Condition.OP_NOT_IN:
						case Condition.OP_NOT_BETWEEN:
							appendOperator((reverseNot ? "IS" : "IS NOT"));
							break;
						default:
							appendOperator(htmlify(Condition.Aux.toOpString(condition.getOp())));
						}
					}
					else {
						switch (condition.getOp()) {
						case Condition.OP_NOT_BETWEEN:
						case Condition.OP_BETWEEN:
						case Condition.OP_IN:
						case Condition.OP_NOT_IN:
							appendOperator("IS ");
						}
						appendOperator(htmlify(Condition.Aux.toOpString(condition.getOp())));
					}
				}
				append(" ");

				write(value);
			}
		}
	}

	private DeployType findDeployType(Reference reference) {
		String s = reference.getClassName();
		String s1 = reference.getAttributeName();

		DomainClass domainclass = DomainManager.getInstance().getDomainClass(s);
		if (domainclass == null) {
			logger.error("Invalid class: " + s);
			return null;
		}
		DomainAttribute domainattribute = domainclass.getDomainAttribute(s1);
		if (domainattribute == null) {
			logger.error("Invalid attr: " + s);
			return null;
		}
		return domainattribute.getDeployType();
	}

	private void writeBetweenCondition(Condition condition) throws ReportException {
		//logger.debug(">>> writeBetweenCondition(Condition): " + condition);
		Value value = condition.getValue();
		boolean flag = false;
		try {
			flag = isEmptyValue(value);
		}
		catch (InvalidDataException ex) {
			logger.error(ex);
		}
		// if empty
		if (flag) {
			write(condition.getReference());
			append(" ");
			appendOperator("IS ");
			append(ANY_VALUE);
		}
		else if (value instanceof IRange) {
			writeBetweenCondition(condition.getReference(), (IRange) value, condition.getOp() == Condition.OP_NOT_BETWEEN);
		}
		else if (value instanceof ColumnReference) {
			try {
				Object cellValue = grid.getCellValueObject(this.row, ((ColumnReference) value).getColumnNo(), ANY_VALUE);
				if (cellValue instanceof IRange) {
					writeBetweenCondition(condition.getReference(), (IRange) cellValue, condition.getOp() == Condition.OP_NOT_BETWEEN);
				}
				else {
					logger.warn("Invalid value - IRange expected: " + value + (value == null ? "" : " (" + value.getClass().getName()));
					throw new ReportException("Invalid condition: range value expected but not found");
				}
			}
			catch (InvalidDataException e) {
				throw new ReportException(e.getMessage());
			}
		}
		else {
			// use deploy type to figure out which IRange implementation to use
			String valueStr = value.toString();
			DeployType deployType = findDeployType(condition.getReference());
			try {
				IRange rangeObj = RuleGeneratorHelper.asIRangeValue(valueStr, deployType);
				writeBetweenCondition(condition.getReference(), rangeObj, condition.getOp() == Condition.OP_NOT_BETWEEN);
			}
			catch (InvalidDataException ex) {
				throw new ReportException(ex.getMessage() + " in " + condition);
			}
		}
	}

	private void writeBetweenCondition(Reference reference, IRange value, boolean isNotBetween) {
		// get min and max
		Object min = null;
		Object max = null;
		if (value instanceof DateRange) {
			min = ((DateRange) value).getLowerValue();
			max = ((DateRange) value).getUpperValue();
		}
		else {
			min = value.getFloor();
			max = value.getCeiling();
		}

		if (isNotBetween) {
			if (min != null) {
				write(reference);
				append(" ");
				appendOperator(value.isLowerValueInclusive() ? Condition.OPSTR_LESS : Condition.OPSTR_LESS_EQUAL);
				append(" ");
				append(value.formatValue(min));
			}
			if (max != null) {
				if (min != null) {
					append(" ");
					appendOperator("or");
					append(" ");
				}
				append(value.formatValue(max));
				append(" ");
				appendOperator(value.isUpperValueInclusive() ? Condition.OPSTR_LESS : Condition.OPSTR_LESS_EQUAL);
				append(" ");
				write(reference);
			}
		}
		else {
			if (min != null) {
				append(value.formatValue(min));
				append(" ");
				appendOperator(value.isLowerValueInclusive() ? Condition.OPSTR_LESS_EQUAL : Condition.OPSTR_LESS);
				append(" ");
				write(reference);
			}
			if (max != null) {
				if (min == null) {
					write(reference);
				}
				append(" ");
				appendOperator(((IRange) value).isUpperValueInclusive() ? Condition.OPSTR_LESS_EQUAL : Condition.OPSTR_LESS);
				append(" ");
				append(((IRange) value).formatValue(max));
			}
		}
	}

	private boolean isEmptyValue(Object value) throws InvalidDataException {
		if (value == null || value.toString() == null || value.toString().trim().length() == 0) return true;
		if (replaceColumnRef && value instanceof ColumnReference) {
			Object valueObj = grid.getCellValueObject(this.row, ((ColumnReference) value).getColumnNo(), null);
			return isEmptyValue(valueObj);
		}
		else {
			return false;
		}
	}

	private boolean isExcludedEnumValue(Object value) throws InvalidDataException {
		if (value != null && EnumValues.class.isInstance(value)) {
			return ((EnumValues<?>) value).isSelectionExclusion();
		}
		else if (value instanceof ColumnReference) {
			Object valueObj = grid.getCellValueObject(this.row, ((ColumnReference) value).getColumnNo(), null);
			return isExcludedEnumValue(valueObj);
		}
		else {
			return false;
		}
	}

	private void write(Reference ref) {
		appendReference(ref.getAttributeName());
		appendReference(" of ");
		appendReference(ref.getClassName());
	}

	private void write(TestCondition testCondition) {
		// TBD implement
		appendReference("TestCondition not supported yet: " + testCondition);
	}

	private void write(ColumnReference ref, boolean forRHS) throws ReportException {
		if (replaceColumnRef) {
			try {
				Object value = grid.getCellValueObject(this.row, ref.getColumnNo(), ANY_VALUE);
				writeValueObject(value, forRHS);
			}
			catch (InvalidDataException ex) {
				logger.error("Invalid date at " + row + "," + ref.getColumnNo(), ex);
				throw new ReportException("Invalid data at " + row + "," + ref.getColumnNo() + ": " + ex.getMessage());
			}
		}
		else {
			append("Column '");
			append((template.getColumn(ref.getColumnNo()) == null ? String.valueOf(ref.getColumnNo()) : template.getColumn(
					ref.getColumnNo()).getTitle()));
			append("'");
		}
	}

	private void writeValueObject(Object value, boolean forRHS) throws ReportException {
		if (value == null || value.toString().trim().length() == 0) {
			append(ANY_VALUE);
		}
		else {
			//logger.debug("... writeValueObject: " + value + " (" + value.getClass().getName() + ")");
			if (value instanceof DateRange) {
				append(value.toString());
			}
			else if (value instanceof IRange) {
				append(value.toString());
			}
			else if (EnumValues.class.isInstance(value)) {
				if (forRHS && ((EnumValues<?>) value).isSelectionExclusion()) {
					appendOperator(("NOT "));
				}
				if (((EnumValues<?>) value).size() > 1) {
					appendOperator("one of ");
				}
				for (int i = 0; i < ((EnumValues<?>) value).size(); i++) {
					if (i > 0) append(",");
					append(((EnumValues<?>) value).get(i).toString());
				}
			}
			else if (value instanceof DynamicStringValue) {
				try {
					append(RuleGeneratorHelper.replaceColumnReferenceInDynamicString(
							(DynamicStringValue) value,
							this.template,
							this.grid,
							this.row));
				}
				catch (InvalidDataException ex) {
					logger.error("Error while writing dynamic string value " + value, ex);
					throw new ReportException("Error while generation report on dynamic string value:" + ex.getMessage());
				}
			}
			else {
				append(value.toString());
			}
		}
	}

	private void write(MathExpressionValue ref) throws ReportException {
		write(ref.getColumnReference(), false);
		append("&nbsp;&nbsp;");
		appendOperator(htmlify(ref.getOperator()));
		append("&nbsp;&nbsp;");
		write(ref.getAttributeReference());
	}

	private void write(Value value) throws ReportException {
		//logger.debug(">>> write(Value): " + value);
		if (value instanceof ColumnReference) {
			write((ColumnReference) value, false);
		}
		else if (value instanceof MathExpressionValue) {
			write((MathExpressionValue) value);
		}
		else if (value instanceof Reference) {
			write((Reference) value);
		}
		else {
			writeValueObject(value, false);
		}
	}

	private void write(RuleAction action) throws ReportException {
		if (action != null && action.getActionType() != null) {
			print("");
			appendAction(htmlify(String.valueOf(action.getActionType().getName())));
			if (action.size() > 0) {
				if (useFormattingInLine) {
					for (int i = 0; i < action.size(); ++i) {
						append(NEW_LINE);
						append("&nbsp;&nbsp;");
						write((FunctionParameter) action.get(i));
					}
				}
				else {
					append("<UL>");
					for (int i = 0; i < action.size(); ++i) {
						write((FunctionParameter) action.get(i));
					}
					append("</UL>");
				}
			}
		}
		appendln("");
	}

	private void write(FunctionParameter param) throws ReportException {
		if (!useFormattingInLine) append("<li>");
		print("");
		appendParameter(htmlify(param.toDisplayName()));
		appendBracket("&nbsp;:&nbsp;&nbsp;");
		if (param instanceof ColumnReference) {
			write((ColumnReference) param, true);
		}
		else {
			append(htmlify(param.valueString()));
		}
		if (!useFormattingInLine) append("</li>");
	}

	public String toString() {
		return buffer.toString();
	}
}