package com.mindbox.pe.server.tag;

import javax.servlet.jsp.tagext.TagSupport;


/**
 * Abstract tag with 'name' attribute.
 * @author Geneho Kim
 * @since PowerEditor 5.3.0
 */
public abstract class AbstractNameTag extends TagSupport {
	/**
	 * 
	 */
	private static final long serialVersionUID = -3951228734910107454L;

	private String name;

	protected Object getNamedObject() {
		if (name == null) throw new IllegalStateException("name cannot be null");
		return pageContext.getAttribute(name);
	}

	protected void setNamedObject(Object obj) {
		if (name == null) throw new IllegalStateException("name cannot be null");
		pageContext.setAttribute(name, obj);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
