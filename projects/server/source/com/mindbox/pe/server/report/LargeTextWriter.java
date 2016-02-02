/*
 * Created on Feb 8, 2006
 *
 */
package com.mindbox.pe.server.report;

import java.util.ArrayList;
import java.util.List;


/**
 * Large Text writer that breaks a large text into multiple smaller XML elements.
 * Use {@link #setSizeLimit(int)} to set the max size of each chunk. 
 * If not specified, it defaults to {@link #DEF_LIMIT}.
 * <p>
 * Usage:
 * <code>  new LargeTextWriter().asElement(sourceStr, "text", true)</code> or
 * <code>  LargeTextWriter ltw = new LargeTextWriter(); ltw.setSizeLimit(4*1024); String[] strs = ltw.breakUpText(sourceStr);</code>
 * @author Geneho Kim
 * @since PowerEditor 4.4.0
 */
public class LargeTextWriter {

	public static class Test {
		public static void main(String[] args) {
			StringBuffer b = new StringBuffer("some-text");
			for (int i = 0; i < DEF_LIMIT * 3; i++) {
				b.append(i % 2);
				b.append("<*" + i + ">");
			}
			//System.out.println("Before: " + b.toString());
			String[] strs = new LargeTextWriter().breakUpText(b.toString());
			int totalSize = 0;
			for (int i = 0; i < strs.length; i++) {
				System.out.println("Strs[" + i + "]=" + strs[i]);
				totalSize += strs[i].length();
			}
			if (b.toString().length() != totalSize)
				System.out.println("Size mismatch: expected " + b.toString().length() + ", got=" + totalSize);
			else
				System.out.println("TOTAL SIZE=" + totalSize);

			System.out.println("Elements: " + new LargeTextWriter().asElements(b.toString(), "text", true));
		}
	}

	/** Default size limit: value is 1* 1024.
	 * 
	 */
	public static final int DEF_LIMIT = 1 * 1024; // 4k characters (8kb)

	private int sizeLimit = DEF_LIMIT;

	public int getSizeLimit() {
		return sizeLimit;
	}

	public void setSizeLimit(int sizeLimit) {
		this.sizeLimit = sizeLimit;
	}

	public String[] breakUpText(String str) {
		if (str == null || str.length() == 0) return new String[0];
		List<String> list = new ArrayList<String>();
		String source = str;
		while (source.length() > sizeLimit) {
			//System.out.println("<breakup> source = " + source);
			String tmp = source.substring(0, sizeLimit);
			int index = tmp.lastIndexOf(">");
			if (index > 0 && index < tmp.length() - 1) {
				++index;
				list.add(tmp.substring(0, index));
				source = source.substring(index);
			}
			else {
				list.add(tmp);
				source = source.substring(sizeLimit);
			}
		}
		if (source.length() > 0) {
			list.add(source);
		}
		return list.toArray(new String[0]);
	}

	public String asElements(String str, String childElementName, boolean wrapWithCDATA) {
		StringBuffer buff = new StringBuffer();
		String[] strs = breakUpText(str);
		for (int i = 0; i < strs.length; i++) {
			buff.append("<");
			buff.append(childElementName);
			buff.append(">");
			if (wrapWithCDATA) {
				buff.append("<![CDATA[");
				buff.append(strs[i]);
				buff.append("]]>");
			}
			else {
				buff.append(strs[i]);
			}
			buff.append("</");
			buff.append(childElementName);
			buff.append(">");
		}
		return buff.toString();
	}
	public String asElements(String str, String childElementName, String orderElementName, String valueElementName, boolean wrapWithCDATA) {
		StringBuffer buff = new StringBuffer();
		String[] strs = breakUpText(str);
		for (int i = 0; i < strs.length; i++) {
			buff.append("<");
			buff.append(childElementName);
			buff.append(">");
			buff.append("<");
			buff.append(orderElementName);
			buff.append(">");
			buff.append(i+1);
			buff.append("</");
			buff.append(orderElementName);
			buff.append(">");
			buff.append("<");
			buff.append(valueElementName);
			buff.append(">");
			if (wrapWithCDATA) {
				buff.append("<![CDATA[");
				buff.append(strs[i]);
				buff.append("]]>");
			}
			else {
				buff.append(strs[i]);
			}
			buff.append("</");
			buff.append(valueElementName);
			buff.append(">");
			buff.append("</");
			buff.append(childElementName);
			buff.append(">");
		}
		return buff.toString();
	}
}
