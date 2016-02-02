package com.mindbox.pe.model.table;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

import com.mindbox.pe.common.UtilBase;

/**
 * Generic IRange value holder for parsing strings for IRange values.
 * @author Geneho Kim
 * @author MindBox
 * @since PowerEditor 2.2.2
 */
final class GenericIRangeCopyCat {

	private static String removeNonNumericChars(String source) {
		char[] chars = source.toCharArray();
		StringBuffer buff = new StringBuffer();
		for (int i = 0; i < chars.length; i++) {
			if (chars[i] != ',' && chars[i] != '$') {
				buff.append(chars[i]);
			}
		}
		return buff.toString();
	}

	private static Pattern pattern = Pattern.compile("^([\\(\\[]-[0-9/\\.,$]+|[\\(\\[][0-9/\\.,$eE]*)?\\-(\\-?[0-9/\\.,$eE]*[\\)\\]])?$");

	private static final Logger LOG = Logger.getLogger(GenericIRangeCopyCat.class);

	/**
	 * @return null if <code>s</code> is null or on exception; instance of this class, otherwise
	 */
	static GenericIRangeCopyCat valueOf(String s, StringToValueMapper valueMapper) {
		boolean lowerValInclusive = true;
		boolean upperValInclusive = true;
		Object lowerVal = null;
		Object upperVal = null;

		if (!UtilBase.isEmpty(s)) {
			try {
				Matcher matcher = pattern.matcher(UtilBase.removeBlanks(s));
				if (matcher.matches()) {
					final String group1Str = matcher.group(1);
					final String group2Str = matcher.group(2);
					// discard matcher
					matcher = null;
					
					if (group1Str != null) {
						lowerValInclusive = (group1Str.charAt(0) == '[');
						if (group1Str.length() > 1) {
							String lowerValString = removeNonNumericChars(group1Str.substring(1));
							lowerVal = valueMapper.valueOf(lowerValString);
						}
					}
					if (group2Str != null) {
						int lastIndex = group2Str.length() - 1;
						upperValInclusive = (group2Str.charAt(lastIndex) == ']');
						if (group2Str.length() > 1) {
							String upperValString = removeNonNumericChars(group2Str.substring(0, lastIndex));
							upperVal = valueMapper.valueOf(upperValString);
						}
					}
				}
			}
			catch (Exception e) {
				LOG.warn("Error in valueOf()", e);
			}
		}
		return new GenericIRangeCopyCat(lowerValInclusive, upperValInclusive, lowerVal, upperVal);
	}

	final boolean lowerValInclusive;
	final boolean upperValInclusive;
	final Object lowerVal;
	final Object upperVal;

	private GenericIRangeCopyCat(boolean lowerValInclusive, boolean upperValInclusive, Object lowerVal, Object upperVal) {
		super();
		this.lowerValInclusive = lowerValInclusive;
		this.upperValInclusive = upperValInclusive;
		this.lowerVal = lowerVal;
		this.upperVal = upperVal;
	}
}
