--
-- PowerEditor 5.1.0 Database Upgrade Script
--

ALTER TABLE MB_ENTITY_COMPATIBILITY DROP effective_date;
ALTER TABLE MB_ENTITY_COMPATIBILITY DROP expiration_date;
ALTER TABLE MB_ENTITY_COMPATIBILITY ADD effective_synonym_id  INT NOT NULL;
ALTER TABLE MB_ENTITY_COMPATIBILITY ADD expiration_synonym_id INT NOT NULL;

ALTER TABLE MB_ENTITY_CATEGORY DROP parent_id;

CREATE TABLE MB_ENTITY_CATEGORY_PARENT (
	category_id   INT NOT NULL,
	category_type INT NOT NULL,
	parent_id     INT NOT NULL,
	effective_synonym_id  INT NOT NULL,
	expiration_synonym_id INT NOT NULL
);

ALTER TABLE MB_ENTITY_CATEGORY_LINK ADD effective_synonym_id  INT NOT NULL;
ALTER TABLE MB_ENTITY_CATEGORY_LINK ADD expiration_synonym_id INT NOT NULL;

ALTER TABLE MB_USER_ACCOUNT ADD reset_password BIT NULL;
ALTER TABLE MB_USER_ACCOUNT ADD failed_login_counter INT NULL;

CREATE TABLE MB_USER_PASSWORD (
  account_id VARCHAR(20) NOT NULL,
  password VARCHAR(50) NOT NULL,
  password_change_date DATETIME
);

insert into MB_PRIVILEGE (privilege_id,privilege_name,display_string,privilege_type) values (60,'EditProductionData','Edit Production Data',0);