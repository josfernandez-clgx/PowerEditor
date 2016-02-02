package com.mindbox.pe.model;


/**
 * @author Geneho Kim
 * @author MindBox
 * @since PowerEditor 2.2.0
 */
public class ParameterTemplate extends AbstractTemplateCore<ParameterTemplateColumn> implements Auditable {

	public static final int DEPLOY_AS_OBJECTS = 0;
	public static final int RUN_SCRIPT_ONCE = 1;

	private static final long serialVersionUID = 20031226130010000L;

	private int deployMethod = DEPLOY_AS_OBJECTS;
	private String deployScriptDetails = null;

	/**
	 * Added for digester support.
	 * @since PowerEditor 3.2.0
	 */
	public ParameterTemplate() {
		super(-1, "", null);
	}

	/**
	 * @param id
	 * @param name
	 */
	public ParameterTemplate(int id, String name, int maxRow, String desc) {
		super(id, name, null, maxRow, desc);
	}

	private ParameterTemplate(ParameterTemplate source) {
		super(source);
		this.deployScriptDetails = source.deployScriptDetails;
		this.deployMethod = source.deployMethod;
	}

	public Auditable deepCopy() {
		return new ParameterTemplate(this);
	}

	public String getAuditDescription() {
		return "parameter template '" + getName() + "'";
	}
	
	public String getDeployScriptDetails() {
		return deployScriptDetails;
	}

	public int getDeployMethod() {
		return deployMethod;
	}

	public void setDeployScriptDetails(String details) {
		this.deployScriptDetails = details;
		this.deployMethod = RUN_SCRIPT_ONCE;
	}

	protected ParameterTemplateColumn createTemplateColumn(ParameterTemplateColumn source) {
		ParameterTemplateColumn column = new ParameterTemplateColumn(source);
		return column;
	}

	protected void adjustDeletedColumnReferences(int columnNo) {
		// noop
	}

	protected void adjustChangedColumnReferences(int columnNo, int newColumnNo) {
		// noop
	}

}
