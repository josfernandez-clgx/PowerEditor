
CREATE TABLE MB_PARAMETER (
  parameter_id int NOT NULL ,
  template_id int NOT NULL ,
  cell_values memo NULL ,
  num_rows int NOT NULL ,
  activation_date datetime NOT NULL ,
  expiration_date datetime NULL ,
  status varchar(32) NULL ,
  PRIMARY KEY ( parameter_id )
);

CREATE TABLE MB_PARAMETER_CONTEXT (
  parameter_id int NOT NULL ,
  product_id int NOT NULL ,
  category_id varchar(255) NOT NULL ,
  channel_id int NULL ,
  investor_key int NULL ,
  PRIMARY KEY ( parameter_id )
);


INSERT INTO MB_PRIVILEGE (PRIVILEGE_ID,PRIVILEGE_NAME,DISPLAY_STRING) VALUES (41,'ManageParameters','Manage Parameters');
INSERT INTO MB_ROLE_PRIVILEGE (PRIVILEGE_ID,ROLE_ID) VALUES (41,2);
