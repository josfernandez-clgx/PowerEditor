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
	 * @param merge merge indicator
	 */
	public ImportSpec(boolean merge) {
		contentMap = new TreeMap<String, String>();
		this.merge = merge;
	}

	/**
	 * Convenience contructor for a single file content.
	 * Equalivalent to <code>new ImportSpec(merge); addContent(filename,xmlContent);</code>.
	 * @param filename filename
	 * @param xmlContent XML content
	 * @param merge merge flag
	 */
	public ImportSpec(String filename, String xmlContent, boolean merge) {
		this(merge);
		addContent(filename, xmlContent);
	}

	/**
	 * Convenience contructor for two single file contents.
	 * Equalivalent to <code>new ImportSpec(importRequestType,merge);addContent(filename1,xmlContent1);addContent(filename2,xmlContent2);</code>.
	 * @param filename1 filename 1
	 * @param xmlContent1 xmlContent 1
	 * @param filename2 filename 2
	 * @param xmlContent2 xmlContent 2
	 * @param merge merge flag
	 */
	public ImportSpec(String filename1, String xmlContent1, String filename2, String xmlContent2, boolean merge) {
		this(merge);
		addContent(filename1, xmlContent1);
		addContent(filename2, xmlContent2);
	}

	public void addContent(String filename, String content) {
		contentMap.put(filename, content);
	}

	public String getContent(String filename) {
		return contentMap.get(filename);
	}

	public String[] getFilenames() {
		return contentMap.keySet().toArray(new String[0]);
	}

	public boolean isMerge() {
		return merge;
	}

	@Override
	public String toString() {
		return "ImportSpec[merge=" + merge + ",fileSize=" + contentMap.size() + "]";
	}
}
