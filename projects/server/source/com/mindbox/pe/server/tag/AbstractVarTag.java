package com.mindbox.pe.server.tag;

import javax.servlet.jsp.tagext.TagSupport;


/**
 * Abstract tag with 'var' attribute.
 * 
 * @author Geneho Kim
 * @since PowerEditor 
 */
public abstract class AbstractVarTag extends TagSupport {

	private String var;

	protected Object getVarObject() {
		if (var == null) throw new IllegalStateException("var cannot be null");
		return pageContext.getAttribute(var);
	}
	
	protected void setVarObject(Object obj) {
		if (var == null) throw new IllegalStateException("var cannot be null");
		pageContext.setAttribute(var, obj);
	}
	
	public String getVar() {
		return var;
	}

	public void setVar(String var) {
		this.var = var;
	}
}
