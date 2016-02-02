
CREATE TABLE MB_ENTITY_CATEGORY (
	category_id    INT NOT NULL ,
	category_name  VARCHAR (128) NOT NULL ,
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


CREATE TABLE MB_USER_ENTITY_LINK (
	user_id     VARCHAR(20) NOT NULL ,
	entity_id   INT NOT NULL ,
	entity_type INT NOT NULL 
);

