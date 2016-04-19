--
-- Creates the admin account (admin/admin) that has manage user and manage roles privileges.
--
delete from MB_USER_ACCOUNT where account_id='admin';
delete from MB_USER_PASSWORD where account_id='admin';
insert into MB_USER_ACCOUNT (account_id,name,status,reset_password,failed_login_counter) values ('admin','admin account','Active',0,0);
insert into MB_USER_PASSWORD (account_id,password) values ('admin','21232f297a57a5a743894ae4a801fc3');
delete from MB_ROLE where role_id=2;
insert into MB_ROLE (role_id,role_name) values (2,'User Admin');
delete from MB_ROLE_PRIVILEGE where role_id=2;
delete from MB_ROLE_PRIVILEGE where privilege_id=12;
delete from MB_ROLE_PRIVILEGE where privilege_id=13;
insert into MB_ROLE_PRIVILEGE (privilege_id,role_id) values (12,2);
insert into MB_ROLE_PRIVILEGE (privilege_id,role_id) values (13,2);
delete from MB_USER_ROLE where account_id='admin';
insert into MB_USER_ROLE (account_id,role_id) values ('admin',2);
