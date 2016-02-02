
DELETE FROM MB_TYPE_ENUM where enum_type in 
('product.amortization_type','product.assumption_type','product.calculation_type','product.late_charge_method','product.loan_type','product.prepaid_int_factor','product.security_type');


INSERT INTO MB_TYPE_ENUM (enum_type,enum_id,enum_value,enum_disp_label) VALUES ('product.amortization_type',1,'79','Adjustable Payment Based On Index');
INSERT INTO MB_TYPE_ENUM (enum_type,enum_id,enum_value,enum_disp_label) VALUES ('product.amortization_type',2,'80','Fixed Payment With Balloon Option');
INSERT INTO MB_TYPE_ENUM (enum_type,enum_id,enum_value,enum_disp_label) VALUES ('product.amortization_type',3,'81','Pledged');
INSERT INTO MB_TYPE_ENUM (enum_type,enum_id,enum_value,enum_disp_label) VALUES ('product.amortization_type',4,'82','Growing Equity Mortgage (GEM)');
INSERT INTO MB_TYPE_ENUM (enum_type,enum_id,enum_value,enum_disp_label) VALUES ('product.amortization_type',5,'83','Fixed Payment');
INSERT INTO MB_TYPE_ENUM (enum_type,enum_id,enum_value,enum_disp_label) VALUES ('product.amortization_type',6,'84','Graduated Payment Mortgage (GPM)');
INSERT INTO MB_TYPE_ENUM (enum_type,enum_id,enum_value,enum_disp_label) VALUES ('product.amortization_type',7,'85','Interest Only With Balloon');
INSERT INTO MB_TYPE_ENUM (enum_type,enum_id,enum_value,enum_disp_label) VALUES ('product.amortization_type',8,'86','Graduated Payment Adjustable Rate Mortgage');
INSERT INTO MB_TYPE_ENUM (enum_type,enum_id,enum_value,enum_disp_label) VALUES ('product.amortization_type',9,'87','Extended Term');
INSERT INTO MB_TYPE_ENUM (enum_type,enum_id,enum_value,enum_disp_label) VALUES ('product.amortization_type',10,'88','Wraparound Mortgage');
INSERT INTO MB_TYPE_ENUM (enum_type,enum_id,enum_value,enum_disp_label) VALUES ('product.amortization_type',11,'89','Collateral Pledge Graduated Payment Mortgage');
INSERT INTO MB_TYPE_ENUM (enum_type,enum_id,enum_value,enum_disp_label) VALUES ('product.amortization_type',12,'90','Fixed Payment With Buydown');
INSERT INTO MB_TYPE_ENUM (enum_type,enum_id,enum_value,enum_disp_label) VALUES ('product.amortization_type',13,'91','Other Loan Payment Type');
INSERT INTO MB_TYPE_ENUM (enum_type,enum_id,enum_value,enum_disp_label) VALUES ('product.amortization_type',14,'92','Step Rate');
INSERT INTO MB_TYPE_ENUM (enum_type,enum_id,enum_value,enum_disp_label) VALUES ('product.amortization_type',15,'93','Tiered');
INSERT INTO MB_TYPE_ENUM (enum_type,enum_id,enum_value,enum_disp_label) VALUES ('product.amortization_type',16,'94','Renegotiated Rate');
INSERT INTO MB_TYPE_ENUM (enum_type,enum_id,enum_value,enum_disp_label) VALUES ('product.amortization_type',17,'95','Reverse Annuity');
INSERT INTO MB_TYPE_ENUM (enum_type,enum_id,enum_value,enum_disp_label) VALUES ('product.amortization_type',18,'96','Reverse Installment Buydown');
INSERT INTO MB_TYPE_ENUM (enum_type,enum_id,enum_value,enum_disp_label) VALUES ('product.amortization_type',19,'97','Shared Appreciation');
INSERT INTO MB_TYPE_ENUM (enum_type,enum_id,enum_value,enum_disp_label) VALUES ('product.amortization_type',20,'98','Second Mortgage');
INSERT INTO MB_TYPE_ENUM (enum_type,enum_id,enum_value,enum_disp_label) VALUES ('product.amortization_type',22,'99','Non-level');
INSERT INTO MB_TYPE_ENUM (enum_type,enum_id,enum_value,enum_disp_label) VALUES ('product.amortization_type',23,'100','Biweekly');

INSERT INTO MB_TYPE_ENUM (enum_type,enum_id,enum_value,enum_disp_label) VALUES ('product.assumption_type',1,'0','None');
INSERT INTO MB_TYPE_ENUM (enum_type,enum_id,enum_value,enum_disp_label) VALUES ('product.assumption_type',2,'1','Subject To Conditions');
INSERT INTO MB_TYPE_ENUM (enum_type,enum_id,enum_value,enum_disp_label) VALUES ('product.assumption_type',3,'2','Not Subject To Conditions');

INSERT INTO MB_TYPE_ENUM (enum_type,enum_id,enum_value,enum_disp_label) VALUES ('product.calculation_type',1,'0','Fixed - Conventional');
INSERT INTO MB_TYPE_ENUM (enum_type,enum_id,enum_value,enum_disp_label) VALUES ('product.calculation_type',2,'1','Fixed - biweekly');
INSERT INTO MB_TYPE_ENUM (enum_type,enum_id,enum_value,enum_disp_label) VALUES ('product.calculation_type',3,'2','Fixed - FHA 203');
INSERT INTO MB_TYPE_ENUM (enum_type,enum_id,enum_value,enum_disp_label) VALUES ('product.calculation_type',4,'3','Fixed - VA');
INSERT INTO MB_TYPE_ENUM (enum_type,enum_id,enum_value,enum_disp_label) VALUES ('product.calculation_type',5,'4','Arm - Conventional');
INSERT INTO MB_TYPE_ENUM (enum_type,enum_id,enum_value,enum_disp_label) VALUES ('product.calculation_type',6,'5','Arm - biweekly');
INSERT INTO MB_TYPE_ENUM (enum_type,enum_id,enum_value,enum_disp_label) VALUES ('product.calculation_type',7,'6','Arm - FHA');
INSERT INTO MB_TYPE_ENUM (enum_type,enum_id,enum_value,enum_disp_label) VALUES ('product.calculation_type',8,'7','Arm - VA');
INSERT INTO MB_TYPE_ENUM (enum_type,enum_id,enum_value,enum_disp_label) VALUES ('product.calculation_type',9,'8','GPM - Conventional');
INSERT INTO MB_TYPE_ENUM (enum_type,enum_id,enum_value,enum_disp_label) VALUES ('product.calculation_type',10,'9','GPM - FHA 245');
INSERT INTO MB_TYPE_ENUM (enum_type,enum_id,enum_value,enum_disp_label) VALUES ('product.calculation_type',11,'10','GPM - VA');
INSERT INTO MB_TYPE_ENUM (enum_type,enum_id,enum_value,enum_disp_label) VALUES ('product.calculation_type',12,'11','Construction Only');
INSERT INTO MB_TYPE_ENUM (enum_type,enum_id,enum_value,enum_disp_label) VALUES ('product.calculation_type',13,'12','Equity - Revolving');
INSERT INTO MB_TYPE_ENUM (enum_type,enum_id,enum_value,enum_disp_label) VALUES ('product.calculation_type',14,'13','Equity - Installment');

INSERT INTO MB_TYPE_ENUM (enum_type,enum_id,enum_value,enum_disp_label) VALUES ('product.late_charge_method',1,'1','Set Late Charge Amount');
INSERT INTO MB_TYPE_ENUM (enum_type,enum_id,enum_value,enum_disp_label) VALUES ('product.late_charge_method',2,'2','Percent');

INSERT INTO MB_TYPE_ENUM (enum_type,enum_id,enum_value,enum_disp_label) VALUES ('product.loan_type',1,'1','Conventional');
INSERT INTO MB_TYPE_ENUM (enum_type,enum_id,enum_value,enum_disp_label) VALUES ('product.loan_type',2,'2','Veterans Administration Loan');
INSERT INTO MB_TYPE_ENUM (enum_type,enum_id,enum_value,enum_disp_label) VALUES ('product.loan_type',3,'3','Federal Housing Administration Loan');
INSERT INTO MB_TYPE_ENUM (enum_type,enum_id,enum_value,enum_disp_label) VALUES ('product.loan_type',4,'4','USDA/Rural Housing Service');
INSERT INTO MB_TYPE_ENUM (enum_type,enum_id,enum_value,enum_disp_label) VALUES ('product.loan_type',5,'5','State Agency');
INSERT INTO MB_TYPE_ENUM (enum_type,enum_id,enum_value,enum_disp_label) VALUES ('product.loan_type',6,'6','Local Agency');
INSERT INTO MB_TYPE_ENUM (enum_type,enum_id,enum_value,enum_disp_label) VALUES ('product.loan_type',7,'7','Other Real Estate Loan');
INSERT INTO MB_TYPE_ENUM (enum_type,enum_id,enum_value,enum_disp_label) VALUES ('product.loan_type',8,'8','FHA Multifamily Loan');
INSERT INTO MB_TYPE_ENUM (enum_type,enum_id,enum_value,enum_disp_label) VALUES ('product.loan_type',9,'9','Mutually Defined');

INSERT INTO MB_TYPE_ENUM (enum_type,enum_id,enum_value,enum_disp_label) VALUES ('product.prepaid_int_factor',1,'0','0');
INSERT INTO MB_TYPE_ENUM (enum_type,enum_id,enum_value,enum_disp_label) VALUES ('product.prepaid_int_factor',2,'360','360');
INSERT INTO MB_TYPE_ENUM (enum_type,enum_id,enum_value,enum_disp_label) VALUES ('product.prepaid_int_factor',3,'365','365');

INSERT INTO MB_TYPE_ENUM (enum_type,enum_id,enum_value,enum_disp_label) VALUES ('product.security_type',1,'1','The goods or property being purchased');
INSERT INTO MB_TYPE_ENUM (enum_type,enum_id,enum_value,enum_disp_label) VALUES ('product.security_type',2,'2','Funds on deposit with lender');
INSERT INTO MB_TYPE_ENUM (enum_type,enum_id,enum_value,enum_disp_label) VALUES ('product.security_type',3,'3','Real property');
INSERT INTO MB_TYPE_ENUM (enum_type,enum_id,enum_value,enum_disp_label) VALUES ('product.security_type',4,'4','Personal property other than household goods');
INSERT INTO MB_TYPE_ENUM (enum_type,enum_id,enum_value,enum_disp_label) VALUES ('product.security_type',5,'5','Right of setoff');
INSERT INTO MB_TYPE_ENUM (enum_type,enum_id,enum_value,enum_disp_label) VALUES ('product.security_type',6,'6','Land contract on real property');
INSERT INTO MB_TYPE_ENUM (enum_type,enum_id,enum_value,enum_disp_label) VALUES ('product.security_type',7,'7','Other');
