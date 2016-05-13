package com.mindbox.pe.model.comparator;

import java.util.Comparator;

import com.mindbox.pe.model.template.GridTemplate;


/**
 * Guideline template comparator.
 * @author Geneho Kim
 * @since PowerEditor 4.2.0
 */
public class GuidelineTemplateComparator implements Comparator<GridTemplate> {
	
	private static GuidelineTemplateComparator instance;

	public static GuidelineTemplateComparator getInstance() {
		if (instance == null) {
			instance = new GuidelineTemplateComparator();
		}
		return instance;
	}

	private GuidelineTemplateComparator() {
	}

	public int compare(GridTemplate arg0, GridTemplate arg1) {
		if (arg0 == arg1) return 0;
		int result = arg0.getName().compareTo(arg1.getName());
		if (result == 0) {
			return arg0.getVersion().compareTo(arg1.getVersion());
		}
		else {
			return result;
		}
	}

}