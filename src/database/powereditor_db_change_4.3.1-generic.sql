DROP TABLE MB_TYPE_ENUM;

ALTER TABLE MB_PRODUCT ADD assumption_type VARCHAR(40);
ALTER TABLE MB_PRODUCT ADD calculation_type VARCHAR(40);
ALTER TABLE MB_PRODUCT ADD days_late SMALLINT;
ALTER TABLE MB_PRODUCT ADD hazard_insurance_amt REAL;
ALTER TABLE MB_PRODUCT ADD hazard_insurance_req SMALLINT;
ALTER TABLE MB_PRODUCT ADD late_charge_flag SMALLINT;
ALTER TABLE MB_PRODUCT ADD late_charge_method VARCHAR(40);
ALTER TABLE MB_PRODUCT ADD late_charge_percent REAL;
ALTER TABLE MB_PRODUCT ADD late_charge_type SMALLINT;
ALTER TABLE MB_PRODUCT ADD penalty_flag SMALLINT;
ALTER TABLE MB_PRODUCT ADD prepay_penalty_percent REAL;
ALTER TABLE MB_PRODUCT ADD prioriy INT;
ALTER TABLE MB_PRODUCT ADD prop_insurance_flag SMALLINT;
ALTER TABLE MB_PRODUCT ADD refund_flag SMALLINT;
ALTER TABLE MB_PRODUCT ADD security_flag SMALLINT;
ALTER TABLE MB_PRODUCT ADD security_type VARCHAR(40);
ALTER TABLE MB_PRODUCT ADD prepaid_int_factor VARCHAR(40);
ALTER TABLE MB_PRODUCT ADD buydown_not_allowed SMALLINT;
ALTER TABLE MB_PRODUCT ADD heloc_flag SMALLINT;

ALTER TABLE MB_PRODUCT ADD product_type VARCHAR(100);
ALTER TABLE MB_PRODUCT ADD pricing_group VARCHAR(100);

CREATE TABLE MB_TYPE_ENUM (
    enum_type VARCHAR(50) NOT NULL ,
    enum_id INT NOT NULL ,
    enum_value VARCHAR(100) NOT NULL ,
    enum_disp_label VARCHAR(100),
    PRIMARY KEY (enum_type,enum_id)
);

DELETE FROM MB_TYPE_ENUM;

INSERT INTO MB_TYPE_ENUM (enum_type,enum_id,enum_value,enum_disp_label) VALUES ('product.amortization_type',1,'ARM','ARM');
INSERT INTO MB_TYPE_ENUM (enum_type,enum_id,enum_value,enum_disp_label) VALUES ('product.amortization_type',2,'Fixed','Fixed');
INSERT INTO MB_TYPE_ENUM (enum_type,enum_id,enum_value,enum_disp_label) VALUES ('product.amortization_type',3,'Hybrid','Hybrid');
INSERT INTO MB_TYPE_ENUM (enum_type,enum_id,enum_value,enum_disp_label) VALUES ('product.amortization_type',4,'NegAm','NegAm');

INSERT INTO MB_TYPE_ENUM (enum_type,enum_id,enum_value,enum_disp_label) VALUES ('product.loan_type',1,'Conforming','Conforming');
INSERT INTO MB_TYPE_ENUM (enum_type,enum_id,enum_value,enum_disp_label) VALUES ('product.loan_type',2,'Jumbo','Jumbo');
INSERT INTO MB_TYPE_ENUM (enum_type,enum_id,enum_value,enum_disp_label) VALUES ('product.loan_type',3,'FNMA','FNMA');
