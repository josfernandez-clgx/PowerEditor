package com.mindbox.pe.model;

public interface ContextContainer {

	boolean hasAnyGenericCategoryContext();
	boolean hasGenericCategoryContext(GenericEntityType type);
	GenericEntityType[] getGenericCategoryEntityTypesInUse();
	int[] getGenericCategoryIDs(GenericEntityType type);
	
	boolean hasAnyGenericEntityContext();
	boolean hasGenericEntityContext(GenericEntityType type);
	GenericEntityType[] getGenericEntityTypesInUse();
	int[] getGenericEntityIDs(GenericEntityType type);
	
	GuidelineContext[] extractGuidelineContext();
}
