package com.mindbox.pe.server;

import java.util.HashSet;
import java.util.Set;

/**
 * Provides Set operations.
 * @author Gene Kim
 * @author MindBox, Inc
 */
public final class SetOperations {

	/**
	 * 
	 * @param <T> element type
	 * @param set1 set1
	 * @param set2 set2
	 * @return the set which is an intersection of set1 and set2
	 */
	public static <T> Set<T> intersection(Set<T> set1, Set<T> set2) {
		Set<T> set = new HashSet<T>();
		for (T elementFrom2 : set2) {
			if (set1.contains(elementFrom2)) {
				set.add(elementFrom2);
			}
		}
		return set;
	}

	/**
	 * @param <T> element type
	 * @param set1 set1
	 * @param set2 set2
	 * @return the set which is a union of set1 and set2.
	 */
	public static <T> Set<T> union(Set<T> set1, Set<T> set2) {
		Set<T> set = new HashSet<T>();
		set.addAll(set1);
		for (T elementFrom2 : set2) {
			if (!set1.contains(elementFrom2)) {
				set.add(elementFrom2);
			}
		}
		return set;
	}

	private SetOperations() {
	}
}
