package com.mindbox.pe.server.tag;

import javax.servlet.jsp.JspException;

import com.mindbox.pe.common.config.EntityTypeDefinition;
import com.mindbox.pe.server.config.ConfigurationManager;

/**
 * Implementation of &lt;entity-types&gt; PowerEditor custom tag.
 * <p>
 * <b>Required Parameters</b><ul>
 * <li><code>name</code> - Name of the attribute to be bound to an array of {@link EntityTypeDefinition} objects</li>
 * </ul>
 */
public class EntityTypeDefinitionsTag extends AbstractVarTag {

	private static final long serialVersionUID = 3038861095417400117L;

	static EntityTypeDefinition[] getEntityTypeDefinitions() {
		return ConfigurationManager.getInstance().getEntityConfiguration().getEntityTypeDefinitions();
	}

	public int doStartTag() throws JspException {
		setVarObject(getEntityTypeDefinitions());
		return SKIP_BODY;
	}
}
