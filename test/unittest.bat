@echo off
cls

SET JRE_DIR=c:\j2sdk1.4.2_05\jre

SET TEST_DIR=D:\mindbox\projects\powereditor\test
SET LIB_DIR=D:\mindbox\projects\powereditor\test\lib



REM Do NOT modify below


SET CP=%TEST_DIR%\config;%LIB_DIR%\powereditor-test-suite.jar;%LIB_DIR%\junit.jar
SET CP=%CP%;%LIB_DIR%\powereditor-server.jar;%LIB_DIR%\powereditor-export.jar
SET CP=%CP%;%LIB_DIR%\log4j-1.2.8.jar;%LIB_DIR%\jsdk23.jar
SET CP=%CP%;%LIB_DIR%\commons-beanutils.jar;%LIB_DIR%\commons-collections.jar;%LIB_DIR%\commons-digester.jar;%LIB_DIR%\commons-logging.jar

REM
REM This brings up JUnit's GUI Tester
REM
REM "%JRE_DIR%\bin\java" -classpath "%CP%" junit.swingui.TestRunner

"%JRE_DIR%\bin\java" -Xms16m -classpath "%CP%" %*
