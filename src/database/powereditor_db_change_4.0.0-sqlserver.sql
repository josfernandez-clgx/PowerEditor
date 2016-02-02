
CREATE TABLE MB_GUIDELINE_ACTION (
	action_id       INT NOT NULL ,
	action_name     VARCHAR(128) NOT NULL ,
	action_desc     VARCHAR(255) NULL ,
	deployment_rule ntext NULL ,
	PRIMARY KEY ( action_id )
);

CREATE TABLE MB_GUIDELINE_ACTION_PARAMETER (
	action_id   INT NOT NULL ,
	param_id    INT NOT NULL ,
	param_name  VARCHAR(128) NOT NULL ,
	deploy_type VARCHAR(128) NOT NULL ,
	param_data  ntext NULL ,
	PRIMARY KEY (action_id,param_id)
);

CREATE TABLE MB_GUIDELINE_ACTION_USAGE (
	action_id  INT NOT NULL,
	usage_type VARCHAR(128) NOT NULL
);


CREATE TABLE MB_ACTIVATION_LABEL (
	label_id        INT NOT NULL ,
	label_name      VARCHAR(128) NOT NULL ,
	label_desc      VARCHAR(255) NULL ,
    activation_date DATETIME NOT NULL ,
    expiration_date DATETIME NULL,
	PRIMARY KEY (label_id)
);

CREATE TABLE MB_GRID_LABEL (
	grid_id  INT NOT NULL ,
	label_id INT NOT NULL
);

CREATE TABLE MB_GRID_CELL_VALUE (
	grid_id     INT NOT NULL ,
	row_id      INT NOT NULL ,
	column_name VARCHAR(128) NOT NULL ,
	cell_value  VARCHAR(255) NULL 
);


CREATE TABLE MB_TEMPLATE (
	template_id   INT NOT NULL ,
	name          VARCHAR(128) NOT NULL ,
	usage         VARCHAR(128) NOT NULL ,
	status        VARCHAR(128) NOT NULL ,
	max_row       INT NOT NULL ,
	parent_id     INT NOT NULL ,
	description   VARCHAR(255) NULL ,
	comment       ntext NULL ,
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
	precision_val VARCHAR(128) NULL ,
	PRIMARY KEY (template_id,column_no)
);

CREATE TABLE MB_TEMPLATE_COLUMN_ENUM (
	template_id  INT NOT NULL ,
	column_no    INT NOT NULL ,
	enum_value   VARCHAR(128) NOT NULL	
);

CREATE TABLE MB_TEMPLATE_COLUMN_ATTR_ITEM (
	template_id   INT NOT NULL ,
	column_no     INT NOT NULL ,
	name          VARCHAR(128) NOT NULL ,
	display_value VARCHAR(255) NULL
);

CREATE TABLE MB_TEMPLATE_MESSAGE_FRAGMENT (
	template_id      INT NOT NULL ,
	column_no        INT NOT NULL ,
	message_text     ntext NULL ,
	type             VARCHAR(64) NULL ,	
	cell_selection   VARCHAR(64) NULL ,
	enum_delim       VARCHAR(64) NULL ,
	enum_final_delim VARCHAR(64) NULL ,
	enum_prefix      VARCHAR(64) NULL ,
	range_style      VARCHAR(64) NULL
);

CREATE TABLE MB_TEMPLATE_MESSAGE (
	template_id      INT NOT NULL ,
	column_no        INT NOT NULL ,
	channel_id       INT NOT NULL ,
	cond_delim       VARCHAR(64) NULL ,
	cond_final_delim VARCHAR(64) NULL ,
	message_text     ntext NULL
);

CREATE TABLE MB_TEMPLATE_DEPLOY_RULE (
	template_id     INT NOT NULL ,
	column_no       INT NOT NULL ,
	rule_def        ntext NULL
);

CREATE TABLE MB_TEMPLATE_LABEL (
	template_id  INT NOT NULL ,
	label_id INT NOT NULL
);

CREATE TABLE MB_TEMPLATE_ALT_DEPLOY_USAGE (
	template_id INT NOT NULL ,
	usage       VARCHAR(128) NOT NULL
);



INSERT INTO MB_PRIVILEGE (PRIVILEGE_ID,PRIVILEGE_NAME,DISPLAY_STRING) VALUES (21,'ManageGuidelineActions','Manage Guideline Actions');
INSERT INTO MB_PRIVILEGE (PRIVILEGE_ID,PRIVILEGE_NAME,DISPLAY_STRING) VALUES (22,'ManageActivationLabels','Manage Activation Labels');
INSERT INTO MB_PRIVILEGE (PRIVILEGE_ID,PRIVILEGE_NAME,DISPLAY_STRING) VALUES (29,'ManageConfiguration','Manage PowerEditor Configuration');

