--
-- PowerEditor 5.4.0 Database Upgrade Script
--

insert into MB_ID_GENERATOR (id_type,next_id,id_cache_size) values ('RuleID', 10000, 20);
insert into MB_ID_GENERATOR (id_type,next_id,id_cache_size) values ('Audit', 10000, 20);
