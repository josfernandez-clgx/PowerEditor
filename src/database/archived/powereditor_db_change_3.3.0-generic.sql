
CREATE TABLE MB_PHASE (
	phase_id     INT NOT NULL ,
	phase_type   INT NOT NULL ,
	phase_name   VARCHAR (128) NOT NULL ,
	display_name VARCHAR (255) NOT NULL , 
	task_name    VARCHAR (128) NULL ,
	prereq_type  INT NULL ,
	PRIMARY KEY ( phase_id )
);

CREATE TABLE MB_PHASE_LINK (
	parent_phase_id INT NOT NULL,
	child_phase_id  INT NOT NULL
);

CREATE TABLE MB_PHASE_PREREQ (
	phases_id       INT NOT NULL,
	prereq_phase_id INT NOT NULL
);

CREATE TABLE MB_REQUEST (
	request_id    INT NOT NULL ,
	request_name  VARCHAR (128) NOT NULL ,
	request_type  VARCHAR (128) NOT NULL ,
	display_name  VARCHAR (128) NOT NULL ,
	description   VARCHAR (255) NULL ,
	init_function VARCHAR (255) NOT NULL , 
	purpose       VARCHAR (128) NOT NULL ,
	phase_id      INT NOT NULL , 
	PRIMARY KEY ( request_id )
);

INSERT INTO MB_PRIVILEGE (PRIVILEGE_ID,PRIVILEGE_NAME,DISPLAY_STRING) VALUES (61,'ManageRequestType','Manage Request Type');
INSERT INTO MB_PRIVILEGE (PRIVILEGE_ID,PRIVILEGE_NAME,DISPLAY_STRING) VALUES (62,'ManagePhase','Manage Phase');
INSERT INTO MB_ROLE_PRIVILEGE (PRIVILEGE_ID,ROLE_ID) VALUES (61,2);
INSERT INTO MB_ROLE_PRIVILEGE (PRIVILEGE_ID,ROLE_ID) VALUES (62,2);
