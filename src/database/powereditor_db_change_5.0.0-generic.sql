--
-- PowerEditor 5.0.0 Database Upgrade Script
--

ALTER TABLE MB_PRIVILEGE ADD privilege_type int DEFAULT 0 NOT NULL ;

UPDATE MB_PRIVILEGE SET privilege_type = 1 WHERE privilege_name = 'EditEntityData';

UPDATE MB_PRIVILEGE SET privilege_type = 1 WHERE privilege_name = 'ViewEntityData';

UPDATE MB_PRIVILEGE SET privilege_type = 2 WHERE privilege_name = 'EditPricingData';

UPDATE MB_PRIVILEGE SET privilege_type = 2 WHERE privilege_name = 'ViewPricingData';

UPDATE MB_PRIVILEGE SET privilege_type = 2 WHERE privilege_name = 'EditQualificationData';

UPDATE MB_PRIVILEGE SET privilege_type = 2 WHERE privilege_name = 'ViewQualificationData';

UPDATE MB_PRIVILEGE SET privilege_type = 2 WHERE privilege_name = 'EditCreditData';

UPDATE MB_PRIVILEGE SET privilege_type = 2 WHERE privilege_name = 'ViewCreditData';

UPDATE MB_PRIVILEGE SET privilege_type = 2 WHERE privilege_name = 'ManageTemplates';

DELETE from MB_ID_GENERATOR where id_type in ('Product', 'SimpleEntity');
UPDATE MB_ID_GENERATOR SET id_cache_size=10 WHERE id_type='SEQUENTIAL';
