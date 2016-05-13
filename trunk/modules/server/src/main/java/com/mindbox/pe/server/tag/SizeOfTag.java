package com.mindbox.pe.server.tag;

import java.io.IOException;
import java.util.Collection;
import java.util.Map;

import javax.servlet.jsp.JspException;

public class SizeOfTag extends AbstractValueTag {

	private static final long serialVersionUID = -2082380420866835576L;

	public int doStartTag() throws JspException {
		Object obj = getValue();
		try {
			int size = 0;
			if (Collection.class.isInstance(obj)) {
				size = ((Collection<?>) obj).size();
			}
			else if (Map.class.isInstance(obj)) {
				size = ((Map<?, ?>) obj).size();
			}
			else if (obj instanceof Object[]) {
				size = ((Object[]) obj).length;
			}
			pageContext.getOut().print(size);
		}
		catch (IOException e) {
			throw new JspException(e);
		}
		return SKIP_BODY;
	}
}
