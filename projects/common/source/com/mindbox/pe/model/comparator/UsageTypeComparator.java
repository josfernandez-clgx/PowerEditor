package com.mindbox.pe.model.comparator;

import java.util.Comparator;

import com.mindbox.pe.model.TemplateUsageType;

/**
 * Comparator for {@link TemplateUsageType} instances.
 * @author Geneho Kim
 * @author MindBox
 * @since PowerEditor 2.2.0
 */
public class UsageTypeComparator implements Comparator<TemplateUsageType> {

	private static UsageTypeComparator instance = null;

	public static UsageTypeComparator getInstance() {
		if (instance == null) {
			instance = new UsageTypeComparator();
		}
		return instance;
	}
	
	
	private UsageTypeComparator() {
	}

	public boolean equals(Object obj) {
		return obj instanceof UsageTypeComparator;
	}
	
	public int compare(TemplateUsageType obj1, TemplateUsageType obj2) {
		if (obj1 == obj2 || obj1.getDisplayName() == obj2.getDisplayName()) {
			return 0;
		}
		else {
			return obj1.getDisplayName().compareTo(obj2.getDisplayName());
		}
	}
}
