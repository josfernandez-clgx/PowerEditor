package com.mindbox.pe.model.template;

import com.mindbox.pe.model.Auditable;


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
	 * 
	 * @param id id
	 * @param name name
	 * @param maxRow maxRow
	 * @param desc desc
	 */
	public ParameterTemplate(int id, String name, int maxRow, String desc) {
		super(id, name, null, maxRow, desc);
	}

	private ParameterTemplate(ParameterTemplate source) {
		super(source);
		this.deployScriptDetails = source.deployScriptDetails;
		this.deployMethod = source.deployMethod;
	}

	@Override
	protected void adjustChangedColumnReferences(int columnNo, int newColumnNo) {
		// noop
	}

	@Override
	protected void adjustDeletedColumnReferences(int columnNo) {
		// noop
	}

	@Override
	protected ParameterTemplateColumn createTemplateColumn(ParameterTemplateColumn source) {
		ParameterTemplateColumn column = new ParameterTemplateColumn(source);
		return column;
	}

	@Override
	public Auditable deepCopy() {
		return new ParameterTemplate(this);
	}

	@Override
	public String getAuditDescription() {
		return "parameter template '" + getName() + "'";
	}

	public int getDeployMethod() {
		return deployMethod;
	}

	public String getDeployScriptDetails() {
		return deployScriptDetails;
	}

	public void setDeployScriptDetails(String details) {
		this.deployScriptDetails = details;
		this.deployMethod = RUN_SCRIPT_ONCE;
	}

}
