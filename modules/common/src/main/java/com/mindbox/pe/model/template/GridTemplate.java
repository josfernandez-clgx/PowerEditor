package com.mindbox.pe.model.template;

import java.util.List;

import com.mindbox.pe.common.UtilBase;
import com.mindbox.pe.model.Auditable;
import com.mindbox.pe.model.TemplateUsageType;
import com.mindbox.pe.model.rule.RuleDefinition;

/**
 * Template Definition.
 * 
 * @author kim
 * @author MindBox
 * @since PowerEditor 1.0
 * @see RuleMessageContainer
 * @see ColumnReferenceContainer
 */
public class GridTemplate extends AbstractTemplateCore<GridTemplateColumn> implements RuleMessageContainer, ColumnReferenceContainer, Auditable {

	private static final long serialVersionUID = 2003062012258002L;

	private int consistencyColumns[];

	private int completenessColumns[];

	private String ruleExplanation;

	private String message;

	private final DefaultMessageContainer messageContainer;

	/** used to store rule def string from template XML; for migration only */
	private transient String ruleDefinitionString;

	private RuleDefinition ruleDefinition;

	/**
	 * Added for digest support.
	 * 
	 * @since PowerEditor 3.2.0
	 */
	public GridTemplate() {
		this(-1, "", null);
	}

	private GridTemplate(GridTemplate source) {
		super(source);
		this.consistencyColumns = UtilBase.copy(source.consistencyColumns);
		this.completenessColumns = UtilBase.copy(source.completenessColumns);
		this.ruleDefinition = source.ruleDefinition == null ? null : new RuleDefinition(source.ruleDefinition);
		this.ruleDefinitionString = source.ruleDefinitionString;
		this.ruleExplanation = source.ruleExplanation;
		this.message = source.message;
		this.messageContainer = new DefaultMessageContainer(source.messageContainer);
	}

	public GridTemplate(int id, String name, TemplateUsageType usageType) {
		super(id, name, usageType);
		completenessColumns = null;
		this.messageContainer = new DefaultMessageContainer();
	}

	public void addGridTemplateColumn(GridTemplateColumn gridtemplatecolumn) {
		addColumn(gridtemplatecolumn);
		gridtemplatecolumn.usageType = this.getUsageType();
	}

	public void addGridTemplateColumn(int id, String name, String desc, int j, TemplateUsageType uType) {
		GridTemplateColumn gridtemplatecolumn = new GridTemplateColumn(id, name, desc, j, uType);
		addColumn(gridtemplatecolumn);
	}

	@Override
	public void addMessageDigest(TemplateMessageDigest digest) {
		messageContainer.addMessageDigest(digest);
	}

	@Override
	public void adjustChangedColumnReferences(final int originalColNo, final int newColNo) {
		if (ruleDefinition != null) {
			ruleDefinition.adjustChangedColumnReferences(originalColNo, newColNo);
		}
		for (final TemplateMessageDigest element : messageContainer.getAllMessageDigest()) {
			element.adjustChangedColumnReferences(originalColNo, newColNo);
		}
		for (GridTemplateColumn column : getColumns()) {
			column.adjustChangedColumnReferences(originalColNo, newColNo);
		}
	}

	@Override
	public void adjustDeletedColumnReferences(int colNo) {
		if (ruleDefinition != null) {
			ruleDefinition.adjustDeletedColumnReferences(colNo);
		}
		for (final TemplateMessageDigest element : messageContainer.getAllMessageDigest()) {
			element.adjustDeletedColumnReferences(colNo);
		}
		for (GridTemplateColumn column : getColumns()) {
			column.adjustDeletedColumnReferences(colNo);
		}
	}

	@Override
	public boolean containsColumnReference(int colNo) {
		return false;
	}

	/**
	 * Makes sure invariants of this is identical to that of the specified template. Note that this
	 * does not copy parent ID.
	 * 
	 * @param template
	 *            the source template
	 */
	public synchronized void copyFrom(GridTemplate template) {
		super.copyFrom(template);
		this.completenessColumns = template.completenessColumns;
		this.consistencyColumns = template.consistencyColumns;
		this.messageContainer.copyFrom(template.messageContainer);
		this.ruleDefinition = (template.ruleDefinition == null ? null : new RuleDefinition(template.ruleDefinition));
		this.ruleExplanation = template.ruleExplanation;
	}

	protected GridTemplateColumn createTemplateColumn(GridTemplateColumn source) {
		GridTemplateColumn column = new GridTemplateColumn(source);
		return column;
	}

	@Override
	public Auditable deepCopy() {
		return new GridTemplate(this);
	}

	public boolean equals(GridTemplate gridtemplate) {
		return gridtemplate.getID() == getID();
	}

	@Override
	public TemplateMessageDigest findMessageForEntity(int channelID) {
		return messageContainer.findMessageForEntity(channelID);
	}

	@Override
	public List<TemplateMessageDigest> getAllMessageDigest() {
		return messageContainer.getAllMessageDigest();
	}

	@Override
	public String getAuditDescription() {
		return getAuditName();
	}

	@Override
	public String getAuditName() {
		return String.format("%s v. %s (type: %s)", getName(), getVersion(), getUsageType().getDisplayName());
	}

	public int[] getCompletenessColumns() {
		return completenessColumns;
	}

	public int[] getConsistencyColumns() {
		return consistencyColumns;
	}

	/**
	 * Added for digest support.
	 * 
	 * @since PowerEditor 3.2.0
	 * @return Returns the message.
	 */
	public String getMessage() {
		return message;
	}

	/**
	 * @return Returns the ruleDefinitio.
	 */
	@Override
	public RuleDefinition getRuleDefinition() {
		return ruleDefinition;
	}

	/**
	 * For migration use only
	 * 
	 * @return rule definition string
	 * @since PowerEditor 4.0
	 */
	public String getRuleDefinitionString() {
		return ruleDefinitionString;
	}

	/**
	 * @return Returns the ruleExplanation.
	 */
	public String getRuleExplanation() {
		return ruleExplanation;
	}

	@Override
	public boolean hasEntitySpecificMessage() {
		return messageContainer.hasEntitySpecificMessage();
	}

	@Override
	public boolean hasMessageDigest() {
		return messageContainer.hasMessageDigest();
	}

	public boolean hasMessages() {
		return hasMessageDigest();
	}

	public boolean isCompletenessCheckColumn(int i) {
		if (completenessColumns == null) return false;
		for (int j = 0; j < completenessColumns.length; j++)
			if (completenessColumns[j] == i) return true;

		return false;
	}

	public boolean isConsistencyCheckColumn(int i) {
		if (consistencyColumns == null) return true;
		for (int j = 0; j < consistencyColumns.length; j++)
			if (consistencyColumns[j] == i) return true;

		return false;
	}

	@Override
	public void removeMessageDigest(TemplateMessageDigest digest) {
		messageContainer.removeMessageDigest(digest);
	}

	/**
	 * Added for template import digester support.
	 * 
	 * @param str
	 * @since PowerEditor 4.2
	 */
	public void setCompleteColumnsString(String str) {
		completenessColumns = UtilBase.toIntArray(str);
	}

	public void setCompletenessColumns(int ai[]) {
		completenessColumns = ai;
	}

	public void setConsistencyColumns(int ai[]) {
		consistencyColumns = ai;
	}

	/**
	 * Added for template import digester support.
	 * 
	 * @param str
	 * @since PowerEditor 4.2
	 */
	public void setConsistentColumnsString(String str) {
		consistencyColumns = UtilBase.toIntArray(str);
	}

	/**
	 * Added for digest support.
	 * 
	 * @since PowerEditor 3.2.0
	 * @param message
	 *            The message to set.
	 */
	public void setMessage(String message) {
		this.message = message;
	}

	/**
	 * @param ruleDefinition
	 *            The ruleDefinition to set.
	 */
	@Override
	public void setRuleDefinition(RuleDefinition ruleDefinition) {
		this.ruleDefinition = ruleDefinition;
	}

	/**
	 * For migration use only
	 * 
	 * @param ruleDefinitionString
	 *            the rule definition string
	 * @since PowerEditor 4.0
	 */
	public void setRuleDefinitionString(String ruleDefinitionString) {
		this.ruleDefinitionString = ruleDefinitionString;
	}

	/**
	 * @param ruleExplanation
	 *            The ruleExplanation to set.
	 */
	public void setRuleExplanation(String ruleExplanation) {
		this.ruleExplanation = ruleExplanation;
	}

	@Override
	public String toString() {
		return "GridTemplate[" + getID() + ",name=" + getName() + ",v=" + getVersion() + ",noCols=" + getNumColumns() + ",maxRows=" + getMaxNumOfRows() + "]@" + Integer.toHexString(hashCode());
	}
}