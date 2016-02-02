--
-- PowerEditor 5.3.0 Database Upgrade Script
--

DROP TABLE MB_AUDIT;

CREATE TABLE MB_AUDIT (
	audit_id       INT NOT NULL ,
	audit_type_id  INT NOT NULL ,
	event_date     VARCHAR2 (21) NOT NULL ,
	user_name      VARCHAR2 (50) NULL ,
	PRIMARY KEY ( audit_id )
);

CREATE TABLE MB_AUDIT_TYPE (
	audit_type_id  INT NOT NULL ,
	description    VARCHAR2 (100) NOT NULL,
	PRIMARY KEY ( audit_type_id )
);

CREATE TABLE MB_KB_AUDIT_DETAIL (
	kb_audit_detail_id INT NOT NULL ,
	kb_audit_id        INT NOT NULL ,
	kb_mod_type_id     INT NOT NULL ,
	description        VARCHAR2(4000) NULL ,
	PRIMARY KEY ( kb_audit_detail_id )
);

CREATE TABLE MB_KB_AUDIT_DETAIL_DATA (
	kb_audit_detail_id INT NOT NULL ,
	kb_element_type_id INT NOT NULL ,
	element_value      VARCHAR2(4000) NULL 
);

CREATE TABLE MB_KB_AUDIT_MASTER (
	kb_audit_id        INT NOT NULL ,
	audit_id           INT NOT NULL ,
	kb_changed_type_id INT NOT NULL ,
	element_id         INT NOT NULL ,
	PRIMARY KEY ( kb_audit_id )
);


INSERT INTO MB_AUDIT_TYPE (audit_type_id,description) VALUES (1,'Logon');
INSERT INTO MB_AUDIT_TYPE (audit_type_id,description) VALUES (2,'Logoff');
INSERT INTO MB_AUDIT_TYPE (audit_type_id,description) VALUES (3,'Server Startup');
INSERT INTO MB_AUDIT_TYPE (audit_type_id,description) VALUES (4,'Server Shutdown');
INSERT INTO MB_AUDIT_TYPE (audit_type_id,description) VALUES (5,'KB Modification');
