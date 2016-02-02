
CREATE TABLE MB_ADHOC_RULESET (
  ruleset_id int NOT NULL ,
  name VARCHAR(255) NULL ,
  description VARCHAR(255) NULL ,
  activation_date datetime NOT NULL ,
  expiration_date datetime NULL ,
  creation_date datetime NOT NULL ,
  status VARCHAR(32) NOT NULL ,
  PRIMARY KEY ( ruleset_id )
);

CREATE TABLE MB_ADHOC_RULESET_USAGE (
  ruleset_id int NOT NULL ,
  usage_type VARCHAR(255) NOT NULL ,
  PRIMARY KEY ( ruleset_id )
);

CREATE TABLE MB_ADHOC_RULESET_CONTEXT (
  ruleset_id int NOT NULL ,
  product_id int NOT NULL ,
  category_id varchar(255) NOT NULL ,
  channel_id int NOT NULL ,
  investor_key int NOT NULL ,
  PRIMARY KEY ( ruleset_id )
);

CREATE TABLE MB_ADHOC_RULESET_RULES (
  rule_id int NOT NULL ,
  ruleset_id int NOT NULL ,
  PRIMARY KEY ( rule_id, ruleset_id )
);

INSERT INTO MB_PRIVILEGE (PRIVILEGE_ID,PRIVILEGE_NAME,DISPLAY_STRING) VALUES (50,'ManageAdHocRules','Manage Ad-Hoc Rules');
INSERT INTO MB_ROLE_PRIVILEGE (PRIVILEGE_ID,ROLE_ID) VALUES (50,2);
