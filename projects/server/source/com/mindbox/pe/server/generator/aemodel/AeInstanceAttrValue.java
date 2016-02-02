package com.mindbox.pe.server.generator.aemodel;

import com.mindbox.server.parser.jtb.rule.syntaxtree.Node;

// Referenced classes of package com.mindbox.server.generator:
//            AbstractAeValue

public class AeInstanceAttrValue extends AbstractAeValue {

	public AeInstanceAttrValue(Node node) {
		super(node);
	}

	public String getClassOrObjectName() {
		return mClassOrObjectName;
	}

	public void setClassOrObjectName(String s) {
		mClassOrObjectName = s;
	}

	public String getAttributeName() {
		return mAttributeName;
	}

	public void setAttributeName(String s) {
		mAttributeName = s;
	}

	public String toString() {
		return "AeInstanceAttrValue["+mClassOrObjectName+"." + mAttributeName+"]";
	}
	
	public int hashCode() {
		return (mClassOrObjectName+"."+mAttributeName).hashCode();
	}
	
	public boolean equals(Object obj) {
		if (obj instanceof AeInstanceAttrValue) {
			return this.hashCode() == obj.hashCode();
		}
		else {
			return false;
		}
	}
	
	private String mClassOrObjectName;
	private String mAttributeName;
}