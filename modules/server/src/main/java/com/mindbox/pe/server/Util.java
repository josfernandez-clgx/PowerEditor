package com.mindbox.pe.server;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

import javax.servlet.http.HttpServletRequest;

import com.mindbox.pe.common.UtilBase;
import com.mindbox.pe.model.GuidelineContext;
import com.mindbox.pe.server.cache.EntityManager;

/**
 * Server Utility method container.
 * @author Geneho Kim
 * @since PowerEditor 1.0
 */
public class Util extends UtilBase {

	public static final String APPLICATION_USERNAME = "PowerEditor";

	private static Util instance = null;

	private static final Map<String, Util> moduleInstances = Collections.synchronizedMap(new HashMap<String, Util>());

	public static int booleanValue(boolean flag) {
		if (flag == true) {
			return 1;
		}
		else {
			return 0;
		}
	}

	public static String extractServerBasePath(HttpServletRequest request) {
		return request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath();
	}

	public static String formatDB2DateString(String date_str) {

		if (date_str.length() == 26) {
			date_str = date_str.substring(0, 10) + " " + date_str.substring(11, 13) + ":" + date_str.substring(14, 16) + ":" + date_str.substring(17, 19);
		}
		return date_str;

	}

	public static final int[] fromIDListString(String idStr) {
		if (idStr == null) {
			return null;
		}
		StringTokenizer st = new StringTokenizer(idStr, ",");
		int[] ids = new int[st.countTokens()];
		for (int i = 0; st.hasMoreTokens(); i++) {
			ids[i] = Integer.valueOf(st.nextToken()).intValue();
		}
		return ids;
	}

	public static Util getUtilInstance() {
		if (instance == null) instance = new Util();
		return instance;
	}

	public static Util getUtilInstance(String s) {
		Util util = moduleInstances.get(s);
		if (util == null) {
			util = new Util();
			moduleInstances.put(s, util);
		}
		return util;
	}

	/**
	 * 
	 * @param contexts
	 * @return string representation of <code>contexts</code>
	 * @since PowerEditor 4.1.1
	 */
	public static final String toString(GuidelineContext[] contexts) {
		StringBuilder buff = new StringBuilder();
		if (contexts != null && contexts.length > 0) {
			for (int i = 0; i < contexts.length; i++) {
				if (i > 0) buff.append(System.getProperty("line.separator"));

				if (contexts[i].getGenericEntityType() != null) {
					// handle generic entity types
					buff.append(contexts[i].getGenericEntityType().toString().toUpperCase());
					buff.append(": ");
					int[] ids = contexts[i].getIDs();
					for (int j = 0; j < ids.length; j++) {
						if (j > 0) buff.append(",  ");
						buff.append(EntityManager.getInstance().getEntity(contexts[i].getGenericEntityType(), ids[j]).getName());
					}
				}
				else if (contexts[i].getGenericCategoryType() > 0) {
					// handle generic entity types
					buff.append(contexts[i].getGenericCategoryType());
					buff.append(": ");
					int[] ids = contexts[i].getIDs();
					for (int j = 0; j < ids.length; j++) {
						if (j > 0) buff.append(",  ");
						buff.append(EntityManager.getInstance().getGenericCategory(contexts[i].getGenericCategoryType(), ids[j]));
					}
				}
			}
		}
		return buff.toString();
	}

	public static final String toString(int[] ids) {
		if (ids == null) {
			return null;
		}
		StringBuilder buff = new StringBuilder();
		for (int i = 0; i < ids.length; i++) {
			buff.append(ids[i]);
			if (i < ids.length - 1) {
				buff.append(",");
			}
		}
		return buff.toString();
	}

	public static String xmlify(String str) {
		if (UtilBase.isEmptyAfterTrim(str)) return "";
		String str2 = str.replaceAll("&", "&amp;").replaceAll(">", "&gt;").replaceAll("<", "&lt;").replaceAll("\"", "&quot;").replaceAll("'", "&apos;");
		StringBuilder buff = new StringBuilder(str2.length());
		for (int i = 0; i < str2.length(); i++) {
			int ci = str2.charAt(i);
			if (ci > 255) {
				buff.append("&#x");
				buff.append(Integer.toHexString(ci));
				buff.append(';');
			}
			else {
				buff.append(str2.charAt(i));
			}
		}
		return buff.toString();
	}

	private Util() {
		init();
	}

	private void init() {
	}

}