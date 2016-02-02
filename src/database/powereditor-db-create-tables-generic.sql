
CREATE TABLE MB_ARM_INFO (
	product_id                 INT NOT NULL ,
	index_name                 VARCHAR (20) NULL ,
	index_value                REAL NULL ,
	first_rate_adjust_mnths    INT NULL ,
	first_rate_adjust_cap      REAL NULL ,
	later_rate_adjust_mnths    INT NULL ,
	later_rate_adjust_cap      REAL NULL ,
	deferred_limit             INT NULL ,
	payment_adjust_cap         REAL NULL ,
	first_payment_adjust_mnths INT NULL ,
	later_payment_adjust_mnths INT NULL ,
	reamortization_period      INT NULL ,
	PRIMARY KEY ( product_id )
);

CREATE TABLE MB_AUDIT (
	user_name       VARCHAR (50) NULL ,
	edit_type       VARCHAR (50) NOT NULL ,
	edit_object_id  INT NULL ,
	edit_value_from VARCHAR (512) NULL ,
	edit_value_to   VARCHAR (512) NULL ,
	edit_date       DATETIME NULL 
);

CREATE TABLE MB_CATEGORY (
	category_id        INT NOT NULL ,
	category_name      VARCHAR(50) NOT NULL ,
	parent_category_id INT NULL ,
	PRIMARY KEY ( category_id )
);

CREATE TABLE MB_CBR_ATTRIBUTE (
	attribute_id	   INT NOT NULL,
	case_base_id	   INT NOT NULL,
	display_name	   VARCHAR(128) NOT NULL,
	attribute_type_id  INT NOT NULL,
	match_contribution INT NULL,
	mismatch_penalty   INT NULL,
	absence_penalty    INT NULL,
	lowest_value       NUMERIC NULL,
	highest_value      NUMERIC NULL,
	match_interval     NUMERIC NULL,
	value_range_id     INT NOT NULL,
	notes	           VARCHAR(1024) NULL,
	PRIMARY KEY ( attribute_id )
);

CREATE TABLE MB_CBR_ATTRIBUTE_TYPE (
	attribute_type_id          INT NOT NULL,
	display_name               VARCHAR(32) NOT NULL,
	symbol                     VARCHAR(32) NOT NULL,
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
	display_name       VARCHAR(128) NOT NULL,
	match_contribution INT NULL,
	mismatch_penalty   INT NULL,
	notes              VARCHAR(1024) NULL,
	PRIMARY KEY ( attribute_value_id )
);

CREATE TABLE MB_CBR_CASE (
	case_id      INT NOT NULL,
	case_base_id INT NOT NULL,
	display_name VARCHAR(128) NOT NULL,
	notes        VARCHAR(1024) NULL,
	PRIMARY KEY ( case_id )
);

CREATE TABLE MB_CBR_CASE_ACTION (
	case_action_id INT NOT NULL,
	display_name   VARCHAR(32) NOT NULL,
	symbol         VARCHAR(32) NOT NULL,
	PRIMARY KEY ( case_action_id )
);

CREATE TABLE MB_CBR_CASE_ACTIONS_MAPPING (
	case_id      INT NOT NULL,
	action_id    INT NOT NULL,
	action_order INT NOT NULL
);

CREATE TABLE MB_CBR_CASE_BASE (
	case_base_id	    INT NOT NULL ,
	display_name        VARCHAR(128) NOT NULL ,
	case_class_id	    INT NULL,
	index_file          VARCHAR(512) NULL,
	scoring_function_id	INT NULL,
	naming_attribute	VARCHAR(128) NULL,
	match_threshold	    INT NULL,
	maximum_matches     INT NULL,
	notes	            VARCHAR(1024) NULL ,
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
	display_name  VARCHAR(32) NOT NULL,
	symbol        VARCHAR(32) NOT NULL,
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
	value_string VARCHAR(128) NOT NULL
);

CREATE TABLE MB_CBR_SCORING_FUNCTION (
	scoring_function_id INT NOT NULL,
	display_name        VARCHAR(32) NOT NULL,
	symbol              VARCHAR(32) NOT NULL,
	PRIMARY KEY ( scoring_function_id )
);

CREATE TABLE MB_CBR_VALUE_RANGE (
	value_range_id            INT NOT NULL,
	display_name              VARCHAR(32) NOT NULL,
	symbol                    VARCHAR(32) NOT NULL,
    description               VARCHAR(128) NOT NULL,
	enumerated_values_allowed INT NOT NULL,
	anything_allowed          INT NOT NULL,
	numeric_allowed           INT NOT NULL,
	float_allowed             INT NOT NULL,
	negative_allowed          INT NOT NULL,
	PRIMARY KEY ( value_range_id )
);

CREATE TABLE MB_CHANNEL (
	channel_id      INT NOT NULL ,
	channel_name    VARCHAR (50) NOT NULL ,
	is_base_channel BIT NOT NULL ,
	PRIMARY KEY ( channel_id )
);

CREATE TABLE MB_DATE_SYNONYM (
	synonym_id   INT NOT NULL,
	synonym_name VARCHAR(255) NOT NULL,
	synonym_desc VARCHAR(255) NULL ,
	synonym_date DATETIME NOT NULL,
	is_named     BIT NULL ,
	PRIMARY KEY (synonym_id)
);

CREATE TABLE MB_ENTITY (
	entity_id   INT NOT NULL ,
	entity_type INT NOT NULL ,
	entity_name VARCHAR(50) NOT NULL ,
	parent_id   INT NULL ,
	PRIMARY KEY ( entity_id , entity_type )
);

CREATE TABLE MB_ENTITY_CATEGORY (
	category_id    INT NOT NULL ,
	category_name  VARCHAR (50) NOT NULL ,
	parent_id      INT NULL ,
	category_type  INT NOT NULL ,  
	PRIMARY KEY ( category_id , category_type )
);

CREATE TABLE MB_ENTITY_CATEGORY_LINK (
	entity_id     INT NOT NULL ,
	entity_type   INT NOT NULL ,
	category_id   INT NOT NULL ,
	category_type INT NOT NULL
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
	property_name VARCHAR(255) NOT NULL ,
	string_value  VARCHAR(512) NULL ,
	blob_value    BLOB NULL ,
	PRIMARY KEY ( entity_id , property_name )
);

CREATE TABLE MB_GRID (
	grid_id               INT NOT NULL ,
	template_id           INT NOT NULL ,
	deploy_status         VARCHAR (20) NULL ,
	last_status_change_on DATETIME NULL ,
	clone_of              INT NULL ,
	creation_date         DATETIME NULL ,
	num_rows              INT NULL ,
	comments              VARCHAR(1024) NULL ,
	PRIMARY KEY ( grid_id )
);

CREATE TABLE MB_GRID_CELL_VALUE (
	grid_id     INT NOT NULL ,
	row_id      INT NOT NULL ,
	column_name VARCHAR(128) NOT NULL ,
	cell_value  VARCHAR(1024) NULL 
);

CREATE TABLE MB_GRID_CONTEXT (
	grid_id      INT NOT NULL ,
	product_id   INT NOT NULL ,
	category_id  VARCHAR(255) NULL,
	channel_id   INT NULL ,
	investor_key INT NULL 
);

CREATE TABLE MB_GRID_DATE_SYNONYM (
	grid_id               INT NOT NULL ,
	effective_synonym_id  INT NOT NULL,
	expiration_synonym_id INT NOT NULL,
	PRIMARY KEY (grid_id)
);

CREATE TABLE MB_GUIDELINE_ACTION (
	action_id       INT NOT NULL ,
	action_name     VARCHAR(128) NOT NULL ,
	action_desc     VARCHAR(255) NULL ,
	deployment_rule VARCHAR(1024) NULL ,
	PRIMARY KEY ( action_id )
);

CREATE TABLE MB_GUIDELINE_ACTION_PARAM (
	action_id   INT NOT NULL ,
	param_id    INT NOT NULL ,
	param_name  VARCHAR(128) NOT NULL ,
	deploy_type VARCHAR(128) NOT NULL ,
	param_data  VARCHAR(1024) NULL ,
	PRIMARY KEY (action_id,param_id)
);

CREATE TABLE MB_GUIDELINE_ACTION_USAGE (
	action_id  INT NOT NULL,
	usage_type VARCHAR(128) NOT NULL
);

CREATE TABLE MB_GUIDELINE_TEST_CONDITION (
    test_id		    INT NOT NULL,
    test_name		VARCHAR(128) NOT NULL,
    test_desc		VARCHAR(255),
    deployment_rule	VARCHAR(1024) NOT NULL,
    PRIMARY KEY (test_id)
);

CREATE TABLE MB_ID_GENERATOR (
	id_type       VARCHAR (50) NOT NULL ,
	next_id       INT NULL ,

	id_cache_size INT NULL ,
	PRIMARY KEY ( id_type )
);

CREATE TABLE MB_INVESTOR (
	Investor_Key  INT NOT NULL ,
	Investor_Name VARCHAR (50) NOT NULL ,
	Description   VARCHAR (50) NULL ,
	PRIMARY KEY ( Investor_Key )
);

CREATE TABLE MB_InvestorsToProducts (
	Investor_Key   INT NOT NULL ,
	product_id     INT NOT NULL ,
	EffectiveDate  DATETIME NOT NULL ,
	ExpirationDate DATETIME NULL ,
	PRIMARY KEY ( Investor_Key, product_id )
);

CREATE TABLE MB_NAMED_FILTER (
	filter_id          INT NOT NULL ,
	filter_entity_type INT NOT NULL ,
	name               VARCHAR (100) NOT NULL ,
	filter_parms       VARCHAR(1024) NOT NULL 
);

CREATE TABLE MB_OrganizationsToInvestors (
	OrganizationID INT NOT NULL ,
	Investor_Key   INT NOT NULL ,
	EffectiveDate  DATETIME NOT NULL ,
	ExpirationDate DATETIME null ,
	PRIMARY KEY ( OrganizationID, Investor_Key )
);

CREATE TABLE MB_OrganizationsToProducts (
	OrganizationID INT NOT NULL ,
	product_id     INT NOT NULL ,
	EffectiveDate  DATETIME NOT NULL ,
	ExpirationDate DATETIME null ,
	PRIMARY KEY ( OrganizationID , product_id )
);

CREATE TABLE MB_PARAMETER (
	parameter_id    INT NOT NULL ,
	template_id     INT NOT NULL ,
	cell_values     VARCHAR(4000) NULL ,
	num_rows        INT NOT NULL ,
	status          VARCHAR(32) NULL ,
	PRIMARY KEY ( parameter_id )
);

CREATE TABLE MB_PARAMETER_CONTEXT (
	parameter_id INT NOT NULL ,
	product_id   INT NOT NULL ,
	category_id  VARCHAR(255) NOT NULL ,
	channel_id   INT NULL ,
	investor_key INT NULL ,
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

CREATE TABLE MB_PRIVILEGE (
	privilege_id   INT NOT NULL ,
	privilege_name VARCHAR (50) NOT NULL ,
	display_string VARCHAR (255) NOT NULL ,
	privilege_type INT NOT NULL,
	PRIMARY KEY ( privilege_id )
);

CREATE TABLE MB_PRODUCT (
	product_id            INT NOT NULL ,
	product_code          VARCHAR(128) NOT NULL ,
	name                  VARCHAR (50) NOT NULL ,
	description           VARCHAR (255) NULL ,
	status                VARCHAR (20) NOT NULL ,
	last_status_change_on DATETIME NULL ,
	clone_generation      INT NOT NULL ,
	clone_of              INT NULL ,
	loan_type             VARCHAR (20) NULL ,
	amortization_type     VARCHAR (20) NULL ,
	lien_priority         INT NULL ,
	assumable             BIT NOT NULL ,
	negative_amortization BIT NOT NULL ,
	prepayment_penalty    BIT NULL ,
	convertible           BIT NOT NULL ,
	min_credit_grade      VARCHAR (10) NULL ,
	activation_date       DATETIME NULL ,
	expiration_date       DATETIME NULL ,
	is_a_paper            BIT NOT NULL ,
	PRIMARY KEY ( product_id )
);

CREATE TABLE MB_PRODUCT_CATEGORY (
	product_id INT NOT NULL ,
	category   INT NOT NULL ,
	PRIMARY KEY ( product_id , category )
);

CREATE TABLE MB_PRODUCT_TERM (
	product_id              INT NOT NULL ,
	amortization_term_mnths INT NULL ,
	PRIMARY KEY ( product_id )
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

CREATE TABLE MB_ROLE (
	role_id   INT NOT NULL ,
	role_name VARCHAR (50) NOT NULL ,
	PRIMARY KEY ( role_id )
);

CREATE TABLE MB_ROLE_PRIVILEGE (
	privilege_id INT NOT NULL ,
	role_id      INT NOT NULL ,
	PRIMARY KEY ( privilege_id , role_id )
);

CREATE TABLE MB_TEMPLATE (
	template_id   INT NOT NULL ,
	name          VARCHAR(128) NOT NULL ,
	usage_type    VARCHAR(128) NOT NULL ,
	status        VARCHAR(128) NOT NULL ,
	max_row       INT NOT NULL ,
	parent_id     INT NOT NULL ,
	description   VARCHAR(255) NULL ,
	comments      VARCHAR(512) NULL ,
	comp_cols     VARCHAR(128) NULL ,
	consist_cols  VARCHAR(128) NULL ,
	fit_screen    BIT NULL ,
	PRIMARY KEY (template_id)
);

CREATE TABLE MB_TEMPLATE_COLUMN (
	template_id   INT NOT NULL ,
	column_no     INT NOT NULL ,
	column_name   VARCHAR(128) NOT NULL ,
	description   VARCHAR(255) NULL ,
	attribute_map VARCHAR(255) NULL ,
	title         VARCHAR(128) NOT NULL ,
	font          VARCHAR(64) NOT NULL ,
	color         VARCHAR(64) NOT NULL ,
	width         INT NOT NULL ,
	data_type     VARCHAR(64) NOT NULL ,
	multi_select  BIT NULL ,
	allow_blank   BIT NULL ,
	sort_enum     BIT NULL ,
	show_lhs_attr BIT NULL ,
	min_value     VARCHAR(128) NULL ,
	max_value     VARCHAR(128) NULL ,
	precision_val INT NULL ,
	PRIMARY KEY (template_id,column_no)
);

CREATE TABLE MB_TEMPLATE_COLUMN_ATTR_ITEM (
	template_id   INT NOT NULL ,
	column_no     INT NOT NULL ,
	name          VARCHAR(128) NOT NULL ,
	display_value VARCHAR(512) NULL
);

CREATE TABLE MB_TEMPLATE_COLUMN_ENUM (
	template_id  INT NOT NULL ,
	column_no    INT NOT NULL ,
	enum_value   VARCHAR(128) NOT NULL	
);

CREATE TABLE MB_TEMPLATE_DEPLOY_RULE (
	template_id     INT NOT NULL ,
	column_no       INT NOT NULL ,
	rule_def        VARCHAR(4000) NULL
);

CREATE TABLE MB_TEMPLATE_MESSAGE (
	template_id      INT NOT NULL ,
	column_no        INT NOT NULL ,
	channel_id       INT NOT NULL ,
	cond_delim       VARCHAR(64) NULL ,
	cond_final_delim VARCHAR(64) NULL ,
	message_text     VARCHAR(1024) NULL
);

CREATE TABLE MB_TEMPLATE_MESSAGE_FRAGMENT (
	template_id      INT NOT NULL ,
	column_no        INT NOT NULL ,
	message_text     VARCHAR(1024) NULL ,
	type             VARCHAR(64) NULL ,	
	cell_selection   VARCHAR(64) NULL ,
	enum_delim       VARCHAR(64) NULL ,
	enum_final_delim VARCHAR(64) NULL ,
	enum_prefix      VARCHAR(64) NULL ,
	range_style      VARCHAR(64) NULL
);

CREATE TABLE MB_TEMPLATE_VERSION (
    template_id  INT NOT NULL,
    version      VARCHAR(255) NOT NULL,
    PRIMARY KEY (template_id)
);

CREATE TABLE MB_TYPE_ENUM (
    enum_type   VARCHAR(32) NOT NULL ,
    enum_id     INT NOT NULL ,
    enum_value  VARCHAR(32) NOT NULL
);

CREATE TABLE MB_UsersToChannels (
	user_account_id VARCHAR (20) NOT NULL ,
	channel_id      INT NOT NULL ,
	PRIMARY KEY ( user_account_id , channel_id )
);

CREATE TABLE MB_UsersToInvestors (
	user_account_id VARCHAR(20) NOT NULL,
	investor_id     INT NOT NULL,
	PRIMARY KEY ( user_account_id , investor_id )
);

CREATE TABLE MB_USER_ACCOUNT (
	account_id VARCHAR (20) NOT NULL ,
	password   VARCHAR (50) NOT NULL ,
	name       VARCHAR (50) NULL ,
	status     VARCHAR (20) NULL ,
	reset_password BIT NOT NULL,
	failed_login_counter INT NOT NULL,	
	PRIMARY KEY ( account_id )
);

CREATE TABLE MB_USER_ENTITY_LINK (
	user_id     VARCHAR(20) NOT NULL ,
	entity_id   INT NOT NULL ,
	entity_type INT NOT NULL 
);

create table MB_USER_PASSWORD (
  account_id VARCHAR(20) NOT NULL,
  password VARCHAR(50) NOT NULL,
  password_change_date DATETIME
);

CREATE TABLE MB_USER_ROLE (
	account_id VARCHAR (20) NOT NULL ,
	role_id    INT NOT NULL ,
	PRIMARY KEY ( account_id , role_id )
);
