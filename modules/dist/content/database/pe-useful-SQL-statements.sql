
-- Useful SQL statements
select distinct G.grid_id,T.template_id,C.column_no from MB_GRID G, MB_GRID_CELL_VALUE V, MB_TEMPLATE T, MB_TEMPLATE_COLUMN C,MB_TEMPLATE_DEPLOY_RULE R where G.grid_id=V.grid_id and V.cell_value='' and G.template_id=T.template_id and T.template_id=C.template_id and T.template_id=R.template_id and R.rule_def != '' and R.column_no > 0 and R.column_no=C.column_no and C.column_name=V.column_name


-- remove unused guideline grids

delete from MB_GRID_LABEL where grid_id in (select grid_id from MB_GRID where template_id not in (select template_id from mb_template))

delete from MB_GRID_CONTEXT where grid_id in (select grid_id from MB_GRID where template_id not in (select template_id from mb_template))

delete from MB_GRID_CELL_VALUE where grid_id in (select grid_id from MB_GRID where template_id not in (select template_id from mb_template))

delete from MB_GRID where template_id not in (select template_id from mb_template)
