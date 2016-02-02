@echo off

SET JREDIR=c:\j2sdk1.4.2_05\jre

SET CP=build\classes
SET PROPS=-Djavax.net.ssl.trustStore=openldap-truststore -Djavax.net.ssl.trustStorePassword=password

%JREDIR%\bin\java %PROPS% -classpath "%CP%" com.mindbox.pe.tools.LDAPTest %*
