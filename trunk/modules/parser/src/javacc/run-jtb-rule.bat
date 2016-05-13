@echo off

SET JTB_DIR=c:\jtb122

SET PACKAGE=com.mindbox.pe.server.parser.jtb.rule

cd rule

call %JTB_DIR%\bin\jtb -p %PACKAGE% -jd -pp -printer ..\DeploymentRule.jj

cd ..
