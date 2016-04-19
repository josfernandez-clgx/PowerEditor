/*
 * Created on Jan 23, 2006
 *
 */
package com.mindbox.pe.client.common;

import com.mindbox.pe.model.template.GridTemplate;


/**
 * @author Geneho Kim
 * @since PowerEditor 4.4.0
 */
public final class TemplateCheckList extends CheckList {
	/**
	 * 
	 */
	private static final long serialVersionUID = -3951228734910107454L;

	protected String getListText(Object obj) {
		return (obj instanceof GridTemplate ? ((GridTemplate) obj).getName() + " (" + ((GridTemplate) obj).getVersion() + ")" : super.getListText(obj));
	}
}
