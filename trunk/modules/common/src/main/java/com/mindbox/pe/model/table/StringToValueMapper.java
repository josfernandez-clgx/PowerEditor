/*
 * Created on 2004. 1. 16.
 *
 */
package com.mindbox.pe.model.table;

/**
 * @author Geneho Kim
 * @author MindBox
 * @since PowerEditor 1.2.0
 */
interface StringToValueMapper {
	Object valueOf(String str) throws Exception;
}
