/**
 * 
 */
package com.mindbox.pe.server.generator.value.rhscolref;

import org.apache.log4j.Logger;


abstract class AbstractWriteValueHelper<V> implements RHSColRefWriteValueHelper<V> {
	
	final Logger logger = Logger.getLogger(getClass());

	/**
	 * Creates the RHS from a String array of values
	 * @author vineet khosla
	 * @since PowerEditor 5.1
	 * @param buff
	 * @param deployValues
	 * @param printQuote
	 * @param appendNot
	 * @return string buffer for RHS
	 */
	final StringBuilder writeDeployValues(StringBuilder buff, String[] deployValues, boolean printQuote, boolean appendNot) {
		if (deployValues != null) {
			if (appendNot) {
				buff.append("NOT ");
			}
			for (int i = 0; i < deployValues.length; i++) {
				if (i > 0) buff.append(' ');
				if (printQuote) buff.append('"');
				buff.append(deployValues[i]);
				if (printQuote) buff.append('"');
			}
		}
		return buff;
	}
}