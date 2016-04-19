package com.mindbox.pe.model;

import java.io.Serializable;
import java.util.Map;
import java.util.TreeMap;


/**
 * Import specification.
 * @author Geneho Kim
 * @since PowerEditor4.4.0 
 */
public final class ImportSpec implements Serializable {

	private static final long serialVersionUID = 20060123700000L;

	private boolean merge;
	private final Map<String, String> contentMap;

	/**
	 * Creates a new import spec.
	 * @param importRequestType must be one of {@link #IMPORT_DATA_REQUEST},{@link #IMPORT_ADHOC_ACTIONS_REQUEST}, {@link #IMPORT_TEMPLATES_REQUEST}
	 * @param merge merge indicator
	 */
	public ImportSpec(boolean merge) {
		contentMap = new TreeMap<String, String>();
		this.merge = merge;
	}

	/**
	 * Convenience contructor for a single file content.
	 * Equalivalent to <code>new ImportSpec(importRequestType,merge);addContent(filename,xmlContent);</code>.
	 * @param filename
	 * @param xmlContent
	 * @param importRequestType 
	 * @param merge
	 */
	public ImportSpec(String filename, String xmlContent, boolean merge) {
		this(merge);
		addContent(filename, xmlContent);
	}

	/**
	 * Convenience contructor for two single file contents.
	 * Equalivalent to <code>new ImportSpec(importRequestType,merge);addContent(filename1,xmlContent1);addContent(filename2,xmlContent2);</code>.
	 * @param filename1
	 * @param xmlContent1
	 * @param filename2
	 * @param xmlContent2
	 * @param importRequestType
	 * @param merge
	 */
	public ImportSpec(String filename1, String xmlContent1, String filename2, String xmlContent2, boolean merge) {
		this(merge);
		addContent(filename1, xmlContent1);
		addContent(filename2, xmlContent2);
	}

	public void addContent(String filename, String content) {
		contentMap.put(filename, content);
	}

	public boolean isMerge() {
		return merge;
	}

	public String[] getFilenames() {
		return contentMap.keySet().toArray(new String[0]);
	}

	public String getContent(String filename) {
		return contentMap.get(filename);
	}

	public String toString() {
		return "ImportSpec[merge=" + merge + ",fileSize=" + contentMap.size() + "]";
	}
}
