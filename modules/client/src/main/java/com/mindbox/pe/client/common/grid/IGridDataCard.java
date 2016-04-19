package com.mindbox.pe.client.common.grid;

import java.util.List;

import com.mindbox.pe.model.grid.AbstractGuidelineGrid;
import com.mindbox.pe.model.template.GridTemplate;

public interface IGridDataCard {

	public abstract void populate(AbstractGuidelineGrid abstractgrid, boolean hideRuleIDColumns);

	public abstract boolean isDirty();

	public abstract List<List<Object>> getDataVector();

	public abstract void setTemplate(GridTemplate gridtemplate);

	public abstract void setEnabled(boolean flag);

	public abstract void setViewOnly(boolean flag);

	public abstract void cancelEdits();

	public abstract AbstractGridTableModel<?> getGridTableModel();

	public void toggleRuleIDColumns();

}
