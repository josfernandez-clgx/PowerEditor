
CREATE TABLE MB_TYPE_ENUM (
    ENUM_TYPE VARCHAR(32) NOT NULL ,
    ENUM_ID INT NOT NULL ,
    ENUM_VALUE VARCHAR(32) NOT NULL
);


DELETE FROM MB_TYPE_ENUM;

INSERT INTO MB_TYPE_ENUM (ENUM_TYPE,ENUM_ID,ENUM_VALUE) VALUES ('STATUS',1,'Draft');
INSERT INTO MB_TYPE_ENUM (ENUM_TYPE,ENUM_ID,ENUM_VALUE) VALUES ('STATUS',2,'Dev Test');
INSERT INTO MB_TYPE_ENUM (ENUM_TYPE,ENUM_ID,ENUM_VALUE) VALUES ('STATUS',3,'QA Alpha');
INSERT INTO MB_TYPE_ENUM (ENUM_TYPE,ENUM_ID,ENUM_VALUE) VALUES ('STATUS',4,'QA Beta');
INSERT INTO MB_TYPE_ENUM (ENUM_TYPE,ENUM_ID,ENUM_VALUE) VALUES ('STATUS',9,'Production');

INSERT INTO MB_TYPE_ENUM (ENUM_TYPE,ENUM_ID,ENUM_VALUE) VALUES ('LOAN',101,'Conforming');
INSERT INTO MB_TYPE_ENUM (ENUM_TYPE,ENUM_ID,ENUM_VALUE) VALUES ('LOAN',102,'Jumbo');
INSERT INTO MB_TYPE_ENUM (ENUM_TYPE,ENUM_ID,ENUM_VALUE) VALUES ('LOAN',103,'FMNA');

INSERT INTO MB_TYPE_ENUM (ENUM_TYPE,ENUM_ID,ENUM_VALUE) VALUES ('AMORTIZATION',201,'Fixed');
INSERT INTO MB_TYPE_ENUM (ENUM_TYPE,ENUM_ID,ENUM_VALUE) VALUES ('AMORTIZATION',202,'ARM');
INSERT INTO MB_TYPE_ENUM (ENUM_TYPE,ENUM_ID,ENUM_VALUE) VALUES ('AMORTIZATION',203,'Hybrid');
INSERT INTO MB_TYPE_ENUM (ENUM_TYPE,ENUM_ID,ENUM_VALUE) VALUES ('AMORTIZATION',204,'NegAm');

INSERT INTO MB_TYPE_ENUM (ENUM_TYPE,ENUM_ID,ENUM_VALUE) VALUES ('ARM-INDEX',211,'CMT');
INSERT INTO MB_TYPE_ENUM (ENUM_TYPE,ENUM_ID,ENUM_VALUE) VALUES ('ARM-INDEX',212,'COFI');
INSERT INTO MB_TYPE_ENUM (ENUM_TYPE,ENUM_ID,ENUM_VALUE) VALUES ('ARM-INDEX',213,'LIBOR');
INSERT INTO MB_TYPE_ENUM (ENUM_TYPE,ENUM_ID,ENUM_VALUE) VALUES ('ARM-INDEX',214,'MTA');


ALTER TABLE MB_USER_ACCOUNT DROP COLUMN ASSIGNED_CHANNEL;

CREATE TABLE MB_UsersToInvestors (
	USER_ACCOUNT_ID VARCHAR(20),
	INVESTOR_ID INT ,
	PRIMARY KEY ( USER_ACCOUNT_ID , INVESTOR_ID )
);


ALTER TABLE MB_PRODUCT MODIFY PRODUCT_CODE VARCHAR(128);

CREATE TABLE MB_GRID_CONTEXT (
	grid_id int NOT NULL ,
	product_id int NOT NULL ,
	category_id varchar(255) NOT NULL ,
	channel_id int NULL ,
	investor_key int NULL 
);
