package com.mindbox.pe.server.generator.aeobjects;

public class LinkHelper {

	private LinkHelper() {}

	public static void link(
		AbstractAeCompoundCondition abstractaecompoundcondition,
		AbstractAeCondition abstractaecondition) {
		AbstractAeCompoundCondition abstractaecompoundcondition1 = abstractaecondition.getParentCondition();
		if (abstractaecompoundcondition1 != null)
			abstractaecompoundcondition1.removeCondition(abstractaecondition);
		abstractaecondition.setParentCondition(abstractaecompoundcondition);
		abstractaecompoundcondition.addCondition(abstractaecondition);
	}
}