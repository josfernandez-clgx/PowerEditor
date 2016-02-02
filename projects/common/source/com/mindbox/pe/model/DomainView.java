/*
 * Created on 2004. 6. 3.
 *
 */
package com.mindbox.pe.model;

import java.io.InvalidObjectException;
import java.io.ObjectStreamException;


/**
 * Domain View types.
 * @author Geneho
 * @since PowerEditor 3.2.0
 */
public class DomainView  extends EnumerationBase {


	private static final long serialVersionUID = 200406031100000L;
	
	private static final String NAME_POLICY_EDITOR = "PolicyEditor";
	private static final String NAME_TEMPLATE_EDITOR = "TemplateEditor";
	private static final String NAME_INPUT_INTERFACE = "InputInterface";
	private static final String NAME_OUTPUT_INTERFACE = "OutputInterface";
	private static final String NAME_ENGINE_OBJECTS = "EngineApp";
	
	public final static DomainView POLICY_EDITOR = new DomainView(1, NAME_POLICY_EDITOR);

	public final static DomainView TEMPLATE_EDITOR = new DomainView(2, NAME_TEMPLATE_EDITOR);
	
	public final static DomainView INPUT_INTERFACE = new DomainView(11, NAME_INPUT_INTERFACE);

	public final static DomainView OUTPUT_INTERFACE = new DomainView(12, NAME_OUTPUT_INTERFACE);

	public final static DomainView ENGINE_OBJECTS = new DomainView(99, NAME_ENGINE_OBJECTS);

	public static DomainView forID(int id) {
		switch (id) {
		case 1 : return POLICY_EDITOR;
		case 2 : return TEMPLATE_EDITOR;
		case 11: return INPUT_INTERFACE;
		case 12: return OUTPUT_INTERFACE;
		case 99: return ENGINE_OBJECTS;
		default:
			throw new IllegalArgumentException("Invalid domain view id " + id);
		}
	}
	
	public static DomainView forName(String str) {
		if (str == null) throw new IllegalArgumentException("domain view name cannot be null");
		if (str.equalsIgnoreCase(NAME_POLICY_EDITOR)) {
			return POLICY_EDITOR;
		}
		if (str.equalsIgnoreCase(NAME_TEMPLATE_EDITOR)) {
			return TEMPLATE_EDITOR;
		}
		if (str.equalsIgnoreCase(NAME_INPUT_INTERFACE)) {
			return INPUT_INTERFACE;
		}
		if (str.equalsIgnoreCase(NAME_OUTPUT_INTERFACE)) {
			return OUTPUT_INTERFACE;
		}
		if (str.equalsIgnoreCase(NAME_ENGINE_OBJECTS)) {
			return ENGINE_OBJECTS;
		}
		throw new IllegalArgumentException("Invalid domain view name: " + str);
	}
	
	/**
	 * @param id
	 * @param name
	 */
	private DomainView(int id, String name) {
		super(id, name);
	}

	private Object readResolve() throws ObjectStreamException {
		try {
			return forID(this.id);
		}
		catch (IllegalArgumentException ex) {
			throw new InvalidObjectException(ex.getMessage());
		}
	}

}
