package com.mindbox.pe.model.filter;

import com.mindbox.pe.model.PeDataType;
import com.mindbox.pe.model.TemplateUsageType;
import com.mindbox.pe.model.rule.ActionTypeDefinition;


/**
 * Guideline action search filter.
 * Performs contains search on name, description, and deployment rule.
 * @author Geneho
 * @since PowerEditor 4.0.0
 */
public class GuidelineActionSearchFilter extends AbstractSearchFilter<ActionTypeDefinition> {

	private static final long serialVersionUID = 200407221057000L;

	private String name, description, ruleText = null;
	private TemplateUsageType usage = null;

	public GuidelineActionSearchFilter() {
		super(PeDataType.GUIDELINE_ACTION);
	}

	public void setUsage(TemplateUsageType usage) {
		this.usage = usage;
	}

	/**
	 * @param description The description to set.
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * @param name The name to set.
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @param ruleText The ruleText to set.
	 */
	public void setDeploymentRule(String ruleText) {
		this.ruleText = ruleText;
	}

	public boolean isAcceptable(ActionTypeDefinition actionDef) {
		if (usage != null && !actionDef.hasUsageType(usage)) {
			return false;
		}
		if (name != null && name.length() > 0) {
			if (actionDef.getName() == null || actionDef.getName().indexOf(name) < 0) {
				return false;
			}
		}
		if (description != null && description.length() > 0) {
			if (actionDef.getDescription() == null || actionDef.getDescription().indexOf(description) < 0) {
				return false;
			}
		}
		if (ruleText != null && ruleText.length() > 0) {
			if (actionDef.getDeploymentRule() == null || actionDef.getDeploymentRule().indexOf(ruleText) < 0) {
				return false;
			}
		}
		return true;
	}

}