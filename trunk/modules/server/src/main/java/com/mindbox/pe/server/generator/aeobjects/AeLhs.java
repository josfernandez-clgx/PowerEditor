package com.mindbox.pe.server.generator.aeobjects;

import com.mindbox.server.parser.jtb.rule.syntaxtree.Node;
import java.util.List;

public class AeLhs extends AeObject {

	public AeLhs(Node pNode) {
		super(pNode);
	}

	public List<AeObjectPattern> getObjectPatterns() {
		return mObjectPatterns;
	}

	public void setObjectPatterns(List<AeObjectPattern> pObjectPatterns) {
		mObjectPatterns = pObjectPatterns;
	}

	private List<AeObjectPattern> mObjectPatterns;
}