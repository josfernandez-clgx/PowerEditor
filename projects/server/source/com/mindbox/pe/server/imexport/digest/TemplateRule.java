/*
 * Created on 2005. 3. 30.
 *
 */
package com.mindbox.pe.server.imexport.digest;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import com.mindbox.pe.model.TemplateMessageDigest;


/**
 * Used for digesting guideline template exports.
 * @author Geneho Kim
 * @since PowerEditor 4.2
 */
public class TemplateRule extends Rule {

	private String name;
	private String usage;
	private String description;
	private String definition;
	private final List<TemplateMessageDigest> messageList;
	private final List<RulePrecondition> conditionList;

	public TemplateRule() {
		messageList = new ArrayList<TemplateMessageDigest>();
		conditionList = new LinkedList<RulePrecondition>();
	}

	public void addMessage(TemplateMessageDigest message) {
		messageList.add(message);
	}

	public TemplateMessageDigest[] getMessages() {
		return messageList.toArray(new TemplateMessageDigest[0]);
	}

	public void addPrecondition(RulePrecondition condition) {
		conditionList.add(condition);
	}

	public boolean hasPrecondition() {
		return !conditionList.isEmpty();
	}

	public RulePrecondition[] getPreconditions() {
		return conditionList.toArray(new RulePrecondition[0]);
	}

	public String getDefinition() {
		return definition;
	}

	public String getDescription() {
		return description;
	}

	public String getName() {
		return name;
	}

	public String getUsage() {
		return usage;
	}

	public void setDefinition(String definition) {
		this.definition = definition;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setUsage(String usage) {
		this.usage = usage;
	}
}