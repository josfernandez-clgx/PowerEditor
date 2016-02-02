/*
 * Created on 2004. 1. 30.
 *
 */
package com.mindbox.pe.tools.gridrepair;

/**
 * @author Geneho Kim
 * @author MindBox
 * @since PowerEditor 2.3.0
 */
public interface TemplateChangeSpec {

	int numberOfColumnChanges();
	
	TemplateColumnChangeSpec getColumnChangeSpecAt(int index);
}
