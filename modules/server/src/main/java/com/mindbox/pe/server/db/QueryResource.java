package com.mindbox.pe.server.db;

import java.util.HashMap;
import java.util.Map;

/**
 * 
 * @author Gene Kim
 * @author MindBox, Inc
 * @since PowerEditor 1.0
 */
public final class QueryResource {

	private static QueryResource instance = null;

	public static QueryResource getInstance() {
		if (instance == null) {
			instance = new QueryResource();
		}
		return instance;
	}

	private final Map<String, String> queryMap = new HashMap<String, String>();

	private QueryResource() {
	}

	/**
	 * @param key the query key
	 * @return query
	 * @throws IllegalArgumentException if key is invalid or not found
	 */
	public String getQuery(String key) {
		if (key == null) throw new NullPointerException("key is null");
		String value = queryMap.get(key);
		if (value == null) {
			throw new IllegalArgumentException("Query for the specified key does not exist: " + key);
		}
		return value;
	}

}
