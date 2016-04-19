package com.mindbox.pe.server.generator.aemodel;

import com.mindbox.server.parser.jtb.rule.syntaxtree.Node;

// Referenced classes of package com.mindbox.server.generator:
//            AbstractAeValue

public class AeColumnValue extends AbstractAeValue {

	public String toString() {
		return "Column " + getColumnNumber();
	}

	public AeColumnValue(Node node) {
		super(node);
	}

	public int getColumnNumber() {
		return mColumnNumber;
	}

	public void setColumnNumber(int i) {
		mColumnNumber = i;
	}

	private int mColumnNumber;
}