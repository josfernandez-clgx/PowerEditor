--==================================================================
-- Script for purging guidelines from PowerEditor DB
-- USAGE:
-- (1) REPLACE '1/01/2007 00:00:00' with a desired date and time
-- (2) Execute the script in a transaction
--==================================================================

-- Uncomment and execute the statement below to check how many grid_ids are to be deleted
--select G.grid_id,D.synonym_date from MB_GRID_DATE_SYNONYM G, MB_DATE_SYNONYM D where G.expiration_synonym_id > 0 and G.expiration_synonym_id=D.synonym_id and D.synonym_date < CAST ('1/01/2007 00:00:00' AS datetime);

delete from MB_ENTITY_GRID_CONTEXT where grid_id in (select G.grid_id from MB_GRID_DATE_SYNONYM G, MB_DATE_SYNONYM D where G.expiration_synonym_id > 0 and G.expiration_synonym_id=D.synonym_id and D.synonym_date < CAST ('1/01/2007 00:00:00' AS datetime))

delete from MB_GRID where grid_id in (select G.grid_id from MB_GRID_DATE_SYNONYM G, MB_DATE_SYNONYM D where G.expiration_synonym_id > 0 and G.expiration_synonym_id=D.synonym_id and D.synonym_date < CAST ('1/01/2007 00:00:00' AS datetime))

delete from MB_GRID_CELL_VALUE where grid_id in (select G.grid_id from MB_GRID_DATE_SYNONYM G, MB_DATE_SYNONYM D where G.expiration_synonym_id > 0 and G.expiration_synonym_id=D.synonym_id and D.synonym_date < CAST ('1/01/2007 00:00:00' AS datetime))

delete from MB_GRID_CONTEXT where grid_id in (select G.grid_id from MB_GRID_DATE_SYNONYM G, MB_DATE_SYNONYM D where G.expiration_synonym_id > 0 and G.expiration_synonym_id=D.synonym_id and D.synonym_date < CAST ('1/01/2007 00:00:00' AS datetime))

delete from MB_GRID_DATE_SYNONYM where grid_id in (select G.grid_id from MB_GRID_DATE_SYNONYM G, MB_DATE_SYNONYM D where G.expiration_synonym_id > 0 and G.expiration_synonym_id=D.synonym_id and D.synonym_date < CAST ('1/01/2007 00:00:00' AS datetime))
