package com.mindbox.pe.server.tag;

import java.io.IOException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;

import com.mindbox.pe.common.config.CategoryTypeDefinition;
import com.mindbox.pe.model.ContextContainer;
import com.mindbox.pe.model.GenericCategory;
import com.mindbox.pe.model.GenericEntity;
import com.mindbox.pe.model.GenericEntityType;
import com.mindbox.pe.model.table.CategoryOrEntityValue;
import com.mindbox.pe.server.cache.EntityManager;

/**
 * Implementation of &lt;as-xml-element&gt; PowerEditor custom tag.
 * <p>
 * <b>Required Parameters</b><ul>
 * <li><code>value</code> - the value to be output as a valid XML element name</li>
 * </ul>
 * @author Geneho Kim
 * @since PowerEditor 
 */
public class WriteContextTag extends AbstractValueTag {

	private static final long serialVersionUID = -4799318067721560041L;

	private static final String NAME_NAME_SEPARATOR = ",";
	private static final String TYPE_NAME_SEPARATOR = ": ";
	private static final String TYPE_TYPE_SEPARATOR = ";";

	static final String toContextTagValue(CategoryOrEntityValue categoryOrEntityValue) throws IOException {
		return toContextTagValue(categoryOrEntityValue.getEntityType(), categoryOrEntityValue.getId(), categoryOrEntityValue.isForEntity());
	}

	static final String toContextTagValue(GenericEntityType entityType, int id, boolean forEntity) throws IOException {
		return toContextTagValue(entityType, new int[] { id }, forEntity);
	}

	private static String toContextTagValue(GenericEntityType entityType, int[] ids, boolean forEntity) throws IOException {
		StringBuffer buff = new StringBuffer();
		appendContextTagValue(buff, entityType, ids, forEntity);
		return buff.toString();
	}

	private static void appendContextTagValue(StringBuffer buff, GenericEntityType entityType, int[] ids, boolean forEntity)
			throws IOException {
		if (ids == null || ids.length == 0) return;
		if (forEntity) {
			buff.append(entityType.getDisplayName());
			buff.append(TYPE_NAME_SEPARATOR);
			for (int i = 0; i < ids.length; i++) {
				if (i > 0) buff.append(NAME_NAME_SEPARATOR);
				GenericEntity entity = EntityManager.getInstance().getEntity(entityType, ids[i]);
				buff.append((entity == null ? entityType.getName() + " " + ids[i] : entity.getName()));
			}
		}
		else {
			CategoryTypeDefinition categoryTypeDefinition = CategoryTypeDefinitionTag.getCategoryTypeDefinition(entityType.getCategoryType());
			buff.append(categoryTypeDefinition.getName());
			buff.append(TYPE_NAME_SEPARATOR);
			for (int i = 0; i < ids.length; i++) {
				if (i > 0) buff.append(NAME_NAME_SEPARATOR);
				GenericCategory category = EntityManager.getInstance().getGenericCategory(entityType.getCategoryType(), ids[i]);
				buff.append((category == null ? categoryTypeDefinition.getName() + " " + ids[i] : category.getName()));
			}
		}
	}
	
	static final String toContextTagValue(ContextContainer contextContainer) throws IOException {
		StringBuffer buff = new StringBuffer();
		boolean isFirst = true;
		if (contextContainer.hasAnyGenericEntityContext() || contextContainer.hasAnyGenericCategoryContext()) {
			GenericEntityType[] entityTypes = GenericEntityType.getAllGenericEntityTypes();
			for (int i = 0; i < entityTypes.length; i++) {
				if (entityTypes[i].isUsedInContext()) {
					if (!isFirst) buff.append(TYPE_TYPE_SEPARATOR);
					if (contextContainer.hasGenericEntityContext(entityTypes[i])) {
						int[] ids = contextContainer.getGenericEntityIDs(entityTypes[i]);
						appendContextTagValue(buff, entityTypes[i], ids, true);
						isFirst = false;
					}
					else if (contextContainer.hasGenericCategoryContext(entityTypes[i])) {
						int[] ids = contextContainer.getGenericEntityIDs(entityTypes[i]);
						appendContextTagValue(buff, entityTypes[i], ids, false);
						isFirst = false;
					}
				}
			}
		}
		return buff.toString();
	}

	public int doStartTag() throws JspException {
		if (value == null) return SKIP_BODY;
		if (!(value instanceof ContextContainer)) {
			throw new JspException("value must be an instance of " + ContextContainer.class.getName());
		}
		ContextContainer contextContainer = (ContextContainer) value;
		try {
			JspWriter writer = pageContext.getOut();
			writer.print("<context>");
			writer.print(toContextTagValue(contextContainer));
			writer.println("</context>");
		}
		catch (IOException e) {
			throw new JspException(e);
		}

		return SKIP_BODY;
	}
	
}
