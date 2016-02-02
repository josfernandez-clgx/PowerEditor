
ALTER TABLE MB_PRODUCT ADD documentation_type VARCHAR(100);
ALTER TABLE MB_PRODUCT ADD interestonly_period INT;

INSERT INTO MB_TYPE_ENUM (enum_type,enum_id,enum_value,enum_disp_label) VALUES ('product.documentation_type',1,'Full','Full');
