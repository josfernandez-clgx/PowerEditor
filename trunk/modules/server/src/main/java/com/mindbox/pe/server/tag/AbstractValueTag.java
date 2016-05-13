package com.mindbox.pe.server.tag;

import javax.servlet.jsp.tagext.TagSupport;


/**
 * Abstract tag with 'value' attribute.
 * 
 * @author Geneho Kim
 * @since PowerEditor 
 */
public abstract class AbstractValueTag extends TagSupport {
	/**
	 * 
	 */
	private static final long serialVersionUID = -3951228734910107454L;

	protected Object value;

	protected Object getValueObject() {
		if (value == null) throw new IllegalStateException("value cannot be null");
		return value;
	}

	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
		this.value = value;
	}
}
