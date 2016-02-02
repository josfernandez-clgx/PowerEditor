CREATE TABLE MB_CBR_CASE_BASE (
	case_base_id	INT NOT NULL ,
	display_name	VARCHAR(128) NOT NULL ,
	case_class_id	INT NULL,
	index_file      VARCHAR(255) NULL,
	scoring_function_id	INT NULL,
	naming_attribute	VARCHAR(128) NULL,
	match_threshold	INT NULL,
	maximum_matches INT NULL,
	notes	ntext NULL ,
	PRIMARY KEY ( case_base_id )
);

CREATE TABLE MB_CBR_CASE_BASE_LABEL_MAPPING (
	case_base_id  INT NOT NULL ,
	label_id INT NOT NULL
);

CREATE TABLE MB_CBR_ATTRIBUTE (
	attribute_id	INT NOT NULL,
	case_base_id	INT NOT NULL,
	display_name	VARCHAR(128) NOT NULL,
	attribute_type_id INT NOT NULL,
	match_contribution INT NULL,
	mismatch_penalty INT NULL,
	absence_penalty INT NULL,
	lowest_value NUMERIC NULL,
	highest_value NUMERIC NULL,
	match_interval NUMERIC NULL,
	value_range_id INT NOT NULL,
	notes	ntext NULL,
	PRIMARY KEY ( attribute_id )
);

CREATE TABLE MB_CBR_VALUE_RANGE (
	value_range_id INT NOT NULL,
	display_name VARCHAR(32) NOT NULL,
	symbol VARCHAR(32) NOT NULL,
    description VARCHAR(128) NOT NULL,
	enumerated_values_allowed INT NOT NULL,
	anything_allowed INT NOT NULL,
	numeric_allowed INT NOT NULL,
	float_allowed INT NOT NULL,
	negative_allowed INT NOT NULL,
	PRIMARY KEY ( value_range_id )
);

INSERT INTO MB_CBR_VALUE_RANGE (value_range_id, display_name, symbol, description, enumerated_values_allowed,
	anything_allowed, numeric_allowed, float_allowed, negative_allowed) 
	VALUES (1, 'Anything', 'Blank', 'Any type of value can be matched',0,1,0,0,0);
INSERT INTO MB_CBR_VALUE_RANGE (value_range_id, display_name, symbol, description, enumerated_values_allowed,
	anything_allowed, numeric_allowed, float_allowed, negative_allowed) 
	VALUES (2, 'Number', 'Number', 'Integer or floating point numbers can be matched',0,0,1,1,1);
INSERT INTO MB_CBR_VALUE_RANGE (value_range_id, display_name, symbol, description, enumerated_values_allowed,
	anything_allowed, numeric_allowed, float_allowed, negative_allowed) 
	VALUES (3, 'Non-negative Number', 'Number>=0', 'Integer or floating point numbers greater than or equal to zero can be matched',0,0,1,1,0);
INSERT INTO MB_CBR_VALUE_RANGE (value_range_id, display_name, symbol, description, enumerated_values_allowed,
	anything_allowed, numeric_allowed, float_allowed, negative_allowed) 
	VALUES (4, 'Integer', 'Integer', 'Integer numbers can be matched',0,0,1,0,1);
INSERT INTO MB_CBR_VALUE_RANGE (value_range_id, display_name, symbol, description, enumerated_values_allowed,
	anything_allowed, numeric_allowed, float_allowed, negative_allowed) 
	VALUES (5, 'Non-negative Integer', 'Integer>=0', 'Integer numbers greater than or equal to zero can be matched',0,0,1,0,0);
INSERT INTO MB_CBR_VALUE_RANGE (value_range_id, display_name, symbol, description, enumerated_values_allowed,
	anything_allowed, numeric_allowed, float_allowed, negative_allowed) 
	VALUES (6, 'Value Set', 'Enumerated', 'Values from a specified set can be matched',1,0,0,0,0);

CREATE TABLE MB_CBR_ENUMERATED_VALUE (
	attribute_id INT NOT NULL,
	value_string VARCHAR(128) NOT NULL
);

CREATE TABLE MB_CBR_CASE (
	case_id INT NOT NULL,
	case_base_id INT NOT NULL,
	display_name VARCHAR(128) NOT NULL,
	notes ntext NULL,
	PRIMARY KEY ( case_id )
);

CREATE TABLE MB_CBR_ATTRIBUTE_VALUE (
	attribute_value_id INT NOT NULL,
	case_id INT NOT NULL,
	attribute_id INT NOT NULL,
	display_name VARCHAR(128) NOT NULL,
	match_contribution INT NULL,
	mismatch_penalty INT NULL,
	notes ntext NULL,
	PRIMARY KEY ( attribute_value_id )
);

CREATE TABLE MB_CBR_CASE_ACTIONS_MAPPING (
	case_id INT NOT NULL,
	action_id INT NOT NULL,
	action_order INT NOT NULL
);

CREATE TABLE MB_CBR_CASE_LABEL_MAPPING (
	case_id  INT NOT NULL ,
	label_id INT NOT NULL
);

INSERT INTO MB_PRIVILEGE (PRIVILEGE_ID,PRIVILEGE_NAME,DISPLAY_STRING) VALUES (23,'ManageCBR','Manage CBR');
INSERT INTO MB_ROLE_PRIVILEGE (PRIVILEGE_ID,ROLE_ID) VALUES (23,2);
