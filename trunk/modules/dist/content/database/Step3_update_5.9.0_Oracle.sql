--
-- PowerEditor Version 5.9.0 DB Update
--

insert into MB_AUDIT_TYPE (audit_type_id,description) values (6, 'Deployment Started');
insert into MB_AUDIT_TYPE (audit_type_id,description) values (7, 'Deployment Completed');

alter table MB_AUDIT add audit_desc VARCHAR2(2000) null;

insert into MB_PRIVILEGE (privilege_id,privilege_name,display_string,privilege_type) values (13,'ManageRoles','Manage Roles',0);

create table MB_PE_VERSION (
	pe_version VARCHAR2(50) NULL,
	PRIMARY KEY (pe_version)
);

create table MB_KB_AUDIT_CHANGES (
	kb_audit_id INTEGER NOT NULL,
	kb_audit_change_detail CLOB NULL
);
