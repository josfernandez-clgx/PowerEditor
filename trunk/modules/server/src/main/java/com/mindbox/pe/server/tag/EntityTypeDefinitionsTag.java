package com.mindbox.pe.server.tag;

import java.util.List;

import javax.servlet.jsp.JspException;

import com.mindbox.pe.server.config.ConfigurationManager;
import com.mindbox.pe.xsd.config.EntityType;

/**
 * Implementation of &lt;entity-types&gt; PowerEditor custom tag.
 * <p>
 * <b>Required Parameters</b><ul>
 * <li><code>name</code> - Name of the attribute to be bound to an array of {@link EntityTypeDefinition} objects</li>
 * </ul>
 */
public class EntityTypeDefinitionsTag extends AbstractVarTag {

	private static final long serialVersionUID = 3038861095417400117L;

	static List<EntityType> getEntityTypeDefinitions() {
		return ConfigurationManager.getInstance().getEntityConfigHelper().getEntityTypeDefinitions();
	}

	public int doStartTag() throws JspException {
		setVarObject(getEntityTypeDefinitions());
		return SKIP_BODY;
	}
}
