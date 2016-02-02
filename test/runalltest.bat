@echo off
cls

SET JRE_DIR=c:\j2sdk1.4.2_03\jre

SET TEST_DIR=D:\mindbox\projects\powereditor\test
SET LIB_DIR=D:\mindbox\projects\powereditor\test\lib


REM Do NOT modify below


SET CP=%TEST_DIR%;%LIB_DIR%\powereditor-test-suite.jar;%LIB_DIR%\junit.jar
SET CP=%CP%;%LIB_DIR%\powereditor-server.jar;%LIB_DIR%\log4j-1.2.8.jar


REM
REM This runs all tests in Text mode
REM
"%JRE_DIR%\bin\java" -classpath "%CP%" com.mindbox.pe.test.AllPowerEditorTestSuite
