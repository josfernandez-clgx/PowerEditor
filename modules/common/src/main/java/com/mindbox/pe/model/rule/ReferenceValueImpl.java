package com.mindbox.pe.model.rule;

/**
 * @author kim
 * @author MindBox
 * @since PowerEditor 4.0
 */
class ReferenceValueImpl extends ReferenceImpl implements Value {

	private static final long serialVersionUID = -2127830274911958934L;

	/**
	 * @param className
	 * @param attribName
	 */
	ReferenceValueImpl(String className, String attribName) {
		super(className, attribName);
	}

	ReferenceValueImpl(Reference ref) {
		super(ref);
	}
}
