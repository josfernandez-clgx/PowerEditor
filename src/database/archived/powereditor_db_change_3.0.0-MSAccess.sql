
DROP TABLE MB_ENTITY_TYPE;

DROP TABLE MB_ENTITY;

CREATE TABLE MB_ENTITY (
	entity_id   INT NOT NULL ,
	entity_type INT NOT NULL ,
	entity_name VARCHAR(50) NOT NULL ,
	parent_id   INT NULL ,
	PRIMARY KEY ( entity_id , entity_type )
);

CREATE TABLE MB_ENTITY_COMPATIBILITY (
	entity1_id      INT NOT NULL ,
	entity1_type    INT NOT NULL ,
	entity2_id      INT NOT NULL ,
	entity2_type    INT NOT NULL ,
	effective_synonym_id  INT NOT NULL,
	expiration_synonym_id INT NOT NULL
);

CREATE TABLE MB_ENTITY_PROPERTY (
	entity_id     INT NOT NULL ,
	entity_type   INT NOT NULL ,
	property_name VARCHAR(255) NOT NULL ,
	string_value  VARCHAR(255) NULL ,
	blob_value    memo NULL ,
	PRIMARY KEY ( entity_id , property_name )
);

CREATE TABLE MB_ENTITY_GRID_CONTEXT (
	grid_id       INT NOT NULL ,
	entity_id     INT NULL ,
	entity_type   INT NULL ,
	category_type INT NULL ,
	category_id    INT NULL
);

CREATE TABLE MB_ENTITY_PARAMETER_CONTEXT (
	parameter_id  INT NOT NULL ,
	entity_id     INT NULL ,
	entity_type   INT NULL ,
	category_type INT NULL ,
	category_id    INT NULL
);

CREATE TABLE MB_ENTITY_ADHOC_RULESET_CONTEXT (
	ruleset_id    INT NOT NULL ,
	entity_id     INT NULL ,
	entity_type   INT NULL ,
	category_type INT NULL ,
	category_id    INT NULL
);
