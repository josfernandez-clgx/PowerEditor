package com.mindbox.pe.model.rule;

/**
 * @author deklerk
 *
 */
public abstract class FunctionCall extends AbstractCompoundRuleElement<FunctionParameter> {

	/**
	 * @param dispName
	 */
	public FunctionCall(String dispName) {
		super(dispName);
	}
	private FunctionTypeDefinition functionType = null;

	public FunctionCall(FunctionCall funcall) {
		this(funcall.name);
		this.functionType = funcall.getFunctionType();
		super.setComment(funcall.getComment());
		for (int i = 0; i < funcall.size(); i++) {
			super.add(RuleElementFactory.deepCopyFunctionParameter(funcall.get(i)));
		}
	}

	public FunctionTypeDefinition getFunctionType() {
		return functionType;
	}

	public void setFunctionType(FunctionTypeDefinition type) {
		this.functionType = type;
	}

	public void clear() {
		this.functionType = null;
		super.setComment(null);
		super.removeAll();
	}

	public void adjustChangedColumnReferences(int originalColNum, int newColNum) {
		for (int i = 0; i < this.size(); i++) this.get(i).adjustChangedColumnReferences(originalColNum, newColNum);
	}

	public void adjustDeletedColumnReferences(int colNo) {
		for (int i = 0; i < this.size();i++) 
			if (this.get(i).containsColumnReference(colNo)) {
				FunctionParameter p = this.get(i);
				int paramIndex = p.index();
				this.remove(i);
				this.insert(i, RuleElementFactory.getInstance().createFunctionParameter(paramIndex,p.toDisplayName(),""));
			}
	}


}
