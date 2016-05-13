package com.mindbox.pe.server.generator.aemodel;

import java.util.List;

import com.mindbox.server.parser.jtb.rule.syntaxtree.Node;

public class AeLhs extends AeObject {

	public AeLhs(Node pNode) {
		super(pNode);
	}

	public List<?> getObjectPatterns() {
		return mObjectPatterns;
	}

	public void setObjectPatterns(List<?> pObjectPatterns) {
		mObjectPatterns = pObjectPatterns;
	}

	private List<?> mObjectPatterns;
}