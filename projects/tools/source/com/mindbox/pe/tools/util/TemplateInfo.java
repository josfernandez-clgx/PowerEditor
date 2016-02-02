/*
 * Created on 2004. 12. 17.
 *
 */
package com.mindbox.pe.tools.util;


/**
 * @author Geneho Kim
 * @since PowerEditor 4.2.0
 */
public class TemplateInfo {

	private final int id;
	private final String name;
	protected String version;
	private String usage;
	
	public TemplateInfo(int id, String name, String usage) {
		this(id, name, usage, null);
	}
	
	public TemplateInfo(int id, String name, String usage, String version) {
		this.id = id;
		this.name = name;
		this.usage = usage;
		this.version = version;
	}
	
	public final String getUsage() {
		return usage;
	}
	
	public final int getID() {
		return id;
	}
	
	public final String getName() {
		return name;
	}
	
	public final String getVersion() {
		return version;
	}
	
	public String toString() {
		return "TemplateVersionMap["+id+"="+version+"]";
	}
}
