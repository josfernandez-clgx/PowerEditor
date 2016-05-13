package com.mindbox.pe.model.comparator;

import java.util.Comparator;

import com.mindbox.pe.model.process.Phase;

/**
 * Comparator for {@link Phase} instances.
 * This compares name field.
 * @author Geneho Kim
 * @author MindBox
 * @since PowerEditor 2.2.0
 */
public class PhaseComparator implements Comparator<Phase> {

	private static PhaseComparator instance;

	public static PhaseComparator getInstance() {
		if (instance == null) {
			instance = new PhaseComparator();
		}
		return instance;
	}

	private PhaseComparator() {
	}

	public boolean equals(Object obj) {
		return obj instanceof PhaseComparator;
	}

	public int compare(Phase obj1, Phase obj2) {
		if (obj1 == obj2 || obj1.getID() == obj2.getID()) {
			return 0;
		}
		else {
			return obj1.getName().compareTo(obj2.getName());
		}
	}
}
