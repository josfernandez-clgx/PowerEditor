package com.mindbox.pe.model.rule;

import java.util.ArrayList;
import java.util.List;

import com.mindbox.pe.model.Auditable;
import com.mindbox.pe.model.TemplateUsageType;

/**
 * @author Geneho Kim
 * @author MindBox
 * @since PowerEditor 2.3.0
 */
public class ActionTypeDefinition extends FunctionTypeDefinition implements Auditable {

	private static final long serialVersionUID = 2007051500002L;
	
	private final List<TemplateUsageType> usageTypeList = new ArrayList<TemplateUsageType>();

	/**
	 * Added for digest support.
	 * @since PowerEditor 3.2.0
	 */
	public ActionTypeDefinition() {
		super(-1, "", null);
	}

	/**
	 * 
	 */
	public ActionTypeDefinition(int id, String name, String desc) {
		super(id, name, desc);
	}
	
	public Auditable deepCopy() {
		ActionTypeDefinition copy = new ActionTypeDefinition();
		copy.copyFrom(this);
		return copy;
	}
	
	public String getAuditDescription() {
		return "guideline action '" + getName() + "'";
	}
	
	/**
	 * Makes sure fields of this are identical to those of the source, except the parameters. 
	 * @param source
	 */
	public synchronized void copyFrom(ActionTypeDefinition source) {
		super.copyFrom(source);
		this.usageTypeList.clear();
		this.usageTypeList.addAll(source.usageTypeList);
	}

	public TemplateUsageType[] getUsageTypes() {
		return usageTypeList.toArray(new TemplateUsageType[0]);
	}

	public boolean hasUsageType(TemplateUsageType usage) {
		return usageTypeList.contains(usage);
	}

	public void addUsageType(TemplateUsageType usage) {
		if (!usageTypeList.contains(usage)) {
			usageTypeList.add(usage);
		}
	}
	
	public void clearUsageTypes() {
		this.usageTypeList.clear();
	}

	/**
	 * Added for digest support.
	 * @since PowerEditor 3.2.0
	 */
	public void addUsageTypeString(String typeStr) {
		addUsageType(TemplateUsageType.valueOf(typeStr));
	}

	public String toString() {
		return "ActionType[" + getID() + "," + getName() + ",noParams=" + parameterSize() + "]";
	}
}