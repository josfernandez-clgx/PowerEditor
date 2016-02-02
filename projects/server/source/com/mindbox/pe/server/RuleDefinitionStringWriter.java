/*
 * Created on 2004. 8. 8.
 */
package com.mindbox.pe.server;

import org.apache.log4j.Logger;

import com.mindbox.pe.common.UtilBase;
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
import com.mindbox.pe.server.generator.RuleGeneratorHelper;

/**
 * Rule definition string writer.
 * @author kim
 * @author MindBox
 * @since PowerEditor
 */
public class RuleDefinitionStringWriter {

	private static final String NEW_LINE = System.getProperty("line.separator");

	private static final String xmlify(String str) {
		return Util.xmlify(str);
	}

	public static String writeAsString(RuleDefinition ruleDef) {
		RuleDefinitionStringWriter writer = new RuleDefinitionStringWriter(ruleDef);
		return writer.toString();
	}

	private final StringBuffer buffer = new StringBuffer();
	private final Logger logger = Logger.getLogger(getClass());
	private int indentCount = 0;

	private RuleDefinitionStringWriter(RuleDefinition ruleDef) {
		write(ruleDef);
	}

	private void indent() {
		for (int i = 0; i < indentCount; i++) {
			buffer.append("  ");
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

	private void write(RuleDefinition rule) {
		logger.debug(">>> write(RuleDefinition): " + rule);
		++indentCount;
		print("<Rule id=\"");
		append(String.valueOf(rule.getID()));
		append("\" name=\"");
		append(xmlify(rule.getName()));
		appendln("\">");

		++indentCount;

		print("<Description>");
		append(xmlify(rule.getDescription()));
		appendln("</Description>");

		println("<LHS>");
		++indentCount;

		write(rule.getRootElement());

		--indentCount;
		println("</LHS>");

		write(rule.getRuleAction());

		// messages no longer stored with rule definition
		//writeMessages(rule);

		--indentCount;
		println("</Rule>");
		--indentCount;
	}

	private void write(CompoundLHSElement element) {
		logger.debug(">>> write(CompoundLHSElement): " + element);
		String tagName = null;
		switch (element.getType()) {
		case CompoundLHSElement.TYPE_AND:
			tagName = "AND";
			break;
		case CompoundLHSElement.TYPE_OR:
			tagName = "OR";
			break;
		case CompoundLHSElement.TYPE_NOT:
			tagName = "NOT";
			break;
		default:
			logger.warn("Invalid compound element type in  " + element);
			tagName = "ERROR_INVALID_" + element.getType();
		}

		println("<" + tagName + ">");
		++indentCount;

		writeElements(element);

		writeComment(element.getComment());
		--indentCount;
		println("</" + tagName + ">");
	}

	private void writeElements(CompoundLHSElement parent) {
		logger.debug(">>> writeElements(CompoundLHSElement): " + parent + ", size=" + parent.size());
		for (int i = 0; i < parent.size(); ++i) {
			RuleElement element = parent.get(i);
			if (element instanceof CompoundLHSElement) {
				write((CompoundLHSElement) element);
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
		}
	}

	private void write(ExistExpression existExpression) {
		print("<Exist class=\"" + existExpression.getClassName() + "\"");
		if (existExpression.getObjectName() != null) {
			append(" objectName=\"" + existExpression.getObjectName() + "\"");
		}
		if (existExpression.getExcludedObjectName() != null) {
			append(" excludedObjectName=\"" + xmlify(existExpression.getExcludedObjectName()) + "\"");
		}
		appendln(">");
		
		++indentCount;

		writeElements(existExpression.getCompoundLHSElement());

		writeComment(existExpression.getComment());
		--indentCount;
		println("</Exist>");
	}

	private void write(Condition condition) {
		println("<Condition>");
		++indentCount;

		write(condition.getReference());

		print("<Operator>");
		append(xmlify(Condition.Aux.toOpString(condition.getOp())));
		appendln("</Operator>");

		write(condition.getValue());

		writeComment(condition.getComment());

		if (!UtilBase.isEmpty(condition.getObjectName())) {
			print("<ObjectName>");
			append(xmlify(condition.getObjectName()));
			appendln("</ObjectName>");
		}
		--indentCount;

		println("</Condition>");
	}

	private void writeComment(String comment) {
		print("<Comment>");
		append(xmlify(comment));
		appendln("</Comment>");
	}

	private void write(Reference ref) {
		println("<Reference>");

		++indentCount;
		print("<Class>");
		append(ref.getClassName());
		appendln("</Class>");
		print("<Attribute>");
		append(ref.getAttributeName());
		appendln("</Attribute>");
		--indentCount;

		println("</Reference>");
	}

	private void write(ColumnReference ref) {
		println("<ColumnRef columnNo=\"" + ref.getColumnNo() + "\"/>");
	}

	private void write(MathExpressionValue ref) {
		println("<MathExpression>");

		++indentCount;

		write(ref.getColumnReference());
		print("<Operator>");
		append(ref.getOperator());
		appendln("</Operator>");
		write(ref.getAttributeReference());

		--indentCount;

		println("</MathExpression>");
	}

	private void write(Value value) {
		println("<Value>");
		++indentCount;
		if (value instanceof ColumnReference) {
			write((ColumnReference) value);
		}
		else if (value instanceof MathExpressionValue) {
			write((MathExpressionValue) value);
		}
		else if (value instanceof Reference) {
			write((Reference) value);
		}
		else {
			append((value == null ? RuleGeneratorHelper.AE_NIL : xmlify(value.toString())));
		}
		appendln("</Value>");
		--indentCount;
	}

	private void write(RuleAction action) {
		logger.debug(">>> write(RuleAction):" + action);
		println("<Action >");

		if (action != null && action.getActionType() != null) {
			++indentCount;

			print("<Type>");
			append(String.valueOf(action.getActionType().getID()));
			appendln("</Type>");

			writeComment(action.getComment());

			println("<ParameterList>");
			++indentCount;

			for (int i = 0; i < action.size(); ++i) {
				write((FunctionParameter) action.get(i));
			}

			--indentCount;
			println("</ParameterList>");

			--indentCount;
		}
		println("</Action>");
	}

	private void write(TestCondition test) {
		logger.debug(">>> write(TestCondition):" + test);
		println("<TestCondition >");

		if (test != null && test.getTestType() != null) {
			++indentCount;

			print("<Type>");
			append(String.valueOf(test.getTestType().getID()));
			appendln("</Type>");

			writeComment(test.getComment());

			println("<ParameterList>");
			++indentCount;

			for (int i = 0; i < test.size(); ++i) {
				FunctionParameter fp = (FunctionParameter) test.get(i);
				logger.debug(">>> param " + fp.index() + " " + fp.getClass().getName() + "" + "\"" + fp.valueString() + "\"");
				write(fp);
			}

			--indentCount;
			println("</ParameterList>");

			--indentCount;
		}
		println("</TestCondition>");
	}

	private void write(FunctionParameter param) {
		print("<Parameter index=\"");
		append(String.valueOf(param.index()));
		append("\" value=\"");
		append(xmlify(param.valueString()));
		appendln("\"/>");
	}

	public String toString() {
		return buffer.toString();
	}
}