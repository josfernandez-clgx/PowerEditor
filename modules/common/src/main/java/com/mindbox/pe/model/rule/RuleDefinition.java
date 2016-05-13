package com.mindbox.pe.model.rule;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.mindbox.pe.common.UtilBase;
import com.mindbox.pe.model.AbstractIDNameDescriptionObject;
import com.mindbox.pe.model.TemplateUsageType;
import com.mindbox.pe.model.template.ColumnReferenceContainer;

/**
 * @author Geneho Kim
 * @author MindBox
 * @since PowerEditor 2.3.0
 */
public class RuleDefinition extends AbstractIDNameDescriptionObject implements ColumnReferenceContainer {

	private static final long serialVersionUID = -2127830274911958933L;

	private RuleAction action;
	private final CompoundLHSElement rootCondition;
	private int ruleSetID = -1;
	private final Map<String, String> messageMap = new HashMap<String, String>();
	private TemplateUsageType usageType;

	/** 
	 * <b>
	 * This is used only by the server to cache old parser generated object tree for rule action.
	 *  DO NOT USE IN CLIENT (APPLET) !!!
	 * </b>
	 */
	private transient Object oldParserObjectForAction;

	public RuleDefinition(int id, String name, String desc) {
		super(id, name, desc);
		this.rootCondition = RuleElementFactory.getInstance().createAndCompoundCondition();
		this.action = RuleElementFactory.getInstance().createRuleAction();
	}

	public RuleDefinition(int id, String name, String desc, CompoundLHSElement rootCondition, RuleAction action) {
		super(id, name, desc);
		this.rootCondition = rootCondition;
		this.action = action;
	}

	/**
	 * Creates a new instance of this that is an exact copy of the source.
	 * @param source source
	 * @since PowerEditor 4.3.2
	 */
	public RuleDefinition(RuleDefinition source) {
		this(
				source.getID(),
				source.getName(),
				source.getDescription(),
				RuleElementFactory.deepCopyCompoundLHSElement(source.rootCondition),
				RuleElementFactory.deepCopyRuleAction(source.action));
	}

	public void add(FunctionParameter element) {
		action.add(element);
	}

	public void add(LHSElement cond) {
		rootCondition.add(cond);
	}

	public void addMessage(String channel, String message) {
		messageMap.put(channel, message);
	}

	@Override
	public void adjustChangedColumnReferences(int originalColNo, int newColNo) {
		rootCondition.adjustChangedColumnReferences(originalColNo, newColNo);
		action.adjustChangedColumnReferences(originalColNo, newColNo);
	}

	@Override
	public void adjustDeletedColumnReferences(int colNo) {
		rootCondition.adjustDeletedColumnReferences(colNo);
		action.adjustDeletedColumnReferences(colNo);
	}

	public void clearAction() {
		action.clear();
	}

	public void clearFunctionParameters() {
		action.removeAll();
	}

	public void clearLHS() {
		rootCondition.removeAll();
	}

	public void clearMessages() {
		messageMap.clear();
	}

	@Override
	public boolean containsColumnReference(int colNo) {
		return false;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof RuleDefinition) {
			return this.getID() == ((RuleDefinition) obj).getID();
		}
		return false;
	}

	public int getActionTypeID() {
		return (action.getActionType() == null ? -1 : action.getActionType().getID());
	}

	public FunctionParameter getFunctionParameterAt(int index) {
		return (FunctionParameter) action.get(index);
	}

	public String getMessage(String channel) {
		return messageMap.get(channel);
	}

	public Set<String> getMessageChannels() {
		return Collections.unmodifiableSet(messageMap.keySet());
	}

	public Map<String, String> getMessageMap() {
		return Collections.unmodifiableMap(messageMap);
	}

	/** 
	 * Gets the old parser object for action.
	 * <b>
	 * This is used only by the server to cache old parser generated object tree for rule action.
	 *  DO NOT USE IN CLIENT (APPLET) !!!
	 * </b>
	 * @return old parser object
	 */
	public Object getOldParserObjectForAction() {
		return oldParserObjectForAction;
	}

	public CompoundLHSElement getRootElement() {
		return RuleElementFactory.unmodifiableCompoundLHSElement(this.rootCondition);
	}

	public LHSElement getRootElementAt(int index) {
		return (LHSElement) rootCondition.get(index);
	}

	public RuleAction getRuleAction() {
		return RuleElementFactory.unmodifiableRuleAction(this.action);
	}

	public int getRuleSetID() {
		return ruleSetID;
	}

	public List<TestCondition> getTestConditions() {
		List<TestCondition> tcList = new ArrayList<TestCondition>();
		for (int i = 0; i < rootCondition.size(); i++) {
			RuleElement element = rootCondition.get(i);
			if (element instanceof TestCondition) tcList.add((TestCondition) element);
		}
		return tcList;
	}

	public TemplateUsageType getUsageType() {
		return usageType;
	}

	public boolean hasAction() {
		return action.getActionType() != null;
	}

	private boolean hasConditionForReference(CompoundLHSElement compoundElement, Reference ref) {
		boolean result = false;
		for (int i = 0; i < compoundElement.size(); ++i) {
			RuleElement element = compoundElement.get(i);
			if (element instanceof CompoundLHSElement) {
				result = hasConditionForReference((CompoundLHSElement) element, ref);
			}
			else if (element instanceof Condition) {
				result = hasConditionForReference((Condition) element, ref);
			}
			else if (element instanceof ExistExpression) {
				result = hasConditionForReference((ExistExpression) element, ref);
			}
			if (result) return true;
		}
		return false;
	}

	private boolean hasConditionForReference(Condition condition, Reference ref) {
		return ref.equals(condition.getReference()) || (UtilBase.isEmpty(ref.getAttributeName()) && condition.getReference().getClassName() != null
				&& condition.getReference().getClassName().equals(ref.getClassName()));
	}

	private boolean hasConditionForReference(ExistExpression existExpression, Reference ref) {
		// ref matches with the class of an ExistExpression
		if (existExpression.getClassName().equals(ref.getClassName()) && UtilBase.isEmpty(ref.getAttributeName())) {
			return true;
		}
		else {
			return hasConditionForReference(existExpression.getCompoundLHSElement(), ref);
		}
	}

	/**
	 * Tests if this has a condition that makes a reference to the specified reference object.
	 * @param ref the reference object
	 * @return <code>true</code> if <code>ref</code> is not <code>null</code>, and this has a condition that makes a reference to <code>ref</code>; 
	 *         <code>false</code>, otherwise
	 */
	public boolean hasConditionForReference(Reference ref) {
		if (ref == null) return false;
		return hasConditionForReference(this.rootCondition, ref);
	}

	/**
	 * Tests if this is an empty rule definition.
	 * @return <code>true</code> if this is an empty rule; <code>false</code>, otherwise
	 * @since PowerEditor 4.2.0
	 */
	public boolean isEmpty() {
		return action.getActionType() == null && (rootCondition == null || rootCondition.size() == 0);
	}

	public void remove(FunctionParameter element) {
		action.remove(element);
	}

	public void remove(LHSElement cond) {
		rootCondition.remove(cond);
	}

	public void removeMessage(String channel) {
		messageMap.remove(channel);
	}

	/** 
	 * Sets the old parser object for action.
	 * <b>
	 * This is used only by the server to cache old parser generated object tree for rule action.
	 *  DO NOT USE IN CLIENT (APPLET) !!!
	 * </b>
	 * @param oldParserObjectForAction old parser object
	 */
	public void setOldParserObjectForAction(Object oldParserObjectForAction) {
		this.oldParserObjectForAction = oldParserObjectForAction;
	}

	public void setRuleSetID(int i) {
		ruleSetID = i;
	}

	public void setUsageType(TemplateUsageType usageType) {
		this.usageType = usageType;
	}

	public int sizeOfActionParemeters() {
		return action.size();
	}

	public int sizeOfRootElements() {
		return rootCondition.size();
	}

	public String toDebugString() {
		StringBuilder buff = new StringBuilder();
		buff.append(toString());
		buff.append(System.getProperty("line.separator"));
		toDebugString(buff, rootCondition, "  ");
		buff.append("Action: ");
		buff.append(action.getActionType());
		for (int i = 0; i < action.size(); i++) {
			buff.append(System.getProperty("line.separator"));
			buff.append("Param-");
			buff.append(i);
			buff.append(action.get(i));
		}

		return buff.toString();
	}

	public void toDebugString(StringBuilder buff, CompoundLHSElement cle, String pad) {
		for (int i = 0; i < cle.size(); i++) {
			RuleElement element = cle.get(i);
			if (element instanceof Condition) {
				buff.append(pad);
				buff.append(element.toString());
			}
			else if (element instanceof CompoundLHSElement) {
				buff.append(pad);
				buff.append(element.toDisplayName());
				buff.append(System.getProperty("line.separator"));
				toDebugString(buff, (CompoundLHSElement) element, pad + "  ");
			}
			else {
				buff.append(pad);
				buff.append("unknown: " + element.toString() + "(" + element.getClass().getName() + ")");
			}
			buff.append(System.getProperty("line.separator"));
		}
	}

	@Override
	public String toString() {
		return "Rule[" + super.getName() + ",id=" + super.getID() + ",ruleSet=" + ruleSetID + "]";
	}

	public void updateAction(RuleAction action) {
		if (action != null) {
			this.action.setActionType(action.getActionType());
			this.action.setComment(action.getComment());
			this.action.removeAll();
			for (int i = 0; i < action.size(); i++) {
				this.action.add(RuleElementFactory.deepCopyFunctionParameter((FunctionParameter) action.get(i)));
			}
		}
	}

	public void updateAction(RuleDefinition rule) {
		updateAction(rule.action);
	}

	public void updateConditions(RuleDefinition rule) {
		updateRootConditions(rule.rootCondition);
	}

	public void updateMessage(String channel, String message) {
		if (messageMap.containsKey(channel)) {
			messageMap.remove(channel);
		}
		messageMap.put(channel, message);
	}

	public void updateRootConditions(CompoundLHSElement conditions) {
		this.rootCondition.removeAll();
		CompoundLHSElement copiedConditions = RuleElementFactory.deepCopyCompoundLHSElement(conditions);
		for (int i = 0; i < copiedConditions.size(); i++) {
			this.rootCondition.add(copiedConditions.get(i));
		}
	}
}