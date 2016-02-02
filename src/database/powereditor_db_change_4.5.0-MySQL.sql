--
-- PowerEditor 4.5.0 Database Upgrade Script
--

CREATE TABLE MB_TEMPLATE_COLUMN_PROP (
	template_id    int not null,
	column_no      int not null,
	property_name  varchar(100) not null,
	property_value varchar(255) not null
);

ALTER TABLE MB_CBR_ATTRIBUTE MODIFY display_name varchar(255);
ALTER TABLE MB_CBR_ATTRIBUTE_TYPE MODIFY display_name varchar(255);
ALTER TABLE MB_CBR_ATTRIBUTE_VALUE MODIFY display_name varchar(255);
ALTER TABLE MB_CBR_CASE MODIFY display_name varchar(255);
ALTER TABLE MB_CBR_CASE_ACTION MODIFY display_name varchar(255);
ALTER TABLE MB_CBR_CASE_BASE MODIFY display_name varchar(255);
ALTER TABLE MB_CBR_CASE_CLASS MODIFY display_name varchar(255);
ALTER TABLE MB_CBR_ENUMERATED_VALUE MODIFY value_string varchar(255);
ALTER TABLE MB_CBR_SCORING_FUNCTION MODIFY display_name varchar(255);
ALTER TABLE MB_CBR_VALUE_RANGE MODIFY display_name varchar(255);
ALTER TABLE MB_ENTITY MODIFY entity_name varchar(255);
ALTER TABLE MB_ENTITY_CATEGORY MODIFY category_name varchar(255);
ALTER TABLE MB_GUIDELINE_ACTION MODIFY action_name varchar(255);
ALTER TABLE MB_GUIDELINE_ACTION_PARAM MODIFY param_name varchar(255);
ALTER TABLE MB_GUIDELINE_TEST_CONDITION MODIFY test_name varchar(255);
ALTER TABLE MB_NAMED_FILTER MODIFY name varchar(255);
ALTER TABLE MB_PHASE MODIFY phase_name varchar(255);
ALTER TABLE MB_PRIVILEGE MODIFY privilege_name varchar(255);
ALTER TABLE MB_REQUEST MODIFY request_name varchar(255);
ALTER TABLE MB_REQUEST MODIFY display_name varchar(255);
ALTER TABLE MB_ROLE MODIFY role_name varchar(255);
ALTER TABLE MB_TEMPLATE MODIFY name varchar(255);
ALTER TABLE MB_TEMPLATE MODIFY description varchar(1000);
ALTER TABLE MB_TEMPLATE_COLUMN MODIFY column_name varchar(255);
ALTER TABLE MB_TEMPLATE_COLUMN MODIFY title varchar(255);
ALTER TABLE MB_TEMPLATE_COLUMN ALTER precision_val INT;
ALTER TABLE MB_NAMED_FILTER ADD filter_ge_type INT NOT NULL;
ALTER TABLE MB_TEMPLATE_MESSAGE DROP channel_id;
ALTER TABLE MB_TEMPLATE_MESSAGE ADD entity_id int not null;

UPDATE MB_TYPE_ENUM set enum_type='product.arm_index_name' where enum_type='product.arm.index_name';

DELETE FROM MB_TYPE_ENUM where enum_type='product.assumption_type';
DELETE FROM MB_TYPE_ENUM where enum_type='product.calculation_type';
DELETE FROM MB_TYPE_ENUM where enum_type='product.late_charge_method';
DELETE FROM MB_TYPE_ENUM where enum_type='product.prepaid_int_factor';
DELETE FROM MB_TYPE_ENUM where enum_type='product.security_type';
DELETE FROM MB_TYPE_ENUM where enum_type='program.code';
INSERT INTO MB_TYPE_ENUM (enum_type,enum_id,enum_value,enum_disp_label) VALUES ('product.assumption_type', 1, '0', 'None');
INSERT INTO MB_TYPE_ENUM (enum_type,enum_id,enum_value,enum_disp_label) VALUES ('product.assumption_type', 2, '1', 'Subject To Conditions');
INSERT INTO MB_TYPE_ENUM (enum_type,enum_id,enum_value,enum_disp_label) VALUES ('product.assumption_type', 3, '2', 'Not Subject To Conditions');
INSERT INTO MB_TYPE_ENUM (enum_type,enum_id,enum_value,enum_disp_label) VALUES ('product.calculation_type', 1, '0', 'Fixed - Conventional');
INSERT INTO MB_TYPE_ENUM (enum_type,enum_id,enum_value,enum_disp_label) VALUES ('product.calculation_type', 2, '1', 'Fixed - biweekly');
INSERT INTO MB_TYPE_ENUM (enum_type,enum_id,enum_value,enum_disp_label) VALUES ('product.calculation_type', 3, '2', 'Fixed - FHA 203');
INSERT INTO MB_TYPE_ENUM (enum_type,enum_id,enum_value,enum_disp_label) VALUES ('product.calculation_type', 4, '3', 'Fixed - VA');
INSERT INTO MB_TYPE_ENUM (enum_type,enum_id,enum_value,enum_disp_label) VALUES ('product.calculation_type', 5, '4', 'Arm - Conventional');
INSERT INTO MB_TYPE_ENUM (enum_type,enum_id,enum_value,enum_disp_label) VALUES ('product.calculation_type', 6, '5', 'Arm - biweekly');
INSERT INTO MB_TYPE_ENUM (enum_type,enum_id,enum_value,enum_disp_label) VALUES ('product.calculation_type', 7, '6', 'Arm - FHA');
INSERT INTO MB_TYPE_ENUM (enum_type,enum_id,enum_value,enum_disp_label) VALUES ('product.calculation_type', 8, '7', 'Arm - VA');
INSERT INTO MB_TYPE_ENUM (enum_type,enum_id,enum_value,enum_disp_label) VALUES ('product.calculation_type', 9, '8', 'GPM - Conventional');
INSERT INTO MB_TYPE_ENUM (enum_type,enum_id,enum_value,enum_disp_label) VALUES ('product.calculation_type', 10, '9', 'GPM - FHA 245');
INSERT INTO MB_TYPE_ENUM (enum_type,enum_id,enum_value,enum_disp_label) VALUES ('product.calculation_type', 11, '10', 'GPM - VA');
INSERT INTO MB_TYPE_ENUM (enum_type,enum_id,enum_value,enum_disp_label) VALUES ('product.calculation_type', 12, '11', 'Construction Only');
INSERT INTO MB_TYPE_ENUM (enum_type,enum_id,enum_value,enum_disp_label) VALUES ('product.calculation_type', 13, '12', 'Equity - Revolving');
INSERT INTO MB_TYPE_ENUM (enum_type,enum_id,enum_value,enum_disp_label) VALUES ('product.calculation_type', 14, '13', 'Equity - Installment');
INSERT INTO MB_TYPE_ENUM (enum_type,enum_id,enum_value,enum_disp_label) VALUES ('product.late_charge_method', 1, '1', 'Set Late Charge Amount');
INSERT INTO MB_TYPE_ENUM (enum_type,enum_id,enum_value,enum_disp_label) VALUES ('product.late_charge_method', 2, '2', 'Percent');
INSERT INTO MB_TYPE_ENUM (enum_type,enum_id,enum_value,enum_disp_label) VALUES ('product.prepaid_int_factor', 1, '0', '0');
INSERT INTO MB_TYPE_ENUM (enum_type,enum_id,enum_value,enum_disp_label) VALUES ('product.prepaid_int_factor', 2, '360', '360');
INSERT INTO MB_TYPE_ENUM (enum_type,enum_id,enum_value,enum_disp_label) VALUES ('product.prepaid_int_factor', 3, '365', '365');
INSERT INTO MB_TYPE_ENUM (enum_type,enum_id,enum_value,enum_disp_label) VALUES ('product.security_type', 1, '1', 'The goods or property being purchased');
INSERT INTO MB_TYPE_ENUM (enum_type,enum_id,enum_value,enum_disp_label) VALUES ('product.security_type', 2, '2', 'Funds on deposit with lender');
INSERT INTO MB_TYPE_ENUM (enum_type,enum_id,enum_value,enum_disp_label) VALUES ('product.security_type', 3, '3', 'Real property');
INSERT INTO MB_TYPE_ENUM (enum_type,enum_id,enum_value,enum_disp_label) VALUES ('product.security_type', 4, '4', 'Personal property other than household goods');
INSERT INTO MB_TYPE_ENUM (enum_type,enum_id,enum_value,enum_disp_label) VALUES ('product.security_type', 5, '5', 'Right of setoff');
INSERT INTO MB_TYPE_ENUM (enum_type,enum_id,enum_value,enum_disp_label) VALUES ('product.security_type', 6, '6', 'Land contract on real property');
INSERT INTO MB_TYPE_ENUM (enum_type,enum_id,enum_value,enum_disp_label) VALUES ('product.security_type', 7, '7', 'Other');
INSERT INTO MB_TYPE_ENUM (enum_type,enum_id,enum_value,enum_disp_label) VALUES ('program.code', 1, '100', 'Program 100 Class');
INSERT INTO MB_TYPE_ENUM (enum_type,enum_id,enum_value,enum_disp_label) VALUES ('program.code', 2, '200', 'Program 200 Class');

DELETE FROM MB_ROLE_PRIVILEGE 
      WHERE privilege_id 
         IN (SELECT privilege_id 
               FROM MB_PRIVILEGE 
              WHERE privilege_name 
                 IN ('EditChannels','EditInvestorData','EditProductData','ManageActivationLabels',
                     'ManageAdHocRules','ManageLocks','ViewInvestorData','ViewProductData'));
DELETE FROM MB_PRIVILEGE 
      WHERE privilege_name 
         IN ('EditChannels','EditInvestorData','EditProductData','ManageActivationLabels',
             'ManageAdHocRules','ManageLocks','ViewInvestorData','ViewProductData'));

--
-- Execute the following only after a successful migration
-- These tables are no longer used
--
drop table MB_ARM_INFO;
drop table MB_CATEGORY;
drop table MB_CHANNEL;
drop table MB_GRID_CONTEXT;
drop table MB_INVESTOR;
drop table MB_InvestorsToProducts;
drop table MB_OrganizationsToInvestors;
drop table MB_OrganizationsToProducts;
drop table MB_PARAMETER_CONTEXT;
drop table MB_PRODUCT;
drop table MB_PRODUCT_CATEGORY;
drop table MB_PRODUCT_TERM;
drop table MB_UsersToChannels;
drop table MB_UsersToInvestors;
