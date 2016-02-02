/*
 * Created on 2005. 6. 24.
 *
 */
package com.mindbox.pe.common.validate;

import com.mindbox.pe.common.DomainClassProvider;
import com.mindbox.pe.common.TemplateUtil;
import com.mindbox.pe.model.GridTemplate;
import com.mindbox.pe.model.rule.CompoundLHSElement;
import com.mindbox.pe.model.rule.ExistExpression;
import com.mindbox.pe.model.rule.RuleDefinition;
import com.mindbox.pe.model.rule.RuleElement;


/**
 * Template Validator.
 * @author Geneho Kim
 * @since PowerEditor 4.3.2
 */
public class TemplateValidator {

	private static String getTemplateName(GridTemplate template, int column) {
		return "the rule for Template '" + template.getName() + "' " + (column > 0 ? ", Column " + column : "");
	}

	private static String toCompoundTypeString(int compoundElementType) {
		switch (compoundElementType) {
		case CompoundLHSElement.TYPE_AND:
			return "AND";
		case CompoundLHSElement.TYPE_NOT:
			return "NOT";
		case CompoundLHSElement.TYPE_OR:
			return "OR";
		default:
			return "UNKNOWN-TYPE";
		}
	}

	/**
	 * Validates the specified rule for empty rule elements and data type compatibilities.
	 * Use this, instead of {@link DataTypeCompatibilityValidator#isValid(RuleDefinition, GridTemplate, TemplateUtil.DomainClassProvider, boolean)},
	 * to perform complete validation of a rule.
	 * @param ruleDef the rule definition
	 * @param template the template
	 * @param column the column
	 * @param domain the domain
	 * @return validation string, if there is an error; <code>null</code>, otherwise
	 * @since PowerEditor 4.3.2
	 */
	public static String isValid(RuleDefinition ruleDef, GridTemplate template, DomainClassProvider domain) {
		// first validate data type compatibility
		String msg = DataTypeCompatibilityValidator.isValid(ruleDef, template, domain, false);
		
		// TODO Kim: 2007-01-09 - validate each rule element
		//           i.e., validate exist expression's class name and attribute references
		return msg;
	}

	/**
	 * This returns warnings as a string if found.
	 * If not found, <code>null</code> is returned.
	 * @param ruleDef the rule definition
	 * @param template the template
	 * @param column the column number
	 * @return the validation string, if there is an error; <code>null</code>, otherwise
	 */
	public static String checkForIncompleteElements(RuleDefinition ruleDef, GridTemplate template, int column) {
		TextWarningConsumer wc = new TextWarningConsumer();
		checkForIncompleteElements(ruleDef, template, column, wc);
		return (wc.hasWarnings() ? wc.toString() : null);
	}
	
	/**
	 * Checks if the specified rule has an incomplete LHS element.
	 * An example of an incomplete element is an OR node with no child elements.
	 * @param ruleDef
	 * @param template the template 
	 * @param column the column number
	 * @param warningConsumer the warning consumer
	 */
	public static void checkForIncompleteElements(RuleDefinition ruleDef, GridTemplate template, int column, WarningConsumer warningConsumer) {
		checkForIncompleteElements(ruleDef, getTemplateName(template, column), warningConsumer);
	}

	private static void checkForIncompleteElements(RuleDefinition ruleDef, String ruleName, WarningConsumer warningConsumer) {
		CompoundLHSElement rootElement = ruleDef.getRootElement();
		if (rootElement.isEmpty()) {
			warningConsumer.addWarning(WarningInfo.WARNING, "No pattern generated for the empty LHS; LHS is empty or only contain empty rule elements.", ruleName);
		}
		else {
			checkForIncompleteElements(rootElement, ruleName, warningConsumer);
		}
	}

	private static void checkForIncompleteElements(CompoundLHSElement element, String ruleName, WarningConsumer warningConsumer) {
		if (element.isEmpty()) {
			warningConsumer.addWarning(WarningInfo.WARNING, "No pattern generated for the empty node; there is a " + toCompoundTypeString(element.getType()) + " node "
					+ " that is empty or only contains empty elements.", ruleName);
		}
		else {
			for (int i = 0; i < element.size(); i++) {
				RuleElement child = (RuleElement) element.get(i);
				if (child instanceof CompoundLHSElement) {
					checkForIncompleteElements((CompoundLHSElement) child, ruleName, warningConsumer);
				}
				else if (child instanceof ExistExpression) {
					checkForIncompleteElements((ExistExpression) child, ruleName, warningConsumer);
				}
			}
		}
	}

	private static void checkForIncompleteElements(ExistExpression element, String ruleName, WarningConsumer warningConsumer) {
		if (element.getCompoundLHSElement().isEmpty()) {
			warningConsumer.addWarning(WarningInfo.WARNING,  
					                   "Empty pattern generated for the empty EXIST node that is empty or only contains empty elements: existNode=" + element, 
									   ruleName);
		}
		else if (element.getClassName() == null || element.getClassName().trim().length() == 0) {
			warningConsumer.addWarning(WarningInfo.WARNING,  "An EXIST node does not have class name set.", ruleName);
		}
		else {
			checkForIncompleteElements(element.getCompoundLHSElement(), ruleName, warningConsumer);
		}
	}

	private TemplateValidator() {

	}
}