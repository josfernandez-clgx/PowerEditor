--
-- Create POWEREDITOR_USER_TRACKING table for SQL Server.
--

create table POWEREDITOR_USER_TRACKING (
	USERNAME varchar(100) not null,
	USERFIRSTNAME varchar(100) null,
	USERLASTNAME varchar(100) null,
	USERDISABLED varchar(1) not null,
	USERDISABLEDDATE DATETIME NULL,
	USERLASTLOGINDATE DATETIME NULL,
	USERCREATEDATE DATETIME NULL,
	USERLASTMODIFIEDDATE DATETIME NULL,
	ENABLED_DATE DATETIME NULL,
	MODIFIEDBY varchar(100),
	PRIMARY KEY (USERNAME)
);