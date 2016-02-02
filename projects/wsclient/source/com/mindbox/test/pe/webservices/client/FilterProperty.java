package com.mindbox.test.pe.webservices.client;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class FilterProperty {

	private static final boolean asBoolean(String value) {
		return (value != null && value.equalsIgnoreCase("YES") || Boolean.valueOf(value).booleanValue());
	}

	private static final int asInt(String value, int defValue) {
		try {
			return (value == null ? defValue : Integer.parseInt(value));
		}
		catch (NumberFormatException ex) {
			return defValue;
		}
	}

	private static List<Integer> toIntList(String str) {
		List<Integer> intList = new ArrayList<Integer>();
		if (str != null && str.length() > 0) {
			String[] strs = str.split(",");
			for (String s : strs) {
				intList.add(Integer.parseInt(s.trim()));
			}
		}
		return intList;
	}

	private static List<String> toStringList(String str) {
		List<String> list = new ArrayList<String>();
		if (str != null && str.length() > 0) {
			String[] strs = str.split(",");
			for (String s : strs) {
				list.add(s.trim());
			}
		}
		return list;
	}

	private static String getProperty(Properties props, String key) {
		String value = props.getProperty(key);
		return (value == null ? null : (value.trim().length() == 0 ? null : value.trim()));
	}
	
	public static FilterProperty loadAsFilterProperty(File propertyFile) throws IOException {
		FilterProperty filterProperty = new FilterProperty();
		try {
			Properties props = new Properties();
			props.load(new FileReader(propertyFile));

			filterProperty.includeEntities = asBoolean(getProperty(props,"includeEntities"));
			filterProperty.includeSecurity = asBoolean(getProperty(props,"includeSecurity"));
			filterProperty.includeGuidelines = asBoolean(getProperty(props,"includeGuidelines"));
			filterProperty.includeParameters = asBoolean(getProperty(props,"includeParameters"));
			filterProperty.includeTemplates = asBoolean(getProperty(props,"includeTemplates"));
			filterProperty.includeGuidelineActions = asBoolean(getProperty(props,"includeGuidelineActions"));
			filterProperty.includeTestConditions = asBoolean(getProperty(props,"includeTestConditions"));
			filterProperty.includeDateSynonyms = asBoolean(getProperty(props,"includeDateSynonyms"));
			filterProperty.includeEmptyContexts = asBoolean(getProperty(props,"includeEmptyContexts"));
			filterProperty.includeParentCategories = asBoolean(getProperty(props,"includeParentCategories"));
			filterProperty.includeChildrenCategories = asBoolean(getProperty(props,"includeChildrenCategories"));
			filterProperty.includeProcessData = asBoolean(getProperty(props,"includeProcessData"));
			filterProperty.includeCBR = asBoolean(getProperty(props,"includeCBR"));
			filterProperty.useDaysAgo = asBoolean(getProperty(props,"useDaysAgo"));
			filterProperty.daysAgo = asInt(getProperty(props,"daysAgo"), 30);
			filterProperty.activeOnDate = getProperty(props,"activeOnDate");
			filterProperty.contextElements = getProperty(props,"contextElements");
			filterProperty.status = getProperty(props,"status");
			filterProperty.guidelineTemplateIDs.addAll(toIntList(getProperty(props,"guidelineTemplateIDs")));
			filterProperty.paramTemplateIDs.addAll(toIntList(getProperty(props,"paramTemplateIDs")));
			filterProperty.usageTypes.addAll(toStringList(getProperty(props,"usageTypes")));
		}
		catch (FileNotFoundException ex) {
			System.err.println("");
			System.err.println("WARNING:  PowerEditor WSClient properties file is not found at " + propertyFile.getAbsolutePath());
			System.err.println("WARNING:  No filter criteria will be supplied to WS calls!");
			System.err.println("");
		}
		return filterProperty;
	}

	private boolean includeEntities = true;
	private boolean includeSecurity = true;
	private boolean includeGuidelines = true;
	private boolean includeParameters = true;
	private boolean includeTemplates = true;
	private boolean includeGuidelineActions = true;
	private boolean includeTestConditions = true;
	private boolean includeDateSynonyms = true;
	private boolean includeEmptyContexts = true;
	private boolean includeParentCategories = true;
	private boolean includeChildrenCategories = true;
	private boolean includeProcessData = false;
	private boolean includeCBR = false;
	private boolean useDaysAgo = true;
	private int daysAgo = 30;

	private String activeOnDate = null;
	private String contextElements = null;
	private String status = null;

	private final List<Integer> guidelineTemplateIDs = new ArrayList<Integer>();
	private final List<Integer> paramTemplateIDs = new ArrayList<Integer>();
	private final List<String> usageTypes = new ArrayList<String>();

	public boolean isIncludeEntities() {
		return includeEntities;
	}

	public boolean isIncludeSecurity() {
		return includeSecurity;
	}

	public boolean isIncludeGuidelines() {
		return includeGuidelines;
	}

	public boolean isIncludeParameters() {
		return includeParameters;
	}

	public boolean isIncludeTemplates() {
		return includeTemplates;
	}

	public boolean isIncludeGuidelineActions() {
		return includeGuidelineActions;
	}

	public boolean isIncludeTestConditions() {
		return includeTestConditions;
	}

	public boolean isIncludeDateSynonyms() {
		return includeDateSynonyms;
	}

	public boolean isIncludeEmptyContexts() {
		return includeEmptyContexts;
	}

	public boolean isIncludeParentCategories() {
		return includeParentCategories;
	}

	public boolean isIncludeChildrenCategories() {
		return includeChildrenCategories;
	}

	public boolean isIncludeProcessData() {
		return includeProcessData;
	}

	public boolean isIncludeCBR() {
		return includeCBR;
	}

	public boolean isUseDaysAgo() {
		return useDaysAgo;
	}

	public int getDaysAgo() {
		return daysAgo;
	}

	public String getActiveOnDate() {
		return activeOnDate;
	}

	public String getContextElements() {
		return contextElements;
	}

	public String getStatus() {
		return status;
	}

	public List<Integer> getGuidelineTemplateIDs() {
		return guidelineTemplateIDs;
	}

	public List<Integer> getParamTemplateIDs() {
		return paramTemplateIDs;
	}

	public List<String> getUsageTypes() {
		return usageTypes;
	}

}
