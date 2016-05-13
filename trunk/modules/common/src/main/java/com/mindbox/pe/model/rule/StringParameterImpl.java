package com.mindbox.pe.model.rule;

/**
 * @author kim
 * @author MindBox
 * @since PowerEditor 
 */
class StringParameterImpl extends AbstractParameter {
	
	private static final long serialVersionUID = 8574015726916261657L;

	private String value;
	
	StringParameterImpl(int index, String name, String value) {
		super(name, index);
		this.value = value;
	}
	
	void setValue(String value) {
		this.value = value;
	}
	
	public String toString() {
		return super.toString()+"[value="+value+"]";
	}

	public String valueString() {
		return value;
	}
	
}
