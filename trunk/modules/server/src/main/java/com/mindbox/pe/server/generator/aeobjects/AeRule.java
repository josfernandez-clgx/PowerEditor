package com.mindbox.pe.server.generator.aeobjects;

import java.util.Collections;
import java.util.List;

import org.apache.log4j.Logger;

import com.mindbox.pe.server.model.rule.RuleActionMethod;
import com.mindbox.pe.server.parser.jtb.rule.syntaxtree.Node;

/**
 * AE Rule.
 * 
 * @author Gene Kim
 * @author MindBox, Inc
 * @since PowerEditor 1.0
 */
public class AeRule extends AbstractAeCompoundCondition {

	private final Logger logger;
	private String mName;
	private String mDescription;
	private String mRuleset;
	private AbstractAeCondition mLhs;
	private RuleActionMethod mActionMethod;
	private boolean mProcessed;

	private final List<AbstractAeValue> mActionParms;
	private final List<String> mUserRegisteredNames;

	public AeRule(Node node) {
		super(node);
		this.logger = Logger.getLogger(getClass());
		mActionParms = Collections.synchronizedList(new java.util.ArrayList<AbstractAeValue>());
		mUserRegisteredNames = new java.util.ArrayList<String>();
	}

	public RuleActionMethod getActionMethod() {
		return mActionMethod;
	}

	public void setActionMethod(RuleActionMethod actionMethod) {
		mActionMethod = actionMethod;
	}

	public List<String> getUserRegisteredNames() {
		return mUserRegisteredNames;
	}

	public void addCondition(AbstractAeCondition abstractaecondition) {
		setLhs(abstractaecondition);
	}

	public String getDescription() {
		return mDescription;
	}

	public void setDescription(String s) {
		mDescription = s;
	}


	public String toString() {
		StringBuilder stringbuffer = new StringBuilder();
		stringbuffer.append(
			"Rule[" + getName() + "; Ruleset=" + getRuleset() + "\nLHS=" + getLhs() + "\nRHS=" + getActionMethod() + "(");
		for (int i = 0; i < getActionParms().size(); i++) {
			stringbuffer.append(getActionParms().get(i) + " ");
		}

		stringbuffer.append(") ]");
		return stringbuffer.toString();
	}

	public AbstractAeCondition getLhs() {
		return mLhs;
	}

	public void setLhs(AbstractAeCondition abstractaecondition) {
		mLhs = abstractaecondition;
	}

	public void addActionParm(AbstractAeValue abstractaevalue) {
		logger.debug(">>> AeRule.addActionParam with " + abstractaevalue);
		mActionParms.add(abstractaevalue);
	}

	public int size() {
		if (getLhs() == null)
			return 0;
		else
			return getLhs().size();
	}

	public void setProcessed(boolean flag) {
		mProcessed = flag;
	}

	public String getName() {
		return mName;
	}

	public void setName(String s) {
		mName = s;
	}

	public void addUserRegisteredName(String s) {
		mUserRegisteredNames.add(s);
	}

	public String getRuleset() {
		return mRuleset;
	}

	public void setRuleset(String s) {
		mRuleset = s;
	}

	public boolean isProcessed() {
		return mProcessed;
	}

	public List<AbstractAeValue> getActionParms() {
		return mActionParms;
	}

	public void removeCondition(AbstractAeCondition abstractaecondition) {
		abstractaecondition.setParentCondition(null);
		setLhs(null);
	}

}