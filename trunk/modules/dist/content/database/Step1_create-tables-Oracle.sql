
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

CREATE TABLE MB_CBR_ATTRIBUTE (
	attribute_id	   INT NOT NULL,
	case_base_id	   INT NOT NULL,
	display_name	   VARCHAR2(255) NOT NULL,
	attribute_type_id  INT NOT NULL,
	match_contribution INT NULL,
	mismatch_penalty   INT NULL,
	absence_penalty    INT NULL,
	lowest_value       NUMERIC NULL,
	highest_value      NUMERIC NULL,
	match_interval     NUMERIC NULL,
	value_range_id     INT NOT NULL,
	notes	           VARCHAR2(1024) NULL,
	PRIMARY KEY ( attribute_id )
);

CREATE TABLE MB_CBR_ATTRIBUTE_TYPE (
	attribute_type_id          INT NOT NULL,
	display_name               VARCHAR2(255) NOT NULL,
	symbol                     VARCHAR2(32) NOT NULL,
	default_match_contribution INT NOT NULL,
	default_mismatch_penalty   INT NOT NULL,
	default_absence_penalty    INT NOT NULL,
	default_value_range_id     INT NOT NULL,
	ask_for_match_interval     INT NOT NULL,
	ask_for_numeric_range      INT NOT NULL,
	PRIMARY KEY ( attribute_type_id )
);

CREATE TABLE MB_CBR_ATTRIBUTE_VALUE (
	attribute_value_id INT NOT NULL,
	case_id            INT NOT NULL,
	attribute_id       INT NOT NULL,
	display_name       VARCHAR2(255) NOT NULL,
	match_contribution INT NULL,
	mismatch_penalty   INT NULL,
	notes              VARCHAR2(1024) NULL,
	PRIMARY KEY ( attribute_value_id )
);

CREATE TABLE MB_CBR_CASE (
	case_id      INT NOT NULL,
	case_base_id INT NOT NULL,
	display_name VARCHAR2(255) NOT NULL,
	notes        VARCHAR2(1024) NULL,
	PRIMARY KEY ( case_id )
);

CREATE TABLE MB_CBR_CASE_ACTION (
	case_action_id INT NOT NULL,
	display_name   VARCHAR2(255) NOT NULL,
	symbol         VARCHAR2(32) NOT NULL,
	PRIMARY KEY ( case_action_id )
);

CREATE TABLE MB_CBR_CASE_ACTIONS_MAPPING (
	case_id      INT NOT NULL,
	action_id    INT NOT NULL,
	action_order INT NOT NULL
);

CREATE TABLE MB_CBR_CASE_BASE (
	case_base_id	    INT NOT NULL ,
	display_name        VARCHAR2(255) NOT NULL ,
	case_class_id	    INT NULL,
	index_file          VARCHAR2(512) NULL,
	scoring_function_id	INT NULL,
	naming_attribute	VARCHAR2(128) NULL,
	match_threshold	    INT NULL,
	maximum_matches     INT NULL,
	notes	            VARCHAR2(1024) NULL ,
	PRIMARY KEY ( case_base_id )
);

CREATE TABLE MB_CBR_CASE_BASE_DATE_SYN (
	case_base_id          INT NOT NULL,
	effective_synonym_id  INT NOT NULL,
	expiration_synonym_id INT NOT NULL,
	PRIMARY KEY (case_base_id)
);

CREATE TABLE MB_CBR_CASE_CLASS (
	case_class_id INT NOT NULL,
	display_name  VARCHAR2(32) NOT NULL,
	symbol        VARCHAR2(32) NOT NULL,
	PRIMARY KEY ( case_class_id )
);

CREATE TABLE MB_CBR_CASE_DATE_SYNONYM (
	case_id               INT NOT NULL,
	effective_synonym_id  INT NOT NULL,
	expiration_synonym_id INT NOT NULL,
	PRIMARY KEY (case_id)
);

CREATE TABLE MB_CBR_ENUMERATED_VALUE (
	attribute_id INT NOT NULL,
	value_string VARCHAR2(255) NOT NULL
);

CREATE TABLE MB_CBR_SCORING_FUNCTION (
	scoring_function_id INT NOT NULL,
	display_name        VARCHAR2(255) NOT NULL,
	symbol              VARCHAR2(32) NOT NULL,
	PRIMARY KEY ( scoring_function_id )
);

CREATE TABLE MB_CBR_VALUE_RANGE (
	value_range_id            INT NOT NULL,
	display_name              VARCHAR2(255) NOT NULL,
	symbol                    VARCHAR2(32) NOT NULL,
    description               VARCHAR2(128) NOT NULL,
	enumerated_values_allowed INT NOT NULL,
	anything_allowed          INT NOT NULL,
	numeric_allowed           INT NOT NULL,
	float_allowed             INT NOT NULL,
	negative_allowed          INT NOT NULL,
	PRIMARY KEY ( value_range_id )
);

CREATE TABLE MB_DATE_SYNONYM (
	synonym_id   INT NOT NULL,
	synonym_name VARCHAR2(255) NOT NULL,
	synonym_desc VARCHAR2(255) NULL ,
	synonym_date VARCHAR2(21) NOT NULL,
	is_named     NUMBER(1) NULL ,
	PRIMARY KEY (synonym_id)
);

CREATE TABLE MB_ENTITY (
	entity_id   INT NOT NULL ,
	entity_type INT NOT NULL ,
	entity_name VARCHAR2(255) NOT NULL ,
	parent_id   INT NULL ,
	PRIMARY KEY ( entity_id , entity_type )
);

CREATE TABLE MB_ENTITY_CATEGORY (
	category_id    INT NOT NULL ,
	category_name  VARCHAR2 (255) NOT NULL ,
	category_type  INT NOT NULL ,  
	PRIMARY KEY ( category_id , category_type )
);

CREATE TABLE MB_ENTITY_CATEGORY_LINK (
	entity_id     INT NOT NULL ,
	entity_type   INT NOT NULL ,
	category_id   INT NOT NULL ,
	category_type INT NOT NULL ,
	effective_synonym_id  INT NOT NULL,
	expiration_synonym_id INT NOT NULL
);

CREATE TABLE MB_ENTITY_CATEGORY_PARENT (
	category_id   INT NOT NULL,
	category_type INT NOT NULL,
	parent_id     INT NOT NULL,
	effective_synonym_id  INT NOT NULL,
	expiration_synonym_id INT NOT NULL
);

CREATE TABLE MB_ENTITY_COMPATIBILITY (
	entity1_id      INT NOT NULL ,
	entity1_type    INT NOT NULL ,
	entity2_id      INT NOT NULL ,
	entity2_type    INT NOT NULL ,
	effective_synonym_id  INT NOT NULL,
	expiration_synonym_id INT NOT NULL
);

CREATE TABLE MB_ENTITY_GRID_CONTEXT (
	grid_id        INT NOT NULL ,
	entity_id      INT NULL ,
	entity_type    INT NULL ,
	category_type  INT NULL ,
	category_id    INT NULL
);

CREATE TABLE MB_ENTITY_PARAMETER_CONTEXT (
	parameter_id   INT NOT NULL ,
	entity_id      INT NULL ,
	entity_type    INT NULL ,
	category_type  INT NULL ,
	category_id    INT NULL
);

CREATE TABLE MB_ENTITY_PROPERTY (
	entity_id     INT NOT NULL ,
	entity_type   INT NOT NULL ,
	property_name VARCHAR2(255) NOT NULL ,
	string_value  VARCHAR2(512) NULL ,
	blob_value    BLOB NULL ,
	PRIMARY KEY ( entity_id , property_name )
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

CREATE TABLE MB_GRID (
	grid_id               INT NOT NULL ,
	template_id           INT NOT NULL ,
	deploy_status         VARCHAR2 (20) NULL ,
	last_status_change_on VARCHAR2(21) NULL ,
	clone_of              INT NULL ,
	creation_date         VARCHAR2(21) NULL ,
	num_rows              INT NULL ,
	comments              VARCHAR2(1024) NULL ,
	PRIMARY KEY ( grid_id )
);

CREATE TABLE MB_GRID_CELL_VALUE (
	grid_id     INT NOT NULL ,
	row_id      INT NOT NULL ,
	column_name VARCHAR2(128) NOT NULL ,
	cell_value  VARCHAR2(1024) NULL 
);

CREATE TABLE MB_GRID_DATE_SYNONYM (
	grid_id               INT NOT NULL ,
	effective_synonym_id  INT NOT NULL,
	expiration_synonym_id INT NOT NULL,
	PRIMARY KEY (grid_id)
);

CREATE TABLE MB_GUIDELINE_ACTION (
	action_id       INT NOT NULL ,
	action_name     VARCHAR2(255) NOT NULL ,
	action_desc     VARCHAR2(255) NULL ,
	deployment_rule VARCHAR2(1024) NULL ,
	PRIMARY KEY ( action_id )
);

CREATE TABLE MB_GUIDELINE_ACTION_PARAM (
	action_id   INT NOT NULL ,
	param_id    INT NOT NULL ,
	param_name  VARCHAR2(255) NOT NULL ,
	deploy_type VARCHAR2(128) NOT NULL ,
	param_data  VARCHAR2(1024) NULL ,
	PRIMARY KEY (action_id,param_id)
);

CREATE TABLE MB_GUIDELINE_ACTION_USAGE (
	action_id  INT NOT NULL,
	usage_type VARCHAR2(128) NOT NULL
);

CREATE TABLE MB_GUIDELINE_TEST_CONDITION (
    test_id		    INT NOT NULL,
    test_name		VARCHAR2(255) NOT NULL,
    test_desc		VARCHAR2(255),
    deployment_rule	VARCHAR2(1024) NOT NULL,
    PRIMARY KEY (test_id)
);

CREATE TABLE MB_ID_GENERATOR (
	id_type       VARCHAR2 (50) NOT NULL ,
	next_id       INT NULL ,
	id_cache_size INT NULL ,
	PRIMARY KEY ( id_type )
);

CREATE TABLE MB_NAMED_FILTER (
	filter_id          INT NOT NULL ,
	filter_entity_type INT NOT NULL ,
	name               VARCHAR2 (255) NOT NULL ,
	filter_parms       VARCHAR2(1024) NOT NULL ,
 	filter_ge_type INT NOT NULL
);

CREATE TABLE MB_PARAMETER (
	parameter_id    INT NOT NULL ,
	template_id     INT NOT NULL ,
	cell_values     VARCHAR2(4000) NULL ,
	num_rows        INT NOT NULL ,
	status          VARCHAR2(32) NULL ,
	PRIMARY KEY ( parameter_id )
);

CREATE TABLE MB_PARAMETER_DATE_SYNONYM (
	parameter_id          INT NOT NULL ,
	effective_synonym_id  INT NOT NULL,
	expiration_synonym_id INT NOT NULL,
	PRIMARY KEY (parameter_id)
);

CREATE TABLE MB_PHASE (
	phase_id     INT NOT NULL ,
	phase_type   INT NOT NULL ,
	phase_name   VARCHAR2 (255) NOT NULL ,
	display_name VARCHAR2 (255) NOT NULL , 
	task_name    VARCHAR2 (128) NULL ,
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

CREATE TABLE MB_PRIVILEGE (
	privilege_id   INT NOT NULL ,
	privilege_name VARCHAR2 (255) NOT NULL ,
	display_string VARCHAR2 (255) NOT NULL ,
	privilege_type INT NOT NULL,
	PRIMARY KEY ( privilege_id )
);


CREATE TABLE MB_REQUEST (
	request_id    INT NOT NULL ,
	request_name  VARCHAR2 (255) NOT NULL ,
	request_type  VARCHAR2 (128) NOT NULL ,
	display_name  VARCHAR2 (255) NOT NULL ,
	description   VARCHAR2 (255) NULL ,
	init_function VARCHAR2 (255) NOT NULL , 
	purpose       VARCHAR2 (128) NOT NULL ,
	phase_id      INT NOT NULL , 
	PRIMARY KEY ( request_id )
);

CREATE TABLE MB_ROLE (
	role_id   INT NOT NULL ,
	role_name VARCHAR2 (255) NOT NULL ,
	PRIMARY KEY ( role_id )
);

CREATE TABLE MB_ROLE_PRIVILEGE (
	privilege_id INT NOT NULL ,
	role_id      INT NOT NULL ,
	PRIMARY KEY ( privilege_id , role_id )
);

CREATE TABLE MB_TEMPLATE (
	template_id   INT NOT NULL ,
	name          VARCHAR2(255) NOT NULL ,
	usage_type    VARCHAR2(128) NOT NULL ,
	status        VARCHAR2(128) NOT NULL ,
	max_row       INT NOT NULL ,
	parent_id     INT NOT NULL ,
	description   VARCHAR2(4000) NULL ,
	comments      VARCHAR2(4000) NULL ,
	comp_cols     VARCHAR2(128) NULL ,
	consist_cols  VARCHAR2(128) NULL ,
	fit_screen    NUMBER(1) NULL ,
	PRIMARY KEY (template_id)
);

CREATE TABLE MB_TEMPLATE_COLUMN (
	template_id   INT NOT NULL ,
	column_no     INT NOT NULL ,
	column_name   VARCHAR2(255) NOT NULL ,
	description   VARCHAR2(255) NULL ,
	attribute_map VARCHAR2(255) NULL ,
	title         VARCHAR2(128) NOT NULL ,
	font          VARCHAR2(64) NULL ,
	color         VARCHAR2(64) NULL ,
	width         INT NOT NULL ,
	data_type     VARCHAR2(64) NOT NULL ,
	multi_select  NUMBER(1) NULL ,
	allow_blank   NUMBER(1) NULL ,
	sort_enum     NUMBER(1) NULL ,
	show_lhs_attr NUMBER(1) NULL ,
	min_value     VARCHAR2(128) NULL ,
	max_value     VARCHAR2(128) NULL ,
	precision_val INT NULL ,
	PRIMARY KEY (template_id,column_no)
);

CREATE TABLE MB_TEMPLATE_COLUMN_ATTR_ITEM (
	template_id   INT NOT NULL ,
	column_no     INT NOT NULL ,
	name          VARCHAR2(128) NOT NULL ,
	display_value VARCHAR2(512) NULL
);

CREATE TABLE MB_TEMPLATE_COLUMN_ENUM (
	template_id  INT NOT NULL ,
	column_no    INT NOT NULL ,
	enum_value   VARCHAR2(128) NOT NULL	
);

CREATE TABLE MB_TEMPLATE_COLUMN_PROP (
	template_id    int not null,
	column_no      int not null,
	property_name  VARCHAR2(100) not null,
	property_value VARCHAR2(255) not null
);

CREATE TABLE MB_TEMPLATE_DEPLOY_RULE (
	template_id     INT NOT NULL ,
	column_no       INT NOT NULL ,
	rule_def        VARCHAR2(4000) NULL,
	big_rule_def    CLOB NULL
);

CREATE TABLE MB_TEMPLATE_MESSAGE (
	template_id      INT NOT NULL ,
	column_no        INT NOT NULL ,
	entity_id        INT NOT NULL ,
	cond_delim       VARCHAR2(64) NULL ,
	cond_final_delim VARCHAR2(64) NULL ,
	message_text     VARCHAR2(1024) NULL
);

CREATE TABLE MB_TEMPLATE_MESSAGE_FRAGMENT (
	template_id      INT NOT NULL ,
	column_no        INT NOT NULL ,
	message_text     VARCHAR2(1024) NULL ,
	type             VARCHAR2(64) NULL ,	
	cell_selection   VARCHAR2(64) NULL ,
	enum_delim       VARCHAR2(64) NULL ,
	enum_final_delim VARCHAR2(64) NULL ,
	enum_prefix      VARCHAR2(64) NULL ,
	range_style      VARCHAR2(64) NULL
);

CREATE TABLE MB_TEMPLATE_VERSION (
    template_id  INT NOT NULL,
    version      VARCHAR2(255) NOT NULL,
    PRIMARY KEY (template_id)
);

CREATE TABLE MB_TYPE_ENUM (
    enum_type VARCHAR(50) NOT NULL ,
    enum_id INT NOT NULL ,
    enum_value VARCHAR(100) NOT NULL ,
    enum_disp_label VARCHAR(100),
    PRIMARY KEY (enum_type,enum_id)
);

CREATE TABLE MB_USER_ACCOUNT (
	account_id VARCHAR2 (20) NOT NULL ,
	name       VARCHAR2 (50) NULL ,
	status     VARCHAR2 (20) NULL ,
	reset_password NUMBER(1) NULL ,
	failed_login_counter INT NOT NULL,	
	PRIMARY KEY ( account_id )
);

CREATE TABLE MB_USER_ENTITY_LINK (
	user_id     VARCHAR2(20) NOT NULL ,
	entity_id   INT NOT NULL ,
	entity_type INT NOT NULL 
);

CREATE TABLE MB_USER_PASSWORD (
  account_id VARCHAR2(20) NOT NULL,
  password   VARCHAR2(50) NOT NULL,
  password_change_date VARCHAR2(21)
);

CREATE TABLE MB_USER_ROLE (
	account_id VARCHAR2 (20) NOT NULL ,
	role_id    INT NOT NULL ,
	PRIMARY KEY ( account_id , role_id )
);
